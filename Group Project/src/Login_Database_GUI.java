import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Login_Database_GUI extends Application {

    // Create the DatabaseHelper instance to manage database interactions
    private DatabaseHelper dbHelper = new DatabaseHelper();

    @Override
    public void start(Stage primaryStage) throws Exception {
        dbHelper.connectToDatabase();  // Connect to the database

        // Create labels and input fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(100);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(100);

        Button loginButton = new Button("Login");
        Button closeButton = new Button("Close");

        // Handle button click for login
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            try {
                if (dbHelper.doesUserExist(username) && dbHelper.checkPassword(username, password)) {
                    System.out.println("Login successful!");

                    // Show success popup
                    showSuccessPopup();

                    // Pass the username to the SetUpAccount_Database_GUI
                    SetUpAccount_Database_GUI setUpAccountGUI = new SetUpAccount_Database_GUI(username);
                    setUpAccountGUI.start(new Stage());  // Open the SetUpAccount window

                    primaryStage.close();  // Close the window on successful login
                } else {
                    System.out.println("Invalid username or password.");
                    showErrorPopup();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Handle button click for closing the window
        closeButton.setOnAction(e -> primaryStage.close());

        // Layout setup
        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 250);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to show success pop-up when the user logs in
    private void showSuccessPopup() {
        // Create an Alert dialog for success
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Successful");
        alert.setHeaderText(null);  // No header
        alert.setContentText("You have successfully logged in!");

        // Add a "Great!" button
        ButtonType buttonTypeGreat = new ButtonType("Great!", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeGreat);

        // Show the alert and wait for user to click "Great!"
        alert.showAndWait();
    }

    // Method to show error pop-up when login fails
    private void showErrorPopup() {
        // Create an Alert dialog for error
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);  // No header
        alert.setContentText("Invalid username or password!");

        // Add an "OK" button
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeOk);

        // Show the alert and wait for user to click "OK"
        alert.showAndWait();
    }

}
