package dauroi.photoeditor.ui.activity;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

import dauroi.com.imageprocessing.ImageProcessingView;
import dauroi.com.imageprocessing.ImageProcessor;
import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.actions.BaseAction;
import dauroi.photoeditor.actions.CropAction;
import dauroi.photoeditor.actions.DrawAction;
import dauroi.photoeditor.actions.EffectAction;
import dauroi.photoeditor.actions.FocusAction;
import dauroi.photoeditor.actions.FrameAction;
import dauroi.photoeditor.actions.RotationAction;
import dauroi.photoeditor.actions.TextAction;
import dauroi.photoeditor.actions.TouchBlurAction;
import dauroi.photoeditor.adapter.CustomMenuAdapter;
import dauroi.photoeditor.blur.StackBlurManager;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.database.DatabaseManager;
import dauroi.photoeditor.horizontalListView.widget.AdapterView;
import dauroi.photoeditor.horizontalListView.widget.HListView;
import dauroi.photoeditor.listener.OnDoneActionsClickListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.utils.FileUtils;
import dauroi.photoeditor.utils.ImageDecoder;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.TempDataContainer;
import dauroi.photoeditor.utils.Utils;

public class ImageProcessingActivity extends BaseAdActivity {
    private static final String TAG = ImageProcessingActivity.class.getSimpleName();
    private static final int DEFAULT_SELECTED_ACTION_INDEX = 1;
    public static final String IMAGE_URI_KEY = "imageUri";
    public static final String IS_EDITING_IMAGE_KEY = "isEditingImage";
    public static final String ROTATION_KEY = "rotation";
    public static final String EXTRA_FLIP_IMAGE = "flipImage";
    public static final String EXTRA_EDITING_IMAGE_PATH = "editingImagePath";
    public static final String EXTRA_RETURN_EDITED_IMAGE_PATH = "editedImage";

    private View mGuideLayout;
    private TextView mFirstGuideView;
    private TextView mSecondGuideView;
    private RelativeLayout mPhotoViewLayout;
    private FrameLayout mBottomLayout;
    private HListView mTopbarListView;
    private CustomMenuAdapter mTopMenuAdapter;
    private TextView mDoneButton;
    private TextView mApplyButton;
    private List<ItemInfo> mTopbarMenuItems;
    private FrameLayout mImageLayout;
    private View mProgressBar;
    // actions
    private int mCurrentTopMenuPosition = DEFAULT_SELECTED_ACTION_INDEX;
    private BaseAction mCurrentAction;
    private BaseAction[] mActions;
    // Image processing
    private int mPhotoViewWidth = 0, mPhotoViewHeight = 0;
    private ImageProcessingView mImageProcessingView;
    private ImageFilter mFilter;

