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
 * Controller for the Problems Management view (admin).
 */
public class ProblemsBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView problemsTable;

    @FXML
    public void initialize() {
        // Initialize problems table and data
        setupProblemsTable();
    }

    /**
     * Setup the problems table structure with sample data.
     */
    private void setupProblemsTable() {
        // Sample problems data
        ObservableList<Object[]> problemsList = FXCollections.observableArrayList(
            new Object[]{"PROB-001", "Two Sum", "Easy", "Solved", 1245, "98.5%", "2024-04-15"},
            new Object[]{"PROB-002", "Longest Substring", "Medium", "Solved", 856, "76.3%", "2024-04-18"},
            new Object[]{"PROB-003", "Merge K Sorted Lists", "Hard", "Active", 234, "42.1%", "2024-05-01"},
            new Object[]{"PROB-004", "Binary Tree Traversal", "Medium", "Solved", 567, "85.2%", "2024-04-20"},
            new Object[]{"PROB-005", "Dynamic Programming", "Hard", "Active", 123, "31.5%", "2024-05-05"},
            new Object[]{"PROB-006", "Array Rotation", "Easy", "Solved", 1890, "96.2%", "2024-04-10"}
        );
        problemsTable.setItems(problemsList);
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
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS_BACK, "Events Management");
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
