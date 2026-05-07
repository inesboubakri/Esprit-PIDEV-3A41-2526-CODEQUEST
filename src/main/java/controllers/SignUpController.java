package controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;
import utils.PaymentSession;
import models.User;
import models.SocialUserInfo;
import dao.UserDAO;
import utils.Session;
import utils.OAuthService;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Controller for the Sign Up view.
 * Handles user registration interactions including traditional email+password signup
 * and OAuth2 social signup (Google, GitHub, Discord).
 */
public class SignUpController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private CheckBox termsCheckBox;
    @FXML private Label socialEmailStatus;
    @FXML private Button googleSignUpButton;
    @FXML private Button githubSignUpButton;
    @FXML private Button discordSignUpButton;

    // Store current social user info for reference
    private SocialUserInfo currentSocialUserInfo = null;

    @FXML
    public void initialize() {
        // Initialization logic here
        if (socialEmailStatus != null) {
            socialEmailStatus.setVisible(false);
            socialEmailStatus.setManaged(false);
        }
    }

    /**
     * Handle traditional Sign Up form submission (email + password).
     */
    @FXML
    private void handleSignUp() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        // Hide previous messages
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("All fields are required");
            return;
        }

        // ✅ Full Name Validation - Only letters, spaces, and hyphens
        if (!isValidName(name)) {
            showError("Full Name must contain only letters, spaces, and hyphens (no numbers or special characters)");
            return;
        }

        // ✅ Email Format Validation
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address (e.g., user@example.com)");
            return;
        }

        // ✅ Password Format Validation - Only letters and numbers
        if (!isValidPassword(password)) {
            showError("Password must contain only letters (A-Z, a-z) and numbers (0-9). No symbols allowed.");
            return;
        }

        // ✅ Minimum Password Length
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }

        // ✅ Password Match Validation
        if (!password.equals(confirm)) {
            showError("Passwords do not match");
            return;
        }

        if (!termsCheckBox.isSelected()) {
            showError("You must agree to the Terms of Service");
            return;
        }

        // Hash password and register
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(name, email, hashed);

        UserDAO dao = new UserDAO();
        if (dao.register(user)) {
            System.out.println("✅ Registration successful!");
            showSuccess("✓ Account created successfully! Redirecting...");

            new Thread(() -> {
                try {
                    Thread.sleep(800); // Shorter delay to navigate faster
                    javafx.application.Platform.runLater(() -> {
                        // Store user info for package selection
                        PackageSelectionController.pendingEmail = email;
                        PackageSelectionController.pendingName = name;
                        PaymentSession.store(email, PaymentSession.PACKAGE_PENDING_CHOICE);

                        navigateToView(AppConfig.VIEW_PAYMENT, "Payment");
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            showError("Registration failed - Email may already be in use");
        }
    }

    /**
     * Handle Google Sign Up button click.
     * Initiates OAuth2 flow with Google in a background thread.
     */
    @FXML
    public void handleGoogleSignUp() {
        System.out.println("🔵 Google Sign Up clicked");
        handleSocialSignUp("google");
    }

    /**
     * Handle GitHub Sign Up button click.
     * Initiates OAuth2 flow with GitHub in a background thread.
     */
    @FXML
    public void handleGithubSignUp() {
        System.out.println("🐙 GitHub Sign Up clicked");
        handleSocialSignUp("github");
    }

    /**
     * Handle Discord Sign Up button click.
     * Initiates OAuth2 flow with Discord in a background thread.
     */
    @FXML
    public void handleDiscordSignUp() {
        System.out.println("🎮 Discord Sign Up clicked");
        handleSocialSignUp("discord");
    }

    /**
     * Generic handler for social signup OAuth2 flows.
     * Runs OAuth flow in background thread, then creates or redirects user.
     * 
     * @param provider The OAuth provider ("google", "github", or "discord")
     */
    private void handleSocialSignUp(String provider) {
        // Show loading state
        showSocialStatus("⏳ Connecting to " + capitalizeProvider(provider) + "...", false);
        disableSocialButtons(true);

        // Run OAuth flow in background thread to avoid blocking UI
        Task<SocialUserInfo> oauthTask = new Task<SocialUserInfo>() {
            @Override
            protected SocialUserInfo call() throws Exception {
                System.out.println("🔐 Starting OAuth2 flow for " + provider);
                
                return switch (provider) {
                    case "google" -> OAuthService.startGoogleOAuth();
                    case "github" -> OAuthService.startGithubOAuth();
                    case "discord" -> OAuthService.startDiscordOAuth();
                    default -> null;
                };
            }

            @Override
            protected void succeeded() {
                SocialUserInfo socialUserInfo = getValue();
                Platform.runLater(() -> {
                    if (socialUserInfo != null) {
                        handleOAuthSuccess(socialUserInfo);
                    } else {
                        handleOAuthFailure("Authorization cancelled or failed");
                    }
                });
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                Platform.runLater(() -> {
                    String errorMsg = exception != null ? exception.getMessage() : "Unknown error";
                    handleOAuthFailure(errorMsg);
                });
                exception.printStackTrace();
            }
        };

        // Execute task in background
        new Thread(oauthTask).start();
    }

    /**
     * Handle successful OAuth2 authentication during signup.
     * Checks if user exists - if yes, show error; if no, create account or pre-fill form.
     * 
     * @param socialUserInfo The user information from OAuth provider
     */
    private void handleOAuthSuccess(SocialUserInfo socialUserInfo) {
        System.out.println("✅ OAuth successful during signup: " + socialUserInfo);
        currentSocialUserInfo = socialUserInfo;
        
        showSocialStatus("✅ Verified via " + socialUserInfo.getProviderDisplayName(), false);

        // Check if user already exists
        UserDAO dao = new UserDAO();
        User existingUser = dao.findByEmail(socialUserInfo.email());

        if (existingUser != null) {
            // User already exists - show error and offer to sign in
            System.out.println("❌ User with email " + socialUserInfo.email() + " already exists");
            showError("❌ Account already exists with this email. Please Sign In instead.");
            showSocialStatus("Account already registered", true);
            disableSocialButtons(false);
            return;
        }

        // User doesn't exist - create account
        System.out.println("📝 Creating new user account from " + socialUserInfo.getProviderDisplayName());
        User newUser = dao.registerFromSocial(socialUserInfo);

        if (newUser != null) {
            System.out.println("✅ User created successfully!");
            Session.setCurrentUser(newUser);
            showSuccess("✅ Account created via " + socialUserInfo.getProviderDisplayName() + "! Redirecting...");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() ->
                        navigateToView(AppConfig.VIEW_HOME, "Home")
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            System.out.println("❌ Failed to create user account");
            showError("Failed to create account. Please try traditional signup.");
            disableSocialButtons(false);
        }
    }

    /**
     * Handle failed OAuth2 authentication during signup.
     * Shows error message and re-enables buttons.
     * 
     * @param errorMessage Description of what went wrong
     */
    private void handleOAuthFailure(String errorMessage) {
        System.out.println("❌ OAuth failed: " + errorMessage);
        showError("❌ " + errorMessage);
        showSocialStatus("❌ " + errorMessage, true);
        disableSocialButtons(false);
    }

    /**
     * Validate Full Name: Only letters, spaces, and hyphens
     * @param name The full name to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        // Pattern: Only letters (uppercase and lowercase), spaces, and hyphens
        // Min 2 characters, max 100 characters
        return name.matches("^[a-zA-Z\\s\\-]{2,100}$");
    }

    /**
     * Validate Email Format
     * @param email The email to validate
     * @return true if valid email format, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Standard email regex pattern
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validate Password: Only letters and numbers (no special characters)
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // Pattern: Only uppercase letters, lowercase letters, and numbers
        return password.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Display success message
     */
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
    }

    /**
     * Display social signup status message
     */
    private void showSocialStatus(String message, boolean isError) {
        if (socialEmailStatus != null) {
            socialEmailStatus.setText(message);
            if (isError) {
                socialEmailStatus.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12; -fx-font-weight: bold;");
            } else {
                socialEmailStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12; -fx-font-weight: bold;");
            }
            socialEmailStatus.setVisible(true);
            socialEmailStatus.setManaged(true);
        }
    }

    /**
     * Disable/enable social signup buttons
     */
    private void disableSocialButtons(boolean disable) {
        if (googleSignUpButton != null) googleSignUpButton.setDisable(disable);
        if (githubSignUpButton != null) githubSignUpButton.setDisable(disable);
        if (discordSignUpButton != null) discordSignUpButton.setDisable(disable);
    }

    /**
     * Capitalize provider name
     */
    private String capitalizeProvider(String provider) {
        return switch (provider) {
            case "google" -> "Google";
            case "github" -> "GitHub";
            case "discord" -> "Discord";
            default -> provider;
        };
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
