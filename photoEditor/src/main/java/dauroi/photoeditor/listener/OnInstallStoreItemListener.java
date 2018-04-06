package dauroi.photoeditor.listener;

import dauroi.photoeditor.model.ItemPackageInfo;

public interface OnInstallStoreItemListener {
	public void onStartDownloading(ItemPackageInfo item);

	public void onFinishInstalling(ItemPackageInfo item, boolean update);
}
