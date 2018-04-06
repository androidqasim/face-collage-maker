package com.codetho.photocollage.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.config.Constant;
import com.codetho.photocollage.listener.OnChooseColorListener;
import com.codetho.photocollage.listener.OnShareImageListener;
import com.codetho.photocollage.multitouch.controller.ImageEntity;
import com.codetho.photocollage.multitouch.controller.MultiTouchEntity;
import com.codetho.photocollage.multitouch.controller.TextDrawable;
import com.codetho.photocollage.multitouch.controller.TextEntity;
import com.codetho.photocollage.multitouch.custom.OnDoubleClickListener;
import com.codetho.photocollage.multitouch.custom.PhotoView;
import com.codetho.photocollage.quickaction.QuickAction;
import com.codetho.photocollage.quickaction.QuickActionItem;
import com.codetho.photocollage.utils.DialogUtils;
import com.codetho.photocollage.utils.DialogUtils.OnAddImageButtonClickListener;
import com.codetho.photocollage.utils.DialogUtils.OnBorderShadowOptionListener;
import com.codetho.photocollage.utils.DialogUtils.OnEditImageMenuClickListener;
import com.codetho.photocollage.utils.ImageUtils;
import com.codetho.photocollage.utils.ResultContainer;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import dauroi.photoeditor.colorpicker.ColorPickerDialog;
import dauroi.photoeditor.utils.DateTimeUtils;
import dauroi.photoeditor.utils.PhotoUtils;

