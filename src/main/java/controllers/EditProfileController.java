package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import dao.UserDAO;
import utils.Session;
import utils.BioGeneratorService;

/**
 * Controller for the Edit Profile view.
 * Handles user profile editing with validation and database persistence.
 */
public class EditProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField roleField;

    @FXML
    private ComboBox<String> niveauCombo;

    @FXML
    private TextField ageField;

    @FXML
    private TextField educationField;

    @FXML
    private TextField formationField;

    @FXML
    private TextField experienceField;

    @FXML
    private TextArea bioArea;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    @FXML
    private Button generateBioButton;

    @FXML
    private Label bioGenerateStatus;

    /**
     * Initialize controller and load current user data
     */
    @FXML
    public void initialize() {
        // Populate ComboBox items first
        niveauCombo.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        niveauCombo.setValue("Beginner"); // Set default value
        
        // Then load user data
        loadCurrentUserData();
    }

    /**
     * Load current user data from session into the form
     */
    private void loadCurrentUserData() {
        User user = Session.getCurrentUser();
        if (user != null) {
            nameField.setText(user.getNomComplet() != null ? user.getNomComplet() : "");
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            roleField.setText(user.getRole() != null ? user.getRole() : "");
            niveauCombo.setValue(user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner");
            ageField.setText(user.getAge() > 0 ? String.valueOf(user.getAge()) : "");
            educationField.setText(user.getEducation() != null ? user.getEducation() : "");
            formationField.setText(user.getFormation() != null ? user.getFormation() : "");
            experienceField.setText(user.getExperience() != null ? user.getExperience() : "");
            bioArea.setText(user.getBio() != null ? user.getBio() : "");
        }
    }

    /**
     * Handle Save Profile button click with input validation
     */
    @FXML
    private void handleSaveProfile() {
        // Hide previous messages
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);

        // Get input values
        String name = nameField.getText().trim();
        String role = roleField.getText().trim();
        String niveau = niveauCombo.getValue();
        String age = ageField.getText().trim();
        String education = educationField.getText().trim();
        String formation = formationField.getText().trim();
        String experience = experienceField.getText().trim();
        String bio = bioArea.getText().trim();

        // Validation
        if (name.isEmpty()) {
            showError("Full name is required");
            return;
        }

        if (name.length() < 3) {
            showError("Full name must be at least 3 characters");
            return;
        }

        if (name.length() > 100) {
            showError("Full name cannot exceed 100 characters");
            return;
        }

        // Validate age if provided
        int ageValue = 0;
        if (!age.isEmpty()) {
            try {
                ageValue = Integer.parseInt(age);
                if (ageValue < 13 || ageValue > 120) {
                    showError("Age must be between 13 and 120");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Age must be a valid number");
                return;
            }
        }

        // Validate bio length
        if (bio.length() > 1000) {
            showError("Bio cannot exceed 1000 characters");
            return;
        }

        // Validate other fields don't exceed reasonable length
        if (role.length() > 100) {
            showError("Role cannot exceed 100 characters");
            return;
        }

        if (education.length() > 200) {
            showError("Education cannot exceed 200 characters");
            return;
        }

        if (formation.length() > 200) {
            showError("Formation cannot exceed 200 characters");
            return;
        }

        if (experience.length() > 500) {
            showError("Experience cannot exceed 500 characters");
            return;
        }

        // All validations passed, update database
        try {
            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                System.out.println("🔐 Current user ID: " + currentUser.getId());
                System.out.println("📝 Attempting to update with: " + name + " (ID: " + currentUser.getId() + ")");
                
                // Update user object
                currentUser.setNomComplet(name);
                currentUser.setRole(role.isEmpty() ? null : role);
                currentUser.setNiveauInfo(niveau);
                currentUser.setAge(ageValue);
                currentUser.setEducation(education.isEmpty() ? null : education);
                currentUser.setFormation(formation.isEmpty() ? null : formation);
                currentUser.setExperience(experience.isEmpty() ? null : experience);
                currentUser.setBio(bio.isEmpty() ? null : bio);

                // Update in database
                UserDAO dao = new UserDAO();
                if (dao.updateProfile(currentUser)) {
                    Session.setCurrentUser(currentUser); // Update session
                    showSuccess("✓ Profile updated successfully!");
                    
                    // Close window after short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(this::closeWindow);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                } else {
                    showError("Failed to update profile in database");
                }
            } else {
                System.err.println("❌ No current user in session!");
                showError("No user logged in - cannot save profile");
            }
        } catch (Exception e) {
            showError("Error saving profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Generate Bio button click
     * Calls Mistral AI to generate a professional bio based on current form values
     * Runs asynchronously to avoid blocking the UI
     */
    @FXML
    private void handleGenerateBio() {
        System.out.println("🤖 Generate Bio button clicked");
        
        // Get current user from session (contains xp and other saved data)
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showBioStatusError("No user logged in");
            return;
        }
        
        // Create a temporary User object with current form values
        // This ensures the AI uses the most up-to-date info even before saving
        User tempUser = new User();
        tempUser.setId(currentUser.getId());
        tempUser.setNomComplet(nameField.getText().trim());
        tempUser.setRole(roleField.getText().trim());
        tempUser.setNiveauInfo(niveauCombo.getValue());
        
        // Parse age if provided
        int ageValue = 0;
        try {
            if (!ageField.getText().trim().isEmpty()) {
                ageValue = Integer.parseInt(ageField.getText().trim());
            }
        } catch (NumberFormatException e) {
            // Age parse error, use 0
        }
        tempUser.setAge(ageValue);
        
        tempUser.setEducation(educationField.getText().trim());
        tempUser.setFormation(formationField.getText().trim());
        tempUser.setExperience(experienceField.getText().trim());
        tempUser.setXp(currentUser.getXp()); // Keep XP from session
        
        // Disable button and show loading state
        generateBioButton.setDisable(true);
        generateBioButton.setText("⏳ Generating...");
        
        // Show status message
        bioGenerateStatus.setText("🤖 Mistral is writing your bio...");
        bioGenerateStatus.setStyle("-fx-font-size: 11; -fx-font-style: italic; -fx-text-fill: #999;");
        bioGenerateStatus.setVisible(true);
        bioGenerateStatus.setManaged(true);
        
        System.out.println("📤 Calling BioGeneratorService...");
        
        // Call the bio generator service (returns CompletableFuture)
        BioGeneratorService.generateBio(tempUser)
            .thenAccept(generatedBio -> {
                // Success callback — update UI on JavaFX thread
                Platform.runLater(() -> {
                    System.out.println("✅ Bio generated successfully");
                    
                    // Set the generated bio into the text area
                    bioArea.setText(generatedBio);
                    
                    // Re-enable button and restore text
                    generateBioButton.setDisable(false);
                    generateBioButton.setText("✨ Generate with AI");
                    
                    // Show success message
                    bioGenerateStatus.setText("✅ Bio generated! You can edit it before saving.");
                    bioGenerateStatus.setStyle("-fx-font-size: 11; -fx-font-style: italic; -fx-text-fill: #4CAF50;");
                    
                    // Auto-hide status message after 4 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(4000);
                            Platform.runLater(() -> {
                                bioGenerateStatus.setVisible(false);
                                bioGenerateStatus.setManaged(false);
                            });
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                });
            })
            .exceptionally(exception -> {
                // Error callback — update UI on JavaFX thread with error message
                Platform.runLater(() -> {
                    System.err.println("❌ Error generating bio: " + exception.getMessage());
                    
                    // Re-enable button and restore text
                    generateBioButton.setDisable(false);
                    generateBioButton.setText("✨ Generate with AI");
                    
                    // Show error message
                    String errorMsg = exception.getMessage() != null 
                        ? exception.getMessage() 
                        : "Failed to generate bio. Please try again.";
                    bioGenerateStatus.setText("❌ " + errorMsg);
                    bioGenerateStatus.setStyle("-fx-font-size: 11; -fx-font-style: italic; -fx-text-fill: #e53935;");
                });
                
                return null;
            });
    }

    /**
     * Display error message in bio generation status label
     */
    private void showBioStatusError(String message) {
        bioGenerateStatus.setText("❌ " + message);
        bioGenerateStatus.setStyle("-fx-font-size: 11; -fx-font-style: italic; -fx-text-fill: #e53935;");
        bioGenerateStatus.setVisible(true);
        bioGenerateStatus.setManaged(true);
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Close the edit profile window
     */
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
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
     * Display success message
     */
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
    }
}
