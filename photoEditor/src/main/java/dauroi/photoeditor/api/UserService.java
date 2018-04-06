package dauroi.photoeditor.api;

import com.google.gson.Gson;

import dauroi.photoeditor.api.request.ChangePasswordRequest;
import dauroi.photoeditor.api.request.LoginRequest;
import dauroi.photoeditor.api.request.RegisterRequest;
import dauroi.photoeditor.api.response.ChangeFullNameResponse;
import dauroi.photoeditor.api.response.ChangePasswordResponse;
import dauroi.photoeditor.api.response.GetProfileResponse;
import dauroi.photoeditor.api.response.LoginResponse;
import dauroi.photoeditor.api.response.LogoutResponse;
import dauroi.photoeditor.api.response.RegisterResponse;
import dauroi.photoeditor.api.response.ResetPasswordResponse;
import dauroi.photoeditor.utils.GsonUtils;
import dauroi.photoeditor.utils.SecurityUtils;
import dauroi.photoeditor.utils.SecurityUtils.Signature;

public class UserService extends BaseService {

	public static GetProfileResponse getProfile(String sessionToken) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/users/profile", sessionToken);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			GetProfileResponse resp = gson.fromJson(str, GetProfileResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static RegisterResponse createUser(RegisterRequest request) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String body = gson.toJson(request);
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/users/register", null), "POST");
		String str = requestPath(signature.signedPath, "POST", body);
		if (str != null && str.length() > 0) {
			RegisterResponse resp = gson.fromJson(str, RegisterResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static LoginResponse login(LoginRequest request) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String body = gson.toJson(request);
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/users/login", null), "POST");
		String str = requestPath(signature.signedPath, "POST", body);
		if (str != null && str.length() > 0) {
			LoginResponse resp = gson.fromJson(str, LoginResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static ResetPasswordResponse requestPassword(String email) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/users/forgot_password", null).concat("&email=").concat(email);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			ResetPasswordResponse resp = gson.fromJson(str, ResetPasswordResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static ChangePasswordResponse changePassword(String sessionToken, ChangePasswordRequest request)
			throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String body = gson.toJson(request);
		Signature signature = SecurityUtils.signSimplePath(buildCommonPath("/api/users/change_password", sessionToken),
				"POST");
		String str = requestPath(signature.signedPath, "POST", body);
		if (str != null && str.length() > 0) {
			ChangePasswordResponse resp = gson.fromJson(str, ChangePasswordResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static LogoutResponse logout(String sessionToken) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/users/logout", sessionToken);
		Signature signature = SecurityUtils.signSimplePath(path, "POST");
		String str = requestPath(signature.signedPath, "POST", null);
		if (str != null && str.length() > 0) {
			LogoutResponse resp = gson.fromJson(str, LogoutResponse.class);
			return resp;
		} else {
			return null;
		}
	}

	public static ChangeFullNameResponse changeFullName(String sessionToken, String fullName) throws Exception {
		Gson gson = GsonUtils.createAndroidStyleGson();
		String path = buildCommonPath("/api/users/change_full_name", sessionToken).concat("&name=").concat(fullName);
		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		String str = requestPath(signature.signedPath, "GET", null);
		if (str != null && str.length() > 0) {
			ChangeFullNameResponse resp = gson.fromJson(str, ChangeFullNameResponse.class);
			return resp;
		} else {
			return null;
		}
	}
}
