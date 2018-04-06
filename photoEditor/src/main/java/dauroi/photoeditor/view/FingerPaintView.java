package dauroi.photoeditor.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;

public class FingerPaintView extends View {

	public static final int DRAW_EFFECT_NORMAL = 0;
	public static final int DRAW_EFFECT_BLUR = 1;
	public static final int DRAW_EFFECT_EMBOSS = 2;
	public static final int DRAW_EFFECT_SRC_A_TOP = 3;
	public static final int DRAW_EFFECT_ERASE = 4;

	private static final float TOUCH_TOLERANCE = 4;

	private float mX, mY;

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	private List<FingerPath> mFingerPathList = new ArrayList<FingerPath>();
	private Path mTempPath;

	private float mPaintSize = 10;
	private int mColor = 0xFFFF0000;
	private int mEffect = DRAW_EFFECT_NORMAL;

	public FingerPaintView(Context c) {
		super(c);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FingerPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void saveInstanceState(Bundle bundle) {
		bundle.putFloat("dauroi.photoeditor.view.FingerPaintView.mX", mX);
		bundle.putFloat("dauroi.photoeditor.view.FingerPaintView.mY", mY);
		bundle.putParcelableArrayList("dauroi.photoeditor.view.FingerPaintView.mFingerPathList",
				(ArrayList<? extends Parcelable>) mFingerPathList);
		bundle.putFloat("dauroi.photoeditor.view.FingerPaintView.mPaintSize", mPaintSize);
		bundle.putInt("dauroi.photoeditor.view.FingerPaintView.mColor", mColor);
		bundle.putInt("dauroi.photoeditor.view.FingerPaintView.mEffect", mEffect);
	}

	public void restoreInstanceState(Bundle bundle) {
		mX = bundle.getFloat("dauroi.photoeditor.view.FingerPaintView.mX", mX);
		mY = bundle.getFloat("dauroi.photoeditor.view.FingerPaintView.mY", mY);
		ArrayList<FingerPath> paths = bundle
				.getParcelableArrayList("dauroi.photoeditor.view.FingerPaintView.mFingerPathList");
		if (paths != null) {
			mFingerPathList = paths;
		}

		mPaintSize = bundle.getFloat("dauroi.photoeditor.view.FingerPaintView.mPaintSize", mPaintSize);
		mColor = bundle.getInt("dauroi.photoeditor.view.FingerPaintView.mColor", mColor);
		mEffect = bundle.getInt("dauroi.photoeditor.view.FingerPaintView.mEffect", mEffect);
	}

	private void init() {
		mTempPath = new Path();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(mColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(mPaintSize);

		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}

	public void setPaintColor(int color) {
		mColor = color;
		mPaint.setColor(color);
	}

	public void setPaintSize(float paintSize) {
		mPaintSize = paintSize;
		mPaint.setStrokeWidth(mPaintSize);
	}

	public int getEffect() {
		return mEffect;
	}

	public void clear() {
		mFingerPathList.clear();
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (FingerPath path : mFingerPathList) {
			mTempPath.reset();
			mPaint.setColor(path.color);
			mPaint.setStrokeWidth(path.size);
			setPaintEffect(path.effect);
			int count = path.pointList.size();
			if (count > 0) {
				mTempPath.moveTo(path.pointList.get(0).x, path.pointList.get(0).y);
			}

			for (int idx = 1; idx < count; idx++) {
				mTempPath.lineTo(path.pointList.get(idx).x, path.pointList.get(idx).y);
			}

			canvas.drawPath(mTempPath, mPaint);
		}

	}

	public Bitmap drawImage(Bitmap bitmap, float ratio) {
		Bitmap temp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(temp);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);

		for (FingerPath path : mFingerPathList) {
			mTempPath.reset();
			mPaint.setColor(path.color);
			mPaint.setStrokeWidth(path.size * ratio);
			setPaintEffect(path.effect);
			int count = path.pointList.size();
			if (count > 0) {
				mTempPath.moveTo(path.pointList.get(0).x * ratio, path.pointList.get(0).y * ratio);
			}

			for (int idx = 1; idx < count; idx++) {
				mTempPath.lineTo(path.pointList.get(idx).x * ratio, path.pointList.get(idx).y * ratio);
			}

			canvas.drawPath(mTempPath, mPaint);
		}

		Bitmap bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		canvas.setBitmap(bm);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.drawBitmap(temp, 0, 0, paint);

		temp.recycle();
		temp = null;

		return bm;
	}

	private void touchStart(float x, float y) {
		FingerPath path = new FingerPath();
		path.color = mColor;
		path.size = mPaintSize;
		path.effect = mEffect;
		path.pointList.add(new PointF(x, y));
		mFingerPathList.add(path);

		mX = x;
		mY = y;
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			if (mFingerPathList.size() > 0) {
				FingerPath path = mFingerPathList.get(mFingerPathList.size() - 1);
				path.pointList.add(new PointF((x + mX) / 2, (y + mY) / 2));
			}

			mX = x;
			mY = y;
		}

	}

	private void touchUp() {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchStart(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touchUp();
			invalidate();
			break;
		}
		return true;
	}

	public void setPaintEffect(int effect) {
		mEffect = effect;

		mPaint.setMaskFilter(null);
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);
		switch (effect) {
		case DRAW_EFFECT_EMBOSS:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			break;
		case DRAW_EFFECT_BLUR:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			break;
		case DRAW_EFFECT_ERASE:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			break;
		case DRAW_EFFECT_SRC_A_TOP:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			break;
		}

	}

	static class FingerPath implements Parcelable {
		List<PointF> pointList = new ArrayList<PointF>();
		int color;
		float size;
		int effect;

		public static final Parcelable.Creator<FingerPath> CREATOR = new Parcelable.Creator<FingerPath>() {
			public FingerPath createFromParcel(Parcel in) {
				return new FingerPath(in);
			}

			public FingerPath[] newArray(int size) {
				return new FingerPath[size];
			}
		};

		private FingerPath() {

		}

		private FingerPath(Parcel in) {
			Parcelable[] points = in.readParcelableArray(PointF.class.getClassLoader());
			for (int idx = 0; idx < points.length; idx++) {
				pointList.add((PointF) points[idx]);
			}
			color = in.readInt();
			size = in.readFloat();
			effect = in.readInt();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			PointF[] points = new PointF[pointList.size()];
			for (int idx = 0; idx < pointList.size(); idx++) {
				points[idx] = pointList.get(idx);
			}

			dest.writeParcelableArray(points, flags);
			dest.writeInt(color);
			dest.writeFloat(size);
			dest.writeInt(effect);
		}
	}
}
