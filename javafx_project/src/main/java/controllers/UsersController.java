package controllers;

import dao.UserDAO;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.User;
import utils.AppConfig;
import utils.NavigationManager;
import utils.SessionManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the Users/Arena view - displays all users in leaderboard style.
 * All data is loaded dynamically from the database, sorted by XP descending.
 */
public class UsersController {

    @FXML
    private HBox navbar;
    
    @FXML
    private FlowPane usersContainerPane;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> niveauFilter;
    
    private List<User> allUsers;
    private Integer currentUserId;

    @FXML
    public void initialize() {
        currentUserId = SessionManager.getCurrentUserId();
        setupSearchAndFilter();
        loadAllUsers();
    }

    /**
     * Setup search and filter functionality
     */
    private void setupSearchAndFilter() {
        // Initialize ComboBox items
        if (niveauFilter != null) {
            niveauFilter.getItems().addAll("All Levels", "Beginner", "Intermediate", "Advanced");
            niveauFilter.setValue("All Levels");
        }
        
        // Add listeners for real-time filtering
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAndDisplayUsers());
        }
        
        if (niveauFilter != null) {
            niveauFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterAndDisplayUsers());
        }
    }

    /**
     * Load all users from database and display them
     */
    private void loadAllUsers() {
        try {
            System.out.println("INFO: Loading all users for Arena page...");
            
            // Fetch all users from DB (already sorted by XP DESC in UserDAO)
            allUsers = UserDAO.getAllUsers();
            
            if (allUsers == null || allUsers.isEmpty()) {
                System.out.println("WARNING: No users found in database");
                showNoUsersMessage();
                return;
            }
            
            System.out.println("SUCCESS: Loaded " + allUsers.size() + " users from database");
            filterAndDisplayUsers();
            
        } catch (Exception e) {
            System.err.println("ERROR loading users: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Failed to load users from database");
        }
    }

    /**
     * Filter users based on search and niveau filter, then display them
     */
    private void filterAndDisplayUsers() {
        if (allUsers == null || allUsers.isEmpty()) {
            showNoUsersMessage();
            return;
        }
        
        // Get filter criteria
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
        String niveauFilterValue = niveauFilter != null ? niveauFilter.getValue() : "All Levels";
        
        // Filter users
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> 
                // Filter by search text (name or role)
                user.getNomComplet().toLowerCase().contains(searchText) ||
                (user.getRole() != null && user.getRole().toLowerCase().contains(searchText))
            )
            .filter(user ->
                // Filter by niveau
                niveauFilterValue.equals("All Levels") ||
                (user.getNiveauInfo() != null && user.getNiveauInfo().equalsIgnoreCase(niveauFilterValue))
            )
            .collect(Collectors.toList());
        
        System.out.println("INFO: Displaying " + filteredUsers.size() + " filtered users");
        
        if (filteredUsers.isEmpty()) {
            showNoUsersMessage();
            return;
        }
        
        // Clear container and add filtered cards
        usersContainerPane.getChildren().clear();
        for (User user : filteredUsers) {
            VBox userCard = createUserCard(user);
            usersContainerPane.getChildren().add(userCard);
        }
    }

    /**
     * Create a dynamic user card for display in the arena
     */
    private VBox createUserCard(User user) {
        VBox card = new VBox();
        card.setSpacing(12);
        card.setStyle("-fx-alignment: center; -fx-min-width: 280; -fx-max-width: 280; " +
                     "-fx-padding: 20; -fx-border-radius: 12; -fx-background-color: white; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 20, 0, 0, 4); " +
                     "-fx-border-color: " + (isCurrentUser(user) ? "#FF6B4A" : "#e0e0e0") + "; " +
                     "-fx-border-width: " + (isCurrentUser(user) ? "3" : "1") + ";");
        
        // Avatar emoji or placeholder
        Label avatarLabel = new Label("👤");
        avatarLabel.setStyle("-fx-font-size: 48;");
        card.getChildren().add(avatarLabel);
        
        // User name
        Label nameLabel = new Label(user.getNomComplet() != null ? user.getNomComplet() : "Unknown");
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        card.getChildren().add(nameLabel);
        
        // Current user badge
        if (isCurrentUser(user)) {
            Label currentBadge = new Label("⭐ YOU");
            currentBadge.setStyle("-fx-font-size: 12; -fx-text-fill: #FF6B4A; -fx-font-weight: bold;");
            card.getChildren().add(currentBadge);
        }
        
        // Level badge - color coded
        String niveauInfo = user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner";
        String levelColor = getLevelColor(niveauInfo);
        Label levelLabel = new Label("🎓 " + niveauInfo);
        levelLabel.setStyle("-fx-font-size: 12; -fx-text-fill: " + levelColor + "; -fx-font-weight: bold;");
        card.getChildren().add(levelLabel);
        
        // Role/Formation
        String role = user.getRole() != null ? user.getRole() : "Student";
        Label roleLabel = new Label(role);
        roleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        card.getChildren().add(roleLabel);
        
        // XP and progress
        int xp = user.getXp();
        Label xpLabel = new Label("⚡ " + xp + " XP");
        xpLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #FFB800; -fx-font-weight: bold;");
        card.getChildren().add(xpLabel);
        
        // Formation info if available
        if (user.getFormation() != null && !user.getFormation().isEmpty()) {
            Label formationLabel = new Label(user.getFormation());
            formationLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999; -fx-wrap-text: true; -fx-text-alignment: center;");
            card.getChildren().add(formationLabel);
        }
        
        // View Profile button
        Button viewProfileBtn = new Button("View Profile");
        viewProfileBtn.setStyle("-fx-padding: 10 20 10 20; -fx-font-size: 12; " +
                               "-fx-background-color: " + (isCurrentUser(user) ? "#FF6B4A" : "#4CAF50") + "; " +
                               "-fx-text-fill: white; -fx-font-weight: 600; -fx-border-radius: 6;");
        viewProfileBtn.setOnAction(e -> viewUserProfile(user));
        card.getChildren().add(viewProfileBtn);
        
        return card;
    }

    /**
     * Check if a user is the currently logged-in user
     */
    private boolean isCurrentUser(User user) {
        return currentUserId != null && currentUserId == user.getId();
    }

    /**
     * Get color for level badge based on user's niveau
     */
    private String getLevelColor(String niveau) {
        if (niveau == null) return "#999";
        switch (niveau.toLowerCase()) {
            case "beginner": return "#4CAF50";      // Green
            case "intermediate": return "#2196F3";  // Blue
            case "advanced": return "#FF9800";      // Orange
            default: return "#999";
        }
    }

    /**
     * Handle view profile action for a user
     */
    private void viewUserProfile(User user) {
        System.out.println("INFO: Viewing profile for user: " + user.getNomComplet());
        // Could navigate to user's profile or show details modal
        showAlert("Profile", "View profile for: " + user.getNomComplet() + "\nXP: " + user.getXp());
    }

    /**
     * Show "No users found" message
     */
    private void showNoUsersMessage() {
        usersContainerPane.getChildren().clear();
        VBox emptyMessage = new VBox(20);
        emptyMessage.setStyle("-fx-alignment: center; -fx-padding: 100;");
        
        Label msgLabel = new Label("❌ No users found");
        msgLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #999;");
        
        Label subLabel = new Label("Try adjusting your search or filter criteria");
        subLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #ccc;");
        
        emptyMessage.getChildren().addAll(msgLabel, subLabel);
        usersContainerPane.getChildren().add(emptyMessage);
    }

    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        usersContainerPane.getChildren().clear();
        VBox errorMessage = new VBox(20);
        errorMessage.setStyle("-fx-alignment: center; -fx-padding: 100;");
        
        Label msgLabel = new Label("⚠️ Error");
        msgLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #FF6B4A;");
        
        Label subLabel = new Label(message);
        subLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        errorMessage.getChildren().addAll(msgLabel, subLabel);
        usersContainerPane.getChildren().add(errorMessage);
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== NAVIGATION HANDLERS =====

    @FXML
    private void handleNavHome() {
        navigateToView(AppConfig.VIEW_HOME, "Home");
    }

    @FXML
    private void handleNavCourses() {
        navigateToView(AppConfig.VIEW_COURSES, "Courses");
    }

    @FXML
    private void handleNavProblems() {
        navigateToView(AppConfig.VIEW_PROBLEMS, "Problems");
    }

    @FXML
    private void handleNavProjects() {
        navigateToView(AppConfig.VIEW_PROJECTS, "Projects");
    }

    @FXML
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS, "Events");
    }

    @FXML
    private void handleNavForum() {
        navigateToView(AppConfig.VIEW_FORUM, "Forum");
    }

    @FXML
    private void handleNavUsers() {
        navigateToView(AppConfig.VIEW_USERS, "Users");
    }

    @FXML
    private void handleNavProfile() {
        navigateToView(AppConfig.VIEW_PROFILE, "Profile");
    }

    @FXML
    private void handleLogOut() {
        SessionManager.logout();
        navigateToView(AppConfig.VIEW_SIGNIN, "Sign In");
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) navbar.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
