package com.codetho.photocollage.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

public class OptionShadowView extends View {

	private int mShadowSize = 0;
	private GradientDrawable mGradientDrawable = new GradientDrawable(
			GradientDrawable.Orientation.TL_BR, new int[] { Color.TRANSPARENT,
					Color.GRAY });
	private Rect mDrawableBounds = null;
	private Rect mOriginalBounds = null;

	public OptionShadowView(Context context) {
		super(context);
	}

	public OptionShadowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OptionShadowView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setDrawableBounds(Rect drawableBounds) {
		mOriginalBounds = drawableBounds;
		mDrawableBounds = new Rect(mOriginalBounds);
	}

	public Rect getDrawableBounds() {
		return mDrawableBounds;
	}

	public void setShadowSize(int shadowSize) {
		mShadowSize = shadowSize;
		if (mDrawableBounds != null && mOriginalBounds != null) {
			mDrawableBounds.left = mOriginalBounds.left + shadowSize;
			mDrawableBounds.right = mOriginalBounds.right + shadowSize;
			mDrawableBounds.top = mOriginalBounds.top + shadowSize;
			mDrawableBounds.bottom = mOriginalBounds.bottom + shadowSize;
		}

		invalidate();
	}

	public int getShadowSize() {
		return mShadowSize;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (getWidth() < 10 || getHeight() < 10) {
			return;
		}

		if (mDrawableBounds == null) {
			mDrawableBounds = new Rect(mShadowSize, mShadowSize, getWidth(),
					getHeight());
		}

		drawShadow(canvas);
	}

	private void drawShadow(Canvas canvas) {
		mGradientDrawable.setBounds(mDrawableBounds);
		mGradientDrawable.setCornerRadius(5);
		mGradientDrawable.draw(canvas);
	}
}
