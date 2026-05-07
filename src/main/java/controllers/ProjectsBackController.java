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
 * Controller for the Projects Management view (admin).
 */
public class ProjectsBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView projectsTable;

    @FXML
    public void initialize() {
        // Initialize projects table and data
        setupProjectsTable();
    }

    /**
     * Setup the projects table structure with sample data.
     */
    private void setupProjectsTable() {
        // Sample projects data
        ObservableList<Object[]> projectsList = FXCollections.observableArrayList(
            new Object[]{"PROJ-001", "E-Commerce Platform", "Completed", 5, "2024-05-01", "2024-04-15"},
            new Object[]{"PROJ-002", "Social Media App", "Active", 8, "2024-06-15", "2024-04-20"},
            new Object[]{"PROJ-003", "AI Chatbot", "In Progress", 3, "2024-06-01", "2024-05-01"},
            new Object[]{"PROJ-004", "Blog CMS", "Completed", 4, "2024-04-30", "2024-04-01"},
            new Object[]{"PROJ-005", "Mobile Game", "On Hold", 6, "2024-07-01", "2024-05-15"},
            new Object[]{"PROJ-006", "Analytics Dashboard", "Active", 5, "2024-06-20", "2024-05-05"}
        );
        projectsTable.setItems(projectsList);
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
