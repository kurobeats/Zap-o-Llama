package org.zaproxy.zap.extension.ollama;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.OptionsChangedListener;
import org.parosproxy.paros.model.OptionsParam;

public class ExtensionOllama extends ExtensionAdaptor implements OptionsChangedListener {

    private static final Logger logger = LogManager.getLogger(ExtensionOllama.class);
    private final OllamaService ollamaService = new OllamaService();
    private final OllamaOptions options = new OllamaOptions();
    private OllamaOptionsPanel optionsPanel;

    public ExtensionOllama() { super("ExtensionOllama"); }

    public Logger getLogger() { return logger; }
    public OllamaService getOllamaService() { return ollamaService; }
    public OllamaOptions getOptions() { return options; }

    @Override public String getName() { return "ExtensionOllama"; }

    @Override
    public String getUIName() {
        String name = Constant.messages.getString("ollama.name");
        return name != null ? name : "Ollama AI Assistant";
    }

    @Override
    public String getDescription() {
        String desc = Constant.messages.getString("ollama.desc");
        return desc != null ? desc : "Integrates Ollama LLMs for AI-assisted security testing";
    }

    @Override
    public void hook(ExtensionHook hook) {
        super.hook(hook);

        if (hasView()) {
            optionsPanel = new OllamaOptionsPanel(this);
            hook.getHookView().addOptionPanel(optionsPanel);
            hook.addOptionsChangedListener(this);
        }

        hook.addHttpSenderListener(new OllamaHttpSenderListener(this));
        logger.info("Ollama AI Assistant extension loaded");
    }

    @Override
    public void optionsChanged(OptionsParam param) {
        ollamaService.updateConfig(options.getBaseUrl(), options.getTimeoutSeconds());
        try {
            if (ollamaService.healthCheck())
                logger.info("Ollama connection OK at " + options.getBaseUrl());
            else
                logger.warn("Ollama not reachable at " + options.getBaseUrl());
        } catch (Exception e) {
            logger.warn("Ollama health check failed", e);
        }
    }

    @Override public void destroy() { super.destroy(); logger.info("Ollama AI Assistant unloaded"); }
    @Override public boolean canUnload() { return true; }
}
