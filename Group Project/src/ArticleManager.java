
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
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Articles>) in.readObject();
        }
    }
}
