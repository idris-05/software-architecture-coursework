#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_SRC_DIR="$SCRIPT_DIR/src"
INTERP_SRC_DIR="$SCRIPT_DIR/interp-code-for-demo/src"
OUT_DIR="$SCRIPT_DIR/out"
BUILD_DIR="$SCRIPT_DIR/build"
INTERP_BUILD_DIR="$BUILD_DIR/interpreter"
LIB_DIR="$SCRIPT_DIR/lib"
INTERP_JAR="$LIB_DIR/interpreter.jar"
APP_SOURCES_FILE="$(mktemp)"
INTERP_SOURCES_FILE="$(mktemp)"

trap 'rm -f "$APP_SOURCES_FILE" "$INTERP_SOURCES_FILE"' EXIT

find "$APP_SRC_DIR" -name "*.java" | sort > "$APP_SOURCES_FILE"
find "$INTERP_SRC_DIR" -name "*.java" | sort > "$INTERP_SOURCES_FILE"

if [ ! -s "$APP_SOURCES_FILE" ]; then
    echo "No application Java source files found in $APP_SRC_DIR"
    exit 1
fi

if [ ! -s "$INTERP_SOURCES_FILE" ]; then
    echo "No interpreter Java source files found in $INTERP_SRC_DIR"
    exit 1
fi

rm -rf "$OUT_DIR" "$BUILD_DIR"
mkdir -p "$OUT_DIR" "$INTERP_BUILD_DIR" "$LIB_DIR"

echo "Compiling interpreter module..."
javac -d "$INTERP_BUILD_DIR" @"$INTERP_SOURCES_FILE"

echo "Packaging interpreter JAR..."
jar --create --file "$INTERP_JAR" -C "$INTERP_BUILD_DIR" .

echo "Compiling calculator application..."
javac -cp "$INTERP_JAR" -d "$OUT_DIR" @"$APP_SOURCES_FILE"

echo "Launching calculator GUI..."
java -cp "$OUT_DIR:$INTERP_JAR" calculator.MainGUI
