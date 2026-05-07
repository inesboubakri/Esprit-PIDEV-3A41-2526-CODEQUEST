package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import models.User;
import dao.UserDAO;
import utils.NavigationManager;
import utils.AppConfig;
import utils.Session;
import java.util.List;
import java.util.Optional;
import javafx.scene.layout.GridPane;


/**
 * Controller for the Users Management Admin Panel with full CRUD operations
 */
public class UsersBackController {

    @FXML private VBox sidebarContent;
    @FXML private TableView<User> usersTable;
    @FXML private TextField searchField;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Button addNewUserBtn;
    @FXML private ComboBox<String> levelFilterCombo;
    @FXML private ComboBox<String> roleFilterCombo;

    private UserDAO userDAO = new UserDAO();
    private ObservableList<User> usersData = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;
    private SortedList<User> sortedData;

    @FXML
    public void initialize() {
        System.out.println("✅ UsersBackController initialized");
        
        // Check if user is admin
        User currentUser = Session.getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            System.err.println("❌ Access denied: Not an admin user");
            return;
        }
        
        setupTableView();
        loadUsersFromDatabase();
        setupFilters();
        setupSearchField();
        setupAddUserButton();
    }

    /**
     * Setup filter ComboBoxes (Level and Role)
     */
    private void setupFilters() {
        if (levelFilterCombo != null) {
            levelFilterCombo.setItems(FXCollections.observableArrayList(
                "All Levels", "Beginner", "Advanced", "Expert"
            ));
            levelFilterCombo.setValue("All Levels");
            levelFilterCombo.setOnAction(e -> applyAllFilters());
        }
        
        if (roleFilterCombo != null) {
            roleFilterCombo.setItems(FXCollections.observableArrayList(
                "All Roles", "Admin", "User"
            ));
            roleFilterCombo.setValue("All Roles");
            roleFilterCombo.setOnAction(e -> applyAllFilters());
        }
    }

    /**
     * Setup TableView columns with PropertyValueFactory binding
     */
    private void setupTableView() {
        if (usersTable == null) {
            System.err.println("❌ usersTable is null!");
            return;
        }

        try {
            // Get columns from TableView
            ObservableList<TableColumn<User, ?>> columns = usersTable.getColumns();
            
            TableColumn<User, Integer> idCol = (TableColumn<User, Integer>) columns.get(0);
            TableColumn<User, String> nameCol = (TableColumn<User, String>) columns.get(1);
            TableColumn<User, String> emailCol = (TableColumn<User, String>) columns.get(2);
            TableColumn<User, String> levelCol = (TableColumn<User, String>) columns.get(3);
            TableColumn<User, String> statusCol = (TableColumn<User, String>) columns.get(4);
            TableColumn<User, String> joinedCol = (TableColumn<User, String>) columns.get(5);
            TableColumn<User, Void> actionsCol = (TableColumn<User, Void>) columns.get(6);

            // Bind columns to User properties
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            levelCol.setCellValueFactory(new PropertyValueFactory<>("niveauInfo"));
            statusCol.setCellValueFactory(cellData -> {
                String status = cellData.getValue().isAdmin() ? "🔐 Admin" : "👤 User";
                return new javafx.beans.property.SimpleStringProperty(status);
            });
            joinedCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

            // Setup Actions column with Edit and Delete buttons
            setupActionsColumn(actionsCol);

            // Setup FilteredList and SortedList for efficient filtering and sorting
            filteredData = new FilteredList<>(usersData, p -> true);
            sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(usersTable.comparatorProperty());
            
            // Set sorted data to table
            usersTable.setItems(sortedData);
            usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            System.out.println("✅ TableView columns configured with sorting enabled");
        } catch (Exception e) {
            System.err.println("❌ Error setting up TableView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup Actions column with Edit and Delete buttons
     */
    private void setupActionsColumn(TableColumn<User, Void> actionsCol) {
        actionsCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox hBox = new HBox(8);

            {
                editBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 12; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                editBtn.setTooltip(new Tooltip("Edit User"));
                deleteBtn.setStyle("-fx-padding: 5 10; -fx-font-size: 12; -fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setTooltip(new Tooltip("Delete User"));

                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });
                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });

                hBox.getChildren().addAll(editBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        });
    }

    /**
     * Load all users from database
     */
    private void loadUsersFromDatabase() {
        try {
            List<User> users = userDAO.getAllUsers();
            usersData.clear();
            usersData.addAll(users);
            System.out.println("✅ Loaded " + users.size() + " users from database");
            updateStats();
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users from database");
        }
    }

    /**
     * Update statistics cards
     */
    private void updateStats() {
        if (totalUsersLabel != null) {
            totalUsersLabel.setText(String.valueOf(usersData.size()));
        }
        if (activeUsersLabel != null) {
            long activeCount = usersData.stream().filter(u -> !u.isAdmin()).count();
            activeUsersLabel.setText(String.valueOf(activeCount));
        }
    }

    /**
     * Setup live search in search field
     */
    private void setupSearchField() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyAllFilters());
        }
    }

    /**
     * Apply all filters (search, level, role) together
     */
    private void applyAllFilters() {
        if (filteredData == null) {
            return;
        }

        String searchTerm = searchField != null ? searchField.getText().toLowerCase().trim() : "";
        String levelFilter = levelFilterCombo != null ? levelFilterCombo.getValue() : "All Levels";
        String roleFilter = roleFilterCombo != null ? roleFilterCombo.getValue() : "All Roles";

        filteredData.setPredicate(user -> {
            // Apply search filter (name, email, or ID)
            boolean matchesSearch = searchTerm.isEmpty() ||
                user.getNomComplet().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm) ||
                String.valueOf(user.getId()).contains(searchTerm);

            // Apply level filter
            boolean matchesLevel = levelFilter.equals("All Levels") ||
                (user.getNiveauInfo() != null && user.getNiveauInfo().equalsIgnoreCase(levelFilter));

            // Apply role filter
            boolean matchesRole = roleFilter.equals("All Roles") ||
                (roleFilter.equals("Admin") && user.isAdmin()) ||
                (roleFilter.equals("User") && !user.isAdmin());

            return matchesSearch && matchesLevel && matchesRole;
        });

        System.out.println("🔍 Filters applied: Search='" + searchTerm + "', Level='" + levelFilter + "', Role='" + roleFilter + "'");
    }

    /**
     * Style dialog buttons with orange theme (#FF6B4A for OK, gray for CANCEL)
     */
    private void styleDialogButtons(Dialog<?> dialog, String actionName) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        
        if (okButton != null) {
            okButton.setText(actionName);
            okButton.setStyle("-fx-background-color: #FF6B4A; -fx-text-fill: white; -fx-font-weight: 600; -fx-font-size: 12; -fx-padding: 10 30; -fx-cursor: hand;");
        }
        
        if (cancelButton != null) {
            cancelButton.setText("Cancel");
            cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333; -fx-font-weight: 600; -fx-font-size: 12; -fx-padding: 10 30; -fx-cursor: hand;");
        }
    }

    /**
     * Setup Add User button action
     */
    private void setupAddUserButton() {
        if (addNewUserBtn != null) {
            addNewUserBtn.setOnAction(e -> handleAddUser());
        }
    }

    /**
     * Setup Add User button action
     */


    /**
     * Handle Add New User button action
     */
    @FXML
    private void handleAddUser() {
        System.out.println("📝 Add New User clicked");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account with complete information");

        // ===== CREATE INPUT FIELDS =====
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email address");
        emailField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (letters and numbers only)");
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField ageField = new TextField();
        ageField.setPromptText("Age (numbers only)");
        ageField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.setItems(FXCollections.observableArrayList("Beginner", "Advanced", "Expert"));
        levelCombo.setValue("Beginner");
        levelCombo.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextArea bioField = new TextArea();
        bioField.setPromptText("Bio (optional - max 500 characters)");
        bioField.setWrapText(true);
        bioField.setPrefRowCount(3);
        bioField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField educationField = new TextField();
        educationField.setPromptText("Education (optional)");
        educationField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField experienceField = new TextField();
        experienceField.setPromptText("Experience (optional)");
        experienceField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField formationField = new TextField();
        formationField.setPromptText("Formation/Certification (optional)");
        formationField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        CheckBox adminCheckBox = new CheckBox("Make this user an Admin");
        adminCheckBox.setStyle("-fx-font-size: 12;");

        // ===== CREATE ERROR LABELS (Initially Hidden) =====
        String errorLabelStyle = "-fx-text-fill: #d32f2f; -fx-font-size: 10; -fx-font-weight: bold;";
        
        Label nameErrorLabel = new Label("");
        nameErrorLabel.setStyle(errorLabelStyle);
        nameErrorLabel.setVisible(false);
        nameErrorLabel.setManaged(false);

        Label emailErrorLabel = new Label("");
        emailErrorLabel.setStyle(errorLabelStyle);
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);

        Label passwordErrorLabel = new Label("");
        passwordErrorLabel.setStyle(errorLabelStyle);
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);

        Label ageErrorLabel = new Label("");
        ageErrorLabel.setStyle(errorLabelStyle);
        ageErrorLabel.setVisible(false);
        ageErrorLabel.setManaged(false);

        Label bioErrorLabel = new Label("");
        bioErrorLabel.setStyle(errorLabelStyle);
        bioErrorLabel.setVisible(false);
        bioErrorLabel.setManaged(false);

        Label educationErrorLabel = new Label("");
        educationErrorLabel.setStyle(errorLabelStyle);
        educationErrorLabel.setVisible(false);
        educationErrorLabel.setManaged(false);

        Label experienceErrorLabel = new Label("");
        experienceErrorLabel.setStyle(errorLabelStyle);
        experienceErrorLabel.setVisible(false);
        experienceErrorLabel.setManaged(false);

        Label formationErrorLabel = new Label("");
        formationErrorLabel.setStyle(errorLabelStyle);
        formationErrorLabel.setVisible(false);
        formationErrorLabel.setManaged(false);

        // ===== ADD REAL-TIME VALIDATION LISTENERS (Using Reusable Methods) =====
        setupFieldValidationListener(nameField, nameErrorLabel, this::validateNameField);
        setupFieldValidationListener(emailField, emailErrorLabel, this::validateEmailField);
        setupFieldValidationListener(passwordField, passwordErrorLabel, this::validatePasswordField);
        setupFieldValidationListener(ageField, ageErrorLabel, this::validateAgeField);
        setupTextAreaValidationListener(bioField, bioErrorLabel, this::validateBioField);
        setupFieldValidationListener(educationField, educationErrorLabel, this::validateEducationField);
        setupFieldValidationListener(experienceField, experienceErrorLabel, this::validateExperienceField);
        setupFieldValidationListener(formationField, formationErrorLabel, this::validateFormationField);

        // ===== CREATE GRID LAYOUT WITH ERROR LABELS =====
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
        grid.setPadding(new Insets(25, 20, 25, 20));
        grid.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 8;");

        String labelStyle = "-fx-font-size: 11; -fx-font-weight: 600; -fx-text-fill: #333;";
        String requiredStyle = "-fx-font-size: 10; -fx-text-fill: #FF6B4A; -fx-font-weight: bold;";

        int row = 0;

        // Row: Full Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setStyle(labelStyle);
        Label nameRequired = new Label("*");
        nameRequired.setStyle(requiredStyle);
        grid.add(nameLabel, 0, row);
        grid.add(nameRequired, 0, row);
        grid.add(nameField, 1, row);
        grid.add(nameErrorLabel, 1, row + 1);
        row += 2;

        // Row: Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle(labelStyle);
        Label emailRequired = new Label("*");
        emailRequired.setStyle(requiredStyle);
        grid.add(emailLabel, 0, row);
        grid.add(emailRequired, 0, row);
        grid.add(emailField, 1, row);
        grid.add(emailErrorLabel, 1, row + 1);
        row += 2;

        // Row: Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle(labelStyle);
        Label passwordRequired = new Label("*");
        passwordRequired.setStyle(requiredStyle);
        grid.add(passwordLabel, 0, row);
        grid.add(passwordRequired, 0, row);
        grid.add(passwordField, 1, row);
        grid.add(passwordErrorLabel, 1, row + 1);
        row += 2;

        // Row: Age
        Label ageLabel = new Label("Age:");
        ageLabel.setStyle(labelStyle);
        Label ageRequired = new Label("*");
        ageRequired.setStyle(requiredStyle);
        grid.add(ageLabel, 0, row);
        grid.add(ageRequired, 0, row);
        grid.add(ageField, 1, row);
        grid.add(ageErrorLabel, 1, row + 1);
        row += 2;

        // Row: Level
        Label levelLabel = new Label("Level:");
        levelLabel.setStyle(labelStyle);
        Label levelRequired = new Label("*");
        levelRequired.setStyle(requiredStyle);
        grid.add(levelLabel, 0, row);
        grid.add(levelRequired, 0, row);
        grid.add(levelCombo, 1, row);
        row += 1;

        // Row: Bio
        Label bioLabel = new Label("Bio:");
        bioLabel.setStyle(labelStyle);
        grid.add(bioLabel, 0, row);
        grid.add(bioField, 1, row);
        grid.add(bioErrorLabel, 1, row + 1);
        row += 2;

        // Row: Education
        Label educationLabel = new Label("Education:");
        educationLabel.setStyle(labelStyle);
        grid.add(educationLabel, 0, row);
        grid.add(educationField, 1, row);
        grid.add(educationErrorLabel, 1, row + 1);
        row += 2;

        // Row: Experience
        Label experienceLabel = new Label("Experience:");
        experienceLabel.setStyle(labelStyle);
        grid.add(experienceLabel, 0, row);
        grid.add(experienceField, 1, row);
        grid.add(experienceErrorLabel, 1, row + 1);
        row += 2;

        // Row: Formation
        Label formationLabel = new Label("Formation:");
        formationLabel.setStyle(labelStyle);
        grid.add(formationLabel, 0, row);
        grid.add(formationField, 1, row);
        grid.add(formationErrorLabel, 1, row + 1);
        row += 2;

        // Row: Admin Checkbox
        grid.add(new Label(""), 0, row);
        grid.add(adminCheckBox, 1, row);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(850);
        dialog.getDialogPane().setStyle("-fx-background-color: #F5F5F5;");
        
        // Style dialog buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogButtons(dialog, "Add User");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Check if any error labels are visible (validation failed)
            if (nameErrorLabel.isVisible() || emailErrorLabel.isVisible() || 
                passwordErrorLabel.isVisible() || ageErrorLabel.isVisible() ||
                bioErrorLabel.isVisible() || educationErrorLabel.isVisible() ||
                experienceErrorLabel.isVisible() || formationErrorLabel.isVisible()) {
                showAlert(Alert.AlertType.ERROR, "Validation Failed ❌", 
                    "Please fix the errors highlighted in red before submitting.");
                return;
            }

            // Validate all inputs one final time
            String validationError = validateAddUserInputs(
                nameField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                ageField.getText().trim(),
                bioField.getText().trim(),
                educationField.getText().trim(),
                experienceField.getText().trim(),
                formationField.getText().trim()
            );
            
            if (!validationError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", validationError);
                return;
            }

            // Create new user and add to database
            User newUser = new User();
            newUser.setNomComplet(nameField.getText().trim());
            newUser.setEmail(emailField.getText().trim());
            newUser.setNiveauInfo(levelCombo.getValue());
            newUser.setAdmin(adminCheckBox.isSelected());
            newUser.setAge(Integer.parseInt(ageField.getText().trim()));
            newUser.setBio(bioField.getText().trim());
            newUser.setEducation(educationField.getText().trim());
            newUser.setExperience(experienceField.getText().trim());
            newUser.setFormation(formationField.getText().trim());
            newUser.setXp(0); // New users start with 0 XP

            boolean success = userDAO.addUser(newUser, passwordField.getText());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success ✅", "User added successfully!");
                loadUsersFromDatabase();
                applyAllFilters();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error ❌", "Failed to add user. Email may already exist.");
            }
        }
    }

    /**
     * Handle Edit User action with REAL-TIME validation and inline error messages
     */
    private void handleEditUser(User user) {
        if (user == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a user to edit");
            return;
        }

        System.out.println("📝 Editing user: " + user.getNomComplet());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getNomComplet());

        // ===== CREATE INPUT FIELDS WITH PRE-FILLED VALUES =====
        TextField nameField = new TextField(user.getNomComplet());
        nameField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField emailField = new TextField(user.getEmail());
        emailField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField ageField = new TextField(String.valueOf(user.getAge()));
        ageField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.setItems(FXCollections.observableArrayList("Beginner", "Advanced", "Expert"));
        levelCombo.setValue(user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner");
        levelCombo.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextArea bioField = new TextArea(user.getBio() != null ? user.getBio() : "");
        bioField.setPromptText("Bio (optional - max 500 characters)");
        bioField.setWrapText(true);
        bioField.setPrefRowCount(3);
        bioField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField educationField = new TextField(user.getEducation() != null ? user.getEducation() : "");
        educationField.setPromptText("Education (optional)");
        educationField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField experienceField = new TextField(user.getExperience() != null ? user.getExperience() : "");
        experienceField.setPromptText("Experience (optional)");
        experienceField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        TextField formationField = new TextField(user.getFormation() != null ? user.getFormation() : "");
        formationField.setPromptText("Formation/Certification (optional)");
        formationField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");

        CheckBox adminCheckBox = new CheckBox("Make this user an Admin");
        adminCheckBox.setSelected(user.isAdmin());
        adminCheckBox.setStyle("-fx-font-size: 12;");

        // ===== CREATE ERROR LABELS (Initially Hidden) =====
        String errorLabelStyle = "-fx-text-fill: #d32f2f; -fx-font-size: 10; -fx-font-weight: bold;";
        
        Label nameErrorLabel = new Label("");
        nameErrorLabel.setStyle(errorLabelStyle);
        nameErrorLabel.setVisible(false);
        nameErrorLabel.setManaged(false);

        Label emailErrorLabel = new Label("");
        emailErrorLabel.setStyle(errorLabelStyle);
        emailErrorLabel.setVisible(false);
        emailErrorLabel.setManaged(false);

        Label ageErrorLabel = new Label("");
        ageErrorLabel.setStyle(errorLabelStyle);
        ageErrorLabel.setVisible(false);
        ageErrorLabel.setManaged(false);

        Label bioErrorLabel = new Label("");
        bioErrorLabel.setStyle(errorLabelStyle);
        bioErrorLabel.setVisible(false);
        bioErrorLabel.setManaged(false);

        Label educationErrorLabel = new Label("");
        educationErrorLabel.setStyle(errorLabelStyle);
        educationErrorLabel.setVisible(false);
        educationErrorLabel.setManaged(false);

        Label experienceErrorLabel = new Label("");
        experienceErrorLabel.setStyle(errorLabelStyle);
        experienceErrorLabel.setVisible(false);
        experienceErrorLabel.setManaged(false);

        Label formationErrorLabel = new Label("");
        formationErrorLabel.setStyle(errorLabelStyle);
        formationErrorLabel.setVisible(false);
        formationErrorLabel.setManaged(false);

        // ===== ADD REAL-TIME VALIDATION LISTENERS (Using Reusable Methods) =====
        setupFieldValidationListener(nameField, nameErrorLabel, this::validateNameField);
        setupFieldValidationListener(emailField, emailErrorLabel, this::validateEmailField);
        setupFieldValidationListener(ageField, ageErrorLabel, this::validateAgeField);
        setupTextAreaValidationListener(bioField, bioErrorLabel, this::validateBioField);
        setupFieldValidationListener(educationField, educationErrorLabel, this::validateEducationField);
        setupFieldValidationListener(experienceField, experienceErrorLabel, this::validateExperienceField);
        setupFieldValidationListener(formationField, formationErrorLabel, this::validateFormationField);

        // ===== CREATE GRID LAYOUT WITH ERROR LABELS =====
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);
        grid.setPadding(new Insets(25, 20, 25, 20));
        grid.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 8;");

        String labelStyle = "-fx-font-size: 11; -fx-font-weight: 600; -fx-text-fill: #333;";
        String requiredStyle = "-fx-font-size: 10; -fx-text-fill: #FF6B4A; -fx-font-weight: bold;";

        int row = 0;

        // Row: Full Name
        Label nameLabel = new Label("Full Name:");
        nameLabel.setStyle(labelStyle);
        Label nameRequired = new Label("*");
        nameRequired.setStyle(requiredStyle);
        grid.add(nameLabel, 0, row);
        grid.add(nameRequired, 0, row);
        grid.add(nameField, 1, row);
        grid.add(nameErrorLabel, 1, row + 1);
        row += 2;

        // Row: Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle(labelStyle);
        Label emailRequired = new Label("*");
        emailRequired.setStyle(requiredStyle);
        grid.add(emailLabel, 0, row);
        grid.add(emailRequired, 0, row);
        grid.add(emailField, 1, row);
        grid.add(emailErrorLabel, 1, row + 1);
        row += 2;

        // Row: Age
        Label ageLabel = new Label("Age:");
        ageLabel.setStyle(labelStyle);
        Label ageRequired = new Label("*");
        ageRequired.setStyle(requiredStyle);
        grid.add(ageLabel, 0, row);
        grid.add(ageRequired, 0, row);
        grid.add(ageField, 1, row);
        grid.add(ageErrorLabel, 1, row + 1);
        row += 2;

        // Row: Level
        Label levelLabel = new Label("Level:");
        levelLabel.setStyle(labelStyle);
        Label levelRequired = new Label("*");
        levelRequired.setStyle(requiredStyle);
        grid.add(levelLabel, 0, row);
        grid.add(levelRequired, 0, row);
        grid.add(levelCombo, 1, row);
        row += 1;

        // Row: Bio
        Label bioLabel = new Label("Bio:");
        bioLabel.setStyle(labelStyle);
        grid.add(bioLabel, 0, row);
        grid.add(bioField, 1, row);
        grid.add(bioErrorLabel, 1, row + 1);
        row += 2;

        // Row: Education
        Label educationLabel = new Label("Education:");
        educationLabel.setStyle(labelStyle);
        grid.add(educationLabel, 0, row);
        grid.add(educationField, 1, row);
        grid.add(educationErrorLabel, 1, row + 1);
        row += 2;

        // Row: Experience
        Label experienceLabel = new Label("Experience:");
        experienceLabel.setStyle(labelStyle);
        grid.add(experienceLabel, 0, row);
        grid.add(experienceField, 1, row);
        grid.add(experienceErrorLabel, 1, row + 1);
        row += 2;

        // Row: Formation
        Label formationLabel = new Label("Formation:");
        formationLabel.setStyle(labelStyle);
        grid.add(formationLabel, 0, row);
        grid.add(formationField, 1, row);
        grid.add(formationErrorLabel, 1, row + 1);
        row += 2;

        // Row: Admin Checkbox
        grid.add(new Label(""), 0, row);
        grid.add(adminCheckBox, 1, row);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(850);
        dialog.getDialogPane().setStyle("-fx-background-color: #F5F5F5;");
        
        // Style dialog buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogButtons(dialog, "Update User");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Check if any error labels are visible (validation failed)
            if (nameErrorLabel.isVisible() || emailErrorLabel.isVisible() || 
                ageErrorLabel.isVisible() || bioErrorLabel.isVisible() || 
                educationErrorLabel.isVisible() || experienceErrorLabel.isVisible() || 
                formationErrorLabel.isVisible()) {
                showAlert(Alert.AlertType.ERROR, "Validation Failed ❌", 
                    "Please fix the errors highlighted in red before updating.");
                return;
            }

            // Validate all inputs one final time
            String nameError = validateNameField(nameField.getText().trim());
            String emailError = validateEmailField(emailField.getText().trim());
            String ageError = validateAgeField(ageField.getText().trim());
            String bioError = validateBioField(bioField.getText().trim());
            String educationError = validateEducationField(educationField.getText().trim());
            String experienceError = validateExperienceField(experienceField.getText().trim());
            String formationError = validateFormationField(formationField.getText().trim());

            if (!nameError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", nameError);
                return;
            }
            if (!emailError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", emailError);
                return;
            }
            if (!ageError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", ageError);
                return;
            }
            if (!bioError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", bioError);
                return;
            }
            if (!educationError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", educationError);
                return;
            }
            if (!experienceError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", experienceError);
                return;
            }
            if (!formationError.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", formationError);
                return;
            }

            // Update user properties
            user.setNomComplet(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setAge(Integer.parseInt(ageField.getText().trim()));
            user.setBio(bioField.getText().trim());
            user.setEducation(educationField.getText().trim());
            user.setExperience(experienceField.getText().trim());
            user.setFormation(formationField.getText().trim());
            user.setNiveauInfo(levelCombo.getValue());
            user.setAdmin(adminCheckBox.isSelected());

            boolean success = userDAO.updateProfile(user);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success ✅", "User updated successfully!");
                loadUsersFromDatabase();
                applyAllFilters();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error ❌", "Failed to update user");
            }
        }
    }

    /**
     * Handle Delete User with confirmation dialog
     */
    private void handleDeleteUser(User user) {
        if (user == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a user to delete");
            return;
        }

        // Confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete User");
        confirmDialog.setHeaderText("Delete User");
        confirmDialog.setContentText("Are you sure you want to delete the user:\n\n" + user.getNomComplet() + 
                                    "\n\nEmail: " + user.getEmail() + 
                                    "\n\n⚠️ This action cannot be undone!");
        confirmDialog.getDialogPane().setStyle("-fx-font-size: 14;");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userDAO.deleteUser(user.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success ✅", "User deleted successfully!");
                loadUsersFromDatabase();
                applyAllFilters();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error ❌", "Failed to delete user");
            }
        }
    }

    /**
     * Validate user input fields
     */
    private String validateUserInputs(String name, String email, String password) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            return "❌ Full name cannot be empty";
        }
        if (name.trim().length() < 3) {
            return "❌ Full name must be at least 3 characters";
        }
        if (name.trim().length() > 100) {
            return "❌ Full name cannot exceed 100 characters";
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            return "❌ Email cannot be empty";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "❌ Invalid email format. Use: user@example.com";
        }

        // Validate password (only if provided)
        if (password != null && !password.isEmpty() && password.length() < 6) {
            return "❌ Password must be at least 6 characters";
        }

        return "";
    }

    /**
     * Validate all Add User input fields (Contrôle de Saisie)
     */
    private String validateAddUserInputs(String name, String email, String password, String age, String bio, String education, String experience, String formation) {
        // === REQUIRED FIELDS ===
        
        // Validate Full Name
        if (name == null || name.isEmpty()) {
            return "❌ Full Name is required";
        }
        if (!name.matches("^[a-zA-Z\\s\\-]{2,100}$")) {
            return "❌ Full Name must contain only letters, spaces, and hyphens (2-100 characters)";
        }

        // Validate Email
        if (email == null || email.isEmpty()) {
            return "❌ Email is required";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "❌ Email must be in valid format (e.g., user@example.com)";
        }

        // Validate Password
        if (password == null || password.isEmpty()) {
            return "❌ Password is required";
        }
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            return "❌ Password must contain only letters (A-Z, a-z) and numbers (0-9)";
        }
        if (password.length() < 6) {
            return "❌ Password must be at least 6 characters";
        }

        // Validate Age
        if (age == null || age.isEmpty()) {
            return "❌ Age is required";
        }
        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 5 || ageValue > 120) {
                return "❌ Age must be between 5 and 120";
            }
        } catch (NumberFormatException e) {
            return "❌ Age must be a valid number (no letters or special characters)";
        }

        // === OPTIONAL FIELDS ===

        // Validate Bio (optional but if provided, check length)
        if (bio != null && !bio.isEmpty() && bio.length() > 500) {
            return "❌ Bio cannot exceed 500 characters";
        }

        // Validate Education (optional but if provided, check length)
        if (education != null && !education.isEmpty() && education.length() > 200) {
            return "❌ Education cannot exceed 200 characters";
        }

        // Validate Experience (optional but if provided, check length)
        if (experience != null && !experience.isEmpty() && experience.length() > 200) {
            return "❌ Experience cannot exceed 200 characters";
        }

        // Validate Formation (optional but if provided, check length)
        if (formation != null && !formation.isEmpty() && formation.length() > 200) {
            return "❌ Formation cannot exceed 200 characters";
        }

        // All validations passed
        return "";
    }

    /**
     * Validate Full Name field (reusable for Add/Edit)
     * Returns empty string if valid, error message if invalid
     */
    private String validateNameField(String name) {
        if (name == null || name.isEmpty()) {
            return "❌ Full Name is required";
        }
        if (!name.matches("^[a-zA-Z\\s\\-]{2,100}$")) {
            return "❌ Only letters, spaces, hyphens (2-100 chars)";
        }
        return "";
    }

    /**
     * Validate Email field (reusable for Add/Edit)
     * Returns empty string if valid, error message if invalid
     */
    private String validateEmailField(String email) {
        if (email == null || email.isEmpty()) {
            return "❌ Email is required";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "❌ Invalid email format (user@example.com)";
        }
        return "";
    }

    /**
     * Validate Password field (reusable for Add User only)
     * For Edit User, password validation is optional
     */
    private String validatePasswordField(String password) {
        if (password == null || password.isEmpty()) {
            return "❌ Password is required";
        }
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            return "❌ Only letters (A-Z, a-z) and numbers (0-9)";
        }
        if (password.length() < 6) {
            return "❌ At least 6 characters required";
        }
        return "";
    }

    /**
     * Validate Age field (reusable for Add/Edit)
     * Returns empty string if valid, error message if invalid
     */
    private String validateAgeField(String age) {
        if (age == null || age.isEmpty()) {
            return "❌ Age is required";
        }
        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue < 5 || ageValue > 120) {
                return "❌ Age must be between 5 and 120";
            }
        } catch (NumberFormatException e) {
            return "❌ Age must be a number (no letters)";
        }
        return "";
    }

    /**
     * Validate Bio field (optional, max 500 chars)
     */
    private String validateBioField(String bio) {
        if (bio != null && bio.length() > 500) {
            return "❌ Bio exceeds 500 characters (" + bio.length() + "/500)";
        }
        return "";
    }

    /**
     * Validate Education field (optional, max 200 chars)
     */
    private String validateEducationField(String education) {
        if (education != null && education.length() > 200) {
            return "❌ Education exceeds 200 characters";
        }
        return "";
    }

    /**
     * Validate Experience field (optional, max 200 chars)
     */
    private String validateExperienceField(String experience) {
        if (experience != null && experience.length() > 200) {
            return "❌ Experience exceeds 200 characters";
        }
        return "";
    }

    /**
     * Validate Formation field (optional, max 200 chars)
     */
    private String validateFormationField(String formation) {
        if (formation != null && formation.length() > 200) {
            return "❌ Formation exceeds 200 characters";
        }
        return "";
    }

    /**
     * Setup real-time validation listener for a text field
     * Automatically shows/hides error labels and updates field styling
     */
    private void setupFieldValidationListener(TextField field, Label errorLabel, java.util.function.Function<String, String> validator) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String error = validator.apply(newVal);
            if (error.isEmpty()) {
                // Valid field
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                field.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #4CAF50; -fx-border-width: 1; -fx-border-radius: 4;");
            } else {
                // Invalid field
                errorLabel.setText(error);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                field.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #d32f2f; -fx-border-width: 2; -fx-border-radius: 4;");
            }
        });
    }

    /**
     * Setup real-time validation listener for a TextArea
     * Automatically shows/hides error labels and updates field styling
     */
    private void setupTextAreaValidationListener(TextArea field, Label errorLabel, java.util.function.Function<String, String> validator) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String error = validator.apply(newVal);
            if (error.isEmpty()) {
                // Valid field
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                field.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4;");
            } else {
                // Invalid field
                errorLabel.setText(error);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                field.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-border-color: #d32f2f; -fx-border-width: 2; -fx-border-radius: 4;");
            }
        });
    }

    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Sidebar navigation handlers
    @FXML
    private void handleNavDashboard() {
        navigateToView(AppConfig.VIEW_DASHBOARD_BACK, "Dashboard");
    }

    @FXML
    private void handleNavUsers() {
        // Already on Users page
    }

    @FXML
    private void handleNavCourses() {
        navigateToView(AppConfig.VIEW_COURSES_BACK, "Courses");
    }

    @FXML
    private void handleNavForum() {
        navigateToView(AppConfig.VIEW_FORUM_BACK, "Forum");
    }

    @FXML
    private void handleNavProblems() {
        navigateToView(AppConfig.VIEW_PROBLEMS_BACK, "Problems");
    }

    @FXML
    private void handleNavProjects() {
        navigateToView(AppConfig.VIEW_PROJECTS_BACK, "Projects");
    }

    @FXML
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS_BACK, "Events");
    }

    @FXML
    private void handleThemeToggle() {
        System.out.println("🌙 Theme toggle clicked");
    }

    /**
     * Navigate to a different view
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = (Stage) usersTable.getScene().getWindow();
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("❌ Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
