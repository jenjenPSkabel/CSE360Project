import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

public class ArticleStudentGUI extends Application {

    private TableView<Articles> articleTable;
    private ObservableList<Articles> articleList;
    private static final String FILE_PATH = "articles_backup.dat"; // File path to save articles
    private List<String> groups = new ArrayList<>();
    private ComboBox<String> groupFilterComboBox; 
    private TableColumn<Articles, Number> numberCol;
    private Label contentLevelCountLabel = new Label();
    private Button viewByTempNumberButton = new Button("View by Temp Number");


    @Override
    public void start(Stage stage) {
        stage.setTitle("Article Viewer");

        // Initialize article list and load from file
        articleList = FXCollections.observableArrayList(loadArticlesFromFile(FILE_PATH));
        articleTable = new TableView<>(articleList);
        
        updateGroups(); // Update the groups based on the articles


        // Define table columns
        TableColumn<Articles, String> headerCol = new TableColumn<>("Header(s)");
        headerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeader()));

        TableColumn<Articles, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Articles, String> authorsCol = new TableColumn<>("Author(s)");
        authorsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));

        TableColumn<Articles, String> descriptionCol = new TableColumn<>("Short Description/Abstract");
        descriptionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getShortDescription()));
        
        
        
     // Add a new column for numbering
        numberCol = new TableColumn<>("#");
        numberCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTempNumber()));
        numberCol.setVisible(false); // Initially hidden
        articleTable.getColumns().add(0, numberCol); // Add the column to the left of others
        
        

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
        Button viewArticleButton = new Button("View Article");

        

        // Set button actions

        viewArticleButton.setOnAction(e -> showViewArticleDialog());


        HBox buttonBox = new HBox(10, viewArticleButton);
        buttonBox.setAlignment(Pos.CENTER);
        
     // Create the button for viewing by temp number
        viewByTempNumberButton.setVisible(false); // Initially hidden
        viewByTempNumberButton.setOnAction(e -> showViewByTempNumberDialog());

     // Add group filter dropdown
        groupFilterComboBox = new ComboBox<>();
        groupFilterComboBox.getItems().add("All Groups");
        groupFilterComboBox.getItems().addAll(getAllGroups());
        groupFilterComboBox.setValue("All Groups"); // Default value
        
        
        ComboBox<String> contentLevelFilterComboBox = new ComboBox<>();
        contentLevelFilterComboBox.getItems().addAll("All Levels", "Beginner", "Intermediate", "Advanced", "Expert", "Uncategorized");
        contentLevelFilterComboBox.setValue("All Levels");
        contentLevelFilterComboBox.setOnAction(e -> { filterArticlesByGroupAndLevel(
            groupFilterComboBox.getValue(),
            contentLevelFilterComboBox.getValue()
        		);
        viewByTempNumberButton.setVisible(false); // Hide the button when filters change
        });
        
        groupFilterComboBox.setOnAction(e -> { filterArticlesByGroupAndLevel(
                groupFilterComboBox.getValue(),
                contentLevelFilterComboBox.getValue()
            );
        viewByTempNumberButton.setVisible(false); 
        });

        VBox layout = new VBox(10, searchBox, contentLevelCountLabel, groupFilterComboBox, contentLevelFilterComboBox, articleTable, viewByTempNumberButton, buttonBox);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 900, 500);
        stage.setScene(scene);
        stage.show();
    }

    // Method to filter and order articles based on search keywords
 // Method to filter and order articles based on search keywords
    private void filterArticlesByKeywords(String keywords) {
        // Get the current group and content level filters
        String selectedGroup = groupFilterComboBox.getValue();
        String selectedContentLevel = ((ComboBox<String>) ((VBox) groupFilterComboBox.getParent()).getChildren().get(3)).getValue();

        ObservableList<Articles> currentList = FXCollections.observableArrayList(
            articleList.stream()
                .filter(article -> "All Groups".equals(selectedGroup) || selectedGroup.equals(article.getGroup()))
                .filter(article -> "All Levels".equals(selectedContentLevel) || selectedContentLevel.equals(article.getContentLevel()))
                .collect(Collectors.toList())
        );

        if (keywords == null || keywords.trim().isEmpty()) {
            // Reset to the filtered list if the search is empty
            articleTable.setItems(currentList);
            numberCol.setVisible(false); // Hide numbering column
            contentLevelCountLabel.setText("");
            viewByTempNumberButton.setVisible(false); 
            articleTable.setRowFactory(tv -> new TableRow<>()); // Remove row styling
            return;
        }

        String[] searchTerms = keywords.toLowerCase().split("\\s+"); // Split search into words

        // Filter articles based on keywords
        List<Articles> matchingArticles = currentList.stream()
            .filter(article -> containsAnyKeyword(article, searchTerms))
            .collect(Collectors.toList());

        // Assign temporary numbers to the matching articles
        for (int i = 0; i < matchingArticles.size(); i++) {
            matchingArticles.get(i).setTempNumber(i + 1); // Start numbering from 1
        }

        // Display the matching articles in the table
        ObservableList<Articles> sortedList = FXCollections.observableArrayList(matchingArticles);
        articleTable.setItems(sortedList);
        numberCol.setVisible(true); // Show numbering column
        
     // Calculate counts for each content level
        String counts = matchingArticles.stream()
            .collect(Collectors.groupingBy(Articles::getContentLevel, Collectors.counting()))
            .entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));

        contentLevelCountLabel.setText("Content Level Counts: " + counts); // Display counts
        viewByTempNumberButton.setVisible(true); 

       
    }




    // Helper method to check if an article contains any of the keywords
    private boolean containsAnyKeyword(Articles article, String[] keywords) {
        String content = (article.getTitle() + " " + article.getId() + " " +
                article.getAuthors() + " " + article.getShortDescription() + " " + article.getKeywords())
                .toLowerCase();

        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
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
            "Group: " + selectedArticle.getGroup() +
            "\nContent Level: " + selectedArticle.getContentLevel() +
            "\nTitle: " + selectedArticle.getTitle() +
            "\nAuthor(s): " + selectedArticle.getAuthors() +
            "\n\nShort Description/Abstract:\n" + selectedArticle.getShortDescription() +
            "\n\nKeywords:\n" + selectedArticle.getKeywords() +
            "\n\nBody:\n" + selectedArticle.getBody() +
            "\n\nReferences:\n" + selectedArticle.getReferences() +
            "\nOther Information:\n" + selectedArticle.getOtherInfo()
        );
        alert.showAndWait();
    }
    
    private void showViewByTempNumberDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("View by Temporary Number");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        Label instructionLabel = new Label("Enter Temporary Number:");
        TextField tempNumberField = new TextField();
        Button viewButton = new Button("View Article");

        grid.add(instructionLabel, 0, 0);
        grid.add(tempNumberField, 1, 0);
        grid.add(viewButton, 1, 1);

        viewButton.setOnAction(e -> {
            try {
                int tempNumber = Integer.parseInt(tempNumberField.getText().trim());
                Articles article = articleList.stream()
                    .filter(a -> a.getTempNumber() == tempNumber)
                    .findFirst()
                    .orElse(null);

                if (article == null) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Number", "No article found with the given temporary number.");
                } else {
                	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("View Article");
                    alert.setHeaderText("ID: " + article.getId() + "\n" + article.getHeader());
                    alert.setContentText(
                        "Group: " + article.getGroup() +
                        "\nContent Level: " + article.getContentLevel() +
                        "\nTitle: " + article.getTitle() +
                        "\nAuthor(s): " + article.getAuthors() +
                        "\n\nShort Description/Abstract:\n" + article.getShortDescription() +
                        "\n\nKeywords:\n" + article.getKeywords() +
                        "\n\nBody:\n" + article.getBody() +
                        "\n\nReferences:\n" + article.getReferences() +
                        "\nOther Information:\n" + article.getOtherInfo()
                    );
                    alert.showAndWait();
                    dialog.close();
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid number.");
            }
        });

        Scene dialogScene = new Scene(grid, 400, 150);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
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
    
    private void saveArticles() {
        try {
            ArticleManager.saveArticlesToFile(FILE_PATH, new ArrayList<>(articleList));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to save articles: " + e.getMessage());
        }
    }

    // Load articles from file
    private List<Articles> loadArticlesFromFile(String filePath) {
        return ArticleManager.loadArticlesSafely(filePath);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    private List<String> getAllGroups() {
        return new ArrayList<>(groups);
    }

    private void updateGroups() {
        groups.clear();
        groups.addAll(articleList.stream()
            .map(Articles::getGroup) // Get the group from each article
            .filter(group -> group != null && !group.isEmpty()) // Exclude null or empty groups
            .distinct() // Ensure uniqueness
            .collect(Collectors.toList()));
    }
    
    private void filterArticlesByGroupAndLevel(String group, String contentLevel) {
        
    	// Reset temporary numbers for all articles
        for (Articles article : articleList) {
            article.setTempNumber(0); // Set temporary numbers to null
        }
    	
    	ObservableList<Articles> filteredList = FXCollections.observableArrayList(
            articleList.stream()
                .filter(article -> ("All Groups".equals(group) || group.equals(article.getGroup())))
                .filter(article -> ("All Levels".equals(contentLevel) || contentLevel.equals(article.getContentLevel())))
                .collect(Collectors.toList())
        );
        articleTable.setItems(filteredList);
        numberCol.setVisible(false); // Hide numbering column when filters are applied
        contentLevelCountLabel.setText("");
    }


    
    private void saveGroupsToFile(String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(groups);
            System.out.println("Groups saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
