# TP3 — Calculator Layered Architecture + MVP

> **Architecture:** Layered + MVP (Model-View-Presenter)  
> **Language:** Java 8+ (Swing)  
> **Build:** Bash/Batch scripts (`compile.sh` / `compile.bat`)  
> **Part of:** [Software Architecture Coursework Monorepo](../README.md)

---

## Overview

This activity refactors the original calculator from `activite1` into a layered architecture.
The presentation layer now follows MVP (Model-View-Presenter), while preserving existing behavior:

- Sum
- Product
- Factorial
- Random number
- Trace persistence to file
- Trace display

The interpreter integration is still done through `lib/interpreter.jar`, via an adapter.

## Architecture

Source root:

```text
src/calculator/
├── adapter/
│   └── InterpreterComputationGateway.java
├── app/
│   └── CalculatorApplication.java
├── domain/
│   ├── TraceEntry.java
│   └── OperationType.java
├── persistence/
│   ├── FileTraceRepository.java
│   └── TraceRepository.java
├── presentation/
│   ├── presenter/
│   │   └── CalculatorPresenter.java
│   └── view/
│       ├── CalculatorView.java
│       └── SwingCalculatorView.java
└── service/
    ├── ComputationGateway.java
    ├── CalculatorService.java
    └── CalculatorApplicationService.java
```

Responsibilities:

- `presentation/view`: Swing UI only, no business logic, no persistence calls.
- `presentation/presenter`: handles button actions, input parsing/validation, calls service, updates view.
- `service`: orchestrates use cases, computes via adapter, persists trace via repository.
- `adapter`: contains all interpreter JAR calls (`Evaluator` + postfix expressions).
- `persistence`: fixed-width text table persistence in `data/track.trc`, no Swing dependency.
- `domain`: operation types and trace entry objects.
- `app/CalculatorApplication`: dependency wiring and app bootstrap.

## Behavior Kept From Activite 1

- Same GUI controls and labels:
  - `Nombre 1`, `Nombre 2`
  - `Somme`, `Produit`, `Factoriel`, `Nombre Aleatoire`, `Trace`, `Quitter`
- Same random behavior: integer in `[0, 9999]`.
- Same factorial strategy using postfix multiplication interpreted by the JAR.
- Trace is persisted as a fixed-width text table with aligned columns.
- Trace file location remains `data/track.trc` under the activity folder.

## Build and Run

Requirements:

- JDK installed (`javac` and `java` available in PATH)
- `lib/interpreter.jar` present (already included in this activity)

### Linux / macOS

```bash
./compile.sh
```

### Windows (cmd)

```bat
compile.bat
```

Both scripts:

1. collect all Java files under `src/`
2. compile to `out/`
3. run `calculator.app.CalculatorApplication`

## Notes

- `lib/interpreter.jar` is reused (copied as-is from previous activity).
- Persistence is decoupled from Swing: repository returns domain objects only.
- The presenter now owns validation and user action handling, keeping the view passive.
- `data/track.trc` is created automatically on first saved operation.
- If an IDE shows unresolved `interpreter.*` imports, refresh Java project indexing or reload the workspace.