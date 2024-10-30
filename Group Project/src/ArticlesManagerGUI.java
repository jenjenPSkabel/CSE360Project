import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ArticlesManagerGUI extends Application {

    private TableView<Articles> articleTable;
    private ObservableList<Articles> articleList;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Articles Manager");

        // Initialize article list and table
        articleList = FXCollections.observableArrayList(new ArrayList<>());
        articleTable = new TableView<>(articleList);

        // Define table columns
        TableColumn<Articles, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Articles, String> authorsCol = new TableColumn<>("Authors");
        authorsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));

        articleTable.getColumns().addAll(titleCol, authorsCol);
        articleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Buttons for article management
        Button addArticleButton = new Button("Add Article");
        Button updateArticleButton = new Button("Update Article");
        Button viewArticleButton = new Button("View Article");
        Button deleteArticleButton = new Button("Delete Article");

        // Set button actions
        addArticleButton.setOnAction(e -> showAddArticleDialog());
        updateArticleButton.setOnAction(e -> showUpdateArticleDialog());
        viewArticleButton.setOnAction(e -> showViewArticleDialog());
        deleteArticleButton.setOnAction(e -> deleteSelectedArticle());

        // Layout for buttons
        HBox buttonBox = new HBox(10, addArticleButton, updateArticleButton, viewArticleButton, deleteArticleButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, articleTable, buttonBox);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAddArticleDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Article");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        TextField titleField = new TextField();
        TextField authorsField = new TextField();
        TextArea abstractField = new TextArea();

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Authors:"), 0, 1);
        grid.add(authorsField, 1, 1);
        grid.add(new Label("Abstract:"), 0, 2);
        grid.add(abstractField, 1, 2);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            Articles article = new Articles(100, 200, 500, 100, 1000, 200);
            article.setTitle(titleField.getText());
            article.setAuthors(authorsField.getText());
            article.setAbstractText(abstractField.getText());
            articleList.add(article);
            dialog.close();
        });

        grid.add(saveButton, 1, 3);

        Scene dialogScene = new Scene(grid, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showUpdateArticleDialog() {
        Articles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an article to update.");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Update Article");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        TextField titleField = new TextField(selectedArticle.getTitle());
        TextField authorsField = new TextField(selectedArticle.getAuthors());
        TextArea abstractField = new TextArea(selectedArticle.getAbstractText());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Authors:"), 0, 1);
        grid.add(authorsField, 1, 1);
        grid.add(new Label("Abstract:"), 0, 2);
        grid.add(abstractField, 1, 2);

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            selectedArticle.setTitle(titleField.getText());
            selectedArticle.setAuthors(authorsField.getText());
            selectedArticle.setAbstractText(abstractField.getText());
            articleTable.refresh();
            dialog.close();
        });

        grid.add(saveButton, 1, 3);

        Scene dialogScene = new Scene(grid, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showViewArticleDialog() {
        Articles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an article to view.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("View Article");
        alert.setHeaderText(selectedArticle.getTitle());
        alert.setContentText("Authors: " + selectedArticle.getAuthors() + "\n\nAbstract:\n" + selectedArticle.getAbstractText());
        alert.showAndWait();
    }

    private void deleteSelectedArticle() {
        Articles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            articleList.remove(selectedArticle);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
