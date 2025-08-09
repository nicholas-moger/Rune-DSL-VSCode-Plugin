# Rune DSL Language Support

**⚠️ EXPERIMENTAL EXTENSION ⚠️**

Visual Studio Code extension providing language support for the Rune DSL (Domain Specific Language). This extension enables syntax highlighting, error checking, auto-completion, and other language features for `.rosetta` files.

**Independence:** This is a community-developed extension built from open-source Rune DSL components. It is **not** affiliated with, endorsed by, or sponsored by Regnosys Ltd, FINOS, Microsoft, or the Rosetta platform.

---

## 🚨 Important Notice

This is an **experimental extension** with known limitations:
- **Performance issues** may occur with large workspaces
- **Not 100% feature complete** — some advanced IDE features may be missing or unstable
- **Active development** — APIs and behavior may change

---

## 📖 Attribution & Open Source

This extension incorporates components from the open-source **Rune DSL** project maintained under the **Fintech Open Source Foundation (FINOS)** and is licensed under **Apache License 2.0**.

**Original sources:**
- **Core Language Server & Tooling:** [`finos/rune-dsl`](https://github.com/finos/rune-dsl) — Apache-2.0
- **Base VS Code example:** Derived from the example in the Rune DSL repository and extended with additional features

**License for this extension:** **Apache 2.0** (same as upstream). See `LICENSE`.  
We recommend including a `NOTICE` file:

```
This product includes software developed as part of the FINOS Rune DSL project
(https://github.com/finos/rune-dsl), Copyright (c) REGnosys
and contributors, licensed under the Apache License, Version 2.0.
```

If you redistribute third-party binaries or code with separate licenses, include their texts and (optionally) a short `THIRD_PARTY_NOTICES.md`.

---

## 🏢 About the Rosetta Platform (separate product)

**Rosetta** is a data-modelling platform by **Regnosys Ltd** that offers community and paid tiers. It is distinct from this extension. References to Rosetta are for context only.

- **This extension:** Free, open-source VS Code tooling for editing `.rosetta` files
- **Rosetta platform:** A separate commercial product with advanced UI, collaboration and enterprise features
- **Relationship:** This project is developed independently and uses open-source Rune DSL components

> Organizations needing comprehensive modelling capabilities, collaboration, governance, or enterprise support can evaluate the official Rosetta platform separately.

---

## ✨ Features

- **Syntax Highlighting** for `.rosetta`
- **Error Checking** with real-time diagnostics  
- **Auto-completion** (intelligent suggestions)
- **Hover Information** for symbols and types
- **Code Formatting**
- **Quick Fixes** (code actions/refactors)
- **Java Code Generation** (configurable)
- **Template Management** (copy basic types)
- **Windows Platform** focus — tested on Windows 10/11

---

## 🔧 Prerequisites

### System Requirements (tested configuration)
- **OS:** Windows 11 (version 10.0)
- **VS Code:** 1.103.0 (x64)
- **Architecture:** x64 (64-bit)

### Required Software

#### 1) Java Development Kit (JDK)
- **Minimum:** Java 11
- **Recommended:** Java 17 or 21 (LTS)
- **Tested:** OpenJDK 21.0.8 (Eclipse Adoptium/Temurin)

Install from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/) and verify:

```bash
java -version
# Example:
# openjdk version "21.0.8" 2025-07-15 LTS
# OpenJDK Runtime Environment Temurin-21.0.8+9 (build 21.0.8+9-LTS)
```

#### 2) Apache Maven
- **Minimum:** 3.6+
- **Recommended:** 3.9+
- **Tested:** 3.9.11

```bash
mvn -version
# Example:
# Apache Maven 3.9.11 (3e54c93a704957b63ee3494413a2b544fd3d825b)
# Java version: 21.0.8, vendor: Eclipse Adoptium
```

#### 3) Visual Studio Code
- **Minimum:** 1.73.0+
- **Tested:** 1.103.0  
Download: https://code.visualstudio.com/

#### 4) Git (optional but recommended)
- **Version:** 2.0+ (tested 2.50.1)

### Development prerequisites (for building from source)

#### Node.js & npm
- **Node.js:** 18+ (tested 22.18.0)
- **npm:** 8+ (tested 10.9.3)

### Environment Setup (Windows)

