package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import utils.NavigationManager;
import utils.SessionManager;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the Profile view (Student).
 * Displays current user's profile information with tabbed interface.
 * All data is loaded dynamically from the database.
 */
public class ProfileController {

    @FXML
    private Label profileName;
    
    @FXML
    private Label profileSubtitle;
    
    @FXML
    private Label profileEmail;
    
    @FXML
    private Label profileBio;
    
    @FXML
    private Label heroTitle;
    
    @FXML
    private Label levelBadge;
    
    @FXML
    private Label memberSinceDisplay;
    
    @FXML
    private Label educationDisplay;
    
    @FXML
    private Label experienceDisplay;
    
    @FXML
    private Label experienceRole;
    
    @FXML
    private Label roleDisplay;
    
    @FXML
    private Label formationDisplay;
    
    @FXML
    private Label ageDisplay;

    // Tab buttons
    @FXML
    private Button tabOverview;
    
    @FXML
    private Button tabCourses;
    
    @FXML
    private Button tabEvents;
    
    @FXML
    private Button tabForum;
    
    @FXML
    private Button tabProblems;
    
    @FXML
    private Button tabProjects;

    // Tab content panels
    @FXML
    private VBox overviewTab;
    
    @FXML
    private VBox coursesTab;
    
    @FXML
    private VBox eventsTab;
    
    @FXML
    private VBox forumTab;
    
    @FXML
    private VBox problemsTab;
    
    @FXML
    private VBox projectsTab;

    /**
     * Initialize method called when FXML is loaded.
     */
    @FXML
    public void initialize() {
        loadUserProfile();
    }

