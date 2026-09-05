# TP5 — CALC Distributed Calculator (REST + SOAP)

> **Architecture:** Distributed (REST + SOAP) with Shared Business Layer  
> **Language:** Java 17+  
> **Frameworks:** Jersey 3 (JAX-RS), Metro 4 (JAX-WS), Grizzly 4 (HTTP)  
> **Frontend:** React 18 + Tailwind (CDN, no build step)  
> **Build:** Maven 3.8+  
> **Persistence:** Flat file (`history.txt`)  
> **Part of:** [Software Architecture Coursework Monorepo](../README.md)

---

A self-contained distributed calculator exposing the same four arithmetic
operations over two protocols — a modern JSON/REST API and a classic XML/SOAP
API — sharing a single business layer and a flat-file operation history.

The frontend is a single-page React 18 app (loaded from a CDN, no build step)
that lets you call the same operations through both backends and inspect the
unified history.

```
tp5-calc-distributed-rest-soap/
├── start.sh                             ← one-shot build + launch
├── README.md                            ← this file
├── CALC_PROJECT_SPEC.md                 ← original specification
└── calc-project/                        ← Maven + frontend source tree
    ├── calc-server/                     ← Java 17 + Jersey 3 + Metro 4 + Grizzly 4
    │   ├── pom.xml
    │   └── src/main/java/calc/
    │       ├── Main.java
    │       ├── calculator/Calculator.java
    │       ├── persistence/HistoryManager.java
    │       ├── model/HistoryRecord.java
    │       ├── rest/CalculatorRestService.java
    │       ├── rest/CorsFilter.java
    │       ├── rest/StaticResource.java
    │       ├── soap/CalculatorSoapService.java
    │       └── soap/CalculatorSoapServiceImpl.java
    └── web-client/
        └── index.html                   ← React 18 + Tailwind (CDN), no build step
```

---

## 1. Quick start

The project ships with a single launch script that does **everything** for
you: verifies the toolchain, builds the fat JAR, frees ports 8080/8081 if
they are busy, and starts the server in the foreground.

```bash
cd tp5-calc-distributed-rest-soap
./start.sh
```

Once you see:

```
[CALC] Starting REST server on http://localhost:8080/
[CALC] Starting SOAP server on http://localhost:8081/calc
[CALC] All services up. Press ENTER to stop.
```

…open the calculator in your browser:

> **http://localhost:8080/**

Stop the server with `Ctrl+C` in the terminal where `start.sh` is running.

> `start.sh` always rebuilds the JAR (`mvn clean package`), so the next
> run picks up any code changes. It is safe to re-run at any time.

---

## 2. Required environment

| Tool                | Version           | Notes                                                        |
|---------------------|-------------------|--------------------------------------------------------------|
| JDK                 | 17+ (tested on 21)| `JAVA_HOME` must be set, `java` and `javac` on `PATH`        |
| Apache Maven        | 3.8+              | only used by `start.sh` to build the fat JAR                |
| Modern web browser  | any recent        | for the GUI at `http://localhost:8080/`                      |
| `curl`              | any               | for command-line testing of REST and SOAP                    |
| Bash                | 4+                | for running `start.sh`                                       |

`start.sh` aborts with a clear message if any of these is missing or too old.

> The frontend uses React 18, Babel standalone and Tailwind CSS from public
> CDNs, so an internet connection is required the first time the page is
> opened.

No application server, no database, no servlet container — everything is
embedded in a single runnable fat JAR.

---

## 3. What `start.sh` does

`start.sh` lives in `tp5-calc-distributed-rest-soap/`, next to the `calc-project/` source tree.
It performs, in order:

1. **Toolchain checks** — verifies `bash ≥ 4`, `java ≥ 17`, `javac ≥ 17`
   and `mvn ≥ 3.8`. Aborts with a precise error if any check fails.
2. **Build the fat JAR** — always runs `mvn -q -DskipTests clean package`
   in `calc-project/calc-server/`. The build is silenced on success.
3. **Free the ports** — for each of 8080 and 8081, finds the PID bound to
   it (cross-platform: `lsof`, `fuser` or `ss + lsof` fallback) and kills
   it cleanly (`TERM` then `KILL` after a short grace period).
4. **Launch the server** — `cd calc-project && java -jar …`, so that
   `web-client/index.html` is reachable by the server and `history.txt`
   is created in a stable, predictable place. The JVM replaces the script
   (`exec`), so `Ctrl+C` goes directly to the server's shutdown hook.

### Exit codes

| Code | Meaning                                        |
|------|------------------------------------------------|
| 0    | Success                                        |
| 1    | Toolchain check failed (Java/Maven too old)    |
| 2    | Maven build failed                             |
| 3    | Could not free a port                          |

---

## 4. Architecture

