# TP2 — Calculator Pipe & Filter

> **Architecture:** Pipe & Filter  
> **Language:** Java 8+ (Swing)  
> **Build:** Bash/Batch scripts (`compile.sh` / `compile.bat`)  
> **Part of:** [Software Architecture Coursework Monorepo](../README.md)

---

## Overview

This project is a Java Swing calculator refactored using a Pipe & Filter style.

Main idea:

- UI events are captured in `GuiSourceFilter`.
- A `CalculationRequest` is pushed into the pipeline input pipe.
- The pipeline runs core filters in order: compute -> trace.
- `GuiOutputAdapter` consumes the final output and updates the GUI.

Execution flow:

- Button click -> `GuiSourceFilter.process()` -> `CalculatorPipeline.run()`
- `ComputeFilter.process()` -> `TraceFilter.process()` -> `GuiOutputAdapter.display(...)`

## Project Structure

```text
.
├── compile.bat
├── compile.sh
├── data
│   └── track.trc
├── diagrams
│   ├── images
│   │   ├── calculator_architecture.png
│   │   └── calculator_pipeline.png
│   └── mermaid
│       ├── vue_cc.mmd
│       └── vue_dev.mmd
├── report[FR].pdf
├── report[FR].tex
├── lib
│   └── interpreter.jar
├── out
├── README.md
└── src
    └── calculatorApp
        ├── calculator
        │   ├── Calcul.java
        │   └── ComputeFilter.java
        ├── gui
        │   ├── GuiOutputAdapter.java
        │   ├── GuiSourceFilter.java
        │   └── MainGUI.java
        ├── Main.java
        ├── pipe
        │   ├── CalculationRequest.java
        │   ├── CalculationResult.java
        │   ├── Filter.java
        │   ├── Pipe.java
        │   └── TraceRecord.java
        ├── pipeline
        │   └── CalculatorPipeline.java
        └── trace
            ├── Operation.java
            ├── TraceFilter.java
            └── Trace.java
```

## Requirements

- JDK installed (`javac` and `java` available in PATH)
- `lib/interpreter.jar` present

## Build and Run

### Linux/macOS

```bash
./compile.sh
```

### Windows (cmd)

```bat
compile.bat
```

Both scripts:

- collect Java sources under `src/`
- compile into `out/`
- run `calculatorApp.Main`

## Notes

- `data/track.trc` stores operation trace history.
- `out/` is regenerated on each script run.
- Mermaid diagrams under `diagrams/mermaid/` document development and C&C views.