package dauroi.photoeditor.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AdvancedDownloadFileManger {
    private String mUrl;
    private String mTitle;
    private String mDescription;
    private DownloadManager downloadManager;
    long downloadReference;
    private Context mContext;
    private String mSubPath;
    private OnDownloadFileListener mListener;
    private boolean mUnregistered = false;
    private String mDownloadedFile;

    public AdvancedDownloadFileManger(Context context, String url,
                                      String title, String desc, OnDownloadFileListener listener) {
        mUrl = url;
        mTitle = title;
        mContext = context;
        try {
            mSubPath = SecurityUtils.sha256s(url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            mSubPath = title + System.currentTimeMillis();
        }
        mDescription = desc;
        mListener = listener;
    }

    public AdvancedDownloadFileManger(Context context, String subPath,
                                      String url, String title, String desc,
                                      OnDownloadFileListener listener) {
        mUrl = url;
        mTitle = title;
        mContext = context;
        mSubPath = subPath;
        mDescription = desc;
        mListener = listener;
    }

    public void setDownloadInfo(String url, String title, String desc) {
        this.mTitle = title;
        this.mUrl = url;
        this.mDescription = desc;
    }

    @SuppressLint("InlinedApi")
    public void execute() {
        if (mListener != null) {
            mListener.onStartDownloading();
        }
        try {
            downloadManager = (DownloadManager) mContext
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(mUrl);
            DownloadManager.Request request = new DownloadManager.Request(
                    Download_Uri);

            // Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            // Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(false);
            // Set the title of this download, to be displayed in notifications (if
            // enabled).
            request.setTitle(mTitle);
            // Set a description of this download, to be displayed in notifications
            // (if enabled)
            request.setDescription(mDescription);
            // Set the local destination for the downloaded file to a path within
            // the application's external files directory
            request.setDestinationInExternalFilesDir(mContext,
                    Environment.DIRECTORY_DOWNLOADS, mSubPath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            // Enqueue a new download and same the referenceId
            downloadReference = downloadManager.enqueue(request);
            // set filter to only when download is complete and register broadcast
            // receiver
            IntentFilter filter = new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            mContext.registerReceiver(downloadReceiver, filter);
            mUnregistered = false;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void unregisterReceiver() {
        if (mUnregistered) {
            return;
        }
        try {
            mContext.unregisterReceiver(downloadReceiver);
            mUnregistered = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int showStatus() {
        Query myDownloadQuery = new Query();
        // set the query filter to our previously Enqueued download
        myDownloadQuery.setFilterById(downloadReference);

        // Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(myDownloadQuery);
        if (cursor.moveToFirst()) {
            return checkStatus(cursor, true);
        }

        return -1;
    }

    public int getStatus() {
        Query myDownloadQuery = new Query();
        // set the query filter to our previously Enqueued download
        myDownloadQuery.setFilterById(downloadReference);

        // Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(myDownloadQuery);
        if (cursor.moveToFirst()) {
            return checkStatus(cursor, false);
        }

        return -1;
    }

    private int checkStatus(Cursor cursor, boolean showToast) {

        // column for status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        // column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        // get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
        String filename = cursor.getString(filenameIndex);
        int localFilenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        mDownloadedFile = cursor.getString(localFilenameIndex);
        String statusText = "";
        String reasonText = "";
        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

        if (showToast) {
            Toast toast = Toast.makeText(mContext, statusText + "\n"
                    + reasonText, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
        }

        return status;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                // Unregister itself
                unregisterReceiver();
                int status = getStatus();
                if (mListener != null && status == DownloadManager.STATUS_SUCCESSFUL) {
                    mListener.onFinishDownloading(mDownloadedFile);
                }
            }
        }
    };

    public interface OnDownloadFileListener {
        public void onStartDownloading();

        public void onFinishDownloading(String downloadedPath);
    }
}