```
                ┌──────────────────────────────────────────────┐
                │                  web-client                  │
                │   index.html  (React 18 + Tailwind, CDN)     │
                └───────────────┬────────────────┬─────────────┘
                                │ JSON / fetch   │ XML / fetch
                                ▼                ▼
                ┌──────────────────────┐  ┌──────────────────────┐
                │  Grizzly (port 8080) │  │  JDK HttpServer      │
                │   + Jersey 3.1.3     │  │   + Metro 4.0.1      │
                │  (REST + static SPA) │  │  (SOAP, port 8081)   │
                └──────────┬───────────┘  └──────────┬───────────┘
                           │                         │
                           ▼                         ▼
                ┌──────────────────────┐  ┌──────────────────────┐
                │  calc.rest           │  │  calc.soap           │
                │   - REST resources   │  │   - SEI + Impl       │
                │   - CORS filter      │  │                      │
                │   - StaticResource   │  │                      │
                └──────────┬───────────┘  └──────────┬───────────┘
                           │                         │
                           └────────────┬────────────┘
                                        ▼
                          ┌─────────────────────────────┐
                          │  calc.calculator.Calculator │   (pure math)
                          └─────────────┬───────────────┘
                                        ▼
                          ┌─────────────────────────────┐
                          │  calc.persistence           │
                          │   HistoryManager (singleton)│
                          │   ↳ history.txt (UTF-8)     │
                          └─────────────────────────────┘
```

### 4.1 Package layout

| Package                   | Responsibility                                                         |
|---------------------------|------------------------------------------------------------------------|
| `calc`                    | Bootstrap: `Main` starts Grizzly (REST) and Metro (SOAP)              |
| `calc.calculator`         | Pure business logic — `add`, `subtract`, `multiply`, `divide`        |
| `calc.persistence`        | `HistoryManager` — thread-safe singleton that owns `history.txt`      |
| `calc.model`              | `HistoryRecord` — POJO with `toFileLine()` / `fromFileLine()`         |
| `calc.rest`               | JAX-RS resources, CORS filter and static-frontend resource            |
| `calc.soap`               | JAX-WS service endpoint interface + implementation                     |

### 4.2 Notes

* The history file is `history.txt`, one record per line, pipe-separated:
  `id|timestamp|operandA|operator|operandB|result|via`.
* `HistoryManager` is a thread-safe singleton; REST and SOAP threads write
  concurrently without races.
* `Calculator` has no state and no I/O — it is a pure functions module that
  can be unit-tested in isolation.
* The REST service uses a `CorsFilter` to allow the browser-based frontend
  to call the API from a different origin.
* The frontend is served by a small Jersey resource (`StaticResource`) that
  reads `web-client/index.html` once at startup and returns it for `GET /`.
  This keeps the architecture homogeneous: the entire HTTP/8080 surface is
  handled by Jersey, no separate `StaticHttpHandler` is needed.
* The SOAP server uses the JDK's built-in `com.sun.net.httpserver.HttpServer`
  with Metro 4 attached via `jakarta.xml.ws.Endpoint.publish`. A
  `com.sun.net.httpserver.Filter` answers CORS preflight `OPTIONS` requests
  and decorates every response with the right headers.

---

## 5. REST API

Base URL: `http://localhost:8080/api`

| Method | Path         | Body              | 200 response        | Other              |
|--------|--------------|-------------------|---------------------|--------------------|
| POST   | `/add`       | `{"a":n,"b":n}`   | `{"result":n}`      | —                  |
| POST   | `/subtract`  | `{"a":n,"b":n}`   | `{"result":n}`      | —                  |
| POST   | `/multiply`  | `{"a":n,"b":n}`   | `{"result":n}`      | —                  |
| POST   | `/divide`    | `{"a":n,"b":n}`   | `{"result":n}`      | 400 `{"error":…}`  |
| GET    | `/history`   | —                 | `[{HistoryRecord}]` | —                  |

CORS response headers (added by `CorsFilter`):
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

### Quick test with curl

```bash
curl -s -H "Content-Type: application/json" \
     -d '{"a":10,"b":3}' http://localhost:8080/api/add
# {"result":13.0}

curl -s -H "Content-Type: application/json" \
     -d '{"a":10,"b":0}' http://localhost:8080/api/divide
# {"error":"Division by zero"}  (HTTP 400)

curl -s http://localhost:8080/api/history
# [{"id":1,"timestamp":"…","operandA":10.0,"operator":"+","operandB":3.0,"result":13.0,"via":"REST"}, …]
```

---

## 6. SOAP API

Endpoint: `http://localhost:8081/calc`
WSDL:      `http://localhost:8081/calc?wsdl`
Namespace: `http://soap.calc/`
Style:     document/literal (SOAP 1.1)

| Operation     | Input            | Output          | Fault                              |
|---------------|------------------|-----------------|------------------------------------|
| `add`         | `a:double,b:double` | `return:double` | —                                  |
| `subtract`    | `a:double,b:double` | `return:double` | —                                  |
| `multiply`    | `a:double,b:double` | `return:double` | —                                  |
| `divide`      | `a:double,b:double` | `return:double` | SOAP Fault `"Division by zero"`    |
| `getHistory`  | —                | `return:List<HistoryRecord>` | —                  |

