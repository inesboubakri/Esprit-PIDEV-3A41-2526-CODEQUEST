package controllers;

import dao.ProjetDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Projet;
import utils.NavigationManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Projects view (user-facing).
 * Dynamically builds project cards from the database.
 */
public class ProjectsController implements Initializable {

    @FXML
    private HBox navbar;

    @FXML
    private FlowPane projectsFlowPane;

    private ProjetDAO projetDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projetDAO = new ProjetDAO();
        loadProjects();
    }

    /**
     * Load all projects from database and display as cards
     */
    private void loadProjects() {
        try {
            System.out.println("[ProjectsController] Starting loadProjects()...");
            List<Projet> projectsList = projetDAO.getAll();
            System.out.println("[ProjectsController] Retrieved " + projectsList.size() + " projects from DAO");
            
            projectsFlowPane.getChildren().clear();

            if (projectsList == null || projectsList.isEmpty()) {
                Label emptyMsg = new Label("📭 No projects found in the database");
                emptyMsg.setStyle("-fx-font-size: 18; -fx-text-fill: #999; -fx-padding: 40;");
                projectsFlowPane.getChildren().add(emptyMsg);
                System.out.println("[ProjectsController] ⚠️  No projects in database");
            } else {
                for (Projet projet : projectsList) {
                    VBox card = createProjectCard(projet);
                    projectsFlowPane.getChildren().add(card);
                    System.out.println("[ProjectsController] Added card for: " + projet.getTitre());
                }
                System.out.println("[ProjectsController] ✓ Loaded " + projectsList.size() + " projects into FlowPane");
            }
        } catch (Exception e) {
            System.err.println("[ProjectsController] ✗ Error loading projects: " + e.getMessage());
            e.printStackTrace();
            
            Label errorMsg = new Label("❌ Error loading projects: " + e.getMessage());
            errorMsg.setStyle("-fx-font-size: 14; -fx-text-fill: #C62828; -fx-padding: 40; -fx-wrap-text: true;");
            projectsFlowPane.getChildren().add(errorMsg);
        }
    }

    /**
     * Create a styled VBox card for a single project
     */
    private VBox createProjectCard(Projet projet) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                      "-fx-background-radius: 10; -fx-min-width: 300; -fx-max-width: 300;" +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

        // Title
        Label title = new Label(projet.getTitre() != null ? projet.getTitre() : "Untitled");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333;");
        title.setWrapText(true);

        // Description
        Label description = new Label(projet.getDescription() != null ? projet.getDescription() : "No description");
        description.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        description.setWrapText(true);
        description.setMaxWidth(260);

        // Level badge
        String niveau = projet.getNiveau() != null ? projet.getNiveau() : "Unknown";
        Label levelBadge = new Label(niveau);
        String badgeColor = switch (niveau.toLowerCase()) {
            case "avancé", "advanced"       -> "-fx-background-color: #FFCCCC; -fx-text-fill: #C62828;";
            case "intermédiaire", "intermediate" -> "-fx-background-color: #FFE0B2; -fx-text-fill: #E65100;";
            default                          -> "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;";
        };
        levelBadge.setStyle(badgeColor + "-fx-padding: 4 12 4 12; -fx-background-radius: 15; " +
                            "-fx-font-size: 11; -fx-font-weight: bold;");

        HBox badges = new HBox(8);
        badges.getChildren().add(levelBadge);

        // Points
        Label pts = new Label("⭐ " + projet.getPts() + " pts");
        pts.setStyle("-fx-font-size: 11; -fx-text-fill: #FFB800;");

        // Dates
        String dateText = "📅 ";
        dateText += projet.getDate_debut()  != null ? projet.getDate_debut().toString()  : "?";
        dateText += "  →  ";
        dateText += projet.getDate_limite() != null ? projet.getDate_limite().toString() : "?";
        Label dates = new Label(dateText);
        dates.setStyle("-fx-font-size: 11; -fx-text-fill: #999;");

        // Progress bar (empty by default, can be wired to depot_projet later)
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        // Button
        Button startBtn = new Button("Start Project");
        startBtn.setStyle("-fx-background-color: #FF6B4A; -fx-text-fill: white; " +
                          "-fx-padding: 10 20 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        startBtn.setOnAction(e -> System.out.println("Starting project: " + projet.getTitre()));

        card.getChildren().addAll(title, description, badges, progressBar, pts, dates, startBtn);
        return card;
    }

    /**
     * Helper method to get Stage from any FXML node
     */
    private Stage getStage() {
        return (Stage) navbar.getScene().getWindow();
    }

    // Navigation handlers
    @FXML
    private void handleNavHome() {
        NavigationManager.navigateTo(getStage(), "views/HomeView.fxml", "Home");
    }

    @FXML
    private void handleNavCourses() {
        NavigationManager.navigateTo(getStage(), "views/CoursesView.fxml", "Courses");
    }

    @FXML
    private void handleNavProblems() {
        NavigationManager.navigateTo(getStage(), "views/ProblemsView.fxml", "Problems");
    }

    @FXML
    private void handleNavProjects() {
        // Already on projects view, no action needed
    }

    @FXML
    private void handleNavEvents() {
        NavigationManager.navigateTo(getStage(), "views/EventsView.fxml", "Events");
    }

    @FXML
    private void handleNavForum() {
        NavigationManager.navigateTo(getStage(), "views/ForumView.fxml", "Forum");
    }

    @FXML
    private void handleNavUsers() {
        NavigationManager.navigateTo(getStage(), "views/UsersView.fxml", "Users");
    }

    @FXML
    private void handleNavProfile() {
        NavigationManager.navigateTo(getStage(), "views/ProfileView.fxml", "Profile");
    }

    @FXML
    private void handleLogOut() {
        NavigationManager.navigateTo(getStage(), "views/SignInView.fxml", "Sign In");
    }

    // Legacy placeholder methods (kept for FXML compatibility if referenced)
    @FXML
    private void handleStartProject1() {
        // Handled by dynamic card button
    }

    @FXML
    private void handleStartProject2() {
        // Handled by dynamic card button
    }

    @FXML
    private void handleContinueProject() {
        // Handled by dynamic card button
    }
}
