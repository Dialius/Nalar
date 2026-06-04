# ========================================================================
#            🌐 Nalar Express API Source Code Packager Utility 🌐
# ========================================================================
# Description: Cleans build artifacts and packages the Node.js Express
#              API source code into a clean, lightweight zip file.
# ========================================================================

$ErrorActionPreference = "Stop"

# 1. Define Paths
$WorkspacePath = "d:\Nalar"
$BackendPath = Join-Path $WorkspacePath "nalar-backend"
$DeliverablesPath = Join-Path $WorkspacePath "deliverables"
$SubfoldersDestPath = Join-Path $DeliverablesPath "XI PPLG 3 - Kelompok Nalar - Nalar\2_Source_Code_dan_Build"
$TempPath = Join-Path $WorkspacePath "temp_api_packaging_dir"

# Define Output Zip Paths
$ZipDestPath1 = Join-Path $DeliverablesPath "Source_Code_API.zip"
$ZipDestPath2 = Join-Path $SubfoldersDestPath "2_Source_Code_API.zip"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "         🌐 Nalar API Source Code Packager Utility" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 2. Check if destination paths exist
if (-not (Test-Path $DeliverablesPath)) {
    Write-Host "Creating deliverables directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $DeliverablesPath | Out-Null
}
if (-not (Test-Path $SubfoldersDestPath)) {
    Write-Host "Creating group deliverables sub-directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $SubfoldersDestPath | Out-Null
}

# 3. Clean up existing files
if (Test-Path $ZipDestPath1) {
    Write-Host "Removing old Source_Code_API.zip..." -ForegroundColor Yellow
    Remove-Item $ZipDestPath1 -Force
}
if (Test-Path $ZipDestPath2) {
    Write-Host "Removing old 2_Source_Code_API.zip..." -ForegroundColor Yellow
    Remove-Item $ZipDestPath2 -Force
}
if (Test-Path $TempPath) {
    Write-Host "Cleaning stale temporary paths..." -ForegroundColor Yellow
    Remove-Item $TempPath -Recurse -Force
}

# 4. Create temporary staging directory
New-Item -ItemType Directory -Path $TempPath | Out-Null

Write-Host "Preparing API source files (excluding node_modules & .git)..." -ForegroundColor Yellow

# 5. Copy files and folders selectively
$ItemsToCopy = @(
    "database",
    "middleware",
    "routes",
    "server.js",
    "package.json",
    "package-lock.json",
    "README.md",
    "LICENSE",
    "Nalar_API.postman_collection.json",
    ".env",
    ".env.example",
    "test-api.js",
    ".gitignore"
)

foreach ($item in $ItemsToCopy) {
    $src = Join-Path $BackendPath $item
    if (Test-Path $src) {
        if (Test-Path -Path $src -PathType Container) {
            Copy-Item -Path $src -Destination $TempPath -Recurse -Force
        } else {
            Copy-Item -Path $src -Destination $TempPath -Force
        }
    }
}

# 6. Compress staging folder
Write-Host "Compressing API source files into Source_Code_API.zip..." -ForegroundColor Yellow
Compress-Archive -Path "$TempPath\*" -DestinationPath $ZipDestPath1 -Force

# Copy compressed archive to group project folder as well
Copy-Item -Path $ZipDestPath1 -Destination $ZipDestPath2 -Force

# 7. Cleanup temp folder
Write-Host "Cleaning up temporary staging directories..." -ForegroundColor Yellow
Remove-Item $TempPath -Recurse -Force

# 8. Report Status
if (Test-Path $ZipDestPath2) {
    $fileSize1 = (Get-Item $ZipDestPath1).Length / 1KB
    $fileSize2 = (Get-Item $ZipDestPath2).Length / 1KB
    
    $sizeStr1 = "{0:N2}" -f $fileSize1
    $sizeStr2 = "{0:N2}" -f $fileSize2

    Write-Host "`n==========================================================" -ForegroundColor Green
    Write-Host "🎉 SUCCESS: API source code packaged successfully!" -ForegroundColor Green
    Write-Host "📍 Primary Zip:   $ZipDestPath1 ($sizeStr1 KB)" -ForegroundColor White
    Write-Host "📍 Group Folder:  $ZipDestPath2 ($sizeStr2 KB)" -ForegroundColor White
    Write-Host "==========================================================" -ForegroundColor Green
    Write-Host "Ready for Google Drive group folder submission!" -ForegroundColor Cyan
} else {
    Write-Error "Failed to generate Source_Code_API.zip"
}
