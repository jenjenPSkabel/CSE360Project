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

public class ArticlesManagerGUI extends Application {

    private TableView<Articles> articleTable;
    private ObservableList<Articles> articleList;
    private static final String FILE_PATH = "articles_backup.dat"; // File path to save articles
    private List<String> groups = new ArrayList<>();
    private ComboBox<String> groupFilterComboBox; 
    private TableColumn<Articles, Number> numberCol;
    private Label contentLevelCountLabel = new Label();
    private Button viewByTempNumberButton = new Button("View by Temp Number");
private List<String> userWhitelist = List.of("admin", "stu");
    
    
    private final String username;
    private final String email;

    public ArticlesManagerGUI(String username, String email) {
        this.username = username;
        this.email = email;
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("Articles Manager");

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

        

        // Article management buttons
        Button addArticleButton = new Button("Add Article");
        Button updateArticleButton = new Button("Update Article");
        Button viewArticleButton = new Button("View Article");
        Button deleteArticleButton = new Button("Delete Article");
        Button backupButton = new Button("Backup Articles");
        Button restoreButton = new Button("Restore Articles");
        Button createGroupButton = new Button("Create Group");
        createGroupButton.setOnAction(e -> showCreateGroupDialog());
        
        // Layout for search bar and buttons
        HBox searchBox = new HBox(10, new Label("Search:"), searchField, moveToTopButton,createGroupButton);
        searchBox.setAlignment(Pos.CENTER);

        // Set button actions
        addArticleButton.setOnAction(e -> showAddArticleDialog());
        updateArticleButton.setOnAction(e -> showUpdateArticleDialog());
        viewArticleButton.setOnAction(e -> showViewArticleDialog());
        deleteArticleButton.setOnAction(e -> deleteSelectedArticle());
        backupButton.setOnAction(e -> backupArticles());
        restoreButton.setOnAction(e -> restoreArticles());

        HBox buttonBox = new HBox(10, addArticleButton, updateArticleButton, viewArticleButton, deleteArticleButton, backupButton, restoreButton);
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
        
     // Dropdown for selecting or creating a group
        ComboBox<String> groupComboBox = new ComboBox<>();
        groupComboBox.setEditable(true); // Allow users to create a new group
        groupComboBox.getItems().addAll(getAllGroups()); // Populate existing groups
        groupComboBox.setPromptText("Create a Group or Add Article to Preexisting Group");

        ComboBox<String> contentLevelComboBox = new ComboBox<>();
        contentLevelComboBox.getItems().addAll("Uncategorized", "Beginner", "Intermediate", "Advanced", "Expert");
        contentLevelComboBox.setPromptText("Select the Content Level");
        contentLevelComboBox.setValue("Uncategorized");
        
        ComboBox<String> encryptionComboBox = new ComboBox<>();
        encryptionComboBox.getItems().addAll("No", "Yes");
        encryptionComboBox.setValue("No"); // Default value
        
        grid.add(new Label("Group:"), 0, 0);
        grid.add(groupComboBox, 1, 0);
        grid.add(new Label("Content Level:"), 0, 1);
        grid.add(contentLevelComboBox, 1, 1);
        grid.add(new Label("Header(s):"), 0, 2);
        grid.add(headerField, 1, 2);
        grid.add(new Label("Title:"), 0, 3);
        grid.add(titleField, 1, 3);
        grid.add(new Label("Author(s):"), 0, 4);
        grid.add(authorsField, 1, 4);
        grid.add(new Label("Short Description/Abstract:"), 0, 5);
        grid.add(shortDescriptionField, 1, 5);
        grid.add(new Label("Keywords:"), 0, 6);
        grid.add(keywordsField, 1, 6);
        grid.add(new Label("Body:"), 0, 7);
        grid.add(bodyField, 1, 7);
        grid.add(new Label("References:"), 0, 8);
        grid.add(referencesField, 1, 8);
        grid.add(new Label("Other Information:"), 0, 9);
        grid.add(otherInfoField, 1, 9);
        grid.add(new Label("Does This Article Need to Be Encrypted?"), 0, 10);
        grid.add(encryptionComboBox, 1, 10);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            
        	
        	
        	String selectedGroup = groupComboBox.getValue();
        	String selectedContentLevel = contentLevelComboBox.getValue();
        	
            if (selectedGroup == null || selectedGroup.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Group", "Please select or create a group.");
                return;
            }

            Articles article = new Articles(100, 200, 500, 100, 1000, 200, 200);
            article.setGroup(selectedGroup.trim());
            article.setContentLevel(selectedContentLevel.trim()); 
            article.setHeader(headerField.getText());
            article.setTitle(titleField.getText());
            article.setAuthors(authorsField.getText());
            article.setShortDescription(shortDescriptionField.getText());
            article.setKeywords(keywordsField.getText());
            article.setBody(bodyField.getText());
            article.setReferences(referencesField.getText());
            article.setOtherInfo(otherInfoField.getText());
            
            boolean needsEncryption = "Yes".equals(encryptionComboBox.getValue());
            article.setNeedsEncryption(needsEncryption);
            if (needsEncryption) {
                // Generate the encryption key dynamically based on the header
                String derivedKey = article.generateKeyFromBase(article.getHeader());
                article.setEncryptedBody(bodyField.getText(), derivedKey);
            }


            articleList.add(article);
            saveArticles();
            updateGroups();
            groupFilterComboBox.getItems().clear();
            groupFilterComboBox.getItems().add("All Groups");
            groupFilterComboBox.getItems().addAll(getAllGroups());
            groupFilterComboBox.setValue("All Groups"); // Set default value
            
            articleTable.refresh();
            
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

     // Dropdown for selecting or creating a group
        ComboBox<String> groupComboBox = new ComboBox<>();
        groupComboBox.setEditable(true); // Allow users to create a new group
        groupComboBox.getItems().addAll(getAllGroups()); // Populate existing groups
        groupComboBox.setPromptText("Create a Group or Add Article to Preexisting Group");
        groupComboBox.setValue(selectedArticle.getGroup());

        ComboBox<String> contentLevelComboBox = new ComboBox<>();
        contentLevelComboBox.getItems().addAll("Uncategorized", "Beginner", "Intermediate", "Advanced", "Expert");
        contentLevelComboBox.setPromptText("Select the Content Level");
        contentLevelComboBox.setValue(selectedArticle.getContentLevel());
        
        ComboBox<String> encryptionComboBox = new ComboBox<>();
        encryptionComboBox.getItems().addAll("No", "Yes");
        encryptionComboBox.setValue("No"); // Default value
        
        grid.add(new Label("Group:"), 0, 0);
        grid.add(groupComboBox, 1, 0);
        grid.add(new Label("Content Level:"), 0, 1);
        grid.add(contentLevelComboBox, 1, 1);
        grid.add(new Label("Header(s):"), 0, 2);
        grid.add(headerField, 1, 2);
        grid.add(new Label("Title:"), 0, 3);
        grid.add(titleField, 1, 3);
        grid.add(new Label("Author(s):"), 0, 4);
        grid.add(authorsField, 1, 4);
        grid.add(new Label("Short Description/Abstract:"), 0, 5);
        grid.add(shortDescriptionField, 1, 5);
        grid.add(new Label("Keywords:"), 0, 6);
        grid.add(keywordsField, 1, 6);
        grid.add(new Label("Body:"), 0, 7);
        grid.add(bodyField, 1, 7);
        grid.add(new Label("References:"), 0, 8);
        grid.add(referencesField, 1, 8);
        grid.add(new Label("Other Information:"), 0, 9);
        grid.add(otherInfoField, 1, 9);
        grid.add(new Label("Does This Article Need to Be Encrypted?"), 0, 10);
        grid.add(encryptionComboBox, 1, 10);

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
        	
        	String selectedGroup = groupComboBox.getValue();
        	String selectedContentLevel = contentLevelComboBox.getValue();
        	
            if (selectedGroup == null || selectedGroup.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Group", "Please select or create a group.");
                return;
            }
            selectedArticle.setGroup(selectedGroup.trim());
            selectedArticle.setContentLevel(selectedContentLevel.trim()); 
            selectedArticle.setHeader(headerField.getText());
            selectedArticle.setTitle(titleField.getText());
            selectedArticle.setAuthors(authorsField.getText());
            selectedArticle.setShortDescription(shortDescriptionField.getText());
            selectedArticle.setKeywords(keywordsField.getText());
            selectedArticle.setBody(bodyField.getText());
            selectedArticle.setOtherInfo(otherInfoField.getText());
            
            boolean needsEncryption = "Yes".equals(encryptionComboBox.getValue());
            selectedArticle.setNeedsEncryption(needsEncryption);
            if (needsEncryption) {
                // Generate the encryption key dynamically based on the header
                String derivedKey = selectedArticle.generateKeyFromBase(selectedArticle.getHeader());
                selectedArticle.setEncryptedBody(bodyField.getText(), derivedKey);
            }



            articleTable.refresh();
            updateGroups();
            groupFilterComboBox.getItems().clear();
            groupFilterComboBox.getItems().add("All Groups");
            groupFilterComboBox.getItems().addAll(getAllGroups());
            groupFilterComboBox.setValue("All Groups"); // Set default value
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

        // Debug: Check if user is in whitelist
        System.out.println("Whitelist: " + userWhitelist);
        System.out.println("Current user: " + username);

        
     	String key = selectedArticle.generateKeyFromBase(selectedArticle.getHeader());
    	String bodyContent = selectedArticle.getBodyForUser(username, userWhitelist, key);

        // Debug: Verify decrypted content
        try {
            EncryptionHelper helper = new EncryptionHelper();

            String decrypted = new String(
                helper.safeDecrypt(
                    selectedArticle.getEncryptedBody(key).getBytes(StandardCharsets.UTF_8),
                    key.getBytes(StandardCharsets.UTF_8)
                ),
                StandardCharsets.UTF_8
            );
            System.out.println("Decrypted body (independent check): " + decrypted); // Debug
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during independent decryption check.");
        }

        // Display article content
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
            "\n\nBody:\n" + bodyContent +
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
                	
                	String key = article.generateKeyFromBase(article.getHeader());
                	String bodyContent = article.getBodyForUser(username, userWhitelist, key);



                    // Debug: Verify decrypted content
                    try {
                        EncryptionHelper helper = new EncryptionHelper();
                        String decrypted = new String(
                            helper.safeDecrypt(
                                article.getEncryptedBody(key).getBytes(StandardCharsets.UTF_8),
                                key.getBytes(StandardCharsets.UTF_8)
                            ),
                            StandardCharsets.UTF_8
                        );
                        System.out.println("Decrypted body (independent check): " + decrypted); // Debug
                    } catch (Exception a) {
                        a.printStackTrace();
                        System.out.println("Error during independent decryption check.");
                    }

                    
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


    private void deleteSelectedArticle() {
        Articles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle != null) {
            articleList.remove(selectedArticle);
            saveArticles();
            updateGroups();
            groupFilterComboBox.getItems().clear();
            groupFilterComboBox.getItems().add("All Groups");
            groupFilterComboBox.getItems().addAll(getAllGroups());
            groupFilterComboBox.setValue("All Groups"); // Set default value
            
            articleTable.refresh();
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
            updateGroups();
            groupFilterComboBox.getItems().clear();
            groupFilterComboBox.getItems().add("All Groups");
            groupFilterComboBox.getItems().addAll(getAllGroups());
            groupFilterComboBox.setValue("All Groups"); // Set default value
            articleTable.refresh();
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
            updateGroups();
            groupFilterComboBox.getItems().clear();
            groupFilterComboBox.getItems().add("All Groups");
            groupFilterComboBox.getItems().addAll(getAllGroups());
            groupFilterComboBox.setValue("All Groups"); // Set default value
            articleTable.refresh();
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
    	List<String> allGroups = new ArrayList<>();

        // Add existing groups from the `groups` list
        allGroups.addAll(groups);
        
        // Add general groups
        generalGroups.stream()
            .map(GroupManager::getGroupName)
            .forEach(allGroups::add);

        // Add specialized groups with an asterisk
        specializedGroups.stream()
            .map(group -> "*" + group.getGroupName())
            .forEach(allGroups::add);

        return allGroups;
    }



//    private void updateGroups() {
//        groups.clear();
//        groups.addAll(articleList.stream()
//            .map(Articles::getGroup) // Get the group from each article
//            .filter(group -> group != null && !group.isEmpty()) // Exclude null or empty groups
//            .distinct() // Ensure uniqueness
//            .collect(Collectors.toList()));
//    }
    
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
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to save groups: " + e.getMessage());
        }
    }


    private void showCreateGroupDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Create Group");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter group name");

        ComboBox<String> groupTypeComboBox = new ComboBox<>();
        groupTypeComboBox.getItems().addAll("General", "Specialized");
        groupTypeComboBox.setPromptText("Select group type");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            String groupType = groupTypeComboBox.getValue();

            if (groupName.isEmpty() || groupType == null) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a group name and select a type.");
                return;
            }

            if ("Specialized".equals(groupType)) {
                // Create specialized group
                String currentUser = "instructor_username"; // Replace with actual logic
                GroupManager newGroup = new GroupManager(groupName, currentUser);
                specializedGroups.add(newGroup);
            } else {
                // Create general group
                GroupManager newGroup = new GroupManager(groupName, "general");
                if (!generalGroups.stream().anyMatch(g -> g.getGroupName().equals(groupName))) {
                    generalGroups.add(newGroup); // Avoid duplicates
                }
            }

            updateGroups();
            refreshGroupFilterComboBox();
            dialog.close();
        });

        grid.add(new Label("Group Name:"), 0, 0);
        grid.add(groupNameField, 1, 0);
        grid.add(new Label("Group Type:"), 0, 1);
        grid.add(groupTypeComboBox, 1, 1);
        grid.add(createButton, 1, 2);

        Scene scene = new Scene(grid, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }






    
    private void showManageUsersDialog(GroupManager group) {
        Stage dialog = new Stage();
        dialog.setTitle("Manage Users: " + group.getGroupName());

        ListView<String> userListView = new ListView<>();
        userListView.setItems(FXCollections.observableArrayList(group.getUsers()));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username to add");

        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                group.addUser(username);
                userListView.getItems().add(username);
            }
        });

        Button removeUserButton = new Button("Remove User");
        removeUserButton.setOnAction(e -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                group.removeUser(selectedUser);
                userListView.getItems().remove(selectedUser);
            }
        });

        VBox layout = new VBox(10, new Label("Users in Group:"), userListView, usernameField, addUserButton, removeUserButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private List<GroupManager> specializedGroups = new ArrayList<>();

    private void updateGroups() {
        groups.clear();

        // Add groups from articles
        groups.addAll(articleList.stream()
            .map(Articles::getGroup) // Get the group from each article
            .filter(group -> group != null && !group.isEmpty()) // Exclude null or empty groups
            .distinct() // Ensure uniqueness
            .collect(Collectors.toList()));

        // Add general groups
        generalGroups.stream()
            .map(GroupManager::getGroupName)
            .distinct()
            .forEach(groups::add);

        // Add specialized groups with an asterisk
        specializedGroups.stream()
            .map(group -> "*" + group.getGroupName())
            .distinct()
            .forEach(groups::add);

        // Save updated groups to file
        saveGroupsToFile("groups_backup.dat");

        // Debugging output
        System.out.println("Updated Groups: " + groups);
    }

    private List<GroupManager> generalGroups = new ArrayList<>();






    
    private void refreshGroupFilterComboBox() {
        groupFilterComboBox.getItems().clear();
        groupFilterComboBox.getItems().add("All Groups"); // Add default option

        // Add groups to the dropdown
        groups.forEach(groupFilterComboBox.getItems()::add);

        groupFilterComboBox.setValue("All Groups"); // Reset to default
    }









    public static void main(String[] args) {
        launch(args);
    }
}
