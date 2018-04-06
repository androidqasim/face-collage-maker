package dauroi.photoeditor.actions;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import dauroi.photoeditor.R;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;

public abstract class MaskAction extends PackageAction {
    private static final String TAG = MaskAction.class.getSimpleName();

    protected LayoutInflater mLayoutInflater;

    protected View mMaskLayout;
    private View mTopExtraView;
    private View mLeftExtraView;
    private View mRightExtraView;
    private View mBottomExtraView;
    protected View mImageMaskView;

    public MaskAction(ImageProcessingActivity activity) {
        super(activity, null);
    }

    public MaskAction(ImageProcessingActivity activity, String packageType) {
        super(activity, packageType);
    }

    @Override
    protected void onInit() {
        super.onInit();
        // image crop masks
        mLayoutInflater = LayoutInflater.from(mActivity);
        mMaskLayout = mLayoutInflater.inflate(getMaskLayoutRes(), null);
        mTopExtraView = mMaskLayout.findViewById(R.id.topView);
        mLeftExtraView = mMaskLayout.findViewById(R.id.leftView);
        mRightExtraView = mMaskLayout.findViewById(R.id.rightView);
        mBottomExtraView = mMaskLayout.findViewById(R.id.bottomView);
        mImageMaskView = mMaskLayout.findViewById(R.id.maskView);
    }

    protected void adjustImageMaskLayout() {
        adjustImageMaskLayout(mActivity.getImageWidth(), mActivity.getImageHeight());
    }

    protected void adjustImageMaskLayout(int maskImageWidth, int maskImageHeight) {
        final int[] size = mActivity.calculateThumbnailSize(maskImageWidth, maskImageHeight);
        final int dx = (mActivity.getPhotoViewWidth() - size[0]) / 2;
        final int dy = (mActivity.getPhotoViewHeight() - size[1]) / 2;
        if (dx <= 0) {
            mLeftExtraView.setVisibility(View.GONE);
            mRightExtraView.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dx,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            mLeftExtraView.setLayoutParams(params);
            mRightExtraView.setLayoutParams(params);
            mLeftExtraView.setVisibility(View.VISIBLE);
            mRightExtraView.setVisibility(View.VISIBLE);
        }

        if (dy <= 0) {
            mTopExtraView.setVisibility(View.GONE);
            mBottomExtraView.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    dy);
            mTopExtraView.setLayoutParams(params);
            mBottomExtraView.setLayoutParams(params);
            mTopExtraView.setVisibility(View.VISIBLE);
            mBottomExtraView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onActivityResume() {
        super.onActivityResume();
        ALog.d(TAG, "onActivityResume");
        if (isAttached()) {
            ALog.d(TAG, "mActivity.attachMaskView");
            mActivity.attachMaskView(mMaskLayout);
        }
    }

    abstract protected int getMaskLayoutRes();
}
