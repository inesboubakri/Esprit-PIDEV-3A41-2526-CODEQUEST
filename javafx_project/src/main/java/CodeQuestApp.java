import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main JavaFX Application for CodeQuest.
 * 
 * This application demonstrates a complete JavaFX implementation of the CodeQuest
 * web template, featuring scene builder-compatible FXML layouts and comprehensive styling.
 */
public class CodeQuestApp extends Application {

    private static final String APP_TITLE = "CodeQuest - Learn Programming Through Gaming";
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load Sign In view as the entry point
            Parent root = FXMLLoader.load(getClass().getResource("views/DashbordView.fxml"));
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Set minimum window size
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading SignInView.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
