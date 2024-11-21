import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HelpMessageManager {

    public static void saveMessagesToFile(String filename, List<HelpMessage> messages) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(messages);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<HelpMessage> loadMessagesFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<HelpMessage>) in.readObject();
        }
    }

    public static List<HelpMessage> loadMessagesSafely(String filename) {
        List<HelpMessage> messages = new ArrayList<>();
        try {
            messages = loadMessagesFromFile(filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No messages file found or failed to load. Returning an empty list.");
        }
        return messages;
    }
}
