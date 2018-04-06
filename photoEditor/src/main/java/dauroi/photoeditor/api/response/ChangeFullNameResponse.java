package dauroi.photoeditor.api.response;

/**
 * Created by Wisekey on 4/28/2015.
 * @Refer API document to change field names
 */
public class ChangeFullNameResponse extends BaseResponse{
    private String mUsername;
    private String mFullName;
    private String mEmail;
    private int mCoins;
    private String mRegisteredTime;

    public String getUsername() {
        return mUsername;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getEmail() {
        return mEmail;
    }

    public int getCoins() {
        return mCoins;
    }

    public String getRegisteredTime() {
        return mRegisteredTime;
    }
}
