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
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArticlesManagerGUI extends Application {

    private TableView<Articles> articleTable;
    private ObservableList<Articles> articleList;
    private static final String FILE_PATH = "articles_backup.dat"; // File path to save articles

    @Override
    public void start(Stage stage) {
        stage.setTitle("Articles Manager");

        // Initialize article list and load from file
        articleList = FXCollections.observableArrayList(loadArticlesFromFile(FILE_PATH));
        articleTable = new TableView<>(articleList);

        // Define table columns
        TableColumn<Articles, String> headerCol = new TableColumn<>("Header(s)");
        headerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeader()));

        TableColumn<Articles, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Articles, String> authorsCol = new TableColumn<>("Author(s)");
        authorsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));

        TableColumn<Articles, String> descriptionCol = new TableColumn<>("Short Description");
        descriptionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getShortDescription()));

        articleTable.getColumns().addAll(headerCol, titleCol, authorsCol, descriptionCol);
        articleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add search bar with widened text field
        TextField searchField = new TextField();
        searchField.setPromptText("Search articles by keywords...");
        searchField.setPrefWidth(300); // Set a wider width for the search bar

        // Add "Move to Top" button next to search bar
        Button moveToTopButton = new Button("Search");
        moveToTopButton.setOnAction(e -> filterArticlesByKeywords(searchField.getText()));

        // Layout for search bar and buttons
        HBox searchBox = new HBox(10, new Label("Search:"), searchField, moveToTopButton);
        searchBox.setAlignment(Pos.CENTER);

        // Article management buttons
        Button addArticleButton = new Button("Add Article");
        Button updateArticleButton = new Button("Update Article");
        Button viewArticleButton = new Button("View Article");
        Button deleteArticleButton = new Button("Delete Article");
        Button backupButton = new Button("Backup Articles");
        Button restoreButton = new Button("Restore Articles");

        // Set button actions
        addArticleButton.setOnAction(e -> showAddArticleDialog());
        updateArticleButton.setOnAction(e -> showUpdateArticleDialog());
        viewArticleButton.setOnAction(e -> showViewArticleDialog());
        deleteArticleButton.setOnAction(e -> deleteSelectedArticle());
        backupButton.setOnAction(e -> backupArticles());
        restoreButton.setOnAction(e -> restoreArticles());

        HBox buttonBox = new HBox(10, addArticleButton, updateArticleButton, viewArticleButton, deleteArticleButton, backupButton, restoreButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, searchBox, articleTable, buttonBox);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 900, 500);
        stage.setScene(scene);
        stage.show();
    }

    // Method to filter and order articles based on search keywords
 // Method to filter and order articles based on search keywords
    private void filterArticlesByKeywords(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            // Reset to original list if the search is empty
            articleTable.setItems(articleList);
            
            // Remove any styling by resetting the row factory
            articleTable.setRowFactory(tv -> new TableRow<>());
            return;
        }

        String[] searchTerms = keywords.toLowerCase().split("\\s+"); // Split search into words

        // Separate matching articles from non-matching
        List<Articles> matchingArticles = articleList.stream()
            .filter(article -> containsAnyKeyword(article, searchTerms))
            .collect(Collectors.toList());
        
        List<Articles> nonMatchingArticles = articleList.stream()
            .filter(article -> !containsAnyKeyword(article, searchTerms))
            .collect(Collectors.toList());

        // Create a new list with matching articles at the top
        ObservableList<Articles> sortedList = FXCollections.observableArrayList();
        sortedList.addAll(matchingArticles);
        sortedList.addAll(nonMatchingArticles);

        // Set the sorted list in the table
        articleTable.setItems(sortedList);

        // Apply green highlight to matching articles
        articleTable.setRowFactory(tv -> new TableRow<Articles>() {
            @Override
            protected void updateItem(Articles article, boolean empty) {
                super.updateItem(article, empty);
                if (!empty && article != null && matchingArticles.contains(article)) {
                    setStyle("-fx-background-color: lightgreen;");
                } else {
                    setStyle("");
                }
            }
        });
    }


    // Helper method to check if an article contains any of the keywords
    private boolean containsAnyKeyword(Articles article, String[] keywords) {
        String content = (article.getTitle() + " " +
                article.getAuthors() + " " + article.getShortDescription())
                .toLowerCase();

        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }



    private void showAddArticleDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Article");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        TextField headerField = new TextField();
        TextField titleField = new TextField();
        TextField authorsField = new TextField();
        TextField shortDescriptionField = new TextField();
        TextField keywordsField = new TextField();
        TextArea bodyField = new TextArea();
        TextArea referencesField = new TextArea();
        TextArea otherInfoField = new TextArea();

        // Set preferred width for the TextField and TextArea components
        headerField.setPrefWidth(400);
        titleField.setPrefWidth(400);
        authorsField.setPrefWidth(400);
        shortDescriptionField.setPrefWidth(400);
        keywordsField.setPrefWidth(400);
        bodyField.setPrefWidth(400);
        referencesField.setPrefWidth(400);
        otherInfoField.setPrefWidth(400);

        grid.add(new Label("Header(s):"), 0, 0);
        grid.add(headerField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author(s):"), 0, 2);
        grid.add(authorsField, 1, 2);
        grid.add(new Label("Short Description:"), 0, 3);
        grid.add(shortDescriptionField, 1, 3);
        grid.add(new Label("Keywords:"), 0, 4);
        grid.add(keywordsField, 1, 4);
        grid.add(new Label("Body:"), 0, 8);
        grid.add(bodyField, 1, 8);
        grid.add(new Label("References:"), 0, 9);
        grid.add(referencesField, 1, 9);
        grid.add(new Label("Other Information:"), 0, 10);
        grid.add(otherInfoField, 1, 10);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            Articles article = new Articles(100, 200, 500, 100, 1000, 200, 200);
            article.setHeader(headerField.getText());
            article.setTitle(titleField.getText());
            article.setAuthors(authorsField.getText());
            article.setShortDescription(shortDescriptionField.getText());
            article.setKeywords(keywordsField.getText());
            article.setBody(bodyField.getText());
            article.setReferences(referencesField.getText());
            article.setOtherInfo(otherInfoField.getText());

            articleList.add(article);
            dialog.close();
        });

        grid.add(saveButton, 1, 11);

        Scene dialogScene = new Scene(grid, 600, 600);  // Set the desired width (e.g., 600)
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

        TextField headerField = new TextField(selectedArticle.getHeader());
        TextField titleField = new TextField(selectedArticle.getTitle());
        TextField authorsField = new TextField(selectedArticle.getAuthors());
        TextField shortDescriptionField = new TextField(selectedArticle.getShortDescription());
        TextField keywordsField = new TextField(selectedArticle.getKeywords());
        TextArea bodyField = new TextArea(selectedArticle.getBody());
        TextArea referencesField = new TextArea(selectedArticle.getReferences());
        TextField otherInfoField = new TextField(selectedArticle.getOtherInfo());

        // Set preferred width for input fields
        headerField.setPrefWidth(400);
        titleField.setPrefWidth(400);
        authorsField.setPrefWidth(400);
        shortDescriptionField.setPrefWidth(400);
        keywordsField.setPrefWidth(400);
        bodyField.setPrefWidth(400);
        referencesField.setPrefWidth(400);
        otherInfoField.setPrefWidth(400);

        grid.add(new Label("Header(s):"), 0, 0);
        grid.add(headerField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author(s):"), 0, 2);
        grid.add(authorsField, 1, 2);
        grid.add(new Label("Short Description:"), 0, 3);
        grid.add(shortDescriptionField, 1, 3);
        grid.add(new Label("Keywords:"), 0, 4);
        grid.add(keywordsField, 1, 4);
        grid.add(new Label("Body:"), 0, 8);
        grid.add(bodyField, 1, 8);
        grid.add(new Label("References:"), 0, 9);
        grid.add(referencesField, 1, 9);
        grid.add(new Label("Other Information:"), 0, 10);
        grid.add(otherInfoField, 1, 10);

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            selectedArticle.setHeader(headerField.getText());
            selectedArticle.setTitle(titleField.getText());
            selectedArticle.setAuthors(authorsField.getText());
            selectedArticle.setShortDescription(shortDescriptionField.getText());
            selectedArticle.setKeywords(keywordsField.getText());
            selectedArticle.setBody(bodyField.getText());
            selectedArticle.setOtherInfo(otherInfoField.getText());

            articleTable.refresh();
            dialog.close();
        });

        grid.add(saveButton, 1, 11);

        Scene dialogScene = new Scene(grid, 600, 600);  // Set the desired width (e.g., 600)
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
        alert.setHeaderText("ID: " + selectedArticle.getId() + "\n" + selectedArticle.getHeader());
        alert.setContentText(
            "Title: " + selectedArticle.getTitle() +
            "\nAuthor(s): " + selectedArticle.getAuthors() +
            "\n\nShort Description:\n" + selectedArticle.getShortDescription() +
            "\n\nKeywords:\n" + selectedArticle.getKeywords() +
            "\n\nBody:\n" + selectedArticle.getBody() +
            "\n\nReferences:\n" + selectedArticle.getReferences() +
            "\nOther Information:\n" + selectedArticle.getOtherInfo()
        );
        alert.showAndWait();
    }

    private void deleteSelectedArticle() {
        Articles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            articleList.remove(selectedArticle);
        }
    }

    private void backupArticles() {
        // Specify the backup file path
        String tempBackupPath = "tempBackup.dat";
        
        // Create or overwrite the file with new data
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempBackupPath))) {
            List<Articles> articlesToSave = new ArrayList<>(articleList); // Get all articles to save
            out.writeObject(articlesToSave); // Serialize and write the list of articles to the file
            System.out.println("Articles backed up successfully to tempBackup.dat.");
            
            showAlert(Alert.AlertType.INFORMATION, "Backup Successful", "All articles have been backed up to tempBackup.dat.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Backup Failed", "Failed to backup articles: " + e.getMessage());
        }
    }

    private void restoreArticles() {
        File backupFile = new File("tempBackup.dat");
        
        // Check if the backup file exists
        if (!backupFile.exists()) {
            showAlert(Alert.AlertType.ERROR, "Restore Failed", "No backup file found to restore from.");
            return;
        }

        // Prompt the user for the restore method
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Restore Articles");
        alert.setHeaderText("Choose Restore Option");
        alert.setContentText("Would you like to replace all existing articles or merge with the current list?");
        
        ButtonType replaceButton = new ButtonType("Replace");
        ButtonType mergeButton = new ButtonType("Merge");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(replaceButton, mergeButton, cancelButton);
        
        // Wait for user selection
        ButtonType result = alert.showAndWait().orElse(cancelButton);

        if (result == cancelButton) {
            return; // Cancel the operation
        }
        
        // Load articles from the backup file
        List<Articles> loadedArticles = loadArticlesFromFile("tempBackup.dat");
        if (loadedArticles == null) {
            showAlert(Alert.AlertType.ERROR, "Restore Failed", "Failed to load articles from the backup file.");
            return;
        }

        if (result == replaceButton) {
            // Replace existing articles with the loaded ones
            articleList.setAll(loadedArticles);
            saveArticlesToFile(loadedArticles, FILE_PATH);  // Overwrite articles_backup.dat
            showAlert(Alert.AlertType.INFORMATION, "Restore Successful", "All existing articles have been replaced with the backup.");
        } else if (result == mergeButton) {
            // Merge: Add articles only if their IDs do not match existing ones
            List<Long> existingIds = new ArrayList<>();
            for (Articles article : articleList) {
                existingIds.add(article.getId()); // Collect all current IDs
            }
            
            for (Articles article : loadedArticles) {
                if (!existingIds.contains(article.getId())) {
                    articleList.add(article); // Add only if ID is unique
                }
            }

            saveArticlesToFile(new ArrayList<>(articleList), FILE_PATH);  // Update articles_backup.dat with merged list
            showAlert(Alert.AlertType.INFORMATION, "Restore Successful", "Articles from the backup have been merged with the current list.");
        }
    }

    // Method to save articles to a specified file
    private void saveArticlesToFile(List<Articles> articles, String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(articles);
            System.out.println("Articles saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to save articles: " + e.getMessage());
        }
    }

    // Load articles from file
    private List<Articles> loadArticlesFromFile(String filePath) {
        List<Articles> articles = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            articles = (List<Articles>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No backup file found or failed to load.");
        }
        return articles;
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
