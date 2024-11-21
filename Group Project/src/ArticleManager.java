
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleManager {

    public static void saveArticlesToFile(String filename, List<Articles> articles) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(articles);
        }
    }

    public static List<Articles> loadArticlesFromFile(String filename) throws IOException, ClassNotFoundException {
        List<Articles> articles;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            articles = (List<Articles>) in.readObject();

            // Ensure no null or empty fields
            for (Articles article : articles) {
                if (article.getGroup() == null || article.getGroup().isEmpty()) {
                    article.setGroup("Uncategorized");
                }
                if (article.getContentLevel() == null || article.getContentLevel().isEmpty()) {
                    article.setContentLevel("Uncategorized");
                }
            }
        }
        return articles;
    }

    
    public static List<Articles> loadArticlesSafely(String filename) {
        List<Articles> articles = new ArrayList<>();
        try {
            articles = loadArticlesFromFile(filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No backup file found or failed to load. Returning an empty list.");
        }
        return articles;
    }
}
