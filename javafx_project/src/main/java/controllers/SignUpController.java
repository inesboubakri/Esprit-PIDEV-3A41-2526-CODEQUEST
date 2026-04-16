package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.NavigationManager;

/**
 * Controller for the Sign Up view.
 * Handles user registration with validation and database persistence.
 */
public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        
        // Initialize role combo box
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("Student", "Mentor", "Teacher");
        }
    }

    /**
     * Handle Sign Up button click.
     * Validates all fields and creates user in database.
     */
    @FXML
    private void handleSignUp() {
        // Get values
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // Validation
        if (!validate(username, email, password, confirmPassword, role)) {
            return;
        }

        // Check if email already exists
        if (UserDAO.emailExists(email)) {
            showError("This email is already registered. Please use another email or sign in.");
            return;
        }

        // Create user with provided fields
        boolean success = UserDAO.addUser(username, email, password, role, 0, "beginner", "", "", "", "");

        if (success) {
            showSuccess("Account created successfully! Redirecting to sign in...");
            navigateToSignIn();
        } else {
            showError("Failed to create account. Please try again.");
        }
    }

    /**
     * Handle Sign In redirect link.
     */
    @FXML
    private void handleSignIn() {
        navigateToSignIn();
    }

    /**
     * Navigation handlers - these route to their respective pages
     * Note: At sign-up page, only route back to sign-in or sign-up
     */
    @FXML
    private void handleNavHome() {
        // Already on auth page - stay here
    }

    @FXML
    private void handleNavCourses() {
        // Already on auth page - stay here or go to courses if logged in
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
     * Handle Sign Up redirect link.
     */
    @FXML
    private void handleSignUpRedirect() {
        // Already on SignUp page - do nothing or refresh
    }

    /**
     * Validate all form fields.
     */
    private boolean validate(String username, String email, String password, String confirmPassword,
                            String role) {
        
        if (username == null || username.trim().isEmpty()) {
            showError("Please enter a username");
            return false;
        }

        if (username.trim().length() < 3) {
            showError("Username must be at least 3 characters long");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            showError("Please enter your email address");
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address");
            return false;
        }

        if (password == null || password.isEmpty()) {
            showError("Please enter a password");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return false;
        }

        if (role == null || role.isEmpty()) {
            showError("Please select your role");
            return false;
        }

        return true;
    }

    /**
     * Display error message.
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #d32f2f;");
        errorLabel.setVisible(true);
    }

    /**
     * Display success message.
     */
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #388e3c;");
        errorLabel.setVisible(true);
    }

    /**
     * Navigate to Sign In view.
     */
    private void navigateToSignIn() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            NavigationManager.navigateTo(stage, "views/SignInView.fxml", "Sign In");
            clearForm();
        } catch (Exception e) {
            System.err.println("Error navigating to Sign In: " + e.getMessage());
            e.printStackTrace();
            showError("An error occurred. Please try again.");
        }
    }

    /**
     * Clear all form fields.
     */
    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue(null);
        errorLabel.setVisible(false);
    }
}
