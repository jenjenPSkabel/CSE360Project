import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HomePageGUI extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Create button for invite code creation
        Button createInviteButton = new Button("Create Invite Code");
        createInviteButton.setOnAction(e -> showInvitePopup());

        // Layout for the homepage
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().add(createInviteButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Homepage");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to display the invite code creation pop-up
    private void showInvitePopup() {
        // New window (Stage)
        Stage popupWindow = new Stage();
        popupWindow.initModality(Modality.APPLICATION_MODAL);  // Blocks other windows until closed
        popupWindow.setTitle("Create Invite Code");

        // Textbox for email input
        TextField emailInput = new TextField();
        emailInput.setPromptText("Enter email");

        // Dropdown for selecting role
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Student", "Instructor", "Admin", "Multi-role");
        roleDropdown.setPromptText("Select Role");

        // Create invite button inside the popup
        Button submitButton = new Button("Generate Code");
        submitButton.setOnAction(e -> {
            String email = emailInput.getText();
            String role = roleDropdown.getValue();
            if (email.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter an email and select a role.");
            } else {
                // Logic for generating the invite code can go here
                System.out.println("Email: " + email + ", Role: " + role);
                popupWindow.close();  // Close the popup after submission
            }
        });

        // Layout for the popup window
        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(20));
        popupLayout.getChildren().addAll(new Label("Enter Email:"), emailInput,
                new Label("Select Role:"), roleDropdown, submitButton);

        Scene popupScene = new Scene(popupLayout, 300, 200);
        popupWindow.setScene(popupScene);
        popupWindow.showAndWait();  // Blocks other windows until this one is closed
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

