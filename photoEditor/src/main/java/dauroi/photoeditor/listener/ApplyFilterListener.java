package dauroi.photoeditor.listener;

import android.graphics.Bitmap;

public abstract class ApplyFilterListener {
	public abstract Bitmap applyFilter();

	public abstract void onFinishFiltering();
}
