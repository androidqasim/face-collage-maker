package com.codetho.photocollage.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.multitouch.controller.ImageEntity;
import com.codetho.photocollage.multitouch.controller.MultiTouchEntity;
import com.codetho.photocollage.multitouch.custom.OnDoubleClickListener;
import com.codetho.photocollage.multitouch.custom.PhotoView;
import com.codetho.photocollage.listener.OnChooseColorListener;
import com.codetho.photocollage.utils.DialogUtils;
import com.codetho.photocollage.utils.DialogUtils.OnBorderShadowOptionListener;
import com.codetho.photocollage.utils.DialogUtils.OnEditImageMenuClickListener;

public class EditImageFragment extends BaseFragment implements
        OnDoubleClickListener, OnEditImageMenuClickListener,
        OnBorderShadowOptionListener {
    protected ImageEntity mSelectedEntity = null;
    protected PhotoView mSelectedPhotoView;
    protected Dialog mBorderShadowOptionDialog;
    protected Dialog mStickerDialog;
    protected Dialog mItemDialog;
    protected OnChooseColorListener mChooseColorListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnChooseColorListener) {
            mChooseColorListener = (OnChooseColorListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemDialog = DialogUtils.createEditImageDialog(getActivity(), this,
                DialogUtils.ITEM_DIALOG_TYPE, false);
        mStickerDialog = DialogUtils.createEditImageDialog(getActivity(), this,
                DialogUtils.STICKER_DIALOG_TYPE, false);
        mBorderShadowOptionDialog = DialogUtils
                .createBorderAndShadowOptionDialog(getActivity(), this, false);
    }

    private void clickBorderView() {
        if (!already()) {
            return;
        }

        Fragment fragment = new ColorChooserFragment();
        FragmentTransaction transaction = mActivity
                .getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // int initialColor = Color.WHITE;
        //
        // ColorPickerDialog colorPickerDialog = new ColorPickerDialog(
        // getActivity(), initialColor, new OnColorSelectedListener() {
        //
        // @Override
        // public void onColorSelected(int color) {
        // selectColorBorder(color);
        // }
        //
        // });
        // colorPickerDialog.show();
    }

    /**
     * @param unused
     * @deprecated
     */
    public void selectColorBorder(int unused) {

    }

    @Override
    protected void resultEditImage(Uri uri) {
        super.resultEditImage(uri);
        if (mSelectedEntity != null) {
            mSelectedEntity.setImageUri(getActivity(), uri);
            if (mSelectedPhotoView != null) {
                mSelectedPhotoView.invalidate();
            }
            mSelectedEntity = null;
        }
    }

    @Override
    public void onRemoveButtonClick() {
        if (mItemDialog.isShowing()) {
            mItemDialog.dismiss();
        }

        if (mStickerDialog.isShowing()) {
            mStickerDialog.dismiss();
        }
    }

    @Override
    public void onAlterBackgroundButtonClick() {
        if (!already()) {
            return;
        }
        mSelectedEntity = null;
        pickBackground();
        if (mItemDialog.isShowing()) {
            mItemDialog.dismiss();
        }

        if (mStickerDialog.isShowing()) {
            mStickerDialog.dismiss();
        }
    }

    @Override
    public void onBorderAndShaderButtonClick() {
        mSelectedEntity = null;
        if (mItemDialog.isShowing()) {
            mItemDialog.dismiss();
        }

        if (mStickerDialog.isShowing()) {
            mStickerDialog.dismiss();
        }

        mBorderShadowOptionDialog.show();
    }

    @Override
    public void onEditButtonClick() {
        mItemDialog.dismiss();
    }

    @Override
    public void onColorBorderButtonClick() {
        mSelectedEntity = null;
        mItemDialog.dismiss();
        clickBorderView();
    }

    @Override
    public void onPhotoViewDoubleClick(PhotoView view, MultiTouchEntity entity) {
        if (!already()) {
            return;
        }
        mSelectedPhotoView = view;
        mSelectedEntity = (ImageEntity) entity;
        mStickerDialog.show();
    }

    @Override
    public void onBorderSizeChange(float borderSize) {

    }

    @Override
    public void onShadowSizeChange(float shadowSize) {

    }

    @Override
    public void onCancelEdit() {
        mSelectedEntity = null;
    }

    @Override
    public void onBackgroundDoubleClick() {
        Toast.makeText(mActivity, "Double click background", Toast.LENGTH_SHORT)
                .show();
    }
}
