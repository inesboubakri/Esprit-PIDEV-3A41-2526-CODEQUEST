# FRONTEND_QUICKTEST.ps1
# Quick script to start front-end testing environment

Write-Host "🚀 CodeQuest Front-End Quick Test" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Check if Python is available
$pythonCheck = python --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Python found: $pythonCheck" -ForegroundColor Green
    $usePython = $true
} else {
    Write-Host "⚠️  Python not found, trying Node.js..." -ForegroundColor Yellow
    $usePython = $false
}

# Get project root
$projectRoot = "c:\Users\msi\Downloads\projet-templates_interfaces\projet-templates_interfaces"
$homePageUrl = "http://localhost:8000/templates/front/home.html"

Write-Host ""
Write-Host "Project root: $projectRoot" -ForegroundColor Gray
Write-Host ""

# Choose method
if ($usePython) {
    Write-Host "📝 Starting HTTP Server with Python..." -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Server will start at: $homePageUrl" -ForegroundColor Yellow
    Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
    Write-Host ""
    
    cd $projectRoot
    
    Write-Host "🌐 Opening browser in 3 seconds..." -ForegroundColor Cyan
    Start-Sleep -Seconds 3
    
    # Open browser
    Start-Process "http://localhost:8000/templates/front/home.html"
    
    # Start server
    Write-Host "" 
    Write-Host "🚀 HTTP Server running..." -ForegroundColor Green
    python -m http.server 8000
} else {
    Write-Host "⚠️  Python not available" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Alternative options:" -ForegroundColor Yellow
    Write-Host "1. Install Python: https://www.python.org" -ForegroundColor Gray
    Write-Host "2. Use Node.js: npm install -g live-server && live-server" -ForegroundColor Gray
    Write-Host "3. Use VS Code Live Server extension" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Quick setup:" -ForegroundColor Cyan
    Write-Host "  npm install -g live-server" -ForegroundColor Yellow
    Write-Host "  cd $projectRoot" -ForegroundColor Yellow
    Write-Host "  live-server --port=8000 --open=templates/front/home.html" -ForegroundColor Yellow
}
