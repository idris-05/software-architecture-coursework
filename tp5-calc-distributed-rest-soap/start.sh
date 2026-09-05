#!/usr/bin/env bash
# start.sh — build and launch the CALC distributed calculator in the foreground.
#
# Project layout (assumes this script sits in activite5/, with the project
# source tree in calc-project/ next to it):
#
#   activite5/
#   ├── start.sh                          ← this file
#   ├── README.md
#   └── calc-project/
#       ├── calc-server/                  ← Maven module (Java 17)
#       └── web-client/index.html
#
# What it does:
#   1. Verifies bash >= 4, java >= 17, javac >= 17, mvn >= 3.8.
#   2. Always runs `mvn clean package` in calc-project/calc-server/.
#   3. Frees ports 8080 and 8081 by killing any PIDs bound to them.
#   4. Launches the fat JAR in the foreground (Ctrl+C to stop).
#
# Exit codes:
#   0  success
#   1  toolchain check failed
#   2  maven build failed
#   3  could not free a port

set -euo pipefail

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/calc-project"
SERVER_DIR="$PROJECT_ROOT/calc-server"
WEB_DIR="$PROJECT_ROOT/web-client"
JAR_PATH="$SERVER_DIR/target/calc-server-1.0.jar"
REST_PORT=8080
SOAP_PORT=8081

# ---------------------------------------------------------------------------
# Logging helpers
# ---------------------------------------------------------------------------
info()  { printf '\033[1;34m[INFO]\033[0m  %s\n' "$*"; }
ok()    { printf '\033[1;32m[ OK ]\033[0m  %s\n' "$*"; }
warn()  { printf '\033[1;33m[WARN]\033[0m  %s\n' "$*" >&2; }
err()   { printf '\033[1;31m[FAIL]\033[0m  %s\n' "$*" >&2; }

# ---------------------------------------------------------------------------
# Toolchain checks
# ---------------------------------------------------------------------------
version_ge() {
    local v1="$1" v2="$2"
    local sorted
    sorted="$(printf '%s\n%s\n' "$v1" "$v2" | sort -V | tail -n1)"
    [ "$sorted" = "$v1" ]
}

require_toolchain() {
    if [ "${BASH_VERSINFO[0]}" -lt 4 ]; then
        err "Bash 4+ is required (found ${BASH_VERSION})."
        exit 1
    fi

    if ! command -v java > /dev/null 2>&1; then
        err "'java' not found. Install JDK 17+ and put java on PATH."
        exit 1
    fi
    if ! command -v javac > /dev/null 2>&1; then
        err "'javac' not found. Install JDK 17+ and put javac on PATH."
        exit 1
    fi

    local java_ver javac_ver
    java_ver="$(java -version 2>&1 | head -n1 | sed -E 's/.*"([0-9][^"]*)".*/\1/')"
    javac_ver="$(javac -version 2>&1 | head -n1 | sed -E 's/.*"([0-9][^"]*)".*/\1/')"

    if ! version_ge "$java_ver" "17"; then
        err "Java 17+ is required (found $java_ver)."
        exit 1
    fi
    if ! version_ge "$javac_ver" "17"; then
        err "javac 17+ is required (found $javac_ver)."
        exit 1
    fi

    if ! command -v mvn > /dev/null 2>&1; then
        err "'mvn' not found. Install Apache Maven 3.8+."
        exit 1
    fi
    local mvn_ver
    mvn_ver="$(mvn -v 2>/dev/null | head -n1 | awk '{print $3}')"
    if ! version_ge "$mvn_ver" "3.8"; then
        err "Maven 3.8+ is required (found $mvn_ver)."
        exit 1
    fi

    ok "Toolchain OK: bash $BASH_VERSION, java $java_ver, javac $javac_ver, maven $mvn_ver"
}

# ---------------------------------------------------------------------------
# Build (always)
# ---------------------------------------------------------------------------
build() {
    if [ ! -d "$SERVER_DIR" ] || [ ! -f "$SERVER_DIR/pom.xml" ]; then
        err "Maven module not found at $SERVER_DIR (no pom.xml)."
        exit 2
    fi

    info "Building fat JAR with Maven (this may take a minute the first time)..."
    ( cd "$SERVER_DIR" && mvn -q -DskipTests clean package )
    if [ ! -f "$JAR_PATH" ]; then
        err "Build did not produce $JAR_PATH."
        exit 2
    fi
    ok "Built $JAR_PATH ($(du -h "$JAR_PATH" | awk '{print $1}'))."
}

# ---------------------------------------------------------------------------
# Port management
# ---------------------------------------------------------------------------
pids_on_port() {
    local port="$1"
    if command -v lsof > /dev/null 2>&1; then
        lsof -ti tcp:"$port" 2>/dev/null || true
    elif command -v fuser > /dev/null 2>&1; then
        fuser "${port}/tcp" 2>/dev/null | tr -s ' ' '\n' | grep -E '^[0-9]+$' || true
    elif command -v ss > /dev/null 2>&1; then
        ss -ltnp 2>/dev/null | awk -v p=":$port" '$4 ~ p {print $0}' \
            | grep -oE 'pid=[0-9]+' | cut -d= -f2 | sort -u
    fi
}

free_port() {
    local port="$1"
    local pids
    pids="$(pids_on_port "$port" | tr '\n' ' ' | tr -s ' ')"
    pids="$(printf '%s' "$pids" | tr ' ' '\n' | sort -u | grep -E '^[0-9]+$' || true)"

    if [ -z "$pids" ]; then
        ok "Port $port is free."
        return 0
    fi

    info "Port $port is busy, killing: $pids"
    for pid in $pids; do
        kill -TERM "$pid" 2>/dev/null || true
    done
    local waited=0
    while [ "$waited" -lt 10 ]; do
        local still=0
        for pid in $pids; do
            if kill -0 "$pid" 2>/dev/null; then still=1; fi
        done
        [ "$still" = "0" ] && break
        sleep 0.5
        waited=$((waited + 1))
    done
    for pid in $pids; do
        if kill -0 "$pid" 2>/dev/null; then
            warn "PID $pid did not exit on TERM, sending KILL."
            kill -KILL "$pid" 2>/dev/null || true
        fi
    done
    sleep 0.3

    if [ -n "$(pids_on_port "$port" || true)" ]; then
        err "Could not free port $port."
        exit 3
    fi
    ok "Port $port freed."
}

# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
require_toolchain
build
free_port "$REST_PORT"
free_port "$SOAP_PORT"

if [ ! -d "$WEB_DIR" ]; then
    err "Static frontend not found at $WEB_DIR."
    err "The server needs to be launched from the directory that contains web-client/."
    exit 1
fi

info "Launching server in the foreground (Ctrl+C to stop)..."
echo
echo "  Web UI  : http://localhost:$REST_PORT/"
echo "  REST    : http://localhost:$REST_PORT/api/*"
echo "  SOAP    : http://localhost:$SOAP_PORT/calc"
echo "  WSDL    : http://localhost:$SOAP_PORT/calc?wsdl"
echo
cd "$PROJECT_ROOT"
exec java -jar "$JAR_PATH"
