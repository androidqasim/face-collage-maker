package com.codetho.photocollage.config;

import com.codetho.photocollage.BuildConfig;
import com.codetho.photocollage.ui.PhotoCollageApp;

/**
 * Config all debug options in application. In release version, all value must
 * set to FALSE
 */
public class DebugOptions {
    public static final boolean ENABLE_LOG = BuildConfig.DEBUG;

    public static final boolean ENABLE_DEBUG = BuildConfig.DEBUG;

    public static final boolean ENABLE_FOR_DEV = false;

    public static boolean isProVersion() {
        return dauroi.photoeditor.config.DebugOptions.isProVersion(PhotoCollageApp.getAppContext());
    }
}
