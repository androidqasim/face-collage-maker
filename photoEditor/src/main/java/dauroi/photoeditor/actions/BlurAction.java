package dauroi.photoeditor.actions;

import android.graphics.Bitmap;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;

public abstract class BlurAction extends BaseAction {
	public static final int FAST_BLUR_RADIUS = 30;
	protected static final int EXCLUDE_BLURRED_SIZE = 60;
	
	protected Bitmap mBlurredImage;

	public BlurAction(ImageProcessingActivity activity) {
		super(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		recycleImages();
	}

	protected void recycleImages() {
		if (mBlurredImage != null && !mBlurredImage.isRecycled()) {
			mBlurredImage.recycle();
			mBlurredImage = null;
		}
	}
}
