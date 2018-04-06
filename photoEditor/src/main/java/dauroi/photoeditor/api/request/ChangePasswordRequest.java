package dauroi.photoeditor.api.request;

/**
 * @Refer API document.
 * @author Wisekey
 *
 */
public class ChangePasswordRequest {
	private String mNewPassword;
	private String mOldPassword;
	
	public void setNewPassword(String newPassword) {
		this.mNewPassword = newPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.mOldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return mNewPassword;
	}
	
	public String getOldPassword() {
		return mOldPassword;
	}
}
