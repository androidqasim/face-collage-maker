package dauroi.photoeditor.view;

import dauroi.photoeditor.utils.PhotoUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OrientationImageView extends View {
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    private float mThumbX;
    private float mOldX;
    private float mMaxAngle = 0;

    private Paint mPaint = new Paint();
    private Matrix mMatrix = new Matrix();
    private Matrix mAppliedMatrix = new Matrix();
    private Bitmap mImage;
    private float mAngle = 0;

    public OrientationImageView(Context context) {
        super(context);
        init();
    }

    public OrientationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrientationImageView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putFloat("dauroi.photoeditor.actions.RotationAction.mAngle", mAngle);
        bundle.putFloat("dauroi.photoeditor.actions.RotationAction.mThumbX", mThumbX);
        bundle.putFloat("dauroi.photoeditor.actions.RotationAction.mOldX", mOldX);
        bundle.putFloat("dauroi.photoeditor.actions.RotationAction.mMaxAngle", mMaxAngle);

        float[] values = new float[9];
        mMatrix.getValues(values);
        bundle.putFloatArray("dauroi.photoeditor.actions.RotationAction.mMatrix", values);

        values = new float[9];
        mAppliedMatrix.getValues(values);
        bundle.putFloatArray("dauroi.photoeditor.actions.RotationAction.mAppliedMatrix", values);
    }

    public void restoreInstanceState(Bundle bundle) {
        mAngle = bundle.getFloat("dauroi.photoeditor.actions.RotationAction.mAngle", mAngle);
        mThumbX = bundle.getFloat("dauroi.photoeditor.actions.RotationAction.mThumbX", mThumbX);
        mOldX = bundle.getFloat("dauroi.photoeditor.actions.RotationAction.mOldX", mOldX);
        mMaxAngle = bundle.getFloat("dauroi.photoeditor.actions.RotationAction.mMaxAngle", mMaxAngle);

        float[] values = bundle.getFloatArray("dauroi.photoeditor.actions.RotationAction.mMatrix");
        if(values != null){
            if(mMatrix == null){
                mMatrix = new Matrix();
            }
            mMatrix.setValues(values);
        }

        values = bundle.getFloatArray("dauroi.photoeditor.actions.RotationAction.mAppliedMatrix");
        if(values != null){
            if(mAppliedMatrix == null){
                mAppliedMatrix = new Matrix();
            }
            mAppliedMatrix.setValues(values);
        }
    }

    public float getAngle() {
        return mAngle;
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    public void setImage(Bitmap image) {
        mImage = image;
        invalidate();
    }

    public Bitmap applyTransform() {
        return Bitmap.createBitmap(mImage, 0, 0, mImage.getWidth(),
                mImage.getHeight(), mAppliedMatrix, true);
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void init(Bitmap image, int viewWidth, int viewHeight) {
        mMatrix.reset();
        mAppliedMatrix.reset();
        mAngle = 0;
        mImage = image;
        mThumbX = viewWidth / 2;
        mMaxAngle = (float) (2 * Math.toDegrees(Math.atan(viewWidth
                / (double) viewHeight)));

        float ratio = PhotoUtils.calculateScaleRatio(mImage.getWidth(),
                mImage.getHeight(), viewWidth, viewHeight);
        float dx = (viewWidth - mImage.getWidth()) / 2.0f;
        float dy = (viewHeight - mImage.getHeight()) / 2.0f;
        mMatrix.postTranslate(dx, dy);
        mMatrix.postScale(1 / ratio, 1 / ratio, viewWidth / 2, viewHeight / 2);
        invalidate();
    }

    public void flip(int type) {
        if (type == FLIP_VERTICAL) {
            // y = y * -1
            mMatrix.postScale(1.0f, -1.0f, getWidth() / 2, getHeight() / 2);
            mAppliedMatrix.postScale(1.0f, -1.0f, mImage.getWidth() / 2,
                    mImage.getHeight() / 2);
        }
        // if horizonal
        else if (type == FLIP_HORIZONTAL) {
            // x = x * -1
            mMatrix.postScale(-1.0f, 1.0f, getWidth() / 2, getHeight() / 2);
            mAppliedMatrix.postScale(-1.0f, 1.0f, mImage.getWidth() / 2,
                    mImage.getHeight() / 2);
        }

        invalidate();
    }

    public void rotate(float degreeAngle) {
        mMatrix.postRotate(degreeAngle, getWidth() / 2, getHeight() / 2);
        mAppliedMatrix.postRotate(degreeAngle, mImage.getWidth() / 2,
                mImage.getHeight() / 2);
        mAngle += degreeAngle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mImage != null && !mImage.isRecycled())
            canvas.drawBitmap(mImage, mMatrix, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mOldX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mOldX;
                float x = Math.max(Math.min(mThumbX + dx, getWidth()), 0);
                dx = x - mThumbX;
                mThumbX = x;
                if (dx != 0) {
                    float angle = mMaxAngle * dx / getWidth();
                    rotate(-angle);
                }

                mOldX = event.getX();
                break;
            default:
                break;
        }
        return true;
    }
}
