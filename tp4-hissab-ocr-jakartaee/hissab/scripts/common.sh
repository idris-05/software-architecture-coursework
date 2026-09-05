#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

if [[ -n "${ASADMIN_BIN:-}" ]]; then
    ASADMIN_BIN="${ASADMIN_BIN}"
elif [[ -n "${GLASSFISH_HOME:-}" ]]; then
    ASADMIN_BIN="${GLASSFISH_HOME%/}/bin/asadmin"
elif [[ -x "/opt/glassfish8/glassfish/bin/asadmin" ]]; then
    ASADMIN_BIN="/opt/glassfish8/glassfish/bin/asadmin"
else
    ASADMIN_BIN="/opt/glassfish8/bin/asadmin"
fi
DOMAIN_NAME="${DOMAIN_NAME:-domain1}"
APP_NAME="${APP_NAME:-hissab}"
APP_VERSION="${APP_VERSION:-1.0-SNAPSHOT}"

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-hissabdb}"
DB_USER="${DB_USER:-hissab}"
DB_PASSWORD="${DB_PASSWORD:-hissab123}"
JDBC_POOL_NAME="${JDBC_POOL_NAME:-HissabPool}"
JDBC_RESOURCE_NAME="${JDBC_RESOURCE_NAME:-jdbc/hissabDS}"

WAR_PATH="$PROJECT_DIR/target/${APP_NAME}-${APP_VERSION}.war"
APP_URL="${APP_URL:-http://localhost:8080/${APP_NAME}/}"

require_command() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "Missing required command: $1" >&2
        exit 1
    fi
}
