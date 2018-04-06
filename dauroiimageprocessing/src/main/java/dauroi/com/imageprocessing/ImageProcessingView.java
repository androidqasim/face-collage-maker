package dauroi.com.imageprocessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.io.File;

import dauroi.com.imageprocessing.filter.ImageFilter;

public class ImageProcessingView extends GLSurfaceView {

	private ImageProcessor mImageProcessor;
	private ImageFilter mFilter;
	private float mRatio = 0.0f;

	public ImageProcessingView(Context context) {
		super(context);
		init();
	}

	public ImageProcessingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mImageProcessor = new ImageProcessor(getContext());
		mImageProcessor.setGLSurfaceView(this);
	}

	public ImageProcessor getImageProcessor() {
		return mImageProcessor;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mRatio == 0.0f) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);

			int newHeight;
			int newWidth;
			if (width / mRatio < height) {
				newWidth = width;
				newHeight = Math.round(width / mRatio);
			} else {
				newHeight = height;
				newWidth = Math.round(height * mRatio);
			}

			int newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth,
					MeasureSpec.EXACTLY);
			int newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight,
					MeasureSpec.EXACTLY);
			super.onMeasure(newWidthSpec, newHeightSpec);
		}
	}

	public void setScaleType(ImageProcessor.ScaleType scaleType) {
		mImageProcessor.setScaleType(scaleType);
	}

	public void setBackground(float r, float g, float b, float alpha){
		mImageProcessor.setBackground(r, g, b, alpha);
	}
	
	// TODO Should be an xml attribute. But then GPUImage can not be distributed
	// as .jar anymore.
	public void setRatio(float ratio) {
		mRatio = ratio;
		requestLayout();
		mImageProcessor.deleteImage();
	}

	/**
	 * Set the filter to be applied on the image.
	 * 
	 * @param filter
	 *            Filter that should be applied on the image.
	 */
	public void setFilter(ImageFilter filter) {
		mFilter = filter;
		mImageProcessor.setFilter(filter);
		requestRender();
	}

	/**
	 * Get the current applied filter.
	 * 
	 * @return the current filter
	 */
	public ImageFilter getFilter() {
		return mFilter;
	}

	/**
	 * Sets the image on which the filter should be applied.
	 * 
	 * @param bitmap
	 *            the new image
	 */
	public void setImage(final Bitmap bitmap) {
		mImageProcessor.setImage(bitmap);
	}

	/**
	 * Sets the image on which the filter should be applied from a Uri.
	 * 
	 * @param uri
	 *            the uri of the new image
	 */
	public void setImage(final Uri uri) {
		mImageProcessor.setImage(uri);
	}

	/**
	 * Sets the image on which the filter should be applied from a File.
	 * 
	 * @param file
	 *            the file of the new image
	 */
	public void setImage(final File file) {
		mImageProcessor.setImage(file);
	}

	/**
	 * Save current image with applied filter to Pictures. It will be stored on
	 * the default Picture folder on the phone below the given folerName and
	 * fileName. <br />
	 * This method is async and will notify when the image was saved through the
	 * listener.
	 * 
	 * @param folderName
	 *            the folder name
	 * @param fileName
	 *            the file name
	 * @param listener
	 *            the listener
	 */
	public void saveToPictures(final String folderName, final String fileName,
			final ImageProcessor.OnPictureSavedListener listener) {
		mImageProcessor.saveToPictures(folderName, fileName, listener);
	}
}
