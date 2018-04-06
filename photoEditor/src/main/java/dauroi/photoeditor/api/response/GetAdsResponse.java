package dauroi.photoeditor.api.response;

/**
 * Created by Wisekey on 5/10/2015.
 */
public class GetAdsResponse extends BaseResponse {
    private String mAppId;
    private String mScreenName;
    private int[] mPeriods;
    private boolean mActive;

    public String getAppId() {
        return mAppId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public int[] getPeriods() {
        return mPeriods;
    }

    public boolean isActive() {
        return mActive;
    }
}