### Quick test with curl

```bash
curl -s -X POST \
     -H "Content-Type: text/xml; charset=utf-8" \
     -H 'SOAPAction: "add"' \
     -d '<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://soap.calc/">
  <soap:Body>
    <tns:add><a>10</a><b>3</b></tns:add>
  </soap:Body>
</soap:Envelope>' \
     http://localhost:8081/calc
# <?xml version='1.0' encoding='UTF-8'?>
# <S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
#   <S:Body>
#     <ns2:addResponse xmlns:ns2="http://soap.calc/"><return>13.0</return></ns2:addResponse>
#   </S:Body>
# </S:Envelope>
```

---

## 7. Frontend (web-client)

`web-client/index.html` is a single self-contained file:

* React 18, ReactDOM and Babel standalone loaded from `unpkg.com`
* Tailwind CSS loaded from `cdn.tailwindcss.com`
* No build step, no `node_modules`, no bundler

### Features

* Two number inputs and an operation selector (add / subtract / multiply / divide)
* One button per backend (`Compute via REST`, `Compute via SOAP`) — both
  call the same operation independently
* Independent result / loading / error blocks for each backend
* `Show / Hide History` toggle that loads `GET /api/history`, reverses the
  array (newest first) and renders a paginated table
* Page-size selector: 5 / 10 / 20 rows
* Sliding-window page numbers with prev/next buttons
* Visual distinction between `REST` (blue pill) and `SOAP` (amber pill)
  records
* Numbers are formatted with up to 6 significant digits, trailing zeros
  stripped

### Browser flow

1. Open `http://localhost:8080/` in any modern browser.
2. Enter two numbers, pick an operation, click either **Compute via REST**
   or **Compute via SOAP**.
3. Click **Show History** to see the full audit trail. The browser fetches
   `http://localhost:8080/api/history` and renders the table.
4. The SOAP call goes directly to port `8081`; no proxy is involved.

---

## 8. Manual build & run (without `start.sh`)

The script is a thin wrapper around three commands. If you prefer to run
them by hand:

```bash
# 1) Build
cd tp5-calc-distributed-rest-soap/calc-project/calc-server
mvn clean package

# 2) Run (from the directory that contains web-client/)
cd ..
java -jar calc-server/target/calc-server-1.0.jar

# 3) Open the GUI
# Browser → http://localhost:8080/
```

The JAR must be launched from a directory that **contains the `web-client/`
folder** next to it, because the server reads `web-client/index.html` at
startup and because `history.txt` is created in the current working
directory.

| Service              | Port | URL                                  |
|----------------------|------|--------------------------------------|
| REST API + frontend  | 8080 | http://localhost:8080/api/*          |
| Static frontend      | 8080 | http://localhost:8080/               |
| SOAP service         | 8081 | http://localhost:8081/calc           |
| SOAP WSDL            | 8081 | http://localhost:8081/calc?wsdl      |

A shutdown hook is also registered, so the server can be stopped cleanly
with `Ctrl+C`.

---

## 9. Troubleshooting

| Symptom                                                       | Likely cause / fix                                                                                                                                |
|---------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `start.sh: command not found` or `Permission denied`          | Run `chmod +x start.sh` first, or invoke with `bash start.sh`.                                                                                       |
| `start.sh` aborts: "Java 17+ is required"                     | Install JDK 17+ and ensure `java -version` reports it.                                                                                             |
| `start.sh` aborts: "Maven 3.8+ is required"                  | Install Apache Maven 3.8+ and ensure `mvn -v` reports it.                                                                                          |
| `mvn` fails with "requires a project to execute"              | Project layout is broken; ensure `calc-project/calc-server/pom.xml` exists.                                                                       |
| `java.net.BindException: Address already in use` on 8080/8081 | Another process (or a leftover server) holds the port. `start.sh` normally frees it; if it fails, `lsof -i :8080` and kill the listed PID.       |
| Browser shows "Loading…" forever for SOAP                     | The browser's CORS preflight to port 8081 fails. The server already replies correctly; if you front it with a custom proxy, the proxy must forward `SOAPAction` and `Content-Type`. |
| GUI cannot fetch `http://localhost:8080/api/history`          | Open the page through `http://localhost:8080/`, not by double-clicking `index.html` (CORS + absolute URLs).                                       |
| `history.txt` ends up in the wrong folder                     | `history.txt` is always created in the **current working directory** of the JVM. `start.sh` always launches the JAR from `calc-project/`.        |
| `WebServiceException: class [Ljava.lang.Object; …` etc.       | You are running with a JDK older than 17, or the `jakarta.xml.ws-api` jar is missing. The provided `pom.xml` pulls every required API jar.        |