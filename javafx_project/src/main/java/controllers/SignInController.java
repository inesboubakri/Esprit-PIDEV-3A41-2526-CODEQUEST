package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import utils.AppConfig;
import utils.NavigationManager;
import utils.SessionManager;

/**
 * Controller for the Sign In view.
 * Handles user authentication and role-based routing.
 */
public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    /**
     * Handle Sign In button click.
     * Validates credentials, checks ban status, and routes based on user role.
     */
    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validation
        if (email == null || email.trim().isEmpty()) {
            showError("Please enter your email address");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showError("Please enter your password");
            return;
        }

        if (!email.contains("@")) {
            showError("Please enter a valid email address");
            return;
        }

        // Debug: Log login attempt
        System.out.println("DEBUG: Attempting login with email: " + email);

        // Verify credentials
        User user = UserDAO.verifyLogin(email, password);

        // Debug: Log result
        if (user == null) {
            System.out.println("DEBUG: Login failed - User not found or password incorrect");
            System.out.println("DEBUG: Email used: " + email);
            showError("Invalid email or password");
            return;
        }

        System.out.println("DEBUG: Login successful - User found: " + user.getNomComplet());

        // Check if user is banned
        if (user.getIsBanned() == 1) {
            showError("Your account has been banned. Contact support for more information.");
            return;
        }

        // Set session
        SessionManager.setCurrentUser(user.getId(), user.getNomComplet(), user.getEmail(),
                user.getIsAdmin(), user.getIsBanned());

        System.out.println("DEBUG: Session created for user ID: " + user.getId());

        // Route based on role
        String nextView;
        String title;

        if (user.getIsAdmin() == 1) {
            nextView = "views/AdminDashboardView.fxml";
            title = "Admin Dashboard";
        } else {
            nextView = "views/ProfileView.fxml";
            title = "My Profile";
        }

        navigateToView(nextView, title);
    }

    /**
     * Handle Sign Up redirect link.
     */
    @FXML
    private void handleSignUpRedirect() {
        navigateToView("views/SignUpView.fxml", "Sign Up");
    }

    /**
     * Navigation handlers - these route to their respective pages
     * Note: At sign-in page, only route back to sign-in or sign-up
     */
    @FXML
    private void handleNavHome() {
        // Already on auth page - stay here
    }

    @FXML
    private void handleNavCourses() {
        // Already on auth page - stay here or go to courses if logged in
        // For unauthenticated users, keep them on sign-in
    }

    @FXML
    private void handleNavProblems() {
        // Already on auth page - stay here
    }

    @FXML
    private void handleNavProjects() {
        // Already on auth page - stay here
    }

    @FXML
    private void handleNavEvents() {
        // Already on auth page - stay here
    }

    @FXML
    private void handleNavForum() {
        // Already on auth page - stay here
    }

    @FXML
    private void handleNavUsers() {
        // Already on auth page - stay here
    }

    /**
     * Display error message.
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
            clearForm();
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
            showError("An error occurred. Please try again.");
        }
    }

    /**
     * Clear the form fields.
     */
    private void clearForm() {
        emailField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }
}
