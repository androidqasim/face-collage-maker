package dauroi.photoeditor.config;

import android.content.Context;
import android.content.SharedPreferences;
import dauroi.photoeditor.PhotoEditorApp;

public class AppConfig {
	private static final String APP_PREF_NAME = "appPref";
	private static final String LANGUAGE_KEY = "language";
	public static final String DEFAULT_LANGUAGE = "en";

	public static void setLanguage(String lang) {
		SharedPreferences pref = PhotoEditorApp.getAppContext().getSharedPreferences(APP_PREF_NAME,
				Context.MODE_PRIVATE);
		pref.edit().putString(LANGUAGE_KEY, lang).commit();
	}

	public static String getLanguage() {
		SharedPreferences pref = PhotoEditorApp.getAppContext().getSharedPreferences(APP_PREF_NAME,
				Context.MODE_PRIVATE);
		return pref.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
	}
}
