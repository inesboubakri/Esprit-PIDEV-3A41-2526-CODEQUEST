package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.NavigationManager;
import utils.AppConfig;
import utils.PaymentSession;
import models.User;
import dao.UserDAO;
import utils.Session;

/**
 * Controller for the Package Selection view.
 * Users arrive here after successful registration to choose between Free and Premium plans.
 * 
 * Flow:
 * 1. User completes Sign Up → redirected here with email and name stored in static fields
 * 2. Shows two package options: Free and Premium
 * 3. Free: Updates subscription to "free", enters Session, navigates to Profile
 * 4. Premium: Stores in PaymentSession, navigates to Payment page for card entry
 */
public class PackageSelectionController {

    @FXML
    private VBox freeCard;

    @FXML
    private VBox premiumCard;

    @FXML
    private Label userWelcomeLabel;

    @FXML
    private Label statusLabel;

    // Static fields set by SignUpController before navigation
    public static String pendingEmail = null;
    public static String pendingName = null;

    @FXML
    public void initialize() {
        // Set the welcome message with the user's name
        if (pendingName != null && !pendingName.trim().isEmpty()) {
            userWelcomeLabel.setText("Welcome, " + pendingName + "! Choose your plan 🎉");
        }

        // Hide status label by default
        if (statusLabel != null) {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        }

        System.out.println("✅ PackageSelectionController initialized");
        System.out.println("   Email: " + pendingEmail);
        System.out.println("   Name: " + pendingName);
    }

    /**
     * Handle selection of the Free plan.
     * Updates the user's subscription to "free" and navigates to Profile.
     */
    @FXML
    private void handleFreePackage() {
        System.out.println("🆓 User selected Free plan");

        if (pendingEmail == null || pendingEmail.isEmpty()) {
            showError("Email not found. Please try signing up again.");
            return;
        }

        try {
            // Get the newly registered user by email
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByEmail(pendingEmail);

            if (user == null) {
                showError("User not found. Please try signing up again.");
                return;
            }

            // Update subscription to "free"
            boolean updated = userDAO.updateSubscription(user.getId(), "free");
            if (!updated) {
                showError("Failed to update subscription. Please try again.");
                return;
            }

            // Refresh user data and set in session
            user = userDAO.findByEmail(pendingEmail);
            Session.setCurrentUser(user);

            System.out.println("✅ User " + user.getNomComplet() + " subscribed to Free plan");

            // Clear the static fields
            pendingEmail = null;
            pendingName = null;

            // Navigate to Profile
            Stage stage = (Stage) freeCard.getScene().getWindow();
            NavigationManager.navigateTo(stage, AppConfig.VIEW_PROFILE, "Profile");

        } catch (Exception e) {
            System.err.println("❌ Error in handleFreePackage: " + e.getMessage());
            e.printStackTrace();
            showError("An error occurred. Please try again.");
        }
    }

    /**
     * Handle selection of the Premium plan.
     * Stores payment info in PaymentSession and navigates to Payment page.
     */
    @FXML
    private void handlePremiumPackage() {
        System.out.println("👑 User selected Premium plan");

        if (pendingEmail == null || pendingEmail.isEmpty()) {
            showError("Email not found. Please try signing up again.");
            return;
        }

        try {
            // Store the email and package in PaymentSession for the Payment controller
            PaymentSession.store(pendingEmail, "pro");

            System.out.println("✅ Pro package stored in PaymentSession for: " + pendingEmail);

            // Navigate to Payment page
            Stage stage = (Stage) premiumCard.getScene().getWindow();
            NavigationManager.navigateTo(stage, AppConfig.VIEW_PAYMENT, "Payment");

        } catch (Exception e) {
            System.err.println("❌ Error in handlePremiumPackage: " + e.getMessage());
            e.printStackTrace();
            showError("An error occurred. Please try again.");
        }
    }

    /**
     * Display an error message to the user.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText("❌ " + message);
                statusLabel.setStyle("-fx-text-fill: #e53935; -fx-font-size: 13px;");
                statusLabel.setVisible(true);
                statusLabel.setManaged(true);
            }
            System.err.println("❌ Error: " + message);
        });
    }
}
