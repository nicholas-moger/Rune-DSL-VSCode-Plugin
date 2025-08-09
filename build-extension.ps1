# Rune DSL VS Code Extension Build Script for PowerShell
# This script builds a standalone VS Code extension by pulling from the GitHub repository

param(
    [string]$Branch = "main",
    [switch]$CleanOnly,
    [switch]$CreateVSIX,
    [switch]$Help,
    [switch]$KeepTemp
)

# Global variables
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ExtensionDir = $ScriptDir
$TempDir = Join-Path $ExtensionDir "temp-build"
$RuneDslRepo = "https://github.com/finos/rune-dsl.git"

# Override branch if environment variable is set
if ($env:RUNE_DSL_BRANCH) {
    $Branch = $env:RUNE_DSL_BRANCH
}

# Colors for output
$Colors = @{
    Info = "Blue"
    Success = "Green" 
    Warning = "Yellow"
    Error = "Red"
}

# Logging functions
function Write-LogInfo {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Colors.Info
}

function Write-LogSuccess {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $Colors.Success
}

function Write-LogWarning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Colors.Warning
}

function Write-LogError {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Colors.Error
}

# Check prerequisites
function Test-Prerequisites {
    Write-LogInfo "Checking prerequisites..."
    
    $Prerequisites = @("java", "mvn", "node", "npm", "git")
    foreach ($tool in $Prerequisites) {
        if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
            Write-LogError "$tool is not installed or not in PATH"
            return $false
        }
    }
    
    Write-LogSuccess "All prerequisites are available"
    
    # Print versions
    Write-LogInfo "Java version:"
    java -version 2>&1 | Select-String "version" | ForEach-Object { Write-Host $_.Line }
    
    Write-LogInfo "Maven version:"
    mvn -version | Select-String "Apache Maven" | ForEach-Object { Write-Host $_.Line }
    
    Write-LogInfo "Node.js version:"
    node --version
    
    Write-LogInfo "npm version:"
    npm --version
    
    return $true
}

# Clean previous build artifacts
function Clear-BuildArtifacts {
    Write-LogInfo "Cleaning previous build artifacts..."
    
    $PathsToClean = @()
    if (-not $KeepTemp) {
        $PathsToClean += $TempDir
    } else {
        Write-LogInfo "Keeping temp directory as requested: $TempDir"
    }
    $PathsToClean += @(
        (Join-Path $ExtensionDir "out"),
        (Join-Path $ExtensionDir "src\rosetta"),
        (Join-Path $ExtensionDir "syntaxes"),
        (Join-Path $ExtensionDir "resources")
    )
    
    foreach ($path in $PathsToClean) {
        if (Test-Path $path) {
            Remove-Item -Path $path -Recurse -Force
        }
    }
    
    Write-LogSuccess "Build artifacts cleaned"
}

# Setup repository
function Initialize-Repository {
    Write-LogInfo "Setting up Rune DSL repository..."
    
    if (-not (Test-Path $TempDir)) {
        New-Item -ItemType Directory -Path $TempDir -Force | Out-Null
    }
    
    Set-Location $TempDir
    
    if (Test-Path "rune-dsl") {
        Write-LogInfo "Repository already exists, updating..."
        Set-Location "rune-dsl"
        git fetch origin
        git reset --hard "origin/$Branch"
    } else {
        Write-LogInfo "Cloning repository from $RuneDslRepo..."
        git clone --depth 1 --branch $Branch $RuneDslRepo
        Set-Location "rune-dsl"
    }
    
    Write-LogSuccess "Repository setup complete"
}

    # Apply local server overrides (source files) into the temp repo prior to build
    # DISABLED: Server overrides functionality removed - using standard rune-dsl build
    # function Apply-ServerOverrides {
    #     $overridesRoot = Join-Path $ExtensionDir "server-overrides"
    #     if (Test-Path $overridesRoot) {
    #         Write-LogInfo "Applying server overrides from $overridesRoot..."
    #         $dstRoot = Join-Path $TempDir "rune-dsl"
    #         Copy-Item -Path (Join-Path $overridesRoot "*") -Destination $dstRoot -Recurse -Force -ErrorAction Stop
    #         Write-LogSuccess "Server overrides applied"
    #     } else {
    #         Write-LogInfo "No server overrides directory found; skipping"
    #     }
    # }

