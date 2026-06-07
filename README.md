# ZAP Ollama

> **Port of [burp-ollama](https://github.com/jayluxferro/burp-ollama) by [@jayluxferro](https://github.com/jayluxferro)** — re-implemented for OWASP ZAP as Python scripts and a Java add-on.

---

## Overview

AI-powered security testing for **OWASP ZAP** using local Ollama models. All data stays on your machine — nothing is sent to external APIs, works offline after models are pulled.

Two forms are provided:

| Form | Location | Use case |
|------|----------|----------|
| **Python scripts** | `zap-scripts/` | Quick setup, no compilation needed |
| **Java add-on** | `zap-addon/` | Full integration, options panel, passive scanning |

---

## Features

| Feature | Python Scripts | Java Add-on |
|---------|:---:|:---:|
| **Ask Ollama** — send HTTP messages to Ollama for analysis | ✅ | ✅ |
| **Context menu** — right-click any message → Ask Ollama | — | ✅ |
| **Alert enrichment** — AI analysis appended to ZAP alerts | ✅ | ✅ |
| **False positive validation** — AI evaluates if findings are real | ✅ | ✅ |
| **Session detection** — detect login pages & session expiry | ✅ | ✅ |
| **Streaming responses** — real-time token display | — | ✅ |
| **Multi-model fallback** — try backup models on failure | — | ✅ |
| **Auto-triage** — AI classifies findings (real/FP/review) | — | ✅ |
| **CWE mapping** — map findings to CWE IDs | — | ✅ |
| **Executive summaries** — AI-generated report summaries | — | ✅ |
| **Auto-report generation** — full markdown/HTML reports | — | ✅ |
| **Options panel** — GUI settings in ZAP preferences | — | ✅ |

---

## Requirements

- [Ollama](https://ollama.com) installed and running
- At least one model pulled: `ollama pull llama3.2:3b`
- **Python scripts:** ZAP with Jython support
- **Java add-on:** ZAP 2.15+, Java 11+

---

## Python Scripts

Three self-contained scripts in `zap-scripts/`:

| Script | ZAP Script Type | What it does |
|--------|-----------------|--------------|
| `ollama_common.py` | Shared module | HTTP client — chat, streaming, list models, health check |
| `ask_ollama_standalone.py` | Standalone | Opens a dialog to send arbitrary queries to Ollama |
| `alert_enricher.py` | Alert Filter | Enriches every new alert with AI false-positive assessment and remediation advice |
| `session_helper.py` | HTTP Sender | Monitors traffic for login pages, session cookies, and 401/403 expiry |

### Setup

1. Copy all `.py` files into ZAP's scripts folder
2. In ZAP: **Scripts panel → Load** each script
3. Edit the `OLLAMA_URL` and `MODEL` variables at the top of each script
4. Ensure Ollama is running (`ollama serve`)

---

## Java Add-On

A full ZAP extension in `zap-addon/` with GUI settings and passive scanning integration.

### Quick Start

```bash
cd zap-addon
./gradlew jar
```
Then in ZAP: **Tools → Options → Add-ons → Install from File** → `build/libs/zap-ollama-1.0.0.jar`

Configure at **Tools → Options → Ollama AI Settings**.

### What it registers

| Extension Point | Class | Description |
|-----------------|-------|-------------|
| Passive Scanner | `OllamaPassiveScanner` | After each response, enriches matching alerts with AI analysis |
| Context Menu | `OllamaPopupMenu` | "Ask Ollama" on right-click of any HTTP message |
| HTTP Sender | `OllamaHttpSenderListener` | Logs login pages, session cookies, 401/403 expiry |
| Options Panel | `OllamaOptionsPanel` | GUI for URL, model, timeout, risk filter, prompts, enhanced features |

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
| Streaming | Off | Real-time token display |
| Multi-model fallback | Off | Try fallback models on failure |
| Auto-triage | Off | AI classifies as real/FP/review |
| CWE mapping | Off | Map findings to CWE IDs |
| Auto-report | Off | Generate reports from findings |
| System Prompts | *(editable)* | Customize Analyze, Validate FP, and Explain prompts |
| Prompt Templates | *(editable)* | `KEY=prompt` pairs for reusable templates |

### Architecture

```
zap-addon/src/main/java/org/zaproxy/zap/extension/ollama/
├── ExtensionOllama.java              — Main extension (hooks all extension points)
├── OllamaService.java                — Ollama HTTP client (sync chat)
├── OllamaEnhancedService.java        — Streaming, multi-model, auto-triage, CWE, reports
├── OllamaOptions.java                — Persistent config via ZAP AbstractParam
├── OllamaOptionsPanel.java           — Settings UI panel
├── OllamaPassiveScanner.java         — Passive scan hook for alert enrichment
├── OllamaPopupMenu.java              — Right-click "Ask Ollama" context menu
├── OllamaHttpSenderListener.java     — Login page & session detection
└── OllamaResultDialog.java           — Response display with copy-to-clipboard
```

---

## Known Limitations

- Alert enrichment is asynchronous — results may not appear instantly
- Python scripts use Jython 2.7 (no `requests` library — uses `urllib2`)
- No WebSocket message support in the ZAP port
- No Intruder/Fuzzer payload generator (requires separate ZAP Fuzzer add-on)

---

## Credits

Port of [burp-ollama](https://github.com/jayluxferro/burp-ollama) by [@jayluxferro](https://github.com/jayluxferro).

## License

GPL3
