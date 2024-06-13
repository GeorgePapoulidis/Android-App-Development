package ModuleName;

public class Owner {

    private String fullName;
    private String username;
    private String password;
    private String email;

    public Owner(String fullName, String username, String password, String email){
        this.fullName=fullName;
        this.username=username;
        this.password=password;
        this.email=email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void printOwner(){
        System.out.println("FullName:" + getFullName() + " " +
                "Username:" + getUsername() + " " +
                "Password:" + getPassword() + " " +
                "Email:" + getEmail());
    }
}


