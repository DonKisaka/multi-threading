#!/bin/bash
# ============================================================
#  Java Multithreading — Run Script
# ============================================================

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC="$PROJECT_DIR/src"
OUT="$PROJECT_DIR/out"

echo ""
echo "☕  Java Multithreading Learning Lab"
echo "======================================"

# Check for Java
if ! command -v javac &>/dev/null; then
    echo "❌ Java not found. Install JDK 11+ first."
    echo "   Ubuntu: sudo apt install default-jdk"
    echo "   macOS:  brew install openjdk"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -1)"

# Compile
echo ""
echo "🔨 Compiling..."
mkdir -p "$OUT"
javac -d "$OUT" "$SRC"/multithreading/*.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"
echo ""

# Run
java -cp "$OUT" multithreading.Main
