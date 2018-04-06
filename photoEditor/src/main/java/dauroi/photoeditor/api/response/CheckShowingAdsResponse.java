package dauroi.photoeditor.api.response;

/**
 * Created by Wisekey on 5/10/2015.
 */
public class CheckShowingAdsResponse extends BaseResponse {
	private boolean mShowAds = false;
	private boolean mGoogleAds = false;
	private boolean mFacebookAds = false;
	private int mStep = 5;

	public int getStep() {
		return mStep;
	}

	public boolean isFacebookAds() {
		return mFacebookAds;
	}

	public boolean isGoogleAds() {
		return mGoogleAds;
	}

	public boolean isShowAds() {
		return mShowAds;
	}
}
