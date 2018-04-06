package dauroi.photoeditor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PinchableImageView extends ImageView {
	private MultiTouchHandler mTouchHandler;

	public PinchableImageView(Context context) {
		super(context);
		setScaleType(ScaleType.FIT_CENTER);
	}

	public PinchableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setScaleType(ScaleType.FIT_CENTER);
	}

	public PinchableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setScaleType(ScaleType.FIT_CENTER);
	}

	/**
	 * Also reset every thing.
	 *
	 */
	public void reset() {
		mTouchHandler = null;
		setScaleType(ScaleType.FIT_CENTER);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mTouchHandler == null && getWidth() > 5 && getHeight() > 5) {
			setScaleType(ScaleType.MATRIX);
			mTouchHandler = new MultiTouchHandler();
			mTouchHandler.setMatrix(getImageMatrix());
			mTouchHandler.setEnableRotation(true);
		}

		if (mTouchHandler != null) {
			mTouchHandler.touch(event);
			setImageMatrix(mTouchHandler.getMatrix());
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	public int[] calculateThumbnailSize(int imageWidth, int imageHeight) {
		int[] size = new int[2];
		float ratioWidth = ((float) imageWidth) / getWidth();
		float ratioHeight = ((float) imageHeight) / getHeight();
		float ratio = Math.max(ratioWidth, ratioHeight);
		if (ratio == ratioWidth) {
			size[0] = getWidth();
			size[1] = (int) (imageHeight / ratio);
		} else {
			size[0] = (int) (imageWidth / ratio);
			size[1] = getHeight();
		}

		return size;
	}
}
