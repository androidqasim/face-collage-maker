package dauroi.photoeditor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.api.FileService;
import dauroi.photoeditor.api.StoreItemService;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.database.DatabaseManager;
import dauroi.photoeditor.database.table.CropTable;
import dauroi.photoeditor.database.table.FilterTable;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.database.table.ShadeTable;
import dauroi.photoeditor.database.table.StoreItemTable;
import dauroi.photoeditor.listener.OnInstallStoreItemListener;
import dauroi.photoeditor.model.CropInfo;
import dauroi.photoeditor.model.FilterInfo;
import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.model.Language;
import dauroi.photoeditor.model.ShadeInfo;

public class StoreUtils {
    private static final String TAG = StoreUtils.class.getSimpleName();
    private static final String STORE_PREF_NAME = "storePref";
    private static final String PURCHASED_ITEM_KEY = "purchasedItem";

    public static void uninstallItemPackage(Context context, ItemPackageInfo packageInfo) {
        ItemPackageTable itemPackageTable = new ItemPackageTable(context);
        itemPackageTable.delete(packageInfo.getId());
        String packageFolder = null;
        if (packageInfo.getType().equals(ItemPackageTable.FRAME_TYPE)
                || packageInfo.getType().equals(ItemPackageTable.BACKGROUND_TYPE)
                || packageInfo.getType().equals(ItemPackageTable.STICKER_TYPE)) {
            ShadeTable shadeTable = new ShadeTable(context);
            if (packageInfo.getType().equals(ItemPackageTable.FRAME_TYPE)) {
                shadeTable.deleteAllItemInPackage(packageInfo.getId(), ShadeTable.FRAME_TYPE);
                packageFolder = Utils.FRAME_FOLDER.concat("/").concat(packageInfo.getFolder());
            } else if (packageInfo.getType().equals(ItemPackageTable.BACKGROUND_TYPE)) {
                shadeTable.deleteAllItemInPackage(packageInfo.getId(), ItemPackageTable.BACKGROUND_TYPE);
                packageFolder = Utils.BACKGROUND_FOLDER.concat("/").concat(packageInfo.getFolder());
            } else if (packageInfo.getType().equals(ItemPackageTable.STICKER_TYPE)) {
                shadeTable.deleteAllItemInPackage(packageInfo.getId(), ItemPackageTable.STICKER_TYPE);
                packageFolder = Utils.STICKER_FOLDER.concat("/").concat(packageInfo.getFolder());
            }
        } else if (packageInfo.getType().equals(ItemPackageTable.SHADE_TYPE)) {
            ShadeTable shadeTable = new ShadeTable(context);
            shadeTable.deleteAllItemInPackage(packageInfo.getId(), ShadeTable.SHADE_TYPE);
            packageFolder = Utils.FRAME_FOLDER.concat("/").concat(packageInfo.getFolder());
        } else if (packageInfo.getType().equals(ItemPackageTable.CROP_TYPE)) {
            CropTable cropTable = new CropTable(context);
            cropTable.deleteAllItemInPackage(packageInfo.getId());
            packageFolder = Utils.CROP_FOLDER.concat("/").concat(packageInfo.getFolder());
        } else if (packageInfo.getType().equals(ItemPackageTable.FILTER_TYPE)) {
            FilterTable filterTable = new FilterTable(context);
            filterTable.deleteAllItemInPackage(packageInfo.getId());
            packageFolder = Utils.FILTER_FOLDER.concat("/").concat(packageInfo.getFolder());
        }
        //delete all files
        if (packageFolder != null) {
            File packageFile = new File(packageFolder);
            FileUtils.deleteFile(packageFile);
        }
    }

    public static void setPurchasedDevice() {
        if (!isPurchasedDevice()) {
            final SharedPreferences pref = PhotoEditorApp.getAppContext().getSharedPreferences(STORE_PREF_NAME, Context.MODE_PRIVATE);
            final String msg = getMsgToSignPurchasedDevice();
            String signature = SecureCache.getInstance().encodeHmacSHA256(msg);
            pref.edit().putString(PURCHASED_ITEM_KEY, signature).commit();
        }
    }

