package dauroi.photoeditor;

import android.app.Application;
import android.content.Context;

public class PhotoEditorApp extends Application {
	private static Context context;
	private static PhotoEditorApp instance;

	public static PhotoEditorApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		context = this;
	}

	public static Context getAppContext() {
		return context;
	}
}
