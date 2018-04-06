package com.codetho.photocollage.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.ui.MainActivity;

/**
 * Created by admin on 6/1/2016.
 */
public class CommonUtils {
    private static final String TAG = CommonUtils.class.getSimpleName();

    public static boolean isStoreVersion(Context context) {
        boolean result = false;

        try {
            String installer = context.getPackageManager()
                    .getInstallerPackageName(context.getPackageName());
            result = (installer != null && installer.trim().length() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getDeviceId(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = tm.getDeviceId();
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        String serial = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) serial = Build.SERIAL;
        String deviceId = "0123456789";
        if (tmDevice != null) deviceId = "01" + tmDevice;
        else if (androidId != null) deviceId = "02" + androidId;
        else if (serial != null) deviceId = "03" + serial;
        ALog.d(TAG, "deviceId=" + deviceId);
        return deviceId;
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        if (c instanceof MainActivity) {
                            ((Activity) c).finish();
                        } else {
                            System.exit(0);
                        }
                    } else {
                        ALog.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    ALog.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                ALog.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            ALog.e(TAG, "Was not able to restart application");
        }
    }
}
