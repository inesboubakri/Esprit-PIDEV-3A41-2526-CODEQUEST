package utils;

/**
 * Configuration utility class containing application-wide constants and settings.
 * 
 * This class centralizes all configuration values for easy maintenance and customization.
 */
public class AppConfig {

    // Application Title
    public static final String APP_TITLE = "CodeQuest - Learn Programming Through Gaming";
    
    // Window Dimensions
    public static final int DEFAULT_WINDOW_WIDTH = 1400;
    public static final int DEFAULT_WINDOW_HEIGHT = 900;
    public static final int MIN_WINDOW_WIDTH = 800;
    public static final int MIN_WINDOW_HEIGHT = 600;
    
    // Color Scheme Constants
    public static final String COLOR_PRIMARY = "#FF6B4A";
    public static final String COLOR_PRIMARY_HOVER = "#ff7a1f";
    public static final String COLOR_SECONDARY = "#FFB800";
    public static final String COLOR_ACCENT = "#2196F3";
    public static final String COLOR_SUCCESS = "#4CAF50";
    public static final String COLOR_WARNING = "#FFC107";
    public static final String COLOR_DANGER = "#F44336";
    public static final String COLOR_TEXT_DARK = "#333333";
    public static final String COLOR_TEXT_LIGHT = "#666666";
    
    // Font Sizes (should match CSS)
    public static final int FONT_SIZE_TITLE = 32;
    public static final int FONT_SIZE_HEADING = 24;
    public static final int FONT_SIZE_LARGE = 18;
    public static final int FONT_SIZE_BASE = 14;
    public static final int FONT_SIZE_SMALL = 12;
    
    // Common Spacing Values
    public static final int PADDING_LARGE = 40;
    public static final int PADDING_MEDIUM = 24;
    public static final int PADDING_SMALL = 16;
    public static final int PADDING_XS = 8;
    
    // Border Radius Values
    public static final int BORDER_RADIUS_FULL = 999;
    public static final int BORDER_RADIUS_LARGE = 20;
    public static final int BORDER_RADIUS_MEDIUM = 16;
    public static final int BORDER_RADIUS_SMALL = 8;
    
    // View Paths
    public static final String VIEW_HOME = "views/HomeView.fxml";
    public static final String VIEW_COURSES = "views/CoursesView.fxml";
    public static final String VIEW_PROBLEMS = "views/ProblemsView.fxml";
    public static final String VIEW_PROJECTS = "views/ProjectsView.fxml";
    public static final String VIEW_EVENTS = "views/EventsView.fxml";
    public static final String VIEW_FORUM = "views/ForumView.fxml";
    public static final String VIEW_USERS = "views/UsersView.fxml";
    public static final String VIEW_PROFILE = "views/ProfileView.fxml";
    public static final String VIEW_EDIT_PROFILE = "views/EditProfileView.fxml";
    public static final String VIEW_SIGNIN = "views/SignInView.fxml";
    public static final String VIEW_SIGNUP = "views/SignUpView.fxml";
    public static final String VIEW_FORGOT_PASSWORD = "views/ForgotPasswordView.fxml";
    public static final String VIEW_PACKAGE_SELECTION = "views/PackageSelectionView.fxml";
    public static final String VIEW_PAYMENT = "views/PaymentView.fxml";
    public static final String VIEW_DASHBOARD = "views/DashboardView.fxml";
    public static final String VIEW_DASHBOARD_BACK = "views/DashboardBackView.fxml";
    public static final String VIEW_USERS_BACK = "views/UsersBackView.fxml";
    public static final String VIEW_COURSES_BACK = "views/CoursesBackView.fxml";
    public static final String VIEW_FORUM_BACK = "views/ForumBackView.fxml";
    public static final String VIEW_PROBLEMS_BACK = "views/ProblemsBackView.fxml";
    public static final String VIEW_PROJECTS_BACK = "views/ProjectsBackView.fxml";
    public static final String VIEW_EVENTS_BACK = "views/EventsBackView.fxml";
    
    // Stylesheet
    public static final String STYLESHEET = "/styles.css";
}
