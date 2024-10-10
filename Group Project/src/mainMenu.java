import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

public class mainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create buttons for Login and Register
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        // Handle button click for "Register"
        registerButton.setOnAction(e -> {
            // Launch the Register GUI
            Register_Database_GUI registerGUI = new Register_Database_GUI();
            try {
                registerGUI.start(new Stage());  // Open the Register GUI in a new window
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Handle button click for "Login" (you can implement this similarly to Register)
        loginButton.setOnAction(e -> {
            // Launch the Login GUI (which needs to be implemented)
            Login_Database_GUI loginGUI = new Login_Database_GUI();  // You will need to create this class
            try {
                loginGUI.start(new Stage());  // Open the Login GUI in a new window
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button closeButton = new Button("Close"); // New close button
        // Handle button click for "Close"
        closeButton.setOnAction(e -> {
            // Close the entire application
            Platform.exit();
        });
        
        // Create a layout and add buttons
        VBox vbox = new VBox(10, loginButton, registerButton,closeButton);
        vbox.setAlignment(Pos.CENTER);

        // Create and set the scene
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}