package org.zaproxy.zap.extension.ollama;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OllamaService {

    public static final String DEFAULT_BASE_URL = "http://localhost:11434";
    public static final int DEFAULT_TIMEOUT = 120;

    private String baseUrl;
    private int timeoutSeconds;
    private final Gson gson = new Gson();

    public OllamaService() { this(DEFAULT_BASE_URL, DEFAULT_TIMEOUT); }

    public OllamaService(String baseUrl, int timeoutSeconds) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.timeoutSeconds = timeoutSeconds;
    }

    public void updateConfig(String baseUrl, int timeoutSeconds) {
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.timeoutSeconds = timeoutSeconds;
    }

    public boolean healthCheck() {
        try {
            int code = httpGet(baseUrl + "/api/tags");
            return code >= 200 && code < 300;
        } catch (Exception e) { return false; }
    }

    public List<String> listModels() throws OllamaException {
        try {
            String body = httpGetBody(baseUrl + "/api/tags");
            TagsResponse resp = gson.fromJson(body, TagsResponse.class);
            List<String> names = new ArrayList<>();
            if (resp.models != null) for (ModelInfo m : resp.models) names.add(m.name);
            return names;
        } catch (Exception e) {
            throw new OllamaException("Failed to list models: " + e.getMessage(), e);
        }
    }

    public ChatResult chat(String model, String systemPrompt, String userMessage) throws OllamaException {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isBlank())
                messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", userMessage));
            String json = gson.toJson(new ChatRequest(model, messages, false));
            String body = httpPost(baseUrl + "/api/chat", json);
            ChatResponse resp = gson.fromJson(body, ChatResponse.class);
            if (resp.error != null && !resp.error.isEmpty())
                throw new OllamaException(resp.error);
            if (resp.message == null || resp.message.content == null)
                throw new OllamaException("Empty response from Ollama");
            return new ChatResult(resp.message.content, resp.promptEvalCount, resp.evalCount);
        } catch (OllamaException e) { throw e;
        } catch (Exception e) {
            throw new OllamaException("Chat failed: " + e.getMessage(), e);
        }
    }

    private int httpGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(timeoutSeconds * 1000);
        return conn.getResponseCode();
    }

    private String httpGetBody(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(timeoutSeconds * 1000);
        return readAll(conn.getInputStream());
    }

    private String httpPost(String urlStr, String body) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(timeoutSeconds * 1000);
        try (OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream())) { w.write(body); }
        return readAll(conn.getInputStream());
    }

    private String readAll(InputStream is) throws IOException {
        java.nio.charset.Charset cs = java.nio.charset.StandardCharsets.UTF_8;
        return new String(is.readAllBytes(), cs);
    }

    // --- DTOs ---

    public static class ChatRequest {
        public String model; public List<ChatMessage> messages; public boolean stream;
        public ChatRequest(String m, List<ChatMessage> ms, boolean s) { model=m; messages=ms; stream=s; }
    }
    public static class ChatMessage {
        public String role; public String content;
        public ChatMessage(String r, String c) { role=r; content=c; }
    }
    public static class ChatResponse {
        public ChatResponseMessage message; public String error;
        @SerializedName("prompt_eval_count") public Integer promptEvalCount;
        @SerializedName("eval_count") public Integer evalCount;
    }
    public static class ChatResponseMessage { public String role; public String content; }
    public static class ChatResult {
        public final String content; public final Integer promptTokens; public final Integer evalTokens;
        public ChatResult(String c, Integer p, Integer e) { content=c; promptTokens=p; evalTokens=e; }
    }
    public static class TagsResponse { public List<ModelInfo> models; }
    public static class ModelInfo { public String name; }
}
