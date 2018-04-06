package dauroi.photoeditor.view;

import dauroi.photoeditor.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 
 * @author VanHung
 * 
 */
public class FocusImageView extends ImageView {
	public static interface OnImageFocusListener {
		public void onCircleFocus(final float[] center, final float radius);

		public void onLinearFocus(float[] coeff, final float radius);

		public void onNoFocus();
	}

	public static final int NO_FOCUS = -1;
	public static final int CIRCLE_FOCUS = 0;
	public static final int RECTANGLE_FOCUS = 1;
	private static final int MIN_CIRCLE_RADIUS = 20;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private OnImageFocusListener mFocusListener;
	private float mFocusStrokeWidth = 1;
	private float mCircleRadius = 100;
	private float mCircleX = 0;
	private float mCircleY = 0;
	// intersection points
	private PointF mFirstA = new PointF();
	private PointF mFirstB = new PointF();
	private PointF mSecondA = new PointF();
	private PointF mSecondB = new PointF();

	private boolean mDisplayFocus = true;
	private int mFocusType = NO_FOCUS;

	private Paint mPaint = new Paint();
	// smooth linear focus
	private float mMinLinearFocusRadius = 10;
	private float mMaxLinearFocusRadius = 100;
	private PointF mPointA = new PointF();
	private PointF mPointB = new PointF();
	private float mLinearFocusRadius = 10;
	// mode when touch
	private int mMode = NONE;
	// Remember some things for zooming
	private PointF mStart = new PointF();
	private PointF mMid = new PointF();
	private float mOldDist = 1f;
	// //////////////////////////////
	private float[] mLastEvent = null;
	private float mD = 0f;
	private float mNewRot = 0f;

	public FocusImageView(Context context) {
		super(context);
		init();
	}

