#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck disable=SC1091
source "$SCRIPT_DIR/common.sh"

require_command mvn

cd "$PROJECT_DIR"

echo "Running CalcEJBTest..."
mvn --no-transfer-progress -Dtest=CalcEJBTest test

echo "Running MathSanitizerTest..."
mvn --no-transfer-progress -Dtest=MathSanitizerTest test

echo "All unit tests passed."
