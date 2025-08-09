# Build script for the Rosetta Language Extension
# This script compiles the TypeScript code and packages the extension into a VSIX file

# Ensure the script exits on error
$ErrorActionPreference = "Stop"

Write-Host "Building Rosetta Language Extension..." -ForegroundColor Green

# Navigate to the extension directory
$currentDir = Get-Location
Write-Host "Current directory: $currentDir"

# Clean any previous build artifacts
Write-Host "Cleaning previous build artifacts..." -ForegroundColor Cyan
if (Test-Path "out") {
    Remove-Item -Recurse -Force "out"
}

# Compile TypeScript
Write-Host "Compiling TypeScript..." -ForegroundColor Cyan
npm run compile

# Check if compilation was successful
if (-not $?) {
    Write-Host "TypeScript compilation failed. See errors above." -ForegroundColor Red
    exit 1
}

# Package the extension
Write-Host "Packaging extension..." -ForegroundColor Cyan
npm run package

# Check if packaging was successful
if (-not $?) {
    Write-Host "Extension packaging failed. See errors above." -ForegroundColor Red
    exit 1
}

Write-Host "Extension build completed successfully!" -ForegroundColor Green
Write-Host "VSIX file created in the current directory." -ForegroundColor Green
Write-Host "You can now install the extension using: code --install-extension rune-language-5.0.0.vsix" -ForegroundColor Green
