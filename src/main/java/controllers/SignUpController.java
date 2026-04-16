package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Sign Up view.
 * Handles user registration interactions.
 */
public class SignUpController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic here
    }

    /**
     * Handle Sign Up button click.
     */
    @FXML
    private void handleSignUp() {
        String name = nameField.getText();
        if (name != null && !name.isEmpty()) {
            // TODO: Validate form and process sign-up
            System.out.println("Sign Up clicked for: " + name);
            // Navigate to Home after successful registration
            navigateToView(AppConfig.VIEW_HOME, "Home");
        }
    }

    /**
     * Handle Sign In redirect link.
     */
    @FXML
    private void handleSignInRedirect() {
        navigateToView(AppConfig.VIEW_SIGNIN, "Sign In");
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
