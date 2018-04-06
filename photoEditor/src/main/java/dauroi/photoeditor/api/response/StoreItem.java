package dauroi.photoeditor.api.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.model.Language;

public class StoreItem extends ItemPackageInfo {
    public static final String EXTRA_ITEM_TYPE_KEY = "itemType";

    public static final String VIP_PERMISSION = "VIP";
    public static final int STATUS_ONLINE = 0;
    public static final int STATUS_DOWNLOADED = 1;
    public static final int STATUS_DOWNLOADING = 2;

    public static class Effect implements Parcelable {
        private String mImage;
        private String mThumbnail;
        private String mSelectedThumbnail;
        private List<Language> mNames;

        public Effect() {

        }

        public String getImage() {
            return mImage;
        }

        public String getThumbnail() {
            return mThumbnail;
        }

        public List<Language> getNames() {
            return mNames;
        }

        public void setThumbnail(String thumbnail) {
            mThumbnail = thumbnail;
        }

        public void setSelectedThumbnail(String selectedThumbnail) {
            mSelectedThumbnail = selectedThumbnail;
        }

        public String getSelectedThumbnail() {
            return mSelectedThumbnail;
        }

        public void setImage(String image) {
            mImage = image;
        }

        public static final Parcelable.Creator<Effect> CREATOR
                = new Parcelable.Creator<Effect>() {
            public Effect createFromParcel(Parcel in) {
                return new Effect(in);
            }

            public Effect[] newArray(int size) {
                return new Effect[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mImage);
            dest.writeString(mThumbnail);
            dest.writeString(mSelectedThumbnail);
            dest.writeTypedList(mNames);
        }

        protected Effect(Parcel in) {
            mImage = in.readString();
            mThumbnail = in.readString();
            mSelectedThumbnail = in.readString();
            mNames = new ArrayList<>();
            in.readTypedList(mNames, Language.CREATOR);
        }
    }

    private String mDescription;
    private String mPermission;
    private String mOriginalThumbnail;
    private int mItemCount;
    private String mUrl;
    private float mPrice;
    private Effect[] mEffects;
    private String mLanguage;
    private String mUpdatedTime;
    private boolean mActive;
    private int mDownloadStatus = STATUS_ONLINE;
    private String mSignature;
    private int mViewCount;
    private int mDownloadCount;

    public StoreItem() {

    }

    public int getViewCount() {
        return mViewCount;
    }

    public void setViewCount(int viewCount) {
        mViewCount = viewCount;
    }

    public int getDownloadCount() {
        return mDownloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        mDownloadCount = downloadCount;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setDownloadStatus(int downloadStatus) {
        mDownloadStatus = downloadStatus;
    }

    public int getDownloadStatus() {
        return mDownloadStatus;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPermission() {
        return mPermission;
    }

    public void setOriginalThumbnail(String originalThumbnail) {
        mOriginalThumbnail = originalThumbnail;
    }

    public String getOriginalThumbnail() {
        return mOriginalThumbnail;
    }

    public int getItemCount() {
        return mItemCount;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public float getPrice() {
        return mPrice;
    }

    public Effect[] getEffects() {
        return mEffects;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getUpdatedTime() {
        return mUpdatedTime;
    }

    public boolean isActive() {
        return mActive;
    }

    public static final Parcelable.Creator<StoreItem> CREATOR
            = new Parcelable.Creator<StoreItem>() {
        public StoreItem createFromParcel(Parcel in) {
            return new StoreItem(in);
        }

        public StoreItem[] newArray(int size) {
            return new StoreItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mDescription);
        dest.writeString(mPermission);
        dest.writeString(mOriginalThumbnail);
        dest.writeInt(mItemCount);
        dest.writeString(mUrl);
        dest.writeFloat(mPrice);
        if (mEffects != null && mEffects.length > 0) {
            dest.writeInt(mEffects.length);
            dest.writeTypedArray(mEffects, flags);
        } else {
            dest.writeInt(-1);
        }

        dest.writeString(mLanguage);
        dest.writeString(mUpdatedTime);
        dest.writeBooleanArray(new boolean[]{mActive});
        dest.writeInt(mDownloadStatus);
        dest.writeString(mSignature);
        dest.writeInt(mViewCount);
        dest.writeInt(mDownloadCount);
    }

    private StoreItem(Parcel in) {
        super(in);
        mDescription = in.readString();
        mPermission = in.readString();
        mOriginalThumbnail = in.readString();
        mItemCount = in.readInt();
        mUrl = in.readString();
        mPrice = in.readFloat();
        int len = in.readInt();
        if (len > 0) {
            mEffects = new Effect[len];
            in.readTypedArray(mEffects, Effect.CREATOR);
        }

        mLanguage = in.readString();
        mUpdatedTime = in.readString();
        boolean[] b = new boolean[1];
        in.readBooleanArray(b);
        mActive = b[0];
        mDownloadStatus = in.readInt();
        mSignature = in.readString();
        mViewCount = in.readInt();
        mDownloadCount = in.readInt();
    }
}
