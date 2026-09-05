# Software Architecture Coursework — Monorepo

This repository contains all 5 practical assignments (TP1–TP5) for the **Software Architecture (Architecture Logicielle)** module. Each assignment builds upon the previous one, progressing from a simple desktop calculator to a distributed, multi-protocol system with OCR capabilities.

---

## Project Structure

| Folder | Assignment | Architecture Style | Tech Stack | Description |
|--------|------------|-------------------|------------|-------------|
| [`tp1-calculator-monolith`](tp1-calculator-monolith/) | TP1 | **Monolithic / Modular JAR** | Java Swing, Custom Interpreter JAR | Calculator GUI with separate interpreter module packaged as `interpreter.jar`. Trace persistence to flat file. |
| [`tp2-calculator-pipe-filter`](tp2-calculator-pipe-filter/) | TP2 | **Pipe & Filter** | Java Swing, Custom Interpreter JAR | Refactored TP1 into explicit Pipe & Filter pipeline: `GuiSourceFilter` → `ComputeFilter` → `TraceFilter` → `GuiOutputAdapter`. |
| [`tp3-calculator-layered-mvp`](tp3-calculator-layered-mvp/) | TP3 | **Layered + MVP** | Java Swing, Custom Interpreter JAR | Layered architecture (presentation, service, adapter, persistence, domain) with MVP in presentation layer. |
| [`tp4-hissab-ocr-jakartaee`](tp4-hissab-ocr-jakartaee/) | TP4 | **Component-Based (EJB)** | Jakarta EE 10, GlassFish 8, PostgreSQL, Tesseract OCR, Maven | **HISSAB** — OCR-powered arithmetic checker for primary students. Three stateless EJBs: `CalcEJB` (CALC component), `OcrEJB`, `TraceEJB`. WAR deployment. |
| [`tp5-calc-distributed-rest-soap`](tp5-calc-distributed-rest-soap/) | TP5 | **Distributed (REST + SOAP)** | Java 17, Jersey 3, Metro 4, Grizzly 4, React 18 (CDN), Maven | **CALC** — Self-contained fat JAR exposing same 4 operations via JSON/REST (port 8080) and XML/SOAP (port 8081). Shared business layer, flat-file history. React SPA served by backend. |

---

## Quick Start per Project

### TP1 — Calculator Monolith
```bash
cd tp1-calculator-monolith
./compile.sh          # Linux/macOS
# or
compile.bat           # Windows
```

### TP2 — Pipe & Filter
```bash
cd tp2-calculator-pipe-filter
./compile.sh
```

### TP3 — Layered + MVP
```bash
cd tp3-calculator-layered-mvp
./compile.sh
```

### TP4 — HISSAB (Jakarta EE)
```bash
cd tp4-hissab-ocr-jakartaee/hissab
# Requires: Java 21, Maven 3.8+, GlassFish 8, PostgreSQL, Tesseract 5
./scripts/launch-all.sh   # Full setup (scripts provided)
# Then open http://localhost:8080/hissab
```

### TP5 — Distributed CALC
```bash
cd tp5-calc-distributed-rest-soap
./start.sh         # Builds fat JAR, starts REST (8080) + SOAP (8081)
# Open http://localhost:8080/
```

---

## Common Requirements

| Project | Java | Build Tool | Runtime / Server |
|---------|------|------------|------------------|
| TP1–TP3 | 8+ (tested on 17/21) | Bash scripts (`compile.sh`) | Standalone JAR |
| TP4 | 21 LTS | Maven 3.8+ | GlassFish 8.0.1 |
| TP5 | 17+ | Maven 3.8+ | Embedded (Grizzly + JDK HttpServer) |

---

## Documentation

Each subproject contains its own detailed `README.md` with:
- Architecture diagrams (Mermaid source in `diagrams/mermaid/`, exported images in `diagrams/images/`)
- Build and run instructions
- API / protocol specifications (TP4, TP5)
- Project structure breakdown
- Troubleshooting guides

Final reports (PDF) are included in each folder:
- `report.pdf` (TP1, TP3, TP4, TP5 — English)
- `report[FR].pdf` (TP2 — French)


---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
