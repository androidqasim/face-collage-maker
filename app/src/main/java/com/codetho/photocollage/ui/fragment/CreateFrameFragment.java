package com.codetho.photocollage.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.config.Constant;
import com.codetho.photocollage.frame.FrameBuilder;
import com.codetho.photocollage.frame.FrameEntity;
import com.codetho.photocollage.frame.FrameTouch;
import com.codetho.photocollage.multitouch.controller.ImageEntity;
import com.codetho.photocollage.multitouch.custom.PhotoView;
import com.codetho.photocollage.ui.custom.FrameImageView;
import com.codetho.photocollage.ui.custom.FrameImageView.OnGetImageListener;
import com.codetho.photocollage.listener.OnShareImageListener;
import com.codetho.photocollage.utils.DialogUtils;
import com.codetho.photocollage.utils.ImageDecoder;
import com.codetho.photocollage.utils.ImageUtils;
import com.codetho.photocollage.utils.ResultContainer;
import com.codetho.photocollage.utils.DialogUtils.OnAddImageButtonClickListener;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CreateFrameFragment extends EditImageFragment implements
		OnGetImageListener, OnAddImageButtonClickListener {
	private static final String TAG = CreateFrameFragment.class.getSimpleName();
	private static final String PREFIX_FRAME_PATH = Environment
			.getExternalStorageDirectory().toString()
			.concat("/Android/data/com.codetho.photocollage/frame");
	// private int mPhotoViewWidth = 0;
	// private int mPhotoViewHeight = 0;
	private int mFrameId = Constant.FRAME1;
	private FrameLayout mFrameContainer;
	private View mCapturedLayout;
	private View mBackgroundLayout;
	private View mFrameLayout;
	private LayoutInflater mInflater;
	private List<View> mFrameViews = new ArrayList<View>();
	// private List<View> mAddIconViews = new ArrayList<View>();
	private Dialog mAddImageDialog;
	private View mAddImageView;
	private Animation mAnimation;
	// private int mClickedViewIndex = -1;
	private FrameBuilder mFrameBuilder;
	private OnShareImageListener mShareImageListener;
	private PhotoView mPhotoView;
	private View mCurrentSelectedView;
	private FrameTouch mFrameTouch = new FrameTouch() {

		@Override
		public void onFrameTouch(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mCurrentSelectedView = touchFrame(mFrameViews, event);
			}

			if (mCurrentSelectedView != null) {
				((FrameImageView) mCurrentSelectedView).touch(event);
			}
		}

		@Override
		public void onFrameDoubleClick(MotionEvent event) {
			mItemDialog.show();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mShareImageListener = (OnShareImageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(e.getMessage());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState != null) {
			ResultContainer.getInstance().restoreFromBundle(savedInstanceState);
		}
		mAnimation = AnimationUtils.loadAnimation(mActivity,
				R.anim.slide_in_bottom);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ResultContainer.getInstance().saveToBundle(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFrameViews.clear();
		final View view = inflater.inflate(R.layout.fragment_create_frame,
				container, false);
		mAddImageDialog = DialogUtils.createAddImageDialog(getActivity(), this,
				false);
		// find content view of dialogs
		mAddImageView = mAddImageDialog.findViewById(R.id.dialogAddImage);

		mInflater = inflater;
		mCapturedLayout = view.findViewById(R.id.containerLayout);
		mBackgroundLayout = view.findViewById(R.id.backgroundLayout);
		mFrameContainer = (FrameLayout) view.findViewById(R.id.photoLayout);
		mFrameId = getArguments().getInt(Constant.FRAME_EXTRA_KEY);
		// mPhotoViewWidth =
		// getArguments().getInt(Constant.PHOTO_VIEW_WIDTH_KEY);
		// mPhotoViewHeight = getArguments()
		// .getInt(Constant.PHOTO_VIEW_HEIGHT_KEY);
		mPhotoView = (PhotoView) view.findViewById(R.id.photoView);
		mPhotoView.setFrameTouchListener(mFrameTouch);
		mPhotoView.setOnDoubleClickListener(this);
		addFrameLayout();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		ALog.d("CreateFrameFragment.onResume",
				"onResume: width=" + mPhotoView.getWidth() + ", height = "
						+ mPhotoView.getHeight());
		loadImages();
	}

	@Override
	public void onPause() {
		super.onPause();
		ALog.d("CreateFrameFragment.onPause",
				"onPause: width=" + mPhotoView.getWidth() + ", height = "
						+ mPhotoView.getHeight());
		unloadImages();
	}

	private void loadImages() {
		mPhotoView.setImageEntities(ResultContainer.getInstance()
				.copyFrameStickerImages());
		mPhotoView.loadImages(getActivity());
		if (ResultContainer.getInstance().getFrameBackgroundImage() != null) {
			Drawable d = ImageDecoder.decodeUriToDrawable(mActivity,
					ResultContainer.getInstance().getFrameBackgroundImage());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				mBackgroundLayout.setBackground(d);
			} else {
				mBackgroundLayout.setBackgroundDrawable(d);
			}
		}

		ArrayList<FrameEntity> frameImages = ResultContainer.getInstance()
				.copyFrameImages();
		final int size = Math.min(frameImages.size(), mFrameViews.size());
		for (int idx = 0; idx < size; idx++)
			if (frameImages.get(idx) != null) {
				FrameEntity entity = frameImages.get(idx);
				if (entity.getImage() != null) {
					((FrameImageView) mFrameViews.get(idx)).loadImage(
							entity.getImage(), entity.getMatrix());
				} else {
					((FrameImageView) mFrameViews.get(idx))
							.setStartedImage(R.drawable.ic_add_pink);
				}
			}
	}

	private void unloadImages() {
		// save frame images
		ResultContainer.getInstance().clearFrameImages();
		for (View v : mFrameViews) {
			ResultContainer.getInstance().putFrameImage(
					((FrameImageView) v).getFrameEntity());
		}
		// destroy all bitmaps
		ImageUtils.recycleView(mBackgroundLayout);
		mPhotoView.unloadImages();
		for (View v : mFrameViews) {
			((FrameImageView) v).unloadImage();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_create_frame, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_done) {
			clickDoneButton();
		} else if (id == R.id.action_add) {
			clickAddButton();
		}

		return super.onOptionsItemSelected(item);
	}

	private void addFrameLayout() {
		switch (mFrameId) {
		case Constant.FRAME1:
			mFrameLayout = mInflater.inflate(R.layout.frame_1, null);
			break;
		case Constant.FRAME2:
			mFrameLayout = mInflater.inflate(R.layout.frame_2, null);
			break;
		case Constant.FRAME3:
			mFrameLayout = mInflater.inflate(R.layout.frame_3, null);
			break;
		case Constant.FRAME4:
			mFrameLayout = mInflater.inflate(R.layout.frame_4, null);
			break;
		case Constant.FRAME5:
			mFrameLayout = mInflater.inflate(R.layout.frame_5, null);
			break;
		case Constant.FRAME6:
			mFrameLayout = mInflater.inflate(R.layout.frame_6, null);
			break;
		case Constant.FRAME7:
			mFrameLayout = mInflater.inflate(R.layout.frame_7, null);
			break;
		case Constant.FRAME8:
			mFrameLayout = mInflater.inflate(R.layout.frame_8, null);
			break;
		case Constant.FRAME9:
			mFrameLayout = mInflater.inflate(R.layout.frame_9, null);
			break;
		default:
			break;
		}
		// add widgets
		View photoView = mFrameLayout.findViewById(R.id.imageView1);
		if (photoView != null)
			mFrameViews.add(photoView);

		photoView = mFrameLayout.findViewById(R.id.imageView2);
		if (photoView != null)
			mFrameViews.add(photoView);

		photoView = mFrameLayout.findViewById(R.id.imageView3);
		if (photoView != null)
			mFrameViews.add(photoView);

		photoView = mFrameLayout.findViewById(R.id.imageView4);
		if (photoView != null)
			mFrameViews.add(photoView);
		// add icon views
		// View addIconView = mFrameLayout.findViewById(R.id.addIconImage1);
		// if (addIconView != null)
		// mAddIconViews.add(addIconView);
		// addIconView = mFrameLayout.findViewById(R.id.addIconImage2);
		// if (addIconView != null)
		// mAddIconViews.add(addIconView);
		// addIconView = mFrameLayout.findViewById(R.id.addIconImage3);
		// if (addIconView != null)
		// mAddIconViews.add(addIconView);
		// addIconView = mFrameLayout.findViewById(R.id.addIconImage4);
		// if (addIconView != null)
		// mAddIconViews.add(addIconView);

		mFrameContainer.addView(mFrameLayout);
		mFrameBuilder = new FrameBuilder(mFrameLayout, mFrameId);
		// set onCLick listener
		// for (int idx = 0; idx < mAddIconViews.size(); idx++) {
		// final View view = mAddIconViews.get(idx);
		// view.setTag(idx);
		// view.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mClickedViewIndex = (Integer) v.getTag();
		// if (already()) {
		// pickImageFromGallery();
		// }
		// }
		// });
		// }

		for (int idx = 0; idx < mFrameViews.size(); idx++) {
			final FrameImageView view = (FrameImageView) mFrameViews.get(idx);
			view.setTag(idx);
			// view.setOnDoubleClickListener(this);
			view.setGetImageListener(this);
			view.setFrameTouchListener(mFrameTouch);
			// set add icon
			view.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {

						@SuppressWarnings("deprecation")
						@Override
						public void onGlobalLayout() {
							view.setStartedImage(R.drawable.ic_add_pink);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								view.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							} else {
								view.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							}
						}
					});
		}
		// find frame positions
		mFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						final float containerMargin = getResources()
								.getDimension(R.dimen.frame_container_margin);
						final float frameMargin = getResources().getDimension(
								R.dimen.frame_margin);
						mFrameBuilder.findFramePositions(containerMargin,
								frameMargin);
						ALog.d(TAG, "mFrameViews count=" + mFrameViews.size()
								+ ", FrameBuilder child count = "
								+ mFrameBuilder.getFramePositions().size());
						for (int idx = 0; idx < mFrameViews.size(); idx++) {
							final FrameImageView view = (FrameImageView) mFrameViews
									.get(idx);
							if (idx < mFrameBuilder.getFramePositions().size())
								view.setImageBound(mFrameBuilder
										.getFramePositions().get(idx));
						}

						if (mChooseColorListener != null) {
							selectColorBorder(mChooseColorListener
									.getSelectedColor());
						}

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							mFrameLayout.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						} else {
							mFrameLayout.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
				});
	}

	@Override
	public void onEditButtonClick() {
		super.onEditButtonClick();
		if (mSelectedEntity != null && mSelectedEntity.getImageUri() != null) {
			requestEditingImage(mSelectedEntity.getImageUri());
		} else {
			FrameImageView v = (FrameImageView) mCurrentSelectedView;
			if (v != null && v.getImageUri() != null)
				requestEditingImage(v.getImageUri());
		}
	}

	@Override
	protected void resultEditImage(Uri uri) {
		super.resultEditImage(uri);
		if (mCurrentSelectedView != null) {
			((FrameImageView) mCurrentSelectedView).loadImage(uri, false);
		}
		// save frame images
		ResultContainer.getInstance().clearFrameImages();
		for (View v : mFrameViews) {
			ResultContainer.getInstance().putFrameImage(
					((FrameImageView) v).getFrameEntity());
		}
		mCurrentSelectedView = null;
	}

	@Override
	public void resultFromPhotoEditor(Uri image) {
		if (!already()) {
			return;
		}

		// if (mClickedViewIndex > -1) {
		if (mCurrentSelectedView != null) {
			// mAddIconViews.get(mClickedViewIndex).setVisibility(View.GONE);
			// ImageEntity entity = new ImageEntity(image, getResources());
			// entity.setDrawImageBorder(false);
			// entity.setInitScaleFactor(0.5);
			// mPhotoViews.get(mClickedViewIndex).addImageEntity(entity);
			FrameImageView v = (FrameImageView) mCurrentSelectedView;// mFrameViews.get(mClickedViewIndex);
			v.loadImage(image, true);
			v.setGetImageMode(false);
			// mClickedViewIndex = -1;
			// save frame images
			ResultContainer.getInstance().clearFrameImages();
			for (View view : mFrameViews) {
				ResultContainer.getInstance().putFrameImage(
						((FrameImageView) view).getFrameEntity());
			}
		}

		mCurrentSelectedView = null;
	}

	@Override
	protected void resultSticker(Uri uri) {
		super.resultSticker(uri);
		ImageEntity entity = new ImageEntity(uri, getResources());
		entity.load(getActivity(),
				(mPhotoView.getWidth() - entity.getWidth()) / 2,
				(mPhotoView.getHeight() - entity.getHeight()) / 2);
		entity.setSticker(true);
		mPhotoView.addImageEntity(entity);
		// save sticker
		ResultContainer.getInstance().putFrameSticker(entity);
		mCurrentSelectedView = null;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void resultBackground(Uri uri) {
		super.resultBackground(uri);
		if (!already()) {
			return;
		}
		mActivity = getActivity();
		Drawable d = ImageDecoder.decodeUriToDrawable(mActivity, uri);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mBackgroundLayout.setBackground(d);
		} else {
			mBackgroundLayout.setBackgroundDrawable(d);
		}
		// save background
		ResultContainer.getInstance().setFrameBackgroundImage(uri);
		mCurrentSelectedView = null;
	}

	public void clickDoneButton() {
		if (!already()) {
			return;
		}
		mActivity = getActivity();
		try {
			final String imagePath = PREFIX_FRAME_PATH
					+ System.currentTimeMillis() + ".jpg";
			ImageUtils.takeScreen(mCapturedLayout, imagePath);
			if (mShareImageListener != null) {
				mShareImageListener.onShareFrame(imagePath);
			}
		} catch (OutOfMemoryError err) {
			Toast.makeText(mActivity,
					mActivity.getString(R.string.waring_out_of_memory),
					Toast.LENGTH_LONG).show();
		}
	}

	public void clickAddButton() {
		pickSticker();
	}

	@Override
	public void onDestroyView() {
		unloadImages();
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		ResultContainer.getInstance().clearAllImageInFrameCreator();
	}

	@Override
	public void selectColorBorder(int color) {
		mFrameLayout.setBackgroundColor(color);
		mCurrentSelectedView = null;
	}

	@Override
	public void onRemoveButtonClick() {
		super.onRemoveButtonClick();

		if (mSelectedEntity != null) {
			if (mSelectedPhotoView != null) {
				mSelectedPhotoView.removeImageEntity(mSelectedEntity);
				ResultContainer.getInstance().removeFrameSticker(
						mSelectedEntity);
				mSelectedEntity = null;
			}
		} else if (mCurrentSelectedView != null) {
			final int size = mFrameViews.size();
			for (int idx = 0; idx < size; idx++)
				if (mCurrentSelectedView == mFrameViews.get(idx)) {
					// mAddIconViews.get(idx).setVisibility(View.VISIBLE);
					((FrameImageView) mCurrentSelectedView)
							.setGetImageMode(true);
					((FrameImageView) mCurrentSelectedView)
							.setStartedImage(R.drawable.ic_add_pink);
					mCurrentSelectedView = null;
					break;
				}
			// save frame images
			ResultContainer.getInstance().clearFrameImages();
			for (View view : mFrameViews) {
				ResultContainer.getInstance().putFrameImage(
						((FrameImageView) view).getFrameEntity());
			}
		}
	}

	@Override
	public void onBorderSizeChange(float borderSize) {
		mFrameBuilder.setBorderSize((int) borderSize);
		for (int idx = 0; idx < mFrameViews.size(); idx++) {
			final FrameImageView view = (FrameImageView) mFrameViews.get(idx);
			view.setImageBound(mFrameBuilder.getFramePositions().get(idx));
		}
		mCurrentSelectedView = null;
	}

	@Override
	public void onShadowSizeChange(float shadowSize) {

	}

	@Override
	public void onFirstTouch(View v) {
		// mClickedViewIndex = (Integer) v.getTag();
		if (already()) {
			if (mAddImageView != null) {
				mAddImageView.startAnimation(mAnimation);
			}
			mAddImageDialog.show();
		}
	}

	@Override
	public void onCancelEdit() {
		super.onCancelEdit();
		mCurrentSelectedView = null;
	}

	private View touchFrame(List<View> views, MotionEvent event) {
		final int size = mFrameViews.size();
		for (int idx = 0; idx < size; idx++) {
			RectF rect = mFrameBuilder.getFramePositions().get(idx);
			if (event.getX() > rect.left && event.getX() < rect.right
					&& event.getY() > rect.top && event.getY() < rect.bottom) {
				return mFrameViews.get(idx);
			}
		}

		return null;
	}

	@Override
	public void onCameraButtonClick() {
		getImageFromCamera();
		if (mAddImageDialog != null && mAddImageDialog.isShowing()) {
			mAddImageDialog.dismiss();
		}
	}

	@Override
	public void onGalleryButtonClick() {
		pickImageFromGallery();
		if (mAddImageDialog != null && mAddImageDialog.isShowing()) {
			mAddImageDialog.dismiss();
		}
	}

	@Override
	public void onStickerButtonClick() {
		// Don't need to implement
	}

	@Override
	public void onTextButtonClick() {
		//TODO:
	}

	@Override
	public void onBackgroundColorButtonClick() {

	}

	@Override
	public void onBackgroundPhotoButtonClick() {

	}
}
