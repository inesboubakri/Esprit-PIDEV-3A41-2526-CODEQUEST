package controllers;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import org.mindrot.jbcrypt.BCrypt;
import utils.EmailService;
import utils.PasswordResetSession;
import dao.UserDAO;
import models.User;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Controller for the Forgot Password flow
 * 
 * This controller manages a 3-step password reset process:
 * Step 1: User enters their email and receives a reset code
 * Step 2: User enters the 6-digit code they received
 * Step 3: User sets a new password
 * 
 * All 3 steps are displayed in the same window using VBox visibility toggling
 */
public class ForgotPasswordController {
    
    // Email validation regex pattern
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    // Step 1: Email Entry
    @FXML
    private VBox stepEmailBox;
    
    @FXML
    private TextField resetEmailField;
    
    @FXML
    private Label emailStepError;
    
    @FXML
    private Button sendCodeButton;
    
    // Step 2: Code Entry
    @FXML
    private VBox stepCodeBox;
    
    @FXML
    private TextField codeField;
    
    @FXML
    private Label codeStepError;
    
    @FXML
    private Label codeStepInfo;
    
    // Step 3: New Password
    @FXML
    private VBox stepNewPasswordBox;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmNewPasswordField;
    
    @FXML
    private Label passwordStepError;
    
    @FXML
    public void initialize() {
        // Initialize: only Step 1 is visible
        stepEmailBox.setVisible(true);
        stepEmailBox.setManaged(true);
        stepCodeBox.setVisible(false);
        stepCodeBox.setManaged(false);
        stepNewPasswordBox.setVisible(false);
        stepNewPasswordBox.setManaged(false);
        
        // Hide error labels by default
        emailStepError.setVisible(false);
        emailStepError.setManaged(false);
        codeStepError.setVisible(false);
        codeStepError.setManaged(false);
        passwordStepError.setVisible(false);
        passwordStepError.setManaged(false);
        
        System.out.println("✅ ForgotPasswordController initialized");
    }
    
