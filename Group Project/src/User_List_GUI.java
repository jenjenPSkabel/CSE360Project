import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class User_List_GUI extends Application {
 
    private TableView table = new TableView();
    private DataBaseHelper dbHelper = new DataBaseHelper();
    public static void main(String[] args) {
        launch(args);
    }
@Override
public void start(Stage stage) {
    Scene scene = new Scene(new Group());
    stage.setTitle("User List");
    stage.setWidth(300);
    stage.setHeight(500);

    final Label label = new Label("Users");
    label.setFont(new Font("Arial", 20));

    table.setEditable(true);

    // Create columns
    TableColumn<User, String> userNameCol = new TableColumn<>("Username");
    userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

    TableColumn<User, String> prefNameCol = new TableColumn<>("Preferred Name");
    prefNameCol.setCellValueFactory(new PropertyValueFactory<>("prefName"));

    TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
    firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

    TableColumn<User, String> middleNameCol = new TableColumn<>("Middle Name");
    middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));

    TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
    lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

    TableColumn<User, String> emailCol = new TableColumn<>("Email Address");
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

    TableColumn<User, String> roleCol = new TableColumn<>("User Role");
    roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

    table.getColumns().addAll(userNameCol, prefNameCol, firstNameCol, middleNameCol, lastNameCol, emailCol, roleCol);

    // Get data from DataBaseHelper
    ObservableList<User> users = FXCollections.observableArrayList();
    try {
        ArrayList<String[]> userList = dbHelper.displayUsersByAdmin();
        for (String[] userInfo : userList) {
            // Create a User object from the userInfo array
            User user = new User(
                userInfo[0], // Username
                userInfo[1], // Preferred Name
                userInfo[2], // First Name
                userInfo[3], // Middle Name
                userInfo[4], // Last Name
                userInfo[5], // Email
                userInfo[6]  // Role
            );
            users.add(user); // Add each user to the ObservableList
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Set data in the TableView
    table.setItems(users);

    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.getChildren().addAll(label, table);

    ((Group) scene.getRoot()).getChildren().addAll(vbox);

    stage.setScene(scene);
    stage.show();
}
}

