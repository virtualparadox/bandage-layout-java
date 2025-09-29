# Self-contained OGDF JNI Build for Bandage Layout
# This Makefile automatically downloads and builds OGDF from source

# Detect platform
UNAME_S := $(shell uname -s)
ifeq ($(OS),Windows_NT)
    PLATFORM = windows
    CXX = g++
    JNI_LIB = bandagelayout.dll
    CXXFLAGS = -std=c++17 -O2 -DNDEBUG -Wall
    LDFLAGS = -shared -static-libgcc -static-libstdc++
    CMAKE_GENERATOR = "MinGW Makefiles"
    MAKE_CMD = mingw32-make -j4
else ifeq ($(UNAME_S),Linux)
    PLATFORM = linux
    CXX = g++
    JNI_LIB = libbandagelayout.so
    CXXFLAGS = -std=c++17 -fPIC -O2 -DNDEBUG -Wall
    LDFLAGS = -shared
    CMAKE_GENERATOR = "Unix Makefiles"
    MAKE_CMD = make -j4
else ifeq ($(UNAME_S),Darwin)
    PLATFORM = osx
    CXX = clang++
    JNI_LIB = libbandagelayout.dylib
    CXXFLAGS = -std=c++17 -fPIC -O2 -DNDEBUG -Wall
    LDFLAGS = -shared -undefined dynamic_lookup
    CMAKE_GENERATOR = "Unix Makefiles"
    MAKE_CMD = make -j4
endif

# Directories
BUILD_DIR = build
OGDF_SOURCE_DIR = $(BUILD_DIR)/ogdf-source
OGDF_BUILD_DIR = $(BUILD_DIR)/ogdf-build
OGDF_INSTALL_DIR = $(BUILD_DIR)/ogdf-install
TARGET_NATIVE_DIR = ../../../target/classes/native

# OGDF version and URL
OGDF_VERSION = latest-release
OGDF_URL = https://github.com/ogdf/ogdf.git

# JAVA_HOME detection
ifeq ($(JAVA_HOME),)
    ifeq ($(PLATFORM),osx)
        JAVA_HOME := $(shell /usr/libexec/java_home 2>/dev/null)
    else ifeq ($(PLATFORM),linux)
        JAVA_HOME := $(shell readlink -f /usr/bin/javac 2>/dev/null | sed "s:bin/javac::")
    endif
endif

# JNI includes
JNI_INCLUDES = -I$(JAVA_HOME)/include
ifeq ($(PLATFORM),osx)
    JNI_INCLUDES += -I$(JAVA_HOME)/include/darwin
else ifeq ($(PLATFORM),linux)
    JNI_INCLUDES += -I$(JAVA_HOME)/include/linux
else ifeq ($(PLATFORM),windows)
    JNI_INCLUDES += -I$(JAVA_HOME)/include/win32
endif

# OGDF includes and libraries
OGDF_INCLUDES = -I$(OGDF_BUILD_DIR)/include/ogdf-release -I$(OGDF_SOURCE_DIR)/include

ifeq ($(PLATFORM),windows)
    # Windows builds produce libOGDF.dll + libOGDF.dll.a
    OGDF_LIBS = $(OGDF_BUILD_DIR)/libOGDF.dll.a
else
    OGDF_LIBS = -L$(OGDF_BUILD_DIR) -lOGDF -lCOIN
endif

# Source files
JNI_SOURCES = src/BandageLayoutJNI.cpp
JNI_OBJECTS = $(BUILD_DIR)/BandageLayoutJNI.o

# Default target
all: $(BUILD_DIR)/$(JNI_LIB)
	@echo "Build completed: $(JNI_LIB)"

# Download OGDF from Git
$(OGDF_SOURCE_DIR):
	@echo "Downloading OGDF $(OGDF_VERSION) from Git..."
	@mkdir -p $(BUILD_DIR)
	@if [ ! -d "$(OGDF_SOURCE_DIR)" ]; then \
		git clone --branch $(OGDF_VERSION) --depth 1 $(OGDF_URL) $(OGDF_SOURCE_DIR); \
	else \
		echo "OGDF source already exists"; \
	fi

# Build OGDF
$(OGDF_BUILD_DIR)/libOGDF.dll.a $(OGDF_BUILD_DIR)/libOGDF.a: $(OGDF_SOURCE_DIR)
	@echo "Building OGDF..."
	@mkdir -p $(OGDF_BUILD_DIR)
	@cd $(OGDF_BUILD_DIR) && \
		cmake -G $(CMAKE_GENERATOR) \
			-DCMAKE_BUILD_TYPE=Release \
			-DCMAKE_CXX_STANDARD=17 \
			-DCMAKE_POSITION_INDEPENDENT_CODE=ON \
			$(abspath $(OGDF_SOURCE_DIR))
	@cd $(OGDF_BUILD_DIR) && $(MAKE_CMD)
	@echo "OGDF build completed!"

# Compile JNI wrapper
$(JNI_OBJECTS): $(JNI_SOURCES) $(OGDF_BUILD_DIR)/libOGDF.dll.a | $(BUILD_DIR)
	@echo "Compiling JNI wrapper..."
	$(CXX) $(CXXFLAGS) $(JNI_INCLUDES) $(OGDF_INCLUDES) \
		-c $(JNI_SOURCES) -o $(JNI_OBJECTS)

# Link library
$(BUILD_DIR)/$(JNI_LIB): $(JNI_OBJECTS)
	@echo "Linking JNI library..."
	$(CXX) $(LDFLAGS) $(JNI_OBJECTS) $(OGDF_LIBS) \
		-o $(BUILD_DIR)/$(JNI_LIB)

	# Copy to Maven target/classes/native directory
	mkdir -p $(TARGET_NATIVE_DIR)
	cp $(BUILD_DIR)/$(JNI_LIB) $(TARGET_NATIVE_DIR)/
	@echo "JNI library copied to $(TARGET_NATIVE_DIR): $(JNI_LIB)"

# Clean everything including OGDF
clean:
	rm -rf $(BUILD_DIR)
	rm -rf $(TARGET_NATIVE_DIR)

check-deps:
	@echo "Checking build dependencies..."
	@command -v git >/dev/null 2>&1 || { echo "Error: git is required but not installed"; exit 1; }
	@command -v cmake >/dev/null 2>&1 || { echo "Error: cmake is required but not installed"; exit 1; }
	@command -v $(CXX) >/dev/null 2>&1 || { echo "Error: $(CXX) is required but not installed"; exit 1; }
	@if [ -z "$(JAVA_HOME)" ]; then echo "Error: JAVA_HOME not set"; exit 1; fi
	@if [ ! -d "$(JAVA_HOME)" ]; then echo "Error: JAVA_HOME directory does not exist: $(JAVA_HOME)"; exit 1; fi
	@echo "All dependencies found!"

.PHONY: all clean check-deps