	public FocusImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FocusImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mMinLinearFocusRadius = getResources().getDimension(R.dimen.photo_editor_min_linear_focus_radius);
		mFocusStrokeWidth = getResources().getDimension(R.dimen.photo_editor_focus_stroke_width);
	}

	public void saveInstanceState(Bundle bundle) {
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mFocusStrokeWidth", mFocusStrokeWidth);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mCircleRadius", mCircleRadius);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mCircleX", mCircleX);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mCircleY", mCircleY);
		// intersection points
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mFirstA", mFirstA);
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mFirstB", mFirstB);
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mSecondA", mSecondA);
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mSecondB", mSecondB);
		bundle.putBoolean("dauroi.photoeditor.view.FocusImageView.mDisplayFocus", mDisplayFocus);
		bundle.putInt("dauroi.photoeditor.view.FocusImageView.mFocusType", mFocusType);
		// smooth linear focus
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mMinLinearFocusRadius", mMinLinearFocusRadius);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mMaxLinearFocusRadius", mMaxLinearFocusRadius);
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mPointA", mPointA);
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mPointB", mPointB);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mLinearFocusRadius", mLinearFocusRadius);
		// mode when touch
		bundle.putInt("dauroi.photoeditor.view.FocusImageView.mMode", mMode);
		// Remember some things for zooming
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mStart", mStart);
		bundle.putParcelable("dauroi.photoeditor.view.FocusImageView.mMid", mMid);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mOldDist", mOldDist);
		// //////////////////////////////
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mD", mD);
		bundle.putFloat("dauroi.photoeditor.view.FocusImageView.mNewRof", mNewRot);
	}

	public void restoreInstanceState(Bundle bundle) {
		mFocusStrokeWidth = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mFocusStrokeWidth", mFocusStrokeWidth);
		mCircleRadius = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mCircleRadius", mCircleRadius);
		mCircleX = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mCircleX", mCircleX);
		mCircleY = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mCircleY", mCircleY);
		// intersection points
		PointF p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mFirstA");
		if (p != null) {
			mFirstA = p;
		}

		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mFirstB");
		if (p != null) {
			mFirstB = p;
		}

		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mSecondA");
		if (p != null) {
			mSecondA = p;
		}

		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mSecondB");
		if (p != null) {
			mSecondB = p;
		}

		mDisplayFocus = bundle.getBoolean("dauroi.photoeditor.view.FocusImageView.mDisplayFocus", mDisplayFocus);
		mFocusType = bundle.getInt("dauroi.photoeditor.view.FocusImageView.mFocusType", mFocusType);
		// smooth linear focus
		mMinLinearFocusRadius = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mMinLinearFocusRadius", mMinLinearFocusRadius);
		mMaxLinearFocusRadius = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mMaxLinearFocusRadius", mMaxLinearFocusRadius);
		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mPointA");
		if (p != null) {
			mPointA = p;
		}

		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mPointB");
		if (p != null) {
			mPointB = p;
		}

		mLinearFocusRadius = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mLinearFocusRadius", mLinearFocusRadius);
		// mode when touch
		mMode = bundle.getInt("dauroi.photoeditor.view.FocusImageView.mMode", mMode);
		// Remember some things for zooming
		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mStart");
		if (p != null) {
			mStart = p;
		}

		p = bundle.getParcelable("dauroi.photoeditor.view.FocusImageView.mMid");
		if (p != null) {
			mMid = p;
		}
		mOldDist = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mOldDist", mOldDist);
		// //////////////////////////////
		mD = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mD", mD);
		mNewRot = bundle.getFloat("dauroi.photoeditor.view.FocusImageView.mNewRof", mNewRot);
	}

	public void setDisplayFocus(boolean displayFocus) {
		mDisplayFocus = displayFocus;
		invalidate();
	}

	public void setFocusType(int focusType) {
		mFocusType = focusType;
		invalidate();
	}

	public int getFocusType() {
		return mFocusType;
	}

	public void setOnImageFocusListener(OnImageFocusListener focusListener) {
		mFocusListener = focusListener;
	}

	public float[] getCircle() {
		float[] infos = new float[3];
		infos[0] = mCircleX;
		infos[1] = mCircleY;
		infos[2] = mCircleRadius;
		return infos;
	}

	public void setCircle(float x, float y, float radius) {
		mCircleX = x;
		mCircleY = y;
		mCircleRadius = radius;
	}

	/**
	 * This method call invalidate() method to repaint. Should call when view
	 * displayed.
	 * 
	 * @param originalBitmap
	 */
	public void setupFocusInfos(int width, int height) {
		// circle focus
		mCircleX = width / 2;
		mCircleY = height / 2;
		mCircleRadius = Math.min(width / 8, height / 8);
		// linear focus
		float d = (float) Math.sqrt(width * width + height * height) + 2;
		mPointA.x = 0;
		mPointA.y = height / 2;
		mPointB.x = width;
		mPointB.y = height / 2;
		mLinearFocusRadius = height / 6f;
		mMaxLinearFocusRadius = d;
		calculateLinearFocusLines(width, height);
		// set default focus type
		mFocusType = CIRCLE_FOCUS;
		invalidate();
	}

	public void circleFocus() {
		float[] center = { mCircleX, mCircleY };
		invalidate();

		if (mFocusListener != null) {
			mFocusListener.onCircleFocus(center, mCircleRadius);
		}
	}

	public void noFocus() {
		invalidate();
		if (mFocusListener != null) {
			mFocusListener.onNoFocus();
		}
	}

	public void linearFocus() {
		calculateLinearFocusLines(getWidth(), getHeight());
		// convert to OpenGL coordinate
		PointF A = new PointF();
		A.x = mPointA.x;
		A.y = getHeight() - mPointA.y;

		PointF B = new PointF();
		B.x = mPointB.x;
		B.y = getHeight() - mPointB.y;

		Line line = new Line(A, B);
		float[] coeff = line.getCoefficients();

		invalidate();

		if (mFocusListener != null) {
			mFocusListener.onLinearFocus(coeff, mLinearFocusRadius);
		}
	}

	public float[] getLinearFocusInfos(float ratio, float x0, float y0) {
		final int height = getHeight();
		PointF A = new PointF();
		A.x = mPointA.x * ratio;
		A.y = (height - mPointA.y) * ratio;

		PointF B = new PointF();
		B.x = mPointB.x * ratio;
		B.y = (height - mPointB.y) * ratio;

		Line line = new Line(A, B);
		float[] coeff = line.getCoefficients();
		final float c = coeff[0] * x0 * ratio + coeff[1] * y0 * ratio + coeff[2];

		float[] result = { coeff[0], coeff[1], c, mLinearFocusRadius * ratio };

		return result;
	}

	public float[] getLinearFocusInfos(int width, int height) {
		PointF A = new PointF();
		A.x = mPointA.x;
		A.y = height - mPointA.y;

		PointF B = new PointF();
		B.x = mPointB.x;
		B.y = height - mPointB.y;

		Line line = new Line(A, B);
		float[] coeff = line.getCoefficients();
		float[] result = { coeff[0], coeff[1], coeff[2], mLinearFocusRadius };

		return result;
	}

	public PointF[] getLinearFocusLine() {
		return new PointF[] { new PointF(mPointA.x, mPointA.y), new PointF(mPointB.x, mPointB.y) };
	}

	public float getLinearFocusRadius() {
		return mLinearFocusRadius;
	}

	private void calculateLinearFocusLines(final float width, final float height) {
		Line line = new Line(mPointA, mPointB);
		Line[] besideLines = findBesideLines(line, mLinearFocusRadius);
		Line mFirstLine = besideLines[0];
		Line mSecondLine = besideLines[1];

		float[] firstCoeff = mFirstLine.getCoefficients();
		float[] secondCoeff = mSecondLine.getCoefficients();
		mFirstA.x = 0;
		mFirstA.y = 0;
		mFirstB.x = 0;
		mFirstB.y = 0;
		if (firstCoeff[0] != 0) {
			mFirstA.x = -firstCoeff[2] / firstCoeff[0];
			mFirstA.y = 0;

			mFirstB.y = height;
			mFirstB.x = -(firstCoeff[2] + mFirstB.y * firstCoeff[1]) / firstCoeff[0];
		} else if (firstCoeff[1] != 0) {
			mFirstA.x = 0;
			mFirstA.y = -firstCoeff[2] / firstCoeff[1];

			mFirstB.x = width;
			mFirstB.y = -(firstCoeff[2] + mFirstB.x * firstCoeff[0]) / firstCoeff[1];
		}

		mSecondA.x = 0;
		mSecondA.y = 0;
		mSecondB.x = 0;
		mSecondB.y = 0;
		if (secondCoeff[0] != 0) {
			mSecondA.x = -secondCoeff[2] / secondCoeff[0];
			mSecondA.y = 0;

			mSecondB.y = height;
			mSecondB.x = -(secondCoeff[2] + mSecondB.y * secondCoeff[1]) / secondCoeff[0];
		} else if (secondCoeff[1] != 0) {
			mSecondA.x = 0;
			mSecondA.y = -secondCoeff[2] / secondCoeff[1];

			mSecondB.x = width;
			mSecondB.y = -(secondCoeff[2] + mSecondB.x * secondCoeff[0]) / secondCoeff[1];
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mDisplayFocus) {
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(mFocusStrokeWidth);
			mPaint.setColor(Color.WHITE);
			mPaint.setAntiAlias(true);
			if (mFocusType == CIRCLE_FOCUS) {
				canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mPaint);
			} else if (mFocusType == RECTANGLE_FOCUS) {
				canvas.drawLine(mFirstA.x, mFirstA.y, mFirstB.x, mFirstB.y, mPaint);
				canvas.drawLine(mSecondA.x, mSecondA.y, mSecondB.x, mSecondB.y, mPaint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (mFocusType == NO_FOCUS) {
			return true;
		}
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mStart.set(event.getX(), event.getY());
			mMode = DRAG;
			mLastEvent = null;

			mDisplayFocus = true;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mOldDist = spacing(event);
			if (mOldDist > 10f) {
				midPoint(mMid, event);
				mMode = ZOOM;
				// //////////////////////////////////
				mLastEvent = new float[4];
				mLastEvent[0] = event.getX(0);
				mLastEvent[1] = event.getX(1);
				mLastEvent[2] = event.getY(0);
				mLastEvent[3] = event.getY(1);
				mD = rotation(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			mDisplayFocus = false;
			invalidate();
		case MotionEvent.ACTION_POINTER_UP:
			mMode = NONE;
			// ///////////////////
			mLastEvent = null;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMode == DRAG) {
				float deltaX = event.getX() - mStart.x;
				float deltaY = event.getY() - mStart.y;
				if (mFocusType == CIRCLE_FOCUS) {
					mCircleX += deltaX;
					mCircleY += deltaY;
				} else {
					mPointA.x += deltaX;
					mPointA.y += deltaY;
					mPointB.x += deltaX;
					mPointB.y += deltaY;
				}

				mStart.x = event.getX();
				mStart.y = event.getY();

			} else if (mMode == ZOOM && event.getPointerCount() == 2) {
				float newDist = spacing(event);
				Matrix m = new Matrix();
				if (newDist > 10f) {
					if (mFocusType == CIRCLE_FOCUS) {
						float radius = mCircleRadius + (newDist - mOldDist);
						if (radius < MIN_CIRCLE_RADIUS) {
							radius = MIN_CIRCLE_RADIUS;
						}
						radius = Math.min(Math.max(getWidth(), getHeight()), radius);
						mCircleRadius = radius;
					} else if (mFocusType == RECTANGLE_FOCUS) {
						m.postScale(newDist / mOldDist, newDist / mOldDist, getWidth() / 2, getHeight() / 2);
						mLinearFocusRadius += (newDist - mOldDist);
						if (mLinearFocusRadius < mMinLinearFocusRadius) {
							mLinearFocusRadius = mMinLinearFocusRadius;
						} else if (mLinearFocusRadius > mMaxLinearFocusRadius) {
							mLinearFocusRadius = mMaxLinearFocusRadius;
						}
					}
					// ////////////////////////
					if (mLastEvent != null) {
						mNewRot = rotation(event);
						float r = mNewRot - mD;
						// mAngle += r;
						m.postRotate(r, getWidth() / 2, getHeight() / 2);
					}

					mD = mNewRot;
					mOldDist = newDist;

					if (mFocusType == RECTANGLE_FOCUS) {
						float[] src = new float[2];
						float[] dst = new float[2];
						src[0] = mPointA.x;
						src[1] = mPointA.y;
						m.mapPoints(dst, src);
						mPointA.x = dst[0];
						mPointA.y = dst[1];

						src[0] = mPointB.x;
						src[1] = mPointB.y;
						m.mapPoints(dst, src);
						mPointB.x = dst[0];
						mPointB.y = dst[1];
					}
				}

			}

			if (mFocusType == RECTANGLE_FOCUS) {
				calculateLinearFocusLines(getWidth(), getHeight());
			}

			if (mFocusType == CIRCLE_FOCUS) {
				circleFocus();
			} else if (mFocusType == RECTANGLE_FOCUS) {
				linearFocus();
			}

			invalidate();

			break;
		}

		return true;
	}

	/** Determine the degree between the first two fingers */
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * Find lines which are parallel with given line and
	 * 
	 * @param line
	 * @param distance
	 * @return lines
	 */
	private Line[] findBesideLines(Line line, float distance) {
		float[] coeff = line.getCoefficients();
		// find lines
		double m = distance * Math.sqrt(coeff[0] * coeff[0] + coeff[1] * coeff[1]);
		float c1 = (float) (coeff[2] - m);
		float c2 = (float) (coeff[2] + m);

		Line[] lines = new Line[2];
		lines[0] = new Line();
		lines[0].a = coeff[0];
		lines[0].b = coeff[1];
		lines[0].c = c1;

		lines[1] = new Line();
		lines[1].a = coeff[0];
		lines[1].b = coeff[1];
		lines[1].c = c2;

		return lines;
	}

	public static class Line {
		public Line() {
		}

		public Line(PointF firstPoint, PointF secondPoint) {
			this.first = firstPoint;
			this.second = secondPoint;
			findGeneralEquation();
		}

		PointF first = new PointF();
		PointF second = new PointF();
		// ax + by + c= 0
		float a = 0, b = 0, c = 0;

		public void findGeneralEquation() {
			a = second.y - first.y;
			b = first.x - second.x;
			c = first.y * second.x - first.x * second.y;
		}

		public float[] getCoefficients() {
			float[] l = { a, b, c };
			return l;
		}

		public float getValues(PointF point) {
			return (a * point.x + b * point.y + c);
		}
	}

	/**
	 * 
	 * @param firstLine
	 * @param secondLine
	 * @param out
	 *            intersected point if two lines intersect
	 * @return 1 if two lines are parallel<br>
	 *         0 if two lines intersect otherwise return -1
	 */
	protected int findIntersectedPoint(Line first, Line second, PointF out) {
		float a1 = first.a, b1 = first.b, c1 = first.c;
		float a2 = second.a, b2 = second.b, c2 = second.c;
		float d = a1 * b2 - a2 * b1;
		float dx = b1 * c2 - c1 * b2;
		float dy = c1 * a2 - a1 * c2;

		if (d != 0) {
			out.x = dx / d;
			out.y = dy / d;
			return 0;
		}

		if (d == 0 && dx == 0 && dy == 0) {
			return -1;
		}

		return 1;
	}
}
