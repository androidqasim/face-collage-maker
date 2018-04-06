package dauroi.photoeditor.actions;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import dauroi.com.imageprocessing.ImageProcessor;
import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.database.table.FilterTable;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.model.FilterInfo;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.Utils;

@SuppressLint("UseSparseArrays")
public class EffectAction extends PackageAction {
	private static final String TAG = EffectAction.class.getSimpleName();

	public EffectAction(ImageProcessingActivity activity) {
		super(activity, ItemPackageTable.FILTER_TYPE);
	}

	@Override
	public View inflateMenuView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		mRootActionView = inflater.inflate(R.layout.photo_editor_action_effect, null);
		return mRootActionView;
	}

	@Override
	public void attach() {
		super.attach();
		ALog.d(TAG, "attach");
		mActivity.attachMaskView(null);
	}

	@Override
	public void restoreInstanceState(Bundle bundle) {
		super.restoreInstanceState(bundle);
		ALog.d(TAG, "restoreInstanceState");
	}

	@Override
	public void onActivityResume() {
		super.onActivityResume();
		ALog.d(TAG, "onActivityResume");
		mActivity.attachMaskView(null);
	}

	@Override
	public void apply(final boolean finish) {
		if (!isAttached()) {
			return;
		}
		ApplyFilterTask task = new ApplyFilterTask(mActivity, new ApplyFilterListener() {

			@Override
			public void onFinishFiltering() {
				mMenuItems.get(mCurrentPosition).setSelected(false);
				mCurrentPosition = 0;
				mCurrentPackageId = 0;
				mCurrentPackageFolder = null;

				mActivity.applyFilter(new ImageFilter());
				mMenuItems.get(0).setSelected(true);
				mMenuAdapter.notifyDataSetChanged();
				mListView.setSelection(0);

				if (finish) {
					done();
				}
			}

			@Override
			public Bitmap applyFilter() {
				Bitmap bm = null;
				try {
					ItemInfo itemInfo = mMenuItems.get(mCurrentPosition);
					ImageFilter filter = ((FilterInfo) itemInfo).getImageFilter();
					bm = ImageProcessor.getFiltratedBitmap(mActivity.getImage(), filter);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				return bm;
			}
		});

		task.execute();
	}

	@Override
	protected void selectNormalItem(int position) {
		final ItemInfo info = mMenuItems.get(position);
		mActivity.applyFilter(((FilterInfo) info).getImageFilter());
	}

	@Override
	protected List<? extends ItemInfo> loadNormalItems(long packageId, String packageFolder) {
		FilterTable filterTable = new FilterTable(mActivity);
		List<FilterInfo> filterInfos = filterTable.getAllRows(packageId);
		if (packageFolder != null && packageFolder.length() > 0) {
			for (FilterInfo filterInfo : filterInfos) {
				filterInfo.setThumbnail(Utils.FILTER_FOLDER.concat("/").concat(packageFolder).concat("/")
						.concat(filterInfo.getThumbnail()));
				filterInfo.setPackageFolder(packageFolder);
			}
		}

		return filterInfos;
	}

	@Override
	public String getActionName() {
		return "EffectAction";
	}
}
