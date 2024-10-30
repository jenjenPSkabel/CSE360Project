
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


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
            if (connection == null || connection.isClosed()) {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                statement = connection.createStatement();
                createTables();  // Create tables if they don't exist
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error while connecting to the database");
        }
    }

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255) UNIQUE, "
				+ "username VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(50), "
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
	public void registerWithEmail(String username, String password, String email, String role) throws SQLException {
	    String checkEmailExistsQuery = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
	    String insertUserQuery = "INSERT INTO cse360users (username, password, email, role) VALUES (?, ?, ?, ?)";
	    String updateUserQuery = "UPDATE cse360users SET username = ?, password = ?, role = ? WHERE email = ?";

	    try (PreparedStatement checkStmt = connection.prepareStatement(checkEmailExistsQuery)) {
	        
	        // Step 1: Check if the email already exists
	        checkStmt.setString(1, email);
	        ResultSet rs = checkStmt.executeQuery();
	        
	        if (rs.next() && rs.getInt(1) > 0) {
	            // Email exists, perform an update
	            System.out.println("Email already exists, updating existing user.");
	            try (PreparedStatement updateStmt = connection.prepareStatement(updateUserQuery)) {
	                updateStmt.setString(1, username);
	                updateStmt.setString(2, password);
	                updateStmt.setString(3, role);
	                updateStmt.setString(4, email);
	                updateStmt.executeUpdate();
	                System.out.println("Existing user updated with new username, password, and role.");
	            }
	        } else {
	            // Email does not exist, perform an insert
	            System.out.println("Email does not exist, inserting new user.");
	            try (PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {
	                insertStmt.setString(1, username);
	                insertStmt.setString(2, password);
	                insertStmt.setString(3, email);
	                insertStmt.setString(4, role);
	                insertStmt.executeUpdate();
	                System.out.println("New user registered with email and role.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
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

	public ArrayList<String[]> displayUsersByAdmin() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not established.");
        }

        String sql = "SELECT * FROM cse360users";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<String[]> userList = new ArrayList<>();
        
        while (rs.next()) {
            String[] userInfo = new String[7];
            userInfo[0] = rs.getString("username");
            userInfo[1] = rs.getString("preferred_name");
            userInfo[2] = rs.getString("first_name");
            userInfo[3] = rs.getString("middle_name");
            userInfo[4] = rs.getString("last_name");
            userInfo[5] = rs.getString("email");
            userInfo[6] = rs.getString("role");
            userList.add(userInfo);
        }

        rs.close();  // Always close the ResultSet and Statement
        stmt.close();
        return userList;
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
	public String getRoleForInviteCode(String inviteCode) throws SQLException {
	    String getRoleQuery = "SELECT role FROM cse360users WHERE oneTimePassword = ?";
	    String role = null;

	    try (PreparedStatement pstmt = connection.prepareStatement(getRoleQuery)) {
	        pstmt.setString(1, inviteCode);
	        ResultSet resultSet = pstmt.executeQuery();

	        if (resultSet.next()) {
	            role = resultSet.getString("role");
	            System.out.println("Role for invite code (OTP) " + inviteCode + " is: " + role);
	        } else {
	            System.out.println("No role found for the provided invite code (OTP).");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return role;
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
        String query = "SELECT password FROM cse360users WHERE username = ?";

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
 // Method to invite a user and save invite code to both tables
    public void inviteUser(String email, String inviteCode) throws SQLException {
        String createUser = "INSERT INTO cse360users (email, oneTimePassword, role) VALUES (?, ?, ?)";
        String otp = String.valueOf(100000000 + (int)(Math.random() * 999999999));  // Generate 9-digit OTP
        String role;

        // Determine the role based on the invite code
        switch (inviteCode) {
            case "STUDENT_CODE":
                role = "Student";
                break;
            case "INSTRUCTOR_CODE":
                role = "Instructor";
                break;
            case "ADMIN_CODE":
                role = "Admin";
                break;
            case "MULTI_ROLE_CODE":
                role = "Multi-role";
                break;
            default:
                throw new IllegalArgumentException("Invalid invite code.");
        }

       try (PreparedStatement userStatement = connection.prepareStatement(createUser)) {
            // Insert into cse360users table
            userStatement.setString(1, email);
            userStatement.setString(2, otp);
            userStatement.setString(3, role);  // Insert the role based on the invite code
            userStatement.executeUpdate();
			

            // Print the details
            System.out.println("User invited successfully:");
            System.out.println("Email: " + email);
            System.out.println("Role: " + role);
            System.out.println("Generated OTP (Invite Code): " + otp);
        }
    }
    
   


 // Method to get the user's role based on the username
    public String getUserRole(String username) throws SQLException {
        String query = "SELECT role FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            // If a result is found, return the role
            if (rs.next()) {
                return rs.getString("role");  // Return the user's role
            }
        }
        return null;  // Return null if no matching user is found
    }
    
 // Method to delete a user by username
    public void deleteUserByUsernameOrEmail(String username, String email) throws SQLException {
        String deleteUserQuery;

        // Determine whether to delete by username or email
        if (username != "null" && !username.isEmpty()) {
            deleteUserQuery = "DELETE FROM cse360users WHERE username = ?";
        } else if (email != "null" && !email.isEmpty()) {
            deleteUserQuery = "DELETE FROM cse360users WHERE email = ?";
        } else {
            System.out.println("Both username and email are null or empty. No deletion performed.");
            return;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserQuery)) {
            pstmt.setString(1, username != "null" && !username.isEmpty() ? username : email);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User deleted successfully using " + (username != null && !username.isEmpty() ? "username: " + username : "email: " + email));
            } else {
                System.out.println("No user found with the provided identifier.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    


 // Method to delete the invite code from the cse360users table after successful registration
    public void deleteInviteCode(String inviteCode) throws SQLException {
        String clearOneTimePasswordQuery = "UPDATE cse360users SET oneTimePassword = NULL WHERE oneTimePassword = ?";
        
        try (PreparedStatement updateStmt = connection.prepareStatement(clearOneTimePasswordQuery)) {
            updateStmt.setString(1, inviteCode);
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("oneTimePassword cleared successfully for the provided invite code.");
            } else {
                System.out.println("No row found with the provided invite code.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void deleteOneTimePassword(String inviteCode) throws SQLException {
        String deleteOneTimePasswordQuery = "DELETE FROM cse360users WHERE oneTimePassword = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteOneTimePasswordQuery)) {
            deleteStmt.setString(1, inviteCode);
            int rowsAffected = deleteStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("One Time Password row deleted successfully.");
            } else {
                System.out.println("No row found with the provided invite code.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Additional method to delete both invite code and user
    public void registerUserWithInviteCode(String username, String password, String inviteCode) throws SQLException {
        String fetchInviteCodeQuery = "SELECT email, role FROM cse360users WHERE oneTimePassword = ?";
        
        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchInviteCodeQuery)) {
            
            // Step 1: Retrieve email and role based on invite code
            fetchStmt.setString(1, inviteCode);
            ResultSet resultSet = fetchStmt.executeQuery();

            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");

                // Step 1: Perform any necessary one-time password deletion
                deleteOneTimePassword(inviteCode);

                // Step 2: Register or update the user with the retrieved email and role
                registerWithEmail(username, password, email, role);

                // Step 3: Delete the invite code row after registration
                deleteInviteCode(inviteCode);
                System.out.println("User registered or updated, and invite code deleted successfully.");
                
            } else {
                System.out.println("Invite code not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	

}