Ensure the following are in your `PATH`:
- `JAVA_HOME\bin`
- `MAVEN_HOME\bin`
- `git` (if using Git)

Verify:
```powershell
java -version
mvn -version
code --version
git --version
node --version   # if building from source
npm --version    # if building from source
```

### Linux/macOS/WSL (community tested)

```bash
# Ubuntu/Debian example
sudo apt update
sudo apt install openjdk-21-jdk maven

# Node.js (if building from source)
curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify
java -version
mvn -version
```

### Memory & Performance

- **RAM:** 4GB minimum (8GB+ recommended for large workspaces)
- **Storage:** ~500MB free
- **CPU:** Modern multi-core recommended

Optional JVM tuning for large projects:
```json
{
  "runeDsl.languageServer.javaOpts": "-Xmx2g -Xms512m"
}
```

---

## 🚀 Quick Start

1. Install the extension from the VS Code Marketplace  
2. Open a folder containing `.rosetta` files  
3. Optional: configure settings
   - Enable/disable Java generation
   - Set output dir for generated code
   - Restrict workspace analysis scope

---

## ⚙️ Configuration (Settings → Extensions → Rune DSL Language Support)

- **Java Code Generation:** `runeDsl.codeGeneration.enableJavaGeneration` (default: `false`)
- **Output Path:** `runeDsl.codeGeneration.outputPath` (default: `src/generated`)
- **Workspace Roots:** `runeDsl.workspace.roots`
- **Only Open Files:** `runeDsl.workspace.onlyOpenFiles`
- **Exclude Globs:** `runeDsl.workspace.excludeGlobs`
- **Trace Level:** `runeDsl.languageServer.traceLevel` (`off`, `messages`, `verbose`)
- **JVM Options:** `runeDsl.languageServer.javaOpts` (e.g., `-Xmx2g -Dprop=value`)
- **Templates Path:** `runeDsl.templates.basicTypesPath`
- **Startup Prompts:** `runeDsl.startup.*`

---

## 📁 Suggested Project Layout

```
your-project/
├── rosetta-models/     # Your .rosetta files
│   ├── types.rosetta
│   └── rules.rosetta
├── src/generated/      # Generated Java code (if enabled)
└── .vscode/
    └── settings.json   # Workspace-specific settings
```

---

## 🔧 Troubleshooting

### Java
- **“Java not found”**: Install Java and ensure it’s in `PATH`
- **Wrong version**: Use Java 11+ (`java -version`)
- **`JAVA_HOME` not set**: Some systems require it

### Maven
- **`mvn` not found**: Add Maven to `PATH`
- **Build failures**: Verify `mvn -version` shows your Java

### VS Code
- **Activation issues**: VS Code 1.73.0+ required
- **Missing deps**: Restart VS Code / reinstall
- **Permissions**: On Windows, try running VS Code as admin

### Performance
- Limit scope via `runeDsl.workspace.roots`
- Enable `runeDsl.workspace.onlyOpenFiles`
- Increase memory via `runeDsl.languageServer.javaOpts`

### Language Server won’t start
- Check Java install & PATH
- Inspect **Output → Rune DSL Language Server**
- Restart VS Code

**Large workspaces:** Initial scans may produce provisional diagnostics. Narrow scope or open only the files you need.

---

## 🖥️ Platform Compatibility

**Tested**
- ✅ Windows 11 (primary)
- ✅ Windows 10 (expected compatible)

**Community-tested (feedback welcome)**
- ❓ WSL
- ❓ macOS
- ❓ Linux

**Windows details**
- `.bat` scripts for language server
- Java detection via `PATH`/`JAVA_HOME`
- PowerShell build script `build-extension.ps1` for development

---

## 🔄 Automatic Updates (from upstream Rune DSL)

The build script can pull the latest Rune DSL from GitHub:

```powershell
# Pull latest and rebuild (Windows PowerShell)
pwsh -NoProfile -File "build-extension.ps1" -CreateVSIX

# The script automatically pulls from the main branch
# To force a fresh build, first clean the workspace:
pwsh -NoProfile -File "build-extension.ps1" -Clean
```

**Note**: The extension currently uses Windows PowerShell build scripts only. Unix shell scripts (`.sh`) are no longer maintained.

---

## 🧰 Build

