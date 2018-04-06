package com.codetho.photocollage.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.Constant;
import com.codetho.photocollage.model.TemplateItem;
import com.codetho.photocollage.template.ItemImageView;
import com.codetho.photocollage.template.PhotoItem;
import com.codetho.photocollage.template.PhotoLayout;
import com.codetho.photocollage.ui.custom.TransitionImageView;

import java.io.File;

import dauroi.photoeditor.utils.FileUtils;
import dauroi.photoeditor.utils.PhotoUtils;

/**
 * Created by vanhu_000 on 3/11/2016.
 */
public class TemplateDetailActivity extends BaseTemplateDetailActivity implements PhotoLayout.OnQuickActionClickListener {
    private PhotoLayout mPhotoLayout;
    private ItemImageView mSelectedItemImageView;
    private TransitionImageView mBackgroundImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (PhotoItem item : mSelectedTemplateItem.getPhotoItemList())
            if (item.imagePath != null && item.imagePath.length() > 0) {
                mSelectedPhotoPaths.add(item.imagePath);
            }
        // show guide on first time
        boolean show = mPreferences.getBoolean(Constant.SHOW_GUIDE_CREATE_TEMPLATE_KEY, true);
        if (show) {
            clickInfoView();
            mPreferences.edit().putBoolean(Constant.SHOW_GUIDE_CREATE_TEMPLATE_KEY, false)
                    .commit();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_template_detail;
    }

    @Override
    public Bitmap createOutputImage() {
        Bitmap template = mPhotoLayout.createImage();
        Bitmap result = Bitmap.createBitmap(template.getWidth(), template.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(template, 0, 0, paint);
        template.recycle();
        template = null;
        Bitmap stickers = mPhotoView.getImage(mOutputScale);
        canvas.drawBitmap(stickers, 0, 0, paint);
        stickers.recycle();
        stickers = null;
        System.gc();
        return result;
    }

    @Override
    public void onEditActionClick(ItemImageView v) {
        mSelectedItemImageView = v;
        if (v.getImage() != null && v.getPhotoItem().imagePath != null && v.getPhotoItem().imagePath.length() > 0) {
            Uri uri = Uri.fromFile(new File(v.getPhotoItem().imagePath));
            requestEditingImage(uri);
        }
    }

    @Override
    public void onChangeActionClick(ItemImageView v) {
        mSelectedItemImageView = v;
        final Dialog dialog = getBackgroundImageDialog();
        if (dialog != null)
            dialog.findViewById(R.id.alterBackgroundView).setVisibility(View.GONE);
        requestPhoto();
    }

    @Override
    public void onChangeBackgroundActionClick(TransitionImageView v) {
        mBackgroundImageView = v;
        final Dialog dialog = getBackgroundImageDialog();
        if (dialog != null)
            dialog.findViewById(R.id.alterBackgroundView).setVisibility(View.VISIBLE);
        requestPhoto();
    }

    @Override
    protected void resultEditImage(Uri uri) {
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setImagePath(FileUtils.getPath(this, uri));
            mBackgroundImageView = null;
        } else if (mSelectedItemImageView != null) {
            mSelectedItemImageView.setImagePath(FileUtils.getPath(this, uri));
        }
    }

    @Override
    protected void resultFromPhotoEditor(Uri image) {
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setImagePath(FileUtils.getPath(this, image));
            mBackgroundImageView = null;
        } else if (mSelectedItemImageView != null) {
            mSelectedItemImageView.setImagePath(FileUtils.getPath(this, image));
        }
    }

    @Override
    protected void resultBackground(Uri uri) {
        if (mBackgroundImageView != null) {
            mBackgroundImageView.setImagePath(FileUtils.getPath(this, uri));
            mBackgroundImageView = null;
        } else if (mSelectedItemImageView != null) {
            mSelectedItemImageView.setImagePath(FileUtils.getPath(this, uri));
        }
    }

    @Override
    protected void buildLayout(TemplateItem templateItem) {
        Bitmap backgroundImage = null;
        if (mPhotoLayout != null) {
            backgroundImage = mPhotoLayout.getBackgroundImage();
            mPhotoLayout.recycleImages(false);
        }

        final Bitmap frameImage = PhotoUtils.decodePNGImage(this, templateItem.getTemplate());
        int[] size = calculateThumbnailSize(frameImage.getWidth(), frameImage.getHeight());
        //Photo Item item_quick_action must be descended by index before creating photo layout.
        mPhotoLayout = new PhotoLayout(this, templateItem.getPhotoItemList(), frameImage);
        mPhotoLayout.setBackgroundImage(backgroundImage);
        mPhotoLayout.setQuickActionClickListener(this);
        mPhotoLayout.build(size[0], size[1], mOutputScale);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size[0], size[1]);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mContainerLayout.removeAllViews();
        mContainerLayout.addView(mPhotoLayout, params);
        //add sticker view
        mContainerLayout.removeView(mPhotoView);
        mContainerLayout.addView(mPhotoView, params);
    }

    @Override
    public void finish() {
        if (mPhotoLayout != null) {
            mPhotoLayout.recycleImages(true);
        }
        super.finish();
    }
}
