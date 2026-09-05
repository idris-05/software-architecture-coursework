#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

"$SCRIPT_DIR/start-domain.sh"
"$SCRIPT_DIR/setup-jdbc.sh"
"$SCRIPT_DIR/build.sh"
"$SCRIPT_DIR/deploy.sh"
"$SCRIPT_DIR/smoke-test.sh"

echo "HISSAB is ready."
