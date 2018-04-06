package dauroi.photoeditor.api;

import com.google.gson.Gson;

import dauroi.photoeditor.api.response.GetStoreItemResponse;
import dauroi.photoeditor.api.response.ListStoreItemResponse;
import dauroi.photoeditor.api.response.RegisterPushResponse;
import dauroi.photoeditor.api.response.UnregisterPushResponse;
import dauroi.photoeditor.utils.GsonUtils;
import dauroi.photoeditor.utils.SecurityUtils;
import dauroi.photoeditor.utils.SecurityUtils.Signature;

public class StoreItemService extends BaseService {
	public static RegisterPushResponse registerToReceivePushNotification(String sessionToken, String regId, String os)
			throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/push/register", sessionToken).concat("&id=").concat(regId).concat("&os=")
				.concat(os);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			RegisterPushResponse resp = gson.fromJson(str, RegisterPushResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static UnregisterPushResponse unregisterToReceivePushNotification(String sessionToken, String regId,
																			 String os) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/push/unregister", sessionToken).concat("&id=").concat(regId).concat("&os=")
				.concat(os);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			UnregisterPushResponse resp = gson.fromJson(str, UnregisterPushResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static GetStoreItemResponse downloadCount(String sessionToken, String itemId) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/items/download_count", sessionToken).concat("&id=").concat(itemId);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetStoreItemResponse resp = gson.fromJson(str, GetStoreItemResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static GetStoreItemResponse view(String sessionToken, String itemId) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/items/view", sessionToken).concat("&id=").concat(itemId);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetStoreItemResponse resp = gson.fromJson(str, GetStoreItemResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static ListStoreItemResponse getStoreItems(String sessionToken, String type, String language, int offset,
			int limit) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = null;
		if (type != null && type.length() > 0) {
			path = buildCommonPath("/api/items/list", sessionToken).concat("&language=").concat(language)
					.concat("&type=").concat(type) + "&offset=" + offset + "&limit=" + limit;
		} else {
			path = buildCommonPath("/api/items/list", sessionToken).concat("&language=").concat(language) + "&offset="
					+ offset + "&limit=" + limit;
		}

		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			ListStoreItemResponse resp = gson.fromJson(str, ListStoreItemResponse.class);
			return resp;
		} else {
			return null;
		}
	}
}
