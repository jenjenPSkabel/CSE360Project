import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelpMessageHandlerTest {
    private HelpMessageHandler helpMessageHandler; // Class responsible for processing help messages
    private List<HelpMessage> mockDatabase; // Simulates an in-memory database

    @BeforeEach
    void setUp() {
        // Initialize the mock database and the handler
        mockDatabase = new ArrayList<>();
        helpMessageHandler = new HelpMessageHandler(mockDatabase);
    }

    @Test
    void testSendHelpMessage_ValidInput() {
        // Arrange
        String username = "user123";
        String email = "user123@example.com";
        String messageType = "Generic";
        String messageHeader = "Help Needed";
        String messageBody = "I need help with topic X";

        // Act
        boolean result = helpMessageHandler.sendHelpMessage(username, email, messageType, messageHeader, messageBody);

        // Assert
        assertTrue(result, "Message should be sent successfully.");
        assertEquals(1, mockDatabase.size(), "Database should contain one message.");
        HelpMessage savedMessage = mockDatabase.get(0);
        assertEquals(username, savedMessage.getUsername());
        assertEquals(email, savedMessage.getEmail());
        assertEquals(messageType, savedMessage.getMessageType());
        assertEquals(messageHeader, savedMessage.getMessageHeader());
        assertEquals(messageBody, savedMessage.getMessageBody());
    }

    @Test
    void testSendHelpMessage_EmptyFields() {
        // Arrange
        String username = "user123";
        String email = ""; // Empty email
        String messageType = "Generic";
        String messageHeader = "Help Needed";
        String messageBody = "I need help with topic X";

        // Act
        boolean result = helpMessageHandler.sendHelpMessage(username, email, messageType, messageHeader, messageBody);

        // Assert
        assertFalse(result, "Message should not be sent with an empty email.");
        assertEquals(0, mockDatabase.size(), "Database should remain empty.");
    }

    @Test
    void testSendHelpMessage_NullFields() {
        // Arrange
        String username = null; // Null username
        String email = "user123@example.com";
        String messageType = "Generic";
        String messageHeader = "Help Needed";
        String messageBody = "I need help with topic X";

        // Act
        boolean result = helpMessageHandler.sendHelpMessage(username, email, messageType, messageHeader, messageBody);

        // Assert
        assertFalse(result, "Message should not be sent with a null username.");
        assertEquals(0, mockDatabase.size(), "Database should remain empty.");
    }
}
