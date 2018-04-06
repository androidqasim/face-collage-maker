package dauroi.photoeditor.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import dauroi.com.imageprocessing.ImageProcessor;
import dauroi.com.imageprocessing.filter.processing.CircleSelectiveBlurFilter;
import dauroi.com.imageprocessing.filter.processing.LinearSelectiveBlurFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;
import dauroi.photoeditor.view.FocusImageView;

public class FocusAction extends BlurAction implements FocusImageView.OnImageFocusListener {
    private static final String TAG = FocusAction.class.getSimpleName();
    private static final String FOCUS_ACTION_PREF_NAME = "focusActionPref";
    private static final String SHOW_GUIDE_NAME = "showGuide";

    private View mCircleView;
    private ImageView mCircleThumbnailView;
    private TextView mCircleNameView;
    private View mLinearView;
    private ImageView mLinearThumbnailView;
    private TextView mLinearNameView;
    private View mCurrentFocusView;

    private FocusImageView mFocusImageView;
    private CircleSelectiveBlurFilter mCircleSelectiveBlurFilter;
    private LinearSelectiveBlurFilter mLinearSelectiveBlurFilter;
    private Bitmap mBlurredImage;
    //show guide
    private SharedPreferences mFocusActionPref;

    public FocusAction(ImageProcessingActivity activity) {
        super(activity);
        mFocusActionPref = activity.getSharedPreferences(FOCUS_ACTION_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        super.saveInstanceState(bundle);
        if (mCurrentFocusView == mCircleView) {
            bundle.putInt("dauroi.photoeditor.actions.FocusAction.mCurrentFocusViewIdx", 0);
        } else {
            bundle.putInt("dauroi.photoeditor.actions.FocusAction.mCurrentFocusViewIdx", 1);
        }
        mFocusImageView.saveInstanceState(bundle);
        if (mCircleSelectiveBlurFilter != null) {
            bundle.putFloat("dauroi.photoeditor.actions.FocusAction.mCircleSelectiveBlurFilter.mBlurSize",
                    mCircleSelectiveBlurFilter.getBlurSize());
            bundle.putFloatArray("dauroi.photoeditor.actions.FocusAction.mCircleSelectiveBlurFilter.mCenterPoint",
                    mCircleSelectiveBlurFilter.getCenterPoint());
            bundle.putFloat("dauroi.photoeditor.actions.FocusAction.mCircleSelectiveBlurFilter.mRadius",
                    mCircleSelectiveBlurFilter.getRadius());
        }

        if (mLinearSelectiveBlurFilter != null) {
            bundle.putFloat("dauroi.photoeditor.actions.FocusAction.mLinearSelectiveBlurFilter.mBlurSize",
                    mLinearSelectiveBlurFilter.getExclude());
            bundle.putFloatArray("dauroi.photoeditor.actions.FocusAction.mLinearSelectiveBlurFilter.mLine",
                    mLinearSelectiveBlurFilter.getLine());
            bundle.putFloat("dauroi.photoeditor.actions.FocusAction.mLinearSelectiveBlurFilter.mRadius",
                    mLinearSelectiveBlurFilter.getRadius());
        }
    }

    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
        int currentFocusIdx = bundle.getInt("dauroi.photoeditor.actions.FocusAction.mCurrentFocusViewIdx", 0);
        if (currentFocusIdx == 0) {
            mCurrentFocusView = mCircleView;
        } else {
            mCurrentFocusView = mLinearView;
        }
        mFocusImageView.restoreInstanceState(bundle);
        mCircleSelectiveBlurFilter.setBlurSize(
                bundle.getFloat("dauroi.photoeditor.actions.FocusAction.mCircleSelectiveBlurFilter.mBlurSize",
                        mCircleSelectiveBlurFilter.getBlurSize()));
        float[] center = bundle
                .getFloatArray("dauroi.photoeditor.actions.FocusAction.mCircleSelectiveBlurFilter.mCenterPoint");
        if (center != null)
            mCircleSelectiveBlurFilter.setCenterPoint(center);
        mCircleSelectiveBlurFilter
                .setRadius(bundle.getFloat("dauroi.photoeditor.actions.FocusAction.mCircleSelectiveBlurFilter.mRadius",
                        mCircleSelectiveBlurFilter.getRadius()));

        mLinearSelectiveBlurFilter.setExclude(
                bundle.getFloat("dauroi.photoeditor.actions.FocusAction.mLinearSelectiveBlurFilter.mBlurSize",
                        mLinearSelectiveBlurFilter.getExclude()));
        center = bundle.getFloatArray("dauroi.photoeditor.actions.FocusAction.mLinearSelectiveBlurFilter.mLine");
        if (center != null)
            mLinearSelectiveBlurFilter.setLine(center);
        mLinearSelectiveBlurFilter
                .setRadius(bundle.getFloat("dauroi.photoeditor.actions.FocusAction.mLinearSelectiveBlurFilter.mRadius",
                        mLinearSelectiveBlurFilter.getRadius()));
    }

