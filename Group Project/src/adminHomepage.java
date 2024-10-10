import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class adminHomepage extends Application {

    // Create the DatabaseHelper instance to manage database interactions
    private DatabaseHelper dbHelper = new DatabaseHelper();
    
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        dbHelper.connectToDatabase();  // Connect to the database

        // Create a button to create an invite code
        Button createInviteButton = new Button("Create Invite Code");
        Button showUsersButton = new Button("Show Users");
        Button logoutButton = new Button("Logout");

        // Set button action to open a new invite code creation window
        createInviteButton.setOnAction(e -> openCreateInviteCodeWindow());
        
        // Set action for the Show Users button to open the User_List_GUI window
        showUsersButton.setOnAction(e -> openUserListWindow());

        // Layout for the Admin home page
        VBox vbox = new VBox(20, createInviteButton, showUsersButton, logoutButton);  // Add the new button here
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setTitle("Admin Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        logoutButton.setOnAction(e -> {
            primaryStage.close();  // Close the window when the Close button is clicked
        });

    }

    // Method to open the Create Invite Code window
    private void openCreateInviteCodeWindow() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Create Invite Code");

        // Create labels, text field, and dropdown for email and role
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(200);

        Label roleLabel = new Label("Role:");

        // Dropdown menu for roles
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Instructor", "Admin", "Multi-role");
        roleComboBox.setPrefWidth(150);

        // Submit button
        Button submitButton = new Button("Submit");

        // Set action for the Submit button
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String selectedRole = roleComboBox.getValue();

            // Check if email or role is empty
            if (email.isEmpty() || selectedRole == null) {
                showAlert(AlertType.ERROR, "Error", "All fields are required.");
                return;
            }

            // Determine the invite code based on the selected role
            String inviteCode;
            switch (selectedRole) {
                case "Student":
                    inviteCode = "STUDENT_CODE";
                    break;
                case "Instructor":
                    inviteCode = "INSTRUCTOR_CODE";
                    break;
                case "Admin":
                    inviteCode = "ADMIN_CODE";
                    break;
                case "Multi-role":
                    inviteCode = "MULTI_ROLE_CODE";
                    break;
                default:
                    inviteCode = "";  // Should not happen
            }

            // Call the inviteUser method from DatabaseHelper
            try {
                dbHelper.inviteUser(email, inviteCode);
                showAlert(AlertType.INFORMATION, "Success", "Invite code sent to " + email);
                popupStage.close();  // Close the popup window
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Failed to create invite code.");
            }
        });

        // Layout for the popup
        VBox popupVBox = new VBox(10, emailLabel, emailField, roleLabel, roleComboBox, submitButton);
        popupVBox.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupVBox, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();  // Wait for the popup to close before returning
    }

    // Method to show alert dialogs
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
 // Method to open the User List window
    private void openUserListWindow() {
        User_List_GUI userListGUI = new User_List_GUI();
        Stage userListStage = new Stage();
        try {
            userListGUI.start(userListStage);  // Start the User_List_GUI window
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
