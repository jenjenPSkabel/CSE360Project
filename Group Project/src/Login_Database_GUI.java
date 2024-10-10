import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
public class Login_Database_GUI extends Application {
   // Create the DatabaseHelper instance to manage database interactions
   private DatabaseHelper dbHelper = new DatabaseHelper();
  
   private String currentUserRole;  // To store the logged-in user's role
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

    	            // Check if the user's profile is complete
    	            if (dbHelper.isProfileComplete(username)) {
    	                System.out.println("Profile is complete!");

    	                // Fetch the role from the database and store it
    	                currentUserRole = dbHelper.getUserRole(username);
    	                System.out.println("User logged in with role: " + currentUserRole);

    	                primaryStage.close();  // Close the login window
    	            } else {
    	                System.out.println("Profile is incomplete! Redirecting to Set Up Account.");

    	                // If profile is incomplete, direct them to SetUpAccount_Database_GUI
    	                SetUpAccount_Database_GUI setUpAccountGUI = new SetUpAccount_Database_GUI(username);
    	                setUpAccountGUI.start(new Stage());  // Open the SetUpAccount window
    	                primaryStage.close();  // Close the login window
    	            }

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
       // Set the stage modality to block other windows until login is completed
       primaryStage.initModality(Modality.APPLICATION_MODAL);
       primaryStage.showAndWait();  // Show and block until window is closed
   }
  
// Method to retrieve the role after login
   public String getRole() {
       return currentUserRole;  // Return the stored user role
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

