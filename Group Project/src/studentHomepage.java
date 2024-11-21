import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class studentHomepage extends Application {
	private final String username;
    private final String email;

    public studentHomepage(String username, String email) {
        this.username = username;
        this.email = email;
    }
    @Override
    public void start(Stage primaryStage) {
        // Create the logout button
        Button logoutButton = new Button("Logout");

        // Create the "Article Viewer" button for the normal article viewer
        Button manageArticlesButton = new Button("Normal Article Viewer");

        // Create the "Help Article Viewer" button
        Button helpArticleViewerButton = new Button("Help Article Viewer");

        // Set the action for the logout button
        logoutButton.setOnAction(e -> primaryStage.close());

        // Set the action for the "Normal Article Viewer" button
        manageArticlesButton.setOnAction(e -> {
            ArticleStudentGUI articleGUI = new ArticleStudentGUI();
            try {
                articleGUI.start(new Stage()); // Open the ArticleStudentGUI in a new window
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Set the action for the "Help Article Viewer" button
        helpArticleViewerButton.setOnAction(e -> {
            HelpArticleViewer helpArticleViewer = new HelpArticleViewer(username, email);
            try {
                helpArticleViewer.start(new Stage()); // Open the HelpArticleViewer in a new window
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Layout for the Student home page
        VBox vbox = new VBox(20, manageArticlesButton, helpArticleViewerButton, logoutButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 250);

        primaryStage.setTitle("Student Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
