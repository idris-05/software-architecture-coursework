#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "$SCRIPT_DIR/common.sh"

require_command "$ASADMIN_BIN"

if "$ASADMIN_BIN" list-domains | grep -qE "^${DOMAIN_NAME}[[:space:]]+running"; then
    echo "Domain '${DOMAIN_NAME}' is already running."
    exit 0
fi

echo "Starting GlassFish domain '${DOMAIN_NAME}'..."
"$ASADMIN_BIN" start-domain "$DOMAIN_NAME"

echo "Domain '${DOMAIN_NAME}' started."
