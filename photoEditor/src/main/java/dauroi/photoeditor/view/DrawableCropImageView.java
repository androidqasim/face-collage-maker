package dauroi.photoeditor.view;

import java.util.ArrayList;

import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.utils.PhotoUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * @author HungNguyen
 * 
 */
public class DrawableCropImageView extends ImageView {
	private static final String TAG = DrawableCropImageView.class.getSimpleName();

	public static interface OnDrawMaskListener {
		public void onStartDrawing();

		public void onFinishDrawing();
	}

	/**
	 * Radius of camera
	 */
	private int mOffsetRadius = 10;
	private int mCircleRadius = 0;
	private int mCameraY = 0;
	private int mCameraX = 0;

	private OnDrawMaskListener mOnDrawMaskListener;
	private Bitmap mCameraBitmap;

	private ArrayList<PointF> mPointList = new ArrayList<PointF>();
	private ArrayList<PointF> mCameraPointList = new ArrayList<PointF>();

	private boolean mFingerDrawingMode = true;
	private boolean mEndDrawing = false;
	private Paint mPaint = new Paint();
	private Path mPath = new Path();
	private Bitmap mBitmap;

	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mFingerDrawingMode) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mEndDrawing = false;
					mPointList.clear();
					mPointList.add(new PointF(event.getX(), event.getY()));
					// create camera
					if (mCameraBitmap != null) {
						mCameraBitmap.recycle();
					}
					mCameraBitmap = createCircleCameraBitmap((int) event.getX(), (int) event.getY(), mCircleRadius);
					invalidate();

					if (mOnDrawMaskListener != null) {
						mOnDrawMaskListener.onStartDrawing();
					}
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					mPointList.add(new PointF(event.getX(), event.getY()));
					// create camera
					if (mCameraBitmap != null) {
						mCameraBitmap.recycle();
					}
					mCameraBitmap = createCircleCameraBitmap((int) event.getX(), (int) event.getY(), mCircleRadius);

					invalidate();
				} else {
					// Log.d("Action", "Up");
					// create camera
					if (mCameraBitmap != null) {
						mCameraBitmap.recycle();
						mCameraBitmap = null;
					}

					mEndDrawing = true;
					invalidate();
					if (mOnDrawMaskListener != null) {
						mOnDrawMaskListener.onFinishDrawing();
					}
				}

				return true;
			}

			return false;
		}
	};

	public DrawableCropImageView(Context context) {
		super(context);
		init();
	}

	public DrawableCropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawableCropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void saveInstanceState(Bundle bundle) {
		bundle.putInt("dauroi.photoeditor.view.DrawableCropImageView.mOffsetRadius", mOffsetRadius);
		bundle.putInt("dauroi.photoeditor.view.DrawableCropImageView.mCircleRadius", mCircleRadius);
		bundle.putInt("dauroi.photoeditor.view.DrawableCropImageView.mCameraY", mCameraY);
		bundle.putInt("dauroi.photoeditor.view.DrawableCropImageView.mCameraX", mCameraX);
		bundle.putParcelableArrayList("dauroi.photoeditor.view.DrawableCropImageView.mPointList", mPointList);
		bundle.putParcelableArrayList("dauroi.photoeditor.view.DrawableCropImageView.mCameraPointList",
				mCameraPointList);
		bundle.putBoolean("dauroi.photoeditor.view.DrawableCropImageView.mFingerDrawingMode", mFingerDrawingMode);
		bundle.putBoolean("dauroi.photoeditor.view.DrawableCropImageView.mEndDrawing", mEndDrawing);
	}

	public void restoreInstanceState(Bundle bundle) {
		mOffsetRadius = bundle.getInt("dauroi.photoeditor.view.DrawableCropImageView.mOffsetRadius", mOffsetRadius);
		mCircleRadius = bundle.getInt("dauroi.photoeditor.view.DrawableCropImageView.mCircleRadius", mCircleRadius);
		mCameraY = bundle.getInt("dauroi.photoeditor.view.DrawableCropImageView.mCameraY", mCameraY);
		mCameraX = bundle.getInt("dauroi.photoeditor.view.DrawableCropImageView.mCameraX", mCameraX);
		ArrayList<PointF> pointList = bundle
				.getParcelableArrayList("dauroi.photoeditor.view.DrawableCropImageView.mPointList");
		if (pointList != null) {
			mPointList = pointList;
		}

		ArrayList<PointF> cameraPointList = bundle
				.getParcelableArrayList("dauroi.photoeditor.view.DrawableCropImageView.mCameraPointList");
		if (cameraPointList != null) {
			mCameraPointList = cameraPointList;
		}

		mFingerDrawingMode = bundle.getBoolean("dauroi.photoeditor.view.DrawableCropImageView.mFingerDrawingMode",
				mFingerDrawingMode);
		mEndDrawing = bundle.getBoolean("dauroi.photoeditor.view.DrawableCropImageView.mEndDrawing", mEndDrawing);
		setFingerDrawingMode(true);
	}

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	private void init() {
		setOnTouchListener(mTouchListener);
		mOffsetRadius = (int) pxFromDp(50);
		mCircleRadius = (int) pxFromDp(60);
	}

	public void setOnDrawMaskListener(OnDrawMaskListener onDrawMaskListener) {
		mOnDrawMaskListener = onDrawMaskListener;
	}

	public void setImagePainterTouchListener() {
		setOnTouchListener(mTouchListener);
	}

	/**
	 * set mTopCamera and mLeftCamera <br>
	 * such that camera position is in top left corner if touch position is in
	 * right<br>
	 * and it is in top right corner if touch position is in left.
	 */
	private void setCameraPosition(float touchX, float touchY) {
		if (touchY < 2 * mCircleRadius) {
			mCameraY = (int) pxFromDp(3);
			if (touchX > getWidth() / 2) {
				mCameraX = (int) pxFromDp(3);
			} else {
				mCameraX = (int) (getWidth() - 2 * mCircleRadius - pxFromDp(3));
			}
		} else {
			mCameraY = (int) (touchY - 2 * mCircleRadius - mOffsetRadius);
			mCameraX = (int) (touchX - mCircleRadius);
		}
	}

	public void setFingerDrawingMode(boolean fingerDrawing) {
		this.mFingerDrawingMode = fingerDrawing;
		invalidate();
	}

	public boolean isFingerDrawingMode() {
		return this.mFingerDrawingMode;
	}

	/**
	 * Clear all points which are drawn
	 */
	public void clear() {
		mPointList.clear();
		mEndDrawing = false;
		mFingerDrawingMode = true;
		invalidate();
	}

	/**
	 * if recyle = true then reduce used memory but if you use this object again
	 * then you must set other image or set image = null.
	 * 
	 * @return cropped image
	 */
	public Bitmap cropImage(boolean recycle) {
		if (mPointList.isEmpty()) {
			return null;
		}

		Bitmap mask = createBitmapMask(mPointList);
		if (mask != null) {
			Bitmap bmp = cropImage(mask, recycle);
			Bitmap result = PhotoUtils.cleanImage(bmp);
			if (bmp != result && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}

			return result;
		} else {
			return null;
		}
	}

	/**
	 * Crop image from given mask. After crop image, the mask image will be
	 * recycled.<br>
	 * So don't use again the mask image.
	 * 
	 * @param mask
	 * @return cropped image
	 */
	private Bitmap cropImage(Bitmap mask, boolean recycleOriginalImage) {
		if (mBitmap == null) {
			return null;
		}
		// create normal mask image and normal image
		final float ratio = ((float) getWidth()) / getHeight();
		Bitmap normalImage = PhotoUtils.transparentPadding(mBitmap, ratio);
		if (normalImage != mBitmap && recycleOriginalImage) {
			mBitmap.recycle();
			mBitmap = null;
		}

		Bitmap normalMask = PhotoUtils.transparentPadding(mask, ratio);
		if (normalMask != mask) {
			mask.recycle();
			mask = null;
		}

		mask = Bitmap.createScaledBitmap(normalMask, normalImage.getWidth(), normalImage.getHeight(), true);
		if (mask != normalMask) {
			normalMask.recycle();
			normalMask = null;
		}

		return cropImage(normalImage, mask, true);
	}

	/**
	 * 
	 * @param pointList
	 * @return a mask
	 */
	private Bitmap createBitmapMask(final ArrayList<PointF> pPointList) {
		if (pPointList == null || pPointList.isEmpty()) {
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.BLACK);

		Path path = new Path();
		for (int i = 0; i < pPointList.size(); i++) {
			if (i == 0) {
				path.moveTo(pPointList.get(i).x, pPointList.get(i).y);
			} else {
				path.lineTo(pPointList.get(i).x, pPointList.get(i).y);
			}
		}

		canvas.drawPath(path, paint);
		canvas.clipPath(path);

		return bitmap;

	}

	private Bitmap cropImage(Bitmap mainImage, Bitmap mask, boolean recycle) {
		Canvas canvas = new Canvas();
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);

		canvas.setBitmap(result);
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);

		canvas.drawBitmap(mainImage, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);
		if (recycle) {
			mainImage.recycle();
			mainImage = null;
			mask.recycle();
			mask = null;
		}

		return result;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @return circle camera
	 */
	protected Bitmap createCircleCameraBitmap(int x, int y, int radius) {
		setCameraPosition(x, y);
		if (radius <= 0 || getWidth() < 10) {
			return null;
		}
		// ////////////////////////////
		final float ratio = calculateScaleRatio(mBitmap.getWidth(), mBitmap.getHeight());
		final int[] thumbnailSize = calculateThumbnailSize(mBitmap.getWidth(), mBitmap.getHeight());
		final float dx = (getWidth() - thumbnailSize[0]) / 2.0f;
		final float dy = (getHeight() - thumbnailSize[1]) / 2.0f;
		ALog.d(TAG, "createCircleCameraBitmap, dx=" + dx + ", dy=" + dy);
		int left = (int) ((x - dx - radius) * ratio);
		int right = (int) ((x - dx + radius) * ratio);
		int top = (int) ((y - dy - radius) * ratio);
		int bottom = (int) ((y - dy + radius) * ratio);

		Rect srcRect = new Rect(left, top, right, bottom);

		Bitmap result = Bitmap.createBitmap((int) (2 * radius * ratio), (int) (2 * radius * ratio),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		canvas.drawCircle(radius * ratio, radius * ratio, radius * ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(mBitmap, srcRect, new Rect(0, 0, result.getWidth(), result.getHeight()), paint);
		// find points in camera
		mCameraPointList.clear();
		for (PointF p : mPointList)
			if (insideCircle(p.x, p.y, x, y, radius)) {
				PointF cp = new PointF(p.x - x + mCameraX + radius, p.y - y + mCameraY + radius);
				mCameraPointList.add(cp);
			} else {
				if (!mCameraPointList.isEmpty()) {
					mCameraPointList.add(new PointF(-1, -1));
				}
			}
		// scale result
		Bitmap bm = Bitmap.createScaledBitmap(result, 2 * radius, 2 * radius, true);
		if (result != bm) {
			result.recycle();
			result = null;
		}

		return bm;
	}

	public float calculateScaleRatio(int imageWidth, int imageHeight) {
		float ratioWidth = ((float) imageWidth) / getWidth();
		float ratioHeight = ((float) imageHeight) / getHeight();
		return Math.max(ratioWidth, ratioHeight);
	}

	public int[] calculateThumbnailSize(int imageWidth, int imageHeight) {
		int[] size = new int[2];
		float ratioWidth = ((float) imageWidth) / getWidth();
		float ratioHeight = ((float) imageHeight) / getHeight();
		float ratio = Math.max(ratioWidth, ratioHeight);
		if (ratio == ratioWidth) {
			size[0] = getWidth();
			size[1] = (int) (imageHeight / ratio);
		} else {
			size[0] = (int) (imageWidth / ratio);
			size[1] = getHeight();
		}

		return size;
	}

	/**
	 * 
	 * @param px
	 * @param py
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @return true if point (px, py) is inside circle
	 */
	private boolean insideCircle(float px, float py, float centerX, float centerY, float radius) {
		double d = (px - centerX) * (px - centerX) + (py - centerY) * (py - centerY);
		return (d < radius * radius);
	}

	/**
	 * Create a bounds with center at (x,y) point
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @return bitmap got from image view. It has width = height = 2 * radius.
	 */
	protected Bitmap createRectangleCameraBitmap(int x, int y, int radius) {
		int left = x - radius;
		int right = x + radius;
		int top = y - radius;
		int bottom = y + radius;

		Rect srcRect = new Rect(left, top, right, bottom);

		Bitmap result = Bitmap.createBitmap(2 * radius, 2 * radius, Bitmap.Config.ARGB_8888);
		BitmapShader shader = new BitmapShader(mBitmap, TileMode.CLAMP, TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);

		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);

		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(mBitmap, srcRect, new RectF(0, 0, result.getWidth(), result.getHeight()), paint);

		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mPointList.isEmpty()) {
			return;
		}

		if (mFingerDrawingMode) {
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);

			Path path = new Path();
			for (int i = 0; i < mPointList.size(); i++) {
				if (i == 0) {
					path.moveTo(mPointList.get(i).x, mPointList.get(i).y);
				} else {
					path.lineTo(mPointList.get(i).x, mPointList.get(i).y);
				}
			}

			if (mEndDrawing) {
				path.lineTo(mPointList.get(0).x, mPointList.get(0).y);
				mFingerDrawingMode = false;
			}

			if (mCameraBitmap != null) {
				canvas.drawBitmap(mCameraBitmap, mCameraX, mCameraY, mPaint);
				paint.setColor(Color.BLACK);
				paint.setAntiAlias(true);
				canvas.drawCircle(mCameraX + mCircleRadius, mCameraY + mCircleRadius, mCircleRadius, paint);
				// draw path in camera

				mPath.reset();
				boolean newpath = true;
				for (int i = 0; i < mCameraPointList.size(); i++) {
					if (mCameraPointList.get(i).x > -1 && mCameraPointList.get(i).y > -1 && newpath) {
						mPath.moveTo(mCameraPointList.get(i).x, mCameraPointList.get(i).y);
						newpath = false;
					} else {
						if (mCameraPointList.get(i).x > -1 && mCameraPointList.get(i).y > -1) {
							mPath.lineTo(mCameraPointList.get(i).x, mCameraPointList.get(i).y);
						} else {
							paint.setColor(Color.RED);
							canvas.drawPath(mPath, paint);
							mPath.reset();
							newpath = true;
						}
					}
				}

				if (mEndDrawing) {
					mPath.lineTo(mCameraPointList.get(0).x, mCameraPointList.get(0).y);
					mFingerDrawingMode = false;
				}

				paint.setColor(Color.RED);
				canvas.drawPath(mPath, paint);
			}

			paint.setColor(Color.RED);
			canvas.drawPath(path, paint);
		}
	}

	public float dpFromPx(float px) {
		return px / this.getContext().getResources().getDisplayMetrics().density;
	}

	public float pxFromDp(float dp) {
		return dp * this.getContext().getResources().getDisplayMetrics().density;
	}
}
