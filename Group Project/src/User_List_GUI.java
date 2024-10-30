import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;

public class User_List_GUI extends Application {

    private TableView<User> table = new TableView<>();
    private DatabaseHelper dbHelper = new DatabaseHelper();
    private ObservableList<User> users = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("User List");
        stage.setWidth(1400);
        stage.setHeight(600);

        final Label label = new Label("Users");
        label.setFont(new Font("Arial", 20));

        table.setEditable(false);
        table.setPrefWidth(1100);

        // Define columns
        TableColumn<User, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userNameCol.setPrefWidth(150);

        TableColumn<User, String> prefNameCol = new TableColumn<>("Preferred Name");
        prefNameCol.setCellValueFactory(new PropertyValueFactory<>("prefName"));
        prefNameCol.setPrefWidth(150);

        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(150);

        TableColumn<User, String> middleNameCol = new TableColumn<>("Middle Name");
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        middleNameCol.setPrefWidth(150);

        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setPrefWidth(150);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(150);

        // Add columns to the table
        table.getColumns().addAll(userNameCol, prefNameCol, firstNameCol, middleNameCol, lastNameCol, emailCol, roleCol);

        // Load data from DatabaseHelper
        try {
            dbHelper.connectToDatabase();
            ArrayList<String[]> userList = dbHelper.displayUsersByAdmin();
            for (String[] userInfo : userList) {
                User user = new User(
                    userInfo[0],    // userName
                    userInfo[1],    // prefName
                    userInfo[2],    // firstName
                    userInfo[3],    // middleName
                    userInfo[4],    // lastName
                    userInfo[5],    // email
                    userInfo[6],    // role
                    Boolean.parseBoolean(userInfo[7])  // isMultirole, converted to boolean
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(users);

        // Create Reset, Delete, and Edit Roles buttons
        Button resetButton = new Button("Reset");
        Button deleteButton = new Button("Delete");
        Button editRolesButton = new Button("Edit Roles");

        // Disable buttons until a row is selected
        resetButton.setDisable(true);
        deleteButton.setDisable(true);
        editRolesButton.setDisable(true);

        // Enable buttons only when a user is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;
            resetButton.setDisable(!selected);
            deleteButton.setDisable(!selected);
            editRolesButton.setDisable(!selected);
        });

        // Button actions
        resetButton.setOnAction(event -> {
            User selectedUser = table.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                confirmAndResetUserAccount(selectedUser);
            }
        });

        deleteButton.setOnAction(event -> {
            User selectedUser = table.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                confirmAndDeleteUserAccount(selectedUser);
            }
        });

        editRolesButton.setOnAction(event -> {
            User selectedUser = table.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                openEditRolesPopup(selectedUser);
            }
        });

        VBox tableBox = new VBox(10, label, table);
        tableBox.setPadding(new Insets(10, 0, 0, 10));

        VBox actionBox = new VBox(15, resetButton, deleteButton, editRolesButton);
        actionBox.setAlignment(Pos.TOP_CENTER);
        actionBox.setPadding(new Insets(50, 20, 0, 20));

        HBox mainLayout = new HBox(10, tableBox, actionBox);

        ((Group) scene.getRoot()).getChildren().add(mainLayout);

        stage.setScene(scene);
        stage.show();
    }

    private void openEditRolesPopup(User user) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Edit Roles for " + user.getUserName());

        Label emailLabel = new Label("User: " + user.getEmail());
        CheckBox studentCheckBox = new CheckBox("Student");
        CheckBox instructorCheckBox = new CheckBox("Instructor");
        CheckBox adminCheckBox = new CheckBox("Admin");

        // Set the checkboxes based on the user's current roles
        List<String> currentRoles = List.of(user.getRole().split(","));
        studentCheckBox.setSelected(currentRoles.contains("Student"));
        instructorCheckBox.setSelected(currentRoles.contains("Instructor"));
        adminCheckBox.setSelected(currentRoles.contains("Admin"));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            List<String> selectedRoles = new ArrayList<>();
            if (studentCheckBox.isSelected()) selectedRoles.add("Student");
            if (instructorCheckBox.isSelected()) selectedRoles.add("Instructor");
            if (adminCheckBox.isSelected()) selectedRoles.add("Admin");

            try {
                dbHelper.updateUserRoles(user.getEmail(), selectedRoles);
                user.setRole(String.join(",", selectedRoles));  // Update role in the table view
                table.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Roles updated successfully.");
                popupStage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update roles.");
            }
        });

        VBox popupVBox = new VBox(10, emailLabel, studentCheckBox, instructorCheckBox, adminCheckBox, saveButton);
        popupVBox.setPadding(new Insets(10, 10, 10, 10));
        popupVBox.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupVBox, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    private void confirmAndResetUserAccount(User user) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Password Reset");
        confirmationAlert.setContentText("Are you sure you want to reset the password for user: " + user.getUserName() + "?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                resetUserAccount(user);
            }
        });
    }

    private void resetUserAccount(User user) {
        try {
            dbHelper.resetPassword(user.getUserName());
            showAlert(Alert.AlertType.INFORMATION, "Account Reset", "Account for " + user.getUserName() + " has been reset with a new one-time password.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to reset account for " + user.getUserName() + ".");
        }
    }

    private void confirmAndDeleteUserAccount(User user) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Account Deletion");
        confirmationAlert.setContentText("Are you sure you want to delete the account for user: " + user.getUserName() + "?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteUserAccount(user);
            }
        });
    }

    private void deleteUserAccount(User user) {
        try {
            dbHelper.deleteUserByUsernameOrEmail(user.getUserName(), user.getEmail());
            users.remove(user);
            showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Account for " + user.getUserName() + " has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete account for " + user.getUserName() + ".");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
