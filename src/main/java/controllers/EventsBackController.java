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
 * Controller for the Events Management view (admin).
 */
public class EventsBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView eventsTable;

    @FXML
    public void initialize() {
        // Initialize events table and data
        setupEventsTable();
    }

    /**
     * Setup the events table structure with sample data.
     */
    private void setupEventsTable() {
        // Sample events data
        ObservableList<Object[]> eventsList = FXCollections.observableArrayList(
            new Object[]{"EVT-001", "Java Workshop", "Workshop", "2024-05-15", 45, "Active"},
            new Object[]{"EVT-002", "Web Dev Bootcamp", "Bootcamp", "2024-06-01", 120, "Active"},
            new Object[]{"EVT-003", "AI/ML Summit", "Conference", "2024-07-10", 200, "Scheduled"},
            new Object[]{"EVT-004", "Coding Challenge", "Competition", "2024-04-20", 78, "Completed"},
            new Object[]{"EVT-005", "React Masterclass", "Workshop", "2024-05-25", 56, "Active"},
            new Object[]{"EVT-006", "Database Optimization", "Webinar", "2024-05-18", 89, "Active"},
            new Object[]{"EVT-007", "DevOps Conference", "Conference", "2024-08-05", 150, "Scheduled"}
        );
        eventsTable.setItems(eventsList);
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
