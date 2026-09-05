#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_SRC_DIR="$SCRIPT_DIR/src"
OUT_DIR="$SCRIPT_DIR/out"
BUILD_DIR="$SCRIPT_DIR/build"
LIB_DIR="$SCRIPT_DIR/lib"
INTERP_JAR="$LIB_DIR/interpreter.jar"
APP_SOURCES_FILE="$(mktemp)"

trap 'rm -f "$APP_SOURCES_FILE"' EXIT

find "$APP_SRC_DIR" -name "*.java" | sort > "$APP_SOURCES_FILE"

if [ ! -s "$APP_SOURCES_FILE" ]; then
    echo "No application Java source files found in $APP_SRC_DIR"
    exit 1
fi

if [ ! -f "$INTERP_JAR" ]; then
    echo "Missing interpreter JAR: $INTERP_JAR"
    exit 1
fi

rm -rf "$OUT_DIR" "$BUILD_DIR"
mkdir -p "$OUT_DIR" "$LIB_DIR"

echo "Compiling calculator application..."
javac -cp "$INTERP_JAR" -d "$OUT_DIR" @"$APP_SOURCES_FILE"

echo "Launching calculator GUI..."
java -cp "$OUT_DIR:$INTERP_JAR" calculatorApp.Main