package com.codetho.photocollage.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.ui.fragment.DownloadedPackageFragment;
import com.codetho.photocollage.utils.DialogUtils;

import java.io.File;
import java.util.ArrayList;

import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;

/**
 * Created by vanhu_000 on 3/17/2016.
 */
public abstract class BasePhotoActivity extends AdsFragmentActivity implements
        DialogUtils.OnAddImageButtonClickListener {
    protected static final int REQUEST_ADD_TEXT_ITEM = 10000;
    protected static final int REQUEST_PHOTO_EDITOR_CODE = 9990;
    protected static final int PICK_IMAGE_REQUEST_CODE = 9980;
    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 9970;
    protected static final int PICK_STICKER_REQUEST_CODE = 9960;
    protected static final int PICK_BACKGROUND_REQUEST_CODE = 9950;
    protected static final int REQUEST_EDIT_IMAGE = 9940;
    protected static final int PICK_MULTIPLE_IMAGE_REQUEST_CODE = 9930;
    protected static final int REQUEST_EDIT_TEXT_ITEM = 9920;

    private Uri mCapturedImageUri;
    private Dialog mAddImageDialog;
    private View mAddImageView;
    private Animation mAnimation;

    public Dialog getBackgroundImageDialog() {
        return mAddImageDialog;
    }

    @Override
    public void onCameraButtonClick() {
        getImageFromCamera();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onGalleryButtonClick() {
        pickImageFromGallery();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onStickerButtonClick() {
        pickSticker();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onTextButtonClick() {

    }

    @Override
    public void onBackgroundPhotoButtonClick() {

    }

    @Override
    public void onBackgroundColorButtonClick() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCapturedImageUri = savedInstanceState.getParcelable("mCapturedImageUri");
        }
        mAddImageDialog = DialogUtils.createAddImageDialog(this, this,
                false);
        mAddImageView = mAddImageDialog.findViewById(R.id.dialogAddImage);
        mAnimation = AnimationUtils.loadAnimation(this,
                R.anim.slide_in_bottom);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mCapturedImageUri", mCapturedImageUri);
    }

    protected void showAddingImageOptions(boolean showSticker, boolean showText) {
        if (mAddImageDialog != null) {
            final View stickerView = mAddImageDialog.findViewById(R.id.stickerView);
            if (showSticker) {
                stickerView.setVisibility(View.VISIBLE);
            } else {
                stickerView.setVisibility(View.GONE);
            }

            final View textView = mAddImageDialog.findViewById(R.id.textView);
            if (showText) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
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
                        //startPhotoEditor(uri);
                        resultFromPhotoEditor(uri);
                    }
                    break;
                case CAPTURE_IMAGE_REQUEST_CODE:
                    if (mCapturedImageUri != null) {
                        startPhotoEditor(mCapturedImageUri, true);
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

    protected void requestPhoto() {
        if (mAddImageView != null) {
            mAddImageView.startAnimation(mAnimation);
        }
        mAddImageDialog.show();
    }

    private void startPhotoEditor(Uri imageUri, boolean capturedFromCamera) {
        Intent newIntent = new Intent(this, ImageProcessingActivity.class);
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getImageFromCamera() {
        // Store image in dcim
        String capturedPath = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/DCIM", capturedPath);
        file.getParentFile().mkdirs();
        mCapturedImageUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void pickSticker() {
        Intent intent = new Intent(this, DownloadedPackageActivity.class);
        intent.putExtra(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE, ItemPackageTable.STICKER_TYPE);
        startActivityForResult(intent, PICK_STICKER_REQUEST_CODE);
    }

    public void addTextItem() {
        Intent intent = new Intent(this, AddTextItemActivity.class);
        startActivityForResult(intent, REQUEST_ADD_TEXT_ITEM);
    }

    public void editTextItem(String text, String font, int textColor) {
        Intent intent = new Intent(this, AddTextItemActivity.class);
        intent.putExtra(AddTextItemActivity.EXTRA_TEXT_CONTENT, text);
        intent.putExtra(AddTextItemActivity.EXTRA_TEXT_FONT, font);
        intent.putExtra(AddTextItemActivity.EXTRA_TEXT_COLOR, textColor);
        startActivityForResult(intent, REQUEST_EDIT_TEXT_ITEM);
    }

    public void pickBackground() {
        Intent intent = new Intent(this, DownloadedPackageActivity.class);
        intent.putExtra(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE, ItemPackageTable.BACKGROUND_TYPE);
        startActivityForResult(intent, PICK_BACKGROUND_REQUEST_CODE);
    }

    public void requestEditingImage(Uri imageUri) {
        Intent newIntent = new Intent(this, ImageProcessingActivity.class);
        newIntent.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, imageUri);
        startActivityForResult(newIntent, REQUEST_EDIT_IMAGE);
    }

    protected void resultFromPhotoEditor(Uri image) {

    }

    protected void resultSticker(Uri uri) {

    }

    protected void resultBackground(Uri uri) {

    }

    protected void resultEditImage(Uri uri) {

    }

    protected void resultAddTextItem(String text, int color, String fontPath) {

    }

    protected void resultEditTextItem(String text, int color, String fontPath) {

    }

    public void resultPickMultipleImages(Uri[] uri) {

    }

    public void resultStickers(Uri[] uri) {

    }
}