# Build project with Maven
function Build-Project {
    Write-LogInfo "Building Rune DSL project with Maven..."
    Set-Location (Join-Path $TempDir "rune-dsl")
    
    $process = Start-Process -FilePath "mvn" -ArgumentList "clean", "install", "-DskipTests", "-q" -Wait -PassThru -NoNewWindow
    if ($process.ExitCode -ne 0) {
        Write-LogError "Maven build failed"
        return $false
    }
    
    Write-LogSuccess "Maven build complete"
    return $true
}

# Copy language server files
function Copy-LanguageServer {
    Write-LogInfo "Copying language server files..."
    
    $SourceLS = Join-Path $TempDir "rune-dsl\rosetta-ide\target\languageserver" 
    $DestLS = Join-Path $ExtensionDir "src\rosetta"
    
    if (-not (Test-Path $SourceLS)) {
        Write-LogError "Language server build output not found at $SourceLS"
        return $false
    }
    
    if (Test-Path $DestLS) {
        Remove-Item -Path $DestLS -Recurse -Force
    }
    
    if (-not (Test-Path (Split-Path $DestLS))) {
        New-Item -ItemType Directory -Path (Split-Path $DestLS) -Force | Out-Null
    }
    
    Copy-Item -Path $SourceLS -Destination $DestLS -Recurse -Force
    
    Write-LogSuccess "Language server files copied"
    return $true
}

# Copy syntax highlighting files
function Copy-SyntaxFiles {
    Write-LogInfo "Copying syntax highlighting files..."
    
    $SourceSyntax = Join-Path $TempDir "rune-dsl\rosetta-ide\src-gen\main\resources\syntaxes"
    $DestSyntax = Join-Path $ExtensionDir "syntaxes"
    
    if (-not (Test-Path $SourceSyntax)) {
        Write-LogError "Syntax files not found at $SourceSyntax"
        return $false
    }
    
    if (Test-Path $DestSyntax) {
        Remove-Item -Path $DestSyntax -Recurse -Force
    }
    
    Copy-Item -Path $SourceSyntax -Destination $DestSyntax -Recurse -Force
    
    Write-LogSuccess "Syntax files copied"
    return $true
}

# Copy basic types and templates
function Copy-BasicTypes {
    Write-LogInfo "Copying basic types and template files..."
    
    $SourceTypes = Join-Path $TempDir "rune-dsl\rosetta-runtime\src\main\resources\model"
    $DestTypes = Join-Path $ExtensionDir "resources\templates"
    
    if (-not (Test-Path $SourceTypes)) {
        Write-LogError "Basic types not found at $SourceTypes"
        return $false
    }
    
    if (-not (Test-Path $DestTypes)) {
        New-Item -ItemType Directory -Path $DestTypes -Force | Out-Null
    }
    
    Copy-Item -Path "$SourceTypes\*.rosetta" -Destination $DestTypes -Force -ErrorAction SilentlyContinue
    
    Write-LogSuccess "Basic types copied"
    return $true
}

# Build extension
function Build-Extension {
    Write-LogInfo "Installing npm dependencies..."
    Set-Location $ExtensionDir
    
    $process = Start-Process -FilePath "cmd" -ArgumentList "/c", "npm", "install" -Wait -PassThru -NoNewWindow
    if ($process.ExitCode -ne 0) {
        Write-LogError "npm install failed"
        return $false
    }    Write-LogInfo "Compiling TypeScript..."
    # Use cmd to run npx to avoid PowerShell execution policy issues
    $process = Start-Process -FilePath "cmd" -ArgumentList "/c", "npx", "tsc" -Wait -PassThru -NoNewWindow
    if ($process.ExitCode -ne 0) {
        Write-LogError "TypeScript compilation failed"
        return $false
    }
    
    Write-LogSuccess "Extension build complete"
    return $true
}

