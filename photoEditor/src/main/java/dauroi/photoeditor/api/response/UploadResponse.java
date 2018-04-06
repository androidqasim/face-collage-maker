package dauroi.photoeditor.api.response;

/**
 * Created by Wisekey on 4/27/2015.
 */
public class UploadResponse extends BaseResponse {
	private String mUrl;
	private String mPathOnCloud;
	private long mTotalBytes = 0;
	private long mUploadedBytes = 0;
	private String mMd5;

	public String getUrl() {
		return mUrl;
	}

	public long getUploadedBytes() {
		return mUploadedBytes;
	}

	public String getPathOnCloud() {
		return mPathOnCloud;
	}

	public long getTotalBytes() {
		return mTotalBytes;
	}

	public String getMd5() {
		return mMd5;
	}
}
