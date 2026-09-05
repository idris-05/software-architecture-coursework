#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "$SCRIPT_DIR/common.sh"

require_command "$ASADMIN_BIN"

if [[ ! -f "$WAR_PATH" ]]; then
    echo "WAR not found: $WAR_PATH" >&2
    echo "Run scripts/build.sh first." >&2
    exit 1
fi

echo "Deploying WAR to GlassFish..."
"$ASADMIN_BIN" deploy --force=true "$WAR_PATH"

echo "Deployment complete."
