package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Home view.
 * Handles user interactions on the home page.
 */
public class HomeController {

    @FXML
    private HBox navbar;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic here
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

    /**
     * Handle Start Adventure button click - navigate to Courses.
     */
    @FXML
    private void handleStartAdventure() {
        navigateToView(AppConfig.VIEW_COURSES, "Courses");
    }

    /**
     * Handle Watch Trailer button click.
     */
    @FXML
    private void handleWatchTrailer() {
        // TODO: Open video trailer modal
        System.out.println("Watch Trailer clicked");
    }

    /**
     * Handle Sign Up button click.
     */
    @FXML
    private void handleSignUp() {
        navigateToView(AppConfig.VIEW_SIGNUP, "Sign Up");
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
