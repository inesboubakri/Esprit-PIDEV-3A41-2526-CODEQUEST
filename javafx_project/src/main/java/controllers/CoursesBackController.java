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
 * Controller for the Courses Management view (admin).
 */
public class CoursesBackController {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView coursesTable;

    @FXML
    public void initialize() {
        // Setup courses table with sample data
        setupCoursesTable();
    }

    /**
     * Setup the courses table with sample data.
     */
    private void setupCoursesTable() {
        // Sample courses data
        ObservableList<Object[]> coursesList = FXCollections.observableArrayList(
            new Object[]{"COURSE-001", "Java Fundamentals", "John Smith", 156, "Active"},
            new Object[]{"COURSE-002", "Web Development Bootcamp", "Jane Doe", 234, "Active"},
            new Object[]{"COURSE-003", "Python for Data Science", "Mike Johnson", 89, "Active"},
            new Object[]{"COURSE-004", "React Mastery", "Sarah Lee", 145, "Active"},
            new Object[]{"COURSE-005", "Database Design & SQL", "David Brown", 67, "Inactive"},
            new Object[]{"COURSE-006", "Cloud Computing Basics", "Emma Wilson", 112, "Active"}
        );
        coursesTable.setItems(coursesList);
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
