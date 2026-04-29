package controllers;

import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import utils.NavigationManager;
import utils.AppConfig;
import models.User;
import dao.UserDAO;
import utils.Session;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the Users view (Arena - Users Community).
 */
public class UsersController {

    @FXML
    private HBox navbar;

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private TextField searchField;

    @FXML
    private Label userCountLabel;
    
    @FXML
    private ComboBox<String> sortComboBox;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private Button resetButton;

    private ObservableList<User> observableUsers;
    private FilteredList<User> filteredUsers;
    private SortedList<User> sortedUsers;
    private List<User> allUsers;

    @FXML
    public void initialize() {
        // Initialize Search Field with live updates
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());
        }
        
        // Initialize Sort ComboBox with items and default selection
        if (sortComboBox != null) {
            sortComboBox.setItems(FXCollections.observableArrayList(
                "Name (A → Z)",
                "Name (Z → A)",
                "XP (High → Low)",
                "XP (Low → High)"
            ));
            sortComboBox.setValue("Name (A → Z)");
            sortComboBox.setOnAction(event -> applyFiltersAndSort());
        }
        
        // Initialize Filter ComboBox with items and default selection
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList(
                "All Levels",
                "Beginner",
                "Advanced",
                "Expert"
            ));
            filterComboBox.setValue("All Levels");
            filterComboBox.setOnAction(event -> applyFiltersAndSort());
        }
        
        // Initialize Reset Button
        if (resetButton != null) {
            resetButton.setOnAction(event -> handleReset());
        }
        
        // Load all users from database and display them
        loadAllUsers();
    }

    /**
     * Load all users from database and setup filtering/sorting
     */
    private void loadAllUsers() {
        UserDAO dao = new UserDAO();
        allUsers = dao.getAllUsers();

        // Create observable list for filtering/sorting
        observableUsers = FXCollections.observableArrayList(allUsers);
        
        // Wrap in FilteredList for filtering
        filteredUsers = new FilteredList<>(observableUsers);
        
        // Wrap in SortedList for sorting
        sortedUsers = new SortedList<>(filteredUsers);
        
        // Set initial sorting comparator
        applySort("Name (A → Z)");
        
        // Apply initial filters
        applyFiltersAndSort();
        
        displayUsers(sortedUsers);
        System.out.println("✅ Loaded " + allUsers.size() + " users from database");
    }

    /**
     * Apply filters and sorting based on current selections
     */
    private void applyFiltersAndSort() {
        // Apply search and level filter
        filteredUsers.setPredicate(user -> {
            // Get search query
            String searchQuery = searchField.getText().toLowerCase().trim();
            
            // Get selected level filter
            String selectedLevel = filterComboBox.getValue();
            String userLevel = user.getNiveauInfo() != null ? user.getNiveauInfo().trim() : "";
            
            // Check search filter
            boolean matchesSearch = searchQuery.isEmpty() ||
                    user.getNomComplet().toLowerCase().contains(searchQuery) ||
                    user.getEmail().toLowerCase().contains(searchQuery) ||
                    (user.getRole() != null && user.getRole().toLowerCase().contains(searchQuery)) ||
                    (user.getNiveauInfo() != null && user.getNiveauInfo().toLowerCase().contains(searchQuery));
            
            // Check level filter (case-insensitive comparison)
            boolean matchesLevel = selectedLevel.equals("All Levels") ||
                    userLevel.equalsIgnoreCase(selectedLevel);
            
            // Debug logging for level filter issues
            if (!selectedLevel.equals("All Levels") && !userLevel.isEmpty()) {
                System.out.println("🔍 Comparing: DB='" + userLevel + "' vs Filter='" + selectedLevel + "' -> " + matchesLevel);
            }
            
            return matchesSearch && matchesLevel;
        });
        
        // Apply sorting
        String sortOption = sortComboBox.getValue();
        applySort(sortOption);
        
        // Refresh the display with filtered and sorted users
        displayUsers(sortedUsers);
    }

    /**
     * Apply sorting based on selected option
     */
    private void applySort(String sortOption) {
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Name (A → Z)":
                sortedUsers.setComparator((u1, u2) -> 
                    u1.getNomComplet().compareToIgnoreCase(u2.getNomComplet()));
                break;
            case "Name (Z → A)":
                sortedUsers.setComparator((u1, u2) -> 
                    u2.getNomComplet().compareToIgnoreCase(u1.getNomComplet()));
                break;
            case "XP (High → Low)":
                sortedUsers.setComparator((u1, u2) -> 
                    Integer.compare(u2.getXp(), u1.getXp()));
                break;
            case "XP (Low → High)":
                sortedUsers.setComparator((u1, u2) -> 
                    Integer.compare(u1.getXp(), u2.getXp()));
                break;
            default:
                sortedUsers.setComparator((u1, u2) -> 
                    u1.getNomComplet().compareToIgnoreCase(u2.getNomComplet()));
        }
    }

    /**
     * Display users in the FlowPane
     */
    private void displayUsers(List<User> users) {
        usersFlowPane.getChildren().clear();
        
        if (users.isEmpty()) {
            Label noUsers = new Label("No users found");
            noUsers.setStyle("-fx-text-fill: #999; -fx-font-size: 14;");
            usersFlowPane.getChildren().add(noUsers);
        } else {
            for (User u : users) {
                VBox card = createUserCard(u);
                usersFlowPane.getChildren().add(card);
            }
        }
        
        userCountLabel.setText(users.size() + " user" + (users.size() != 1 ? "s" : "") + " found");
    }

    /**
     * Handle search field changes for live filtering
     */
    @FXML
    private void handleSearch() {
        applyFiltersAndSort();
    }
    
    /**
     * Reset all filters and sorting to defaults
     */
    @FXML
    private void handleReset() {
        searchField.clear();
        sortComboBox.setValue("Name (A → Z)");
        filterComboBox.setValue("All Levels");
    }

    /**
     * Create a visual card for each user matching the FXML styling
     */
    private VBox createUserCard(User user) {
        VBox card = new VBox(12);
        card.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: white; -fx-padding: 20; -fx-alignment: CENTER; -fx-min-width: 280; -fx-max-width: 280;");
        
        // Avatar emoji
        Label avatar = new Label("👨‍💻");
        avatar.setStyle("-fx-font-size: 48;");
        
        // Name
        Label nameLabel = new Label(user.getNomComplet());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Role/Level
        Label roleLabel = new Label(user.getRole() != null ? user.getRole() : "Member");
        roleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #FF6B4A; -fx-font-weight: bold;");
        
        // Skills/Info
        String skills = user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner";
        Label skillsLabel = new Label(skills);
        skillsLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        // XP/Stats
        Label statsLabel = new Label("⭐ " + user.getXp() + " XP");
        statsLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #FFB800; -fx-text-alignment: center;");
        
        // View Profile Button
        Button viewButton = new Button("View Profile");
        viewButton.setStyle("-fx-padding: 10 20 10 20; -fx-font-size: 12;");
        viewButton.setOnAction(e -> viewUserProfile(user));
        
        card.getChildren().addAll(avatar, nameLabel, roleLabel, skillsLabel, statsLabel, viewButton);
        return card;
    }

    /**
     * View full profile of selected user
     */
    private void viewUserProfile(User user) {
        System.out.println("Viewing profile of: " + user.getNomComplet());
        // Could implement profile modal or navigation here
    }

    // Navbar navigation handlers
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
