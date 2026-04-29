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
 * Controller for the Forum Management view (admin).
 */
public class ForumBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView forumTable;

    @FXML
    public void initialize() {
        // Initialize forum table and data
        setupForumTable();
    }

    /**
     * Setup the forum table structure with sample data.
     */
    private void setupForumTable() {
        // Sample forum posts data
        ObservableList<Object[]> forumList = FXCollections.observableArrayList(
            new Object[]{"POST-001", "How to start with Java?", "John Doe", "Beginner Help", 12, 156, "2024-05-10"},
            new Object[]{"POST-002", "Best practices for REST APIs", "Jane Smith", "Web Development", 8, 342, "2024-05-08"},
            new Object[]{"POST-003", "React Hooks Q&A", "Mike Johnson", "React Discussion", 23, 578, "2024-05-12"},
            new Object[]{"POST-004", "Database indexing explained", "Sarah Lee", "Database Design", 15, 289, "2024-05-09"},
            new Object[]{"POST-005", "Spring Boot microservices", "David Brown", "Java Backend", 19, 412, "2024-05-11"},
            new Object[]{"POST-006", "CSS Grid vs Flexbox", "Emma Davis", "Frontend Design", 6, 234, "2024-05-07"}
        );
        forumTable.setItems(forumList);
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
