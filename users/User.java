package users;
public class User {
    protected int userId;
    protected String email;
    protected String username;
    protected String password;
    protected String userType;

    public User(int userId, String email, String username, String password, String userType) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }
    public String getUsername(){
        return username;
    }
}
