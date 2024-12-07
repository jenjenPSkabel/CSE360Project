
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import org.bouncycastle.util.Arrays;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;



public class ArticleManager {

	public static void saveArticlesToFile(String filename, List<Articles> articles) throws IOException {
	    System.out.println("Starting to save articles...");
	    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
	        for (Articles article : articles) {
	            if (article.isNeedsEncryption()) {
	                System.out.println("Encrypting article with header: " + article.getHeader());
	                // Convert char[] key to String before passing
	                String key = new String(EncryptionUtils.getInitializationVector(article.getHeader().toCharArray()));
	                
	                // Encrypt the body before saving
	                article.setEncryptedBody(
	                    article.getBody(),
	                    key
	                );
	                System.out.println("Successfully encrypted article with header: " + article.getHeader());
	            }
	        }
	        out.writeObject(articles); // Save the entire list of articles
	    }
	    System.out.println("Finished saving articles.");
	}




	public static List<Articles> loadArticlesFromFile(String filename) throws IOException, ClassNotFoundException, Exception {
	    List<Articles> articles;
	    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
	        articles = (List<Articles>) in.readObject();

	        // Create an instance of EncryptionHelper
	        EncryptionHelper encryptionHelper = new EncryptionHelper();

	        // Decrypt articles as needed
	        for (Articles article : articles) {
	            if (article.isNeedsEncryption()) {
	                try {
	                    String key = new String(EncryptionUtils.getInitializationVector(article.getHeader().toCharArray()));
	                    System.out.println("Decrypting article ID: " + article.getId() + " with key: " + key); // Debug
	                    String decryptedBody = new String(
	                        encryptionHelper.safeDecrypt(
	                            article.getBody().getBytes(StandardCharsets.UTF_8),
	                            key.getBytes(StandardCharsets.UTF_8)
	                        ),
	                        StandardCharsets.UTF_8
	                    );
	                    article.setBody(decryptedBody);
	                    System.out.println("Decrypted body: " + decryptedBody); // Debug
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    System.err.println("Failed to decrypt article with ID: " + article.getId());
	                }
	            }
	        }

	        // Ensure group and content level are properly set
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
	    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
	        Object obj = in.readObject();
	        if (obj instanceof List) {
	            articles = (List<Articles>) obj; // Properly cast to List<Articles>
	        } else if (obj instanceof Articles) {
	            articles.add((Articles) obj); // Handle legacy data storing single articles
	        }
	    } catch (IOException | ClassNotFoundException e) {
	        System.out.println("No backup file found or failed to load. Returning an empty list.");
	    }
	    return articles;
	}

}
