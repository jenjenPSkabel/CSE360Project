import java.io.Serializable;

public class HelpMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String messageType;
    private String messageHeader;
    private String messageBody;

    public HelpMessage(String username, String email, String messageType, String messageHeader, String messageBody) {
        this.username = username;
        this.email = email;
        this.messageType = messageType;
        this.messageHeader = messageHeader;
        this.messageBody = messageBody;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public String getMessageBody() {
        return messageBody;
    }

    @Override
    public String toString() {
        return "Username: " + username + "\nEmail: " + email + "\nType: " + messageType +
                "\nHeader: " + messageHeader + "\nBody: " + messageBody;
    }
}
