package dauroi.photoeditor.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.database.table.CropTable;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.model.CropInfo;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;
import dauroi.photoeditor.view.CropImageView;
import dauroi.photoeditor.view.DrawableCropImageView;
import dauroi.photoeditor.view.MultiTouchHandler;
import dauroi.photoeditor.view.DrawableCropImageView.OnDrawMaskListener;

@SuppressLint({"UseSparseArrays", "NewApi"})
public class CropAction extends MaskAction implements OnTouchListener, OnDrawMaskListener {
    private static final String TAG = CropAction.class.getSimpleName();
    private static final String CROP_ACTION_PREF_NAME = "cropActionPref";
    private static final String SHOW_GUIDE_NAME = "showGuide";

    private View mDrawableCropLayout;
    private DrawableCropImageView mDrawableCropImageView;
    private ImageView mClearImageView;
    private CropImageView mRectangleCropMaskView;

    private MultiTouchHandler mTouchHandler;

    private Bundle mSavedInstanceSquareData;
    private Bundle mSavedInstanceCustomData;
    private Bundle mSavedInstanceDrawData;
    //show guide
    private SharedPreferences mCropActionPref;

    public CropAction(ImageProcessingActivity activity) {
        super(activity, ItemPackageTable.CROP_TYPE);
        mCropActionPref = activity.getSharedPreferences(CROP_ACTION_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void onInit() {
        super.onInit();
        mSelectedItemIndexes = new HashMap<Long, Integer>();
        mListViewPositions = new HashMap<Long, Point>();
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        super.saveInstanceState(bundle);
        bundle.putParcelable("dauroi.photoeditor.actions.CropAction.mTouchHandler", mTouchHandler);
        if (mCurrentPosition == 0) {
            if (mSavedInstanceSquareData == null) {
                mSavedInstanceSquareData = new Bundle();
            }
            mRectangleCropMaskView.saveInstanceState(mSavedInstanceSquareData);
        } else if (mCurrentPosition == 1) {
            if (mSavedInstanceCustomData == null) {
                mSavedInstanceCustomData = new Bundle();
            }
            mRectangleCropMaskView.saveInstanceState(mSavedInstanceCustomData);
        } else if (mCurrentPosition == 2) {
            if (mSavedInstanceDrawData == null) {
                mSavedInstanceDrawData = new Bundle();
            }
            mDrawableCropImageView.saveInstanceState(mSavedInstanceDrawData);
        }
        bundle.putBundle("dauroi.photoeditor.actions.CropAction.mSavedInstanceSquareData", mSavedInstanceSquareData);
        bundle.putBundle("dauroi.photoeditor.actions.CropAction.mSavedInstanceCustomData", mSavedInstanceCustomData);
        bundle.putBundle("dauroi.photoeditor.actions.CropAction.mSavedInstanceDrawData", mSavedInstanceDrawData);
    }

    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
        ALog.d(TAG, "restoreInstanceState");
        MultiTouchHandler touchHandler = bundle.getParcelable("dauroi.photoeditor.actions.CropAction.mTouchHandler");
        if (touchHandler != null) {
            mTouchHandler = touchHandler;
        }

        mSavedInstanceSquareData = bundle.getBundle("dauroi.photoeditor.actions.CropAction.mSavedInstanceSquareData");
        mSavedInstanceCustomData = bundle.getBundle("dauroi.photoeditor.actions.CropAction.mSavedInstanceCustomData");
        mSavedInstanceDrawData = bundle.getBundle("dauroi.photoeditor.actions.CropAction.mSavedInstanceDrawData");
    }

    @Override
    public View inflateMenuView() {
        mRootActionView = mLayoutInflater.inflate(R.layout.photo_editor_action_crop, null);
        // attach crop image view
        mRectangleCropMaskView = new CropImageView(mActivity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mRectangleCropMaskView.setLayoutParams(params);
        mRectangleCropMaskView.setPaintMode(true);
        // drawable crop layout
        mDrawableCropLayout = mLayoutInflater.inflate(R.layout.photo_editor_crop_mask_draw, null);
        mDrawableCropImageView = (DrawableCropImageView) mDrawableCropLayout.findViewById(R.id.drawbleCropView);
        mDrawableCropImageView.setOnDrawMaskListener(this);
        mClearImageView = (ImageView) mDrawableCropLayout.findViewById(R.id.clearImage);

        mActivity.getNormalImageView().setOnTouchListener(this);

        mClearImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDrawableCropImageView.clear();
            }
        });

