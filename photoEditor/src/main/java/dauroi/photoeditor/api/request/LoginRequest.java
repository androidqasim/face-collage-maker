package dauroi.photoeditor.api.request;

/**
 * @Refer API document
 * @author Wisekey
 *
 */
public class LoginRequest {
	private String mUsername;
	private String mPassword;
	
	public void setPassword(String password) {
		this.mPassword = password;
	}
	
	public void setUsername(String username) {
		this.mUsername = username;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	public String getUsername() {
		return mUsername;
	}
}