    /**
     * Step 1: Send password reset code to user's email
     */
    @FXML
    public void handleSendCode() {
        String email = resetEmailField.getText().trim();
        
        // Validate email format
        if (email.isEmpty()) {
            showEmailError("Please enter your email address");
            return;
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showEmailError("Please enter a valid email address");
            return;
        }
        
        // Check if user exists
        User user = new UserDAO().findByEmail(email);
        if (user == null) {
            showEmailError("No account found with this email address");
            return;
        }
        
        // Generate 6-digit reset code
        String code = String.format("%06d", new Random().nextInt(1000000));
        System.out.println("✅ Generated reset code: " + code + " for " + email);
        
        // Store code in session
        PasswordResetSession.storeCode(email, code);
        
        // Disable button and show sending status
        sendCodeButton.setDisable(true);
        sendCodeButton.setText("📤 Sending...");
        emailStepError.setVisible(false);
        emailStepError.setManaged(false);
        
        // Send email in background thread (don't block UI)
        new Thread(() -> {
            boolean sent = EmailService.sendPasswordResetCode(email, code);
            
            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                if (sent) {
                    System.out.println("✅ Email sent successfully");
                    // Move to Step 2
                    stepEmailBox.setVisible(false);
                    stepEmailBox.setManaged(false);
                    stepCodeBox.setVisible(true);
                    stepCodeBox.setManaged(true);
                    codeStepInfo.setText("📧 Verification code sent to " + email);
                    codeField.requestFocus();
                } else {
                    System.err.println("❌ Failed to send email");
                    sendCodeButton.setDisable(false);
                    sendCodeButton.setText("📧 Send Recovery Code");
                    showEmailError("Failed to send email. Check your connection and try again.");
                }
            });
        }).start();
    }
    
    /**
     * Step 2: Verify the 6-digit code
     */
    @FXML
    public void handleVerifyCode() {
        String code = codeField.getText().trim();
        String email = PasswordResetSession.getPendingEmail();
        
        if (email == null) {
            showCodeError("Session expired. Please start over.");
            showStep1();
            return;
        }
        
        if (code.isEmpty()) {
            showCodeError("Please enter the verification code");
            return;
        }
        
        if (code.length() != 6 || !code.matches("\\d{6}")) {
            showCodeError("Code must be 6 digits");
            return;
        }
        
        // Verify code
        if (!PasswordResetSession.isCodeValid(email, code)) {
            int remaining = PasswordResetSession.getRemainingSeconds();
            if (remaining <= 0) {
                showCodeError("Code expired. Please request a new one.");
            } else {
                showCodeError("Invalid code. Please try again. (" + remaining + "s remaining)");
            }
            return;
        }
        
        // Code is valid - move to Step 3
        System.out.println("✅ Code verified successfully");
        stepCodeBox.setVisible(false);
        stepCodeBox.setManaged(false);
        stepNewPasswordBox.setVisible(true);
        stepNewPasswordBox.setManaged(true);
        newPasswordField.requestFocus();
        codeStepError.setVisible(false);
        codeStepError.setManaged(false);
    }
    
    /**
     * Step 2: Resend the verification code
     */
    @FXML
    public void handleResendCode() {
        String email = PasswordResetSession.getPendingEmail();
        if (email == null) {
            showCodeError("Session expired. Please start over.");
            showStep1();
            return;
        }
        
        // Generate new code
        String newCode = String.format("%06d", new Random().nextInt(1000000));
        System.out.println("✅ Generated new reset code: " + newCode);
        
        // Store new code
        PasswordResetSession.storeCode(email, newCode);
        
        // Show sending status
        codeStepError.setVisible(false);
        codeStepError.setManaged(false);
        codeStepInfo.setText("📤 Sending new code...");
        
        // Send email in background
        new Thread(() -> {
            boolean sent = EmailService.sendPasswordResetCode(email, newCode);
            
            Platform.runLater(() -> {
                if (sent) {
                    codeStepInfo.setText("📧 New verification code sent to " + email);
                    codeField.clear();
                    codeField.requestFocus();
                    System.out.println("✅ New code sent successfully");
                } else {
                    showCodeError("Failed to send email. Check your connection.");
                }
            });
        }).start();
    }
    
    /**
     * Step 3: Reset the password
     */
    @FXML
    public void handleResetPassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();
        String email = PasswordResetSession.getPendingEmail();
        
        // Validate email
        if (email == null) {
            showPasswordError("Session expired. Please start over.");
            showStep1();
            return;
        }
        
        // Validate passwords
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showPasswordError("Please enter both password fields");
            return;
        }
        
        if (newPassword.length() < 6) {
            showPasswordError("Password must be at least 6 characters");
            return;
        }
        
        if (!newPassword.matches("^[a-zA-Z0-9]+$")) {
            showPasswordError("Password can only contain letters (A-Z, a-z) and numbers (0-9)");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showPasswordError("Passwords do not match");
            return;
        }
        
        // Get user
        User user = new UserDAO().findByEmail(email);
        if (user == null) {
            showPasswordError("User not found. Please restart.");
            PasswordResetSession.clear();
            showStep1();
            return;
        }
        
        try {
            // Hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            
            // Update password in database
            boolean updated = new UserDAO().updatePassword(user.getId(), hashedPassword);
            
            if (!updated) {
                showPasswordError("Failed to update password. Please try again.");
                return;
            }
            
            // Clear session
            PasswordResetSession.clear();
            
            // Show success message
            passwordStepError.setStyle("-fx-text-fill: #4CAF50;");
            passwordStepError.setText("✅ Password reset successfully! Redirecting to Sign In...");
            passwordStepError.setVisible(true);
            passwordStepError.setManaged(true);
            
            System.out.println("✅ Password reset successful for: " + email);
            
            // Navigate back to Sign In after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                Platform.runLater(this::handleBackToSignIn);
            }).start();
            
        } catch (Exception e) {
            System.err.println("❌ Error resetting password: "+ e.getMessage());
            e.printStackTrace();
            showPasswordError("An error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Navigate back to Sign In screen
     */
    @FXML
    public void handleBackToSignIn() {
        // Clear session
        PasswordResetSession.clear();
        
        // Navigate back
        try {
            Stage stage = (Stage) resetEmailField.getScene().getWindow();
            navigateToView(stage, "views/SignInView.fxml", "Sign In");
        } catch (Exception e) {
            System.err.println("❌ Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ─── UI HELPER METHODS ───────────────────────────────────
    
    private void showStep1() {
        stepEmailBox.setVisible(true);
        stepEmailBox.setManaged(true);
        stepCodeBox.setVisible(false);
        stepCodeBox.setManaged(false);
        stepNewPasswordBox.setVisible(false);
        stepNewPasswordBox.setManaged(false);
    }
    
    private void showEmailError(String message) {
        emailStepError.setText("❌ " + message);
        emailStepError.setVisible(true);
        emailStepError.setManaged(true);
    }
    
    private void showCodeError(String message) {
        codeStepError.setText("❌ " + message);
        codeStepError.setVisible(true);
        codeStepError.setManaged(true);
    }
    
    private void showPasswordError(String message) {
        passwordStepError.setStyle("-fx-text-fill: #d32f2f;");
        passwordStepError.setText("❌ " + message);
        passwordStepError.setVisible(true);
        passwordStepError.setManaged(true);
    }
    
    /**
     * Helper method to navigate to a view
     */
    private void navigateToView(Stage stage, String viewPath, String title) {
        try {
            utils.NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
