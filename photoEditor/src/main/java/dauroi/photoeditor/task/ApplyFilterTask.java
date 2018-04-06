package dauroi.photoeditor.task;

import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ApplyFilterTask extends AsyncTask<Void, Void, Bitmap> {
	private ImageProcessingActivity mActivity;
	private ApplyFilterListener mListener;

	public ApplyFilterTask(ImageProcessingActivity activity,
			ApplyFilterListener listener) {
		mActivity = activity;
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mActivity.hideAllMenus();
		mActivity.showProgress(true);
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		return mListener.applyFilter();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (result != null) {
			mActivity.setImage(result, true);
		}
		// go to filter mode
		mActivity.selectAction(0);
		mActivity.showProgress(false);
		mActivity.showAllMenus();
		mListener.onFinishFiltering();
	}
}
