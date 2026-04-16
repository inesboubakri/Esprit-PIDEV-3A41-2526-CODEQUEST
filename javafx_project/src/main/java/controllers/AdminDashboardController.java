package controllers;

import dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.User;
import utils.NavigationManager;
import utils.SessionManager;

import java.util.List;

/**
 * Controller for the Admin Dashboard view.
 * Handles user management, search, filter, and CRUD operations.
 */
public class AdminDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> levelColumn;

    @FXML
    private TableColumn<User, Integer> adminColumn;

    @FXML
    private TableColumn<User, Integer> bannedColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label adminNameLabel;

    private ObservableList<User> allUsers;
    private ObservableList<User> filteredUsers;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        setupTable();
        loadUsers();
        displayAdminName();
    }

    /**
     * Setup table columns with cell factories.
     */
    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomComplet()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        levelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNiveauInfo()));
        adminColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIsAdmin()).asObject());
        bannedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIsBanned()).asObject());

        // Setup actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button banBtn = new Button("Ban");

            {
                editBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10;");
                deleteBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-text-fill: white; -fx-background-color: #d32f2f;");
                banBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-text-fill: white; -fx-background-color: #f57c00;");

                editBtn.setOnAction(event -> editUser(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteUser(getTableView().getItems().get(getIndex())));
                banBtn.setOnAction(event -> toggleBanUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    HBox actions = new HBox(5);
                    actions.setAlignment(Pos.CENTER);

                    actions.getChildren().add(editBtn);
                    actions.getChildren().add(deleteBtn);

                    if (user.getIsBanned() == 1) {
                        Button unbanBtn = new Button("Unban");
                        unbanBtn.setStyle("-fx-padding: 4 8; -fx-font-size: 10; -fx-text-fill: white; -fx-background-color: #388e3c;");
                        unbanBtn.setOnAction(event -> toggleBanUser(user));
                        actions.getChildren().add(unbanBtn);
                    } else {
                        actions.getChildren().add(banBtn);
                    }

                    setGraphic(actions);
                }
            }
        });
    }

    /**
     * Load all users from database.
     */
    private void loadUsers() {
        try {
            List<User> users = UserDAO.getAllUsers();
            allUsers = FXCollections.observableArrayList(users);
            filteredUsers = FXCollections.observableArrayList(users);
            usersTable.setItems(filteredUsers);
            statusLabel.setText("Total users: " + users.size());
        } catch (Exception e) {
            statusLabel.setText("Error loading users: " + e.getMessage());
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Handle search functionality.
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            filteredUsers.setAll(allUsers);
            statusLabel.setText("Total users: " + allUsers.size());
        } else {
            ObservableList<User> filtered = FXCollections.observableArrayList();
            for (User user : allUsers) {
                if (user.getNomComplet().toLowerCase().contains(searchTerm) ||
                    user.getEmail().toLowerCase().contains(searchTerm)) {
                    filtered.add(user);
                }
            }
            filteredUsers.setAll(filtered);
            statusLabel.setText("Found: " + filtered.size() + " users");
        }
    }

    /**
     * Handle refresh button click.
     */
    @FXML
    private void handleRefresh() {
        loadUsers();
        searchField.clear();
        statusLabel.setText("Data refreshed");
    }

    /**
     * Edit user - open edit dialog.
     */
    private void editUser(User user) {
        System.out.println("Edit user: " + user.getNomComplet());
        // TODO: Implement edit user dialog
    }

    /**
     * Delete user from database.
     */
    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Delete user: " + user.getNomComplet() + "? This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (UserDAO.deleteUser(user.getId())) {
                statusLabel.setText("User deleted successfully");
                loadUsers();
            } else {
                statusLabel.setText("Error deleting user");
            }
        }
    }

    /**
     * Toggle user ban status.
     */
    private void toggleBanUser(User user) {
        String action = user.getIsBanned() == 1 ? "unban" : "ban";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(action.substring(0, 1).toUpperCase() + action.substring(1) + " User");
        alert.setHeaderText("Are you sure?");
        alert.setContentText(action.substring(0, 1).toUpperCase() + action.substring(1) + " user: " + user.getNomComplet() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if (user.getIsBanned() == 1) {
                success = UserDAO.unbanUser(user.getId());
            } else {
                success = UserDAO.banUser(user.getId());
            }

            if (success) {
                statusLabel.setText("User status updated");
                loadUsers();
            } else {
                statusLabel.setText("Error updating user status");
            }
        }
    }

    /**
     * Handle Add User button click.
     */
    @FXML
    private void handleAddUser() {
        System.out.println("Add new user clicked");
        // TODO: Implement add user dialog
    }

    /**
     * Handle View Stats button click.
     */
    @FXML
    private void handleViewStats() {
        System.out.println("View statistics clicked");
        // TODO: Implement statistics view
    }

    /**
     * Handle View Users button click (reload).
     */
    @FXML
    private void handleViewUsers() {
        loadUsers();
    }

    /**
     * Display current admin's name in header.
     */
    private void displayAdminName() {
        String adminName = SessionManager.getCurrentUserName();
        if (adminName != null) {
            adminNameLabel.setText("Admin: " + adminName);
        }
    }

    /**
     * Handle Logout button click.
     */
    @FXML
    private void handleLogOut() {
        SessionManager.logout();
        try {
            Stage stage = (Stage) usersTable.getScene().getWindow();
            NavigationManager.navigateTo(stage, "views/SignInView.fxml", "Sign In");
        } catch (Exception e) {
            System.err.println("Error navigating to Sign In: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
