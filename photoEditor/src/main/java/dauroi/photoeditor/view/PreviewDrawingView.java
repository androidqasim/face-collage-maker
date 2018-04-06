package dauroi.photoeditor.view;

import dauroi.photoeditor.R;
import dauroi.photoeditor.config.ALog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class PreviewDrawingView extends View {
	// drawing path
	private Path mDrawPath;
	// drawing and canvas paint
	private Paint mDrawPaint;
	// initial color
	private int mPaintColor = Color.RED;
	private float mPaintSize = 30;

	public PreviewDrawingView(Context context) {
		super(context);
		setupDrawing();
	}

	public PreviewDrawingView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupDrawing();
	}

	// constructor
	public PreviewDrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	// prepare drawing
	private void setupDrawing() {
		mDrawPath = new Path();
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mPaintSize = getContext().getResources().getDimension(
				R.dimen.photo_editor_min_finger_width);
		mDrawPaint.setStrokeWidth(mPaintSize);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	public void init(int widthView, int heightView) {
		ALog.d("PreviewDrawingView", "init, widthView=" + widthView
				+ ", heightView=" + heightView);
		// create sin path
		mDrawPath.reset();
		final int count = 100;
		float x, y, temp;
		for (int idx = 10; idx < count - 10; idx++) {
			x = idx * (widthView / (float) count);
			temp = (float) (idx * (Math.PI / count));
			y = (float) (heightView / 3 * Math.cos(temp)) + heightView / 2;
			if (idx == 10) {
				mDrawPath.moveTo(x, y);
			} else
				mDrawPath.lineTo(x, y);
		}

		invalidate();
	}

	public void setPaintColor(int paintColor) {
		mPaintColor = paintColor;
		mDrawPaint.setColor(mPaintColor);
		invalidate();
	}

	public void setPaintSize(float paintSize) {
		mPaintSize = paintSize;
		mDrawPaint.setStrokeWidth(mPaintSize);
		invalidate();
	}

	public float getPaintSize() {
		return mPaintSize;
	}

	public int getPaintColor() {
		return mPaintColor;
	}

	// draw view
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(mDrawPath, mDrawPaint);
	}
}
