#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "$SCRIPT_DIR/common.sh"

require_command mvn

echo "Building ${APP_NAME}..."
cd "$PROJECT_DIR"
mvn --no-transfer-progress clean package

echo "Build artifact: $WAR_PATH"