public class PhotoCollageFragment extends BaseFragment implements
        OnDoubleClickListener, OnEditImageMenuClickListener,
        OnAddImageButtonClickListener, OnBorderShadowOptionListener, ColorPickerDialog.OnColorChangedListener {
    //action id
    private static final int ID_EDIT = 1;
    private static final int ID_CHANGE = 2;
    private static final int ID_DELETE = 3;
    private static final int ID_CANCEL = 4;

    private OnShareImageListener mShareImageListener;
    private ImageView mDeletePhotoView;
    private ImageView mBorderView;
    private ImageView mInfoView;
    private PhotoView mPhotoView;
    private ViewGroup mPhotoLayout;

    private int mItemType = Constant.NORMAL_IMAGE_ITEM;
    // edit image
    private ImageEntity mSelectedEntity = null;
    // use for animation, these views are found from dialogs
    private Animation mAnimation;
    private View mAddImageView;
    private View mGuideView;
    private View mStickerView;
    private View mItemView;
    private View mSelectPhotoView;
    // Dialogs
    private Dialog mItemDialog;
    private Dialog mStickerDialog;
    private Dialog mAddImageDialog;
    private Dialog mBorderShadowOptionDialog;
    private Dialog mGuideDialog;
    private Dialog mSelectPhotoDialog;
    private int mPhotoViewWidth;
    private int mPhotoViewHeight;
    private SharedPreferences mPreferences;
    private OnChooseColorListener mChooseColorListener;
    private int mCurrentColor = Color.WHITE;
    private ColorPickerDialog mColorPickerDialog;

    private QuickAction mTextQuickAction;
    private QuickAction mStickerQuickAction;
    private QuickAction mPhotoQuickAction;

    private void createQuickAction() {
        QuickActionItem editItem = new QuickActionItem(ID_EDIT, mActivity.getString(R.string.edit), getResources().getDrawable(R.drawable.menu_edit));
        QuickActionItem deleteItem = new QuickActionItem(ID_DELETE, mActivity.getString(R.string.delete), mActivity.getResources().getDrawable(R.drawable.menu_delete));
        QuickActionItem cancelItem = new QuickActionItem(ID_CANCEL, mActivity.getString(R.string.cancel), mActivity.getResources().getDrawable(R.drawable.menu_cancel));

        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
        editItem.setSticky(true);
        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
        //orientation
        mTextQuickAction = new QuickAction(mActivity, QuickAction.HORIZONTAL);
        mStickerQuickAction = new QuickAction(mActivity, QuickAction.HORIZONTAL);
        mPhotoQuickAction = new QuickAction(mActivity, QuickAction.HORIZONTAL);
        //add action items into QuickAction
        mTextQuickAction.addActionItem(editItem);
        mTextQuickAction.addActionItem(deleteItem);
        mTextQuickAction.addActionItem(cancelItem);
        mStickerQuickAction.addActionItem(deleteItem);
        mStickerQuickAction.addActionItem(cancelItem);
        mPhotoQuickAction.addActionItem(editItem);
        mPhotoQuickAction.addActionItem(deleteItem);
        mPhotoQuickAction.addActionItem(cancelItem);
        //Set listener for action item clicked
        mPhotoQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                QuickActionItem quickActionItem = mPhotoQuickAction.getActionItem(pos);
                mPhotoQuickAction.dismiss();
                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == ID_DELETE) {
                    onRemoveButtonClick();
                } else if (actionId == ID_EDIT) {
                    onEditButtonClick();
                } else if (actionId == ID_CANCEL) {

                }
            }
        });
        //Set listener for action item clicked
        mTextQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                QuickActionItem quickActionItem = mTextQuickAction.getActionItem(pos);
                mTextQuickAction.dismiss();
                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == ID_DELETE) {
                    mPhotoView.removeImageEntity(mSelectedEntity);
                } else if (actionId == ID_EDIT) {
                    if (mSelectedEntity instanceof TextEntity) {
                        TextDrawable textDrawable = (TextDrawable) ((TextEntity) mSelectedEntity).getDrawable();
                        editTextItem(textDrawable.getText(), textDrawable.getTypefacePath(), textDrawable.getTextColor());
                    }
                } else if (actionId == ID_CANCEL) {

                }
            }
        });
        //Set listener for action item clicked
        mStickerQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                QuickActionItem quickActionItem = mStickerQuickAction.getActionItem(pos);
                mStickerQuickAction.dismiss();
                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == ID_DELETE) {
                    mPhotoView.removeImageEntity(mSelectedEntity);
                } else if (actionId == ID_CANCEL) {

                }
            }
        });
        //set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
        //by clicking the area outside the dialog.
        mTextQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPreferences = activity.getSharedPreferences(Constant.PREF_NAME,
                Context.MODE_PRIVATE);
        try {
            mShareImageListener = (OnShareImageListener) activity;
            if (activity instanceof OnChooseColorListener) {
                mChooseColorListener = (OnChooseColorListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            ResultContainer.getInstance().restoreFromBundle(savedInstanceState);
            mPhotoViewWidth = savedInstanceState
                    .getInt(Constant.PHOTO_VIEW_WIDTH_KEY);
            mPhotoViewHeight = savedInstanceState
                    .getInt(Constant.PHOTO_VIEW_HEIGHT_KEY);
        }
        mAnimation = AnimationUtils.loadAnimation(mActivity,
                R.anim.slide_in_bottom);
        createQuickAction();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ResultContainer.getInstance().saveToBundle(outState);
        outState.putInt(Constant.PHOTO_VIEW_WIDTH_KEY, mPhotoViewWidth);
        outState.putInt(Constant.PHOTO_VIEW_HEIGHT_KEY, mPhotoViewHeight);
        outState.putParcelableArrayList("imageEntities", mPhotoView.getImageEntities());
        outState.putParcelable("backgroundUri", mPhotoView.getPhotoBackgroundUri());
        outState.putParcelable("mSelectedEntity", mSelectedEntity);
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ALog.d("PhotoCollageFragment.onCreateView", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_photocollage,
                container, false);
        mPhotoLayout = (ViewGroup) rootView.findViewById(R.id.photoLayout);
        mDeletePhotoView = (ImageView) rootView.findViewById(R.id.deleteView);
        mDeletePhotoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickDeleteCurrentPhotoView();
            }
        });

        mBorderView = (ImageView) rootView.findViewById(R.id.borderView);
        mBorderView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickBorderView();
            }
        });

        mInfoView = (ImageView) rootView.findViewById(R.id.infoView);
        mInfoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickInfoView();
            }
        });

        mPhotoView = new PhotoView(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mPhotoLayout.addView(mPhotoView, params);
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        mPhotoViewWidth = mPhotoView.getWidth();
                        mPhotoViewHeight = mPhotoView.getHeight();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mPhotoView.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            mPhotoView.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }

                    }
                });

        mPhotoView.setOnDoubleClickListener(this);

        if (savedInstanceState != null) {
            ArrayList<MultiTouchEntity> entities = savedInstanceState.getParcelableArrayList("imageEntities");
            if (entities != null) {
                mPhotoView.setImageEntities(entities);
            }
            Uri backgroundUri = savedInstanceState.getParcelable("backgroundUri");
            if (backgroundUri != null) {
                mPhotoView.setPhotoBackground(backgroundUri);
            }
            ImageEntity entity = savedInstanceState.getParcelable("mSelectedEntity");
            if (entity != null) {
                mSelectedEntity = entity;
            }
        } else {
            mPhotoView.setImageEntities(ResultContainer.getInstance()
                    .copyImageEntities());

            if (ResultContainer.getInstance().getPhotoBackgroundImage() != null) {
                mPhotoView.setPhotoBackground(ResultContainer.getInstance()
                        .getPhotoBackgroundImage());
            }
        }

        mItemDialog = DialogUtils.createEditImageDialog(getActivity(), this,
                DialogUtils.ITEM_DIALOG_TYPE, false);
        mStickerDialog = DialogUtils.createEditImageDialog(getActivity(), this,
                DialogUtils.STICKER_DIALOG_TYPE, false);
        mAddImageDialog = DialogUtils.createAddImageDialog(getActivity(), this,
                false);
        mAddImageDialog.findViewById(R.id.dividerTextView).setVisibility(View.VISIBLE);
        mAddImageDialog.findViewById(R.id.alterBackgroundView).setVisibility(View.VISIBLE);
        mBorderShadowOptionDialog = DialogUtils
                .createBorderAndShadowOptionDialog(getActivity(), this, false);
        mGuideDialog = DialogUtils.createGuideDialog(getActivity(), false);
        mGuideDialog.findViewById(R.id.guideSwapLayout).setVisibility(View.GONE);
        mSelectPhotoDialog = DialogUtils.createSelectPhotoDialog(mActivity,
                this, this, false);
        // find content view of dialogs
        mAddImageView = mAddImageDialog.findViewById(R.id.dialogAddImage);
        mGuideView = mGuideDialog.findViewById(R.id.dialogGesture);
        mItemView = mItemDialog.findViewById(R.id.dialogEditImage);
        mStickerView = mStickerDialog.findViewById(R.id.dialogEditImage);
        mSelectPhotoView = mSelectPhotoDialog
                .findViewById(R.id.dialogSelectPhoto);
        // set title
        mActivity = getActivity();
        mActivity.setTitle(
                R.string.create_from_photo);
        // show guide on first time
        boolean show = mPreferences.getBoolean(Constant.SHOW_GUIDE_CREATE_FREELY_KEY, true);
        if (show) {
            clickInfoView();
            mPreferences.edit().putBoolean(Constant.SHOW_GUIDE_CREATE_FREELY_KEY, false)
                    .commit();
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ALog.d("PhotoCollageFragment.onPause",
                "onPause: width=" + mPhotoView.getWidth() + ", height = "
                        + mPhotoView.getHeight());
        mPhotoView.unloadImages();
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.d("PhotoCollageFragment.onResume",
                "onResume: width=" + mPhotoView.getWidth() + ", height = "
                        + mPhotoView.getHeight());
        mPhotoView.loadImages(getActivity());
        mPhotoView.invalidate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_photocollage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ALog.d("PhotoCollageFragment.onOptionsItemSelected",
                "max memory=" + Runtime.getRuntime().maxMemory() + ", used="
                        + ImageUtils.getUsedMemorySize());
        if (id == R.id.action_add) {
            if (mAddImageView != null) {
                mAddImageView.startAnimation(mAnimation);
            }
            mAddImageDialog.show();
        } else if (id == R.id.action_share) {
            clickShareView();
        } else if (id == R.id.action_help) {
            clickInfoView();
        } else if (id == R.id.action_trash) {
            clickDeleteCurrentPhotoView();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorChanged(int color) {
        mCurrentColor = color;
        drawImageBounds(mCurrentColor);
    }

    public void clickDeleteCurrentPhotoView() {
        if (!already()) {
            return;
        }

        DialogUtils.showConfirmDialog(getActivity(), R.string.confirm,
                R.string.confirm_delete_photo,
                new DialogUtils.ConfirmDialogOnClickListener() {

                    @Override
                    public void onOKButtonOnClick() {
                        ResultContainer.getInstance().clearAll();
                        mPhotoView.clearAllImageEntities();
                        mPhotoView.destroyBackground();
                    }

                    @Override
                    public void onCancelButtonOnClick() {

                    }
                });
    }

    public void clickBorderView() {
        if (!already()) {
            return;
        }

        if (mColorPickerDialog == null) {
            mColorPickerDialog = new ColorPickerDialog(mActivity, mCurrentColor);
            mColorPickerDialog.setOnColorChangedListener(this);
        }

        mColorPickerDialog.setOldColor(mCurrentColor);
        if (!mColorPickerDialog.isShowing()) {
            mColorPickerDialog.show();
        }
    }

    public void clickInfoView() {
        if (mGuideView != null) {
            mGuideView.startAnimation(mAnimation);
        }
        mGuideDialog.show();
    }

    @Override
    protected void resultEditTextItem(String text, int color, String fontPath) {
        if (mSelectedEntity instanceof TextEntity) {
            TextEntity textEntity = (TextEntity) mSelectedEntity;
            textEntity.setTextColor(color);
            textEntity.setTypefacePath(fontPath);
            textEntity.setText(text);
        }
    }

    @Override
    protected void resultAddTextItem(String text, int color, String fontPath) {
        final TextEntity entity = new TextEntity(text, getResources());
        entity.setTextColor(color);
        entity.setTypefacePath(fontPath);
        entity.load(getActivity(),
                (mPhotoView.getWidth() - entity.getWidth()) / 2,
                (mPhotoView.getHeight() - entity.getHeight()) / 2);
        entity.setSticker(false);
        entity.setDrawImageBorder(true);
        mPhotoView.addImageEntity(entity);
        if (ResultContainer.getInstance().getImageEntities() != null) {
            ResultContainer.getInstance().getImageEntities().add(entity);
        }
    }

    @Override
    public void resultFromPhotoEditor(Uri uri) {
        super.resultFromPhotoEditor(uri);

        ALog.d("PhotoCollageFragment.resultFromPhotoEditor", "uri=" + uri.toString());
        if (!already()) {
            return;
        }

        if (mItemType != Constant.BACKGROUND_ITEM) {
            ImageEntity entity = new ImageEntity(uri, getResources());
            entity.setSticker(false);
            entity.load(getActivity(),
                    (mPhotoViewWidth - entity.getWidth()) / 2,
                    (mPhotoViewHeight - entity.getHeight()) / 2);
            mPhotoView.addImageEntity(entity);
            if (ResultContainer.getInstance().getImageEntities() != null) {
                ResultContainer.getInstance().getImageEntities().add(entity);
            }

        } else {
            mPhotoView.setPhotoBackground(uri);
            ResultContainer.getInstance().setPhotoBackgroundImage(uri);
        }
    }

    @Override
    protected void resultSticker(Uri uri) {
        super.resultSticker(uri);
        ImageEntity entity = new ImageEntity(uri, getResources());
        entity.load(getActivity(),
                (mPhotoView.getWidth() - entity.getWidth()) / 2,
                (mPhotoView.getHeight() - entity.getHeight()) / 2);
        entity.setSticker(true);
        mPhotoView.addImageEntity(entity);
        if (ResultContainer.getInstance().getImageEntities() != null) {
            ResultContainer.getInstance().getImageEntities().add(entity);
        }
    }

    @Override
    protected void resultBackground(Uri uri) {
        super.resultBackground(uri);
        mPhotoView.setPhotoBackground(uri);
        ResultContainer.getInstance().setPhotoBackgroundImage(uri);
        mItemType = Constant.NORMAL_IMAGE_ITEM;
    }

    @Override
    protected void resultEditImage(Uri uri) {
        super.resultEditImage(uri);
        mSelectedEntity.setImageUri(getActivity(), uri);
        mPhotoView.invalidate();
    }

    @Override
    protected void resultStickers(Uri[] uri) {
        super.resultStickers(uri);
        if (!already()) {
            return;
        }
        final int size = uri.length;

        for (int idx = 0; idx < size; idx++) {
            float angle = (float) (idx * Math.PI / 20);

            ImageEntity entity = new ImageEntity(uri[idx], getResources());
            entity.load(getActivity(),
                    (mPhotoViewWidth - entity.getWidth()) / 2,
                    (mPhotoViewHeight - entity.getHeight()) / 2, angle);
            mPhotoView.addImageEntity(entity);
            if (ResultContainer.getInstance().getImageEntities() != null) {
                ResultContainer.getInstance().getImageEntities().add(entity);
            }
        }
    }

    @Override
    public void resultPickMultipleImages(Uri[] uri) {
        super.resultPickMultipleImages(uri);
        if (!already()) {
            return;
        }
        final int size = uri.length;

        for (int idx = 0; idx < size; idx++) {
            float angle = (float) (idx * Math.PI / 20);

            ImageEntity entity = new ImageEntity(uri[idx], getResources());
            entity.setInitScaleFactor(0.5f);
            entity.setSticker(false);
            entity.load(getActivity(),
                    (mPhotoViewWidth - entity.getWidth()) / 2,
                    (mPhotoViewHeight - entity.getHeight()) / 2, angle);
            mPhotoView.addImageEntity(entity);
            if (ResultContainer.getInstance().getImageEntities() != null) {
                ResultContainer.getInstance().getImageEntities().add(entity);
            }
        }
    }

    public void clickShareView() {
        if (!already()) {
            return;
        }
        mActivity = getActivity();
        final Bitmap image = mPhotoView.getImage(ImageUtils.calculateOutputScaleFactor(mPhotoView.getWidth(), mPhotoView.getHeight()));
        AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>() {
            Dialog dialog;
            String errMsg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(mActivity, getString(R.string.app_name), getString(R.string.creating));
            }

            @Override
            protected File doInBackground(Void... params) {
                try {
                    String fileName = DateTimeUtils.getCurrentDateTime().replaceAll(":", "-").concat(".png");
                    File collageFolder = new File(ImageUtils.OUTPUT_COLLAGE_FOLDER);
                    if (!collageFolder.exists()) {
                        collageFolder.mkdirs();
                    }
                    File photoFile = new File(collageFolder, fileName);
                    image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(photoFile));
                    PhotoUtils.addImageToGallery(photoFile.getAbsolutePath(), mActivity);
                    return photoFile;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errMsg = ex.getMessage();
                } catch (OutOfMemoryError err) {
                    err.printStackTrace();
                    errMsg = err.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                dialog.dismiss();
                if (file != null) {
                    if (mShareImageListener != null) {
                        mShareImageListener.onShareImage(file.getAbsolutePath());
                    }
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/png");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
                } else if (errMsg != null) {
                    Toast.makeText(mActivity, errMsg, Toast.LENGTH_LONG).show();
                }
                //log
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share/create_freely");
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "create_freely");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onPhotoViewDoubleClick(PhotoView view, MultiTouchEntity entity) {
        if (!already()) {
            return;
        }
        mSelectedEntity = (ImageEntity) entity;
        if (mSelectedEntity.isSticker()) {
            mStickerQuickAction.show(mPhotoView, (int) entity.getCenterX(), (int) entity.getCenterY());
        } else if (mSelectedEntity instanceof TextEntity) {
            mTextQuickAction.show(mPhotoView, (int) entity.getCenterX(), (int) entity.getCenterY());
        } else {
            mPhotoQuickAction.show(mPhotoView, (int) entity.getCenterX(), (int) entity.getCenterY());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ALog.d("PhotoCollageFragment.onDestroyView", "Destroy view");
        mPhotoView.unloadImages();
        mPhotoView.setImageEntities(null);
        mPhotoView.destroyBackground();
    }

    private void drawImageBounds(int color) {
        mPhotoView.setBorderColor(color);
    }

    @Override
    public void onCameraButtonClick() {
        mItemType = Constant.NORMAL_IMAGE_ITEM;
        getImageFromCamera();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onGalleryButtonClick() {
        mItemType = Constant.NORMAL_IMAGE_ITEM;
        // pickImageFromGallery();
        pickMultipleImageFromGallery();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onStickerButtonClick() {
        mItemType = Constant.STICKER_ITEM;
        pickSticker();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onTextButtonClick() {
        mItemType = Constant.TEXT_ITEM;
        addTextItem();
        mAddImageDialog.dismiss();
    }

    @Override
    public void onRemoveButtonClick() {
        if (mSelectedEntity != null) {
            mPhotoView.removeImageEntity(mSelectedEntity);
            // save result
            ResultContainer.getInstance().removeImageEntity(mSelectedEntity);
        }

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
        mItemType = Constant.BACKGROUND_ITEM;
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
        mBorderShadowOptionDialog.show();
        if (mItemDialog.isShowing()) {
            mItemDialog.dismiss();
        }

        if (mStickerDialog.isShowing()) {
            mStickerDialog.dismiss();
        }
    }

    @Override
    public void onEditButtonClick() {
        requestEditingImage(mSelectedEntity.getImageUri());
        mItemDialog.dismiss();
    }

    @Override
    public void onColorBorderButtonClick() {
        mItemDialog.dismiss();
        clickBorderView();
    }

    @Override
    public void onBorderSizeChange(float borderSize) {
        mPhotoView.setBorderSize(borderSize);
    }

    @Override
    public void onShadowSizeChange(float shadowSize) {
        if (shadowSize > 1) {
            mPhotoView.setDrawShadow(true);
            mPhotoView.setShadowSize((int) shadowSize);
        } else {
            mPhotoView.setDrawShadow(false);
            mPhotoView.setShadowSize((int) shadowSize);
        }
    }

    @Override
    public void onCancelEdit() {

    }

    @Override
    public void onBackgroundDoubleClick() {
        if (mSelectPhotoView != null) {
            mSelectPhotoView.startAnimation(mAnimation);
        }
        mSelectPhotoDialog.show();
    }

    @Override
    public void onBackgroundColorButtonClick() {

    }

    @Override
    public void onBackgroundPhotoButtonClick() {
        onAlterBackgroundButtonClick();
    }
}
