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

    // Constructor to accept the comma-separated roles string
    public multiRoleHomepage(String rolesString) {
        this.rolesString = rolesString;
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
            adminButton.setOnAction(e -> openAdminHomepage());
            vbox.getChildren().add(adminButton);
        }
        if (roles.contains("Student")) {
            Button studentButton = new Button("Switch to Student");
            studentButton.setOnAction(e -> openStudentHomepage());
            vbox.getChildren().add(studentButton);
        }
        if (roles.contains("Instructor")) {
            Button instructorButton = new Button("Switch to Instructor");
            instructorButton.setOnAction(e -> openInstructorHomepage());
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

    private void openAdminHomepage() {
        adminHomepage adminPage = new adminHomepage();
        Stage adminStage = new Stage();
        try {
            adminPage.start(adminStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openStudentHomepage() {
        studentHomepage studentPage = new studentHomepage();
        Stage studentStage = new Stage();
        try {
            studentPage.start(studentStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openInstructorHomepage() {
        instructorHomepage instructorPage = new instructorHomepage();
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