    @Override
    public void apply(final boolean finish) {
        if (!isAttached()) {
            return;
        }
        ApplyFilterListener listener = new ApplyFilterListener() {

            @Override
            public void onFinishFiltering() {
                mLinearSelectiveBlurFilter = null;
                mCircleSelectiveBlurFilter = null;

                if (finish) {
                    done();
                }
            }

            @Override
            public Bitmap applyFilter() {
                final int[] size = mActivity.calculateThumbnailSize();
                final float ratio = mActivity.calculateScaleRatio();
                final float dx = (mActivity.getPhotoViewWidth() - size[0]) / 2.0f;
                final float dy = (mActivity.getPhotoViewHeight() - size[1]) / 2.0f;
                Bitmap result = null;
                if (mCurrentFocusView == mCircleView) {
                    ALog.d("FocusAction", "circleView");
                    final float[] circle = mFocusImageView.getCircle();
                    circle[0] = (circle[0] - dx) * ratio;
                    circle[1] = (circle[1] - dy) * ratio;
                    circle[2] = circle[2] * ratio;
                    CircleSelectiveBlurFilter filter = new CircleSelectiveBlurFilter();
                    filter.setBlurSize(mCircleSelectiveBlurFilter.getBlurSize() * ratio);
                    filter.setRecycleBitmap(true);
                    filter.setRadius(circle[2]);
                    filter.setCenterPoint(new float[]{circle[0], mActivity.getImageHeight() - circle[1]});
                    filter.setBitmap(mBlurredImage);

                    result = ImageProcessor.getFiltratedBitmap(mActivity.getImage(), filter);
                } else {
                    float[] focus = mFocusImageView.getLinearFocusInfos(ratio, dx, dy);
                    LinearSelectiveBlurFilter filter = new LinearSelectiveBlurFilter();
                    filter.setRecycleBitmap(true);
                    filter.setExclude(mLinearSelectiveBlurFilter.getExclude() * ratio);
                    filter.setRadius(focus[3]);
                    filter.setLine(new float[]{focus[0], focus[1], focus[2]});
                    filter.setBitmap(mBlurredImage);
                    result = ImageProcessor.getFiltratedBitmap(mActivity.getImage(), filter);
                }

                mCircleSelectiveBlurFilter.setRecycleBitmap(true);
                mCircleSelectiveBlurFilter.destroy();
                mCircleSelectiveBlurFilter = null;

                mLinearSelectiveBlurFilter.setRecycleBitmap(true);
                mLinearSelectiveBlurFilter.destroy();
                mLinearSelectiveBlurFilter = null;

                return result;
            }
        };

        ApplyFilterTask task = new ApplyFilterTask(mActivity, listener);
        task.execute();
    }

