package dauroi.photoeditor.api.response;

/**
 * Created by vanhu_000 on 6/20/2015.
 */
public class BigDAds {
    private String m_id;
    private String mPackageId;
    private String mTitle;
    private String[] mContent;
    private String[] mImages;
    private String mThumbnail;
    private String mActionName;
    private String mUrl;
    private int mTime;
    private boolean mActive;
    private String mLang;

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
    }

    public String getPackageId() {
        return mPackageId;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public int getTime() {
        return mTime;
    }

    public String getId() {
        return m_id;
    }

    public String[] getImages() {
        return mImages;
    }

    public String getActionName() {
        return mActionName;
    }

    public String[] getContent() {
        return mContent;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isActive() {
        return mActive;
    }
}

