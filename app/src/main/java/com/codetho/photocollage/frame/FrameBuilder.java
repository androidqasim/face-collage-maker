package com.codetho.photocollage.frame;

import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.Constant;
import com.codetho.photocollage.ui.custom.FrameImageView;

import java.util.ArrayList;
import java.util.List;

public class FrameBuilder {
	private List<View> mChildViews = new ArrayList<View>();
	private View mRootView;
	private int mFrameType = Constant.FRAME1;
	private List<RectF> mFramePositions = new ArrayList<RectF>();
	private List<FrameImageView> mFrameImageViews = new ArrayList<FrameImageView>();

	/**
	 * 
	 * @param rootView
	 * @param frameType
	 * @see Constant class
	 */
	public FrameBuilder(View rootView, int frameType) {
		this.mRootView = rootView;
		mFrameType = frameType;

		switch (frameType) {
		case Constant.FRAME1:
			inflateFrame1();
			break;
		case Constant.FRAME2:
			inflateFrame2();
			break;
		case Constant.FRAME3:
			inflateFrame3();
			break;
		case Constant.FRAME4:
			inflateFrame4();
			break;
		case Constant.FRAME5:
			inflateFrame5();
			break;
		case Constant.FRAME6:
			inflateFrame6();
			break;
		case Constant.FRAME7:
			inflateFrame7();
			break;
		case Constant.FRAME8:
			inflateFrame8();
			break;
		case Constant.FRAME9:
			inflateFrame9();
			break;
		default:
			break;
		}
		// find frame image views
		FrameImageView imageView = (FrameImageView) mRootView
				.findViewById(R.id.imageView1);
		if (imageView != null) {
			mFrameImageViews.add(imageView);
		}

		imageView = (FrameImageView) mRootView.findViewById(R.id.imageView2);
		if (imageView != null) {
			mFrameImageViews.add(imageView);
		}

		imageView = (FrameImageView) mRootView.findViewById(R.id.imageView3);
		if (imageView != null) {
			mFrameImageViews.add(imageView);
		}

		imageView = (FrameImageView) mRootView.findViewById(R.id.imageView4);
		if (imageView != null) {
			mFrameImageViews.add(imageView);
		}
	}

	public List<RectF> getFramePositions() {
		return mFramePositions;
	}

	public void setBorderSize(int size) {
		switch (mFrameType) {
		case Constant.FRAME1:
			setBorderSizeForFrame1(size);
			break;
		case Constant.FRAME2:
			setBorderSizeForFrame2(size);
			break;
		case Constant.FRAME3:
			setBorderSizeForFrame3(size);
			break;
		case Constant.FRAME4:
			setBorderSizeForFrame4(size);
			break;
		case Constant.FRAME5:
			setBorderSizeForFrame5(size);
			break;
		case Constant.FRAME6:
			setBorderSizeForFrame6(size);
			break;
		case Constant.FRAME7:
			setBorderSizeForFrame7(size);
			break;
		case Constant.FRAME8:
			setBorderSizeForFrame8(size);
			break;
		case Constant.FRAME9:
			setBorderSizeForFrame9(size);
			break;
		default:
			break;
		}
		// update position infos
		final float containerMargin = mRootView.getResources().getDimension(
				R.dimen.frame_container_margin);
		findFramePositions(containerMargin, size);
	}

