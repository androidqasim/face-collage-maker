package com.codetho.photocollage.listener;

import dauroi.photoeditor.model.ItemPackageInfo;

/**
 * Created by vanhu_000 on 3/7/2016.
 */
public interface OnDownloadedPackageClickListener {
    void onDeleteButtonClick(int position, ItemPackageInfo info);

    void onItemClick(int position, ItemPackageInfo info);
}
