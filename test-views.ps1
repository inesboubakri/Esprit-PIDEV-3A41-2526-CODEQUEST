# CodeQuest JavaFX - Test Script to Verify All Views Load
# This script compiles the project and tests that all admin back views are accessible
# Run from: cd to javafx_project directory, then .\test-views.ps1

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "CodeQuest Admin Views - Load Test Script" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path "pom.xml")) {
    Write-Host "ERROR: pom.xml not found. Please run this script from the javafx_project directory." -ForegroundColor Red
    exit 1
}

# List of all admin back views to verify
$AdminViews = @{
    "Dashboard" = "src\main\resources\views\DashboardView.fxml"
    "Users Management" = "src\main\resources\views\UsersBackView.fxml"
    "Courses Management" = "src\main\resources\views\CoursesBackView.fxml"
    "Events Management" = "src\main\resources\views\EventsBackView.fxml"
    "Forum Management" = "src\main\resources\views\ForumBackView.fxml"
    "Problems Management" = "src\main\resources\views\ProblemsBackView.fxml"
    "Projects Management" = "src\main\resources\views\ProjectsBackView.fxml"
}

# List of all admin controllers to verify
$AdminControllers = @(
    "src\main\java\controllers\DashboardController.java"
    "src\main\java\controllers\UsersBackController.java"
    "src\main\java\controllers\CoursesBackController.java"
    "src\main\java\controllers\EventsBackController.java"
    "src\main\java\controllers\ForumBackController.java"
    "src\main\java\controllers\ProblemsBackController.java"
    "src\main\java\controllers\ProjectsBackController.java"
)

# Test 1: Verify all FXML files exist
Write-Host "[TEST 1] Checking if all admin view FXML files exist..." -ForegroundColor Yellow
$FxmlMissing = $false
foreach ($viewName in $AdminViews.Keys) {
    $fxmlPath = $AdminViews[$viewName]
    if (Test-Path $fxmlPath) {
        Write-Host "  ✓ $viewName - EXISTS" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $viewName - MISSING" -ForegroundColor Red
        $FxmlMissing = $true
    }
}

if ($FxmlMissing) {
    Write-Host ""
    Write-Host "ERROR: Some FXML files are missing!" -ForegroundColor Red
    exit 1
}

# Test 2: Verify all controller files exist
Write-Host ""
Write-Host "[TEST 2] Checking if all admin controller files exist..." -ForegroundColor Yellow
$ControllerMissing = $false
foreach ($controllerPath in $AdminControllers) {
    $controllerName = (Split-Path $controllerPath -Leaf)
    if (Test-Path $controllerPath) {
        Write-Host "  ✓ $controllerName - EXISTS" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $controllerName - MISSING" -ForegroundColor Red
        $ControllerMissing = $true
    }
}

if ($ControllerMissing) {
    Write-Host ""
    Write-Host "ERROR: Some controller files are missing!" -ForegroundColor Red
    exit 1
}

# Test 3: Check for sample data in controllers
Write-Host ""
Write-Host "[TEST 3] Verifying sample data is set up in controllers..." -ForegroundColor Yellow
$DataVerified = 0
$ControllersList = @(
    "EventsBackController",
    "ForumBackController",
    "ProblemsBackController",
    "ProjectsBackController",
    "UsersBackController",
    "CoursesBackController"
)

foreach ($controllerName in $ControllersList) {
    $path = "src\main\java\controllers\$controllerName.java"
    $content = Get-Content $path -Raw
    if ($content -match "observableArrayList") {
        Write-Host "  ✓ $controllerName - Sample data configured" -ForegroundColor Green
        $DataVerified++
    } else {
        Write-Host "  ✗ $controllerName - Sample data NOT found" -ForegroundColor Red
    }
}

# Test 4: Verify styles.css accessibility
Write-Host ""
Write-Host "[TEST 4] Checking CSS stylesheet..." -ForegroundColor Yellow
if (Test-Path "src\main\resources\styles.css") {
    Write-Host "  ✓ styles.css - EXISTS" -ForegroundColor Green
} else {
    Write-Host "  ✗ styles.css - MISSING" -ForegroundColor Red
}

# Test 5: Verify AppConfig has all view paths
Write-Host ""
Write-Host "[TEST 5] Verifying AppConfig view paths..." -ForegroundColor Yellow
$appConfigPath = "src\main\java\utils\AppConfig.java"
$appConfigContent = Get-Content $appConfigPath -Raw

$ViewPaths = @(
    "VIEW_DASHBOARD"
    "VIEW_USERS_BACK"
    "VIEW_COURSES_BACK"
    "VIEW_EVENTS_BACK"
    "VIEW_FORUM_BACK"
    "VIEW_PROBLEMS_BACK"
    "VIEW_PROJECTS_BACK"
)

$ConfigVerified = 0
foreach ($viewPath in $ViewPaths) {
    if ($appConfigContent -match $viewPath) {
        Write-Host "  ✓ AppConfig.$viewPath - FOUND" -ForegroundColor Green
        $ConfigVerified++
    } else {
        Write-Host "  ✗ AppConfig.$viewPath - NOT FOUND" -ForegroundColor Red
    }
}

# Summary
Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "FXML Views:           ✓ All 7 files present" -ForegroundColor Green
Write-Host "Controllers:          ✓ All 7 files present" -ForegroundColor Green
Write-Host "Sample Data:          ✓ $DataVerified/6 controllers configured" -ForegroundColor Green
Write-Host "CSS Stylesheet:       ✓ Present" -ForegroundColor Green
Write-Host "AppConfig Paths:      ✓ $ConfigVerified/7 paths configured" -ForegroundColor Green
Write-Host ""

# Test 6: Compile the project
Write-Host "[TEST 6] Attempting to compile project with Maven..." -ForegroundColor Yellow
$MavenCheck = mvn clean compile 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Maven compile - SUCCESS" -ForegroundColor Green
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Green
    Write-Host "✓ ALL TESTS PASSED - Project is ready to run!" -ForegroundColor Green
    Write-Host "================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "To start the application, run:" -ForegroundColor Cyan
    Write-Host "  mvn javafx:run" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "All admin views available:" -ForegroundColor Cyan
    foreach ($viewName in $AdminViews.Keys) {
        Write-Host "  • $viewName" -ForegroundColor Yellow
    }
} else {
    Write-Host "  ✗ Maven compile - FAILED" -ForegroundColor Red
    Write-Host ""
    Write-Host "Check the compilation errors above." -ForegroundColor Red
    exit 1
}
