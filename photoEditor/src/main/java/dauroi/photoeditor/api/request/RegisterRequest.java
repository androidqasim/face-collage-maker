package dauroi.photoeditor.api.request;

/**
 * Created by vanhu_000 on 3/11/2015.
 * @Refer to API document
 */
public class RegisterRequest {
    private String mFullName;
    private String mUsername;
    private String mPassword;
    private String mEmail;

    public void setFullName(String fullName){
        this.mFullName = fullName;
    }

    public String getFullName() {
        return this.mFullName;
    }

    public void setUserName(String userName) {
        this.mUsername = userName;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getUserName() {
        return this.mUsername;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public String getEmail() {
        return this.mEmail;
    }
}
