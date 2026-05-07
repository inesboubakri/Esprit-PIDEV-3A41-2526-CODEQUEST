package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.NavigationManager;
import utils.AppConfig;

/**
 * Controller for the Users Management view (admin).
 */
public class UsersBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView usersTable;

    @FXML
    public void initialize() {
        // Setup users table with sample data
        setupUsersTable();
    }

    /**
     * Setup the users table with sample data.
     */
    private void setupUsersTable() {
        // Sample users data
        ObservableList<Object[]> usersList = FXCollections.observableArrayList(
            new Object[]{"USR-001", "John Doe", "john.doe@email.com", "15", "Active"},
            new Object[]{"USR-002", "Sarah Miller", "sarah.miller@email.com", "12", "Active"},
            new Object[]{"USR-003", "Alex Kim", "alex.kim@email.com", "10", "Active"},
            new Object[]{"USR-004", "Emily Brown", "emily.brown@email.com", "8", "Inactive"},
            new Object[]{"USR-005", "David Wilson", "david.wilson@email.com", "18", "Active"},
            new Object[]{"USR-006", "Jessica Lee", "jessica.lee@email.com", "9", "Active"}
        );
        usersTable.setItems(usersList);
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
        // TODO: Implement theme toggle functionality
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
