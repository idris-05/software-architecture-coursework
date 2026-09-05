# TP4 — HISSAB: OCR Arithmetic Checker (Jakarta EE)

> **Architecture:** Component-Based (Stateless EJBs)  
> **Language:** Java 21 LTS  
> **Platform:** Jakarta EE 10, GlassFish 8.0.1  
> **Build:** Maven 3.8+  
> **Database:** PostgreSQL 14+  
> **OCR:** Tesseract 5 + Tess4j  
> **Part of:** [Software Architecture Coursework Monorepo](../README.md)

---

## What is HISSAB?

HISSAB (Arabic for _calculation_) is a web application designed to help primary school students verify their arithmetic exercises, guided by their teacher. Instead of manually correcting a list of operations, the teacher can scan the workbook page or load a PDF, and the system automatically reads each expression, evaluates it, and tells the student whether their written answer is correct.

The application was built as part of an exercise in component-based software architecture using the Jakarta EE platform. The core requirement was to take a simple calculator (developed in a prior activity) and promote it into a reusable **EJB component** named **CALC**, then integrate it into the broader **HISSAB** system.

---

## What it does

| Feature                     | Description                                                                                           |
| --------------------------- | ----------------------------------------------------------------------------------------------------- |
| **Image / PDF upload**      | Upload a scanned workbook page (PNG, JPEG) or a PDF; the system reads every arithmetic line using OCR |
| **Manual entry**            | Type an expression directly into the form, with an optional expected answer to verify                 |
| **Step-by-step evaluation** | Each expression is broken down into individual operations and displayed                               |
| **Answer checking**         | If a student writes their answer, the system says whether it is correct or wrong                      |
| **Computation history**     | Every calculation is saved — searchable, with stats, and exportable as CSV                            |

---

## Architecture

HISSAB follows a **component-based architecture** built on three Stateless EJBs:

```
Browser
  │
  ├── HissabServlet  (/process)       ← handles upload and manual entry
  └── HistoryServlet (/history)       ← displays and exports past computations
          │
          ├── OcrEJB      ← reads expressions from images/PDFs using Tesseract OCR
          ├── CalcEJB     ← the CALC component: evaluates arithmetic expressions
          └── TraceEJB    ← saves and retrieves computation records from PostgreSQL
```

**CalcEJB** is the central component. It exposes a `CalcService` interface and evaluates any arithmetic expression — including operator precedence and parentheses — using the Shunting-Yard algorithm. It was designed to be reusable: it has no dependency on OCR, servlets, or the database.

**OcrEJB** wraps Tesseract 5 via the Tess4j library. It accepts raw file bytes, writes a temporary file, runs OCR (in French and English), and returns only the lines that look like arithmetic expressions.

**TraceEJB** is the persistence component. It saves every computation as a `ComputationTrace` record in PostgreSQL via JPA, and exposes queries for history, search, and statistics.

---

## Two input modes

Both modes support **answer validation**: the student can supply the answer they believe is correct, and the system will tell them whether it matches the computed result (within a tolerance of 10⁻⁹).

### Mode 1 — Upload (the main use case)

The teacher scans a workbook page and uploads the image or PDF. The system:

1. Extracts all arithmetic lines via OCR
2. Normalizes the expressions (replacing `×`, `÷`, unicode dashes with ASCII equivalents)
3. Evaluates each one using the CALC component
4. Displays the results and saves every computation to the database

If a scanned line already contains an equals sign (e.g. `5 + 3 = 8`), the system automatically splits it: the left side is evaluated and the right side is treated as the student's answer, which is then verified. The student can also supply their answer separately through the form.

### Mode 2 — Manual entry

A student types an expression directly into the form. They can optionally type the answer they believe is correct in a second field. The system evaluates the expression using the CALC component and, if an answer was provided, compares it to the computed result and returns a clear correct / wrong verdict.

---

## Technology stack

