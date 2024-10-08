package main.java.edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class Register_Database_GUI extends Application {

    // Create the DatabaseHelper instance to manage database interactions
    private DatabaseHelper dbHelper = new DatabaseHelper();

    @Override
    public void start(Stage primaryStage) throws Exception {
        dbHelper.connectToDatabase();  // Connect to the database

        // Create labels and input fields
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(100);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(100);

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPrefWidth(100);

        Label roleLabel = new Label("Role:");
        
        // Use ComboBox for role selection
        ComboBox<String> roleComboBox = new ComboBox<>();
        
        // Check if the database is empty
        if (dbHelper.isDatabaseEmpty()) {
            // First user must be Admin
            roleComboBox.getItems().add("Admin");
            roleComboBox.setValue("Admin");  // Automatically select "Admin"
            roleComboBox.setDisable(true);  // Disable dropdown to prevent changing
        } else {
            // For subsequent users, show all roles
            roleComboBox.getItems().addAll("Admin", "Instructor", "Student");
        }
        
        roleComboBox.setPrefWidth(100);

        Button registerButton = new Button("Register");
        Button closeButton = new Button("Close");

        // Handle button click for registration
        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String role = roleComboBox.getValue();  // Get selected role

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
                System.out.println("All fields are required.");
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match.");
                return;
            }

            try {
                if (dbHelper.doesUserExist(email)) {
                    System.out.println("User already exists.");
                } else {
                    dbHelper.register(email, password, role);
                    System.out.println("User registered successfully!");

                    primaryStage.close();
                    // Display the popup dialog
                    showSuccessPopup(role);

                    // Hide the Register button and show the Close button
                    registerButton.setVisible(false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Handle button click for closing the window
        closeButton.setOnAction(e -> {
            primaryStage.close();  // Close the window when the Close button is clicked
        });
        
        // Layout setup
        VBox vbox = new VBox(10, emailLabel, emailField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, roleLabel, roleComboBox, registerButton, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 400, 400);

        primaryStage.setTitle("Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Method to show success pop-up when the user registers
    private void showSuccessPopup(String role) {
        // Create an Alert dialog for success
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);  // No header
        alert.setContentText("User Successfully Registered as " + role);

        // Add a "Great!" button
        ButtonType buttonTypeGreat = new ButtonType("Great!", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeGreat);

        // Show the alert and wait for user to click "Great!"
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
