package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Dashboard view (backend/admin).
 * Handles admin dashboard interactions and data presentation.
 */
public class DashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private GridPane statsGrid;

    @FXML
    private VBox sidebarContent;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic here
    }

    // Sidebar navigation handlers
    @FXML
    private void handleNavDashboard() {
        navigateToView(AppConfig.VIEW_DASHBOARD, "Dashboard");
    }

    @FXML
    private void handleNavUsers() {
        navigateToView(AppConfig.VIEW_USERS_BACK, "Users Management");
    }

    @FXML
    private void handleNavCourses() {
        navigateToView(AppConfig.VIEW_COURSES_BACK, "Courses Management");
    }

    @FXML
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS_BACK, "Events Management");
    }

    @FXML
    private void handleNavForum() {
        navigateToView(AppConfig.VIEW_FORUM_BACK, "Forum Management");
    }

    @FXML
    private void handleNavProblems() {
        navigateToView(AppConfig.VIEW_PROBLEMS_BACK, "Problems Management");
    }

    @FXML
    private void handleNavProjects() {
        navigateToView(AppConfig.VIEW_PROJECTS_BACK, "Projects Management");
    }

    @FXML
    private void handleNavHome() {
        navigateToView(AppConfig.VIEW_HOME, "Home");
    }

    /**
     * Handle theme toggle button click.
     */
    @FXML
    private void handleThemeToggle() {
        // TODO: Toggle between light and dark theme
        System.out.println("Theme toggle clicked");
    }

    @FXML
    private void handleLogOut() {
        navigateToView(AppConfig.VIEW_SIGNIN, "Sign In");
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) statsGrid.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
