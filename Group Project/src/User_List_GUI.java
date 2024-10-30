import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class User_List_GUI extends Application {

    private TableView<User> table = new TableView<>();
    private DatabaseHelper dbHelper = new DatabaseHelper();
    private ObservableList<User> users = FXCollections.observableArrayList();  // Updated

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("User List");
        stage.setWidth(600);
        stage.setHeight(500);

        final Label label = new Label("Users");
        label.setFont(new Font("Arial", 20));

        table.setEditable(false);

        // Create and set up columns
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

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Add columns to the table
        table.getColumns().addAll(userNameCol, prefNameCol, firstNameCol, middleNameCol, lastNameCol, emailCol, roleCol);

        // Get data from DataBaseHelper
        try {
            dbHelper.connectToDatabase();  // Ensure connection is established
            ArrayList<String[]> userList = dbHelper.displayUsersByAdmin();
            for (String[] userInfo : userList) {
                User user = new User(
                    userInfo[0], userInfo[1], userInfo[2],
                    userInfo[3], userInfo[4], userInfo[5], userInfo[6]
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(users);

        // Add context menu for row actions
        table.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(event -> {
                User selectedUser = row.getItem();
                if (selectedUser != null) {
                    users.remove(selectedUser);  // Remove from ObservableList
                    try {
                    	dbHelper.deleteUserByUsernameOrEmail(selectedUser.getUserName(), selectedUser.getEmail());  // Delete from database
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            MenuItem resetMenuItem = new MenuItem("Reset");
            resetMenuItem.setOnAction(event -> {
                User selectedUser = row.getItem();
                if (selectedUser != null) {
                    selectedUser.setPrefName("");   // Reset some fields (example)
                    selectedUser.setEmail("");      // Reset email
                    table.refresh();  // Refresh table to show updates
                    // Optionally update the database if you want to save this change
                }
            });

            contextMenu.getItems().addAll(deleteMenuItem, resetMenuItem);

            // Set context menu on row
            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );

            return row;
        });

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }
}
