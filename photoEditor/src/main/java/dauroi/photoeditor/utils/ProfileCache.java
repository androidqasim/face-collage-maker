package dauroi.photoeditor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import dauroi.photoeditor.PhotoEditorApp;

/**
 * Created by vanhu_000 on 3/11/2015.
 */
public class ProfileCache {
    private static final String PREF_ACCESS_TOKEN = "Pref_accessToken";

    private static final String KEY_EMAIL_ADDRESS = "Key_Profile_Email_Address";
    public static final String KEY_DISPLAY_NAME = "Key_Profile_Display_Name";
    public static final String KEY_USER_NAME = "userName";

    public static final String KEY_PHOTO_URL = "Key_Profile_Photo_Url";
    public static final String KEY_USER_ID = "Key_User_Id";
    public static final String KEY_FOLLOWER_NUM = "Key_Follower_Num";
    public static final String KEY_FOLLOWING_NUM = "Key_Following_Num";
    public static final String KEY_COINS_AMOUNT = "Key_Coins_Amount";
    private static final String USER_INFO_REFERENCE = "USER_INFO_REFERENCE";
    public static final String GCM_REGISTRATION_TOKEN = "gcmToken";

    public static SharedPreferences getCachePreference() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        return prefs;
    }

    public static void saveUserInfo(String email, String displayName, String username) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putString(KEY_EMAIL_ADDRESS, email)
                .putString(KEY_DISPLAY_NAME, displayName)
                .putString(KEY_USER_NAME, username)
                .commit();
    }

    public static void saveDisplayName(String displayName) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putString(KEY_DISPLAY_NAME, displayName).commit();
    }

    public static String[] getEmailAndDisplayName() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        String email = prefs.getString(KEY_EMAIL_ADDRESS, "");
        String displayName = prefs.getString(KEY_DISPLAY_NAME, "");
        return new String[]{email, displayName};
    }

    public static String getDisplayName() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        String displayName = prefs.getString(KEY_DISPLAY_NAME, "");
        return displayName;
    }

    public static String getUserName() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        String displayName = prefs.getString(KEY_USER_NAME, "");
        return displayName;
    }

    public static String getEmail() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        String email = prefs.getString(KEY_EMAIL_ADDRESS, "");
        return email;
    }

    public static void savePhotoUrl(String photoUrl) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putString(KEY_PHOTO_URL, photoUrl).commit();

    }

    public static String getPhotoUrl() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PHOTO_URL, null);

    }

    public static void saveUserId(long userId) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putLong(KEY_USER_ID, userId).commit();

    }

    public static long getUserId() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_USER_ID, 0);

    }

    public static void saveFollower(long follower) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putLong(KEY_FOLLOWER_NUM, follower).commit();

    }

    public static long getFollower() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_FOLLOWER_NUM, 0);

    }

    public static void saveFollowing(long following) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putLong(KEY_FOLLOWING_NUM, following).commit();

    }

    public static long getFollowing() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_FOLLOWING_NUM, 0);

    }

    public static void saveCoinsAmount(long coins) {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.putLong(KEY_COINS_AMOUNT, coins).commit();

    }

    public static long getCoinsAmount() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences prefs = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_COINS_AMOUNT, 0);

    }

    public static void clearUserCachedInfo() {
        Context appContext = PhotoEditorApp.getAppContext();
        SharedPreferences.Editor edit = appContext.getSharedPreferences(USER_INFO_REFERENCE, Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();
    }

    public static void saveToken(Context ctx, String accessToken) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putString(PREF_ACCESS_TOKEN, accessToken).commit();
    }

    public static String getToken(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return pref.getString(PREF_ACCESS_TOKEN, "");
    }

    public static void saveGcmToken(Context ctx, String gcmToken) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putString(GCM_REGISTRATION_TOKEN, gcmToken).commit();
    }

    public static String getGcmToken(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return pref.getString(GCM_REGISTRATION_TOKEN, "");
    }
}