    @Override
    public View inflateMenuView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        mRootActionView = inflater.inflate(R.layout.photo_editor_action_focus, null);
        mCircleView = mRootActionView.findViewById(R.id.circleView);
        mCircleNameView = (TextView) mRootActionView.findViewById(R.id.circleNameView);
        mCircleThumbnailView = (ImageView) mRootActionView.findViewById(R.id.circleThumbnailView);
        mCircleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                circleFocusButtonClick();
                mCurrentFocusView = mCircleView;
                onClicked();
            }
        });

        mLinearView = mRootActionView.findViewById(R.id.linearView);
        mLinearNameView = (TextView) mRootActionView.findViewById(R.id.linearNameView);
        mLinearThumbnailView = (ImageView) mRootActionView.findViewById(R.id.linearThumbnailView);
        mLinearView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                linearFocusButtonClick();
                mCurrentFocusView = mLinearView;
                onClicked();
            }
        });

        mCurrentFocusView = mCircleView;

        mFocusImageView = new FocusImageView(mActivity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mFocusImageView.setLayoutParams(params);
        mFocusImageView.setOnImageFocusListener(this);

        initFocusFilters();

        return mRootActionView;
    }

    @Override
    public void attach() {
        super.attach();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mActivity.hideAllMenus();
                mActivity.showProgress(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                long time = System.currentTimeMillis();
                recycleImages();
                if (mLinearSelectiveBlurFilter == null || mCircleSelectiveBlurFilter == null) {
                    initFocusFilters();
                } else {
                    mLinearSelectiveBlurFilter = new LinearSelectiveBlurFilter(mLinearSelectiveBlurFilter.getLine(),
                            mLinearSelectiveBlurFilter.getRadius(), mLinearSelectiveBlurFilter.getExclude());
                    mLinearSelectiveBlurFilter.setRecycleBitmap(false);

                    mCircleSelectiveBlurFilter = new CircleSelectiveBlurFilter(
                            mCircleSelectiveBlurFilter.getCenterPoint(), mCircleSelectiveBlurFilter.getRadius(),
                            mCircleSelectiveBlurFilter.getBlurSize());
                    mCircleSelectiveBlurFilter.setRecycleBitmap(false);
                }

                long blurredTime = System.currentTimeMillis();
                mBlurredImage = PhotoUtils.blurImage(mActivity.getImage(), FAST_BLUR_RADIUS);
                ALog.d(TAG, "blurred time = " + (System.currentTimeMillis() - blurredTime));
                mCircleSelectiveBlurFilter.setBitmap(mBlurredImage);
                mLinearSelectiveBlurFilter.setBitmap(mBlurredImage);
                ALog.d(TAG, "attach.doInBackground, takeTime=" + (System.currentTimeMillis() - time));
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mActivity.attachMaskView(mFocusImageView);
                mFocusImageView.setDisplayFocus(true);
                if (mCurrentFocusView == mCircleView) {
                    changeSelectedFocusView(false);
                    mActivity.applyFilter(mCircleSelectiveBlurFilter);
                } else {
                    changeSelectedFocusView(true);
                    mActivity.applyFilter(mLinearSelectiveBlurFilter);
                }
                mActivity.showProgress(false);
                mActivity.showAllMenus();
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //check show guide
        boolean showGuide = mFocusActionPref.getBoolean(SHOW_GUIDE_NAME, true);
        if (showGuide) {
            mActivity.setGuideTexts(mActivity.getString(R.string.photo_editor_guide_zoom_focus_area), mActivity.getString(R.string.photo_editor_guide_drag_focus_area));
            mActivity.showGuideLayout(true, true, true);
            mActivity.setGuideLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFocusActionPref.edit().putBoolean(SHOW_GUIDE_NAME, false).commit();
                    mActivity.showGuideLayout(false, false, false);
                }
            });
        } else {
            mActivity.showGuideLayout(false, false, false);
        }
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        if(isAttached()) {
            mActivity.attachMaskView(mFocusImageView);
            if (mCurrentFocusView == mCircleView) {
                circleFocusButtonClick();
            } else {
                linearFocusButtonClick();
            }
        }
    }

    private void changeSelectedFocusView(boolean isLinear) {
        mCircleThumbnailView.setImageResource(R.drawable.photo_editor_ic_radial_normal);
        mCircleNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
        mLinearThumbnailView.setImageResource(R.drawable.photo_editor_ic_linear_normal);
        mLinearNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
        if(isLinear){
            mLinearThumbnailView.setImageResource(R.drawable.photo_editor_ic_linear_pressed);
            mLinearNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
        }else{
            mCircleThumbnailView.setImageResource(R.drawable.photo_editor_ic_radial_pressed);
            mCircleNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
        }
    }

    private void initFocusFilters() {
        mFocusImageView.setupFocusInfos(mActivity.getPhotoViewWidth(), mActivity.getPhotoViewHeight());

        float[] circle = mFocusImageView.getCircle();
        mCircleSelectiveBlurFilter = new CircleSelectiveBlurFilter();
        mCircleSelectiveBlurFilter.setRecycleBitmap(false);
        mCircleSelectiveBlurFilter.setBlurSize(Utils.pxFromDp(mActivity, EXCLUDE_BLURRED_SIZE));
        mCircleSelectiveBlurFilter.setCenterPoint(new float[]{circle[0], circle[1]});
        mCircleSelectiveBlurFilter.setRadius(circle[2]);

        float[] linear = mFocusImageView.getLinearFocusInfos(mActivity.getPhotoViewWidth(),
                mActivity.getPhotoViewHeight());
        mLinearSelectiveBlurFilter = new LinearSelectiveBlurFilter();
        mLinearSelectiveBlurFilter.setRecycleBitmap(false);
        mLinearSelectiveBlurFilter.setExclude(Utils.pxFromDp(mActivity, EXCLUDE_BLURRED_SIZE));
        mLinearSelectiveBlurFilter.setLine(new float[]{linear[0], linear[1], linear[2]});
        mLinearSelectiveBlurFilter.setRadius(linear[3]);
    }

    public void circleFocusButtonClick() {
        changeSelectedFocusView(false);
        mFocusImageView.setFocusType(FocusImageView.CIRCLE_FOCUS);
        mFocusImageView.setDisplayFocus(true);
        if(mCircleSelectiveBlurFilter != null) {
            if (!mCircleSelectiveBlurFilter.isInitialized()) {
                mCircleSelectiveBlurFilter.init();
            }
            mActivity.applyFilter(mCircleSelectiveBlurFilter);
        }
    }

    public void linearFocusButtonClick() {
        changeSelectedFocusView(true);
        mFocusImageView.setFocusType(FocusImageView.RECTANGLE_FOCUS);
        mFocusImageView.setDisplayFocus(true);
        if(mLinearSelectiveBlurFilter != null) {
            if (!mLinearSelectiveBlurFilter.isInitialized()) {
                mLinearSelectiveBlurFilter.init();
            }
            mActivity.applyFilter(mLinearSelectiveBlurFilter);
        }
    }

    @Override
    public void onCircleFocus(float[] center, float radius) {
        mCircleSelectiveBlurFilter.setCenterPoint(new float[]{center[0], mFocusImageView.getHeight() - center[1]});
        mCircleSelectiveBlurFilter.setRadius(radius);
        mActivity.getImageProcessingView().requestRender();
    }

    @Override
    public void onLinearFocus(float[] coeff, float radius) {
        mLinearSelectiveBlurFilter.setLine(coeff);
        mLinearSelectiveBlurFilter.setRadius(radius);
        mActivity.getImageProcessingView().requestRender();
    }

    @Override
    public void onNoFocus() {

    }

    @Override
    public String getActionName() {
        return "FocusAction";
    }
}
