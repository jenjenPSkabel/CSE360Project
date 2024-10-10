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

import java.sql.SQLException;
import java.util.ArrayList;

public class User_List_GUI extends Application {

    // Updated: Specify generic type for TableView
    private TableView<User> table = new TableView<>();
    private DatabaseHelper dbHelper = new DatabaseHelper();

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

        // Create columns for TableView (omitted for brevity)

        // Get data from DataBaseHelper
        ObservableList<User> users = FXCollections.observableArrayList();
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

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }

}

