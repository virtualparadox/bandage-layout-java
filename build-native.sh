#!/bin/bash
# Cross-platform native build script with automatic OGDF download and build

set -e

echo "Building Bandage Layout Wrapper with automatic OGDF build..."

# Detect platform
UNAME_S=$(uname -s 2>/dev/null || echo "Unknown")
if [[ "$UNAME_S" == "MINGW"* ]] || [[ "$UNAME_S" == "CYGWIN"* ]] || [[ "$OS" == "Windows_NT" ]]; then
    PLATFORM="windows"
elif [[ "$UNAME_S" == "Darwin" ]]; then
    PLATFORM="osx"
elif [[ "$UNAME_S" == "Linux" ]]; then
    PLATFORM="linux"
else
    echo "Unsupported platform: $UNAME_S"
    exit 1
fi

echo "Detected platform: $PLATFORM"

# Find Java first
if [[ -z "$JAVA_HOME" ]]; then
    if [[ "$PLATFORM" == "osx" ]]; then
        JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo "")
    elif [[ "$PLATFORM" == "linux" ]]; then
        JAVA_HOME=$(readlink -f /usr/bin/javac 2>/dev/null | sed "s:bin/javac::" || echo "")
    fi
fi

if [[ -z "$JAVA_HOME" ]] || [[ ! -d "$JAVA_HOME" ]]; then
    echo "Error: JAVA_HOME not found. Please set JAVA_HOME environment variable."
    exit 1
fi

echo "Using JAVA_HOME: $JAVA_HOME"

# Save project root and go into native folder
PROJECT_ROOT=$(pwd)
cd src/main/native

echo "Checking build dependencies..."
make check-deps

echo "Starting build process..."
echo "This will:"
echo "  1. Download OGDF (latest release) from GitHub (~20MB)"
echo "  2. Build OGDF with CMake (may take 5-15 minutes)"
echo "  3. Compile and link the JNI wrapper"

# Use the Makefile which handles OGDF download and build
make all

echo "Native build completed!"

# Verify library was created
if [[ "$PLATFORM" == "linux" ]]; then
    LIB_FILE="$PROJECT_ROOT/target/classes/native/libbandagelayout.so"
elif [[ "$PLATFORM" == "osx" ]]; then
    LIB_FILE="$PROJECT_ROOT/target/classes/native/libbandagelayout.dylib"
elif [[ "$PLATFORM" == "windows" ]]; then
    LIB_FILE="$PROJECT_ROOT/target/classes/native/bandagelayout.dll"
fi

if [[ -f "$LIB_FILE" ]]; then
    echo "✅ Native library created successfully: $LIB_FILE"
else
    echo "❌ Error: Native library not found at $LIB_FILE"
    exit 1
fi

cd "$PROJECT_ROOT"
echo "🎉 Build script completed successfully!"
echo ""
echo "To clean JNI build artifacts only:"
echo "  cd src/main/native && make clean-jni"
echo ""
echo "To clean everything including OGDF:"
echo "  cd src/main/native && make clean"
echo ""
echo "To rebuild OGDF from scratch:"
echo "  cd src/main/native && make rebuild-ogdf"
