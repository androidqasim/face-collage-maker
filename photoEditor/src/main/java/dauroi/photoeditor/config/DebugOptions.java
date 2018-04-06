package dauroi.photoeditor.config;

import android.content.Context;

import dauroi.photoeditor.BuildConfig;

/**
 * Config all debug options in application. In release version, all value must
 * set to FALSE
 */
public class DebugOptions {
    /**
     * Set enable/disable log in logcat
     */
    public static final boolean ENABLE_LOG = BuildConfig.DEBUG;

    public static final boolean ENABLE_DEBUG = BuildConfig.DEBUG;

    public static final boolean ENABLE_FOR_DEV = false;

    public static boolean isProVersion(Context context) {
        if (context == null) {
            return false;
        }

        final String packageName = context.getPackageName(); //&& CommonUtils.isStoreVersion(PhotoCollageApp.getAppContext());
        if (packageName != null && packageName.equals("com.codetho.photocollagepro")) {
            return true;
        } else {
            return false;
        }
    }
}
