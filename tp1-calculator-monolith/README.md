# TP1 — Calculator Monolith

> **Architecture:** Modular Monolith (Interpreter as separate JAR)  
> **Language:** Java 8+ (Swing)  
> **Build:** Bash/Batch scripts (`compile.sh` / `compile.bat`)  
> **Part of:** [Software Architecture Coursework Monorepo](../README.md)

---

This project is a calculator application refactored for the _Architecture Logicielle_ assignment.
It separates the GUI, the calculator adapter, the interpreter module, and the trace mechanism.

## Project Structure

- `src/calculator/`
  Calculator application source code:
  - `MainGUI.java`: Swing user interface
  - `Calcul.java`: adapter between the GUI and the interpreter
  - `Trace.java`: trace persistence
  - `Operation.java`: serialized trace data

- `interp-code-for-demo/src/interpreter/`
  This folder is kept to show the original interpreter code from which the JAR is built.
  The calculator application does not use these Java files directly at runtime.
  Interpreter source code kept for demonstration/reference:
  - `Expression.java`
  - `Number.java`
  - `Variable.java`
  - `Plus.java`
  - `Minus.java`
  - `Multiply.java`
  - `Evaluator.java`

- `lib/`
  Contains the reusable interpreter library:
  - `interpreter.jar`
    This is the component actually used by the calculator application during compilation and execution.

- `diagrams/`
  Mermaid sources and exported images for the architecture diagrams.

- `report.pdf`
  Final compiled report (English).

- `compile.sh`
  Build-and-run script for Linux/macOS.

- `compile.bat`
  Build-and-run script for Windows.

## How To Run The Application

### Linux / macOS

Run:

```bash
./compile.sh
```

This script:

1. compiles the interpreter module
2. rebuilds `lib/interpreter.jar`
3. compiles the calculator application
4. launches the GUI

### Windows

Run:

```bat
compile.bat
```

This batch file performs the same steps and then launches the GUI.

## Manual Build Commands

If needed, the project can also be built manually:

```bash
javac -d build/interpreter interp-code-for-demo/src/interpreter/*.java
jar --create --file lib/interpreter.jar -C build/interpreter .
javac -cp lib/interpreter.jar -d out src/calculator/*.java
java -cp out:lib/interpreter.jar calculator.MainGUI
```

On Windows, replace `:` in the classpath with `;`.

## Notes

- The interpreter is now reused as a separate module and packaged as `interpreter.jar`.
- `interp-code-for-demo/` is only kept to show the interpreter source used to build the JAR.
- The calculator application uses `lib/interpreter.jar`, not the interpreter Java source files directly.
- The GUI handles trace logging directly, while `Calcul` only delegates computations to the interpreter.
- Trace data is stored in `data/track.trc` when the application runs.