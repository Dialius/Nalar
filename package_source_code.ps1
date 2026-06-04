# ========================================================================
#            🦉 Nalar Android Source Code Packager Utility 🦉
# ========================================================================
# Description: Cleans build artifacts and packages the pure Android source
#              code into a lightweight zip file for Google Drive submission.
# ========================================================================

$ErrorActionPreference = "Stop"

# 1. Define Paths
$WorkspacePath = "d:\Nalar"
$DeliverablesPath = Join-Path $WorkspacePath "deliverables"
$TempPath = Join-Path $WorkspacePath "temp_packaging_dir"
$ZipDestPath = Join-Path $DeliverablesPath "Source_Code_Android.zip"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "         🦉 Nalar Source Code Packager Utility" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 2. Check if deliverables directory exists, create if not
if (-not (Test-Path $DeliverablesPath)) {
    Write-Host "Creating deliverables directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $DeliverablesPath | Out-Null
}

# 3. Clean up existing files
if (Test-Path $ZipDestPath) {
    Write-Host "Removing existing Source_Code_Android.zip..." -ForegroundColor Yellow
    Remove-Item $ZipDestPath -Force
}
if (Test-Path $TempPath) {
    Write-Host "Cleaning stale temporary paths..." -ForegroundColor Yellow
    Remove-Item $TempPath -RecurRecurse -Force
}

# 4. Create temporary staging directory
New-Item -ItemType Directory -Path $TempPath | Out-Null

Write-Host "Preparing source files (excluding heavy build files)..." -ForegroundColor Yellow

# 5. Selective copy to staging folder (excluding build, .gradle, .idea, .kotlin, node_modules)
$FilesToCopy = @(
    "build.gradle.kts",
    "settings.gradle.kts",
    "gradle.properties",
    "gradlew",
    "gradlew.bat",
    "README.md",
    "LICENSE",
    ".gitignore"
)

# Copy root config files
foreach ($file in $FilesToCopy) {
    $src = Join-Path $WorkspacePath $file
    if (Test-Path $src) {
        Copy-Item -Path $src -Destination $TempPath -Force
    }
}

# Copy gradle wrapper folder
if (Test-Path (Join-Path $WorkspacePath "gradle")) {
    Copy-Item -Path (Join-Path $WorkspacePath "gradle") -Destination $TempPath -Recurse -Force
}

# Copy Android app package selectively (skipping 'build')
$AppSrc = Join-Path $WorkspacePath "app"
$AppDest = Join-Path $TempPath "app"
if (Test-Path $AppSrc) {
    New-Item -ItemType Directory -Path $AppDest | Out-Null
    
    # Copy app src, build.gradle, and configs
    Get-ChildItem -Path $AppSrc -Exclude "build" | ForEach-Object {
        Copy-Item -Path $_.FullName -Destination $AppDest -Recurse -Force
    }
}

# 6. Compress staging folder
Write-Host "Compressing source files into Source_Code_Android.zip..." -ForegroundColor Yellow
Compress-Archive -Path "$TempPath\*" -DestinationPath $ZipDestPath -Force

# 7. Cleanup temp folder
Write-Host "Cleaning up temporary staging directories..." -ForegroundColor Yellow
Remove-Item $TempPath -Recurse -Force

# 8. Report Status
if (Test-Path $ZipDestPath) {
    $fileSize = (Get-Item $ZipDestPath).Length / 1MB
    Write-Host "`n==========================================================" -ForegroundColor Green
    Write-Host "🎉 SUCCESS: Source code packaged successfully!" -ForegroundColor Green
    Write-Host "📍 Output File: $ZipDestPath" -ForegroundColor White
    Write-Host "📦 File Size:   $('{0:N2}' -f $fileSize) MB" -ForegroundColor White
    Write-Host "==========================================================" -ForegroundColor Green
    Write-Host "Ready to be uploaded to your Google Drive group folder!" -ForegroundColor Cyan
} else {
    Write-Error "Failed to generate Source_Code_Android.zip"
}
