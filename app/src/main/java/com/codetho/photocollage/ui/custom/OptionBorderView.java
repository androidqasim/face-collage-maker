package com.codetho.photocollage.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.codetho.photocollage.R;

public class OptionBorderView extends View {

	private float mBorderSize = 0;
	private int mForegroundColor = 0xA6033E;
	private Paint mPaint = new Paint();
	private RectF mRectF = new RectF();

	public OptionBorderView(Context context) {
		super(context);
		init();
	}

	public OptionBorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OptionBorderView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void setBorderSize(float borderSize) {
		mBorderSize = borderSize;
		invalidate();
	}

	public float getBorderSize() {
		return mBorderSize;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (getWidth() < 10 || getHeight() < 10) {
			return;
		}
		mRectF.set(mBorderSize, mBorderSize, getWidth() - mBorderSize,
				getHeight() - mBorderSize);
		canvas.drawRect(mRectF, mPaint);
	}

	private void init() {
		mForegroundColor = getResources().getColor(
				R.color.border_view_foreground);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(mForegroundColor);
	}
}
