package dauroi.photoeditor.model;
/**
 * Field names are used in Gson. So Don't rename if file 'package.json' doesn't change field names.
 * @author vanhu_000
 *
 */
public class ShadeInfo extends ItemInfo {
	private Language[] mNames;
	private String mImage;
	private String mType;
	private long mPackageId;

	public void setForeground(String foreground) {
		mImage = foreground;
	}

	public void setShadeType(String type) {
		mType = type;
	}

	public void setPackageId(long packageId) {
		mPackageId = packageId;
	}

	public String getForeground() {
		return mImage;
	}

	public String getShadeType() {
		return mType;
	}

	public long getPackageId() {
		return mPackageId;
	}

	public Language[] getLanguages() {
		return mNames;
	}
}
