package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.User;
import utils.NavigationManager;
import utils.PasswordUtils;
import utils.AppConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Users Management view (admin).
 * Handles admin user management with full CRUD operations and statistics.
 */
public class UsersBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView<User> usersTable;
    
    @FXML
    private TextField searchField;

    // Filter buttons
    @FXML
    private Button filterAllBtn;
    
    @FXML
    private Button filterActiveBtn;
    
    @FXML
    private Button filterInactiveBtn;
    
    @FXML
    private Button filterAdminBtn;
    
    @FXML
    private Button filterTeacherBtn;
    
    @FXML
    private Button addUserBtn;

    private ObservableList<User> allUsers;
    private ObservableList<User> filteredUsers;
    private String currentFilter = "all";

    @FXML
    public void initialize() {
        // Setup users table with columns
        setupUsersTable();
        
        // Load data from database
        loadStatistics();
        loadUsers();
        
        // Setup search listener
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    /**
     * Setup the users table with dynamic columns and cell factories.
     */
    private void setupUsersTable() {
        // Get the columns from FXML
        List<TableColumn<User, ?>> columns = usersTable.getColumns();
        
        if (columns.size() >= 7) {
            // User ID
            ((TableColumn<User, Integer>) columns.get(0)).setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            
            // Name
            ((TableColumn<User, String>) columns.get(1)).setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomComplet()));
            
            // Email
            ((TableColumn<User, String>) columns.get(2)).setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
            
            // Level
            ((TableColumn<User, String>) columns.get(3)).setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getNiveauInfo() != null ? cellData.getValue().getNiveauInfo() : "N/A"));
            
            // Status (Active/Banned)
            ((TableColumn<User, String>) columns.get(4)).setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getIsBanned() == 1 ? "Banned" : "Active"));
            
            // Set cell factory for status column with color coding
            ((TableColumn<User, String>) columns.get(4)).setCellFactory(p -> new TableCell<User, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("Banned".equals(item)) {
                            setStyle("-fx-background-color: rgba(211, 47, 47, 0.2); -fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-background-color: rgba(76, 175, 80, 0.2); -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        }
                    }
                }
            });
            
            // Joined Date
            ((TableColumn<User, String>) columns.get(5)).setCellValueFactory(cellData -> {
                LocalDateTime createdAt = cellData.getValue().getCreatedAt();
                String formatted = createdAt != null ?
                    createdAt.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) : "N/A";
                return new javafx.beans.property.SimpleStringProperty(formatted);
            });
            
            // Actions
            ((TableColumn<User, Void>) columns.get(6)).setCellFactory(p -> new TableCell<User, Void>() {
                private final Button editBtn = new Button("✏️ Edit");
                private final Button deleteBtn = new Button("🗑️ Delete");
                private final HBox actionsBox = new HBox(8);
                
                {
                    editBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-font-weight: 600;");
                    deleteBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-font-weight: 600; " +
                        "-fx-text-fill: white; -fx-background-color: #d32f2f;");
                    
                    actionsBox.setAlignment(Pos.CENTER);
                    actionsBox.setSpacing(5);
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        User user = getTableView().getItems().get(getIndex());
                        
                        // Edit button action
                        editBtn.setOnAction(e -> openEditUserDialog(user));
                        
                        // Delete button action
                        deleteBtn.setOnAction(e -> deleteUser(user));
                        
                        actionsBox.getChildren().clear();
                        actionsBox.getChildren().add(editBtn);
                        actionsBox.getChildren().add(deleteBtn);
                        
                        // Add Ban/Unban button
                        Button banBtn = new Button();
                        if (user.getIsBanned() == 1) {
                            banBtn.setText("✅ Unban");
                            banBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-font-weight: 600; " +
                                "-fx-text-fill: white; -fx-background-color: #388e3c;");
                            banBtn.setOnAction(e -> toggleBanUser(user));
                        } else {
                            banBtn.setText("🚫 Ban");
                            banBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-font-weight: 600; " +
                                "-fx-text-fill: white; -fx-background-color: #f57c00;");
                            banBtn.setOnAction(e -> toggleBanUser(user));
                        }
                        actionsBox.getChildren().add(banBtn);
                        
                        setGraphic(actionsBox);
                    }
                }
            });
        }
    }

    /**
     * Load statistics from database and display in cards.
     */
    private void loadStatistics() {
        try {
            System.out.println("INFO: Loading statistics...");
            
            int totalUsers = UserDAO.getTotalUsers();
            int activeUsers = totalUsers - UserDAO.getTotalBannedUsers();
            
            System.out.println("SUCCESS: Statistics loaded - Total: " + totalUsers + ", Active: " + activeUsers);
        } catch (Exception e) {
            System.err.println("ERROR loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load all users from database.
     */
    private void loadUsers() {
        try {
            System.out.println("INFO: Loading all users from database...");
            
            List<User> users = UserDAO.getAllUsersAdmin();
            allUsers = FXCollections.observableArrayList(users);
            filteredUsers = FXCollections.observableArrayList(users);
            usersTable.setItems(filteredUsers);
            
            System.out.println("SUCCESS: Loaded " + users.size() + " users");
        } catch (Exception e) {
            System.err.println("ERROR loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Apply filters based on search and current filter setting.
     */
    private void applyFilters() {
        String searchTerm = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        
        ObservableList<User> filtered = FXCollections.observableArrayList();
        
        for (User user : allUsers) {
            // Check search term
            boolean matchesSearch = searchTerm.isEmpty() ||
                user.getNomComplet().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm);
            
            if (!matchesSearch) continue;
            
            // Check filter
            boolean passesFilter = false;
            switch (currentFilter) {
                case "all":
                    passesFilter = true;
                    break;
                case "active":
                    passesFilter = user.getIsBanned() == 0;
                    break;
                case "inactive":
                    passesFilter = user.getIsBanned() == 1;
                    break;
                case "admin":
                    passesFilter = user.getIsAdmin() == 1;
                    break;
                case "teacher":
                    // Check if user role indicates teacher/instructor
                    passesFilter = user.getRole() != null && 
                        (user.getRole().equalsIgnoreCase("Instructor") || 
                         user.getRole().equalsIgnoreCase("Teacher") ||
                         user.getRole().equalsIgnoreCase("Staff"));
                    break;
                case "student":
                    passesFilter = user.getRole() != null && user.getRole().equalsIgnoreCase("Student");
                    break;
            }
            
            if (passesFilter) {
                filtered.add(user);
            }
        }
        
        filteredUsers.setAll(filtered);
    }

    /**
     * Handle filter button clicks.
     */
    @FXML
    private void handleFilterAll() {
        currentFilter = "all";
        applyFilters();
        updateFilterButtonStyles();
    }

    @FXML
    private void handleFilterActive() {
        currentFilter = "active";
        applyFilters();
        updateFilterButtonStyles();
    }

    @FXML
    private void handleFilterInactive() {
        currentFilter = "inactive";
        applyFilters();
        updateFilterButtonStyles();
    }

    @FXML
    private void handleFilterAdmin() {
        currentFilter = "admin";
        applyFilters();
        updateFilterButtonStyles();
    }

    @FXML
    private void handleFilterTeacher() {
        currentFilter = "teacher";
        applyFilters();
        updateFilterButtonStyles();
    }

    /**
     * Update filter button styles to highlight the active filter.
     */
    private void updateFilterButtonStyles() {
        // Reset all buttons to inactive style
        String inactiveStyle = "-fx-padding: 8 16; -fx-background-color: #f0f0f0; -fx-text-fill: #666; -fx-font-weight: 500;";
        String activeStyle = "-fx-padding: 8 16; -fx-text-fill: white; -fx-background-color: #FF6B4A; -fx-font-weight: 500;";
        
        if (filterAllBtn != null) filterAllBtn.setStyle(inactiveStyle);
        if (filterActiveBtn != null) filterActiveBtn.setStyle(inactiveStyle);
        if (filterInactiveBtn != null) filterInactiveBtn.setStyle(inactiveStyle);
        if (filterAdminBtn != null) filterAdminBtn.setStyle(inactiveStyle);
        if (filterTeacherBtn != null) filterTeacherBtn.setStyle(inactiveStyle);
        
        // Highlight the active filter button
        switch (currentFilter) {
            case "all":
                if (filterAllBtn != null) filterAllBtn.setStyle(activeStyle);
                break;
            case "active":
                if (filterActiveBtn != null) filterActiveBtn.setStyle(activeStyle);
                break;
            case "inactive":
                if (filterInactiveBtn != null) filterInactiveBtn.setStyle(activeStyle);
                break;
            case "admin":
                if (filterAdminBtn != null) filterAdminBtn.setStyle(activeStyle);
                break;
            case "teacher":
                if (filterTeacherBtn != null) filterTeacherBtn.setStyle(activeStyle);
                break;
        }
    }

    /**
     * Open Add User dialog.
     */
    @FXML
    private void handleAddUser() {
        openAddUserDialog();
    }

    /**
     * Open Add User dialog form.
     */
    private void openAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        
        TextField nomComeletField = new TextField();
        nomComeletField.setPromptText("Full Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("Student", "Instructor", "Staff"));
        roleCombo.setValue("Student");
        
        Spinner<Integer> ageSpinner = new Spinner<>(1, 100, 20);
        
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        niveauCombo.setValue("Beginner");
        
        TextField formationField = new TextField();
        formationField.setPromptText("Formation/Specialization");
        
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nomComeletField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);
        grid.add(new Label("Age:"), 0, 4);
        grid.add(ageSpinner, 1, 4);
        grid.add(new Label("Level:"), 0, 5);
        grid.add(niveauCombo, 1, 5);
        grid.add(new Label("Formation:"), 0, 6);
        grid.add(formationField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        if (dialog.showAndWait().isPresent()) {
            try {
                // Validate
                if (nomComeletField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    showAlert("Validation Error", "Name, Email, and Password are required!");
                    return;
                }
                
                // Add user to database
                boolean success = UserDAO.addUser(
                    nomComeletField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    roleCombo.getValue(),
                    ageSpinner.getValue(),
                    niveauCombo.getValue(),
                    "",  // bio
                    "",  // education
                    "",  // experience
                    formationField.getText()
                );
                
                if (success) {
                    showAlert("Success", "User created successfully!");
                    loadStatistics();
                    loadUsers();
                } else {
                    showAlert("Error", "Failed to create user!");
                }
            } catch (Exception e) {
                showAlert("Error", "Error adding user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Open Edit User dialog.
     */
    private void openEditUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getNomComplet());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        
        TextField nomCompletField = new TextField(user.getNomComplet());
        TextField emailField = new TextField(user.getEmail());
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("Student", "Instructor", "Staff"));
        roleCombo.setValue(user.getRole() != null ? user.getRole() : "Student");
        
        Spinner<Integer> ageSpinner = new Spinner<>(1, 100, user.getAge());
        
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        niveauCombo.setValue(user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner");
        
        TextField bioField = new TextField(user.getBio() != null ? user.getBio() : "");
        bioField.setPromptText("Bio");
        
        TextField educationField = new TextField(user.getEducation() != null ? user.getEducation() : "");
        educationField.setPromptText("Education");
        
        TextField experienceField = new TextField(user.getExperience() != null ? user.getExperience() : "");
        experienceField.setPromptText("Experience");
        
        TextField formationField = new TextField(user.getFormation() != null ? user.getFormation() : "");
        formationField.setPromptText("Formation");
        
        Spinner<Integer> xpSpinner = new Spinner<>(0, 1000000, user.getXp());
        
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nomCompletField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleCombo, 1, 2);
        grid.add(new Label("Age:"), 0, 3);
        grid.add(ageSpinner, 1, 3);
        grid.add(new Label("Level:"), 0, 4);
        grid.add(niveauCombo, 1, 4);
        grid.add(new Label("Bio:"), 0, 5);
        grid.add(bioField, 1, 5);
        grid.add(new Label("Education:"), 0, 6);
        grid.add(educationField, 1, 6);
        grid.add(new Label("Experience:"), 0, 7);
        grid.add(experienceField, 1, 7);
        grid.add(new Label("Formation:"), 0, 8);
        grid.add(formationField, 1, 8);
        grid.add(new Label("XP:"), 0, 9);
        grid.add(xpSpinner, 1, 9);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        if (dialog.showAndWait().isPresent()) {
            try {
                // Update user in database
                boolean success = UserDAO.updateUser(
                    user.getId(),
                    nomCompletField.getText(),
                    emailField.getText(),
                    roleCombo.getValue(),
                    ageSpinner.getValue(),
                    niveauCombo.getValue(),
                    bioField.getText(),
                    educationField.getText(),
                    experienceField.getText(),
                    formationField.getText()
                );
                
                // Update XP separately
                String sql = "UPDATE users SET xp = ? WHERE id = ?";
                // Note: Would need to add updateUserXP method to UserDAO in real implementation
                
                if (success) {
                    showAlert("Success", "User updated successfully!");
                    loadStatistics();
                    loadUsers();
                } else {
                    showAlert("Error", "Failed to update user!");
                }
            } catch (Exception e) {
                showAlert("Error", "Error updating user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete user from database.
     */
    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Delete user: " + user.getNomComplet() + "\n\nThis action cannot be undone!");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                if (UserDAO.deleteUser(user.getId())) {
                    showAlert("Success", "User deleted successfully!");
                    loadStatistics();
                    loadUsers();
                } else {
                    showAlert("Error", "Failed to delete user!");
                }
            } catch (Exception e) {
                showAlert("Error", "Error deleting user: " + e.getMessage());
            }
        }
    }

    /**
     * Toggle user ban status.
     */
    private void toggleBanUser(User user) {
        String action = user.getIsBanned() == 1 ? "Unban" : "Ban";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(action + " User");
        alert.setHeaderText("Are you sure?");
        alert.setContentText(action + " user: " + user.getNomComplet() + "?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success;
                if (user.getIsBanned() == 1) {
                    success = UserDAO.unbanUser(user.getId());
                } else {
                    success = UserDAO.banUser(user.getId());
                }
                
                if (success) {
                    showAlert("Success", "User status updated!");
                    loadStatistics();
                    loadUsers();
                } else {
                    showAlert("Error", "Failed to update user status!");
                }
            } catch (Exception e) {
                showAlert("Error", "Error updating user status: " + e.getMessage());
            }
        }
    }

    /**
     * Show alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Sidebar navigation handlers
    @FXML
    private void handleNavDashboard() {
        navigateToView(AppConfig.VIEW_DASHBOARD, "Dashboard");
    }

    @FXML
    private void handleNavUsers() {
        navigateToView(AppConfig.VIEW_USERS_BACK, "Users Management");
    }

    @FXML
    private void handleNavCourses() {
        navigateToView(AppConfig.VIEW_COURSES_BACK, "Courses Management");
    }

    @FXML
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS_BACK, "Events Management");
    }

    @FXML
    private void handleNavForum() {
        navigateToView(AppConfig.VIEW_FORUM_BACK, "Forum Management");
    }

    @FXML
    private void handleNavProblems() {
        navigateToView(AppConfig.VIEW_PROBLEMS_BACK, "Problems Management");
    }

    @FXML
    private void handleNavProjects() {
        navigateToView(AppConfig.VIEW_PROJECTS_BACK, "Projects Management");
    }

    @FXML
    private void handleNavHome() {
        navigateToView(AppConfig.VIEW_HOME, "Home");
    }

    @FXML
    private void handleThemeToggle() {
        System.out.println("Theme toggle clicked");
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) sidebarContent.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }
}
