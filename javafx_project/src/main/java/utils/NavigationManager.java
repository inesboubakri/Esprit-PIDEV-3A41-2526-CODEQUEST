package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

/**
 * Navigation utility class for managing scene switching in the JavaFX application.
 * 
 * This class provides convenient methods to load and display FXML files, making it easy
 * to navigate between different views/pages in the application.
 * 
 * Usage:
 * <pre>
 * NavigationManager.navigateTo(primaryStage, AppConfig.VIEW_COURSES, "Courses");
 * </pre>
 */
public class NavigationManager {

    /**
     * Navigate to a specific FXML view.
     *
     * @param stage The primary stage to display the view
     * @param fxmlPath Path to the FXML file (e.g., "views/HomeView.fxml")
     * @param title Title for the window
     * @param width Window width
     * @param height Window height
     */
    public static void navigateTo(Stage stage, String fxmlPath, String title, double width, double height) {
        try {
            Parent root = loadFXML(fxmlPath);
            Scene scene = new Scene(root, width, height);
            
            // Apply stylesheet with error handling
            try {
                URL cssResource = NavigationManager.class.getResource("/styles.css");
                if (cssResource != null) {
                    String css = cssResource.toExternalForm();
                    scene.getStylesheets().add(css);
                } else {
                    System.out.println("Warning: styles.css not found in classpath");
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not load styles.css: " + e.getMessage());
            }
            
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Navigate to a specific FXML view with default dimensions.
     *
     * @param stage The primary stage to display the view
     * @param fxmlPath Path to the FXML file
     * @param title Title for the window
     */
    public static void navigateTo(Stage stage, String fxmlPath, String title) {
        navigateTo(stage, fxmlPath, title, AppConfig.DEFAULT_WINDOW_WIDTH, AppConfig.DEFAULT_WINDOW_HEIGHT);
    }

    /**
     * Load an FXML file into a Parent node from classpath.
     *
     * @param fxmlPath Path to the FXML file (e.g., "views/HomeView.fxml")
     * @return The loaded Parent node
     * @throws IOException If the FXML file cannot be found or loaded
     */
    public static Parent loadFXML(String fxmlPath) throws IOException {
        String resource = "/" + fxmlPath;
        URL resourceUrl = NavigationManager.class.getResource(resource);
        
        // Try alternative resource loading if first attempt fails
        if (resourceUrl == null) {
            resourceUrl = ClassLoader.getSystemResource(fxmlPath);
        }
        
        // If still null, throw descriptive error
        if (resourceUrl == null) {
            throw new IOException("Cannot find FXML file in classpath: " + resource + 
                                "\nMake sure the file exists at: src/main/resources/" + fxmlPath);
        }
        
        FXMLLoader loader = new FXMLLoader(resourceUrl);
        return loader.load();
    }

    /**
     * Get an FXML loader for advanced use cases.
     *
     * @param fxmlPath Path to the FXML file (e.g., "views/HomeView.fxml")
     * @return Configured FXMLLoader
     */
    public static FXMLLoader getFXMLLoader(String fxmlPath) {
        String resource = "/" + fxmlPath;
        URL resourceUrl = NavigationManager.class.getResource(resource);
        
        // Try alternative resource loading if first attempt fails
        if (resourceUrl == null) {
            resourceUrl = ClassLoader.getSystemResource(fxmlPath);
        }
        
        if (resourceUrl != null) {
            return new FXMLLoader(resourceUrl);
        } else {
            throw new IllegalArgumentException("Cannot find FXML file in classpath: " + resource + 
                                             "\nMake sure the file exists at: src/main/resources/" + fxmlPath);
        }
    }
}