```bash
# Build the entire project
mvn clean install -DskipTests

# Navigate to the VS Code extension
cd rosetta-ide/vscode

# Install npm dependencies and compile
npm install
npm run compile
```

### Build Scripts by Platform

**Windows (tested & supported):**
- `build-extension.ps1` — complete build (PowerShell)
- `npm run build-extension-ps` — calls the PowerShell script

**Unix-like (legacy - no longer maintained):**
- `./build-extension.sh` — **DEPRECATED** - shell script no longer maintained
- `npm run build-extension` — **DEPRECATED** - calls legacy shell script

### Development Scripts

- `npm run compile` — TypeScript compile only  
- `npm run watch` — TypeScript watch mode  
- `npm run clean-ps` — clean artifacts (Windows PowerShell)
- `npm run build` — build + VSIX package

**Note**: Environment variables like `RUNE_DSL_BRANCH` are no longer supported in the current PowerShell build.

---

## 🧪 Development & Testing

**F5 testing (Extension Development Host)**

1. Build once:
   ```powershell
   pwsh -NoProfile -File "build-extension.ps1" -CreateVSIX
   # or, if already built:
   npm run compile
   ```
2. Open the extension folder in VS Code
3. Press **F5**
4. Open a `.rosetta` file and try:
   - syntax highlighting
   - diagnostics/completions
   - **Copy Basic Rosetta Types to Workspace**
   - **Restart Language Server**

**Tips**
- Watch mode: `./node_modules/.bin/tsc --watch`
- Reload extension: `Ctrl+R` / `Cmd+R` in the Dev Host
- Verbose logs: set `runeDsl.languageServer.traceLevel = "verbose"`

**WSL notes**
- Run all commands within WSL
- Path conversions are handled at runtime
- Prefer `./node_modules/.bin/tsc` over `npx tsc`

**VSIX package**
```bash
npm run build
# produces e.g. rune-language-5.0.0.vsix
```

**After-build tree**
```
vscode/
├── src/
│   ├── extension.ts
│   ├── rosetta/
│   │   ├── bin/      # rune-dsl-ls, rune-dsl-ls.bat
│   │   └── repo/     # JARs
│   └── tsconfig.json
├── syntaxes/
│   └── rosetta.tmLanguage.json
├── resources/
│   └── templates/
│       ├── basictypes.rosetta
│       └── annotations.rosetta
├── out/
│   └── extension.js
├── build-info.json
└── package.json
```

---

## 🧩 Commands

- **Copy Basic Rosetta Types to Workspace**
- **Copy Basic Rune DSL Types from GitHub**
- **Toggle Java Code Generation**
- **Open Rune DSL Settings**
- **Restart Language Server**

---

## 🏗️ Java Code Generation

Disabled by default to avoid clutter.

- Enable: `runeDsl.codeGeneration.enableJavaGeneration = true`
- Output directory: `runeDsl.codeGeneration.outputPath` (default: `src/generated`)
- Quick toggle via command palette

On startup the extension may show an info message about current code-gen status (disable via `runeDsl.startup.showCodeGenerationInfo = false`).

Generated code may include:
- Java classes for data types
- Validation logic
- Serialization/deserialization
- Builder patterns

---

## 🧭 Build Information

The build writes `build-info.json`:
```json
{
  "buildDate": "2025-06-20T12:00:00Z",
  "runeDslCommit": "abc123...",
  "runeDslCommitDate": "2025-06-20 11:30:00 +0000",
  "repository": "https://github.com/finos/rune-dsl.git",
  "branch": "main"
}
```

---

## 🤝 Contributing

Contributions are welcome! Please open issues/PRs in this repository.  
To contribute to Rune DSL itself, see [`finos/rune-dsl`](https://github.com/finos/rune-dsl).

---

## 📄 License

This project is licensed under the **Apache License 2.0** — see `LICENSE` for details.

---

## 🔗 Related Projects

- **Rune DSL:** https://github.com/finos/rune-dsl  
- **FINOS:** https://www.finos.org/  
- **Rosetta DSL Documentation:** https://docs.rosetta-technology.io/

---

## ⚖️ Trademarks & Disclaimer

“Rosetta” and any other product names are trademarks of their respective owners.  
Use here is for identification only — **no affiliation or endorsement is implied**.

**Disclaimer:** This extension is provided “as-is” without warranty. Evaluate suitability for your use case, especially in production environments.
