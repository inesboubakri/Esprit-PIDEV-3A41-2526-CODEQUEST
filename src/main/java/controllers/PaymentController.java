package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;
import utils.PaymentSession;
import utils.EmailService;
import models.User;
import dao.UserDAO;
import utils.Session;

import java.util.Random;
import java.util.function.UnaryOperator;
import java.time.YearMonth;

/**
 * Controller for the Payment view.
 * Handles a two-step payment flow:
 * 1. Card Details Form - collect and validate card information
 * 2. Verification Code - user enters the code sent to their email
 * 
 * This is a simulation - no real payment processing occurs.
 * The code is validated client-side and stored in PaymentSession.
 */
public class PaymentController {

    @FXML
    private Label backArrowLabel;

    @FXML
    private VBox planChoiceBox;

    @FXML
    private Label planChoiceTitleLabel;

    @FXML
    private Button chooseFreePlanButton;

    // ── Step 1: Card Form ──────────────────────────────────
    @FXML
    private VBox stepCardBox;

    @FXML
    private TextField cardNameField;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField expiryField;

    @FXML
    private TextField cvvField;

    @FXML
    private Label cardStepError;

    @FXML
    private Label packageSummaryLabel;

    @FXML
    private Button payButton;

    // ── Step 2: Verification Code ──────────────────────────
    @FXML
    private VBox stepVerifyBox;

    @FXML
    private TextField verifyCodeField;

    @FXML
    private Label verifyStepError;

    @FXML
    private Label verifyStepInfo;

    @FXML
    private Button verifyButton;

    @FXML
    private Hyperlink resendLink;

    // ── State ──────────────────────────────────────────────
    private String currentEmail = null;
    private String currentCode = null;

    @FXML
    public void initialize() {
        currentEmail = PaymentSession.getPendingEmail();
        String packageName = PaymentSession.getSelectedPackage();

        if (currentEmail == null) {
            System.err.println("❌ PaymentSession email missing!");
            showCardError("Session expired. Please try again.");
            return;
        }

        setupVerificationCodeField();

        if (PaymentSession.PACKAGE_PENDING_CHOICE.equals(packageName)) {
            System.out.println("💳 Payment Controller — plan choice step (after sign-up)");
            if (planChoiceTitleLabel != null) {
                String nm = PackageSelectionController.pendingName;
                if (nm != null && !nm.trim().isEmpty()) {
                    planChoiceTitleLabel.setText("Welcome, " + nm.trim() + "! Choose your plan");
                }
            }
            if (planChoiceBox != null) {
                planChoiceBox.setVisible(true);
                planChoiceBox.setManaged(true);
            }
            if (stepCardBox != null) {
                stepCardBox.setVisible(false);
                stepCardBox.setManaged(false);
            }
            setupPaymentFormListeners();
            return;
        }

        if (packageName == null) {
            System.err.println("❌ PaymentSession package missing!");
            showCardError("Session expired. Please try again.");
            return;
        }

        if (planChoiceBox != null) {
            planChoiceBox.setVisible(false);
            planChoiceBox.setManaged(false);
        }
        if (stepCardBox != null) {
            stepCardBox.setVisible(true);
            stepCardBox.setManaged(true);
        }

        System.out.println("💳 Payment Controller initialized");
        System.out.println("   Email: " + currentEmail);
        System.out.println("   Package: " + packageName);

        if (packageSummaryLabel != null) {
            packageSummaryLabel.setText("You are subscribing to: " + packageName + " — $9.99/month");
        }

        // Hide error labels by default
        if (cardStepError != null) {
            cardStepError.setVisible(false);
            cardStepError.setManaged(false);
        }
        if (verifyStepError != null) {
            verifyStepError.setVisible(false);
            verifyStepError.setManaged(false);
        }

        // Hide verification step by default
        if (stepVerifyBox != null) {
            stepVerifyBox.setVisible(false);
            stepVerifyBox.setManaged(false);
        }

        // Update verification step info with masked email
        if (verifyStepInfo != null) {
            String maskedEmail = maskEmail(currentEmail);
            verifyStepInfo.setText("Verification code sent to " + maskedEmail);
        }

        setupPaymentFormListeners();
    }

    /**
     * Header back control — returns to plan selection and clears checkout session state.
     */
    @FXML
    private void handleBack() {
        javafx.scene.Node node = backArrowLabel != null ? backArrowLabel : payButton;
        if (node == null || node.getScene() == null) {
            return;
        }
        Stage stage = (Stage) node.getScene().getWindow();
        String email = PaymentSession.getPendingEmail();
        PaymentSession.clear();
        if (email != null) {
            PackageSelectionController.pendingEmail = email;
        }
        NavigationManager.navigateTo(stage, AppConfig.VIEW_PACKAGE_SELECTION, "Choose Your Plan");
    }

