package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Sign In view.
 * Handles user sign-in interactions.
 */
public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic here
    }

    /**
     * Handle Sign In button click.
     */
    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        if (email != null && !email.isEmpty()) {
            // TODO: Validate and process sign-in
            System.out.println("Sign In clicked: " + email);
            // Navigate to Home after successful login
            navigateToView(AppConfig.VIEW_HOME, "Home");
        }
    }

    /**
     * Handle Sign Up redirect link.
     */
    @FXML
    private void handleSignUpRedirect() {
        navigateToView(AppConfig.VIEW_SIGNUP, "Sign Up");
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
