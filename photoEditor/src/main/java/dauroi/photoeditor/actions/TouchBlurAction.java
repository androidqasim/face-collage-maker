package dauroi.photoeditor.actions;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import dauroi.com.imageprocessing.ImageProcessor;
import dauroi.com.imageprocessing.filter.processing.TouchBlurFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.adapter.CustomMenuAdapter;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.horizontalListView.widget.HListView;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;
import dauroi.photoeditor.view.TouchBlurView;
import dauroi.photoeditor.view.TouchBlurView.OnTouchBlurListener;

public class TouchBlurAction extends BlurAction implements OnTouchBlurListener {
	private static final String TAG = BlurAction.class.getSimpleName();
	private static final String[] BLUR_NAMES = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};

	private float[] mBlurRadius;
	private HListView mListView;
	private String[] mRadiusNames;
	private CustomMenuAdapter mMenuAdapter;
	private List<ItemInfo> mMenuItems;
	private int mCurrentPosition = 0;
	private TouchBlurFilter mTouchBlurFilter;
	private TouchBlurView mTouchBlurView;

	public TouchBlurAction(ImageProcessingActivity activity) {
		super(activity);
		mActivity.setDoneActionsClickListener(this);
	}

	@Override
	protected void onInit() {
		super.onInit();
		final float[] radius = { 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150 };
		int size = radius.length;
		mBlurRadius = new float[size];
		for (int idx = 0; idx < size; idx++) {
			mBlurRadius[idx] = Utils.pxFromDp(mActivity, radius[idx]);
		}
	}

	@Override
	public void saveInstanceState(Bundle bundle) {
		super.saveInstanceState(bundle);
		bundle.putFloatArray("dauroi.photoeditor.actions.TouchBlurAction.mBlurRadius", mBlurRadius);
		bundle.putStringArray("dauroi.photoeditor.actions.TouchBlurAction.mRadiusNames", mRadiusNames);
		bundle.putInt("dauroi.photoeditor.actions.TouchBlurAction.mCurrentPosition", mCurrentPosition);
		if (mTouchBlurFilter != null) {
			bundle.putFloat("dauroi.photoeditor.actions.TouchBlurAction.mTouchBlurFilter.mRadius",
					mTouchBlurFilter.getRadius());
			bundle.putFloat("dauroi.photoeditor.actions.TouchBlurAction.mTouchBlurFilter.mBlurSize",
					mTouchBlurFilter.getBlurSize());
			bundle.putFloatArray("dauroi.photoeditor.actions.TouchBlurAction.mTouchBlurFilter.mCenterPoint",
					mTouchBlurFilter.getCenterPoint());
		}
	}

	@Override
	public void restoreInstanceState(Bundle bundle) {
		super.restoreInstanceState(bundle);
		float[] blurRadius = bundle.getFloatArray("dauroi.photoeditor.actions.TouchBlurAction.mBlurRadius");
		if (blurRadius != null) {
			mBlurRadius = blurRadius;
		}

		String[] radiusNames = bundle.getStringArray("dauroi.photoeditor.actions.TouchBlurAction.mRadiusNames");
		if (radiusNames != null) {
			mRadiusNames = radiusNames;
		}

		mCurrentPosition = bundle.getInt("dauroi.photoeditor.actions.TouchBlurAction.mCurrentPosition",
				mCurrentPosition);
		float radius = bundle.getFloat("dauroi.photoeditor.actions.TouchBlurAction.mTouchBlurFilter.mRadius", -1);
		float blurSize = bundle.getFloat("dauroi.photoeditor.actions.TouchBlurAction.mTouchBlurFilter.mBlurSize", -1);
		float[] centerPoints = bundle
				.getFloatArray("dauroi.photoeditor.actions.TouchBlurAction.mTouchBlurFilter.mCenterPoint");
		if (centerPoints != null && radius > 0 && blurSize > 0) {
			mTouchBlurFilter = new TouchBlurFilter(centerPoints, radius, blurSize);
		}
	}

	@Override
	public void apply(final boolean finish) {
		if (!isAttached()) {
			return;
		}

		if (mTouchBlurFilter == null) {
			return;
		}

		ApplyFilterListener listener = new ApplyFilterListener() {

			@Override
			public void onFinishFiltering() {
				if (finish) {
					done();
				}
			}

			@Override
			public Bitmap applyFilter() {
				final int[] size = mActivity.calculateThumbnailSize();
				final float ratio = mActivity.calculateScaleRatio();
				final float dx = (mActivity.getPhotoViewWidth() - size[0]) / 2.0f;
				final float dy = (mActivity.getPhotoViewHeight() - size[1]) / 2.0f;
				Bitmap result = null;
				final float[] center = mTouchBlurFilter.getCenterPoint();
				center[0] = (center[0] - dx) * ratio;
				center[1] = (center[1] - dy) * ratio;

				TouchBlurFilter filter = new TouchBlurFilter(center, mTouchBlurFilter.getRadius() * ratio,
						mTouchBlurFilter.getBlurSize() * ratio);
				filter.setBitmap(mBlurredImage);

				result = ImageProcessor.getFiltratedBitmap(mActivity.getImage(), filter);

				mTouchBlurFilter.setRecycleBitmap(true);
				mTouchBlurFilter.destroy();
				mTouchBlurFilter = null;

				return result;
			}
		};

		ApplyFilterTask task = new ApplyFilterTask(mActivity, listener);
		task.execute();

	}

	@Override
	public View inflateMenuView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		mRootActionView = inflater.inflate(R.layout.photo_editor_action_touch_blur, null);
		mListView = (HListView) mRootActionView.findViewById(R.id.bottomListView);
		// create menus
		mRadiusNames = BLUR_NAMES;
		mMenuItems = new ArrayList<ItemInfo>();
		for (int idx = 0; idx < mRadiusNames.length; idx++) {
			ItemInfo item = new ItemInfo();
			item.setTitle(mRadiusNames[idx]);
			item.setThumbnail("drawable://" + R.drawable.photo_editor_ic_blur_normal);
			item.setSelectedThumbnail("drawable://" + R.drawable.photo_editor_ic_blur_pressed);
			mMenuItems.add(item);
		}

		mMenuAdapter = new CustomMenuAdapter(mActivity, mMenuItems, true);
		mListView.setAdapter(mMenuAdapter);
		mListView.setOnItemClickListener(
				new dauroi.photoeditor.horizontalListView.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(dauroi.photoeditor.horizontalListView.widget.AdapterView<?> parent,
							View view, int position, long id) {
						if (mCurrentPosition != position) {
							selectItem(position);
						}
						onClicked();
					}

				});
		mTouchBlurView = new TouchBlurView(mActivity);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		mTouchBlurView.setLayoutParams(params);
		mTouchBlurView.setTouchBlurListener(this);

		mCurrentPosition = 0;

		return mRootActionView;
	}

	@Override
	public void attach() {
		super.attach();
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mActivity.hideAllMenus();
				mActivity.showProgress(true);
			}

			@Override
			protected Void doInBackground(Void... params) {
				recycleImages();
				long blurredTime = System.currentTimeMillis();
				mBlurredImage = PhotoUtils.blurImage(mActivity.getImage(), Utils.pxFromDp(mActivity, FAST_BLUR_RADIUS));
				ALog.d(TAG, "blurred time = " + (System.currentTimeMillis() - blurredTime));
				if (mTouchBlurFilter != null) {
					TouchBlurFilter touchBlurFilter = new TouchBlurFilter(mTouchBlurFilter.getCenterPoint(),
							mTouchBlurFilter.getRadius(), mTouchBlurFilter.getBlurSize());
					mTouchBlurFilter.destroy();
					mTouchBlurFilter = touchBlurFilter;
				} else {
					float[] center = new float[] { mActivity.getPhotoViewWidth() / 2,
							mActivity.getPhotoViewHeight() / 2 };
					mTouchBlurFilter = new TouchBlurFilter(center, mBlurRadius[0],
							Utils.pxFromDp(mActivity, EXCLUDE_BLURRED_SIZE));

				}

				mTouchBlurFilter.setRecycleBitmap(false);
				mTouchBlurFilter.setBitmap(mBlurredImage);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mActivity.attachMaskView(mTouchBlurView);
				mActivity.applyFilter(mTouchBlurFilter);
				selectItem(mCurrentPosition);
				mActivity.showProgress(false);
				mActivity.showAllMenus();
			}
		};

		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onActivityResume() {
		super.onActivityResume();
		if(isAttached()) {
			mActivity.attachMaskView(mTouchBlurView);
			mActivity.applyFilter(mTouchBlurFilter);
			selectItem(mCurrentPosition);
		}
	}

	public void selectItem(int position) {
		mMenuItems.get(mCurrentPosition).setSelected(false);
		if (mCurrentPosition < position) {
			if (position < mMenuItems.size() - 1) {
				mListView.smoothScrollToPosition(position + 1);
			} else {
				mListView.smoothScrollToPosition(position);
			}
		} else {
			if (position > 0) {
				mListView.smoothScrollToPosition(position - 1);
			} else {
				mListView.smoothScrollToPosition(position);
			}
		}
		mMenuItems.get(position).setSelected(true);
		mMenuAdapter.notifyDataSetChanged();
		// apply frame
		mTouchBlurFilter.setRadius(mBlurRadius[position]);
		mActivity.getImageProcessingView().requestRender();
		mCurrentPosition = position;
	}

	@Override
	public void onTouchBlur(float x, float y) {
		mTouchBlurFilter.setCenterPoint(new float[] { x, mTouchBlurView.getHeight() - y });
		mActivity.getImageProcessingView().requestRender();
	}

	@Override
	public String getActionName() {
		return "TouchBlurAction";
	}
}
