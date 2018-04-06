package dauroi.photoeditor.api.response;

public class GetUpdateInfoResponse extends BaseResponse {
	private String mAppName;
	private String mPackageName;
	private String mVersion;
	private String mFileName;
	private boolean mActive;

	public String getAppName() {
		return mAppName;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getVersion() {
		return mVersion;
	}

	public String getFileName() {
		return mFileName;
	}

	public boolean isActive() {
		return mActive;
	}
}