        mCurrentPosition = DEFAULT_CROP_SELECTED_ITEM_INDEX;

        return mRootActionView;
    }

    @Override
    public String getActionName() {
        return "CropAction";
    }

    @Override
    public void attach() {
        super.attach();
        ALog.d(TAG, "attach");
        mActivity.getNormalImageView().setVisibility(View.VISIBLE);
        mMaskLayout.setVisibility(View.VISIBLE);
        mActivity.applyFilter(new ImageFilter());
        if (mTouchHandler != null) {
            pinchImage();
        } else {
            mTouchHandler = new MultiTouchHandler();
            mTouchHandler.setEnableRotation(true);
            initSourceImageView();
        }

        mMaskLayout.post(new Runnable() {

            @Override
            public void run() {
                mActivity.getImageProcessingView().setVisibility(View.GONE);
            }
        });
        //check show guide
        boolean showGuide = mCropActionPref.getBoolean(SHOW_GUIDE_NAME, true);
        if (showGuide) {
            mActivity.showGuideLayout(true, true, true);
            mActivity.setGuideLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCropActionPref.edit().putBoolean(SHOW_GUIDE_NAME, false).commit();
                    mActivity.showGuideLayout(false, false, false);
                }
            });
        } else {
            mActivity.showGuideLayout(false, false, false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMaskLayout.setVisibility(View.GONE);
        mActivity.getNormalImageView().setVisibility(View.GONE);
        mActivity.getImageProcessingView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        ALog.d(TAG, "onActivityResume");
    }

    private void clickSquareView() {
        if (mSavedInstanceSquareData != null) {
            mRectangleCropMaskView.restoreInstanceState(mSavedInstanceSquareData);
        } else {
            mRectangleCropMaskView.setPaintMode(true);
            mRectangleCropMaskView.setBackgroundColor(Color.TRANSPARENT);

            final float width = mActivity.getPhotoViewWidth();
            final float height = mActivity.getPhotoViewHeight();
            float ratio = 1.0f;
            int size = (int) (Math.min(width, height));
            float clipWidth = size / 2.0f;
            float clipHeight = clipWidth / ratio;
            if (clipHeight > size / 2.0f) {
                clipHeight = size / 2.0f;
                clipWidth = size * ratio;
            }

            RectF rect = new RectF();
            rect.top = height / 2 - clipHeight / 2;
            rect.bottom = rect.top + clipHeight;
            rect.left = width / 2 - clipWidth / 2;
            rect.right = rect.left + clipWidth;

            mRectangleCropMaskView.setCropArea(rect);

            mRectangleCropMaskView.setRatio(ratio);
        }
    }

    private void clickCustomView() {
        if (mSavedInstanceCustomData != null) {
            mRectangleCropMaskView.restoreInstanceState(mSavedInstanceCustomData);
        } else {
            mRectangleCropMaskView.setPaintMode(true);
            mRectangleCropMaskView.setBackgroundColor(Color.TRANSPARENT);
            RectF rect = new RectF();
            final float width = mActivity.getPhotoViewWidth();
            final float height = mActivity.getPhotoViewHeight();
            rect.top = height / 3.0f;
            rect.bottom = 2 * height / 3.0f;
            rect.left = width / 3.0f;
            rect.right = 2 * width / 3.0f;
            mRectangleCropMaskView.setCropArea(rect);
            mRectangleCropMaskView.setRatio(CropImageView.CUSTOM_SIZE);
        }
    }

    private void initSourceImageView() {
        Matrix m = new Matrix();
        float ratio = mActivity.calculateScaleRatio();
        int[] thumbnail = mActivity.calculateThumbnailSize();
        m.postScale(1.0f / ratio, 1.0f / ratio, mActivity.getImageWidth() / 2.0f, mActivity.getImageHeight() / 2.0f);

        float dx = (mActivity.getImageWidth() - thumbnail[0]) / 2.0f;
        float dy = (mActivity.getImageHeight() - thumbnail[1]) / 2.0f;
        m.postTranslate(-dx, -dy);

        dx = (mActivity.getPhotoViewWidth() - thumbnail[0]) / 2.0f;
        dy = (mActivity.getPhotoViewHeight() - thumbnail[1]) / 2.0f;
        m.postTranslate(dx, dy);

        mActivity.getNormalImageView().setImageMatrix(m);
        mTouchHandler.setMatrix(m);
        mTouchHandler.setScale(ratio);
    }

    public void reset() {
        // reset touch handler
        mTouchHandler = null;
        mSavedInstanceCustomData = null;
        mSavedInstanceDrawData = null;
        mSavedInstanceSquareData = null;
    }

    @Override
    public void apply(final boolean finish) {
        if (!isAttached()) {
            return;
        }
        ApplyFilterTask task = new ApplyFilterTask(mActivity, new ApplyFilterListener() {
            String errMsg = null;

            @Override
            public void onFinishFiltering() {
                if (errMsg != null && errMsg.length() > 0) {
                    Toast.makeText(mActivity, errMsg, Toast.LENGTH_SHORT).show();
                }
                // reset touch handler
                mTouchHandler = null;
                mActivity.getNormalImageView().setImageBitmap(null);
                mSavedInstanceCustomData = null;
                mSavedInstanceDrawData = null;
                mSavedInstanceSquareData = null;
                mCurrentPosition = DEFAULT_CROP_SELECTED_ITEM_INDEX;
                mCurrentPackageId = 0;
                mSelectedItemIndexes.clear();
                mListViewPositions.clear();
                mCurrentPackageFolder = null;

                if (finish) {
                    done();
                }

                if (mActivity.getRotationAction() != null) {
                    mActivity.getRotationAction().reset();
                }
            }

            @Override
            public Bitmap applyFilter() {
                Bitmap bm = null;
                try {
                    if (mCurrentPackageId == 0 && mCurrentPosition < 2) {
                        bm = rectangleCrop();
                    } else if (mCurrentPackageId == 0 && mCurrentPosition == 2) {
                        bm = cropDrawnImage();
                    } else {
                        bm = cropFrame(mCurrentPosition);
                    }
                } catch (OutOfMemoryError err) {
                    errMsg = err.getMessage();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return bm;
            }
        });

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private Bitmap cropDrawnImage() {
        return mDrawableCropImageView.cropImage(true);
    }

    /**
     * This function will recycle image in ImageProcessingView. So must set
     * result image to image processingview.
     *
     * @param position
     * @return
     */
    private Bitmap cropFrame(int position) throws OutOfMemoryError {
        try {
            final ItemInfo info = mMenuItems.get(position);
            if (info.getShowingType() != ItemInfo.NORMAL_ITEM_TYPE) {
                return null;
            }
            // create normal mask image and normal image
            final float ratio = ((float) mActivity.getPhotoViewWidth()) / mActivity.getPhotoViewHeight();
            Bitmap normalImage = PhotoUtils.transparentPadding(mActivity.getImage(), ratio);
            if (normalImage != mActivity.getImage()) {
                mActivity.getImage().recycle();
            }
            Bitmap mask = PhotoUtils.decodePNGImage(mActivity, ((CropInfo) info).getBackground());
            Bitmap normalMask = PhotoUtils.transparentPadding(mask, ratio);
            if (normalMask != mask) {
                mask.recycle();
                mask = null;
                System.gc();
            }

            mask = Bitmap.createScaledBitmap(normalMask, normalImage.getWidth(), normalImage.getHeight(), true);
            if (mask != normalMask) {
                normalMask.recycle();
                normalMask = null;
                System.gc();
            }

            Bitmap croppedImage = PhotoUtils.cropImage(normalImage, mask, mTouchHandler.getScaleMatrix());
            if (croppedImage != normalImage) {
                normalImage.recycle();
                normalImage = null;
                System.gc();
            }

            Bitmap result = PhotoUtils.cleanImage(croppedImage);
            if (result != croppedImage) {
                croppedImage.recycle();
                croppedImage = null;
                System.gc();
            }

            return result;
        } catch (OutOfMemoryError err) {
            throw err;
        }
    }

    private Bitmap rectangleCrop() {
        final RectF cropArea = mRectangleCropMaskView.getCropArea();
        final float ratio = mActivity.calculateScaleRatio();
        final int[] thumbnailSize = mActivity.calculateThumbnailSize();
        final int dx = (mActivity.getPhotoViewWidth() - thumbnailSize[0]) / 2;
        final int dy = (mActivity.getPhotoViewHeight() - thumbnailSize[1]) / 2;
        final float left = Math.max((cropArea.left - dx) * ratio, 0);
        final float right = Math.min((cropArea.right - dx) * ratio, mActivity.getImageWidth());
        final float top = Math.max((cropArea.top - dy) * ratio, 0);
        final float bottom = Math.min((cropArea.bottom - dy) * ratio, mActivity.getImageHeight());
        Bitmap bitmap = Bitmap.createBitmap(mActivity.getImage(), (int) left, (int) top, (int) (right - left),
                (int) (bottom - top));
        return bitmap;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isAttached()) {
            mTouchHandler.touch(event);
            pinchImage();
            return true;
        } else {
            return false;
        }
    }

    private void pinchImage() {
        mActivity.getNormalImageView().setImageMatrix(mTouchHandler.getMatrix());
    }

    @Override
    public void onStartDrawing() {
        if (mClearImageView != null) {
            mClearImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFinishDrawing() {
        if (mClearImageView != null) {
            mClearImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getMaskLayoutRes() {
        return R.layout.photo_editor_mask_layout;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void selectNormalItem(int position) {
        if (!isAttached()) {
            return;
        }
        // save info
        final ItemInfo currentInfo = mMenuItems.get(mCurrentPosition);
        if (currentInfo.getShowingType() == ItemInfo.SQUARE_CROP_TYPE) {
            if (mSavedInstanceSquareData == null) {
                mSavedInstanceSquareData = new Bundle();
            }
            mRectangleCropMaskView.saveInstanceState(mSavedInstanceSquareData);
        } else if (currentInfo.getShowingType() == ItemInfo.CUSTOM_CROP_TYPE) {
            if (mSavedInstanceCustomData == null) {
                mSavedInstanceCustomData = new Bundle();
            }
            mRectangleCropMaskView.saveInstanceState(mSavedInstanceCustomData);
        } else if (currentInfo.getShowingType() == ItemInfo.DRAW_CROP_TYPE) {
            if (mSavedInstanceDrawData == null) {
                mSavedInstanceDrawData = new Bundle();
            }
            mDrawableCropImageView.saveInstanceState(mSavedInstanceDrawData);
        }

        final ItemInfo info = mMenuItems.get(position);
        if (info.getShowingType() == ItemInfo.SQUARE_CROP_TYPE) {
            mActivity.getNormalImageView().setImageMatrix(new Matrix());
            mActivity.getNormalImageView().setOnTouchListener(null);
            mActivity.getNormalImageView().setScaleType(ScaleType.FIT_CENTER);
            mActivity.attachMaskView(mRectangleCropMaskView);
            clickSquareView();
            mCurrentPosition = position;
        } else if (info.getShowingType() == ItemInfo.CUSTOM_CROP_TYPE) {
            mActivity.getNormalImageView().setImageMatrix(new Matrix());
            mActivity.getNormalImageView().setOnTouchListener(null);
            mActivity.getNormalImageView().setScaleType(ScaleType.FIT_CENTER);
            mActivity.attachMaskView(mRectangleCropMaskView);
            clickCustomView();
            mCurrentPosition = position;
        } else if (info.getShowingType() == ItemInfo.DRAW_CROP_TYPE) {
            mActivity.getNormalImageView().setImageMatrix(new Matrix());
            mActivity.getNormalImageView().setOnTouchListener(null);
            mActivity.getNormalImageView().setScaleType(ScaleType.FIT_CENTER);
            if (mSavedInstanceDrawData != null) {
                mDrawableCropImageView.restoreInstanceState(mSavedInstanceDrawData);
                mDrawableCropImageView.setFingerDrawingMode(true);
            }
            mDrawableCropImageView.setBitmap(mActivity.getImage());
            mActivity.attachMaskView(mDrawableCropLayout);
            mCurrentPosition = position;
        } else if (info.getShowingType() == ItemInfo.NORMAL_ITEM_TYPE) {
            mActivity.attachMaskView(mMaskLayout);
            // recycle old bg
            Drawable drawable = mImageMaskView.getBackground();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bd = (BitmapDrawable) drawable;
                Bitmap bm = bd.getBitmap();
                if (bm != null && !bm.isRecycled()) {
                    mImageMaskView.setBackgroundColor(Color.TRANSPARENT);
                    bm.recycle();
                    bm = null;
                    System.gc();
                }
            }

            String bgPath = ((CropInfo) info).getForeground();
            Bitmap bg = PhotoUtils.decodePNGImage(mActivity, bgPath);
            adjustImageMaskLayout(bg.getWidth(), bg.getHeight());
            if (Build.VERSION.SDK_INT < 16) {
                mImageMaskView.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), bg));
            } else {
                mImageMaskView.setBackground(new BitmapDrawable(mActivity.getResources(), bg));
            }

            mActivity.getNormalImageView().setImageBitmap(mActivity.getImage());
            mActivity.getNormalImageView().setOnTouchListener(this);
            mActivity.getNormalImageView().setScaleType(ScaleType.MATRIX);
            mActivity.getNormalImageView().post(new Runnable() {

                @Override
                public void run() {
                    if (mTouchHandler != null)
                        mActivity.getNormalImageView().setImageMatrix(mTouchHandler.getMatrix());
                }
            });

            mCurrentPosition = position;
        }
    }

    @Override
    protected List<? extends ItemInfo> loadNormalItems(long packageId, String packageFolder) {
        CropTable cropTable = new CropTable(mActivity);
        List<CropInfo> cropInfos = cropTable.getAllRows(packageId);
        if (packageFolder != null && packageFolder.length() > 0) {
            final String baseFolder = Utils.CROP_FOLDER.concat("/").concat(packageFolder).concat("/");
            for (CropInfo info : cropInfos) {
                info.setForeground(baseFolder.concat(info.getForeground()));
                info.setThumbnail(baseFolder.concat(info.getThumbnail()));
                if (info.getSelectedThumbnail() != null && info.getSelectedThumbnail().length() > 0)
                    info.setSelectedThumbnail(baseFolder.concat(info.getSelectedThumbnail()));
                info.setBackground(baseFolder.concat(info.getBackground()));
            }
        }

        List<ItemInfo> itemInfos = new ArrayList<ItemInfo>();
        if (packageId < 1) {
            final ItemInfo squareItem = new ItemInfo();
            squareItem.setTitle(mActivity.getString(R.string.photo_editor_square));
            squareItem.setThumbnail(PhotoUtils.DRAWABLE_PREFIX + R.drawable.photo_editor_crop_square_normal);
            squareItem.setSelectedThumbnail(PhotoUtils.DRAWABLE_PREFIX + R.drawable.photo_editor_crop_square_pressed);
            squareItem.setShowingType(ItemInfo.SQUARE_CROP_TYPE);
            itemInfos.add(0, squareItem);

            final ItemInfo customItem = new ItemInfo();
            customItem.setTitle(mActivity.getString(R.string.photo_editor_custom));
            customItem.setThumbnail(PhotoUtils.DRAWABLE_PREFIX + R.drawable.photo_editor_crop_custom_normal);
            customItem.setSelectedThumbnail(PhotoUtils.DRAWABLE_PREFIX + R.drawable.photo_editor_crop_custom_pressed);
            customItem.setShowingType(ItemInfo.CUSTOM_CROP_TYPE);
            itemInfos.add(1, customItem);

            final ItemInfo drawItem = new ItemInfo();
            drawItem.setTitle(mActivity.getString(R.string.photo_editor_draw));
            drawItem.setThumbnail(PhotoUtils.DRAWABLE_PREFIX + R.drawable.photo_editor_ic_draw_bottom_normal);
            drawItem.setSelectedThumbnail(PhotoUtils.DRAWABLE_PREFIX + R.drawable.photo_editor_ic_draw_bottom_pressed);
            drawItem.setShowingType(ItemInfo.DRAW_CROP_TYPE);
            itemInfos.add(2, drawItem);
        }

        itemInfos.addAll(cropInfos);
        return itemInfos;
    }
}
