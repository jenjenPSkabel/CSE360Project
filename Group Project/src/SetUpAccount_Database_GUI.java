

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class SetUpAccount_Database_GUI extends Application {

    // Create the DatabaseHelper instance to manage database interactions
    private DatabaseHelper dbHelper = new DatabaseHelper();
    public String username;  // Store the username here

    public SetUpAccount_Database_GUI(String username2) {
		// TODO Auto-generated constructor stub
    	this.username = username2;
	}

	
    @Override
    public void start(Stage primaryStage) throws Exception {
        dbHelper.connectToDatabase();  // Connect to the database

        // Create labels and input fieldsz
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(100);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setPrefWidth(100);

        Label middleNameLabel = new Label("Middle Name:");
        TextField middleNameField = new TextField();
        middleNameField.setPrefWidth(100);
        
        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPrefWidth(100);
        
        Label preferredNameLabel = new Label("Preferred Name:");
        TextField preferredNameField = new TextField();
        preferredNameField.setPrefWidth(100);

        

        Button SetUpAccountButton = new Button("Set Up Account");
        Button closeButton = new Button("Close");

        // Handle button click for registration
        SetUpAccountButton.setOnAction(e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String middleName = middleNameField.getText();
            String lastName = lastNameField.getText();
            String preferredName = preferredNameField.getText();

            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                System.out.println("Email, First Name, and Last Name are required.");
                return;
            }


            try {
                
                    dbHelper.setUpAccount(username, email, firstName, middleName, lastName, preferredName);
                    System.out.println("Account set up successfully!");

                    primaryStage.close();


                    // Hide the Register button and show the Close button
                    SetUpAccountButton.setVisible(false);
                }
        catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Handle button click for closing the window
        closeButton.setOnAction(e -> {
            primaryStage.close();  // Close the window when the Close button is clicked
        });
        
        // Layout setup
        VBox vbox = new VBox(10, emailLabel, emailField, firstNameLabel, firstNameField, middleNameLabel, middleNameField, lastNameLabel, lastNameField, preferredNameLabel, preferredNameField, SetUpAccountButton, closeButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 400, 400);

        primaryStage.setTitle("SetUpAccount");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Method to show success pop-up when the user registers
    private void showSuccessPopup() {
        // Create an Alert dialog for success
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Account Set Up Successful");
        alert.setHeaderText(null);  // No header
        alert.setContentText("Account was successfully set up!");

        // Add a "Great!" button
        ButtonType buttonTypehome = new ButtonType("Go To Home Page", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypehome);

        // Show the alert and wait for user to click "Great!"
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
