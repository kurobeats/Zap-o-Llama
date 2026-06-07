package org.zaproxy.zap.extension.ollama;

import org.apache.commons.configuration.FileConfiguration;
import org.parosproxy.paros.common.AbstractParam;

public class OllamaOptions extends AbstractParam {

    private static final String ROOT = "ollama";

    public OllamaOptions() {}

    @Override
    protected void parse() {
        // Called by ZAP when loading config — no special parsing needed
    }

    private FileConfiguration cfg() { return getConfig(); }

    public String getBaseUrl() { return cfg().getString(ROOT + ".baseUrl", OllamaService.DEFAULT_BASE_URL); }
    public void setBaseUrl(String v) { cfg().setProperty(ROOT + ".baseUrl", v); }

    public String getModel() { return cfg().getString(ROOT + ".model", "llama3.2:3b"); }
    public void setModel(String v) { cfg().setProperty(ROOT + ".model", v); }

    public int getTimeoutSeconds() { return cfg().getInt(ROOT + ".timeout", OllamaService.DEFAULT_TIMEOUT); }
    public void setTimeoutSeconds(int v) { cfg().setProperty(ROOT + ".timeout", v); }

    public String getRiskFilter() { return cfg().getString(ROOT + ".riskFilter", "Medium"); }
    public void setRiskFilter(String v) { cfg().setProperty(ROOT + ".riskFilter", v); }

    public String getOutputDir() { return cfg().getString(ROOT + ".outputDir", ""); }
    public void setOutputDir(String v) { cfg().setProperty(ROOT + ".outputDir", v); }

    public String getSystemPromptAnalyze() {
        return cfg().getString(ROOT + ".promptAnalyze", "You are a security researcher. Analyze for vulnerabilities. Be concise.");
    }
    public void setSystemPromptAnalyze(String v) { cfg().setProperty(ROOT + ".promptAnalyze", v); }

    public String getSystemPromptValidateFP() {
        return cfg().getString(ROOT + ".promptValidateFP", "You are a security researcher. Evaluate: Real, False positive, or Uncertain.");
    }
    public void setSystemPromptValidateFP(String v) { cfg().setProperty(ROOT + ".promptValidateFP", v); }

    public String getSystemPromptExplain() {
        return cfg().getString(ROOT + ".promptExplain", "You are a security researcher. Explain concisely.");
    }
    public void setSystemPromptExplain(String v) { cfg().setProperty(ROOT + ".promptExplain", v); }

    public boolean isEnrichAlerts() { return cfg().getBoolean(ROOT + ".enrichAlerts", true); }
    public void setEnrichAlerts(boolean v) { cfg().setProperty(ROOT + ".enrichAlerts", v); }

    public boolean isValidateFalsePositives() { return cfg().getBoolean(ROOT + ".validateFP", true); }
    public void setValidateFalsePositives(boolean v) { cfg().setProperty(ROOT + ".validateFP", v); }

    public int getMaxRequestLength() { return cfg().getInt(ROOT + ".maxReqLength", 8000); }
    public void setMaxRequestLength(int v) { cfg().setProperty(ROOT + ".maxReqLength", v); }

    public boolean isStreamingEnabled() { return cfg().getBoolean(ROOT + ".streaming", false); }
    public void setStreamingEnabled(boolean v) { cfg().setProperty(ROOT + ".streaming", v); }

    public boolean isMultiModelEnabled() { return cfg().getBoolean(ROOT + ".multiModel", false); }
    public void setMultiModelEnabled(boolean v) { cfg().setProperty(ROOT + ".multiModel", v); }

    public String getFallbackModels() { return cfg().getString(ROOT + ".fallbackModels", ""); }
    public void setFallbackModels(String v) { cfg().setProperty(ROOT + ".fallbackModels", v); }

    public boolean isAutoTriageEnabled() { return cfg().getBoolean(ROOT + ".autoTriage", false); }
    public void setAutoTriageEnabled(boolean v) { cfg().setProperty(ROOT + ".autoTriage", v); }

    public boolean isCweMappingEnabled() { return cfg().getBoolean(ROOT + ".cweMapping", false); }
    public void setCweMappingEnabled(boolean v) { cfg().setProperty(ROOT + ".cweMapping", v); }

    public boolean isAutoReportEnabled() { return cfg().getBoolean(ROOT + ".autoReport", false); }
    public void setAutoReportEnabled(boolean v) { cfg().setProperty(ROOT + ".autoReport", v); }

    public String getPromptTemplates() { return cfg().getString(ROOT + ".promptTemplates", ""); }
    public void setPromptTemplates(String v) { cfg().setProperty(ROOT + ".promptTemplates", v); }
}
