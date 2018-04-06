package com.codetho.photocollage.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.ui.AddTextItemActivity;
import com.codetho.photocollage.ui.DownloadedPackageActivity;
import com.codetho.photocollage.ui.SelectPhotoActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;

public class BaseFragment extends Fragment {
    protected static final int REQUEST_ADD_TEXT_ITEM = 1000;
    protected static final int REQUEST_PHOTO_EDITOR_CODE = 999;
    protected static final int PICK_IMAGE_REQUEST_CODE = 998;
    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 997;
    protected static final int PICK_STICKER_REQUEST_CODE = 996;
    protected static final int PICK_BACKGROUND_REQUEST_CODE = 995;
    protected static final int REQUEST_EDIT_IMAGE = 994;
    protected static final int PICK_MULTIPLE_IMAGE_REQUEST_CODE = 993;
    protected static final int REQUEST_EDIT_TEXT_ITEM = 992;

    public static final int MAX_NEEDED_PHOTOS = 20;
    protected static final String CAPTURE_TITLE = "capture.jpg";
    protected static final int BACKGROUND_ITEM = 0;
    protected static final int STICKER_ITEM = 1;
    protected static final int NORMAL_IMAGE_ITEM = 2;
    protected Activity mActivity;
    private String mTitle = null;
    //Analytics
    protected FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        setTitle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mActivity);
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle();
    }

    protected void setTitle() {
        if (this instanceof PhotoCollageFragment) {
            mTitle = getString(R.string.collage);
        } else if (this instanceof SelectFrameFragment) {
            mTitle = getString(R.string.select_frame);
        } else if (this instanceof CreateFrameFragment) {
            mTitle = getString(R.string.create_frame);
        } else if (this instanceof MainPhotoFragment) {
            mTitle = getString(R.string.home);
        } else if (this instanceof ColorChooserFragment) {
            mTitle = getString(R.string.select_color);
        }
        setTitle(mTitle);
    }

    public void setTitle(String title) {
        mTitle = title;
        if (already()) {
            mActivity.setTitle(title);
            if (mActivity instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) mActivity;
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                }
            }
        }
    }

    public void setTitle(int res) {
        mTitle = getString(res);
        setTitle(mTitle);
    }

    public String getTitle() {
        return mTitle;
    }

    private void startPhotoEditor(Uri imageUri, boolean capturedFromCamera) {
        if (!already()) {
            return;
        }
        ALog.d("BaseFragment", "startPhotoEditor, imageUri=" + imageUri + ", capturedFromCamera=" + capturedFromCamera);
        Intent newIntent = new Intent(getActivity(), ImageProcessingActivity.class);
        newIntent.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, imageUri);
        if (capturedFromCamera) {
            newIntent.putExtra(ImageProcessingActivity.ROTATION_KEY, 90);
        }
        startActivityForResult(newIntent, REQUEST_PHOTO_EDITOR_CODE);
    }

    public void pickImageFromGallery() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pickMultipleImageFromGallery() {
        Intent data = new Intent(getActivity(), SelectPhotoActivity.class);
        data.putExtra(SelectPhotoActivity.EXTRA_IMAGE_COUNT, MAX_NEEDED_PHOTOS);
        data.putExtra(SelectPhotoActivity.EXTRA_IS_MAX_IMAGE_COUNT, true);
        startActivityForResult(data, PICK_MULTIPLE_IMAGE_REQUEST_CODE);
    }

    public void getImageFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void pickSticker() {
        if (!already()) {
            return;
        }

        Intent intent = new Intent(getActivity(), DownloadedPackageActivity.class);
        intent.putExtra(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE, ItemPackageTable.STICKER_TYPE);
        startActivityForResult(intent, PICK_STICKER_REQUEST_CODE);
    }

    public void addTextItem() {
        if (!already()) {
            return;
        }

        Intent intent = new Intent(getActivity(), AddTextItemActivity.class);
        startActivityForResult(intent, REQUEST_ADD_TEXT_ITEM);
    }

    public void pickBackground() {
        if (!already()) {
            return;
        }

        Intent intent = new Intent(getActivity(), DownloadedPackageActivity.class);
        intent.putExtra(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE, ItemPackageTable.BACKGROUND_TYPE);
        startActivityForResult(intent, PICK_BACKGROUND_REQUEST_CODE);
    }

    public void requestEditingImage(Uri imageUri) {
        if (!already()) {
            return;
        }

        Intent newIntent = new Intent(getActivity(), ImageProcessingActivity.class);
        newIntent.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, imageUri);
        startActivityForResult(newIntent, REQUEST_EDIT_IMAGE);
    }

    public void editTextItem(String text, String font, int textColor) {
        Intent intent = new Intent(mActivity, AddTextItemActivity.class);
        intent.putExtra(AddTextItemActivity.EXTRA_TEXT_CONTENT, text);
        intent.putExtra(AddTextItemActivity.EXTRA_TEXT_FONT, font);
        intent.putExtra(AddTextItemActivity.EXTRA_TEXT_COLOR, textColor);
        startActivityForResult(intent, REQUEST_EDIT_TEXT_ITEM);
    }

    protected Uri getImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory()
                + "/DCIM", CAPTURE_TITLE);
        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PHOTO_EDITOR_CODE:
                    // output image path
                    Uri uri = data.getData();
                    resultFromPhotoEditor(uri);
                    break;
                case PICK_IMAGE_REQUEST_CODE:
                    if (data != null && data.getData() != null) {
                        uri = data.getData();
                        startPhotoEditor(uri, false);
                    }
                    break;
                case CAPTURE_IMAGE_REQUEST_CODE:
                    uri = getImageUri();
                    if (uri != null) {
                        startPhotoEditor(uri, true);
                    }
                    break;
                case PICK_BACKGROUND_REQUEST_CODE:
                    ArrayList<String> allPaths = data.getStringArrayListExtra(SelectPhotoActivity.EXTRA_SELECTED_IMAGES);
                    if (allPaths != null && allPaths.size() > 0) {
                        uri = Uri.fromFile(new File(allPaths.get(0)));
                        resultBackground(uri);
                    }
                    break;
                case REQUEST_EDIT_IMAGE:
                    uri = data.getData();
                    resultEditImage(uri);
                    break;
                case PICK_STICKER_REQUEST_CODE:
                    allPaths = data.getStringArrayListExtra(SelectPhotoActivity.EXTRA_SELECTED_IMAGES);
                    //.getStringArrayExtra(DBConstant.MULTIPLE_IMAGE_PATHS_KEY);
                    if (allPaths != null && allPaths.size() > 0) {
                        final int len = allPaths.size();
                        Uri[] result = new Uri[len];
                        for (int idx = 0; idx < len; idx++) {
                            uri = Uri.fromFile(new File(allPaths.get(idx)));
                            result[idx] = uri;
                        }

                        resultStickers(result);
                    }
                    break;
                case PICK_MULTIPLE_IMAGE_REQUEST_CODE:
                    allPaths = data.getStringArrayListExtra(SelectPhotoActivity.EXTRA_SELECTED_IMAGES);
                    //.getStringArrayExtra(DBConstant.MULTIPLE_IMAGE_PATHS_KEY);
                    if (allPaths != null && allPaths.size() > 0) {
                        final int len = allPaths.size();
                        Uri[] result = new Uri[len];
                        for (int idx = 0; idx < len; idx++) {
                            uri = Uri.fromFile(new File(allPaths.get(idx)));
                            result[idx] = uri;
                        }

                        resultPickMultipleImages(result);
                    }
                    break;
                case REQUEST_EDIT_TEXT_ITEM:
                    String text = data.getStringExtra(AddTextItemActivity.EXTRA_TEXT_CONTENT);
                    String fontPath = data.getStringExtra(AddTextItemActivity.EXTRA_TEXT_FONT);
                    int color = data.getIntExtra(AddTextItemActivity.EXTRA_TEXT_COLOR, Color.BLACK);
                    resultEditTextItem(text, color, fontPath);
                    break;
                case REQUEST_ADD_TEXT_ITEM:
                    text = data.getStringExtra(AddTextItemActivity.EXTRA_TEXT_CONTENT);
                    fontPath = data.getStringExtra(AddTextItemActivity.EXTRA_TEXT_FONT);
                    color = data.getIntExtra(AddTextItemActivity.EXTRA_TEXT_COLOR, Color.BLACK);
                    resultAddTextItem(text, color, fontPath);
                    break;
            }

        }
    }

    protected void resultEditTextItem(String text, int color, String fontPath) {

    }

    protected void resultFromPhotoEditor(Uri image) {

    }

    protected void resultSticker(Uri uri) {

    }

    protected void resultStickers(Uri[] uri) {

    }

    protected void resultBackground(Uri uri) {

    }

    protected void resultEditImage(Uri uri) {

    }

    protected void resultAddTextItem(String text, int color, String fontPath) {

    }

    public void resultPickMultipleImages(Uri[] uri) {

    }

    public boolean already() {
        return (isAdded() && getActivity() != null);
    }

}
