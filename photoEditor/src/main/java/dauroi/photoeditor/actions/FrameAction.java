package dauroi.photoeditor.actions;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import dauroi.com.imageprocessing.ImageProcessor;
import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.com.imageprocessing.filter.blend.SourceOverBlendFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.database.table.ShadeTable;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.model.ShadeInfo;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;

public class FrameAction extends MaskAction {
	public FrameAction(ImageProcessingActivity activity) {
		super(activity, ItemPackageTable.FRAME_TYPE);
	}

	@Override
	public void saveInstanceState(Bundle bundle) {
		super.saveInstanceState(bundle);
		saveCurrentInfos(bundle, "dauroi.photoeditor.actions.FrameAction.mCurrentPosition",
				"dauroi.photoeditor.actions.FrameAction.mPackageId",
				"dauroi.photoeditor.actions.FrameAction.mCurrentPackageFolder");
		saveMaps(bundle, "dauroi.photoeditor.actions.FrameAction.mSelectedItemIndexes",
				"dauroi.photoeditor.actions.FrameAction.mListViewPositions");
	}

	@Override
	public void restoreInstanceState(Bundle bundle) {
		super.restoreInstanceState(bundle);
		restoreCurrentInfos(bundle, "dauroi.photoeditor.actions.FrameAction.mCurrentPosition",
				"dauroi.photoeditor.actions.FrameAction.mPackageId",
				"dauroi.photoeditor.actions.FrameAction.mCurrentPackageFolder");
		restoreMaps(bundle, "dauroi.photoeditor.actions.FrameAction.mSelectedItemIndexes",
				"dauroi.photoeditor.actions.FrameAction.mListViewPositions");
	}

	@Override
	public void apply(final boolean finish) {
		if (!isAttached()) {
			return;
		}
		ApplyFilterTask task = new ApplyFilterTask(mActivity, new ApplyFilterListener() {

			@Override
			public void onFinishFiltering() {
				mImageMaskView.setBackgroundColor(Color.TRANSPARENT);
				mCurrentPosition = 0;
				mCurrentPackageId = 0;
				mCurrentPackageFolder = null;

				if (finish) {
					done();
				}
			}

			@Override
			public Bitmap applyFilter() {
				Bitmap bm = null;
				try {
					Drawable drawable = mImageMaskView.getBackground();
					if (drawable != null && drawable instanceof BitmapDrawable) {
						BitmapDrawable bd = (BitmapDrawable) drawable;
						Bitmap frame = bd.getBitmap();
						if (frame != null && !frame.isRecycled()) {
							SourceOverBlendFilter filter = new SourceOverBlendFilter();
							filter.setBitmap(frame);
							bm = ImageProcessor.getFiltratedBitmap(mActivity.getImage(), filter);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return bm;
			}
		});

		task.execute();
	}

	@Override
	public View inflateMenuView() {
		mRootActionView = mLayoutInflater.inflate(R.layout.photo_editor_action_frame, null);
		return mRootActionView;
	}

	@Override
	public void attach() {
		super.attach();
		mMaskLayout.setBackgroundColor(Color.TRANSPARENT);
		mActivity.attachMaskView(mMaskLayout);
		adjustImageMaskLayout();
		mActivity.applyFilter(new ImageFilter());
	}

	@Override
	public void onActivityResume() {
		super.onActivityResume();
		if (isAttached()) {
			mActivity.attachMaskView(mMaskLayout);
			mActivity.applyFilter(new ImageFilter());
		}
	}

	@Override
	protected int getMaskLayoutRes() {
		return R.layout.photo_editor_mask_layout;
	}

	protected String getShadeType() {
		return ShadeTable.FRAME_TYPE;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void selectNormalItem(int position) {
		final ItemInfo info = mMenuItems.get(position);
		// recycle old bg
		Drawable drawable = mImageMaskView.getBackground();
		if (drawable != null && drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			Bitmap bm = bd.getBitmap();
			if (bm != null && !bm.isRecycled()) {
				mImageMaskView.setBackgroundColor(Color.TRANSPARENT);
				bm.recycle();
				bm = null;
			}
		}

		Bitmap bg = PhotoUtils.decodePNGImage(mActivity, ((ShadeInfo) info).getForeground());
		if (Build.VERSION.SDK_INT < 16) {
			mImageMaskView.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), bg));
		} else {
			mImageMaskView.setBackground(new BitmapDrawable(mActivity.getResources(), bg));
		}
	}

	@Override
	protected List<? extends ItemInfo> loadNormalItems(long packageId, String packageFolder) {
		ShadeTable shadeTable = new ShadeTable(mActivity);
		List<ShadeInfo> frameInfos = shadeTable.getRows(packageId, getShadeType());
		if (packageFolder != null && packageFolder.length() > 0) {
			final String baseFolder = Utils.FRAME_FOLDER.concat("/").concat(packageFolder).concat("/");
			for (ShadeInfo info : frameInfos) {
				info.setForeground(baseFolder.concat(info.getForeground()));
				info.setThumbnail(baseFolder.concat(info.getThumbnail()));
			}
		}

		return frameInfos;
	}

	@Override
	public String getActionName() {
		return "FrameAction";
	}
}