    private OnDoneActionsClickListener mDoneActionsClickListener;
    private Bitmap mImage;
    private ImageView mNormalImageView;
    private Uri mImageUri;
    private boolean mIsEditingImage = false;
    private String mEditingImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_editor_activity_main);

        if (!DatabaseManager.getInstance(this).isDbFileExisted()) {
            DatabaseManager.getInstance(this).createDb();
        } else {
            boolean isOpen = DatabaseManager.getInstance(this).openDb();
            ALog.d("ImageProcessingActivity", "onCreate, database isOpen=" + isOpen);
        }

        mImageUri = getIntent().getParcelableExtra(IMAGE_URI_KEY);
        mIsEditingImage = getIntent().getBooleanExtra(IS_EDITING_IMAGE_KEY, false);
        mEditingImagePath = getIntent().getStringExtra(EXTRA_EDITING_IMAGE_PATH);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mImageProcessingView = (ImageProcessingView) findViewById(R.id.imageProcessingView);
        mImageProcessingView.setScaleType(ImageProcessor.ScaleType.CENTER_INSIDE);
        setImageProcessingViewBackgroundColor();
        mNormalImageView = (ImageView) findViewById(R.id.sourceImage);
        mProgressBar = findViewById(R.id.progressBar);
        mImageLayout = (FrameLayout) findViewById(R.id.imageViewLayout);
        mPhotoViewLayout = (RelativeLayout) findViewById(R.id.photoViewLayout);
        mBottomLayout = (FrameLayout) findViewById(R.id.bottomLayout);
        mTopbarListView = (HListView) findViewById(R.id.topListView);
        mTopbarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentAction != mActions[position]) {
                    mTopbarMenuItems.get(mCurrentTopMenuPosition).setSelected(false);
                    if (mCurrentTopMenuPosition < position) {
                        if (position < mTopbarMenuItems.size() - 1) {
                            mTopbarListView.smoothScrollToPosition(position + 1);
                        } else {
                            mTopbarListView.smoothScrollToPosition(position);
                        }
                    } else {
                        if (position > 0) {
                            mTopbarListView.smoothScrollToPosition(position - 1);
                        } else {
                            mTopbarListView.smoothScrollToPosition(position);
                        }
                    }
                    // do action
                    selectAction(position);
                }

                if (getAdCreator() != null)
                    getAdCreator().onClicked();
            }
        });

        mDoneButton = (TextView) findViewById(R.id.doneButton);
        mDoneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDoneActionsClickListener != null) {
                    mDoneActionsClickListener.onDoneButtonClick();
                }
            }
        });

        mApplyButton = (TextView) findViewById(R.id.applyButton);
        mApplyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDoneActionsClickListener != null) {
                    mDoneActionsClickListener.onApplyButtonClick();
                }
            }
        });
        //Guide views
        mGuideLayout = findViewById(R.id.guideLayout);
        mFirstGuideView = (TextView) findViewById(R.id.firstGuideImage);
        mSecondGuideView = (TextView) findViewById(R.id.secondGuideImage);
        // topbar menus
        addTopbarMenus();
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
            mImageProcessingView.setImage(mImage);
            selectAction(mCurrentTopMenuPosition);
        } else {
            initInfo();
        }
        //StoreUtils.redownloadItems();
    }

    private void initInfo() {
        TempDataContainer.getInstance().clear();
        mActions = new BaseAction[mTopbarMenuItems.size()];
        mActions[0] = new EffectAction(this);
        mActions[1] = new CropAction(this);
        mCurrentAction = mActions[1];
        // test
        if (mPhotoViewLayout != null) {
            mPhotoViewLayout.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            mPhotoViewWidth = mPhotoViewLayout.getWidth();
                            mPhotoViewHeight = mPhotoViewLayout.getHeight();
                            DisplayMetrics out = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(out);
                            ALog.d(TAG, "onGlobalLayout, mPhotoViewWidth=" + mPhotoViewWidth + ", mPhotoViewHeight=" + mPhotoViewHeight + ", displayWidth=" + out.widthPixels + ", displayHeight=" + out.heightPixels);
                            if (out.heightPixels >= out.widthPixels) {
                                if (mImageUri != null) {
                                    Bitmap tmp = ImageDecoder.decodeUriToBitmap(ImageProcessingActivity.this, mImageUri);
                                    int mImageRot = getIntent().getIntExtra(ROTATION_KEY, 0);
                                    boolean flip = getIntent().getBooleanExtra(EXTRA_FLIP_IMAGE, false);
                                    ALog.d(TAG, "onGlobalLayout, mImageRot=" + mImageRot + ", flip=" + flip);
                                    if (mImageRot > 0) {
                                        mImage = PhotoUtils.rotateImage(tmp, mImageRot, flip);
                                        if (mImage != tmp) {
                                            tmp.recycle();
                                            tmp = null;
                                            System.gc();
                                        }
                                    } else {
                                        mImage = tmp;
                                    }

                                    mImageProcessingView.setImage(mImage);
                                }
                                // attach menu
                                selectAction(DEFAULT_SELECTED_ACTION_INDEX);
                            }
                            // remove listener
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mPhotoViewLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } else {
                                mPhotoViewLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        }
                    });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ALog.d(TAG, "onConfigurationChanged");
        initInfo();
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public boolean isEditingImage() {
        return mIsEditingImage;
    }

    public String getEditingImagePath() {
        return mEditingImagePath;
    }

    public void showGuideLayout(boolean showLayout, boolean showFirstGuideView, boolean showSecondGuideView) {
        if (mGuideLayout != null) {
            if (showLayout) {
                mGuideLayout.setVisibility(View.VISIBLE);

                if (mFirstGuideView != null) {
                    if (showFirstGuideView) {
                        mFirstGuideView.setVisibility(View.VISIBLE);
                    } else {
                        mFirstGuideView.setVisibility(View.INVISIBLE);
                    }
                }

                if (mSecondGuideView != null) {
                    if (showSecondGuideView) {
                        mSecondGuideView.setVisibility(View.VISIBLE);
                    } else {
                        mSecondGuideView.setVisibility(View.GONE);
                    }
                }
            } else {
                mGuideLayout.setVisibility(View.GONE);
            }
        }
    }

    public void setGuideTexts(String firstGuide, String secondGuide) {
        if (mFirstGuideView != null && firstGuide != null) {
            mFirstGuideView.setText(firstGuide);
        }

        if (mSecondGuideView != null && secondGuide != null) {
            mSecondGuideView.setText(secondGuide);
        }
    }

    public void setGuideLayoutClickListener(View.OnClickListener listener) {
        if (mGuideLayout != null) {
            if (listener != null) {
                mGuideLayout.setOnClickListener(listener);
            } else {
                mGuideLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ALog.d("ImageProcessingActivity", "onResume");
        for (BaseAction action : mActions)
            if (action != null) {
                action.onActivityResume();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (BaseAction action : mActions)
            if (action != null) {
                action.onActivityPause();
            }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ALog.d("ImageProcessingActivity", "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ALog.d("ImageProcessingActivity", "onSaveInstanceState");
        outState.putParcelable("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mImageUri", mImageUri);
        outState.putBoolean("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mIsEditingImage", mIsEditingImage);
        outState.putInt("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mCurrentTopMenuPosition",
                mCurrentTopMenuPosition);
        outState.putInt("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mPhotoViewWidth", mPhotoViewWidth);
        outState.putInt("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mPhotoViewHeight", mPhotoViewHeight);
        outState.putString("ImageProcessingActivity.mEditingImagePath", mEditingImagePath);
        if (mImage != null && !mImage.isRecycled()) {
            String outPath = Utils.TEMP_FOLDER.concat("/processing_image.tmp");
            String tempPath = FileUtils.saveBitmapToFile(mImage, outPath);
            if (tempPath != null) {
                outState.putString("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mImage", tempPath);
            }
        }

        for (BaseAction action : mActions)
            if (action != null) {
                action.saveInstanceState(outState);
            }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        ALog.d("ImageProcessingActivity", "restoreInstanceState");
        mEditingImagePath = savedInstanceState.getString("ImageProcessingActivity.mEditingImagePath");
        mImageUri = savedInstanceState.getParcelable("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mImageUri");
        mIsEditingImage = savedInstanceState.getBoolean("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mIsEditingImage", mIsEditingImage);
        mCurrentTopMenuPosition = savedInstanceState.getInt(
                "dauroi.photoeditor.ui.activity.ImageProcessingActivity.mCurrentTopMenuPosition",
                mCurrentTopMenuPosition);
        mPhotoViewWidth = savedInstanceState
                .getInt("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mPhotoViewWidth", mPhotoViewWidth);
        mPhotoViewHeight = savedInstanceState
                .getInt("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mPhotoViewHeight", mPhotoViewHeight);
        String tempPath = savedInstanceState.getString("dauroi.photoeditor.ui.activity.ImageProcessingActivity.mImage");
        if (tempPath != null && tempPath.length() > 0) {
            if (mImage != null && !mImage.isRecycled()) {
                mImage.recycle();
            }
            mImage = BitmapFactory.decodeFile(tempPath);
        }

        mActions = new BaseAction[mTopbarMenuItems.size()];
        mActions[0] = new EffectAction(this);
        mActions[1] = new CropAction(this);
        mActions[2] = new RotationAction(this);
        mActions[3] = new TextAction(this);
        mActions[4] = new FrameAction(this);
        // mActions[5] = new ShadeAction(this);
        mActions[5] = new DrawAction(this);
        mActions[6] = new FocusAction(this);
        mActions[7] = new TouchBlurAction(this);

        for (BaseAction action : mActions) {
            action.restoreInstanceState(savedInstanceState);
        }

    }

    public CropAction getCropAction() {
        if (mActions != null && mActions[1] != null) {
            return (CropAction) mActions[1];
        } else {
            return null;
        }
    }

    public RotationAction getRotationAction() {
        if (mActions != null && mActions[2] != null) {
            return (RotationAction) mActions[2];
        } else {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        for (BaseAction action : mActions)
            if (action != null) {
                action.onActivityDestroy();
            }

        super.onDestroy();
        ALog.d(TAG, "destroy");
        StackBlurManager.shutdownExecutor();
        DatabaseManager.getInstance(this).closeDb();
    }

    public ImageView getNormalImageView() {
        return mNormalImageView;
    }

    public void hideAllMenus() {
        mDoneButton.setVisibility(View.INVISIBLE);
        mApplyButton.setVisibility(View.INVISIBLE);
        mTopbarListView.setVisibility(View.INVISIBLE);
        mBottomLayout.setVisibility(View.INVISIBLE);
    }

    public void showAllMenus() {
        mDoneButton.setVisibility(View.VISIBLE);
        mApplyButton.setVisibility(View.VISIBLE);
        mTopbarListView.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.VISIBLE);
    }

    public ImageProcessingView getImageProcessingView() {
        return mImageProcessingView;
    }

    public void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mApplyButton.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mApplyButton.setVisibility(View.VISIBLE);
        }
    }

    public void selectAction(int position) {
        switch (position) {
            case 0:
                if (mActions[position] == null) {
                    mActions[position] = new EffectAction(this);
                }
                break;
            case 1:
                if (mActions[position] == null) {
                    mActions[position] = new CropAction(this);
                }
                break;
            case 2:
                if (mActions[position] == null) {
                    mActions[position] = new RotationAction(this);
                }
                break;
            case 3:
                if (mActions[position] == null) {
                    mActions[position] = new TextAction(this);
                }
                break;
            case 4:
                if (mActions[position] == null) {
                    mActions[position] = new FrameAction(this);
                }
                break;
            // case 5:
            // if (mActions[position] == null) {
            // mActions[position] = new ShadeAction(this);
            // }
            // break;
            case 5:
                if (mActions[position] == null) {
                    mActions[position] = new DrawAction(this);
                }
                break;
            case 6:
                if (mActions[position] == null) {
                    mActions[position] = new FocusAction(this);
                }
                break;
            case 7:
                if (mActions[position] == null) {
                    mActions[position] = new TouchBlurAction(this);
                }
                break;
            default:
                break;
        }

        if (mTopbarMenuItems.get(mCurrentTopMenuPosition) != null)
            mTopbarMenuItems.get(mCurrentTopMenuPosition).setSelected(false);
        if (mTopbarMenuItems.get(position) != null)
            mTopbarMenuItems.get(position).setSelected(true);
        if (mTopMenuAdapter != null)
            mTopMenuAdapter.notifyDataSetChanged();
        mCurrentTopMenuPosition = position;

        if (mActions[position] != null) {
            mActions[position].attach();
        }
    }

    public void attachMaskView(View v) {
        mImageLayout.removeAllViews();
        if (v != null) {
            mImageLayout.addView(v);
            mImageLayout.setVisibility(View.VISIBLE);
        } else {
            mImageLayout.setVisibility(View.GONE);
        }
    }

    public boolean applyFilter(ImageFilter filter) {
        if (mPhotoViewWidth < 5 || mPhotoViewHeight < 5) {
            return false;
        }

        if (mFilter != filter) {
            mFilter = filter;
            mImageProcessingView.setFilter(filter);
            mImageProcessingView.requestRender();
            return true;
        }

        return false;
    }

    public ImageFilter getFilter() {
        return mFilter;
    }

    public void attachBottomMenu(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.photo_editor_slide_in_bottom);
        mBottomLayout.removeAllViews();
        mBottomLayout.addView(view);
        view.startAnimation(anim);
    }

    public void setCurrentAction(BaseAction currentAction) {
        this.mCurrentAction = currentAction;
    }

    public BaseAction getCurrentAction() {
        return mCurrentAction;
    }

    public int getPhotoViewWidth() {
        return mPhotoViewWidth;
    }

    public int getPhotoViewHeight() {
        return mPhotoViewHeight;
    }

    public void setImage(Bitmap image, boolean recycleOld) {
        if (image != null && !image.isRecycled()) {
            if (recycleOld && mImage != null && mImage != image && !mImage.isRecycled()) {
                mImage.recycle();
                mImage = null;
                System.gc();
            }

            mImageProcessingView.getImageProcessor().deleteImage();
            mImage = image;
            mImageProcessingView.setImage(mImage);
        } else {
            FirebaseCrash.report(new Exception("Set null image or recycled image"));
        }
    }

    public Bitmap getImage() {
        if (mImage == null || mImage.isRecycled()) {
            if (mImageUri != null) {
                mImage = ImageDecoder.decodeUriToBitmap(ImageProcessingActivity.this, mImageUri);
                FirebaseCrash.report(new Exception("mImage is null. Recreate!!!"));
            } else {
                FirebaseCrash.report(new Exception("mImage is null and mImageUri is also null!!!"));
            }
        }
        return mImage;
    }

    public int getImageWidth() {
        if (mImage != null && !mImage.isRecycled()) {
            return mImage.getWidth();
        }

        return 0;
    }

    public int getImageHeight() {
        if (mImage != null && !mImage.isRecycled()) {
            return mImage.getHeight();
        }

        return 0;
    }

    public float calculateScaleRatio() {
        return calculateScaleRatio(getImageWidth(), getImageHeight());
    }

    public int[] calculateThumbnailSize() {
        return calculateThumbnailSize(getImageWidth(), getImageHeight());
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

    public void setDoneActionsClickListener(OnDoneActionsClickListener doneActionsClickListener) {
        mDoneActionsClickListener = doneActionsClickListener;
    }

    private void addTopbarMenus() {
        mTopbarMenuItems = new ArrayList<ItemInfo>();

        ItemInfo item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_effect));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_effect_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_effect_pressed);
        mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_crop));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_crop_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_crop_pressed);
        mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_rotate));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_rotate_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_rotate_pressed);
        mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_text));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_text_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_text_pressed);
        mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_frame));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_frame_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_frame_pressed);
        mTopbarMenuItems.add(item);

        // item = new ItemInfo();
        // item.setTitle(getString(R.string.photo_editor_shade));
        // item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_shade);
        // mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_draw));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_draw_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_draw_pressed);
        mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_focus));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_focus_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_focus_pressed);
        mTopbarMenuItems.add(item);

        item = new ItemInfo();
        item.setTitle(getString(R.string.photo_editor_blur));
        item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_blur_normal);
        item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_blur_pressed);
        mTopbarMenuItems.add(item);

        mTopMenuAdapter = new CustomMenuAdapter(this, mTopbarMenuItems, false);
        mTopbarListView.setAdapter(mTopMenuAdapter);
    }

    private void setImageProcessingViewBackgroundColor() {
        final int color = getResources().getColor(R.color.photo_editor_bg_action_bar);
        float r = Color.red(color) / 255.0f;
        float g = Color.green(color) / 255.0f;
        float b = Color.blue(color) / 255.0f;
        float a = Color.alpha(color) / 255.0f;
        mImageProcessingView.setBackground(r, g, b, a);
    }

    @Override
    public void finish() {
        if (getAdCreator() != null)
            getAdCreator().showGoogleInterstitialAd();
        super.finish();
    }
}
