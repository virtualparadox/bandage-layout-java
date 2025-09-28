# 🧩 Bandage Layout JNI

A **tiny but powerful** Java JNI wrapper around the **OGDF FMMM** (Fast Multipole Multilevel) layout algorithm –  
the same one used by [Bandage](https://rrwick.github.io/Bandage/).  
Perfect if you want **Bandage-like graph layouts** directly in your Java applications.

---

## ✨ Features

- 🔄 **Identical layouts** to Bandage  
- 💻 **Cross-platform**: Linux • macOS • Windows  
- ☕ **Simple Java API** – just a few lines of code  
- 🎚️ All **Bandage quality levels** (FAST → MAXIMUM)  
- ⚡ **Automatic OGDF build** – no manual steps required  

---

## 🚀 Quick Start

### ⚡ Using with Maven

To use **Bandage Layout JNI** in your project, add the dependency:

```xml
<dependency>
    <groupId>eu.virtualparadox</groupId>
    <artifactId>bandage-layout-jni</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 🔨 Build

The build process automatically downloads and compiles OGDF:

```bash
# Full build (downloads OGDF v2023.09 on first run)
mvn clean compile

# Or build native only
./build-native.sh
```

💡 **Tip:** First build may take **5–15 minutes**. Subsequent builds are much faster.

---

### 📦 Usage Example

```java
import eu.virtualparadox.bandage.model.*;
import eu.virtualparadox.bandage.layout.*;

// Create a small graph
BandageGraph graph = new BandageGraph();
BandageNode n1 = new BandageNode("A");
BandageNode n2 = new BandageNode("B");
graph.addNode(n1);
graph.addNode(n2);
graph.addEdge(n1, n2);

// Run layout
BandageLayout layout = new BandageLayout();
layout.layout(graph, LayoutQuality.MEDIUM);

// Get positions
System.out.printf("Node A at: %.2f, %.2f%n", n1.getX(), n1.getY());
```

---

## 📚 API Overview

### Core Classes
- 🕸️ **`BandageGraph`** – Lightweight graph container  
- 🔵 **`BandageNode`** – Graph node with ID & position  
- 🔗 **`BandageEdge`** – Graph edge with weight  
- 🧮 **`BandageLayout`** – Main JNI layout wrapper  
- 🎚️ **`LayoutQuality`** – Enum for quality settings  

### Layout Methods

```java
// Basic layout
layout.layout(graph, LayoutQuality.MEDIUM);

// With custom parameters
layout.layout(graph, quality, linearLayout, aspectRatio, componentSeparation);

// For linear chains
layout.layoutLinear(graph, LayoutQuality.HIGH);
```

---

## 🛠️ Building

### Prerequisites
- ☕ Java **21+**
- 📦 Maven **3.6+**
- 🛠️ CMake **3.16+**
- 🌐 Git
- 🧑‍💻 C++ compiler (g++ / clang++ / MSVC)

### Automatic Build Process
The build system will:
1. 📥 **Download OGDF** (v2023.09) from GitHub  
2. 🏗️ **Build OGDF** with Bandage-compatible settings  
3. 🔗 **Compile & link JNI wrapper** against OGDF  
4. 📦 **Produce native lib** (`.so` / `.dylib` / `.dll`)  
5. 🚚 **Copy into resources** for runtime loading  

### Useful Commands

```bash
# Full build (Java + Native)
mvn clean compile

# Native only
cd src/main/native && make all

# Check build dependencies
cd src/main/native && make check-deps

# Clean JNI artifacts (keep OGDF)
cd src/main/native && make clean-jni

# Clean everything including OGDF
cd src/main/native && make clean
```

---

## ✅ Testing

```bash
mvn test
```

📌 Note: Tests require the **native library** to be built first.

---

Made with ❤️ by **Virtual Paradox Bt.** – bringing Bandage layouts to the Java world.
