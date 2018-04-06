package dauroi.photoeditor.api.response;

/**
 * Created by Wisekey on 5/8/2015.
 */
public class GadsResponse extends BaseResponse{
    private int mCoins;
    private String mToken;
    private String mCreatedTime;
    private String mExpiredTime;

    public int getCoins() {
        return mCoins;
    }

    public String getToken() {
        return mToken;
    }

    public String getExpiredTime() {
        return mExpiredTime;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

}
