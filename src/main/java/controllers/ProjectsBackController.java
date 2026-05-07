package controllers;

import dao.ProjetDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Projet;
import utils.NavigationManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Projects Management view (admin).
 */
public class ProjectsBackController implements Initializable {

    @FXML
    private VBox sidebarContent;

    @FXML
    private TableView<Projet> projectsTable;

    private ProjetDAO projetDAO;
    private ObservableList<Projet> projectsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projetDAO = new ProjetDAO();
        projectsList = FXCollections.observableArrayList();

        // Setup table columns
        setupTableColumns();

        // Load projects from database
        loadProjects();
    }

    /**
     * Setup TableView columns with property bindings
     */
    private void setupTableColumns() {
        TableColumn<Projet, Integer> idCol = new TableColumn<>("Project ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(110);

        TableColumn<Projet, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        titleCol.setPrefWidth(200);

        TableColumn<Projet, String> niveauCol = new TableColumn<>("Level");
        niveauCol.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        niveauCol.setPrefWidth(120);

        TableColumn<Projet, String> taskCol = new TableColumn<>("Tasks");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("taches"));
        taskCol.setPrefWidth(110);

        TableColumn<Projet, LocalDate> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("date_limite"));
        deadlineCol.setPrefWidth(140);

        TableColumn<Projet, LocalDate> createdCol = new TableColumn<>("Created Date");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        createdCol.setPrefWidth(130);

        projectsTable.getColumns().clear();
        projectsTable.getColumns().addAll(idCol, titleCol, niveauCol, taskCol, deadlineCol, createdCol);
    }

    /**
     * Load all projects from database and populate TableView
     */
    private void loadProjects() {
        try {
            System.out.println("[ProjectsBackController] Starting loadProjects()...");
            List<Projet> projets = projetDAO.getAll();
            System.out.println("[ProjectsBackController] Retrieved " + projets.size() + " projects from DAO");
            
            projectsList.clear();
            projectsList.addAll(projets);
            projectsTable.setItems(projectsList);
            
            System.out.println("[ProjectsBackController] ✓ Loaded " + projectsList.size() + " projects into TableView");
        } catch (Exception e) {
            System.err.println("[ProjectsBackController] ✗ Error loading projects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to get Stage from any FXML node
     */
    private Stage getStage() {
        return (Stage) sidebarContent.getScene().getWindow();
    }

    // Sidebar navigation handlers
    @FXML
    private void handleNavDashboard() {
        NavigationManager.navigateTo(getStage(), "views/DashboardView.fxml", "Dashboard");
    }

    @FXML
    private void handleNavUsers() {
        NavigationManager.navigateTo(getStage(), "views/UsersBackView.fxml", "Users - Admin");
    }

    @FXML
    private void handleNavCourses() {
        NavigationManager.navigateTo(getStage(), "views/CoursesBackView.fxml", "Courses - Admin");
    }

    @FXML
    private void handleNavForum() {
        NavigationManager.navigateTo(getStage(), "views/ForumBackView.fxml", "Forum - Admin");
    }

    @FXML
    private void handleNavProblems() {
        NavigationManager.navigateTo(getStage(), "views/ProblemsBackView.fxml", "Problems - Admin");
    }

    @FXML
    private void handleNavProjects() {
        // Already on projects view, no action needed
    }

    @FXML
    private void handleNavEvents() {
        NavigationManager.navigateTo(getStage(), "views/EventsBackView.fxml", "Events - Admin");
    }

    @FXML
    private void handleThemeToggle() {
        // TODO: Implement theme toggle functionality
        System.out.println("Theme toggle clicked");
    }
}
