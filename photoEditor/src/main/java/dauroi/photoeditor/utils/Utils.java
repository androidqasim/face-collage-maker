package dauroi.photoeditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.ImageView;

import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.api.FileService;
import dauroi.photoeditor.config.ALog;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static final String BIG_D_FOLDER = Environment.getExternalStorageDirectory().toString()
            .concat("/Android/data/com.codetho.photocollage");
    public static final String TEMP_FOLDER = BIG_D_FOLDER.concat("/Temp");
    public static final String FILE_FOLDER = BIG_D_FOLDER.concat("/files");
    public static final String ROOT_EDITED_IMAGE_FOLDER = FILE_FOLDER.concat("/edited");
    public static final String EDITED_IMAGE_FOLDER = ROOT_EDITED_IMAGE_FOLDER.concat("/images");
    public static final String EDITED_IMAGE_THUMBNAIL_FOLDER = ROOT_EDITED_IMAGE_FOLDER.concat("/thumbnails");
    public static final String CROP_FOLDER = FILE_FOLDER.concat("/crop");
    public static final String FRAME_FOLDER = FILE_FOLDER.concat("/frame");
    public static final String FILTER_FOLDER = FILE_FOLDER.concat("/filter");
    public static final String BACKGROUND_FOLDER = FILE_FOLDER.concat("/background");
    public static final String STICKER_FOLDER = FILE_FOLDER.concat("/sticker");

    private Utils() {
    }

    public static File copyFileFromAsset(Context context, final String outFolder, final String assetFilePath, boolean override) {
        try {
            File file = new File(assetFilePath);
            final String outFilePath = outFolder.concat("/").concat(file.getName());
            file = new File(outFilePath);
            if (!file.exists() || file.length() == 0 || override) {
                InputStream is = context.getAssets().open(assetFilePath);
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buff = new byte[2048];
                int len = -1;
                while ((len = is.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void displayImageAsync(final String cloudPath, final ImageView imageView, final boolean offline) {
        List<ImageView> imageViews = new ArrayList<ImageView>();
        imageViews.add(imageView);
        displayImageAsync(cloudPath, imageViews, offline);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void displayImageAsync(final String cloudPath, final List<ImageView> imageViews,
                                         final boolean offline) {
        try {
            ALog.d(TAG, "displayImageAsync, cloudPath=" + cloudPath);
            String name = SecurityUtils.sha256s(cloudPath);
            final File file = new File(FILE_FOLDER, name);

            AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>() {
                Bitmap bitmap;

                @Override
                protected File doInBackground(Void... params) {
                    long time = System.currentTimeMillis();
                    if (file.exists()) {
                        bitmap = ImageDecoder.decodeFileToBitmap(file.getAbsolutePath());
                        ALog.d(TAG, "displayImageAsync, decode taken time=" + (System.currentTimeMillis() - time));
                        publishProgress();
                    }
                    time = System.currentTimeMillis();
                    try {
                        if (!offline && NetworkUtils.checkNetworkAvailable(PhotoEditorApp.getAppContext())) {
                            File file = FileService.downloadFile(ProfileCache.getToken(PhotoEditorApp.getAppContext()),
                                    cloudPath, null);
                            ALog.d(TAG, "displayImageAsync, download time=" + (System.currentTimeMillis() - time));
                            return file;
                        } else {
                            return null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                    ALog.d(TAG, "onProgressUpdate");
                    if (bitmap != null && imageViews != null && imageViews.size() > 0) {
                        for (ImageView imageView : imageViews)
                            imageView.setImageBitmap(bitmap);
                    }
                    ALog.d(TAG, "onProgressUpdate, end");
                }

                @Override
                protected void onPostExecute(File file) {
                    super.onPostExecute(file);
                    long time = System.currentTimeMillis();
                    if (file != null && imageViews != null && imageViews.size() > 0) {
                        Bitmap bitmap = ImageDecoder.decodeFileToBitmap(file.getAbsolutePath());
                        for (ImageView imageView : imageViews)
                            imageView.setImageBitmap(bitmap);
                    }

                    ALog.d(TAG, "onPostExecute, display image=" + (System.currentTimeMillis() - time));
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static File savedAndRemoveCachedImage(String url) {
        // try {
        // ImageLoader imageLoader = ImageLoader.getInstance();
        // File oldImage = imageLoader.getDiskCache().get(url);
        // if (!oldImage.exists()) {
        // return null;
        // }
        //
        // String tempName = SecurityUtils.sha256s(url);
        // File tempFile = new File(TEMP_FOLDER, tempName);
        // copyFile(oldImage, tempFile);
        // if (oldImage.exists()) {
        // oldImage.deleteAllItemInPackage();
        // }
        //
        // DiskCacheUtils.removeFromCache(url, imageLoader.getDiskCache());
        // MemoryCacheUtils.removeFromCache(url, imageLoader.getMemoryCache());
        // return tempFile;
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }

        return null;
    }

    public static boolean copyFile(File source, File dest) {
        try {
            dest.getParentFile().mkdirs();
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(dest);
            byte[] buff = new byte[2048];
            int len = -1;
            while ((len = fis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }

            fos.flush();

            fis.close();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void clearCachedImage(String url) {
        // ImageLoader imageLoader = ImageLoader.getInstance();
        // MemoryCacheUtils.removeFromCache(url, imageLoader.getMemoryCache());
        // DiskCacheUtils.removeFromCache(url, imageLoader.getDiskCache());
    }

    public static void saveObjectInPrefs(SharedPreferences prefs, Object obj, String key) {
        final GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(obj);
        prefs.edit().putString(key, json).commit();
    }

    public static Object retrieveObjectInPrefs(SharedPreferences prefs, String key, Class<?> cls) {
        String json = prefs.getString(key, "");
        Object obj = null;
        if (json != null && json.length() > 0) {
            final GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            obj = gson.fromJson(json, cls);
        }

        return obj;
    }

    @SuppressLint("NewApi")
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll()
                    .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                // vmPolicyBuilder
                // .setClassInstanceLimit(ImageGridActivity.class, 1)
                // .setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed
        // behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static String stringTransform(String s, int i) {
        char[] chars = s.toCharArray();
        for (int j = 0; j < chars.length; j++)
            chars[j] = (char) (chars[j] ^ i);
        return String.valueOf(chars);
    }
}