    public static boolean isPurchasedDevice() {
        final SharedPreferences pref = PhotoEditorApp.getAppContext().getSharedPreferences(STORE_PREF_NAME, Context.MODE_PRIVATE);
        final String sign = pref.getString(PURCHASED_ITEM_KEY, null);
        if (sign != null) {
            final String msg = getMsgToSignPurchasedDevice();
            String signature = SecureCache.getInstance().encodeHmacSHA256(msg);
            if (sign.equals(signature)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static String getMsgToSignPurchasedDevice() {
        final String deviceId = Settings.Secure.ANDROID_ID;
        String msg = PhotoEditorApp.getAppContext().getPackageName();
        if (deviceId != null && deviceId.length() > 0) {
            msg = msg.concat("\n").concat(deviceId);
        }

        return msg;
    }

    private static String getMsgToSign(StoreItem item) {
        final String deviceId = Settings.Secure.ANDROID_ID;
        String msg = item.getIdString().concat("\n").concat(item.getTitle()).concat("\n").concat(item.getType())
                .concat("\n").concat(item.getUrl());
        if (deviceId != null && deviceId.length() > 0) {
            msg = deviceId.concat("\n").concat(msg);
            ALog.d(TAG, "getMsgToSign, deviceId=".concat(deviceId));
        } else {
            ALog.d(TAG, "getMsgToSign, deviceId is null");
        }

        return msg;
    }

    private static boolean verify(StoreItem item) {
        final String msg = getMsgToSign(item);
        String signature = SecureCache.getInstance().encodeHmacSHA256(msg);
        if (signature.equals(item.getSignature())) {
            return true;
        } else {
            return false;
        }
    }

    public static void redownloadItems() {
        StoreItemTable table = new StoreItemTable(PhotoEditorApp.getAppContext());
        List<StoreItem> items = table.getAllRows();
        for (StoreItem item : items) {
            if (verify(item)) {
                downloadItem(item, true);
            } else {
                table.deleteItem(item.getIdString());
            }
        }
    }

    public static void downloadItem(final StoreItem item) {
        downloadItem(item, false);
    }

    public static void downloadItem(final StoreItem item, boolean redownload) {
        StoreItemTable table = new StoreItemTable(PhotoEditorApp.getAppContext());
        String msg = getMsgToSign(item);
        item.setSignature(SecureCache.getInstance().encodeHmacSHA256(msg));
        table.insertOrUpdate(item);
        item.setDownloadStatus(StoreItem.STATUS_DOWNLOADING);
        // start downloading
        final String url = FileService.getUploadedPath(ProfileCache.getToken(PhotoEditorApp.getAppContext()),
                item.getUrl(), FileService.VIDEO_TYPE);
        final AdvancedDownloadFileManger downloader = new AdvancedDownloadFileManger(PhotoEditorApp.getAppContext(),
                url, item.getTitle(), item.getDescription(), new AdvancedDownloadFileManger.OnDownloadFileListener() {

            @Override
            public void onStartDownloading() {
                for (OnInstallStoreItemListener listener : TempDataContainer.getInstance()
                        .getOnInstallStoreItemListeners())
                    if (listener != null) {
                        listener.onStartDownloading(item);
                    }
            }

            @Override
            public void onFinishDownloading(String downloadedPath) {
                installItem(item, downloadedPath);
            }
        });
        downloader.execute();
        //report to server
        if (!redownload) {
            final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        StoreItemService.downloadCount(ProfileCache.getToken(PhotoEditorApp.getAppContext()), item.getIdString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            };
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @SuppressLint("NewApi")
    private static void installItem(final StoreItem item, final String filePath) {
        AsyncTask<Void, Void, ItemPackageInfo> task = new AsyncTask<Void, Void, ItemPackageInfo>() {
            boolean update = false;

            @Override
            protected ItemPackageInfo doInBackground(Void... params) {

                boolean isOpen = DatabaseManager.getInstance().isOpenedDb();
                if (!isOpen) {
                    DatabaseManager.getInstance().openDb();
                }
                ItemPackageTable itemPackageTable = new ItemPackageTable(PhotoEditorApp.getAppContext());
                ItemPackageInfo packageInfo = new ItemPackageInfo();
                packageInfo.setTitle(item.getTitle());
                packageInfo.setType(item.getType());
                packageInfo.setThumbnail("thumbnail.jpg");
                packageInfo.setIdString(item.getIdString());
                final File zipFile = new File(item.getUrl());
                final String zipName = zipFile.getName();
                String currentInstalledPackage = zipName.substring(0, zipName.length() - ".zip".length());
                packageInfo.setFolder(currentInstalledPackage);

                long id = 0;
                ItemPackageInfo info = itemPackageTable.getRowWithStoreId(item.getIdString());
                if (info == null) {
                    id = itemPackageTable.insert(packageInfo);
                    update = false;
                } else {
                    id = info.getId();
                    itemPackageTable.update(packageInfo);
                    update = true;
                }

                if (item.getType().equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                    installCropPackage(id, currentInstalledPackage, filePath);
                } else if (item.getType().equalsIgnoreCase(ItemPackageTable.FRAME_TYPE) ||
                        item.getType().equalsIgnoreCase(ItemPackageTable.BACKGROUND_TYPE) ||
                        item.getType().equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
                    installFramePackage(id, currentInstalledPackage, filePath, item.getType());
                } else if (item.getType().equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                    installFilterPackage(id, currentInstalledPackage, filePath);
                }

                StoreItemTable storeItemTable = new StoreItemTable(PhotoEditorApp.getAppContext());
                storeItemTable.deleteItem(item.getIdString());
                if (!isOpen) {
                    DatabaseManager.getInstance().closeDb();
                }
                return packageInfo;
            }

            @Override
            protected void onPostExecute(ItemPackageInfo packageInfo) {
                super.onPostExecute(packageInfo);
                for (OnInstallStoreItemListener listener : TempDataContainer.getInstance()
                        .getOnInstallStoreItemListeners())
                    if (listener != null) {
                        listener.onFinishInstalling(packageInfo, update);
                    }
                packageInfo.setShowingType(StoreItem.STATUS_DOWNLOADED);
            }
        };

        task.execute();
    }

    private static List<FilterInfo> parseInfoJson(final String json) {
        List<FilterInfo> result = new ArrayList<>();
        try {
            JSONArray filterArr = new JSONArray(json);
            for (int idx = 0; idx < filterArr.length(); idx++) {
                JSONObject obj = filterArr.getJSONObject(idx);
                FilterInfo info = new FilterInfo();
                if (obj.has("names")) {
                    JSONArray nameArr = obj.getJSONArray("names");
                    Language[] lang = new Language[nameArr.length()];
                    for (int jdx = 0; jdx < nameArr.length(); jdx++) {
                        JSONObject nameObj = nameArr.getJSONObject(jdx);
                        lang[jdx] = new Language();
                        lang[jdx].setName(nameObj.getString("name"));
                        lang[jdx].setValue(nameObj.getString("value"));
                    }
                    info.setLanguages(lang);
                }

                if (obj.has("thumbnail")) {
                    String thumbnail = obj.getString("thumbnail");
                    info.setThumbnail(thumbnail);
                }

                if (obj.has("cmd")) {
                    String cmd = obj.getString("cmd");
                    info.setCmd(cmd);
                }

                result.add(info);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static List<FilterInfo> installFilterPackage(final long packageId, final String packageFolder,
                                                         final String filePath) {
        try {
            final String outFile = Utils.FILTER_FOLDER;
            FileUtils.unzip(filePath, outFile);
            File zipFile = new File(filePath);
            if (zipFile.delete()) {
                ALog.d("StoreUtils", "installFilterPackage, deleted file = " + filePath);
            } else {
                ALog.d("StoreUtils", "installFilterPackage, deleteAllItemInPackage fail, file=" + filePath);
            }
            // Read package.json file to get item infos
            String packageJson = outFile.concat("/").concat(packageFolder).concat("/package.json");
            BufferedReader br = new BufferedReader(new FileReader(packageJson));
            String json = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                json = json.concat(line).concat("\n");
            }
            br.close();

//            Gson gson = GsonUtils.createAndroidStyleGson();
//            Type collectionType = new TypeToken<List<FilterInfo>>() {
//            }.getType();
            List<FilterInfo> infos = parseInfoJson(json);//gson.fromJson(json, collectionType);

            FilterTable filterTable = new FilterTable(PhotoEditorApp.getAppContext());
            for (FilterInfo info : infos) {
                info.setTitle(info.getLanguages()[0].getValue());
                info.setPackageId(packageId);
                FilterInfo filterInfo = filterTable.get(packageId, info.getTitle());
                if (filterInfo == null) {
                    filterTable.insert(info);
                } else {
                    filterTable.update(info);
                }
            }

            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<ShadeInfo> installFramePackage(final long packageId, final String packageFolder,
                                                       final String filePath, final String type) {
        try {
            String outFile = Utils.FRAME_FOLDER;
            if (ItemPackageTable.BACKGROUND_TYPE.equalsIgnoreCase(type)) {
                outFile = Utils.BACKGROUND_FOLDER;
            } else if (ItemPackageTable.STICKER_TYPE.equalsIgnoreCase(type)) {
                outFile = Utils.STICKER_FOLDER;
            }

            FileUtils.unzip(filePath, outFile);
            File zipFile = new File(filePath);
            if (zipFile.delete()) {
                ALog.d("StoreUtils", "installFramePackage, deleted file = " + filePath);
            } else {
                ALog.d("StoreUtils", "installFramePackage, deleteAllItemInPackage fail, file=" + filePath);
            }
            // Read package.json file to get item infos
            String packageJson = outFile.concat("/").concat(packageFolder).concat("/package.json");
            BufferedReader br = new BufferedReader(new FileReader(packageJson));
            String json = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                json = json.concat(line).concat("\n");
            }
            br.close();

            Gson gson = GsonUtils.createAndroidStyleGson();
            Type collectionType = new TypeToken<List<ShadeInfo>>() {
            }.getType();
            List<ShadeInfo> infos = gson.fromJson(json, collectionType);

            ShadeTable shadeTable = new ShadeTable(PhotoEditorApp.getAppContext());
            for (ShadeInfo info : infos) {
                info.setTitle(info.getLanguages()[0].getValue());
                info.setPackageId(packageId);
                ShadeInfo shadeInfo = shadeTable.get(packageId, info.getTitle(), info.getShadeType());
                if (shadeInfo == null) {
                    shadeTable.insert(info);
                } else {
                    shadeTable.update(info);
                }
            }

            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<CropInfo> installCropPackage(final long packageId, final String packageFolder,
                                                     final String filePath) {
        try {
            final String outFile = Utils.CROP_FOLDER;
            FileUtils.unzip(filePath, outFile);
            // Read package.json file to get item infos
            String packageJson = outFile.concat("/").concat(packageFolder).concat("/package.json");
            BufferedReader br = new BufferedReader(new FileReader(packageJson));
            String json = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                json = json.concat(line).concat("\n");
            }
            br.close();

            Gson gson = GsonUtils.createAndroidStyleGson();
            Type collectionType = new TypeToken<List<CropInfo>>() {
            }.getType();
            List<CropInfo> infos = gson.fromJson(json, collectionType);

            CropTable cropTable = new CropTable(PhotoEditorApp.getAppContext());
            for (CropInfo info : infos) {
                info.setTitle(info.getLanguages()[0].getValue());
                info.setPackageId(packageId);
                CropInfo cropInfo = cropTable.getRow(packageId, info.getTitle());
                if (cropInfo == null) {
                    cropTable.insert(info);
                } else {
                    cropTable.update(info);
                }
            }
            //Delete zip file
            File zipFile = new File(filePath);
            if (zipFile.delete()) {
                ALog.d("StoreUtils", "installCropPackage, deleted file = " + filePath);
            } else {
                ALog.d("StoreUtils", "installCropPackage, deleteAllItemInPackage fail, file=" + filePath);
            }
            //return infos
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
