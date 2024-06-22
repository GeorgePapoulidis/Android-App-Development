package ModuleName;

/**
 * The Owner class represents a user with full name, username, password, and email.
 */
public class Owner {

    // Fields for storing owner information
    private String fullName;
    private String username;
    private String password;
    private String email;

    /**
     * Constructor for creating an Owner object.
     *
     * @param fullName the full name of the owner
     * @param username the username of the owner
     * @param password the password of the owner
     * @param email    the email of the owner
     */
    public Owner(String fullName, String username, String password, String email) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the full name of the owner.
     *
     * @return the full name of the owner
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     * Sets the full name of the owner.
     *
     * @param fullName the new full name of the owner
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the username of the owner.
     *
     * @return the username of the owner
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username of the owner.
     *
     * @param username the new username of the owner
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the owner.
     *
     * @return the password of the owner
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of the owner.
     *
     * @param password the new password of the owner
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email of the owner.
     *
     * @return the email of the owner
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email of the owner.
     *
     * @param email the new email of the owner
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Prints the details of the owner.
     */
    public void printOwner() {
        System.out.println("FullName: " + getFullName() + " " +
                "Username: " + getUsername() + " " +
                "Password: " + getPassword() + " " +
                "Email: " + getEmail());
    }
}
