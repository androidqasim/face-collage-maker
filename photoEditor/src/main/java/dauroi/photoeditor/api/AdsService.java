package dauroi.photoeditor.api;

import com.google.gson.Gson;

import dauroi.photoeditor.api.response.CheckShowingAdsResponse;
import dauroi.photoeditor.api.response.GetAppInfoResponse;
import dauroi.photoeditor.api.response.GetBigDAdsResponse;
import dauroi.photoeditor.api.response.GetUpdateInfoResponse;
import dauroi.photoeditor.api.response.ListAdsResponse;
import dauroi.photoeditor.utils.GsonUtils;
import dauroi.photoeditor.utils.SecurityUtils;
import dauroi.photoeditor.utils.SecurityUtils.Signature;

/**
 * Created by Wisekey on 5/10/2015.
 */
public class AdsService extends BaseService {
	public static GetBigDAdsResponse getBigDAds(String language) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/ads/bigd_ads", null);
		if (language != null && language.length() > 0) {
			path = path.concat("&language=").concat(language);
		}

		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetBigDAdsResponse resp = gson.fromJson(str, GetBigDAdsResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static CheckShowingAdsResponse checkShowingAds() throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/ads/show_ads", null), "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			CheckShowingAdsResponse resp = gson.fromJson(str, CheckShowingAdsResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static GetUpdateInfoResponse getUpdateInfo(String packageName) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/ads/check_update", null).concat("&app=").concat(packageName);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetUpdateInfoResponse resp = gson.fromJson(str, GetUpdateInfoResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static GetAppInfoResponse listBigDApp() throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/ads/app", null), "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetAppInfoResponse resp = gson.fromJson(str, GetAppInfoResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static ListAdsResponse getGoogleAdsCycle() throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/ads/ads", null), "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			ListAdsResponse resp = gson.fromJson(str, ListAdsResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static GetBigDAdsResponse getBigDAds() throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/ads/bigd_ads", null), "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetBigDAdsResponse resp = gson.fromJson(str, GetBigDAdsResponse.class);
			return resp;
		} else {
			return null;
		}
	}
}