    private void setupPaymentFormListeners() {
        setupCardNumberFormatting();
        setupExpiryFormatting();
        setupCVVFormatting();
    }

    /**
     * Limits code entry to six digits and avoids TextField alignment issues that cause
     * IllegalArgumentException on Backspace (JavaFX 21 / centered caret).
     */
    private void setupVerificationCodeField() {
        if (verifyCodeField == null) {
            return;
        }
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String next = change.getControlNewText();
            if (next.matches("\\d{0,6}")) {
                return change;
            }
            return null;
        };
        verifyCodeField.setTextFormatter(new TextFormatter<>(filter));
    }

    /**
     * User chose the Free plan on the payment screen (e.g. after sign-up).
     */
    @FXML
    private void handleChooseFreePlan() {
        System.out.println("🆓 Free plan chosen from payment screen");

        if (currentEmail == null || currentEmail.isEmpty()) {
            showCardError("Email not found. Please try signing up again.");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByEmail(currentEmail);

            if (user == null) {
                showCardError("User not found. Please try signing up again.");
                return;
            }

            if (!userDAO.updateSubscription(user.getId(), "free")) {
                showCardError("Failed to update subscription. Please try again.");
                return;
            }

            user = userDAO.findByEmail(currentEmail);
            Session.setCurrentUser(user);

            PackageSelectionController.pendingEmail = null;
            PackageSelectionController.pendingName = null;
            PaymentSession.clear();

            Stage stage = (Stage) chooseFreePlanButton.getScene().getWindow();
            NavigationManager.navigateTo(stage, AppConfig.VIEW_PROFILE, "Profile");

        } catch (Exception e) {
            System.err.println("❌ handleChooseFreePlan: " + e.getMessage());
            e.printStackTrace();
            showCardError("An error occurred. Please try again.");
        }
    }

    /**
     * User chose Premium — show card form and lock session to pro checkout.
     */
    @FXML
    private void handleChoosePremiumPlan() {
        System.out.println("👑 Premium plan chosen from payment screen");

        if (currentEmail == null || currentEmail.isEmpty()) {
            showCardError("Email not found. Please try signing up again.");
            return;
        }

        PaymentSession.store(currentEmail, "pro");

        if (planChoiceBox != null) {
            planChoiceBox.setVisible(false);
            planChoiceBox.setManaged(false);
        }
        if (stepCardBox != null) {
            stepCardBox.setVisible(true);
            stepCardBox.setManaged(true);
        }
        if (packageSummaryLabel != null) {
            packageSummaryLabel.setText("You are subscribing to: pro — $9.99/month");
        }

        hideCardError();
    }

    /**
     * Handle the Pay button click.
     * Validates card details and initiates the verification code flow.
     */
    @FXML
    private void handlePay() {
        System.out.println("💳 Pay button clicked");

        // Clear previous errors
        hideCardError();

        // Get form values
        String cardName = cardNameField.getText().trim();
        String cardNumber = cardNumberField.getText().replaceAll("\\s", ""); // Remove spaces
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();

        // ── Validation ──────────────────────────────────────
        // 1. Card Name Validation
        if (cardName.isEmpty()) {
            showCardError("Please enter the name on the card");
            return;
        }
        if (!cardName.matches("[a-zA-Z\\s-]+")) {
            showCardError("Card name must contain only letters, spaces, and hyphens");
            return;
        }

        // 2. Card Number Validation (16 digits)
        if (cardNumber.isEmpty()) {
            showCardError("Please enter your card number");
            return;
        }
        if (!cardNumber.matches("\\d{16}")) {
            showCardError("Card number must be exactly 16 digits");
            return;
        }

        // 3. Expiry Validation (MM/YY format and not expired)
        if (expiry.isEmpty()) {
            showCardError("Please enter the expiry date");
            return;
        }
        if (!expiry.matches("\\d{2}/\\d{2}")) {
            showCardError("Expiry date must be in MM/YY format");
            return;
        }

        // Check if card is expired
        if (isCardExpired(expiry)) {
            showCardError("This card has expired");
            return;
        }

        // 4. CVV Validation (exactly 3 digits)
        if (cvv.isEmpty()) {
            showCardError("Please enter the CVV");
            return;
        }
        if (!cvv.matches("\\d{3}")) {
            showCardError("CVV must be exactly 3 digits");
            return;
        }

        // ── All Valid ──────────────────────────────────────
        System.out.println("✅ All card details valid");

        // Generate 6-digit verification code
        currentCode = generateVerificationCode();
        System.out.println("📝 Generated verification code: " + currentCode);

        // Store the code in PaymentSession
        PaymentSession.storePaymentCode(currentEmail, currentCode);

        // Disable the pay button and show loading state
        payButton.setDisable(true);
        payButton.setText("⏳ Sending code...");

        // Send verification email in background thread
        new Thread(() -> {
            boolean sent = EmailService.sendPaymentVerificationCode(
                currentEmail,
                currentCode,
                "Pro"
            );

            if (sent) {
                Platform.runLater(this::showVerificationStep);
            } else {
                System.err.println("❌ Failed to send verification code");
                Platform.runLater(() -> {
                    payButton.setDisable(false);
                    payButton.setText("💳 Pay and Verify");
                    showCardError("Failed to send verification code. Please try again.");
                });
            }
        }).start();
    }

    /**
     * Switches from the card form to the email verification step. Both {@code visible} and
     * {@code managed} must be updated — leaving {@code stepVerifyBox} unmanaged breaks layout.
     */
    private void showVerificationStep() {
        if (stepCardBox != null) {
            stepCardBox.setVisible(false);
            stepCardBox.setManaged(false);
        }
        if (stepVerifyBox != null) {
            stepVerifyBox.setVisible(true);
            stepVerifyBox.setManaged(true);
        }
        if (verifyStepInfo != null) {
            verifyStepInfo.setText(
                "Enter the 6-digit code we sent to " + maskEmail(currentEmail) + ". It expires in 10 minutes.");
        }
        if (verifyCodeField != null) {
            verifyCodeField.clear();
            verifyCodeField.requestFocus();
        }
        hideVerifyError();
    }

    /**
     * Handle the verification button click.
     * Validates the code and completes the payment/subscription.
     */
    @FXML
    private void handleVerifyPayment() {
        System.out.println("✅ Verify button clicked");

        // Clear previous errors
        hideVerifyError();

        String enteredCode = verifyCodeField.getText().trim();

        if (enteredCode.isEmpty()) {
            showVerifyError("Please enter the verification code");
            return;
        }

        // Check if code is valid
        if (!PaymentSession.isCodeValid(currentEmail, enteredCode)) {
            showVerifyError("Invalid or expired code. Please try again or resend.");
            return;
        }

        // ── Payment Successful ──────────────────────────────
        System.out.println("✅ Verification code valid - Payment successful!");

        // Disable verify button
        verifyButton.setDisable(true);
        verifyButton.setText("⏳ Processing...");

        try {
            // Get the user by email
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByEmail(currentEmail);

            if (user == null) {
                showVerifyError("User not found. Please try again.");
                verifyButton.setDisable(false);
                verifyButton.setText("✅ Confirm payment");
                return;
            }

            // Update user subscription to "pro"
            boolean updated = userDAO.updateSubscription(user.getId(), "pro");
            if (!updated) {
                showVerifyError("Failed to update subscription. Please try again.");
                verifyButton.setDisable(false);
                verifyButton.setText("✅ Confirm payment");
                return;
            }

            // Refresh user data and set in session
            user = userDAO.findByEmail(currentEmail);
            user.setSubscription("pro");
            Session.setCurrentUser(user);

            System.out.println("✅ User " + user.getNomComplet() + " upgraded to Premium");

            // Set flag to show welcome popup on profile page
            ProfileController.showPremiumWelcome = true;

            // Clear payment session
            PaymentSession.clear();

            // Navigate to Profile with a small delay to let UI update
            Platform.runLater(() -> {
                Stage stage = (Stage) verifyButton.getScene().getWindow();
                NavigationManager.navigateTo(stage, AppConfig.VIEW_PROFILE, "Profile");
            });

        } catch (Exception e) {
            System.err.println("❌ Error completing payment: " + e.getMessage());
            e.printStackTrace();
            showVerifyError("An error occurred. Please try again.");
            verifyButton.setDisable(false);
            verifyButton.setText("✅ Confirm payment");
        }
    }

    /**
     * Handle the Resend Code link click.
     * Re-sends the verification code to the user's email.
     */
    @FXML
    private void handleResendCode() {
        System.out.println("📧 Resend code clicked");

        if (currentCode == null) {
            showVerifyError("No code to resend. Please contact support.");
            return;
        }

        // Disable resend link temporarily
        resendLink.setDisable(true);

        // Send email in background thread
        new Thread(() -> {
            boolean sent = EmailService.sendPaymentVerificationCode(
                currentEmail,
                currentCode,
                "Premium"
            );

            Platform.runLater(() -> {
                if (sent) {
                    System.out.println("✅ Verification code resent successfully");
                    showVerifyInfo("✅ Code resent to your email!");
                    
                    // Re-enable resend link after 2 seconds
                    new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> resendLink.setDisable(false));
                            }
                        },
                        2000
                    );
                } else {
                    showVerifyError("Failed to resend code. Please try again.");
                    resendLink.setDisable(false);
                }
            });
        }).start();
    }

    // ────────────── Helper Methods ──────────────────────────

    /**
     * Generate a random 6-digit verification code.
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit random number
        return String.valueOf(code);
    }

    /**
     * Check if a card expiry date (MM/YY) is in the past.
     */
    private boolean isCardExpired(String expiry) {
        try {
            String[] parts = expiry.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            // Convert 2-digit year to 4-digit (assuming 2000s)
            if (year < 100) {
                year += 2000;
            }

            // Get current month/year
            YearMonth currentMonth = YearMonth.now();
            YearMonth cardMonth = YearMonth.of(year, month);

            // Card is expired if expiry month is before current month
            return cardMonth.isBefore(currentMonth);
        } catch (Exception e) {
            return true; // If parsing fails, consider it expired
        }
    }

    /**
     * Mask email for display (e.g., user@example.com → u***@example.com).
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) return email;
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return email;
        
        String masked = email.charAt(0) + "***" + email.substring(atIndex);
        return masked;
    }

    // ────────────── UI Update Methods ──────────────────────

    private void showCardError(String message) {
        Platform.runLater(() -> {
            if (cardStepError != null) {
                cardStepError.setText("❌ " + message);
                cardStepError.setStyle("-fx-text-fill: #e53935; -fx-font-size: 13px;");
                cardStepError.setVisible(true);
                cardStepError.setManaged(true);
            }
        });
    }

    private void hideCardError() {
        if (cardStepError != null) {
            cardStepError.setVisible(false);
            cardStepError.setManaged(false);
        }
    }

    private void showVerifyError(String message) {
        Platform.runLater(() -> {
            if (verifyStepError != null) {
                verifyStepError.setText("❌ " + message);
                verifyStepError.setStyle("-fx-text-fill: #e53935; -fx-font-size: 13px;");
                verifyStepError.setVisible(true);
                verifyStepError.setManaged(true);
            }
        });
    }

    private void hideVerifyError() {
        if (verifyStepError != null) {
            verifyStepError.setVisible(false);
            verifyStepError.setManaged(false);
        }
    }

    private void showVerifyInfo(String message) {
        Platform.runLater(() -> {
            if (verifyStepError != null) {
                verifyStepError.setText(message);
                verifyStepError.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 13px;");
                verifyStepError.setVisible(true);
                verifyStepError.setManaged(true);
            }
        });
    }

    // ────────────── Card Formatting Methods ─────────────────

    /**
     * Set up auto-formatting for card number (spaces every 4 digits).
     * Use a flag to prevent recursive updates.
     */
    private boolean isUpdatingCardNumber = false;

    private void setupCardNumberFormatting() {
        cardNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingCardNumber) return;

            // Remove non-digits
            String digitsOnly = newVal.replaceAll("\\D", "");

            // Limit to 16 digits
            if (digitsOnly.length() > 16) {
                digitsOnly = digitsOnly.substring(0, 16);
            }

            // Add spaces every 4 digits
            String formatted = "";
            for (int i = 0; i < digitsOnly.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formatted += " ";
                }
                formatted += digitsOnly.charAt(i);
            }

            isUpdatingCardNumber = true;
            cardNumberField.setText(formatted);
            isUpdatingCardNumber = false;
        });
    }

    /**
     * Set up auto-formatting for expiry date (MM/YY format).
     */
    private boolean isUpdatingExpiry = false;

    private void setupExpiryFormatting() {
        expiryField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingExpiry) return;

            // Remove non-digits
            String digitsOnly = newVal.replaceAll("\\D", "");

            // Limit to 4 digits (MMYY)
            if (digitsOnly.length() > 4) {
                digitsOnly = digitsOnly.substring(0, 4);
            }

            // Add slash after 2 digits
            String formatted = "";
            if (digitsOnly.length() >= 2) {
                formatted = digitsOnly.substring(0, 2) + "/";
                if (digitsOnly.length() > 2) {
                    formatted += digitsOnly.substring(2);
                }
            } else {
                formatted = digitsOnly;
            }

            isUpdatingExpiry = true;
            expiryField.setText(formatted);
            isUpdatingExpiry = false;
        });
    }

    /**
     * Set up auto-formatting for CVV (max 3 digits).
     */
    private boolean isUpdatingCVV = false;

    private void setupCVVFormatting() {
        cvvField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isUpdatingCVV) return;

            // Remove non-digits and limit to 3
            String digitsOnly = newVal.replaceAll("\\D", "");
            if (digitsOnly.length() > 3) {
                digitsOnly = digitsOnly.substring(0, 3);
            }

            isUpdatingCVV = true;
            cvvField.setText(digitsOnly);
            isUpdatingCVV = false;
        });
    }
}
