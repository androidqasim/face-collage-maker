package dauroi.photoeditor.api.response;

/**
 * Created by vanhu_000 on 6/20/2015.
 */
public class User {
    private String mUsername;
    private String mFullName;
    private String mEmail;
    private String mThumbnail;
    private long mCoins;
    private String[] mRecentVideos;
    private String[] mLikedVideos;
    private TokenInfo[] mToken;

    public String getUsername() {
        return mUsername;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public long getCoins() {
        return mCoins;
    }

    public String[] getRecentVideos() {
        return mRecentVideos;
    }

    public String[] getLikedVideos() {
        return mLikedVideos;
    }

    public TokenInfo[] getToken() {
        return mToken;
    }
}
