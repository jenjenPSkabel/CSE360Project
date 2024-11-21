import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class multiRoleHomepage extends Application {
    private String rolesString;

    private final String username;
    private final String email;


    // Constructor to accept the comma-separated roles string
    public multiRoleHomepage(String rolesString,String username, String email) {
        this.rolesString = rolesString;
        this.username = username;
        this.email = email;
        
    }

    @Override
    public void start(Stage primaryStage) {
        // Parse the roles string into a list of roles
        List<String> roles = Arrays.asList(rolesString.split(","));

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);

        // Add buttons dynamically based on the roles parsed
        if (roles.contains("Admin")) {
            Button adminButton = new Button("Switch to Admin");
            adminButton.setOnAction(e -> openAdminHomepage(username, email));
            vbox.getChildren().add(adminButton);
        }
        if (roles.contains("Student")) {
            Button studentButton = new Button("Switch to Student");
            studentButton.setOnAction(e -> openStudentHomepage(username, email));
            vbox.getChildren().add(studentButton);
        }
        if (roles.contains("Instructor")) {
            Button instructorButton = new Button("Switch to Instructor");
            instructorButton.setOnAction(e -> openInstructorHomepage(username, email));
            vbox.getChildren().add(instructorButton);
        }

        // Logout button to close the multi-role window
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> primaryStage.close());
        vbox.getChildren().add(logoutButton);

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setTitle("Multi-Role Home Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openAdminHomepage(String username, String email) {
        adminHomepage adminPage = new adminHomepage(username, email);
        Stage adminStage = new Stage();
        try {
            adminPage.start(adminStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openStudentHomepage(String username, String email) {
        studentHomepage studentPage = new studentHomepage(username, email);
        Stage studentStage = new Stage();
        try {
            studentPage.start(studentStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openInstructorHomepage(String username, String email) {
        instructorHomepage instructorPage = new instructorHomepage(username, email);
        Stage instructorStage = new Stage();
        try {
            instructorPage.start(instructorStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
