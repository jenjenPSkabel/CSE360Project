import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

        // Layout for the Admin home page
        VBox vbox = new VBox(20, createInviteButton, showUsersButton, articlesManagerButton, logoutButton);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 250);

        primaryStage.setTitle("Admin Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();

        logoutButton.setOnAction(e -> primaryStage.close());
    }

    private void openCreateInviteCodeWindow() {
        // ... (Your existing implementation for creating an invite code)
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
