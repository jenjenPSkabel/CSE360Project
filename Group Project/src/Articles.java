import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Arrays;

public class Articles {
    private char[] title;
    private char[] authors;
    private char[] abstractText;
    private char[] keywords;
    private char[] body;
    private char[] references;

    public Articles(int titleLength, int authorsLength, int abstractLength, int keywordsLength, int bodyLength, int referencesLength) {
        title = new char[titleLength];
        authors = new char[authorsLength];
        abstractText = new char[abstractLength];
        keywords = new char[keywordsLength];
        body = new char[bodyLength];
        references = new char[referencesLength];
    }

    // Setters for each field
    public void setTitle(String t) {
        Arrays.fill(title, ' ');
        System.arraycopy(t.toCharArray(), 0, title, 0, Math.min(t.length(), title.length));
    }

    public void setAuthors(String a) {
        Arrays.fill(authors, ' ');
        System.arraycopy(a.toCharArray(), 0, authors, 0, Math.min(a.length(), authors.length));
    }

    public void setAbstractText(String a) {
        Arrays.fill(abstractText, ' ');
        System.arraycopy(a.toCharArray(), 0, abstractText, 0, Math.min(a.length(), abstractText.length));
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

    // Getters
    public String getTitle() {
        return new String(title).trim();
    }

    public String getAuthors() {
        return new String(authors).trim();
    }

    public String getAbstractText() {
        return new String(abstractText).trim();
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
}
