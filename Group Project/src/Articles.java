import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Articles implements Serializable {

    private static final long serialVersionUID = 1L;
    
    

    // Core fields for each article
    
    private static final Random RANDOM = new Random();
    private static final String ENCRYPTION_KEY = "MySuperSecureKey123";
    private static final EncryptionHelper encryptionHelper;

    static {
        try {
            encryptionHelper = new EncryptionHelper(); // Initialize EncryptionHelper
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EncryptionHelper", e);
        }
    }    
    private long id;  // 7-digit unique identifier for each article
    
    private int tempNumber = 0;
    private String group = "Uncategorized"; // Default group
    private String contentLevel = "Uncategorized"; // Default content level
    private String header;  // Unique header or metadata about the article
    private char[] title;
    private char[] authors;
    private char[] shortDescription;
    private char[] keywords;
    private char[] body;
    private char[] references;
    private char[] otherInfo;  // Title or Description without sensitive information

    public Articles(int titleLength, int authorsLength, int shortDescriptionLength, int keywordsLength, int bodyLength, int referencesLength, int infoLength) {
    	this.id = generateRandomId();
    	title = new char[titleLength];
        authors = new char[authorsLength];
        shortDescription = new char[shortDescriptionLength];
        keywords = new char[keywordsLength];
        body = new char[bodyLength];
        references = new char[referencesLength];
        
        otherInfo = new char[infoLength];
        
    }
    
    private static long generateRandomId() {
        return 1_000_000 + RANDOM.nextInt(9_000_000);  // Range from 1,000,000 to 9,999,999
    }

    // Getter for id
    public long getId() {
        return id;
    }

    // Setters for each field
    
    public void setTempNumber(int tempNumber) {
        this.tempNumber = tempNumber;
    }
    
    public void setGroup(String group) {
        this.group = group != null ? group : "Uncategorized"; // Ensure non-null value
    }
    
    public void setContentLevel(String contentLevel) {
        this.contentLevel = contentLevel != null ? contentLevel : "Uncategorized";
    }
    
    public void setHeader(String header) {
        this.header = header;
    }

    public void setTitle(String t) {
        Arrays.fill(title, ' ');
        System.arraycopy(t.toCharArray(), 0, title, 0, Math.min(t.length(), title.length));
    }

    public void setAuthors(String a) {
        Arrays.fill(authors, ' ');
        System.arraycopy(a.toCharArray(), 0, authors, 0, Math.min(a.length(), authors.length));
    }

    public void setShortDescription(String desc) {
        Arrays.fill(shortDescription, ' ');
        System.arraycopy(desc.toCharArray(), 0, shortDescription, 0, Math.min(desc.length(), shortDescription.length));
    }

    public void setKeywords(String k) {
        Arrays.fill(keywords, ' ');
        System.arraycopy(k.toCharArray(), 0, keywords, 0, Math.min(k.length(), keywords.length));
    }

    public void setBody(String b) {
        Arrays.fill(body, ' ');
        System.arraycopy(b.toCharArray(), 0, body, 0, Math.min(b.length(), body.length));
    }

    public void setReferences(String r) {
        Arrays.fill(references, ' ');
        System.arraycopy(r.toCharArray(), 0, references, 0, Math.min(r.length(), references.length));
    }

    public void setOtherInfo(String t) {
        Arrays.fill(otherInfo, ' ');
        System.arraycopy(t.toCharArray(), 0, otherInfo, 0, Math.min(t.length(), otherInfo.length));
    }


    // Getters
    
    public int getTempNumber() {
        return tempNumber;
    }
    
    public String getGroup() {
        return group;
    }
    
    public String getContentLevel() {
        return contentLevel;
    }
    
    public String getHeader() {
        return header;
    }

    public String getTitle() {
        return new String(title).trim();
    }

    public String getAuthors() {
        return new String(authors).trim();
    }

    public String getShortDescription() {
        return new String(shortDescription).trim();
    }

    public String getKeywords() {
        return new String(keywords).trim();
    }

    public String getBody() {
        return new String(body).trim();
    }

    public String getReferences() {
        return new String(references).trim();
    }

    public String getOtherInfo() {
        return new String(otherInfo).trim();
    }



     // Getter for encrypted body
 // Setter for encrypted body
    public void setEncryptedBody(String bodyContent, String keyBase) {
        String key = generateKeyFromBase(keyBase); // Derive a consistent key from the base
        System.out.println("Encrypting with key: " + key); // Debug
        if (needsEncryption) {
            this.body = new String(
                encryptionHelper.safeEncrypt(
                    bodyContent.getBytes(StandardCharsets.UTF_8),
                    key.getBytes(StandardCharsets.UTF_8)
                ),
                StandardCharsets.UTF_8
            ).toCharArray();
        } else {
            this.body = bodyContent.toCharArray();
        }
    }

    public String generateKeyFromBase(String base) {
        if (base == null) {
            throw new IllegalArgumentException("Base string cannot be null");
        }
        // Pad or truncate the base to exactly 16 characters
        String key = base.length() >= 16 
                     ? base.substring(0, 16) 
                     : String.format("%-16s", base).replace(' ', 'x');
        System.out.println("Generated key: " + key); // Debug
        return key;
    }





    public String getEncryptedBody(String key) {
        System.out.println("Decrypting with key: " + key); // Debug
        if (!needsEncryption) return new String(body).trim();
        System.out.println("Encrypted body: " + new String(body).trim()); // Debug
        return new String(
            encryptionHelper.safeDecrypt(
                new String(body).getBytes(StandardCharsets.UTF_8),
                key.getBytes(StandardCharsets.UTF_8)
            ),
            StandardCharsets.UTF_8
        );
    }









    // Simple XOR encryption/decryption method
    private String encryptDecrypt(String input, String key) {
        try {
            EncryptionHelper encryptionHelper = new EncryptionHelper(); // Create instance
            return new String(
                encryptionHelper.encrypt(
                    input.getBytes(StandardCharsets.UTF_8), // Convert input to byte[]
                    key.getBytes(StandardCharsets.UTF_8)    // Convert key to byte[]
                ),
                StandardCharsets.UTF_8 // Convert result back to String
            );
        } catch (Exception e) {
            e.printStackTrace();
            return input; // Fallback to plain text in case of errors
        }
    }
    
    //private String decrypted;
    //private String encrypted;
    
    
    public String getBodyForUser(String username, List<String> whitelist, String key) {
        System.out.println("Checking access for user: " + username); // Debug
        System.out.println("Whitelist: " + whitelist); // Debug

        if (whitelist.contains(username)) {
            System.out.println("User is in whitelist. Returning decrypted body."); // Debug
            //String key = generateKeyFromBase(keyBase); // Generate the same key used for encryption
            try {
                String decrypted = new String(
                    encryptionHelper.safeDecrypt(
                        new String(body).getBytes(StandardCharsets.UTF_8), // Ensure proper encoding
                        key.getBytes(StandardCharsets.UTF_8)               // Key encoding
                    ),
                    StandardCharsets.UTF_8
                );
                System.out.println("Decrypted body: " + decrypted); // Debug
                return decrypted; // Return the decrypted body
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Decryption failed for user: " + username);
                return "Error: Decryption failed.";
            }
        }

        System.out.println("User is not in whitelist. Returning encrypted body."); // Debug
        return new String(body).trim();
    }




}