# Create VSIX package
function New-VSIXPackage {
    param([bool]$CreateVSIX = $false)
    
    if (-not $CreateVSIX) {
        return $true
    }
    
    # Auto-bump patch version so each VSIX has a new semver
    try {
        Write-LogInfo "Bumping extension version (patch)..."
        Set-Location $ExtensionDir
        # Show current version
        $pkgJson = Get-Content -Path (Join-Path $ExtensionDir 'package.json') -Raw | ConvertFrom-Json
        $oldVer = $pkgJson.version
        Write-LogInfo "Current version: $oldVer"
        # Use npm to bump without git tagging
        $verProc = Start-Process -FilePath "cmd" -ArgumentList "/c", "npm", "version", "patch", "--no-git-tag-version" -Wait -PassThru -NoNewWindow
        if ($verProc.ExitCode -ne 0) {
            Write-LogWarning "npm version patch failed; attempting manual bump"
            $parts = $oldVer.Split('.')
            if ($parts.Count -ge 3) {
                $parts[2] = ([int]$parts[2] + 1).ToString()
                $newVer = ($parts -join '.')
                $pkgJson.version = $newVer
                ($pkgJson | ConvertTo-Json -Depth 10) | Set-Content -Path (Join-Path $ExtensionDir 'package.json')
            }
        }
        # Re-read and show new version
        $pkgJsonNew = Get-Content -Path (Join-Path $ExtensionDir 'package.json') -Raw | ConvertFrom-Json
        Write-LogSuccess "New version: $($pkgJsonNew.version)"
    } catch {
        Write-LogWarning "Version bump step encountered an issue: $($_.Exception.Message)"
    }

    Write-LogInfo "Creating VSIX package..."
    Set-Location $ExtensionDir
      # Check if vsce is installed
    $vsceCheck = Start-Process -FilePath "cmd" -ArgumentList "/c", "npx", "vsce", "--version" -Wait -PassThru -NoNewWindow
    if ($vsceCheck.ExitCode -ne 0) {
        Write-LogInfo "Installing vsce (Visual Studio Code Extension manager)..."
        $process = Start-Process -FilePath "cmd" -ArgumentList "/c", "npm", "install", "-g", "@vscode/vsce" -Wait -PassThru -NoNewWindow
        if ($process.ExitCode -ne 0) {
            Write-LogError "Failed to install vsce"
            return $false
        }
    }
    
    # Create VSIX package
    $process = Start-Process -FilePath "cmd" -ArgumentList "/c", "npx", "vsce", "package" -Wait -PassThru -NoNewWindow
    if ($process.ExitCode -ne 0) {
        Write-LogError "VSIX package creation failed"
        return $false
    }
    
    # Find the created VSIX file
    $VSIXFiles = Get-ChildItem -Path $ExtensionDir -Filter "*.vsix" | Sort-Object LastWriteTime -Descending
    if ($VSIXFiles.Count -gt 0) {
        Write-LogSuccess "VSIX package created: $($VSIXFiles[0].Name)"
    } else {
        Write-LogWarning "VSIX package may have been created but not found in current directory"
    }
    
    return $true
}

# Create version info
function New-VersionInfo {
    Write-LogInfo "Creating version information..."
    
    Set-Location (Join-Path $TempDir "rune-dsl")
    
    $CommitHash = git rev-parse HEAD
    $CommitDate = git log -1 --format="%ci"
    $BuildDate = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
    
    Set-Location $ExtensionDir
    
    $VersionInfo = @{
        buildDate = $BuildDate
        runeDslCommit = $CommitHash
        runeDslCommitDate = $CommitDate
        repository = $RuneDslRepo
        branch = $Branch
    }
    
    $VersionInfo | ConvertTo-Json | Set-Content -Path "build-info.json"
    
    Write-LogSuccess "Version information created"
}

