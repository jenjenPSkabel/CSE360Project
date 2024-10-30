import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Articles implements Serializable {

    private static final long serialVersionUID = 1L;
    
    

    // Core fields for each article
    
    private static final Random RANDOM = new Random();
    private long id;  // 7-digit unique identifier for each article
    
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


}