# ZAP Ollama

> **Port of [burp-ollama](https://github.com/jayluxferro/burp-ollama) by [@jayluxferro](https://github.com/jayluxferro)** — re-implemented for OWASP ZAP as a Java add-on.

---

## Overview

AI-powered security testing for **OWASP ZAP** using local Ollama models. All data stays on your machine — nothing is sent to external APIs, and it works offline after models are pulled.

---

## Features

- **Ask Ollama** — send HTTP messages to Ollama for analysis via the options panel
- **Options panel** — full GUI settings in ZAP preferences (URL, model, timeout, prompts, etc.)
- **Alert enrichment** — AI analysis appended to ZAP alert "Other Info"
- **False positive validation** — AI evaluates if findings are real or false positives
- **Session detection** — detect login pages, session cookies, and 401/403 session expiry
- **Result dialog** — view AI responses with copy-to-clipboard support
- **System prompts** — customizable prompts for Analyze, Validate FP, and Explain
- **Prompt templates** — reusable `KEY=prompt` template pairs
- **Health check** — test Ollama connectivity from the options panel
- **Model discovery** — refresh and select from available Ollama models

Planned / in-progress features (UI checkboxes present, backend pending):

- Streaming responses, multi-model fallback, auto-triage, CWE mapping, auto-report generation

---

## Requirements

- [Ollama](https://ollama.com) installed and running
- At least one model pulled: `ollama pull llama3.2:3b`
- ZAP 2.15+, Java 11+

---

## Java Add-On

A ZAP extension in `zap-addon/` with GUI settings and HTTP sender listener integration.

### Quick Start

```bash
./gradlew :zap-addon:jar
```
This produces `zap-addon/build/libs/zap-ollama-1.0.0.jar`.

Then in ZAP: **Tools → Options → Add-ons → Install from File** → select the JAR.

Configure at **Tools → Options → Ollama AI Settings**.

### What it registers

| Extension Point | Class | Description |
|-----------------|-------|-------------|
| Options Panel | `OllamaOptionsPanel` | GUI for URL, model, timeout, risk filter, prompts, feature toggles |
| HTTP Sender | `OllamaHttpSenderListener` | Logs login pages, session cookies, 401/403 expiry |

### Options panel settings

| Setting | Default | Description |
|---------|---------|-------------|
| Ollama URL | `http://localhost:11434` | Where Ollama is running |
| Model | `llama3.2:3b` | Primary model |
| Fallback Models | *(empty)* | Comma-separated backup models |
| Timeout | 120s | Request timeout |
| Min Risk Filter | Medium | Only enrich alerts at this risk or above |
| Output Dir | *(empty)* | Directory for auto-generated reports |
| Enrich alerts | On | Append AI analysis to alert "Other Info" |
| Validate false positives | On | AI FP assessment |
| Streaming | Off | Real-time token display (pending) |
| Multi-model fallback | Off | Try fallback models on failure (pending) |
| Auto-triage | Off | AI classifies as real/FP/review (pending) |
| CWE mapping | Off | Map findings to CWE IDs (pending) |
| Auto-report | Off | Generate reports from findings (pending) |
| System Prompts | *(editable)* | Customize Analyze, Validate FP, and Explain prompts |
| Prompt Templates | *(editable)* | `KEY=prompt` pairs for reusable templates |

### Architecture

```
zap-addon/src/main/java/org/zaproxy/zap/extension/ollama/
├── ExtensionOllama.java              — Main extension (hooks options panel & HTTP listener)
├── OllamaService.java                — Ollama HTTP client (sync chat, health check, model list)
├── OllamaOptions.java                — Persistent config via ZAP AbstractParam
├── OllamaOptionsPanel.java           — Settings UI panel with connection test & model refresh
├── OllamaHttpSenderListener.java     — Login page & session detection
├── OllamaResultDialog.java           — Response display with copy-to-clipboard
└── OllamaException.java              — Custom exception class
```

---

## Building from source

### Prerequisites

- JDK 11+
- Gradle (wrapper included — no separate Gradle install needed)

### Build commands

```bash
# Compile and package the add-on JAR
./gradlew :zap-addon:jar

# Run tests
./gradlew :zap-addon:test

# Clean build artifacts
./gradlew clean
```

The built JAR will be at `zap-addon/build/libs/zap-ollama-1.0.0.jar`.

### Installing into ZAP

1. Open ZAP
2. Go to **Tools → Options → Add-ons**
3. Click **Install from File…**
4. Select the built JAR
5. Restart ZAP if prompted
6. Configure at **Tools → Options → Ollama AI Settings**

---

## Known Limitations

- Alert enrichment requires a passive scan to have already produced alerts — results may not appear instantly
- No WebSocket message support
- No Intruder/Fuzzer payload generator (requires separate ZAP Fuzzer add-on)
- Streaming, multi-model fallback, auto-triage, CWE mapping, and auto-report features are UI-only pending backend implementation

---

## Credits

Port of [burp-ollama](https://github.com/jayluxferro/burp-ollama) by [@jayluxferro](https://github.com/jayluxferro).

## License

GPL3
