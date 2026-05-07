package controllers;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Projects view.
 */
public class ProjectsController {

    @FXML
    private HBox navbar;

    @FXML
    public void initialize() {
        // Initialization logic
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
     * Handle Start Project button for Weather App (Project 1).
     */
    @FXML
    private void handleStartProject1() {
        System.out.println("Starting Weather App project...");
        // TODO: Navigate to project detail/editor view or initiate project
    }

    /**
     * Handle Start Project button for E-Commerce Platform (Project 2).
     */
    @FXML
    private void handleStartProject2() {
        System.out.println("Starting E-Commerce Platform project...");
        // TODO: Navigate to project detail/editor view or initiate project
    }

    /**
     * Handle Continue Project button for Chat Application (Project 3).
     */
    @FXML
    private void handleContinueProject() {
        System.out.println("Continuing Chat Application project...");
        // TODO: Navigate to project detail/editor view or continue where left off
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
