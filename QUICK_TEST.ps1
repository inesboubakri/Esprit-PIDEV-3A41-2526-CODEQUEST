Write-Host "CodeQuest - Admin Views Test" -ForegroundColor Cyan
Write-Host ""

# Test 1: Check FXML files
Write-Host "[TEST 1] Checking admin view FXML files..." -ForegroundColor Yellow
$fxmlFiles = @(
    "src\main\resources\views\DashboardView.fxml",
    "src\main\resources\views\UsersBackView.fxml",
    "src\main\resources\views\CoursesBackView.fxml",
    "src\main\resources\views\EventsBackView.fxml",
    "src\main\resources\views\ForumBackView.fxml",
    "src\main\resources\views\ProblemsBackView.fxml",
    "src\main\resources\views\ProjectsBackView.fxml"
)

$allFxmlExist = $true
foreach ($file in $fxmlFiles) {
    if (Test-Path $file) {
        Write-Host "  OK: $(Split-Path $file -Leaf)" -ForegroundColor Green
    } else {
        Write-Host "  MISSING: $file" -ForegroundColor Red
        $allFxmlExist = $false
    }
}

# Test 2: Check controller files
Write-Host ""
Write-Host "[TEST 2] Checking admin controller files..." -ForegroundColor Yellow
$controllers = @(
    "src\main\java\controllers\DashboardController.java",
    "src\main\java\controllers\UsersBackController.java",
    "src\main\java\controllers\CoursesBackController.java",
    "src\main\java\controllers\EventsBackController.java",
    "src\main\java\controllers\ForumBackController.java",
    "src\main\java\controllers\ProblemsBackController.java",
    "src\main\java\controllers\ProjectsBackController.java"
)

$allControllerExist = $true
foreach ($file in $controllers) {
    if (Test-Path $file) {
        Write-Host "  OK: $(Split-Path $file -Leaf)" -ForegroundColor Green
    } else {
        Write-Host "  MISSING: $file" -ForegroundColor Red
        $allControllerExist = $false
    }
}

# Test 3: Compile project
Write-Host ""
Write-Host "[TEST 3] Compiling project..." -ForegroundColor Yellow
mvn clean compile
$compileSuccess = $LASTEXITCODE -eq 0

if ($compileSuccess) {
    Write-Host ""
    Write-Host "SUCCESS! All components ready." -ForegroundColor Green
    Write-Host ""
    Write-Host "Run the app with: mvn javafx:run" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "FAILED! Check compilation errors above." -ForegroundColor Red
}
