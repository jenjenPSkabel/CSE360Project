

import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class Register_Database_GUI extends Application {

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

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPrefWidth(100);

        Label inviteCodeLabel = new Label("Invite Code:");
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPrefWidth(100);
        
        // Check if this is the first user (admin)
        boolean isFirstUser = dbHelper.isDatabaseEmpty();
        if (isFirstUser) {
            inviteCodeField.setDisable(true);  // Disable invite code field for the first user
            inviteCodeField.setText("N/A");    // Set a placeholder text for the first user
        }

        Button registerButton = new Button("Register");
        Button closeButton = new Button("Close");

        // Handle button click for registration
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Now get the invite code here when registering
            String inviteCode = inviteCodeField.getText().trim();  // Get the invite code when clicked

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match.");
                return;
            }

            String role = null;
            try {
                if (dbHelper.isDatabaseEmpty()) {
                    // First user, assign Admin role
                    role = "Admin";
                    System.out.println("First user. Assigning role: Admin");
                } else {
                    // Validate the invite code by querying the database
                    role = dbHelper.getRoleForInviteCode(inviteCode);  // Check if invite code exists and get role
                    if (role == null) {
                        System.out.println("Invalid invite code.");
                        return;
                    }
                }

                // Check if user already exists
                if (dbHelper.doesUserExist(username)) {
                    System.out.println("User already exists.");
                } else {
                    // Register the user
                    dbHelper.register(username, password, role);
                    System.out.println("User registered successfully as a " + role + "!");
                    
                    // Delete the invite code from the invite_codes table
                    if (!isFirstUser) {
                        dbHelper.deleteInviteCode(inviteCode);
                    }
                    
                    primaryStage.close();
                    showSuccessPopup(role);  // Show success popup
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Handle button click for closing the window
        closeButton.setOnAction(e -> {
            primaryStage.close();  // Close the window when the Close button is clicked
        });

        // Layout setup
        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, inviteCodeLabel, inviteCodeField, registerButton, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setTitle("Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to show success pop-up when the user registers
    private void showSuccessPopup(String role) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);  // No header
        alert.setContentText("User Successfully Registered as " + role);
        alert.getButtonTypes().setAll(new ButtonType("Great!", ButtonBar.ButtonData.OK_DONE));
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


