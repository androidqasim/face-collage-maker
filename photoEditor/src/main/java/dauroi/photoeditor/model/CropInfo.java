package dauroi.photoeditor.model;
/**
 * Field names are used in Gson. So Don't rename if file 'package.json' doesn't change field names.
 * @author vanhu_000
 *
 */
public class CropInfo extends ItemInfo {
	private Language[] mNames;
	private String mBackground;
	private String mForeground;
	private long mPackageId;

	public Language[] getLanguages() {
		return mNames;
	}

	public void setPackageId(long packageId) {
		mPackageId = packageId;
	}

	public long getPackageId() {
		return mPackageId;
	}

	public String getForeground() {
		return mForeground;
	}

	public String getBackground() {
		return mBackground;
	}

	public void setForeground(String foreground) {
		mForeground = foreground;
	}

	public void setBackground(String background) {
		mBackground = background;
	}
}
