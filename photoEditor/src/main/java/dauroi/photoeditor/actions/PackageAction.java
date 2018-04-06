package dauroi.photoeditor.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import dauroi.photoeditor.R;
import dauroi.photoeditor.adapter.CustomMenuAdapter;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.horizontalListView.widget.HListView;
import dauroi.photoeditor.listener.OnBottomMenuItemClickListener;
import dauroi.photoeditor.listener.OnInstallStoreItemListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.ui.activity.StoreActivity;
import dauroi.photoeditor.utils.DialogUtils;
import dauroi.photoeditor.utils.StoreUtils;
import dauroi.photoeditor.utils.TempDataContainer;
import dauroi.photoeditor.utils.Utils;

@SuppressLint("UseSparseArrays")
public abstract class PackageAction extends BaseAction
        implements OnBottomMenuItemClickListener, OnInstallStoreItemListener {
    protected static final int DEFAULT_CROP_SELECTED_ITEM_INDEX = 3;

    protected HListView mListView;
    protected CustomMenuAdapter mMenuAdapter;
    protected List<ItemInfo> mMenuItems;
    protected int mCurrentPosition = 0;
    protected long mCurrentPackageId = 0;
    protected String mCurrentPackageFolder;
    protected Map<Long, Integer> mSelectedItemIndexes;
    protected Map<Long, Point> mListViewPositions;
    protected View mBackButton;
    private String mPackageType;

    public PackageAction(ImageProcessingActivity activity, String packageType) {
        super(activity);
        createMenuList();
        mPackageType = packageType;
        if (mPackageType != null && ItemPackageTable.CROP_TYPE.equalsIgnoreCase(mPackageType)) {
            mCurrentPosition = DEFAULT_CROP_SELECTED_ITEM_INDEX;
        }
    }

    public static void setAbsoluteBackgroundPath(ItemPackageInfo info) {
        info.setShowingType(ItemInfo.PACKAGE_ITEM_TYPE);
        String baseFolder = Utils.CROP_FOLDER;
        if (ItemPackageTable.FRAME_TYPE.equalsIgnoreCase(info.getType())) {
            baseFolder = Utils.FRAME_FOLDER;
        } else if (ItemPackageTable.FILTER_TYPE.equalsIgnoreCase(info.getType())) {
            baseFolder = Utils.FILTER_FOLDER;
        } else if (ItemPackageTable.CROP_TYPE.equalsIgnoreCase(info.getType())) {
            baseFolder = Utils.CROP_FOLDER;
        } else if (ItemPackageTable.BACKGROUND_TYPE.equalsIgnoreCase(info.getType())) {
            baseFolder = Utils.BACKGROUND_FOLDER;
        } else if (ItemPackageTable.STICKER_TYPE.equalsIgnoreCase(info.getType())) {
            baseFolder = Utils.STICKER_FOLDER;
        }

        info.setThumbnail(baseFolder.concat("/").concat(info.getFolder()).concat("/")
                .concat(info.getThumbnail()));
        if (info.getSelectedThumbnail() != null && info.getSelectedThumbnail().length() > 0) {
            info.setSelectedThumbnail(baseFolder.concat("/").concat(info.getFolder()).concat("/")
                    .concat(info.getSelectedThumbnail()));
        }
    }

    protected abstract void selectNormalItem(final int position);

    protected abstract List<? extends ItemInfo> loadNormalItems(final long packageId, final String packageFolder);

    @Override
    public void onFinishInstalling(ItemPackageInfo info, boolean update) {
        if (mPackageType != null && info.getType().equalsIgnoreCase(mPackageType)) {
            info.setShowingType(ItemInfo.PACKAGE_ITEM_TYPE);
            String baseFolder = Utils.CROP_FOLDER;
            if (ItemPackageTable.FRAME_TYPE.equalsIgnoreCase(mPackageType)) {
                baseFolder = Utils.FRAME_FOLDER;
            } else if (ItemPackageTable.FILTER_TYPE.equalsIgnoreCase(mPackageType)) {
                baseFolder = Utils.FILTER_FOLDER;
            } else if (ItemPackageTable.CROP_TYPE.equalsIgnoreCase(mPackageType)) {
                baseFolder = Utils.CROP_FOLDER;
            } else if (ItemPackageTable.BACKGROUND_TYPE.equalsIgnoreCase(mPackageType)) {
                baseFolder = Utils.BACKGROUND_FOLDER;
            } else if (ItemPackageTable.STICKER_TYPE.equalsIgnoreCase(mPackageType)) {
                baseFolder = Utils.STICKER_FOLDER;
            }

            info.setThumbnail(baseFolder.concat("/").concat(info.getFolder()).concat("/")
                    .concat(info.getThumbnail()));
            if (info.getSelectedThumbnail() != null && info.getSelectedThumbnail().length() > 0)
                info.setSelectedThumbnail(baseFolder.concat("/").concat(info.getFolder()).concat("/")
                        .concat(info.getSelectedThumbnail()));
            boolean installed = false;
            for (ItemInfo itemInfo : mMenuItems)
                if (itemInfo instanceof ItemPackageInfo
                        && ((ItemPackageInfo) itemInfo).getIdString().equals(info.getIdString())) {
                    itemInfo.setTitle(info.getTitle());
                    installed = true;
                    break;
                }

            if (!installed) {
                final int size = mMenuItems.size();
                if (mCurrentPosition == size - 1) {
                    mCurrentPosition = size;
                }
                mMenuItems.add(size - 1, info);
            }

            mMenuAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStartDownloading(ItemPackageInfo item) {

    }

    @Override
    public void onMenuItemClick(int position, ItemInfo itemInfo) {
        if (mMenuAdapter.isShaking()) {
            mMenuAdapter.setShaking(false);
        } else if (mCurrentPosition != position || mMenuItems.get(position).getShowingType() == ItemInfo.ADD_ITEM_TYPE
                || mMenuItems.get(position).getShowingType() == ItemInfo.PACKAGE_ITEM_TYPE) {
            mMenuItems.get(mCurrentPosition).setSelected(false);
            if (mCurrentPosition < position) {
                if (position < mMenuItems.size() - 1) {
                    mListView.smoothScrollToPosition(position + 1);
                } else {
                    mListView.smoothScrollToPosition(position);
                }
            } else {
                if (position > 0) {
                    mListView.smoothScrollToPosition(position - 1);
                } else {
                    mListView.smoothScrollToPosition(position);
                }
            }
            mMenuItems.get(position).setSelected(true);
            mMenuAdapter.notifyDataSetChanged();
            selectItem(position);
            onClicked();
        }
    }

    @Override
    public void onDeleteButtonClick(final int position, final ItemInfo itemInfo) {
        DialogUtils.showCoolConfirmDialog(mActivity, R.string.photo_editor_app_name,
                R.string.photo_editor_confirm_uninstall, new DialogUtils.ConfirmDialogOnClickListener() {

                    @Override
                    public void onOKButtonOnClick() {
                        StoreUtils.uninstallItemPackage(mActivity, (ItemPackageInfo) itemInfo);
                        mMenuItems.remove(itemInfo);
                        mMenuAdapter.setShaking(false);
                    }

                    @Override
                    public void onCancelButtonOnClick() {
                        mMenuAdapter.setShaking(false);
                    }
                });

    }

    @Override
    public void onMenuItemLongClick(int position, ItemInfo itemInfo) {
        mMenuAdapter.setShaking(true);
    }

    @Override
    protected void onInit() {
        super.onInit();
        mSelectedItemIndexes = new HashMap<Long, Integer>();
        mListViewPositions = new HashMap<Long, Point>();
    }

    @Override
    public void attach() {
        super.attach();
        TempDataContainer.getInstance().getOnInstallStoreItemListeners().add(this);
        if (mListView != null) {
            if (mMenuItems == null || mMenuItems.isEmpty()) {
                loadData(mCurrentPackageId, mCurrentPackageFolder);
            }

            ItemInfo info = mMenuItems.get(mCurrentPosition);
            if (info.getShowingType() != ItemInfo.ADD_ITEM_TYPE) {
                selectItem(mCurrentPosition);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TempDataContainer.getInstance().getOnInstallStoreItemListeners().remove(this);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        if (mPackageType != null && mPackageType.length() > 0)
            loadData(mCurrentPackageId, mCurrentPackageFolder);
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        super.saveInstanceState(bundle);
        saveCurrentInfos(bundle, "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mCurrentPosition"),
                "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mPackageId"),
                "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mCurrentPackageFolder"));
        saveMaps(bundle, "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mSelectedItemIndexes"),
                "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mListViewPositions"));
    }

    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
        restoreCurrentInfos(bundle, "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mCurrentPosition"),
                "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mPackageId"),
                "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mCurrentPackageFolder"));
        restoreMaps(bundle, "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mSelectedItemIndexes"),
                "dauroi.photoeditor.actions.".concat(getActionName()).concat(".mListViewPositions"));
    }

    private void createMenuList() {
        mListView = (HListView) mRootActionView.findViewById(R.id.bottomListView);
        if (mListView != null) {
            mMenuItems = new ArrayList<ItemInfo>();
            mMenuAdapter = new CustomMenuAdapter(mActivity, mMenuItems, true);
            mMenuAdapter.setListener(this);
            mListView.setAdapter(mMenuAdapter);
        }
        // Installed items
        mBackButton = mRootActionView.findViewById(R.id.backButton);
        if (mBackButton != null)
            mBackButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    backNormalPackage();
                }
            });

    }

    private void backNormalPackage() {
        Point point = new Point();
        if (mListView != null) {
            point.x = mListView.getFirstVisiblePosition();
            if (mListView.getChildCount() > 0)
                point.y = mListView.getChildAt(0).getLeft();
        } else {
            point.x = 0;
            point.y = 0;
        }

        mListViewPositions.put(mCurrentPackageId, point);

        mBackButton.setVisibility(View.GONE);
        mCurrentPackageId = 0;
        mCurrentPackageFolder = null;
        loadData(mCurrentPackageId, mCurrentPackageFolder);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectItem(int position) {
        // save current position
        Point point = new Point();
        point.x = position;
        if (mListView != null) {
            point.x = mListView.getFirstVisiblePosition();
            if (mListView.getChildCount() > 0)
                point.y = mListView.getChildAt(0).getLeft();
        } else {
            point.y = 0;
        }
        mListViewPositions.put(mCurrentPackageId, point);
        final ItemInfo info = mMenuItems.get(position);
        if (info.getShowingType() == ItemInfo.NORMAL_ITEM_TYPE) {
            mSelectedItemIndexes.put(mCurrentPackageId, position);
        }

        if (info.getShowingType() == ItemInfo.ADD_ITEM_TYPE) {
            Intent intent = new Intent(mActivity, StoreActivity.class);
            intent.putExtra(StoreItem.EXTRA_ITEM_TYPE_KEY, mPackageType);
            mActivity.startActivity(intent);
        } else if (info.getShowingType() == ItemInfo.PACKAGE_ITEM_TYPE) {
            ItemPackageInfo itemPackageInfo = (ItemPackageInfo) info;
            mCurrentPackageId = itemPackageInfo.getId();
            mCurrentPackageFolder = itemPackageInfo.getFolder();
            mCurrentPosition = 0;
            loadData(mCurrentPackageId, mCurrentPackageFolder);
        } else {
            selectNormalItem(position);
            mCurrentPosition = position;
        }
    }

    /**
     * If frameFolder is null then load frames from resources folder
     */
    private void loadData(long packageId, final String packageFolder) {
        if (mPackageType == null || mPackageType.length() < 1) {
            return;
        }

        if (mMenuItems == null) {
            mMenuItems = new ArrayList<ItemInfo>();
        } else {
            mMenuItems.clear();
        }

        List<? extends ItemInfo> itemInfos = loadNormalItems(packageId, packageFolder);
        if (itemInfos != null && itemInfos.size() > 0)
            mMenuItems.addAll(itemInfos);
        if (packageId < 1) {
            ItemPackageTable itemPackageTable = new ItemPackageTable(mActivity);
            if (mPackageType != null && mPackageType.length() > 0) {
                List<ItemPackageInfo> itemPackageInfos = itemPackageTable.getRows(mPackageType);
                for (ItemPackageInfo info : itemPackageInfos) {
                    setAbsoluteBackgroundPath(info);
                }
                mMenuItems.addAll(itemPackageInfos);
            }

            final ItemInfo addItem = new ItemInfo();
            addItem.setShowingType(ItemInfo.ADD_ITEM_TYPE);
            mMenuItems.add(addItem);
            if (mBackButton != null)
                mBackButton.setVisibility(View.GONE);
        } else {
            if (mBackButton != null)
                mBackButton.setVisibility(View.VISIBLE);
        }
        // restore old position
        final Point position = mListViewPositions.get(packageId);
        Integer currentIndex = mSelectedItemIndexes.get(packageId);
        if (currentIndex != null) {
            mCurrentPosition = currentIndex;
        } else {
            mCurrentPosition = 0;
            if (mPackageType != null && ItemPackageTable.CROP_TYPE.equalsIgnoreCase(mPackageType)) {
                mCurrentPosition = DEFAULT_CROP_SELECTED_ITEM_INDEX;
            }
        }

        if (mCurrentPosition >= mMenuItems.size()) {
            if (packageId == 0 && mPackageType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                mCurrentPosition = DEFAULT_CROP_SELECTED_ITEM_INDEX;
            } else {
                mCurrentPosition = 0;
            }
        }

        mMenuItems.get(mCurrentPosition).setSelected(false);
        if (mMenuItems.get(mCurrentPosition).getShowingType() == ItemInfo.NORMAL_ITEM_TYPE) {
            selectItem(mCurrentPosition);
        } else {
            selectItem(0);
        }

        mMenuItems.get(mCurrentPosition).setSelected(true);
        if (mMenuAdapter != null)
            mMenuAdapter.notifyDataSetChanged();

        if (mListView != null)
            mListView.post(new Runnable() {

                @Override
                public void run() {
                    if (position != null) {
                        mListView.setSelectionFromLeft(position.x, position.y);
                    } else {
                        mListView.setSelection(0);
                    }
                }
            });
    }

    protected void saveCurrentInfos(final Bundle bundle, final String currentPositionKey, final String packageIdKey,
                                    final String currentPackageFolderKey) {
        bundle.putInt(currentPositionKey, mCurrentPosition);
        bundle.putLong(packageIdKey, mCurrentPackageId);
        bundle.putString(currentPackageFolderKey, mCurrentPackageFolder);
    }

    protected void restoreCurrentInfos(final Bundle bundle, final String currentPositionKey, final String packageIdKey,
                                       final String currentPackageFolderKey) {
        mCurrentPosition = bundle.getInt(currentPositionKey, mCurrentPosition);
        mCurrentPackageId = bundle.getLong(packageIdKey, mCurrentPackageId);
        mCurrentPackageFolder = bundle.getString(currentPackageFolderKey);
    }

    protected void restoreMaps(final Bundle bundle, final String selectedItemIndexesKey,
                               final String listViewPositionsKey) {
        // selected item indexes
        long[] keys = bundle.getLongArray(selectedItemIndexesKey.concat("_Keys"));
        int[] values = bundle.getIntArray(selectedItemIndexesKey.concat("_Values"));
        if (keys != null && values != null && keys.length == values.length) {
            mSelectedItemIndexes = new HashMap<Long, Integer>();
            for (int idx = 0; idx < keys.length; idx++) {
                mSelectedItemIndexes.put(keys[idx], values[idx]);
            }
        }

        keys = bundle.getLongArray(listViewPositionsKey.concat("_Keys"));
        Parcelable[] pointValues = bundle.getParcelableArray(listViewPositionsKey.concat("_Values"));
        if (keys != null && pointValues != null && keys.length == pointValues.length) {
            mListViewPositions = new HashMap<Long, Point>();
            for (int idx = 0; idx < keys.length; idx++) {
                mListViewPositions.put(keys[idx], (Point) pointValues[idx]);
            }
        }
    }

    protected void saveMaps(final Bundle bundle, final String selectedItemIndexesKey,
                            final String listViewPositionsKey) {
        // put selected item indexes
        Set<Long> keyset = mSelectedItemIndexes.keySet();
        long[] keys = new long[keyset.size()];
        int idx = 0;
        Iterator<Long> it = keyset.iterator();

        while (it.hasNext()) {
            keys[idx] = it.next();
            idx++;
        }

        bundle.putLongArray(selectedItemIndexesKey.concat("_Keys"), keys);

        final Collection<Integer> valueCollection = mSelectedItemIndexes.values();
        int[] values = new int[valueCollection.size()];
        Iterator<Integer> vit = valueCollection.iterator();
        idx = 0;
        while (vit.hasNext()) {
            values[idx] = vit.next();
            idx++;
        }

        bundle.putIntArray(selectedItemIndexesKey.concat("_Values"), values);
        // put list view positions
        keyset = mListViewPositions.keySet();
        keys = new long[keyset.size()];
        idx = 0;
        it = keyset.iterator();
        while (it.hasNext()) {
            keys[idx] = it.next();
            idx++;
        }

        bundle.putLongArray(listViewPositionsKey.concat("_Keys"), keys);

        final Collection<Point> pointCollection = mListViewPositions.values();
        Point[] pointValues = new Point[pointCollection.size()];
        Iterator<Point> pointIt = pointCollection.iterator();
        idx = 0;
        while (pointIt.hasNext()) {
            pointValues[idx] = pointIt.next();
            idx++;
        }

        bundle.putParcelableArray(listViewPositionsKey.concat("_Values"), pointValues);
    }

}
