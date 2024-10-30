
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelpSystemApp extends Application {

    private ObservableList<Articles> articleList;
    private TableView<Articles> articleTable;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Help System");

        articleList = FXCollections.observableArrayList();
        articleTable = new TableView<>(articleList);
        articleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Articles, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Articles, String> authorCol = new TableColumn<>("Authors");
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));

        articleTable.getColumns().addAll(titleCol, authorCol);

        Button addArticleButton = new Button("Add Article");
        addArticleButton.setOnAction(e -> showAddArticleDialog());

        Button deleteArticleButton = new Button("Delete Article");
        deleteArticleButton.setOnAction(e -> deleteSelectedArticle());

        Button backupButton = new Button("Backup Articles");
        backupButton.setOnAction(e -> backupArticles());

        Button restoreButton = new Button("Restore Articles");
        restoreButton.setOnAction(e -> restoreArticles());

        VBox vbox = new VBox(10, articleTable, addArticleButton, deleteArticleButton, backupButton, restoreButton);
        vbox.setPadding(new Insets(10));
        
        primaryStage.setScene(new Scene(vbox, 600, 400));
        primaryStage.show();
    }

    private void showAddArticleDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Article");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextArea abstractField = new TextArea();
        abstractField.setPromptText("Abstract...");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Authors:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Abstract:"), 0, 2);
        grid.add(abstractField, 1, 2);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            Articles article = new Articles(100, 200, 500, 100, 1000, 200, 200);
            article.setTitle(titleField.getText());
            article.setAuthors(authorField.getText());
//            article.setAbstractText(abstractField.getText());
            articleList.add(article);
            dialog.close();
        });

        grid.add(saveButton, 1, 3);

        dialog.setScene(new Scene(grid, 400, 300));
        dialog.showAndWait();
    }

    private void deleteSelectedArticle() {
        Articles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            articleList.remove(selectedArticle);
        }
    }

    private void backupArticles() {
        try {
            ArticleManager.saveArticlesToFile("articles_backup.dat", new ArrayList<>(articleList));
            showAlert("Backup Successful", "Articles backed up successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to backup articles.");
        }
    }

    private void restoreArticles() {
        try {
            List<Articles> loadedArticles = ArticleManager.loadArticlesFromFile("articles_backup.dat");
            articleList.setAll(loadedArticles);
            showAlert("Restore Successful", "Articles restored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to restore articles.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
