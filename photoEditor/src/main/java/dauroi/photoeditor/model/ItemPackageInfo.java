package dauroi.photoeditor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Field names are used in Gson. So Don't rename if file 'package.json' doesn't
 * change field names.
 *
 * @author vanhu_000
 */
public class ItemPackageInfo extends ItemInfo {
    private String m_id;
    private String mType;
    private String mFolder;
    private String mBillingId;

    public void setFolder(String folder) {
        mFolder = folder;
    }

    public String getFolder() {
        return mFolder;
    }

    public String getIdString() {
        return m_id;
    }

    public void setIdString(String idStr) {
        m_id = idStr;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setBillingId(String billingId) {
        mBillingId = billingId;
    }

    public String getBillingId() {
        return mBillingId;
    }

    public ItemPackageInfo(){

    }

    public static final Parcelable.Creator<ItemPackageInfo> CREATOR
            = new Parcelable.Creator<ItemPackageInfo>() {
        public ItemPackageInfo createFromParcel(Parcel in) {
            return new ItemPackageInfo(in);
        }

        public ItemPackageInfo[] newArray(int size) {
            return new ItemPackageInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(m_id);
        dest.writeString(mType);
        dest.writeString(mFolder);
        dest.writeString(mBillingId);
    }

    protected ItemPackageInfo(Parcel in) {
        super(in);
        m_id = in.readString();
        mType = in.readString();
        mFolder = in.readString();
        mBillingId = in.readString();
    }
}
