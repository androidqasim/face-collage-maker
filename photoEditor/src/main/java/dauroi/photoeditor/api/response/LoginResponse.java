package dauroi.photoeditor.api.response;

public class LoginResponse extends BaseResponse {

    private String mToken;
    private String mFullName;
    private String mUsername;
    private String mEmail;
    private String mThumbnail;
    private int mCoins;
    private String mExpiredTime;

    public String getExpiredTime() {
        return mExpiredTime;
    }

    public String getFullName() {
        return this.mFullName;
    }

    public String getEmail() {
        return this.mEmail;
    }


    public String getThumbnail() {
        return this.mThumbnail;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public String getToken() {
        return mToken;
    }

    public int getCoins() {
        return mCoins;
    }
}
