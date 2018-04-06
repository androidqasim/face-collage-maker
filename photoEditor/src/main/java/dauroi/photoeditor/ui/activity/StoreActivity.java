package dauroi.photoeditor.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.R;
import dauroi.photoeditor.adapter.StoreItemAdapter;
import dauroi.photoeditor.adapter.StoreItemAdapter.OnStoreItemClickListener;
import dauroi.photoeditor.api.StoreItemService;
import dauroi.photoeditor.api.response.ListStoreItemResponse;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.database.DatabaseManager;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.database.table.StoreItemTable;
import dauroi.photoeditor.listener.OnInstallStoreItemListener;
import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.utils.GsonUtils;
import dauroi.photoeditor.utils.TempDataContainer;

public class StoreActivity extends BaseStoreActivity implements OnInstallStoreItemListener {
    static final String LANGUAGE = "en";
    private static final int LIMIT_ITEMS = 50;
    static final String STORE_ITEM_PREF = "storeItemPref";
    public static final String EXTRA_STORE_ITEM = "storeItem";
    static final String BACKGROUND_ITEM_KEY = "backgroundItems";
    static final String STICKER_ITEM_KEY = "stickerItems";
    static final String EFFECT_ITEM_KEY = "effectItems";
    static final String CROP_ITEM_KEY = "cropItems";
    static final String FRAME_ITEM_KEY = "frameItems";