| Component          | Technology      | Version           |
| ------------------ | --------------- | ----------------- |
| Language           | Java (OpenJDK)  | 21 LTS            |
| Platform           | Jakarta EE      | 10.0              |
| Application server | GlassFish       | 8.0.1             |
| Business logic     | Stateless EJBs  | Jakarta EJB 4.0   |
| Web layer          | Servlets + JSP  | Servlet 6.0       |
| Persistence        | EclipseLink JPA | 3.0 · JTA         |
| Database           | PostgreSQL      | 14+               |
| JDBC driver        | postgresql      | 42.7.3            |
| OCR engine         | Tesseract       | 5.3.4 (fra + eng) |
| OCR Java bridge    | Tess4j          | 5.11.0            |
| Build tool         | Apache Maven    | 3.8.7             |
| Tests              | JUnit Jupiter   | 5.10.2            |

---

## Project structure

```
hissab/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/dz/hissab/
    │   │   ├── calc/          CalcService, CalcEJB, ExpressionEvaluator, CalculationDetails
    │   │   ├── ocr/           OcrService, OcrEJB
    │   │   ├── trace/         TraceService, TraceEJB, ComputationTrace
    │   │   ├── servlet/       HissabServlet, HistoryServlet, FileUploadHelper
    │   │   ├── util/          MathSanitizer
    │   │   └── exception/     HissabException, CalcException, OcrProcessingException
    │   ├── resources/
    │   │   └── META-INF/persistence.xml
    │   └── webapp/
    │       ├── index.jsp
    │       ├── assets/        css, js, img
    │       └── WEB-INF/
    │           ├── views/     result.jsp, history.jsp, error.jsp
    │           ├── web.xml
    │           └── glassfish-web.xml
    └── test/
        └── java/dz/hissab/
            ├── calc/          CalcEJBTest
            └── util/          MathSanitizerTest
```

---

## Prerequisites

Before running the application, the following must be installed and configured on the machine:

1. **Java 21** — `JAVA_HOME` must point to the JDK 21 installation
2. **Maven 3** — `mvn -version` must confirm it uses Java 21
3. **GlassFish 8.0.1** — extracted to `/opt/glassfish8`, `GLASSFISH_HOME` exported
4. **PostgreSQL** — database `hissabdb`, user `hissab`, reachable on port 5432
5. **PostgreSQL JDBC driver** (`postgresql-42.7.3.jar`) copied into `$GLASSFISH_HOME/domains/domain1/lib/`
6. **Tesseract 5** — with `fra` and `eng` language packs installed at `/usr/share/tesseract-ocr/5/tessdata/`

---

## Running the application

All steps are scripted. From the project root:

```bash
# 1. Start GlassFish
./scripts/start-domain.sh

# 2. Create the JDBC pool and resource (first time only)
./scripts/setup-jdbc.sh

# 3. Build the WAR
./scripts/build.sh

# 4. Deploy to GlassFish
./scripts/deploy.sh

# 5. Verify it is running
./scripts/smoke-test.sh

# All in one
./scripts/launch-all.sh
```

The application is then available at:

```
http://localhost:8080/hissab
```

The GlassFish admin console is at `http://localhost:4848`.

---

## Database

The `computation_trace` table is created automatically by EclipseLink on first deployment. No manual SQL setup is needed.

| Column                 | Description                                        |
| ---------------------- | -------------------------------------------------- |
| `id`                   | Auto-generated primary key                         |
| `image_name`           | Source filename, or `manual` for typed expressions |
| `extracted_expression` | Raw line from OCR output                           |
| `sanitized_expression` | Normalized ASCII expression sent to the evaluator  |
| `result`               | Computed result, or an error message               |
| `provided_answer`      | The student's answer, if supplied                  |
| `status`               | `SUCCESS` or `ERROR`                               |
| `computed_at`          | Timestamp set at computation time                  |

---

## Authors

HIMEUR IDRIS, DIAR ADEM

Activity 4 — Component-Based Architecture with Jakarta EE  
Module: Software Architecture