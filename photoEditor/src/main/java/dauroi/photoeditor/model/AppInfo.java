package dauroi.photoeditor.model;

/**
 * Created by Wisekey on 5/10/2015.
 */
public class AppInfo {
    private String mAppName;
    private String mPackageName;
    private String mAppleId;
    private String mUrl;
    private String mThumbnail;
    private boolean mActive;

    public String getAppName() {
        return mAppName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getAppleId() {
        return mAppleId;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public boolean isActive() {
        return mActive;
    }
}
