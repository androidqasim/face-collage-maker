package dauroi.photoeditor.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import dauroi.photoeditor.R;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.view.OrientationImageView;

public class RotationAction extends BaseAction {
    private static final String ROTATION_ACTION_PREF_NAME = "rotationActionPref";
    private static final String SHOW_GUIDE_NAME = "showGuide";

    private View mRotateLeftView;
    private View mRotateRightView;
    private View mFlipVerView;
    private View mFlipHorView;
    private View mOrientationLayout;
    private OrientationImageView mOrientationImageView;
    private boolean mRestoreOldState = false;
    private boolean mFirstAttached = false;
    private boolean mFirstApplied = false;
    //show guide
    private SharedPreferences mRotationActionPref;

    public RotationAction(ImageProcessingActivity activity) {
        super(activity);
        mRotationActionPref = activity.getSharedPreferences(ROTATION_ACTION_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        super.saveInstanceState(bundle);
        bundle.putBoolean("dauroi.photoeditor.actions.RotationAction.mFirstAttached", mFirstAttached);
        mOrientationImageView.saveInstanceState(bundle);
    }

    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
        mFirstAttached = bundle.getBoolean("dauroi.photoeditor.actions.RotationAction.mFirstAttached", mFirstAttached);
        if (mFirstAttached) {
            mOrientationImageView.restoreInstanceState(bundle);
            mRestoreOldState = true;
        } else {
            mRestoreOldState = false;
        }
    }

    public void reset() {
        mRestoreOldState = false;
        mFirstAttached = false;
    }

    @Override
    public void apply(final boolean finish) {
        if (!isAttached()) {
            return;
        }
        ApplyFilterTask task = new ApplyFilterTask(mActivity, new ApplyFilterListener() {

            @Override
            public void onFinishFiltering() {
                reset();
                if (mOrientationImageView.getAngle() % 180 != 0) {
                    if (mActivity.getCropAction() != null) {
                        mActivity.getCropAction().reset();
                    }
                }
                //TODO: fix bug: black screen on Android 5. It seems stupid.
                if(!mFirstApplied && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                    Bitmap bm = Bitmap.createBitmap(mActivity.getImageWidth(), mActivity.getImageHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bm);
                    canvas.drawBitmap(mActivity.getImage(), 0, 0, new Paint());
                    mActivity.setImage(bm, true);
                    mFirstApplied = true;
                }

                if (finish) {
                    done();
                }
            }

            @Override
            public Bitmap applyFilter() {
                return mOrientationImageView.applyTransform();
            }
        });

        task.execute();
    }

    @Override
    public View inflateMenuView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        mRootActionView = inflater.inflate(R.layout.photo_editor_action_rotation, null);
        mRotateLeftView = mRootActionView.findViewById(R.id.leftView);
        mRotateLeftView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rotateLeft();
                onClicked();
            }
        });

        mRotateRightView = mRootActionView.findViewById(R.id.rightView);
        mRotateRightView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rotateRight();
                onClicked();
            }
        });

        mFlipHorView = mRootActionView.findViewById(R.id.horView);
        mFlipHorView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flipHor();
                onClicked();
            }
        });

        mFlipVerView = mRootActionView.findViewById(R.id.verView);
        mFlipVerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flipVer();
                onClicked();
            }
        });
        // orientation view
        mOrientationLayout = inflater.inflate(R.layout.photo_editor_orientation, null);
        mOrientationImageView = (OrientationImageView) mOrientationLayout.findViewById(R.id.orientationView);

        return mRootActionView;
    }

    /**
     * Called after restoring instance state
     */
    @Override
    public void attach() {
        super.attach();
        mActivity.attachMaskView(mOrientationLayout);
        if (mRestoreOldState || mFirstAttached) {
            //if (mRestoreOldState) {
            mOrientationImageView.setImage(mActivity.getImage());
            // }
            // mOrientationImageView.invalidate();
            mRestoreOldState = false;
        } else {
            mOrientationImageView.init(mActivity.getImage(), mActivity.getPhotoViewWidth(), mActivity.getPhotoViewHeight());
        }

        mOrientationImageView.post(new Runnable() {

            @Override
            public void run() {
                mActivity.getImageProcessingView().setVisibility(View.GONE);
            }
        });

        mFirstAttached = true;
        //check show guide
        boolean showGuide = mRotationActionPref.getBoolean(SHOW_GUIDE_NAME, true);
        if (showGuide) {
            mActivity.setGuideTexts(null, mActivity.getString(R.string.photo_editor_guide_rotate_image));
            mActivity.showGuideLayout(true, false, true);
            mActivity.setGuideLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRotationActionPref.edit().putBoolean(SHOW_GUIDE_NAME, false).commit();
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
        if (isAttached() && mFirstAttached) {
            mActivity.attachMaskView(mOrientationLayout);
            mOrientationImageView.invalidate();
            mOrientationImageView.post(new Runnable() {

                @Override
                public void run() {
                    mActivity.getImageProcessingView().setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.getImageProcessingView().setVisibility(View.VISIBLE);
    }

    public void rotateLeft() {
        mOrientationImageView.rotate(-90);
    }

    public void rotateRight() {
        mOrientationImageView.rotate(90);
    }

    public void flipVer() {
        mOrientationImageView.flip(OrientationImageView.FLIP_VERTICAL);
    }

    public void flipHor() {
        mOrientationImageView.flip(OrientationImageView.FLIP_HORIZONTAL);
    }

    @Override
    public String getActionName() {
        return "RotationAction";
    }
}
