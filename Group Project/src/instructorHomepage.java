import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class instructorHomepage extends Application {
	
	private final String username;
    private final String email;

    public instructorHomepage(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Override
    public void start(Stage primaryStage) {
    	Button articlesManagerButton = new Button("Articles Manager");
    	articlesManagerButton.setOnAction(e -> openArticlesManagerWindow());
    	
    	
    	
        // Create the logout button
        Button logoutButton = new Button("Logout");

        // Set the action for the logout button
        logoutButton.setOnAction(e -> primaryStage.close());

        // Layout for the Student home page
        VBox vbox = new VBox(20, articlesManagerButton,logoutButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setTitle("insutrctor Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    private void openArticlesManagerWindow() {
        ArticlesManagerGUI articlesManagerGUI = new ArticlesManagerGUI();
        Stage articlesManagerStage = new Stage();
        try {
            articlesManagerGUI.start(articlesManagerStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
