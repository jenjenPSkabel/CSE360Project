public class User {
    private String userName;
    private String prefName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String role;
    private boolean isMultirole;

    // Updated Constructor to include isMultirole
    public User(String userName, String prefName, String firstName, String middleName, String lastName, String email, String role, boolean isMultirole) {
        this.userName = userName;
        this.prefName = prefName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.isMultirole = isMultirole;
    }

    // Getters
    public String getUserName() { return userName; }
    public String getPrefName() { return prefName; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isMultirole() { return isMultirole; }

    // Setters
    public void setUserName(String userName) { this.userName = userName; }
    public void setPrefName(String prefName) { this.prefName = prefName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setMultirole(boolean isMultirole) { this.isMultirole = isMultirole; }
}
