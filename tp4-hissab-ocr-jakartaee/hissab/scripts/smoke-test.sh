#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "$SCRIPT_DIR/common.sh"

require_command curl

echo "Checking app endpoint: $APP_URL"
curl --fail --silent --show-error --location "$APP_URL" >/dev/null
echo "Smoke test passed."
