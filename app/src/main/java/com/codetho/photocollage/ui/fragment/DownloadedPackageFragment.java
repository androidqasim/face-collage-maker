package com.codetho.photocollage.ui.fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.adapter.DownloadedPackageAdapter;
import com.codetho.photocollage.listener.OnDownloadedPackageClickListener;
import com.daimajia.swipe.util.Attributes;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.actions.PackageAction;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.utils.DateTimeUtils;
import dauroi.photoeditor.utils.DialogUtils;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.StoreUtils;

/**
 * Created by vanhu_000 on 3/26/2016.
 */
public class DownloadedPackageFragment extends BaseFragment implements OnDownloadedPackageClickListener {
    public static final String EXTRA_PACKAGE_TYPE = "packageType";
    public static final String EXTRA_PACKAGE_NAME = "packageName";
    public static final String EXTRA_PACKAGE_ID = "packageId";
    public static final String EXTRA_PACKAGE_FOLDER = "packageFolder";

    private static final String DEFAULT_BACKGROUND_THUMBNAIL = PhotoUtils.ASSET_PREFIX.concat("background/bg_1.png");
    private static final String DEFAULT_STICKER_THUMBNAIL = PhotoUtils.ASSET_PREFIX.concat("sticker/st_1.png");
    public static final String DEFAULT_BACKGROUND_PACKAGE_TEXT_ID = "default_background_package";
    public static final String DEFAULT_STICKER_PACKAGE_TEXT_ID = "default_sticker_package";
    public static final long DEFAULT_BACKGROUND_PACKAGE_INT_ID = -100;
    public static final long DEFAULT_STICKER_PACKAGE_INT_ID = -99;
    private static final String PREF_NAME = "downloadedPackagePref";
    private static final String OPEN_COUNT_KEY = "openCount";

    private ListView mListView;
    private View mProgressView;

    private List<ItemPackageInfo> mItemPackageInfos = new ArrayList<>();
    private String mPackageType = ItemPackageTable.BACKGROUND_TYPE;
    private DownloadedPackageAdapter mPackageAdapter;
    private Parcelable mListViewState;

    @Override
    public void onPause() {
        // Save ListView mListViewState @ onPause
        if (mListView != null)
            mListViewState = mListView.onSaveInstanceState();
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloaded_package, container, false);
        mPackageType = getArguments().getString(EXTRA_PACKAGE_TYPE);
        if (mPackageType == null) {
            mPackageType = ItemPackageTable.BACKGROUND_TYPE;
        }
        if (ItemPackageTable.BACKGROUND_TYPE.equals(mPackageType)) {
            setTitle(R.string.background);
        } else {
            setTitle(R.string.sticker);
        }

        mListView = (ListView) view.findViewById(R.id.listView);
        mProgressView = view.findViewById(R.id.progressBar);
        //show guide
        final View guideView = view.findViewById(R.id.guideView);
        SharedPreferences pref = mActivity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int count = pref.getInt(OPEN_COUNT_KEY, 0);
        if (count > 3) {
            guideView.setVisibility(View.GONE);
        } else {
            count++;
            pref.edit().putInt(OPEN_COUNT_KEY, count).commit();
        }

        loadData();

        return view;
    }

    private void loadData() {
        AsyncTask<Void, Void, List<ItemPackageInfo>> task = new AsyncTask<Void, Void, List<ItemPackageInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressView.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<ItemPackageInfo> doInBackground(Void... params) {
                ItemPackageTable table = new ItemPackageTable(mActivity);
                List<ItemPackageInfo> result = table.getRows(mPackageType);
                for (ItemPackageInfo info : result) {
                    PackageAction.setAbsoluteBackgroundPath(info);
                }
                //add default package
                ItemPackageInfo defaultPackage = new ItemPackageInfo();
                if (ItemPackageTable.BACKGROUND_TYPE.equalsIgnoreCase(mPackageType)) {
                    defaultPackage.setIdString(DEFAULT_BACKGROUND_PACKAGE_TEXT_ID);
                    defaultPackage.setTitle(getString(R.string.default_backgrounds));
                    defaultPackage.setThumbnail(DEFAULT_BACKGROUND_THUMBNAIL);
                    defaultPackage.setId(DEFAULT_BACKGROUND_PACKAGE_INT_ID);
                    defaultPackage.setType(mPackageType);
                } else {
                    defaultPackage.setIdString(DEFAULT_STICKER_PACKAGE_TEXT_ID);
                    defaultPackage.setTitle(getString(R.string.default_stickers));
                    defaultPackage.setThumbnail(DEFAULT_STICKER_THUMBNAIL);
                    defaultPackage.setId(DEFAULT_STICKER_PACKAGE_INT_ID);
                    defaultPackage.setType(mPackageType);
                }
                defaultPackage.setLastModified(DateTimeUtils.getCurrentDateTime());
                result.add(0, defaultPackage);
                return result;
            }

            @Override
            protected void onPostExecute(List<ItemPackageInfo> result) {
                super.onPostExecute(result);
                if (!already()) {
                    return;
                }

                mProgressView.setVisibility(View.GONE);
                if (result != null) {
                    mItemPackageInfos.clear();
                    mItemPackageInfos.addAll(result);
                    mPackageAdapter = new DownloadedPackageAdapter(getActivity(), mItemPackageInfos, DownloadedPackageFragment.this);
                    mPackageAdapter.setMode(Attributes.Mode.Single);
                    mListView.setAdapter(mPackageAdapter);
                    if (mListViewState != null) {
                        mListView.onRestoreInstanceState(mListViewState);
                    }
                }
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDeleteButtonClick(final int position, final ItemPackageInfo info) {
        if (info.getIdString().equals(DEFAULT_STICKER_PACKAGE_TEXT_ID) || info.getIdString().equals(DEFAULT_BACKGROUND_PACKAGE_TEXT_ID)) {
            Toast.makeText(mActivity, getString(R.string.warning_uninstall_default_package), Toast.LENGTH_SHORT).show();
        } else {
            DialogUtils.showCoolConfirmDialog(getActivity(), R.string.app_name,
                    R.string.photo_editor_confirm_uninstall, new DialogUtils.ConfirmDialogOnClickListener() {

                        @Override
                        public void onOKButtonOnClick() {
                            StoreUtils.uninstallItemPackage(getActivity(), info);
                            mPackageAdapter.remove(info);
                            //send statistics
                            //log
                            if (info.getTitle() != null && info.getType() != null) {
                                Bundle bundle = new Bundle();
                                String msg = "uninstall/".concat(info.getTitle()).concat("-").concat(info.getType());
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, msg);
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            }
                        }

                        @Override
                        public void onCancelButtonOnClick() {

                        }
                    });
        }
    }

    @Override
    public void onItemClick(int position, ItemPackageInfo info) {
        Bundle data = new Bundle();
        data.putLong(EXTRA_PACKAGE_ID, info.getId());
        data.putString(EXTRA_PACKAGE_NAME, info.getTitle());
        data.putString(EXTRA_PACKAGE_TYPE, info.getType());
        data.putString(EXTRA_PACKAGE_FOLDER, info.getFolder());
        ItemPackageDetailFragment fragment = new ItemPackageDetailFragment();
        fragment.setArguments(data);
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