    private int mBackgroundOffset = 0;
    private int mStickerOffset = 0;
    private int mEffectOffset = 0;
    private int mCropOffset = 0;
    private int mFrameOffset = 0;
    private boolean mAllBackgroundLoaded = false;
    private boolean mAllStickerLoaded = false;
    private boolean mAllEffectLoaded = false;
    private boolean mAllCropLoaded = false;
    private boolean mAllFrameLoaded = false;
    //private TextView mPurchaseGuideView;
    private View mRefreshButton;
    private View mCropFrame;
    private View mEffectFrame;
    private View mFrameFrame;
    private View mCropButton;
    private ImageView mCropThumbnailView;
    private TextView mCropNameView;
    private View mEffectButton;
    private ImageView mEffectThumbnailView;
    private TextView mEffectNameView;
    private View mFrameButton;
    private ImageView mFrameThumbnailView;
    private TextView mFrameNameView;
    private ListView mListView;
    private StoreItemAdapter mAdapter;
    private View mProgressView;
    private String mItemType;
    private List<StoreItem> mBackgroundItems = new ArrayList<StoreItem>();
    private List<StoreItem> mStickerItems = new ArrayList<StoreItem>();
    private List<StoreItem> mStoreItems = new ArrayList<StoreItem>();
    private List<StoreItem> mEffectItems = new ArrayList<StoreItem>();
    private List<StoreItem> mCropItems = new ArrayList<StoreItem>();
    private List<StoreItem> mFrameItems = new ArrayList<StoreItem>();
    private List<StoreItem> mShowingItems = new ArrayList<StoreItem>();

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_editor_activity_store);

        if (!DatabaseManager.getInstance(this).isDbFileExisted()) {
            DatabaseManager.getInstance(this).createDb();
        } else {
            boolean isOpen = DatabaseManager.getInstance(this).openDb();
            ALog.d("StoreActivity", "onCreate, database isOpen=" + isOpen);
        }

        mRefreshButton = findViewById(R.id.refreshButton);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mItemType != null && mItemType.length() > 0) {
                    if (mItemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
                        mStickerOffset = 0;
                        mAllStickerLoaded = false;
                    } else if (mItemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                        mFrameOffset = 0;
                        mAllFrameLoaded = false;
                    } else if (mItemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                        mEffectOffset = 0;
                        mAllEffectLoaded = false;
                    } else if (mItemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                        mCropOffset = 0;
                        mAllCropLoaded = false;
                    } else {
                        mBackgroundOffset = 0;
                        mAllBackgroundLoaded = false;
                    }
                }
                asyncLoadStoreItems(mItemType);
            }
        });

        mCropFrame = findViewById(R.id.cropFrame);
        mFrameFrame = findViewById(R.id.frameFrame);
        mEffectFrame = findViewById(R.id.effectFrame);
        mCropButton = findViewById(R.id.cropView);
        mCropThumbnailView = (ImageView) findViewById(R.id.cropThumbnailView);
        mCropNameView = (TextView) findViewById(R.id.cropNameView);
        mCropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mItemType = ItemPackageTable.CROP_TYPE;
                selectCategory(mCropButton);
                mShowingItems.clear();
                mShowingItems.addAll(mCropItems);
                mAdapter.notifyDataSetChanged();
                if (mCropItems == null || mCropItems.isEmpty() || mCropOffset < 1) {
                    asyncLoadStoreItems(mItemType);
                }
            }
        });

        mEffectButton = findViewById(R.id.effectView);
        mEffectThumbnailView = (ImageView) findViewById(R.id.effectThumbnailView);
        mEffectNameView = (TextView) findViewById(R.id.effectNameView);
        mEffectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mItemType = ItemPackageTable.FILTER_TYPE;
                selectCategory(mEffectButton);
                mShowingItems.clear();
                mShowingItems.addAll(mEffectItems);
                mAdapter.notifyDataSetChanged();
                if (mEffectItems == null || mEffectItems.isEmpty() || mEffectOffset < 1) {
                    asyncLoadStoreItems(mItemType);
                }
            }
        });

        mFrameButton = findViewById(R.id.frameView);
        mFrameThumbnailView = (ImageView) findViewById(R.id.frameThumbnailView);
        mFrameNameView = (TextView) findViewById(R.id.frameNameView);
        mFrameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mItemType = ItemPackageTable.FRAME_TYPE;
                selectCategory(mFrameButton);
                mShowingItems.clear();
                mShowingItems.addAll(mFrameItems);
                mAdapter.notifyDataSetChanged();
                if (mFrameItems == null || mFrameItems.isEmpty() || mFrameOffset < 1) {
                    asyncLoadStoreItems(mItemType);
                }
            }
        });

        mListView = (ListView) findViewById(R.id.itemList);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressView = findViewById(R.id.progressBar);
        mItemType = getIntent().getStringExtra(StoreItem.EXTRA_ITEM_TYPE_KEY);

        mAdapter = new StoreItemAdapter(this, mShowingItems, new OnStoreItemClickListener() {

            @Override
            public void onStoreItemClick(StoreItem item) {
                Intent intent = new Intent(StoreActivity.this, StoreItemDetailActivity.class);
                intent.putExtra(EXTRA_STORE_ITEM, item);
                startActivity(intent);
            }

            @Override
            public void onPriceButtonClick(StoreItem item) {
                if (item.getDownloadStatus() == StoreItem.STATUS_ONLINE) {
                    purchaseItem(item);
                }
            }
        });

        mAdapter.setEndListListener(new StoreItemAdapter.OnEndListListener() {
            @Override
            public void onEndList(int position) {
                ALog.d("StoreFragment", "onEndList, position=" + position);
                if (mItemType != null && mItemType.length() > 0) {
                    asyncLoadStoreItems(mItemType);
                }
            }
        });

        mListView.setAdapter(mAdapter);
        mPref = getSharedPreferences(STORE_ITEM_PREF, Context.MODE_PRIVATE);
        TempDataContainer.getInstance().getOnInstallStoreItemListeners().add(this);

        loadOfflineItems();
        asyncLoadStoreItems(mItemType);
        //Admob ad
        if (getAdView() != null) {
            final ViewGroup adLayout = (ViewGroup) findViewById(R.id.adsLayout);
            adLayout.addView(getAdView());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void selectCategory(View categoryView) {
        mCropThumbnailView.setImageResource(R.drawable.photo_editor_ic_crop_normal);
        mCropNameView.setTextColor(getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
        mFrameThumbnailView.setImageResource(R.drawable.photo_editor_ic_frame_normal);
        mFrameNameView.setTextColor(getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
        mEffectThumbnailView.setImageResource(R.drawable.photo_editor_ic_effect_normal);
        mEffectNameView.setTextColor(getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
        if (categoryView == mCropButton) {
            mCropThumbnailView.setImageResource(R.drawable.photo_editor_ic_crop_pressed);
            mCropNameView.setTextColor(getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
        } else if (categoryView == mFrameButton) {
            mFrameThumbnailView.setImageResource(R.drawable.photo_editor_ic_frame_pressed);
            mFrameNameView.setTextColor(getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
        } else {
            mEffectThumbnailView.setImageResource(R.drawable.photo_editor_ic_effect_pressed);
            mEffectNameView.setTextColor(getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
        }
    }

    private void categorizeStoreItems() {
        mEffectItems.clear();
        mCropItems.clear();
        mFrameItems.clear();
        for (StoreItem storeItem : mStoreItems) {
            if (storeItem.getType().equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                mEffectItems.add(storeItem);
            } else if (storeItem.getType().equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                mFrameItems.add(storeItem);
            } else if (storeItem.getType().equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                mCropItems.add(storeItem);
            }
        }
    }

    private void showCategoryTypeButtons() {
        if (mEffectItems == null || mEffectItems.isEmpty()) {
            mEffectFrame.setVisibility(View.GONE);
        } else {
            mEffectFrame.setVisibility(View.VISIBLE);
        }

        if (mCropItems == null || mCropItems.isEmpty()) {
            mCropFrame.setVisibility(View.GONE);
        } else {
            mCropFrame.setVisibility(View.VISIBLE);
        }

        if (mFrameItems == null || mFrameItems.isEmpty()) {
            mFrameFrame.setVisibility(View.GONE);
        } else {
            mFrameFrame.setVisibility(View.VISIBLE);
        }
    }


    private void loadOfflineItems() {
        Gson gson = GsonUtils.createAndroidStyleGson();
        Type collectionType = new TypeToken<List<StoreItem>>() {
        }.getType();
        //backgrounds
        String text = mPref.getString(BACKGROUND_ITEM_KEY, null);
        if (text != null && text.length() > 0) {
            List<StoreItem> items = gson.fromJson(text, collectionType);
            if (items != null && items.size() > 0) {
                mBackgroundItems.addAll(items);
            }
        }
        //stickers
        text = mPref.getString(STICKER_ITEM_KEY, null);
        if (text != null && text.length() > 0) {
            List<StoreItem> items = gson.fromJson(text, collectionType);
            if (items != null && items.size() > 0) {
                mStickerItems.addAll(items);
            }
        }
        //frame
        text = mPref.getString(FRAME_ITEM_KEY, null);
        if (text != null && text.length() > 0) {
            List<StoreItem> items = gson.fromJson(text, collectionType);
            if (items != null && items.size() > 0) {
                mFrameItems.addAll(items);
            }
        }
        //crop
        text = mPref.getString(CROP_ITEM_KEY, null);
        if (text != null && text.length() > 0) {
            List<StoreItem> items = gson.fromJson(text, collectionType);
            if (items != null && items.size() > 0) {
                mCropItems.addAll(items);
            }
        }
        //filter
        text = mPref.getString(EFFECT_ITEM_KEY, null);
        if (text != null && text.length() > 0) {
            List<StoreItem> items = gson.fromJson(text, collectionType);
            if (items != null && items.size() > 0) {
                mEffectItems.addAll(items);
            }
        }

        mShowingItems.clear();
        if (mItemType != null && mItemType.length() > 0) {
//            if (mItemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
//                mShowingItems.addAll(mStickerItems);
//                selectCategory(mStickerButton);
//            } else
            if (mItemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                mShowingItems.addAll(mFrameItems);
                selectCategory(mFrameButton);
            } else if (mItemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                mShowingItems.addAll(mEffectItems);
                selectCategory(mEffectButton);
            } else if (mItemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                mShowingItems.addAll(mCropItems);
                selectCategory(mCropButton);
            } else {
                mShowingItems.addAll(mCropItems);
                selectCategory(mCropButton);
            }
        } else {
            mShowingItems.addAll(mBackgroundItems);
            selectCategory(mCropButton);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void asyncLoadStoreItems(final String itemType) {
        ALog.d("StoreFragment", "asyncLoadStoreItems, itemType=" + itemType);
        if (itemType != null && itemType.length() > 0) {
            if (mAllStickerLoaded && itemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
                return;
            } else if (mAllFrameLoaded && itemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                return;
            } else if (mAllEffectLoaded && itemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                return;
            } else if (mAllCropLoaded && itemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                return;
            } else if (mAllBackgroundLoaded && itemType.equalsIgnoreCase(ItemPackageTable.BACKGROUND_TYPE)) {
                return;
            }
        }

        AsyncTask<Void, Void, List<StoreItem>> task = new AsyncTask<Void, Void, List<StoreItem>>() {
            private String mError = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressView.setVisibility(View.VISIBLE);
                mRefreshButton.setVisibility(View.GONE);
            }

            @Override
            protected List<StoreItem> doInBackground(Void... params) {
                try {
                    int offset = mBackgroundOffset;
                    if (itemType != null && itemType.length() > 0) {
                        if (itemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
                            offset = mStickerOffset;
                        } else if (itemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                            offset = mFrameOffset;
                        } else if (itemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                            offset = mEffectOffset;
                        } else if (itemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                            offset = mCropOffset;
                        } else {
                            offset = mBackgroundOffset;
                        }
                    }
                    ALog.d("StoreFragment", "asyncLoadStoreItems, offset=" + offset + ", itemType=" + itemType);
                    final ListStoreItemResponse resp = StoreItemService.getStoreItems(null, itemType, LANGUAGE, offset,
                            LIMIT_ITEMS);
                    if (resp != null) {
                        List<StoreItem> result = resp.getItems();
                        List<StoreItem> selectedItems = new ArrayList<>();
                        if (itemType != null && itemType.length() > 0) {
                            if (itemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
                                if (mStickerOffset == 0) {
                                    mStickerItems.clear();
                                }
                                mStickerOffset += result.size();
                                mStickerItems.addAll(result);
                                selectedItems = mStickerItems;
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                                if (mFrameOffset == 0) {
                                    mFrameItems.clear();
                                }
                                mFrameOffset += result.size();
                                mFrameItems.addAll(result);
                                selectedItems = mFrameItems;
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                                if (mEffectOffset == 0) {
                                    mEffectItems.clear();
                                }
                                mEffectOffset += result.size();
                                mEffectItems.addAll(result);
                                selectedItems = mEffectItems;
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                                if (mCropOffset == 0) {
                                    mCropItems.clear();
                                }
                                mCropOffset += result.size();
                                mCropItems.addAll(result);
                                selectedItems = mCropItems;
                            } else {
                                if (mBackgroundOffset == 0) {
                                    mBackgroundItems.clear();
                                }
                                mBackgroundOffset += result.size();
                                mBackgroundItems.addAll(result);
                                selectedItems = mBackgroundItems;
                            }
                        }

                        ItemPackageTable table = new ItemPackageTable(StoreActivity.this);
                        StoreItemTable storeItemTable = new StoreItemTable(StoreActivity.this);
                        for (StoreItem item : selectedItems) {
                            boolean hasItem = table.hasItem(item.getIdString(), true);
                            if (hasItem) {
                                item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                            } else {
                                item.setDownloadStatus(StoreItem.STATUS_ONLINE);
                            }

                            if (storeItemTable.hasItem(item.getIdString(), true)) {
                                item.setDownloadStatus(StoreItem.STATUS_DOWNLOADING);
                            }
                        }
                        return result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mError = e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<StoreItem> result) {
                super.onPostExecute(result);
                mProgressView.setVisibility(View.GONE);
                mRefreshButton.setVisibility(View.VISIBLE);
                if (mError == null) {
                    if (result != null && result.size() > 0) {
                        mShowingItems.clear();
                        if (itemType != null && itemType.length() > 0) {
//                            if (itemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE) && mStickerItems.size() > 0) {
//                                mShowingItems.addAll(mStickerItems);
//                                selectCategory(mStickerButton);
//                            } else
                            if (itemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE) && mFrameItems.size() > 0) {
                                mShowingItems.addAll(mFrameItems);
                                selectCategory(mFrameButton);
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE) && mEffectItems.size() > 0) {
                                mShowingItems.addAll(mEffectItems);
                                selectCategory(mEffectButton);
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE) && mCropItems.size() > 0) {
                                mShowingItems.addAll(mCropItems);
                                selectCategory(mCropButton);
                            } else {
                                mShowingItems.addAll(mCropItems);
                                selectCategory(mCropButton);
                            }
                        } else {
                            mShowingItems.addAll(mCropItems);
                            selectCategory(mCropButton);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (itemType != null && itemType.length() > 0) {
                            if (itemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE) && mStickerItems.size() > 0) {
                                mAllStickerLoaded = true;
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE) && mFrameItems.size() > 0) {
                                mAllFrameLoaded = true;
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE) && mEffectItems.size() > 0) {
                                mAllEffectLoaded = true;
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE) && mCropItems.size() > 0) {
                                mAllCropLoaded = true;
                            } else {
                                mAllBackgroundLoaded = true;
                            }
                        }
                    }
                }
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Gson gson = GsonUtils.createAndroidStyleGson();
        String text = null;
        if (mBackgroundItems != null) {
            text = gson.toJson(mBackgroundItems);
            mPref.edit().putString(BACKGROUND_ITEM_KEY, text).commit();
        }

        if (mStickerItems != null) {
            text = gson.toJson(mStickerItems);
            mPref.edit().putString(STICKER_ITEM_KEY, text).commit();
        }

        if (mCropItems != null) {
            text = gson.toJson(mCropItems);
            mPref.edit().putString(CROP_ITEM_KEY, text).commit();
        }

        if (mFrameItems != null) {
            text = gson.toJson(mFrameItems);
            mPref.edit().putString(FRAME_ITEM_KEY, text).commit();
        }

        if (mEffectItems != null) {
            text = gson.toJson(mEffectItems);
            mPref.edit().putString(EFFECT_ITEM_KEY, text).commit();
        }

        TempDataContainer.getInstance().getOnInstallStoreItemListeners().remove(this);
    }

    @Override
    public void onFinishInstalling(ItemPackageInfo packageInfo, boolean update) {
        for (StoreItem item : mStoreItems)
            if (item.getIdString().equals(packageInfo.getIdString())) {
                item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                mAdapter.notifyDataSetChanged();
                break;
            }

    }

    @Override
    public void onStartDownloading(ItemPackageInfo item) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        if (getAdCreator() != null)
            getAdCreator().showGoogleInterstitialAd();
        super.finish();
    }
}