	/**
	 * @see R.layout.frame_1
	 */
	private void inflateFrame1() {
		LinearLayout linearLayout1 = (LinearLayout) mRootView
				.findViewById(R.id.linearLayout1);
		mChildViews.add(linearLayout1);
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
		LinearLayout linearLayout2 = (LinearLayout) mRootView
				.findViewById(R.id.linearLayout2);
		mChildViews.add(linearLayout2);
		FrameLayout frameLayout3 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout3);
		mChildViews.add(frameLayout3);
		FrameLayout frameLayout4 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout4);
		mChildViews.add(frameLayout4);
	}

	/**
	 * @see R.layout.frame_1
	 * @param size
	 */
	private void setBorderSizeForFrame1(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.rightMargin = size;
		mChildViews.get(1).setLayoutParams(params);
		// ////////
		params = (LinearLayout.LayoutParams) mChildViews.get(3)
				.getLayoutParams();
		params.leftMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(3).setLayoutParams(params);
		// /////////
		params = (LinearLayout.LayoutParams) mChildViews.get(4)
				.getLayoutParams();
		params.rightMargin = size;
		mChildViews.get(4).setLayoutParams(params);
	}

	/**
	 * @see R.layout.frame_2
	 */
	private void inflateFrame2() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
		FrameLayout frameLayout3 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout3);
		mChildViews.add(frameLayout3);
	}

	/**
	 * @see R.layout.frame_2
	 * @param size
	 */
	private void setBorderSizeForFrame2(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.rightMargin = size;
		params.leftMargin = size;
		mChildViews.get(1).setLayoutParams(params);
		// ////////
		params = (LinearLayout.LayoutParams) mChildViews.get(2)
				.getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(2).setLayoutParams(params);
	}

	/**
	 * @see R.layout.frame_3
	 */
	private void inflateFrame3() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		LinearLayout linearLayout1 = (LinearLayout) mRootView
				.findViewById(R.id.linearLayout1);
		mChildViews.add(linearLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
		FrameLayout frameLayout3 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout3);
		mChildViews.add(frameLayout3);
	}

	/**
	 * @see R.layout.frame_3
	 * @param size
	 */
	private void setBorderSizeForFrame3(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(1).setLayoutParams(params);
		// ////////
		params = (LinearLayout.LayoutParams) mChildViews.get(2)
				.getLayoutParams();
		params.bottomMargin = size;
		mChildViews.get(2).setLayoutParams(params);
	}

	/**
	 * @see R.layout.frame_4
	 */
	private void inflateFrame4() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
		FrameLayout frameLayout3 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout3);
		mChildViews.add(frameLayout3);
	}

	/**
	 * @see R.layout.frame_4
	 * @param size
	 */
	private void setBorderSizeForFrame4(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.topMargin = size;
		params.bottomMargin = size;
		mChildViews.get(1).setLayoutParams(params);
		// ////////
		params = (LinearLayout.LayoutParams) mChildViews.get(2)
				.getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(2).setLayoutParams(params);
	}

	/**
	 * @see R.layout.frame_5
	 */
	private void inflateFrame5() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
	}

	/**
	 * @see R.layout.frame_5
	 * @param size
	 */
	private void setBorderSizeForFrame5(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
	}

	/*
	 * @see R.layout.frame_6
	 */
	private void inflateFrame6() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
	}

	/**
	 * @see R.layout.frame_6
	 * @param size
	 */
	private void setBorderSizeForFrame6(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.leftMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(1).setLayoutParams(params);
	}

	/*
	 * @see R.layout.frame_7
	 */
	private void inflateFrame7() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
	}

	/**
	 * @see R.layout.frame_7
	 * @param size
	 */
	private void setBorderSizeForFrame7(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(1).setLayoutParams(params);
	}

	/**
	 * @see R.layout.frame_8
	 */
	private void inflateFrame8() {
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		LinearLayout linearLayout1 = (LinearLayout) mRootView
				.findViewById(R.id.linearLayout1);
		mChildViews.add(linearLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
		FrameLayout frameLayout3 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout3);
		mChildViews.add(frameLayout3);
	}

	/**
	 * @see R.layout.frame_8
	 * @param size
	 */
	private void setBorderSizeForFrame8(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.leftMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(1).setLayoutParams(params);
		// ////////
		params = (LinearLayout.LayoutParams) mChildViews.get(2)
				.getLayoutParams();
		params.rightMargin = size;
		mChildViews.get(3).setLayoutParams(params);
	}

	/**
	 * @see R.layout.frame_9
	 */
	private void inflateFrame9() {
		LinearLayout linearLayout1 = (LinearLayout) mRootView
				.findViewById(R.id.linearLayout1);
		mChildViews.add(linearLayout1);
		FrameLayout frameLayout1 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout1);
		mChildViews.add(frameLayout1);
		FrameLayout frameLayout2 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout2);
		mChildViews.add(frameLayout2);
		FrameLayout frameLayout3 = (FrameLayout) mRootView
				.findViewById(R.id.frameLayout3);
		mChildViews.add(frameLayout3);
	}

	/**
	 * @see R.layout.frame_9
	 * @param size
	 */
	private void setBorderSizeForFrame9(int size) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mChildViews
				.get(0).getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		mChildViews.get(0).setLayoutParams(params);
		// second view
		params = (LinearLayout.LayoutParams) mChildViews.get(1)
				.getLayoutParams();
		params.rightMargin = size;
		mChildViews.get(1).setLayoutParams(params);
		// ////////
		params = (LinearLayout.LayoutParams) mChildViews.get(3)
				.getLayoutParams();
		params.leftMargin = size;
		params.topMargin = size;
		params.rightMargin = size;
		params.bottomMargin = size;
		mChildViews.get(3).setLayoutParams(params);
	}

	// /////find positions of frames/////////////////////
	public void findFramePositions(final float containerMargin,
			final float frameMargin) {
		switch (mFrameType) {
		case Constant.FRAME1:
			findFramePositions1(containerMargin, frameMargin);
			break;
		case Constant.FRAME2:
			findFramePositions2(containerMargin, frameMargin);
			break;
		case Constant.FRAME3:
			findFramePositions3(containerMargin, frameMargin);
			break;
		case Constant.FRAME4:
			findFramePositions4(containerMargin, frameMargin);
			break;
		case Constant.FRAME5:
			findFramePositions5(containerMargin, frameMargin);
			break;
		case Constant.FRAME6:
			findFramePositions6(containerMargin, frameMargin);
			break;
		case Constant.FRAME7:
			findFramePositions7(containerMargin, frameMargin);
			break;
		case Constant.FRAME8:
			findFramePositions8(containerMargin, frameMargin);
			break;
		case Constant.FRAME9:
			findFramePositions9(containerMargin, frameMargin);
			break;
		default:
			break;
		}
		// update frame image position
		for (int idx = 0; idx < mFrameImageViews.size(); idx++) {
			if (idx < mFramePositions.size()) {
				RectF rect = mFramePositions.get(idx);
				if (rect != null) {
					mFrameImageViews.get(idx).setFramePosition(rect.left,
							rect.top);
				}
			}
		}
	}

	private void findFramePositions1(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();
		float width = (mRootView.getWidth() - 3 * frameMargin) / 2.0f;
		float height = (mRootView.getHeight() - 3 * frameMargin) / 2.0f;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin + width;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 3
		rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + 2 * frameMargin + height;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 4
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin + width;
		rect.top = containerMargin + 2 * frameMargin + height;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
	}

	private void findFramePositions2(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		float width = mRootView.getWidth() - 2 * frameMargin;
		float height = (mRootView.getHeight() - 3 * frameMargin) / 3.0f;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + 2 * frameMargin + height;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 3
		rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + 3 * frameMargin + 2 * height;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
	}

	private void findFramePositions3(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		final int rootWidth = mRootView.getWidth();
		final int rootHeight = mRootView.getHeight();
		final float frameWidth = (rootWidth - 3 * frameMargin) / 2.0f;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + frameWidth;
		rect.bottom = rect.top + (rootHeight - 2 * frameMargin);
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin + frameWidth;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + frameWidth;
		rect.bottom = rect.top + (rootHeight - 3 * frameMargin) / 2.0f;
		mFramePositions.add(rect);
		// frame 3
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin + frameWidth;
		rect.top = containerMargin + 2 * frameMargin
				+ (rootHeight - 3 * frameMargin) / 2.0f;
		rect.right = rect.left + frameWidth;
		rect.bottom = rect.top + (rootHeight - 3 * frameMargin) / 2.0f;
		mFramePositions.add(rect);
		mFramePositions.add(rect);
	}

	private void findFramePositions4(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		float width = (mRootView.getWidth() - 4 * frameMargin) / 3.0f;
		float height = mRootView.getHeight() - 2 * frameMargin;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin + width;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 3
		rect = new RectF();
		rect.left = containerMargin + 3 * frameMargin + 2 * width;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
	}

	private void findFramePositions5(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		float width = mRootView.getWidth() - 2 * frameMargin;
		float height = mRootView.getHeight() - 2 * frameMargin;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
	}

	private void findFramePositions6(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		float width = mRootView.getWidth() - 2 * frameMargin;
		float height = (mRootView.getHeight() - 3 * frameMargin) / 2.0f;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + 2 * frameMargin + height;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
	}

	private void findFramePositions7(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		float width = (mRootView.getWidth() - 3 * frameMargin) / 2.0f;
		float height = mRootView.getHeight() - 2 * frameMargin;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin + width;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + width;
		rect.bottom = rect.top + height;
		mFramePositions.add(rect);
	}

	private void findFramePositions8(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		final int rootWidth = mRootView.getWidth();
		final float frameHeight = (mRootView.getHeight() - 3 * frameMargin) / 2.0f;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + (rootWidth - 2 * frameMargin);
		rect.bottom = rect.top + frameHeight;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + 2 * frameMargin + frameHeight;
		rect.right = rect.left + (rootWidth - 3 * frameMargin) / 2.0f;
		rect.bottom = rect.top + frameHeight;
		mFramePositions.add(rect);
		// frame 3
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin
				+ (rootWidth - 3 * frameMargin) / 2.0f;
		rect.top = containerMargin + 2 * frameMargin + frameHeight;
		rect.right = rect.left + (rootWidth - 3 * frameMargin) / 2.0f;
		rect.bottom = rect.top + frameHeight;
		mFramePositions.add(rect);
	}

	private void findFramePositions9(final float containerMargin,
			final float frameMargin) {
		mFramePositions.clear();

		final int rootWidth = mRootView.getWidth();
		final float frameHeight = (mRootView.getHeight() - 3 * frameMargin) / 2.0f;
		// frame 1
		RectF rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + (rootWidth - 3 * frameMargin) / 2.0f;
		rect.bottom = rect.top + frameHeight;
		mFramePositions.add(rect);
		// frame 2
		rect = new RectF();
		rect.left = containerMargin + 2 * frameMargin
				+ (rootWidth - 3 * frameMargin) / 2.0f;
		rect.top = containerMargin + frameMargin;
		rect.right = rect.left + (rootWidth - 3 * frameMargin) / 2.0f;
		rect.bottom = rect.top + frameHeight;
		mFramePositions.add(rect);
		// frame 3
		rect = new RectF();
		rect.left = containerMargin + frameMargin;
		rect.top = containerMargin + 2 * frameMargin + frameHeight;
		rect.right = rect.left + (rootWidth - 2 * frameMargin);
		rect.bottom = rect.top + frameHeight;
		mFramePositions.add(rect);
	}
}
