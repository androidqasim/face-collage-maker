package com.codetho.photocollage.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.codetho.photocollage.R;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.PhotoEditorApp;

public class PermissionUtils {
    public static class Permission {
        public Permission(String permissionName, String displayName) {
            this.permissionName = permissionName;
            this.displayName = displayName;
        }

        public Permission(String permissionName, String displayName, boolean isGranted) {
            this.permissionName = permissionName;
            this.displayName = displayName;
            this.isGranted = isGranted;
        }

        public String permissionName;
        public String displayName;
        public boolean isGranted;
    }

    private static final String PERMISSION_PREF_NAME = "permissionPref";
    private static final String REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION_KEY = "REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION";

    public static ArrayList<Permission> sPermissions;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 7889;

    public static void checkAndRequestSystemAlertWindowPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SharedPreferences pref = context.getSharedPreferences(PERMISSION_PREF_NAME, Context.MODE_PRIVATE);
            if (pref.getBoolean(REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION_KEY, false)) {
                requestSystemAlertWindowPermission(context);
            }
        }
    }

    public static void setRequestingSystemAlertWindowPermission(Context context, boolean need) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SharedPreferences pref = context.getSharedPreferences(PERMISSION_PREF_NAME, Context.MODE_PRIVATE);
            pref.edit().putBoolean(REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION_KEY, need).commit();
        }
    }

    public static boolean requestSystemAlertWindowPermission(Activity context) {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isGranted = context.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED;
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            if (!isGranted) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                myIntent.setData(Uri.parse("package:" + context.getPackageName()));
                try {
                    context.startActivity(myIntent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void createPermissionsCheckListIfNeed() {
        if (Build.VERSION.SDK_INT < 23) return;
        Context context = PhotoEditorApp.getAppContext();
        if (sPermissions == null || sPermissions.size() == 0) {
            sPermissions = new ArrayList<Permission>();
            sPermissions.add(new Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getString(R.string.permission_write_ex_storage)));
        }
        for (Permission item : sPermissions) {
            item.isGranted = context.checkSelfPermission(item.permissionName) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermissionsGranted(final Activity activity) {
        if (Build.VERSION.SDK_INT < 23) return true;
        createPermissionsCheckListIfNeed();
        String[] notGrantedList = getPackageNameNotGrantedArray(sPermissions);
        if (notGrantedList != null && notGrantedList.length > 0) {
            String message = activity.getString(R.string.permission_guide);
            for (Permission item : sPermissions) {
                if (!item.isGranted) {
                    message += "\n    " + item.displayName;
                }
            }
            DialogUtils.showConfirmDialog(activity, activity.getString(R.string.app_name),
                    message, new DialogUtils.ConfirmDialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {
                            activity.requestPermissions(getPackageNameNotGrantedArray(sPermissions),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }

                        @Override
                        public void onCancelButtonOnClick() {
                            Toast.makeText(activity, activity.getString(R.string.permission_denied_and_guide_to_setting),
                                    Toast.LENGTH_LONG).show();
                            goAppSetting(activity);
                        }
                    });
            return false;
        }
        return true;

    }

    private static String[] getPackageNameNotGrantedArray(ArrayList<Permission> permissions) {
        List<String> list = new ArrayList<String>();
        for (Permission item : permissions) {
            if (!item.isGranted) {
                list.add(item.permissionName);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static boolean isGrantedAllPermissions(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, activity.getString(R.string.permission_denied_and_guide_to_setting), Toast.LENGTH_LONG).show();
                goAppSetting(activity);
                return false;
            }
        }
        return true;
    }

    private static void goAppSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
        activity.finish();
    }

    public static void clearCheckList() {
        if (sPermissions != null) {
            sPermissions.clear();
            sPermissions = null;
        }
    }
}
