package dauroi.photoeditor.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.R;
import dauroi.photoeditor.adapter.CustomMenuAdapter;
import dauroi.photoeditor.api.FileService;
import dauroi.photoeditor.api.StoreItemService;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.api.response.StoreItem.Effect;
import dauroi.photoeditor.config.AppConfig;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.horizontalListView.widget.HListView;
import dauroi.photoeditor.listener.OnInstallStoreItemListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.model.Language;
import dauroi.photoeditor.utils.DialogUtils;
import dauroi.photoeditor.utils.ProfileCache;
import dauroi.photoeditor.utils.StoreUtils;
import dauroi.photoeditor.utils.TempDataContainer;

public class StoreItemDetailActivity extends BaseStoreActivity implements OnInstallStoreItemListener {
    private ImageView mSampleView;
    private HListView mListView;
    private TextView mDownloadView;
    private View mProgressView;
    private View mDownloadLayout;
    private StoreItem mStoreItem;
    private CustomMenuAdapter mMenuAdapter;
    private List<ItemInfo> mMenuItems;
    private int mCurrentPosition = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_editor_activity_store_item_detail);
        mStoreItem = getIntent().getParcelableExtra(StoreActivity.EXTRA_STORE_ITEM);
        if (savedInstanceState != null) {
            mStoreItem = savedInstanceState.getParcelable("mStoreItem");
        }

        final TextView titleView = (TextView) findViewById(R.id.titleView);
        titleView.setText(mStoreItem.getTitle());
        mProgressView = findViewById(R.id.progressBar);
        mSampleView = (ImageView) findViewById(R.id.sampleView);
        mListView = (HListView) findViewById(R.id.itemListView);
        mDownloadView = (TextView) findViewById(R.id.downloadView);
        mDownloadLayout = findViewById(R.id.downloadLayout);
        mDownloadLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mStoreItem.getDownloadStatus() == StoreItem.STATUS_ONLINE) {
                    purchaseItem(mStoreItem);
                    report("download/" + mStoreItem.getTitle());
                }
                if (getAdCreator() != null)
                    getAdCreator().showGoogleInterstitialAd();
            }
        });

        final View backView = findViewById(R.id.backView);
        backView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final View uninstallView = findViewById(R.id.uninstallView);
        uninstallView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogUtils.showCoolConfirmDialog(StoreItemDetailActivity.this, R.string.photo_editor_app_name,
                        R.string.photo_editor_confirm_uninstall, new DialogUtils.ConfirmDialogOnClickListener() {

                            @Override
                            public void onOKButtonOnClick() {
                                ItemPackageTable table = new ItemPackageTable(StoreItemDetailActivity.this);
                                ItemPackageInfo info = table.getRowWithStoreId(mStoreItem.getIdString());
                                if (info != null)
                                    StoreUtils.uninstallItemPackage(StoreItemDetailActivity.this, info);
                                mStoreItem.setDownloadStatus(StoreItem.STATUS_ONLINE);
                                uninstallView.setVisibility(View.GONE);
                                changeStoreItemStatus(mStoreItem.getDownloadStatus());
                            }

                            @Override
                            public void onCancelButtonOnClick() {

                            }
                        });

            }
        });

        changeStoreItemStatus(mStoreItem.getDownloadStatus());

        if (mStoreItem.getDownloadStatus() == StoreItem.STATUS_DOWNLOADED) {
            uninstallView.setVisibility(View.VISIBLE);
        } else {
            uninstallView.setVisibility(View.GONE);
        }

        TempDataContainer.getInstance().getOnInstallStoreItemListeners().add(this);

        loadData();
        //report to server
        if (savedInstanceState == null) {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        StoreItemService.view(ProfileCache.getToken(StoreItemDetailActivity.this), mStoreItem.getIdString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            };

            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TempDataContainer.getInstance().getOnInstallStoreItemListeners().remove(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mStoreItem", mStoreItem);
    }

    private void report(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "StoreItemDetail");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void changeStoreItemStatus(int downloadStatus) {
        if (downloadStatus == StoreItem.STATUS_ONLINE) {
            if (mStoreItem.getPrice() > 0) {
                mDownloadView.setText(mStoreItem.getPrice() + "$");
            } else {
                mDownloadView.setText(R.string.photo_editor_free);
            }
            mDownloadView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_editor_ic_downloaded, 0, 0, 0);
            mDownloadLayout.setBackgroundColor(getResources().getColor(R.color.photo_editor_price_view_normal));
        } else if (downloadStatus == StoreItem.STATUS_DOWNLOADING) {
            mDownloadView.setText(R.string.photo_editor_downloading);
            mDownloadView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_editor_ic_downloaded, 0, 0, 0);
            mDownloadLayout.setBackgroundColor(getResources().getColor(R.color.photo_editor_price_view_normal));
        } else {
            mDownloadView.setText(R.string.photo_editor_used);
            mDownloadView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_editor_ic_ok_white, 0, 0, 0);
            mDownloadLayout.setBackgroundColor(getResources().getColor(R.color.photo_editor_price_view_use));
        }
    }

    public void loadData() {
        // show menu item
        final String lang = AppConfig.getLanguage();
        mMenuItems = new ArrayList<ItemInfo>();
        for (Effect effect : mStoreItem.getEffects()) {
            ItemInfo item = new ItemInfo();
            String langValue = AppConfig.DEFAULT_LANGUAGE;
            for (Language language : effect.getNames()) {
                if (language.getName().equalsIgnoreCase(lang)) {
                    langValue = language.getValue();
                    break;
                }
            }
            item.setTitle(langValue);
            item.setThumbnail(FileService.getUploadedPath(null, effect.getThumbnail(), FileService.IMAGE_TYPE));
            if (effect.getSelectedThumbnail() != null && effect.getSelectedThumbnail().length() > 0)
                item.setSelectedThumbnail(FileService.getUploadedPath(null, effect.getSelectedThumbnail(), FileService.IMAGE_TYPE));
            item.setShowingType(ItemInfo.NORMAL_ITEM_TYPE);
            item.setSelected(false);
            mMenuItems.add(item);
        }
        if (mMenuItems.size() > 0) {
            mMenuItems.get(0).setSelected(true);
        }
        mMenuAdapter = new CustomMenuAdapter(this, mMenuItems, true);
        mListView.setAdapter(mMenuAdapter);
        mListView.setOnItemClickListener(
                new dauroi.photoeditor.horizontalListView.widget.AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(dauroi.photoeditor.horizontalListView.widget.AdapterView<?> parent,
                                            View view, int position, long id) {
                        if (mCurrentPosition != position || position == mMenuItems.size() - 1) {
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
                            mCurrentPosition = position;
                        }
                    }

                });
        // show sample
        if (mMenuItems.size() > 0) {
            selectItem(0);
        }
    }

    private void selectItem(final int position) {
        if (mStoreItem.getEffects() == null || position < 0 || position >= mStoreItem.getEffects().length) {
            return;
        }
        mProgressView.setVisibility(View.VISIBLE);
        String path = mStoreItem.getEffects()[position].getImage();
        if (!path.startsWith("http://") && !path.startsWith("https://")) {
            path = FileService.getUploadedPath(null, path, FileService.IMAGE_TYPE);
        }
        final String imagePath = path;
        Glide.with(this).load(imagePath).listener(new RequestListener<String, GlideDrawable>() {

            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                       boolean isFirstResource) {
                mProgressView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                mStoreItem.getEffects()[position].setImage(imagePath);
                mProgressView.setVisibility(View.GONE);
                return false;
            }
        }).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(mSampleView);
    }

    @Override
    public void onFinishInstalling(ItemPackageInfo item, boolean update) {
        if (item.getIdString().equals(mStoreItem.getIdString())) {
            mStoreItem.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
            changeStoreItemStatus(mStoreItem.getDownloadStatus());
        }
    }

    @Override
    public void onStartDownloading(ItemPackageInfo item) {
        if (item.getIdString().equals(mStoreItem.getIdString())) {
            mDownloadView.setText(R.string.photo_editor_downloading);
        }
    }
}
