import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class Login_Database_GUI extends Application {
    private DatabaseHelper dbHelper = new DatabaseHelper();
    private String currentUserRole;
    private String currentUserEmail;  // To store the logged-in user's email
    private String currentusername;
    private boolean usedResetPassword = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        dbHelper.connectToDatabase();

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(100);
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(100);
        Button loginButton = new Button("Login");
        Button closeButton = new Button("Close");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            try {
                // Check if user exists and validate password or one-time reset
                if (dbHelper.doesUserExist(username) && dbHelper.checkPassword(username, password)) {
                    System.out.println("Login successful!");

                    // Set usedResetPassword if login was done via one-time reset
                    if (dbHelper.validateOneTimeReset(username, password)) {
                        usedResetPassword = true;
                    }

                    // Check if the profile is complete
                    if (dbHelper.isProfileComplete(username)) {
                        currentUserEmail = username;  // Store the user's email
                        currentusername = username;
                        currentUserRole = dbHelper.getUserRole(username);
                        System.out.println("User logged in with role: " + currentUserRole);
                        
                        // If the user logged in with a one-time reset password, prompt for a new password
                        if (usedResetPassword) {
                            showResetPasswordPopup(username);
                        } else {
                            showSuccessPopup();
                            primaryStage.close();
                        }
                    } else {
                        System.out.println("Profile is incomplete! Redirecting to Set Up Account.");
                        SetUpAccount_Database_GUI setUpAccountGUI = new SetUpAccount_Database_GUI(username);
                        setUpAccountGUI.start(new Stage());
                        primaryStage.close();
                    }

                } else {
                    System.out.println("Invalid username or password.");
                    showErrorPopup();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        closeButton.setOnAction(e -> primaryStage.close());

        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, closeButton);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.showAndWait();
    }

    // Method to retrieve the role after login
    public String getRole() {
        return currentUserRole;
    }

    // Method to determine if the user has multiple roles
    public boolean isMultirole() {
        try {
            return dbHelper.isUserMultirole(currentusername);  // Check multi-role status for current user
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if there's an issue retrieving the information
        }
    }

    // Pop-up to prompt for password reset if login was via one-time reset
    private void showResetPasswordPopup(String username) {
        Stage resetStage = new Stage();
        resetStage.initModality(Modality.APPLICATION_MODAL);
        resetStage.setTitle("Reset Password");

        Label newPasswordLabel = new Label("Enter New Password:");
        PasswordField newPasswordField = new PasswordField();
        Button resetButton = new Button("Reset Password");

        resetButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            if (!newPassword.isEmpty()) {
                try {
                    dbHelper.finalizePasswordReset(username, newPassword);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password has been reset successfully. Please log in again.", ButtonType.OK);
                    alert.showAndWait();
                    resetStage.close();
                    System.out.println("Password reset successfully for user: " + username);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to reset password. Please try again.", ButtonType.OK);
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "New password cannot be empty.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        VBox resetVbox = new VBox(10, newPasswordLabel, newPasswordField, resetButton);
        resetVbox.setAlignment(Pos.CENTER);
        Scene resetScene = new Scene(resetVbox, 300, 200);
        resetStage.setScene(resetScene);
        resetStage.showAndWait();
    }

    // Method to show success pop-up when the user logs in
    private void showSuccessPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Successful");
        alert.setHeaderText(null);
        alert.setContentText("You have successfully logged in!");
        ButtonType buttonTypeGreat = new ButtonType("Great!", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeGreat);
        alert.showAndWait();
    }

    // Method to show error pop-up when login fails
    private void showErrorPopup() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText("Invalid username or password!");
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeOk);
        alert.showAndWait();
    }
}
