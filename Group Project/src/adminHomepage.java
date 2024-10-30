import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;

public class adminHomepage extends Application {

    private DatabaseHelper dbHelper = new DatabaseHelper();

    @Override
    public void start(Stage primaryStage) throws Exception {
        dbHelper.connectToDatabase();  // Connect to the database

        // Create buttons
        Button createInviteButton = new Button("Create Invite Code");
        Button showUsersButton = new Button("Show Users");
        Button articlesManagerButton = new Button("Articles Manager"); // New button for Articles Manager
        Button logoutButton = new Button("Logout");

        // Set button actions
        createInviteButton.setOnAction(e -> openCreateInviteCodeWindow());
        showUsersButton.setOnAction(e -> openUserListWindow());
        articlesManagerButton.setOnAction(e -> openArticlesManagerWindow()); // Open Articles Manager window
        logoutButton.setOnAction(e -> primaryStage.close());

        // Layout for the Admin home page
        VBox vbox = new VBox(20, createInviteButton, showUsersButton, articlesManagerButton, logoutButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 250);

        primaryStage.setTitle("Admin Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openCreateInviteCodeWindow() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Create Invite Code");

        // Create labels, text field, and checkboxes for email and roles
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPrefWidth(200);

        Label roleLabel = new Label("Role:");
        CheckBox studentCheckBox = new CheckBox("Student");
        CheckBox instructorCheckBox = new CheckBox("Instructor");
        CheckBox adminCheckBox = new CheckBox("Admin");

        Button submitButton = new Button("Submit");
        
        // Set action for the Submit button
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            List<String> selectedRoles = new ArrayList<>();
            if (studentCheckBox.isSelected()) selectedRoles.add("Student");
            if (instructorCheckBox.isSelected()) selectedRoles.add("Instructor");
            if (adminCheckBox.isSelected()) selectedRoles.add("Admin");

            if (email.isEmpty() || selectedRoles.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
                return;
            }

            // Generate invite code based on selected roles
            String inviteCode = String.join("_", selectedRoles).toUpperCase() + "_CODE";

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
        VBox popupVBox = new VBox(10, emailLabel, emailField, roleLabel, studentCheckBox, instructorCheckBox, adminCheckBox, submitButton);
        popupVBox.setPadding(new Insets(10, 0, 0, 0));
        popupVBox.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupVBox, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();  // Wait for the popup to close before returning
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openUserListWindow() {
        User_List_GUI userListGUI = new User_List_GUI();
        Stage userListStage = new Stage();
        try {
            userListGUI.start(userListStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // New method to open the Articles Manager window
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
