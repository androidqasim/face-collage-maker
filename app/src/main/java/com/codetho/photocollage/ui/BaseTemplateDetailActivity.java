package com.codetho.photocollage.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.adapter.HorizontalPreviewTemplateAdapter;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.config.Constant;
import com.codetho.photocollage.model.TemplateItem;
import com.codetho.photocollage.multitouch.controller.ImageEntity;
import com.codetho.photocollage.multitouch.controller.MultiTouchEntity;
import com.codetho.photocollage.multitouch.controller.TextDrawable;
import com.codetho.photocollage.multitouch.controller.TextEntity;
import com.codetho.photocollage.multitouch.custom.OnDoubleClickListener;
import com.codetho.photocollage.multitouch.custom.PhotoView;
import com.codetho.photocollage.quickaction.QuickAction;
import com.codetho.photocollage.quickaction.QuickActionItem;
import com.codetho.photocollage.template.PhotoItem;
import com.codetho.photocollage.ui.fragment.DownloadedPackageFragment;
import com.codetho.photocollage.utils.DialogUtils;
import com.codetho.photocollage.utils.ImageUtils;
import com.codetho.photocollage.utils.ResultContainer;
import com.codetho.photocollage.utils.TemplateImageUtils;
import com.codetho.photocollage.utils.frame.FrameImageUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.utils.DateTimeUtils;
import dauroi.photoeditor.utils.PhotoUtils;

/**
 * Created by admin on 7/4/2016.
 */
public abstract class BaseTemplateDetailActivity extends BasePhotoActivity implements HorizontalPreviewTemplateAdapter.OnPreviewTemplateClickListener, OnDoubleClickListener {
    private static final String TAG = BaseTemplateDetailActivity.class.getSimpleName();
    private static final String PREF_NAME = "templateDetailPref";
    private static final String RATIO_KEY = "ratio";
    protected static final int RATIO_SQUARE = 0;
    protected static final int RATIO_FIT = 1;
    protected static final int RATIO_GOLDEN = 2;
    //action id
    private static final int ID_EDIT = 1;
    private static final int ID_DELETE = 2;
    private static final int ID_CANCEL = 3;

    private Dialog mGuideDialog;
    protected RelativeLayout mContainerLayout;
    protected RecyclerView mTemplateView;
    protected PhotoView mPhotoView;
    private View mGuideView;
    protected float mOutputScale = 1;
    protected Dialog mAddImageDialog;
    protected View mAddImageView;
    protected Animation mAnimation;
    protected int mItemType = Constant.NORMAL_IMAGE_ITEM;
    protected TemplateItem mSelectedTemplateItem;
    protected ArrayList<TemplateItem> mTemplateItemList = new ArrayList<>();
    private int mImageInTemplateCount = 0;

    protected HorizontalPreviewTemplateAdapter mTemplateAdapter;
    protected List<String> mSelectedPhotoPaths = new ArrayList<>();
    private Dialog mRatioDialog;
    private SharedPreferences mPref;
    protected int mLayoutRatio = RATIO_SQUARE;
    private ImageEntity mSelectedEntity = null;
    private QuickAction mTextQuickAction;
    private QuickAction mStickerQuickAction;
    protected SharedPreferences mPreferences;
    private boolean mIsFrameImage = true;
    private boolean mClickedShareButton = false;

    //abstract methods
    protected abstract int getLayoutId();

    protected abstract void buildLayout(TemplateItem templateItem);

    protected abstract Bitmap createOutputImage();

    protected boolean isShowingAllTemplates() {
        return true;
    }

