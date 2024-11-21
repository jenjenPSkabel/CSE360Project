import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HelpArticleManager {

    public static void saveHelpArticlesToFile(String filename, List<HelpArticles> articles) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            // Convert ObservableList to standard ArrayList before saving
            out.writeObject(new ArrayList<>(articles));
        }
    }

    public static List<HelpArticles> loadHelpArticlesFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            // Read the serialized list and return
            return (List<HelpArticles>) in.readObject();
        }
    }

    public static List<HelpArticles> loadHelpArticlesSafely(String filename) {
        List<HelpArticles> articles = new ArrayList<>();
        try {
            articles = loadHelpArticlesFromFile(filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No backup file found or failed to load. Returning an empty list.");
        }
        return articles;
    }
}
