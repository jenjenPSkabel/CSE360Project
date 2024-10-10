
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255) UNIQUE, "
				+ "username VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20), "
				+ "first_name VARCHAR(255), "
				+ "middle_name VARCHAR(255), "
				+ "last_name VARCHAR(255), "
				+ "preferred_name VARCHAR(255), "
				+ "oneTimePassword VARCHAR(255))";
		statement.execute(userTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(String username, String password, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (username, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		}
	}

	public void setUpAccount(String username, String email, String firstName, String middleName, String lastName, String preferredName) throws SQLException {
        String updateQuery = "UPDATE cse360users SET email = ?, first_name = ?, middle_name = ?, last_name = ?, preferred_name = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, email);
            pstmt.setString(2, firstName);
            pstmt.setString(3, middleName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, preferredName);
            pstmt.setString(6, username);  // Use the username to find the correct row
            pstmt.executeUpdate();
		}
	}

	// Method to check if the user's profile is complete (email, first name, last name are not null)
    public boolean isProfileComplete(String username) throws SQLException {
        String query = "SELECT email, first_name, last_name FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);  // Use the username to find the row
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Check if email, first name, or last name are null or empty
                String email = rs.getString("email");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                return email != null && !email.isEmpty() && firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty();
            } else {
                return false;  // User doesn't exist (shouldn't happen because login already checks)
            }
        }
    }

	public boolean login(String username, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public boolean doesUserExist(String username) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Username: " + username); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role); 
		} 
	}
	
	public void displayUsersByUser() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Username: " + username); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role);
		}
	}


	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	//ADDED
	// Method to check if the password matches the one stored for the username
    public boolean checkPassword(String username, String password) throws SQLException {
        // SQL query to get the stored password for the given username
        String query = "SELECT password FROM users WHERE username = ?";

        // Prepare statement to prevent SQL injection
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);  // Set the username in the query

            // Execute query and retrieve result
            ResultSet resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                // Get the stored password from the result set
                String storedPassword = resultSet.getString("password");

                // Check if the entered password matches the stored password
                return storedPassword.equals(password);
            } else {
                // If no user is found, return false
                return false;
            }
        }
    }
	public void inviteUser(String email)throws SQLException {
		String createUser = "INSERT INTO cse360users (email, oneTimePassword) VALUES = (?, ?)";
		String otp = String.valueOf(100000000 + (int)(Math.random() * 999999999);
		try(PreparedStatement statement = connection.prepareStatement(createUser)){
			statement.setString(1, email);
			statement.setString(2, otp);
			statement.executeUpdate();
		}
	}
	
	public void deleteUser(String username) throws SQLException{
		String deleteQuery = "DELETE FROM cse360users WHERE username = ?";
		try(PreparedStatement statement = connction.prepareStatement(deleteQuery)){
			statement.setString(1, username);
			statement.executeUpdate();
		}
	}

	public void editRoles(String username, String role)throws SQLException{
		String editQuery = "UPDATE cse360users SET role = ? WHERE username = ?";
		try(PreparedStatement statement = connection.prepareStatement(editQuery)){
			statement.setString(1, role);
			statement.setString(2, username);
			statement.executeUpdate();
		}
	}
	

}
