import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class HelpArticleViewer extends Application {

    private TableView<HelpArticles> articleTable;
    private ObservableList<HelpArticles> articleList;
    private static final String FILE_PATH = "help_articles_backup.dat"; // File path to save help articles

    private String username;
    private String email;

    // Constructor to pass username and email
    public HelpArticleViewer(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Help Article Viewer");

        articleList = FXCollections.observableArrayList();
        loadHelpArticles();

        // Initialize article list and load from file
        articleList = FXCollections.observableArrayList(HelpArticleManager.loadHelpArticlesSafely(FILE_PATH));
        articleTable = new TableView<>(articleList);

        // Define table columns
        TableColumn<HelpArticles, String> headerCol = new TableColumn<>("Header");
        headerCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeader()));

        TableColumn<HelpArticles, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<HelpArticles, String> authorsCol = new TableColumn<>("Author");
        authorsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));

        TableColumn<HelpArticles, String> abstractCol = new TableColumn<>("Abstract");
        abstractCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAbstractText()));

        articleTable.getColumns().addAll(headerCol, titleCol, authorsCol, abstractCol);
        articleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search articles by keywords...");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> filterArticlesByKeywords(searchField.getText()));

        HBox searchBox = new HBox(10, new Label("Search:"), searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        // Buttons at the bottom
        Button viewArticleButton = new Button("View Article");
        Button addArticleButton = new Button("Add Article");
        Button messageHelpButton = new Button("Message Help System");
        messageHelpButton.setOnAction(e -> showMessageHelpPopup());

        viewArticleButton.setOnAction(e -> showViewArticleDialog());
//        addArticleButton.setOnAction(e -> showAddArticleDialog());

        HBox buttonBox = new HBox(20, viewArticleButton, addArticleButton, messageHelpButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Layout
        VBox layout = new VBox(15, new Label("Help Article Viewer"), searchBox, articleTable, buttonBox);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    // Filter articles by keywords
    private void filterArticlesByKeywords(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            articleTable.setItems(articleList);
            return;
        }

        String[] searchTerms = keywords.toLowerCase().split("\\s+");

        List<HelpArticles> filtered = articleList.stream()
                .filter(article -> containsAnyKeyword(article, searchTerms))
                .toList();

        articleTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private boolean containsAnyKeyword(HelpArticles article, String[] keywords) {
        String content = (article.getHeader() + " " + article.getTitle() + " " +
                article.getAuthors() + " " + article.getAbstractText()).toLowerCase();

        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Show dialog for viewing an article
    private void showViewArticleDialog() {
        HelpArticles selectedArticle = articleTable.getSelectionModel().getSelectedItem();
        if (selectedArticle == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an article to view.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("View Article");
        alert.setHeaderText(selectedArticle.getTitle());
        alert.setContentText(
                "Header: " + selectedArticle.getHeader() + "\n" +
                "Author(s): " + selectedArticle.getAuthors() + "\n\n" +
                "Abstract:\n" + selectedArticle.getAbstractText()
        );
        alert.showAndWait();
    }

    private void showMessageHelpPopup() {
        Stage dialog = new Stage();
        dialog.setTitle("Message Help System");

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton genericButton = new RadioButton("Generic");
        genericButton.setToggleGroup(typeGroup);
        genericButton.setSelected(true);

        RadioButton specificButton = new RadioButton("Specific");
        specificButton.setToggleGroup(typeGroup);

        TextField messageHeaderField = new TextField();
        messageHeaderField.setPromptText("Enter Message Header");

        TextArea messageBodyField = new TextArea();
        messageBodyField.setPromptText("Enter Message Body");
        messageBodyField.setWrapText(true);

        Button sendButton = new Button("Send Message");
        sendButton.setOnAction(e -> {
            String messageType = genericButton.isSelected() ? "Generic" : "Specific";
            String messageHeader = messageHeaderField.getText().trim();
            String messageBody = messageBodyField.getText().trim();

            if (messageHeader.isEmpty() || messageBody.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "All fields must be filled.");
                return;
            }

            HelpMessage message = new HelpMessage(username, email, messageType, messageHeader, messageBody);

            // Save the message to the file
            List<HelpMessage> messages = HelpMessageManager.loadMessagesSafely("help_messages.dat");
            messages.add(message);
            try {
                HelpMessageManager.saveMessagesToFile("help_messages.dat", messages);
                showAlert(Alert.AlertType.INFORMATION, "Message Sent", "Your message has been sent successfully.");
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to save your message.");
            }

            dialog.close();
        });

        VBox layout = new VBox(10,
                new Label("Message Type:"),
                new HBox(10, genericButton, specificButton),
                new Label("Message Header:"), messageHeaderField,
                new Label("Message Body:"), messageBodyField,
                sendButton
        );
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Load help articles from file
    private void loadHelpArticles() {
        List<HelpArticles> loadedArticles = HelpArticleManager.loadHelpArticlesSafely("help_articles_backup.dat");
        articleList.setAll(loadedArticles); // Update the ObservableList
    }

    private void saveHelpArticles() {
        try {
            HelpArticleManager.saveHelpArticlesToFile("help_articles_backup.dat", new ArrayList<>(articleList));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to save help articles: " + e.getMessage());
        }
    }

    // Show alerts
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
