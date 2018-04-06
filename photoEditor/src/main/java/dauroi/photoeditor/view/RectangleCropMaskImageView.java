package dauroi.photoeditor.view;

import dauroi.photoeditor.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class RectangleCropMaskImageView extends ImageView {
	public static final float ORIGINAL_SIZE = -1;
	public static final float CUSTOM_SIZE = -2;
	public static final int MOVE_UP = 0;
	public static final int MOVE_DOWN = 1;
	public static final int MOVE_LEFT = 2;
	public static final int MOVE_RIGHT = 3;
	private static final int MIN_TOUCH_DIST_DP = 15;
	private float mFingerWidth = 15;

	private static final int OUTSIDE_RECT = -1;
	private static final int LEFT_TOP_CIRCLE = 0;
	private static final int RIGHT_TOP_CIRCLE = 1;
	private static final int LEFT_BOTTOM_CIRCLE = 2;
	private static final int RIGHT_BOTTOM_CIRCLE = 3;
	private static final int LEFT_LINE = 4;
	private static final int TOP_LINE = 5;
	private static final int RIGHT_LINE = 6;
	private static final int BOTTOM_LINE = 7;
	private static final int INSIDE_RECT = 8;

	private Bitmap mCircleBitmap;
	// cropped area
	private float mTop;
	private float mLeft;
	private float mBottom;
	private float mRight;
	private int mMaskColor;
	private Paint mPaint;
	private int mCurrentTouchedArea = OUTSIDE_RECT;
	private float radius = 0;
	private boolean mPaintMode = false;
	private PointF mCurrentTouchedPoint = new PointF();
	// ratio between width and height of clip area.ratio = width / height
	private float mRatio = -1;
	private float mScaleRatio = 1.0f;
	// swipe mode
	private int mMinTouchDist = MIN_TOUCH_DIST_DP;
	private IChangeDirection mChangeDirection;
	private boolean mSlideMode = false;

	public RectangleCropMaskImageView(Context context) {
		super(context);
		initClipArea();
	}

	public RectangleCropMaskImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClipArea();
	}

	public RectangleCropMaskImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initClipArea();
	}

	public void setPaintMode(boolean paintMode) {
		mPaintMode = paintMode;
		invalidate();
	}

	public void setScaleRatio(float scaleRatio) {
		mScaleRatio = scaleRatio;
	}

	public float getScaleRatio() {
		return mScaleRatio;
	}

	/**
	 * 
	 * @param ratio
	 *            = (width / height) of clip area
	 */
	public void setRatio(float ratio) {
		mRatio = ratio;
		invalidate();
	}

	public void setSlideMode(boolean slideMode) {
		mSlideMode = slideMode;
	}

	public boolean isSlideMode() {
		return mSlideMode;
	}

	/**
	 * ratio = width / height
	 * 
	 * @return ratio between width and height of clip area
	 */
	public float getRatio() {
		return mRatio;
	}

	public void setCropArea(RectF rect) {
		mTop = rect.top;
		mBottom = rect.bottom;
		mLeft = rect.left;
		mRight = rect.right;
		invalidate();
	}

	public RectF getCropArea() {
		return new RectF(mLeft, mTop, mRight, mBottom);
	}

	public boolean isPaintMode() {
		return mPaintMode;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// paint mode
		if (!mPaintMode) {
			return;
		}
		// draw blur area
		mPaint.setColor(mMaskColor);
		mPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, mLeft, getHeight(), mPaint);
		canvas.drawRect(mRight, 0, getWidth(), getHeight(), mPaint);
		canvas.drawRect(mLeft, 0, mRight, mTop, mPaint);
		canvas.drawRect(mLeft, mBottom, mRight, getHeight(), mPaint);
		// draw border
		mPaint.setStrokeWidth(3);
		mPaint.setColor(Color.WHITE);
		canvas.drawLine(mLeft, mTop, mRight, mTop, mPaint);
		canvas.drawLine(mLeft, mTop, mLeft, mBottom, mPaint);
		canvas.drawLine(mRight, mTop, mRight, mBottom, mPaint);
		canvas.drawLine(mLeft, mBottom, mRight, mBottom, mPaint);
		// draw corner
		radius = mCircleBitmap.getWidth() / 2.0f;
		canvas.drawBitmap(mCircleBitmap, mLeft - radius, mTop - radius, null);
		canvas.drawBitmap(mCircleBitmap, mRight - radius, mTop - radius, null);
		canvas.drawBitmap(mCircleBitmap, mLeft - radius, mBottom - radius, null);
		canvas.drawBitmap(mCircleBitmap, mRight - radius, mBottom - radius,
				null);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mPaintMode) {

			if (mSlideMode) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mCurrentTouchedPoint.x = event.getX();
					mCurrentTouchedPoint.y = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					PointF currentTouch = new PointF(event.getX(), event.getY());
					float dist = (currentTouch.x - mCurrentTouchedPoint.x)
							* (currentTouch.x - mCurrentTouchedPoint.x)
							+ (currentTouch.y - mCurrentTouchedPoint.y)
							* (currentTouch.y - mCurrentTouchedPoint.y);
					if (dist >= mMinTouchDist * mMinTouchDist) {
						int direction = findDirectionMovement(
								mCurrentTouchedPoint, currentTouch);
						if (mChangeDirection != null) {
							mChangeDirection.changeDirection(direction);
						}
					} else if (mChangeDirection != null) {
						mChangeDirection.clickAt(event.getX(), event.getY());
					}
					break;
				default:
					break;
				}
			}

			return true;
		}

		final float x = event.getX();
		final float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mCurrentTouchedArea = calculatePosition(x, y);
			mCurrentTouchedPoint.x = x;
			mCurrentTouchedPoint.y = y;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mCurrentTouchedArea = OUTSIDE_RECT;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float deltaY = y - mCurrentTouchedPoint.y;
			float deltaX = x - mCurrentTouchedPoint.x;

			float left = mLeft + deltaX;
			float right = mRight + deltaX;
			float top = mTop + deltaY;
			float bottom = mBottom + deltaY;

			boolean repaint = false;

			switch (mCurrentTouchedArea) {
			// drag clip area
			case INSIDE_RECT:
				if (top >= 0 && bottom <= getHeight() && left >= 0
						&& right <= getWidth()) {
					mTop = top;
					mBottom = bottom;
					mLeft = left;
					mRight = right;
					invalidate();
					repaint = true;
				} else if (top >= 0 && bottom <= getHeight()) {
					mTop = top;
					mBottom = bottom;
					invalidate();
					repaint = true;
				} else if (left >= 0 && right <= getWidth()) {
					mLeft = left;
					mRight = right;
					invalidate();
					repaint = true;
				}

				break;
			// resize clip area
			case LEFT_TOP_CIRCLE:
				if (mRatio > 0) {
					top = mTop + deltaX / mRatio;
				}

				if (mRatio < 0) {
					if (top < mBottom && top >= 0) {
						mTop = top;
						repaint = true;
					}
					if (left < mRight && left >= 0) {
						mLeft = left;
						repaint = true;
					}
					if (repaint)
						invalidate();
				} else {
					if (top < mBottom && top >= 0 && left < mRight && left >= 0) {
						mTop = top;
						mLeft = left;
						invalidate();
					}
				}
				break;
			case RIGHT_TOP_CIRCLE:
				if (mRatio > 0) {
					top = mTop - deltaX / mRatio;
				}

				if (mRatio < 0) {
					if (top < mBottom && top >= 0) {
						mTop = top;
						repaint = true;
					}
					if (right > mLeft && right <= getWidth()) {
						mRight = right;
						repaint = true;
					}
					if (repaint)
						invalidate();
				} else {
					if (top < mBottom && top >= 0 && right > mLeft
							&& right <= getWidth()) {
						mTop = top;
						mRight = right;
						invalidate();
					}
				}

				break;
			case RIGHT_BOTTOM_CIRCLE:
				if (mRatio > 0) {
					bottom = mBottom + deltaX / mRatio;
				}

				if (mRatio < 0) {
					if (bottom > mTop && bottom <= getHeight()) {
						mBottom = bottom;
						repaint = true;
					}
					if (right > mLeft && right <= getWidth()) {
						mRight = right;
						repaint = true;
					}
					if (repaint)
						invalidate();
				} else {
					if (bottom > mTop && bottom <= getHeight() && right > mLeft
							&& right <= getWidth()) {
						mBottom = bottom;
						mRight = right;
						invalidate();
					}
				}

				break;
			case LEFT_BOTTOM_CIRCLE:
				if (mRatio > 0) {
					bottom = mBottom - deltaX / mRatio;
				}

				if (mRatio < 0) {
					if (bottom > mTop && bottom <= getHeight()) {
						mBottom = bottom;
						repaint = true;
					}

					if (left < mRight && left >= 0) {
						mLeft = left;
						repaint = true;
					}
					if (repaint)
						invalidate();
				} else {
					if (bottom > mTop && bottom <= getHeight() && left < mRight
							&& left >= 0) {
						mBottom = bottom;
						mLeft = left;
						invalidate();
					}
				}

				break;
			// border lines
			case TOP_LINE:
				if (mRatio > 0) {
					left = mLeft + deltaY * mRatio / 2;
					right = mRight - deltaY * mRatio / 2;
				}

				if (top < mBottom && top >= 0) {
					if (mRatio > 0 && left >= 0 && left < right
							&& right <= getWidth()) {
						mTop = top;
						mLeft = left;
						mRight = right;
						invalidate();
					} else if (mRatio < 0) {
						mTop = top;
						invalidate();
					}
				}
				break;
			case RIGHT_LINE:
				if (mRatio > 0) {
					top = mTop - 0.5f * deltaX / mRatio;
					bottom = mBottom + 0.5f * deltaX / mRatio;
				}

				if (right > mLeft && right <= getWidth()) {
					if (mRatio > 0 && top >= 0 && top < bottom
							&& bottom <= getHeight()) {
						mRight = right;
						mTop = top;
						mBottom = bottom;
						invalidate();
					} else if (mRatio < 0) {
						mRight = right;
						invalidate();
					}
				}
				break;
			case BOTTOM_LINE:
				if (mRatio > 0) {
					left = mLeft - 0.5f * deltaY * mRatio;
					right = mRight + 0.5f * deltaY * mRatio;
				}

				if (bottom > mTop && bottom <= getHeight()) {
					if (mRatio > 0 && left >= 0 && left < right
							&& right <= getWidth()) {
						mBottom = bottom;
						mLeft = left;
						mRight = right;
						invalidate();
					} else if (mRatio < 0) {
						mBottom = bottom;
						invalidate();
					}
				}
				break;
			case LEFT_LINE:
				if (mRatio > 0) {
					top = mTop + 0.5f * deltaX / mRatio;
					bottom = mBottom - 0.5f * deltaX / mRatio;
				}
				if (left < mRight && left >= 0) {
					if (mRatio > 0 && top >= 0 && top < bottom
							&& bottom <= getHeight()) {
						mLeft = left;
						mTop = top;
						mBottom = bottom;
						invalidate();
					} else if (mRatio < 0) {
						mLeft = left;
						invalidate();
					}
				}
				break;
			default:
				break;
			}

			mCurrentTouchedPoint.x = x;
			mCurrentTouchedPoint.y = y;
		}

		return true;
	}

	public Bitmap cropImage(Bitmap sourceBitmap) {
		RectF cropArea = getCropArea();
		final float ratio = getScaleRatio();

		Rect src = new Rect((int) (ratio * cropArea.left),
				(int) (ratio * cropArea.top), (int) (ratio * cropArea.right),
				(int) (ratio * cropArea.bottom));
		Bitmap bitmap = Bitmap.createBitmap(
				(int) (ratio * cropArea.right - ratio * cropArea.left),
				(int) (ratio * cropArea.bottom - ratio * cropArea.top),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(sourceBitmap, src, new RectF(0, 0, bitmap.getWidth(),
				bitmap.getHeight()), paint);

		return bitmap;
	}

	private int calculatePosition(float x, float y) {
		// is inside?
		if (x > mLeft + mFingerWidth && x < mRight - mFingerWidth
				&& y > mTop + mFingerWidth && y < mBottom - mFingerWidth) {
			return INSIDE_RECT;
		}
		// in circles?
		final float d = radius * radius;

		if (((x - mLeft) * (x - mLeft) + (y - mTop) * (y - mTop)) < d) {
			return LEFT_TOP_CIRCLE;
		}

		if (((x - mRight) * (x - mRight) + (y - mTop) * (y - mTop)) < d) {
			return RIGHT_TOP_CIRCLE;
		}

		if (((x - mRight) * (x - mRight) + (y - mBottom) * (y - mBottom)) < d) {
			return RIGHT_BOTTOM_CIRCLE;
		}

		if (((x - mLeft) * (x - mLeft) + (y - mBottom) * (y - mBottom)) < d) {
			return LEFT_BOTTOM_CIRCLE;
		}
		// in lines?
		if (y > mTop - mFingerWidth && y < mTop + mFingerWidth && x > mLeft
				&& x < mRight) {
			return TOP_LINE;
		}

		if (y > mTop && y < mBottom && x > mRight - mFingerWidth
				&& x < mRight + mFingerWidth) {
			return RIGHT_LINE;
		}

		if (y > mBottom - mFingerWidth && y < mBottom + mFingerWidth
				&& x > mLeft && x < mRight) {
			return BOTTOM_LINE;
		}

		if (y > mTop && y < mBottom && x > mLeft - mFingerWidth
				&& x < mRight + mFingerWidth) {
			return LEFT_LINE;
		}

		return OUTSIDE_RECT;
	}

	private void initClipArea() {
		mTop = 50;
		mLeft = 50;
		mBottom = 200;
		mRight = 200;
		mMaskColor = getContext().getResources().getColor(
				R.color.photo_editor_mask_color);
		mCircleBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.photo_editor_circle_small);
		mPaint = new Paint();
		float scale = getContext().getResources().getDisplayMetrics().density;
		mMinTouchDist = (int) (MIN_TOUCH_DIST_DP * scale + 0.5f);
		mFingerWidth = getResources().getDimension(
				R.dimen.photo_editor_finger_width);
	}

	/**
	 * Find direction when touch. Direction is to move up or down or left or
	 * right.
	 * 
	 * @param first
	 * @param second
	 * @return direction. It is MOVE_UP or MOVE_DOWN or MOVE_LEFT or MOVE_RIGHT
	 */
	public static int findDirectionMovement(PointF first, PointF second) {
		final float x = second.x - first.x;
		final float y = second.y - first.y;
		float px = Math.abs(x);
		float py = Math.abs(y);
		if (y * x >= 0) {
			if (x < 0) {
				if (px >= py) {
					return MOVE_LEFT;
				} else {
					return MOVE_UP;
				}
			} else {
				if (px >= py) {
					return MOVE_RIGHT;
				} else {
					return MOVE_DOWN;
				}
			}
		} else {
			if (x < 0) {
				if (px >= py) {
					return MOVE_LEFT;
				} else {
					return MOVE_DOWN;
				}
			} else {
				if (px >= py) {
					return MOVE_RIGHT;
				} else {
					return MOVE_UP;
				}
			}
		}
	}

	public void setChangeDirection(IChangeDirection changeDirection) {
		mChangeDirection = changeDirection;
	}

	public interface IChangeDirection {
		/**
		 * @param direction
		 *            It is MOVE_UP or MOVE_DOWN or MOVE_LEFT or MOVE_RIGHT
		 */
		public void changeDirection(int direction);

		public void clickAt(float x, float y);
	}
}
