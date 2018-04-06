package com.codetho.photocollage.ui.fragment;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codetho.photocollage.R;
import com.codetho.photocollage.adapter.GalleryAlbumImageAdapter;
import com.codetho.photocollage.model.ImageItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.database.table.ShadeTable;
import dauroi.photoeditor.model.ShadeInfo;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;

/**
 * Created by admin on 4/6/2016.
 */
public class ItemPackageDetailFragment extends BaseFragment {
    private static final String EXTRACTED_IMAGE_FOLDER_PATH = Utils.BIG_D_FOLDER.concat("/assets");
    public static final String ASSET_BACKGROUND_FOLDER = "background";
    public static final String ASSET_STICKER_FOLDER = "sticker";

    private GalleryAlbumImageAdapter mAdapter;
    private String mAssetImageFolder = ASSET_BACKGROUND_FOLDER;
    private List<ImageItem> mImageList = new ArrayList<ImageItem>();
    private GridView mGridView;
    private View mProgressView;

    private String mPackageName;
    private long mPackageId;
    private String mPackageType;
    private String mPackageFolder;
    private GalleryAlbumImageFragment.OnSelectImageListener mListener;

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            final ImageItem img = mImageList.get(position);
            String path = img.imagePath;
            File file;
            if (img.imagePath.startsWith(PhotoUtils.ASSET_PREFIX)) {
                path = path.substring(PhotoUtils.ASSET_PREFIX.length());
                file = Utils.copyFileFromAsset(getActivity(), EXTRACTED_IMAGE_FOLDER_PATH, path, false);
            } else {
                file = new File(path);
            }

            if (file != null) {
                if (mListener != null) {
                    mListener.onSelectImage(file.getAbsolutePath());
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof GalleryAlbumImageFragment.OnSelectImageListener) {
            mListener = (GalleryAlbumImageFragment.OnSelectImageListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_package_detail, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mProgressView = view.findViewById(R.id.progressBar);

        mPackageId = getArguments().getLong(DownloadedPackageFragment.EXTRA_PACKAGE_ID, 0);
        mPackageName = getArguments().getString(DownloadedPackageFragment.EXTRA_PACKAGE_NAME);
        mPackageType = getArguments().getString(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE);
        mPackageFolder = getArguments().getString(DownloadedPackageFragment.EXTRA_PACKAGE_FOLDER);

        if (mPackageName != null && mPackageName.length() > 0) {
            if (DownloadedPackageFragment.DEFAULT_BACKGROUND_PACKAGE_INT_ID == mPackageId) {
                mAssetImageFolder = ASSET_BACKGROUND_FOLDER;
            } else {
                mAssetImageFolder = ASSET_STICKER_FOLDER;
            }
            setTitle(mPackageName);
        }
        loadImages();
        return view;
    }

    private void loadImages() {
        AsyncTask<Void, Void, List<ImageItem>> task = new AsyncTask<Void, Void, List<ImageItem>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressView.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<ImageItem> doInBackground(Void... params) {
                if (DownloadedPackageFragment.DEFAULT_BACKGROUND_PACKAGE_INT_ID == mPackageId
                        || DownloadedPackageFragment.DEFAULT_STICKER_PACKAGE_INT_ID == mPackageId) {
                    return loadAssetPhotos(mAssetImageFolder);
                } else {
                    List<ImageItem> imageItems = new ArrayList<>();
                    ShadeTable table = new ShadeTable(getActivity());
                    List<ShadeInfo> frameInfos = table.getRows(mPackageId, mPackageType);
                    if (mPackageFolder != null && mPackageFolder.length() > 0) {
                        String baseFolder = Utils.FRAME_FOLDER.concat("/").concat(mPackageFolder).concat("/");
                        if (ItemPackageTable.BACKGROUND_TYPE.equals(mPackageType)) {
                            baseFolder = Utils.BACKGROUND_FOLDER.concat("/").concat(mPackageFolder).concat("/");
                        } else if (ItemPackageTable.STICKER_TYPE.equals(mPackageType)) {
                            baseFolder = Utils.STICKER_FOLDER.concat("/").concat(mPackageFolder).concat("/");
                        } else if (ItemPackageTable.CROP_TYPE.equals(mPackageType)) {
                            baseFolder = Utils.CROP_FOLDER.concat("/").concat(mPackageFolder).concat("/");
                        } else if (ItemPackageTable.FRAME_TYPE.equals(mPackageType)) {
                            baseFolder = Utils.FRAME_FOLDER.concat("/").concat(mPackageFolder).concat("/");
                        }

                        for (ShadeInfo info : frameInfos) {
                            info.setForeground(baseFolder.concat(info.getForeground()));
                            info.setThumbnail(baseFolder.concat(info.getThumbnail()));
                            ImageItem item = new ImageItem();
                            item.imagePath = info.getForeground();
                            item.thumbnailPath = info.getThumbnail();
                            item.isSelected = false;
                            if (ItemPackageTable.STICKER_TYPE.equals(mPackageType)) {
                                item.isSticker = true;
                            } else {
                                item.isSticker = false;
                            }

                            imageItems.add(item);
                        }
                    }

                    return imageItems;
                }
            }

            @Override
            protected void onPostExecute(List<ImageItem> imageItems) {
                super.onPostExecute(imageItems);
                if (!already()) {
                    return;
                }

                mProgressView.setVisibility(View.GONE);
                mImageList.clear();
                mImageList.addAll(imageItems);
                final List<String> imagePaths = new ArrayList<>();
                for (ImageItem item : imageItems)
                    imagePaths.add(item.imagePath);
//                    imagePaths.add(item.thumbnailPath);
                mAdapter = new GalleryAlbumImageAdapter(getActivity(), imagePaths);
                if (ItemPackageTable.STICKER_TYPE.equals(mPackageType)) {
                    mAdapter.setImageFitCenter(true);
                } else {
                    mAdapter.setImageFitCenter(false);
                }
                mGridView.setOnItemClickListener(mItemClickListener);
                mGridView.setAdapter(mAdapter);
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private List<ImageItem> loadAssetPhotos(String assetImageFolder) {
        List<ImageItem> result = new ArrayList<>();
        if (assetImageFolder != null && assetImageFolder.length() > 0) {
            AssetManager am = mActivity.getAssets();
            try {
                String[] images = am.list(assetImageFolder);
                if (images != null) {
                    for (String str : images) {
                        ImageItem item = new ImageItem();
                        item.imagePath = PhotoUtils.ASSET_PREFIX.concat(assetImageFolder).concat("/").concat(str);
                        item.thumbnailPath = item.imagePath;
                        item.isSelected = false;
                        if (assetImageFolder.equals(ASSET_STICKER_FOLDER)) {
                            item.isSticker = true;
                        }
                        result.add(item);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