    /**
     * Load complete user profile from database and display all information.
     * Fetches fresh data from DB to ensure we have latest information.
     */
    private void loadUserProfile() {
        Integer userId = SessionManager.getCurrentUserId();

        if (userId == null) {
            System.err.println("ERROR: No user in session, redirecting to login");
            handleLogOut();
            return;
        }

        // Fetch fresh data from database
        User user = UserDAO.getUserById(userId);

        if (user == null) {
            System.err.println("ERROR: User not found in database for ID: " + userId);
            handleLogOut();
            return;
        }

        System.out.println("INFO: Loading profile for user: " + user.getNomComplet() + " (ID: " + user.getId() + ")");

        try {
            // ===== HERO SECTION ====
            String fullName = user.getNomComplet() != null ? user.getNomComplet() : "Unknown User";
            String level = user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner";
            int xp = user.getXp();
            
            profileName.setText(fullName);
            heroTitle.setText(fullName + "'s Complete Profile");
            levelBadge.setText("⚡ " + level + " • " + xp + " XP");
            
            // ===== PROFILE HEADER ====
            profileSubtitle.setText((user.getRole() != null ? user.getRole() : "Developer") + " Profile");
            profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Not specified");
            
            // ===== BIO SECTION ====
            profileBio.setText(user.getBio() != null && !user.getBio().isEmpty() 
                ? user.getBio() 
                : "No bio provided");
            
            // ===== EDUCATION & EXPERIENCE ====
            educationDisplay.setText(user.getEducation() != null && !user.getEducation().isEmpty()
                ? user.getEducation()
                : "Not specified");
            
            experienceDisplay.setText(user.getExperience() != null && !user.getExperience().isEmpty()
                ? user.getExperience()
                : "Not specified");
            
            experienceRole.setText((user.getRole() != null ? user.getRole() : "Developer") + " & Mentor");
            
            // ===== MEMBER INFORMATION ====
            roleDisplay.setText(user.getRole() != null ? user.getRole() : "Student");
            
            formationDisplay.setText(user.getFormation() != null && !user.getFormation().isEmpty()
                ? user.getFormation()
                : "Not specified");
            
            ageDisplay.setText(user.getAge() > 0 ? String.valueOf(user.getAge()) : "Not specified");
            
            // ===== MEMBER SINCE DATE ====
            if (user.getCreatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                memberSinceDisplay.setText(user.getCreatedAt().format(formatter));
            } else {
                memberSinceDisplay.setText("January 2024");
            }
            
            System.out.println("SUCCESS: Profile loaded successfully for " + fullName);
            
        } catch (Exception e) {
            System.err.println("ERROR loading profile data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tab handlers - switch between different profile sections
     */
    @FXML
    private void handleTabOverview() {
        switchTab(overviewTab, tabOverview);
    }

    @FXML
    private void handleTabCourses() {
        switchTab(coursesTab, tabCourses);
    }

    @FXML
    private void handleTabEvents() {
        switchTab(eventsTab, tabEvents);
    }

    @FXML
    private void handleTabForum() {
        switchTab(forumTab, tabForum);
    }

    @FXML
    private void handleTabProblems() {
        switchTab(problemsTab, tabProblems);
    }

    @FXML
    private void handleTabProjects() {
        switchTab(projectsTab, tabProjects);
    }

    /**
     * Helper method to switch between tabs
     */
    private void switchTab(VBox tabContent, Button tabButton) {
        // Hide all tabs
        overviewTab.setVisible(false);
        overviewTab.setManaged(false);
        coursesTab.setVisible(false);
        coursesTab.setManaged(false);
        eventsTab.setVisible(false);
        eventsTab.setManaged(false);
        forumTab.setVisible(false);
        forumTab.setManaged(false);
        problemsTab.setVisible(false);
        problemsTab.setManaged(false);
        projectsTab.setVisible(false);
        projectsTab.setManaged(false);

        // Reset all tab button styles
        tabOverview.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #666; -fx-background-color: transparent; -fx-border-color: transparent;");
        tabCourses.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #666; -fx-background-color: transparent; -fx-border-color: transparent;");
        tabEvents.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #666; -fx-background-color: transparent; -fx-border-color: transparent;");
        tabForum.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #666; -fx-background-color: transparent; -fx-border-color: transparent;");
        tabProblems.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #666; -fx-background-color: transparent; -fx-border-color: transparent;");
        tabProjects.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #666; -fx-background-color: transparent; -fx-border-color: transparent;");

        // Show selected tab and highlight button
        tabContent.setVisible(true);
        tabContent.setManaged(true);
        tabButton.setStyle("-fx-padding: 15 25 15 25; -fx-font-size: 14; -fx-font-weight: 500; -fx-text-fill: #FF6B4A; -fx-background-color: transparent; -fx-border-color: #FF6B4A; -fx-border-width: 0 0 2 0;");
    }

    /**
     * Navigation handlers - route to other pages
     */
    @FXML
    private void handleNavHome() {
        navigateToPage("views/HomeView.fxml", "Home");
    }

    @FXML
    private void handleNavCourses() {
        navigateToPage("views/CoursesView.fxml", "Courses");
    }

    @FXML
    private void handleNavProblems() {
        navigateToPage("views/ProblemsView.fxml", "Quests");
    }

    @FXML
    private void handleNavProjects() {
        navigateToPage("views/ProjectsView.fxml", "Missions");
    }

    @FXML
    private void handleNavEvents() {
        navigateToPage("views/EventsView.fxml", "Tournaments");
    }

    @FXML
    private void handleNavForum() {
        navigateToPage("views/ForumView.fxml", "Guild");
    }

    @FXML
    private void handleNavUsers() {
        navigateToPage("views/UsersView.fxml", "Arena");
    }

    /**
     * Navigate to another page
     */
    private void navigateToPage(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) profileName.getScene().getWindow();
            NavigationManager.navigateTo(stage, fxmlPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Logout button click.
     */
    @FXML
    private void handleLogOut() {
        SessionManager.logout();
        try {
            Stage stage = (Stage) profileName.getScene().getWindow();
            NavigationManager.navigateTo(stage, "views/SignInView.fxml", "Sign In");
        } catch (Exception e) {
            System.err.println("Error navigating to Sign In: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
