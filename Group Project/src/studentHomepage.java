import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class studentHomepage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the logout button
        Button logoutButton = new Button("Logout");

        // Set the action for the logout button
        logoutButton.setOnAction(e -> primaryStage.close());

        // Layout for the Student home page
        VBox vbox = new VBox(20, logoutButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setTitle("Student Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
