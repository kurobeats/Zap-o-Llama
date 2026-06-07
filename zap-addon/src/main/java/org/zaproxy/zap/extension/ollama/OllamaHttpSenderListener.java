package org.zaproxy.zap.extension.ollama;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.zaproxy.zap.network.HttpSenderListener;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

public class OllamaHttpSenderListener implements HttpSenderListener {

    private static final Logger logger = LogManager.getLogger(OllamaHttpSenderListener.class);
    private final Set<String> detectedLogins = Collections.synchronizedSet(new HashSet<>());
    private final ExtensionOllama extension;

    public OllamaHttpSenderListener(ExtensionOllama extension) { this.extension = extension; }

    private boolean isLoginPath(String path) {
        if (path == null) return false;
        String l = path.toLowerCase();
        return l.contains("login") || l.contains("signin")
            || l.contains("sign-in") || l.contains("auth");
    }

    @Override
    public void onHttpRequestSend(HttpMessage msg, int initiator, HttpSender sender) {
        if (msg == null) return;
        try {
            String path = msg.getRequestHeader().getURI().getPath();
            if ("POST".equals(msg.getRequestHeader().getMethod()) && isLoginPath(path)) {
                String url = msg.getRequestHeader().getURI().toString();
                if (detectedLogins.add(url)) logger.info("Login page: " + url);
            }
        } catch (Exception e) { logger.debug("Error: " + e.getMessage()); }
    }

    @Override
    public void onHttpResponseReceive(HttpMessage msg, int initiator, HttpSender sender) {
        if (msg == null) return;
        try {
            int status = msg.getResponseHeader().getStatusCode();
            if (status >= 401 && status <= 403)
                logger.info("Session may have expired (HTTP " + status + ")");
            String path = msg.getRequestHeader().getURI().getPath();
            if (status == 200 && isLoginPath(path)) {
                String c = msg.getResponseHeader().getHeader("Set-Cookie");
                if (c != null)
                    logger.info("Session established. Cookies: " + (c.length() > 200 ? c.substring(0,200) : c));
            }
        } catch (Exception e) { logger.debug("Error: " + e.getMessage()); }
    }

    @Override public int getListenerOrder() { return 0; }
}
