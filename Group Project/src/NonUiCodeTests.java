import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NonUiCodeTests {

    @TempDir
    Path tempDir;

    private File articleFile;
    private File messageFile;
    private File helpArticleFile;

    @BeforeEach
    void setup() throws IOException {
        articleFile = tempDir.resolve("test_articles.dat").toFile();
        messageFile = tempDir.resolve("test_messages.dat").toFile();
        helpArticleFile = tempDir.resolve("test_help_articles.dat").toFile();
    }

    @Test
    @DisplayName("Test saving and loading articles")
    void testArticleManagerSaveLoad() throws IOException, ClassNotFoundException {
        Articles article1 = new Articles(50, 100, 200, 50, 500, 100, 50);
        article1.setTitle("Intro to Testing");
        article1.setGroup("UnitTests");
        article1.setContentLevel("Beginner");

        Articles article2 = new Articles(50, 100, 200, 50, 500, 100, 50);
        article2.setTitle("Advanced Topics");
        article2.setGroup("Integration");
        article2.setContentLevel("Advanced");

        List<Articles> articlesToSave = new ArrayList<>();
        articlesToSave.add(article1);
        articlesToSave.add(article2);

        ArticleManager.saveArticlesToFile(articleFile.getAbsolutePath(), articlesToSave);
        List<Articles> loadedArticles = ArticleManager.loadArticlesFromFile(articleFile.getAbsolutePath());

        assertEquals(2, loadedArticles.size());
        assertEquals("Intro to Testing", loadedArticles.get(0).getTitle());
        assertEquals("Beginner", loadedArticles.get(0).getContentLevel());
        assertEquals("Advanced Topics", loadedArticles.get(1).getTitle());
        assertEquals("Integration", loadedArticles.get(1).getGroup());
    }

    @Test
    @DisplayName("Test saving and loading help messages")
    void testHelpMessageManagerSaveLoad() throws IOException, ClassNotFoundException {
        HelpMessage msg1 = new HelpMessage("john", "john@example.com", "Generic", "Need Help", "I need help with my account.");
        HelpMessage msg2 = new HelpMessage("jane", "jane@example.com", "Specific", "Article Issue", "Cannot view article #42.");

        List<HelpMessage> messages = new ArrayList<>();
        messages.add(msg1);
        messages.add(msg2);

        HelpMessageManager.saveMessagesToFile(messageFile.getAbsolutePath(), messages);
        List<HelpMessage> loadedMessages = HelpMessageManager.loadMessagesFromFile(messageFile.getAbsolutePath());

        assertEquals(2, loadedMessages.size());
        assertEquals("john", loadedMessages.get(0).getUsername());
        assertEquals("Need Help", loadedMessages.get(0).getMessageHeader());
        assertEquals("jane", loadedMessages.get(1).getUsername());
        assertEquals("Article Issue", loadedMessages.get(1).getMessageHeader());
    }

    @Test
    @DisplayName("Test saving and loading help articles")
    void testHelpArticleManagerSaveLoad() throws IOException, ClassNotFoundException {
        HelpArticles article1 = new HelpArticles("Header1", "Title1", "Author1", "Abstract1");
        HelpArticles article2 = new HelpArticles("Header2", "Title2", "Author2", "Abstract2");

        List<HelpArticles> helpArticles = new ArrayList<>();
        helpArticles.add(article1);
        helpArticles.add(article2);

        HelpArticleManager.saveHelpArticlesToFile(helpArticleFile.getAbsolutePath(), helpArticles);
        List<HelpArticles> loadedHelpArticles = HelpArticleManager.loadHelpArticlesFromFile(helpArticleFile.getAbsolutePath());

        assertEquals(2, loadedHelpArticles.size());
        assertEquals("Title1", loadedHelpArticles.get(0).getTitle());
        assertEquals("Author2", loadedHelpArticles.get(1).getAuthors());
    }

    @Test
    @DisplayName("Test DatabaseHelper basic operations")
    void testDatabaseHelper() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        boolean isEmpty = dbHelper.isDatabaseEmpty();
        assertNotNull(isEmpty);
        String testUsername = "testuser_junit";
        String testPassword = "testpass_junit";
        String testRole = "Student";

        if (!dbHelper.doesUserExist(testUsername)) {
            dbHelper.register(testUsername, testPassword, testRole);
        }
        assertTrue(dbHelper.doesUserExist(testUsername));
        assertTrue(dbHelper.login(testUsername, testPassword, testRole));
        dbHelper.deleteUserByUsernameOrEmail(testUsername, "");
        assertFalse(dbHelper.doesUserExist(testUsername));
    }

    @Test
    @DisplayName("Test invite code user creation and role retrieval")
    void testInviteUserAndRoles() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        String testEmail = "newuser@example.com";
        String inviteCode = "STUDENT_CODE";
        dbHelper.inviteUser(testEmail, inviteCode);
        String fetchedRole = dbHelper.getRoleForInviteCode(dbHelper.isDatabaseEmpty() ? null : dbHelper.getUserEmail("null")); 
       
        assertNotNull(fetchedRole);
    }

    @Test
    @DisplayName("Test GroupManager basic functionality")
    void testGroupManager() {
        GroupManager group = new GroupManager("TestGroup", "ownerUser");
        group.addUser("alice");
        group.addUser("bob");
        assertEquals("TestGroup", group.getGroupName());
        assertEquals("ownerUser", group.getOwner());
        assertTrue(group.getUsers().contains("alice"));
        assertTrue(group.getUsers().contains("bob"));
        group.removeUser("alice");
        assertFalse(group.getUsers().contains("alice"));
    }

  
}
