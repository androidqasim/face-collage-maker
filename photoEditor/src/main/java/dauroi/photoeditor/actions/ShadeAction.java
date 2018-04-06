package dauroi.photoeditor.actions;

import android.os.Bundle;
import dauroi.photoeditor.database.table.ShadeTable;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;

public class ShadeAction extends FrameAction {
	public ShadeAction(ImageProcessingActivity activity) {
		super(activity);
	}

	@Override
	public void saveInstanceState(Bundle bundle) {
		super.saveInstanceState(bundle);
		bundle.putInt("dauroi.photoeditor.actions.ShadeAction.mCurrentPosition", mCurrentPosition);
		bundle.putLong("dauroi.photoeditor.actions.ShadeAction.mPackageId", mCurrentPackageId);
	}

	@Override
	public void restoreInstanceState(Bundle bundle) {
		super.restoreInstanceState(bundle);
		mCurrentPosition = bundle.getInt("dauroi.photoeditor.actions.ShadeAction.mCurrentPosition", mCurrentPosition);
		mCurrentPackageId = bundle.getLong("dauroi.photoeditor.actions.ShadeAction.mCurrentPosition", mCurrentPackageId);
	}

	@Override
	protected String getShadeType() {
		return ShadeTable.SHADE_TYPE;
	}
}
