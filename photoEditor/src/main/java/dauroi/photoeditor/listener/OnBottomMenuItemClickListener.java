package dauroi.photoeditor.listener;

import dauroi.photoeditor.model.ItemInfo;

public interface OnBottomMenuItemClickListener {
	void onMenuItemClick(int position, ItemInfo itemInfo);

	void onDeleteButtonClick(int position, ItemInfo itemInfo);

	void onMenuItemLongClick(int position, ItemInfo itemInfo);
}