# Validate build
function Test-Build {
    Write-LogInfo "Validating build..."
    
    $RequiredFiles = @(
        (Join-Path $ExtensionDir "out\extension.js"),
        (Join-Path $ExtensionDir "src\rosetta\bin\rune-dsl-ls.bat"),
        (Join-Path $ExtensionDir "syntaxes\rosetta.tmLanguage.json")
    )
    
    foreach ($file in $RequiredFiles) {
        if (-not (Test-Path $file)) {
            $fileName = Split-Path $file -Leaf
            Write-LogError "$fileName not found - build validation failed"
            return $false
        }
    }
    
    $BasicTypesFile = Join-Path $ExtensionDir "resources\templates\basictypes.rosetta"
    if (-not (Test-Path $BasicTypesFile)) {
        Write-LogWarning "Basic types file not found - Copy Basic Types command may not work"
    }
    
    Write-LogSuccess "Build validation passed"
    return $true
}

# Cleanup temp files
function Clear-TempFiles {
    Write-LogInfo "Cleaning up temporary files..."
    
    Set-Location $ScriptDir
    if ($KeepTemp) {
        Write-LogInfo "-KeepTemp specified: skipping removal of $TempDir"
    } else {
        if (Test-Path $TempDir) {
            Remove-Item -Path $TempDir -Recurse -Force
        }
        Write-LogSuccess "Cleanup complete"
    }
}

# Show help
function Show-Help {
    Write-Host "Rune DSL VS Code Extension Build Script" -ForegroundColor Cyan
    Write-Host ""    Write-Host "Usage: .\build-extension.ps1 [OPTIONS]" -ForegroundColor White
    Write-Host ""
    Write-Host "Options:" -ForegroundColor White
    Write-Host "  -CleanOnly      Only clean build artifacts" -ForegroundColor Gray
    Write-Host "  -CreateVSIX     Create VSIX package after build" -ForegroundColor Gray
    Write-Host "  -Help           Show this help message" -ForegroundColor Gray
    Write-Host "  -Branch <name>  Git branch to build from (default: main)" -ForegroundColor Gray
    Write-Host "  -KeepTemp       Preserve the temp-build folder after build and skip pre-clean of it" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Environment Variables:" -ForegroundColor White
    Write-Host "  RUNE_DSL_BRANCH   Git branch to build from (default: main)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor White
    Write-Host "  .\build-extension.ps1                 # Basic build" -ForegroundColor Gray
    Write-Host "  .\build-extension.ps1 -CreateVSIX     # Build and create VSIX" -ForegroundColor Gray
    Write-Host "  .\build-extension.ps1 -CleanOnly      # Clean only" -ForegroundColor Gray
    Write-Host ""
}

# Main execution
function Main {
    if ($Help) {
        Show-Help
        return
    }
    
    if ($CleanOnly) {
        Clear-BuildArtifacts
        Clear-TempFiles
        Write-LogSuccess "Clean completed"
        return
    }
      Write-LogInfo "Starting Rune DSL VS Code Extension build..."
    Write-LogInfo "Extension directory: $ExtensionDir"
    Write-LogInfo "Using branch: $Branch"
    if ($CreateVSIX) {
        Write-LogInfo "VSIX package will be created after build"
    }
    
    try {
        if (-not (Test-Prerequisites)) { return }
        Clear-BuildArtifacts
        Initialize-Repository
        # Apply-ServerOverrides  # DISABLED: Using standard rune-dsl build without custom overrides
        if (-not (Build-Project)) { return }
        if (-not (Copy-LanguageServer)) { return }
        if (-not (Copy-SyntaxFiles)) { return }
        if (-not (Copy-BasicTypes)) { return }        if (-not (Build-Extension)) { return }
        New-VersionInfo
        if (-not (Test-Build)) { return }
        Clear-TempFiles  # Clean temp files BEFORE creating VSIX
        if (-not (New-VSIXPackage -CreateVSIX $CreateVSIX)) { return }
        
        Write-LogSuccess "🎉 Build completed successfully!"
        if ($CreateVSIX) {
            Write-LogInfo "Extension built and packaged as VSIX"
        } else {
            Write-LogInfo "Extension is ready for testing with F5 in VS Code"
            Write-LogInfo "To create VSIX package, run with -CreateVSIX switch"
        }
    }
    catch {
        Write-LogError "Build failed with error: $($_.Exception.Message)"
        Clear-TempFiles
    }
}

# Run main function
Main