    @Override
    protected void preCreateAdsHelper() {
        mLoadedData = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.d(TAG, "onCreate, savedInstanceState=" + savedInstanceState);
        setContentView(getLayoutId());
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.collage);
        }
        mPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mLayoutRatio = mPref.getInt(RATIO_KEY, RATIO_SQUARE);
        mImageInTemplateCount = getIntent().getIntExtra(TemplateActivity.EXTRA_IMAGE_IN_TEMPLATE_COUNT, 0);
        mIsFrameImage = getIntent().getBooleanExtra(TemplateActivity.EXTRA_IS_FRAME_IMAGE, true);
        final int selectedItemIndex = getIntent().getIntExtra(TemplateActivity.EXTRA_SELECTED_TEMPLATE_INDEX, 0);
        final ArrayList<String> extraImagePaths = getIntent().getStringArrayListExtra(TemplateActivity.EXTRA_IMAGE_PATHS);
        //pref
        mPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        mContainerLayout = (RelativeLayout) findViewById(R.id.containerLayout);
        mTemplateView = (RecyclerView) findViewById(R.id.templateView);
        mPhotoView = new PhotoView(this);
        mPhotoView.setOnDoubleClickListener(this);
        createQuickAction();
        //Option dialog
        mGuideDialog = DialogUtils.createGuideDialog(this, false);
        mGuideView = mGuideDialog.findViewById(R.id.dialogGesture);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        mAddImageDialog = DialogUtils.createAddImageDialog(this, this, false);
        mAddImageDialog.findViewById(R.id.cameraView).setVisibility(View.GONE);
        mAddImageDialog.findViewById(R.id.dividerCameraView).setVisibility(View.GONE);
        mAddImageDialog.findViewById(R.id.galleryView).setVisibility(View.GONE);
        mAddImageDialog.findViewById(R.id.dividerGalleryView).setVisibility(View.GONE);
        mAddImageView = mAddImageDialog.findViewById(R.id.dialogAddImage);
        //loading data
        if (savedInstanceState != null) {
            mClickedShareButton = savedInstanceState.getBoolean("mClickedShareButton", false);
            final int idx = savedInstanceState.getInt("mSelectedTemplateItemIndex", 0);
            mImageInTemplateCount = savedInstanceState.getInt("mImageInTemplateCount", 0);
            mIsFrameImage = savedInstanceState.getBoolean("mIsFrameImage", false);
            loadFrameImages(mIsFrameImage);
            ALog.d(TAG, "onCreate, mTemplateItemList size=" + mTemplateItemList.size() + ", selected idx=" + idx + ", mImageInTemplateCount=" + mImageInTemplateCount);
            if (idx < mTemplateItemList.size() && idx >= 0)
                mSelectedTemplateItem = mTemplateItemList.get(idx);
            if (mSelectedTemplateItem != null) {
                ArrayList<String> imagePaths = savedInstanceState.getStringArrayList("photoItemImagePaths");
                if (imagePaths != null) {
                    int size = Math.min(imagePaths.size(), mSelectedTemplateItem.getPhotoItemList().size());
                    for (int i = 0; i < size; i++)
                        mSelectedTemplateItem.getPhotoItemList().get(i).imagePath = imagePaths.get(i);
                }
            }
            ArrayList<MultiTouchEntity> entities = savedInstanceState.getParcelableArrayList("mPhotoViewImageEntities");
            if (entities != null) {
                mPhotoView.setImageEntities(entities);
            }
        } else {
            loadFrameImages(mIsFrameImage);
            mSelectedTemplateItem = mTemplateItemList.get(selectedItemIndex);
            mSelectedTemplateItem.setSelected(true);
            if (extraImagePaths != null) {
                int size = Math.min(extraImagePaths.size(), mSelectedTemplateItem.getPhotoItemList().size());
                for (int i = 0; i < size; i++)
                    mSelectedTemplateItem.getPhotoItemList().get(i).imagePath = extraImagePaths.get(i);
            }
        }

        mTemplateAdapter = new HorizontalPreviewTemplateAdapter(mTemplateItemList, this);
        //Show templates
        mTemplateView.setHasFixedSize(true);
        mTemplateView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTemplateView.setAdapter(mTemplateAdapter);
        //Create after initializing
        mContainerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mOutputScale = ImageUtils.calculateOutputScaleFactor(mContainerLayout.getWidth(), mContainerLayout.getHeight());
                buildLayout(mSelectedTemplateItem);
                // remove listener
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mContainerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mContainerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        showAddingImageOptions(false, false);
        //Scroll to selected item
        if (mTemplateItemList != null && selectedItemIndex >= 0 && selectedItemIndex < mTemplateItemList.size()) {
            mTemplateView.scrollToPosition(selectedItemIndex);
        }
    }

    private void loadFrameImages(boolean isFrameImage) {
        ArrayList<TemplateItem> mAllTemplateItemList = new ArrayList<>();
        if (!isFrameImage) {
            mAllTemplateItemList.addAll(TemplateImageUtils.loadTemplates());
        } else {
            mAllTemplateItemList.addAll(FrameImageUtils.loadFrameImages(this));
        }

        mTemplateItemList = new ArrayList<>();
        if (mImageInTemplateCount > 0) {
            for (TemplateItem item : mAllTemplateItemList)
                if (item.getPhotoItemList().size() == mImageInTemplateCount) {
                    mTemplateItemList.add(item);
                }
        } else {
            mTemplateItemList.addAll(mAllTemplateItemList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int idx = mTemplateItemList.indexOf(mSelectedTemplateItem);
        if (idx < 0) idx = 0;
        ALog.d(TAG, "onSaveInstanceState, idx=" + idx);
        outState.putInt("mSelectedTemplateItemIndex", idx);
        //saved all image path of template item
        ArrayList<String> imagePaths = new ArrayList<>();
        for (PhotoItem item : mSelectedTemplateItem.getPhotoItemList()) {
            if (item.imagePath == null) item.imagePath = "";
            imagePaths.add(item.imagePath);
        }
        outState.putStringArrayList("photoItemImagePaths", imagePaths);
        outState.putParcelableArrayList("mPhotoViewImageEntities", mPhotoView.getImageEntities());
        outState.putInt("mImageInTemplateCount", mImageInTemplateCount);
        outState.putBoolean("mIsFrameImage", mIsFrameImage);
        outState.putBoolean("mClickedShareButton", mClickedShareButton);
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
        mPhotoView.loadImages(this);
        mPhotoView.invalidate();
        if (mClickedShareButton) {
            mClickedShareButton = false;
            if (getAdsHelper() != null) {
                getAdsHelper().showInterstitialAds();
            }
        }
    }

    private void createQuickAction() {
        QuickActionItem editItem = new QuickActionItem(ID_EDIT, getString(R.string.edit), getResources().getDrawable(R.drawable.menu_edit));
        QuickActionItem deleteItem = new QuickActionItem(ID_DELETE, getString(R.string.delete), getResources().getDrawable(R.drawable.menu_delete));
        QuickActionItem cancelItem = new QuickActionItem(ID_CANCEL, getString(R.string.cancel), getResources().getDrawable(R.drawable.menu_cancel));

        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
        editItem.setSticky(true);
        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
        //orientation
        mTextQuickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        mStickerQuickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        //add action items into QuickAction
        mTextQuickAction.addActionItem(editItem);
        mTextQuickAction.addActionItem(deleteItem);
        mTextQuickAction.addActionItem(cancelItem);
        mStickerQuickAction.addActionItem(deleteItem);
        mStickerQuickAction.addActionItem(cancelItem);
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
    public void onPhotoViewDoubleClick(PhotoView view, MultiTouchEntity entity) {
        mSelectedEntity = (ImageEntity) entity;
        if (mSelectedEntity instanceof TextEntity) {
            mTextQuickAction.show(view, (int) mSelectedEntity.getCenterX(), (int) mSelectedEntity.getCenterY());
        } else {
            mStickerQuickAction.show(view, (int) mSelectedEntity.getCenterX(), (int) mSelectedEntity.getCenterY());
        }
    }

    @Override
    public void onBackgroundDoubleClick() {

    }

    @Override
    public void onPreviewTemplateClick(TemplateItem item) {
        mSelectedTemplateItem.setSelected(false);
        for (int idx = 0; idx < mSelectedTemplateItem.getPhotoItemList().size(); idx++) {
            PhotoItem photoItem = mSelectedTemplateItem.getPhotoItemList().get(idx);
            if (photoItem.imagePath != null && photoItem.imagePath.length() > 0) {
                if (idx < mSelectedPhotoPaths.size()) {
                    mSelectedPhotoPaths.add(idx, photoItem.imagePath);
                } else {
                    mSelectedPhotoPaths.add(photoItem.imagePath);
                }
            }
        }

        final int size = Math.min(mSelectedPhotoPaths.size(), item.getPhotoItemList().size());
        for (int idx = 0; idx < size; idx++) {
            PhotoItem photoItem = item.getPhotoItemList().get(idx);
            if (photoItem.imagePath == null || photoItem.imagePath.length() < 1) {
                photoItem.imagePath = mSelectedPhotoPaths.get(idx);
            }
        }

        mSelectedTemplateItem = item;
        mSelectedTemplateItem.setSelected(true);
        mTemplateAdapter.notifyDataSetChanged();

        buildLayout(item);
        //show ads
        if (getAdsHelper() != null) {
            getAdsHelper().clickItem();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_template_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            mClickedShareButton = true;
            asyncSaveAndShare();
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            if (mAddImageView != null) {
                mAddImageView.startAnimation(mAnimation);
            }
            mAddImageDialog.show();
            return true;
        } else if (item.getItemId() == R.id.action_ratio) {
            if (mRatioDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String[] layoutRatioName = new String[]{getString(R.string.photo_editor_square), getString(R.string.fit),
                        getString(R.string.golden_ratio),};

                builder.setTitle(R.string.select_ratio);
                builder.setSingleChoiceItems(layoutRatioName, mPref.getInt(RATIO_KEY, 0),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPref.edit().putInt(RATIO_KEY, which).commit();
                                mLayoutRatio = which;
                                dialog.dismiss();
                                buildLayout(mSelectedTemplateItem);
                            }
                        });
                mRatioDialog = builder.create();
            }
            mRatioDialog.show();
            return true;
        } else if (item.getItemId() == R.id.action_help) {
            clickInfoView();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void clickInfoView() {
        if (mGuideView != null) {
            mGuideView.startAnimation(mAnimation);
        }
        mGuideDialog.show();
    }

    private void asyncSaveAndShare() {
        AsyncTask<Void, Void, File> task = new AsyncTask<Void, Void, File>() {
            Dialog dialog;
            String errMsg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(BaseTemplateDetailActivity.this, getString(R.string.app_name), getString(R.string.creating));
            }

            @Override
            protected File doInBackground(Void... params) {
                try {
                    Bitmap image = createOutputImage();
                    String fileName = DateTimeUtils.getCurrentDateTime().replaceAll(":", "-").concat(".png");
                    File collageFolder = new File(ImageUtils.OUTPUT_COLLAGE_FOLDER);
                    if (!collageFolder.exists()) {
                        collageFolder.mkdirs();
                    }
                    File photoFile = new File(collageFolder, fileName);
                    image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(photoFile));
                    PhotoUtils.addImageToGallery(photoFile.getAbsolutePath(), BaseTemplateDetailActivity.this);
                    return photoFile;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errMsg = ex.getMessage();
                } catch (OutOfMemoryError err) {
                    err.printStackTrace();
                    errMsg = err.getMessage();
                    FirebaseCrash.report(err);
                }
                return null;
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                try {
                    dialog.dismiss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (file != null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/png");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
                } else if (errMsg != null) {
                    Toast.makeText(BaseTemplateDetailActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }
                //log
                Bundle bundle = new Bundle();
                if (mIsFrameImage) {
                    String[] layoutRatioName = new String[]{"square", "fit", "golden"};
                    String ratio = "";
                    if (mLayoutRatio < layoutRatioName.length)
                        ratio = layoutRatioName[mLayoutRatio];
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share/frame_".concat(ratio).concat("_").concat(mSelectedTemplateItem.getTitle()));
                } else {
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share/template_".concat(mSelectedTemplateItem.getTitle()));
                }

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mSelectedTemplateItem.getTitle());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public float calculateScaleRatio(int imageWidth, int imageHeight) {
        float ratioWidth = ((float) imageWidth) / getPhotoViewWidth();
        float ratioHeight = ((float) imageHeight) / getPhotoViewHeight();
        return Math.max(ratioWidth, ratioHeight);
    }

    public int[] calculateThumbnailSize(int imageWidth, int imageHeight) {
        int[] size = new int[2];
        float ratioWidth = ((float) imageWidth) / getPhotoViewWidth();
        float ratioHeight = ((float) imageHeight) / getPhotoViewHeight();
        float ratio = Math.max(ratioWidth, ratioHeight);
        if (ratio == ratioWidth) {
            size[0] = getPhotoViewWidth();
            size[1] = (int) (imageHeight / ratio);
        } else {
            size[0] = (int) (imageWidth / ratio);
            size[1] = getPhotoViewHeight();
        }

        return size;
    }

    private int getPhotoViewWidth() {
        return mContainerLayout.getWidth();
    }

    private int getPhotoViewHeight() {
        return mContainerLayout.getHeight();
    }

    @Override
    public void onStickerButtonClick() {
        mItemType = Constant.STICKER_ITEM;
        pickSticker();
        try {
            mAddImageDialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTextButtonClick() {
        mItemType = Constant.TEXT_ITEM;
        addTextItem();
        try {
            mAddImageDialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackgroundColorButtonClick() {

    }

    @Override
    public void onBackgroundPhotoButtonClick() {
        mItemType = Constant.BACKGROUND_ITEM;
        pickBackground();
    }

    @Override
    public void resultStickers(Uri[] uri) {
        super.resultPickMultipleImages(uri);
        final int size = uri.length;

        for (int idx = 0; idx < size; idx++) {
            float angle = (float) (idx * Math.PI / 20);

            ImageEntity entity = new ImageEntity(uri[idx], getResources());
            entity.setInitScaleFactor(0.25);
            entity.load(this,
                    (mPhotoView.getWidth() - entity.getWidth()) / 2,
                    (mPhotoView.getHeight() - entity.getHeight()) / 2, angle);
            mPhotoView.addImageEntity(entity);
            if (ResultContainer.getInstance().getImageEntities() != null) {
                ResultContainer.getInstance().getImageEntities().add(entity);
            }
        }
    }

    @Override
    protected void resultAddTextItem(String text, int color, String fontPath) {
        final TextEntity entity = new TextEntity(text, getResources());
        entity.setTextColor(color);
        entity.setTypefacePath(fontPath);
        entity.load(this,
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
    protected void resultEditTextItem(String text, int color, String fontPath) {
        if (mSelectedEntity instanceof TextEntity) {
            TextEntity textEntity = (TextEntity) mSelectedEntity;
            textEntity.setTextColor(color);
            textEntity.setTypefacePath(fontPath);
            textEntity.setText(text);
        }
    }

    public void pickSticker() {
        Intent intent = new Intent(this, DownloadedPackageActivity.class);
        intent.putExtra(DownloadedPackageFragment.EXTRA_PACKAGE_TYPE, ItemPackageTable.STICKER_TYPE);
        startActivityForResult(intent, PICK_STICKER_REQUEST_CODE);
    }
}
