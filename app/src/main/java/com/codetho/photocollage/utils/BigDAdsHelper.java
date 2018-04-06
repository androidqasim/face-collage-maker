package com.codetho.photocollage.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.receiver.PackageInstallReceiver;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dauroi.photoeditor.api.AdsService;
import dauroi.photoeditor.api.response.BigDAds;
import dauroi.photoeditor.api.response.GetBigDAdsResponse;
import dauroi.photoeditor.utils.FileUtils;
import dauroi.photoeditor.utils.PhotoUtils;

/**
 * Created by admin on 7/23/2016.
 */
public class BigDAdsHelper {
    private static List<ApplicationInfo> installedApps;

    private Context mContext;
    private View mBigDAdsLayout;
    private ImageView mAppIconView;
    private TextView mAppNameView;
    private TextView mAppDetailView1;
    private TextView mAppDetailView2;

    public static void clearInstalledApp() {
        if (installedApps != null) installedApps.clear();
        installedApps = null;
    }

    public static void addInstalledApp(String packageName) {
        if (installedApps != null) {
            ApplicationInfo info = new ApplicationInfo();
            info.packageName = packageName;
            installedApps.add(info);
        }
    }

    public static List<ApplicationInfo> getInstalledApps(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> installedApps = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                installedApps.add(app);
            }
        }
        ALog.d("BigAdsHelper", "getInstalledApps, count=" + installedApps.size());
        return installedApps;
    }

    private boolean containApps(List<ApplicationInfo> installedApp, String appURL) {
        for (ApplicationInfo info : installedApp)
            if (appURL.contains(info.packageName)) {
                return true;
            }
        return false;
    }

    public BigDAdsHelper(Context context) {
        mContext = context;
        mBigDAdsLayout = LayoutInflater.from(context).inflate(R.layout.bigd_ads_banner, null);
        mAppIconView = (ImageView) mBigDAdsLayout.findViewById(R.id.appIcon);
        mAppNameView = (TextView) mBigDAdsLayout.findViewById(R.id.nameView);
        mAppDetailView1 = (TextView) mBigDAdsLayout.findViewById(R.id.firstDetailView);
        mAppDetailView2 = (TextView) mBigDAdsLayout.findViewById(R.id.secondDetailView);
    }

    public void showSecondDetailView(boolean show) {
        if (show) {
            mAppDetailView2.setVisibility(View.VISIBLE);
        } else {
            mAppDetailView2.setVisibility(View.GONE);
        }

    }

    public void showDetailViews(boolean show) {
        if (show) {
            mAppDetailView1.setVisibility(View.VISIBLE);
            mAppDetailView2.setVisibility(View.VISIBLE);
        } else {
            mAppDetailView1.setVisibility(View.GONE);
            mAppDetailView2.setVisibility(View.GONE);
        }

    }

    public void attach(ViewGroup parent) {
        parent.removeView(mBigDAdsLayout);
        parent.addView(mBigDAdsLayout);
    }

    public boolean isVisible() {
        return mBigDAdsLayout.getVisibility() == View.VISIBLE;
    }

    public void detach(ViewGroup parent) {
        parent.removeView(mBigDAdsLayout);
    }

    /**
     * Called after attach() method
     */
    public void asyncLoadBigDAds() {
        final AsyncTask<Void, GetBigDAdsResponse, GetBigDAdsResponse> task = new AsyncTask<Void, GetBigDAdsResponse, GetBigDAdsResponse>() {
            private String mDownloadedThumbnail;
            private List<Pair<String, String>> mDownloadingImages = new ArrayList<>();
            BigDAds bigDAds = null;

            private void setData(GetBigDAdsResponse result) {
                if (result != null && result.getBigdAds() != null && result.getBigdAds().length > 0) {
                    // set data for dialog
                    if (bigDAds == null) {
                        mBigDAdsLayout.setVisibility(View.GONE);
                        return;
                    }

                    if (mDownloadedThumbnail != null) {
                        try {
                            PhotoUtils.loadImageWithGlide(mContext, mAppIconView, mDownloadedThumbnail);
                        } catch (OutOfMemoryError err) {
                            err.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    mAppNameView.setText(bigDAds.getTitle());
                    if (bigDAds.getContent() != null && bigDAds.getContent().length >= 2) {
                        mAppDetailView1.setText(bigDAds.getContent()[0]);
                        mAppDetailView2.setText(bigDAds.getContent()[1]);
                    } else if (bigDAds.getContent() != null && bigDAds.getContent().length == 1) {
                        mAppDetailView1.setText(bigDAds.getContent()[0]);
                    }

                    mBigDAdsLayout.setVisibility(View.VISIBLE);
                    final String appURL = bigDAds.getUrl();
                    mBigDAdsLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                PackageInstallReceiver.clickedApp = appURL;
                                try {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse("market://details?id=" + bigDAds.getPackageId()));
                                    mContext.startActivity(i);
                                } catch (Exception ex) {
                                    try {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(bigDAds.getUrl()));
                                        mContext.startActivity(i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(mContext, mContext.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            report("home/clicked_bigd_ads");
                        }
                    });
                } else {
                    mBigDAdsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            protected GetBigDAdsResponse doInBackground(Void... params) {
                try {
                    String lang = Locale.getDefault().getLanguage();
                    if (!"vi".equalsIgnoreCase(lang) && !"ja".equalsIgnoreCase(lang) && !"ru".equalsIgnoreCase(lang)) {
                        lang = "en";
                    }

                    GetBigDAdsResponse result = AdsService.getBigDAds(lang);
                    if (result != null && result.getBigdAds() != null && result.getBigdAds().length > 0) {
                        if (installedApps == null) {
                            installedApps = getInstalledApps(mContext);
                        }

                        for (int idx = 0; idx < result.getBigdAds().length; idx++) {
                            if (result.getBigdAds()[idx].getUrl() != null && !result.getBigdAds()[idx].getUrl().contains(mContext.getPackageName())
                                    && !containApps(installedApps, result.getBigdAds()[idx].getUrl())) {
                                bigDAds = result.getBigdAds()[idx];
                                break;
                            }
                        }

                        if (bigDAds != null && bigDAds.getThumbnail() != null && bigDAds.getThumbnail().length() > 0) {
                            mDownloadedThumbnail = FileUtils.TEMP_FOLDER.concat("/")
                                    .concat(FileUtils.sha128s(bigDAds.getThumbnail()))
                                    .concat(".png");
                            File file = new File(mDownloadedThumbnail);
                            if (file.exists() && file.length() > 0) {
                                Pair<String, String> thumbnail = new Pair<String, String>(bigDAds.getThumbnail(),
                                        mDownloadedThumbnail);
                                mDownloadingImages.add(thumbnail);
                            } else {
                                FileUtils.downloadFile(bigDAds.getThumbnail(),
                                        mDownloadedThumbnail);
                            }
                        }
                    }

//                    if (mDownloadingImages.size() > 0) {
//                        publishProgress(new GetBigDAdsResponse[]{result});
//                        for (Pair<String, String> pair : mDownloadingImages) {
//                            FileUtils.downloadFile(pair.first, pair.second);
//                        }
//                    }

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(GetBigDAdsResponse... values) {
                super.onProgressUpdate(values);
                if (values != null && values.length > 0) {
                    setData(values[0]);
                }
            }

            @Override
            protected void onPostExecute(GetBigDAdsResponse result) {
                super.onPostExecute(result);
                setData(result);
            }

        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    private void report(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "home");
        FirebaseAnalytics.getInstance(mContext).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
