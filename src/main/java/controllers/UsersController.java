package controllers;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Users view (Arena - Users Community).
 */
public class UsersController {

    @FXML
    private HBox navbar;

    @FXML
    public void initialize() {
        // Initialization
    }

    // Navbar navigation handlers
    @FXML
    private void handleNavHome() {
        navigateToView(AppConfig.VIEW_HOME, "Home");
    }

    @FXML
    private void handleNavCourses() {
        navigateToView(AppConfig.VIEW_COURSES, "Courses");
    }

    @FXML
    private void handleNavProblems() {
        navigateToView(AppConfig.VIEW_PROBLEMS, "Problems");
    }

    @FXML
    private void handleNavProjects() {
        navigateToView(AppConfig.VIEW_PROJECTS, "Projects");
    }

    @FXML
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS, "Events");
    }

    @FXML
    private void handleNavForum() {
        navigateToView(AppConfig.VIEW_FORUM, "Forum");
    }

    @FXML
    private void handleNavUsers() {
        navigateToView(AppConfig.VIEW_USERS, "Users");
    }

    @FXML
    private void handleNavProfile() {
        navigateToView(AppConfig.VIEW_PROFILE, "Profile");
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
            Stage stage = (Stage) navbar.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
