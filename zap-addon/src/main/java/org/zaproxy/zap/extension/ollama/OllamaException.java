package org.zaproxy.zap.extension.ollama;

public class OllamaException extends Exception {
    public OllamaException(String message) {
        super(message);
    }

    public OllamaException(String message, Throwable cause) {
        super(message, cause);
    }
}
