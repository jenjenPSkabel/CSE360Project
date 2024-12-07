import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpArticleHandlerTest {
    private HelpArticleHandler handler; // Handler for search functionality
    private List<HelpArticles> mockDatabase; // Simulates the article database

    @BeforeEach
    void setUp() {
        // Initialize the mock database with sample data
        mockDatabase = new ArrayList<>();
        mockDatabase.add(new HelpArticles(
            "Understanding Loops", 
            "Loops Overview", 
            "John Doe", 
            "Loops are a fundamental programming concept."
        ));
        mockDatabase.add(new HelpArticles(
            "Recursion in Depth", 
            "Deep Dive into Recursion", 
            "Jane Smith", 
            "Recursion involves a function calling itself."
        ));
        mockDatabase.add(new HelpArticles(
            "Sorting Algorithms", 
            "Sorting Overview", 
            "Alice Johnson", 
            "Sorting is key to many problems in computer science."
        ));

        // Initialize the handler with the mock database
        handler = new HelpArticleHandler(mockDatabase);
    }

    @Test
    void testSearchArticles_ValidKeywordInTitle() {
        // Search for "loops"
        List<HelpArticles> results = handler.searchArticles("loops");

        // Assert that the correct article is returned
        assertEquals(1, results.size(), "Only one article should match.");
        assertEquals("Understanding Loops", results.get(0).getTitle());
    }

    @Test
    void testSearchArticles_ValidKeywordInHeader() {
        // Search for "Overview"
        List<HelpArticles> results = handler.searchArticles("Overview");

        // Assert that two articles are returned
        assertEquals(2, results.size(), "Two articles should match.");
        assertTrue(results.stream().anyMatch(a -> a.getTitle().equals("Understanding Loops")));
        assertTrue(results.stream().anyMatch(a -> a.getTitle().equals("Sorting Algorithms")));
    }

    @Test
    void testSearchArticles_ValidKeywordInAuthors() {
        // Search for "Jane Smith"
        List<HelpArticles> results = handler.searchArticles("Jane Smith");

        // Assert that the correct article is returned
        assertEquals(1, results.size(), "Only one article should match.");
        assertEquals("Recursion in Depth", results.get(0).getTitle());
    }

    @Test
    void testSearchArticles_ValidKeywordInAbstract() {
        // Search for "function calling itself"
        List<HelpArticles> results = handler.searchArticles("function calling itself");

        // Assert that the correct article is returned
        assertEquals(1, results.size(), "Only one article should match.");
        assertEquals("Recursion in Depth", results.get(0).getTitle());
    }

    @Test
    void testSearchArticles_NoMatches() {
        // Search for a nonexistent keyword
        List<HelpArticles> results = handler.searchArticles("nonexistentKeyword");

        // Assert that no articles are returned
        assertTrue(results.isEmpty(), "No articles should match.");
    }

    @Test
    void testSearchArticles_CaseInsensitive() {
        // Search with different case
        List<HelpArticles> results = handler.searchArticles("LOOPS");

        // Assert that the correct article is returned
        assertEquals(1, results.size(), "Search should be case-insensitive.");
        assertEquals("Understanding Loops", results.get(0).getTitle());
    }

    @Test
    void testSearchArticles_EmptyKeyword() {
        // Search with an empty keyword
        List<HelpArticles> results = handler.searchArticles("");

        // Assert that no articles are returned
        assertTrue(results.isEmpty(), "Empty keywords should return no results.");
    }

    @Test
    void testSearchArticles_NullKeyword() {
        // Search with a null keyword
        List<HelpArticles> results = handler.searchArticles(null);

        // Assert that no articles are returned
        assertTrue(results.isEmpty(), "Null keywords should return no results.");
    }
}
