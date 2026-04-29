package controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;
import models.User;
import models.SocialUserInfo;
import dao.UserDAO;
import utils.Session;
import utils.FaceIdService;
import utils.OAuthService;

/**
 * Controller for the Sign In view.
 * Handles user sign-in interactions including traditional email+password login and OAuth2 social login.
 */
public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label socialLoginStatus;

    @FXML
    private Button googleSignInButton;

    @FXML
    private Button githubSignInButton;

    @FXML
    private Button discordSignInButton;

    @FXML
    private Button faceIdLoginButton;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic here
        if (socialLoginStatus != null) {
            socialLoginStatus.setVisible(false);
            socialLoginStatus.setManaged(false);
        }
    }

    /**
     * Handle Sign In button click.
     * Traditional email + password authentication.
     */
    @FXML
    public void handleSignIn() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Hide error message
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password are required");
            return;
        }

        // Query database
        UserDAO dao = new UserDAO();
        User user = dao.login(email, password);

        if (user != null) {
            Session.setCurrentUser(user);
            System.out.println("✅ Login successful! Welcome " + user.getNomComplet());
            
            // Check if user is admin and redirect accordingly
            if (user.isAdmin()) {
                System.out.println("🔐 Admin user detected - redirecting to admin panel");
                navigateToView(AppConfig.VIEW_USERS_BACK, "Admin Panel - Users");
            } else {
                System.out.println("👤 Regular user - redirecting to home");
                navigateToView(AppConfig.VIEW_HOME, "Home");
            }
        } else {
            showError("Wrong email or password");
        }
    }

    /**
     * Handle Google Sign In button click.
     * Initiates OAuth2 flow with Google in a background thread.
     */
    @FXML
    public void handleGoogleSignIn() {
        System.out.println("🔵 Google Sign In clicked");
        handleSocialSignIn("google");
    }

    /**
     * Handle GitHub Sign In button click.
     * Initiates OAuth2 flow with GitHub in a background thread.
     */
    @FXML
    public void handleGithubSignIn() {
        System.out.println("🐙 GitHub Sign In clicked");
        handleSocialSignIn("github");
    }

    /**
     * Handle Discord Sign In button click.
     * Initiates OAuth2 flow with Discord in a background thread.
     */
    @FXML
    public void handleDiscordSignIn() {
        System.out.println("🎮 Discord Sign In clicked");
        handleSocialSignIn("discord");
    }

    /**
     * Generic handler for social login OAuth2 flows.
     * Runs OAuth flow in background thread, then checks/creates user account.
     * 
     * @param provider The OAuth provider ("google", "github", or "discord")
     */
    private void handleSocialSignIn(String provider) {
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
     * Handle successful OAuth2 authentication.
     * Checks if user exists, logs in or redirects to signup.
     * 
     * @param socialUserInfo The user information from OAuth provider
     */
    private void handleOAuthSuccess(SocialUserInfo socialUserInfo) {
        System.out.println("✅ OAuth successful: " + socialUserInfo);
        showSocialStatus("✅ Authenticated via " + socialUserInfo.getProviderDisplayName(), false);

        // Check if user exists in database
        UserDAO dao = new UserDAO();
        User existingUser = dao.findByEmail(socialUserInfo.email());

        if (existingUser != null) {
            // User exists - log them in
            System.out.println("👤 User exists - logging in: " + existingUser.getNomComplet());
            Session.setCurrentUser(existingUser);
            
            // Redirect based on role
            if (existingUser.isAdmin()) {
                navigateToView(AppConfig.VIEW_USERS_BACK, "Admin Panel - Users");
            } else {
                navigateToView(AppConfig.VIEW_HOME, "Home");
            }
        } else {
            // User doesn't exist - redirect to signup with pre-filled info
            System.out.println("📝 New user - redirecting to signup with pre-filled email");
            showSocialStatus("New account - please complete signup", false);
            
            // Store social info temporarily for signup to use
            // We'll pass this through the controller hierarchy
            redirectToSignUpWithSocialInfo(socialUserInfo);
        }
    }

    /**
     * Handle failed OAuth2 authentication.
     * Shows error message and re-enables buttons.
     * 
     * @param errorMessage Description of what went wrong
     */
    private void handleOAuthFailure(String errorMessage) {
        System.out.println("❌ OAuth failed: " + errorMessage);
        showSocialStatus("❌ " + errorMessage, true);
        disableSocialButtons(false);
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Display social login status message
     */
    private void showSocialStatus(String message, boolean isError) {
        if (socialLoginStatus != null) {
            socialLoginStatus.setText(message);
            if (isError) {
                socialLoginStatus.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12; -fx-font-weight: bold;");
            } else {
                socialLoginStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12; -fx-font-weight: bold;");
            }
            socialLoginStatus.setVisible(true);
            socialLoginStatus.setManaged(true);
        }
    }

    /**
     * Disable/enable social login buttons
     */
    private void disableSocialButtons(boolean disable) {
        if (googleSignInButton != null) googleSignInButton.setDisable(disable);
        if (githubSignInButton != null) githubSignInButton.setDisable(disable);
        if (discordSignInButton != null) discordSignInButton.setDisable(disable);
    }

    /**
     * Redirect to signup view with pre-filled social information
     * This stores the social info in Session temporary storage
     */
    private void redirectToSignUpWithSocialInfo(SocialUserInfo socialUserInfo) {
        // Store social info in application properties or session for signup to read
        // Simple approach: store in static field in Session or pass via controller
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            NavigationManager.navigateTo(stage, AppConfig.VIEW_SIGNUP, "Sign Up");
            
            // The signup controller will need to be modified to read this info
            // For now, we'll clear status after navigation
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Give time for navigation
                    Platform.runLater(() -> clearSocialStatus());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Error navigating to signup: " + e.getMessage());
            e.printStackTrace();
            handleOAuthFailure("Failed to navigate to signup");
        }
    }

    /**
     * Clear social login status message
     */
    private void clearSocialStatus() {
        if (socialLoginStatus != null) {
            socialLoginStatus.setVisible(false);
            socialLoginStatus.setManaged(false);
        }
    }

    private void handleFaceIdFailure(String message) {
        showError(message != null && !message.isBlank() ? message : "Face ID login failed");
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
     * Handle Sign Up redirect link.
     */
    @FXML
    public void handleSignUpRedirect() {
        System.out.println("📝 Sign Up redirect button clicked");
        navigateToView(AppConfig.VIEW_SIGNUP, "Sign Up");
    }

    /**
     * Handle Forgot Password link.
     * Navigates to the forgot password recovery flow.
     */
    @FXML
    public void handleForgotPassword() {
        System.out.println("🔐 Forgot Password link clicked");
        navigateToView(AppConfig.VIEW_FORGOT_PASSWORD, "Forgot Password");
    }

    /**
     * Handle Face ID sign in.
     */
    @FXML
    public void handleFaceIdLogin() {
        showSocialStatus("🔍 Scanning Face ID...", false);
        if (faceIdLoginButton != null) {
            faceIdLoginButton.setDisable(true);
        }
        disableSocialButtons(true);

        Task<FaceIdService.Result> faceTask = new Task<FaceIdService.Result>() {
            @Override
            protected FaceIdService.Result call() {
                return FaceIdService.login();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (faceIdLoginButton != null) {
                        faceIdLoginButton.setDisable(false);
                    }
                    disableSocialButtons(false);
                    FaceIdService.Result result = getValue();
                    if (result == null) {
                        handleFaceIdFailure("No result was returned by the Face ID service.");
                        return;
                    }

                    if (!result.success) {
                        handleFaceIdFailure(result.message);
                        return;
                    }

                    UserDAO dao = new UserDAO();
                    User user = dao.findById(result.userId);
                    if (user == null) {
                        handleFaceIdFailure("Face matched, but the user record could not be loaded.");
                        return;
                    }

                    Session.setCurrentUser(user);
                    showSocialStatus("✅ Face ID accepted for " + user.getNomComplet(), false);

                    if (user.isAdmin()) {
                        navigateToView(AppConfig.VIEW_USERS_BACK, "Admin Panel - Users");
                    } else {
                        navigateToView(AppConfig.VIEW_HOME, "Home");
                    }
                });
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                Platform.runLater(() -> {
                    if (faceIdLoginButton != null) {
                        faceIdLoginButton.setDisable(false);
                    }
                    disableSocialButtons(false);
                    handleFaceIdFailure(exception != null ? exception.getMessage() : "Unknown Face ID error");
                });
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        };

        Thread worker = new Thread(faceTask, "face-id-login");
        worker.setDaemon(true);
        worker.start();
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

