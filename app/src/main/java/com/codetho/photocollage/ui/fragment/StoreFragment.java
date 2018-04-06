package com.codetho.photocollage.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clockbyte.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.codetho.photocollage.R;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.ui.AdsFragmentActivity;
import com.codetho.photocollage.utils.AdsHelper;
import com.google.android.gms.ads.AdSize;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.adapter.StoreItemAdapter;
import dauroi.photoeditor.api.StoreItemService;
import dauroi.photoeditor.api.response.ListStoreItemResponse;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.database.table.ItemPackageTable;
import dauroi.photoeditor.database.table.StoreItemTable;
import dauroi.photoeditor.listener.OnInstallStoreItemListener;
import dauroi.photoeditor.model.ItemPackageInfo;
import dauroi.photoeditor.receiver.NetworkStateReceiver;
import dauroi.photoeditor.ui.activity.StoreActivity;
import dauroi.photoeditor.ui.activity.StoreItemDetailActivity;
import dauroi.photoeditor.utils.GsonUtils;
import dauroi.photoeditor.utils.TempDataContainer;

public class StoreFragment extends BaseFragment implements OnInstallStoreItemListener, SearchView.OnQueryTextListener,
        NetworkStateReceiver.NetworkStateReceiverListener {
    static final String LANGUAGE = "en";
    private static final int LIMIT_ITEMS = 50;
    static final String STORE_ITEM_PREF = "storeItemPref";
    static final String BACKGROUND_ITEM_KEY = "backgroundItems";
    static final String STICKER_ITEM_KEY = "stickerItems";
    static final String EFFECT_ITEM_KEY = "effectItems";
    static final String CROP_ITEM_KEY = "cropItems";
    static final String FRAME_ITEM_KEY = "frameItems";
    // private TextView mPurchaseGuideView;
    private View mBackgroundFrame;
    private View mStickerFrame;
    private View mCropFrame;
    private View mEffectFrame;
    private View mFrameFrame;

    private View mBackgroundButton;
    private ImageView mBackgroundThumbnailView;
    private TextView mBackgroundNameView;
    private View mStickerButton;
    private ImageView mStickerThumbnailView;
    private TextView mStickerNameView;
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
    private String mItemType;
    //    private List<StoreItem> mStoreItems = new ArrayList<StoreItem>();
    private List<StoreItem> mBackgroundItems = new ArrayList<StoreItem>();
    private List<StoreItem> mStickerItems = new ArrayList<StoreItem>();
    private List<StoreItem> mEffectItems = new ArrayList<StoreItem>();
    private List<StoreItem> mCropItems = new ArrayList<StoreItem>();
    private List<StoreItem> mFrameItems = new ArrayList<StoreItem>();
    private List<StoreItem> mShowingItems = new ArrayList<StoreItem>();
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

    private SharedPreferences mPref;
    //Menu items
    private boolean mShowProgress = true;
    private MenuItem mProgressItem;
    private MenuItem mRefreshItem;
    private boolean mIsActualSearch = false;
    private boolean mShowRefreshButton = true;

    private SearchView mSearchViewAction;
    private AdmobExpressAdapterWrapper mAdmobAdapterWrapper;
    private Parcelable mListViewState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_store, container, false);
        mBackgroundFrame = view.findViewById(R.id.backgroundFrame);
        mStickerFrame = view.findViewById(R.id.stickerFrame);

        mBackgroundButton = view.findViewById(R.id.backgroundLayout);
        mBackgroundThumbnailView = (ImageView) view.findViewById(R.id.backgroundThumbnailView);
        mBackgroundNameView = (TextView) view.findViewById(R.id.backgroundNameView);
        mBackgroundButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mItemType = ItemPackageTable.BACKGROUND_TYPE;
                selectCategory(mBackgroundButton);
                mShowingItems.clear();
                mShowingItems.addAll(mBackgroundItems);
                mAdapter.notifyDataSetChanged();
                if (mBackgroundItems == null || mBackgroundItems.isEmpty() || mBackgroundOffset < 1) {
                    asyncLoadStoreItems(mItemType);
                }
            }
        });

        mStickerButton = view.findViewById(R.id.stickerLayout);
        mStickerThumbnailView = (ImageView) view.findViewById(R.id.stickerThumbnailView);
        mStickerNameView = (TextView) view.findViewById(R.id.stickerNameView);
        mStickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mItemType = ItemPackageTable.STICKER_TYPE;
                selectCategory(mStickerButton);
                mShowingItems.clear();
                mShowingItems.addAll(mStickerItems);
                mAdapter.notifyDataSetChanged();
                if (mStickerItems == null || mStickerItems.isEmpty() || mStickerOffset < 1) {
                    asyncLoadStoreItems(mItemType);
                }
            }
        });

        mCropFrame = view.findViewById(R.id.cropFrame);
        mFrameFrame = view.findViewById(R.id.frameFrame);
        mEffectFrame = view.findViewById(R.id.effectFrame);
        mCropButton = view.findViewById(R.id.cropView);
        mCropThumbnailView = (ImageView) view.findViewById(R.id.cropThumbnailView);
        mCropNameView = (TextView) view.findViewById(R.id.cropNameView);
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

        mEffectButton = view.findViewById(R.id.effectView);
        mEffectThumbnailView = (ImageView) view.findViewById(R.id.effectThumbnailView);
        mEffectNameView = (TextView) view.findViewById(R.id.effectNameView);
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

        mFrameButton = view.findViewById(R.id.frameView);
        mFrameThumbnailView = (ImageView) view.findViewById(R.id.frameThumbnailView);
        mFrameNameView = (TextView) view.findViewById(R.id.frameNameView);
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

        mListView = (ListView) view.findViewById(R.id.itemList);
        if (getArguments() != null) {
            mItemType = getArguments().getString(StoreItem.EXTRA_ITEM_TYPE_KEY);
        }

        if (mItemType == null || mItemType.length() < 1) {
            mItemType = ItemPackageTable.BACKGROUND_TYPE;
        }

        mAdapter = new StoreItemAdapter(getActivity(), mShowingItems, new StoreItemAdapter.OnStoreItemClickListener() {

            @Override
            public void onStoreItemClick(StoreItem item) {
                if (mActivity instanceof AdsFragmentActivity) {
                    AdsHelper adsHelper = ((AdsFragmentActivity) mActivity).getAdsHelper();
                    if (adsHelper != null) {
                        adsHelper.setClickedPeriod(5);
                        adsHelper.clickItem();
                    }
                }

                Intent intent = new Intent(getActivity(), StoreItemDetailActivity.class);
                intent.putExtra(StoreActivity.EXTRA_STORE_ITEM, item);
                startActivity(intent);
            }

            @Override
            public void onPriceButtonClick(StoreItem item) {
                if (item.getDownloadStatus() == StoreItem.STATUS_ONLINE) {
                    // purchaseItem(item);
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

        createAdmobAdapterWrapper();
        mListView.setAdapter(mAdmobAdapterWrapper);
        if (mListViewState != null)
            mListView.onRestoreInstanceState(mListViewState);
        mPref = getActivity().getSharedPreferences(STORE_ITEM_PREF, Context.MODE_PRIVATE);
        TempDataContainer.getInstance().getOnInstallStoreItemListeners().add(this);
        NetworkStateReceiver.addListener(this);
        loadOfflineItems();
        asyncLoadStoreItems(mItemType);

        return view;
    }

    public void showProgressBar(boolean show) {
        mShowProgress = show;
        if (mProgressItem != null) {
            mProgressItem.setVisible(mShowProgress);
        }
    }

    public void showRefreshButton(boolean show) {
        mShowRefreshButton = show;
        if (mRefreshItem != null) {
            mRefreshItem.setVisible(mShowRefreshButton);
        }
    }

    private void createAdmobAdapterWrapper() {
        if (mAdapter == null) {
            return;
        }

        if (mAdmobAdapterWrapper != null) {
            mAdmobAdapterWrapper.release();
        }

        mAdmobAdapterWrapper = new AdmobExpressAdapterWrapper(mActivity, AdsHelper.NATIVE_AD_ID);
        //By default both types of ads are loaded by wrapper.
        // To set which of them to show in the list you should use an appropriate ctor
        //adapterWrapper = new AdmobAdapterWrapper(this, testDevicesIds, EnumSet.of(EAdType.ADVANCED_INSTALLAPP));
        //wrapping your adapter with a AdmobAdapterWrapper.
        mAdmobAdapterWrapper.setAdapter(mAdapter);
        //inject your custom layout and strategy of binding for installapp/content  ads
        //here you should pass the extended NativeAdLayoutContext
        //by default it has a value InstallAppAdLayoutContext.getDefault()
        //adapterWrapper.setInstallAdsLayoutContext(...);
        //by default it has a value ContentAdLayoutContext.getDefault()
        //adapterWrapper.setContentAdsLayoutContext(...);

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        mAdmobAdapterWrapper.setLimitOfAds(3);

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        mAdmobAdapterWrapper.setNoOfDataBetweenAds(10);
        mAdmobAdapterWrapper.setFirstAdIndex(2);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.store, menu);
        // Associate searchable configuration with the SearchView
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (searchMenuItem != null) {
            mSearchViewAction = (SearchView) MenuItemCompat
                    .getActionView(searchMenuItem);
            if (mSearchViewAction != null)
                mSearchViewAction.setOnQueryTextListener(this);
        }

        mProgressItem = menu.findItem(R.id.action_show_progress);
        if (mProgressItem != null) {
            mProgressItem.setVisible(mShowProgress);
        }

        mRefreshItem = menu.findItem(R.id.action_refresh);
        if (mRefreshItem != null) {
            mRefreshItem.setVisible(mShowRefreshButton);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
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
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NetworkStateReceiver.removeListener(this);
        if (mAdmobAdapterWrapper != null)
            mAdmobAdapterWrapper.release();
    }

    private void selectCategory(View categoryView) {
        mBackgroundThumbnailView.setImageResource(R.drawable.ic_store_background_normal);
        mBackgroundNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_normal_text_main_topbar));
        mStickerThumbnailView.setImageResource(R.drawable.ic_store_stickers_normal);
        mStickerNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_normal_text_main_topbar));
        mCropThumbnailView.setImageResource(dauroi.photoeditor.R.drawable.photo_editor_ic_crop_normal);
        mCropNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_normal_text_main_topbar));
        mFrameThumbnailView.setImageResource(dauroi.photoeditor.R.drawable.photo_editor_ic_frame_normal);
        mFrameNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_normal_text_main_topbar));
        mEffectThumbnailView.setImageResource(dauroi.photoeditor.R.drawable.photo_editor_ic_effect_normal);
        mEffectNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_normal_text_main_topbar));

        if (categoryView == mBackgroundButton) {
            mBackgroundThumbnailView.setImageResource(R.drawable.ic_store_background_pressed);
            mBackgroundNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_selected_text_main_topbar));
        } else if (categoryView == mStickerButton) {
            mStickerThumbnailView.setImageResource(R.drawable.ic_store_stickers_pressed);
            mStickerNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_selected_text_main_topbar));
        } else if (categoryView == mCropButton) {
            mCropThumbnailView.setImageResource(dauroi.photoeditor.R.drawable.photo_editor_ic_crop_pressed);
            mCropNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_selected_text_main_topbar));
        } else if (categoryView == mFrameButton) {
            mFrameThumbnailView.setImageResource(dauroi.photoeditor.R.drawable.photo_editor_ic_frame_pressed);
            mFrameNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_selected_text_main_topbar));
        } else {
            mEffectThumbnailView.setImageResource(dauroi.photoeditor.R.drawable.photo_editor_ic_effect_pressed);
            mEffectNameView.setTextColor(getResources().getColor(dauroi.photoeditor.R.color.photo_editor_selected_text_main_topbar));
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
            if (mItemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE)) {
                mShowingItems.addAll(mStickerItems);
                selectCategory(mStickerButton);
            } else if (mItemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE)) {
                mShowingItems.addAll(mFrameItems);
                selectCategory(mFrameButton);
            } else if (mItemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE)) {
                mShowingItems.addAll(mEffectItems);
                selectCategory(mEffectButton);
            } else if (mItemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE)) {
                mShowingItems.addAll(mCropItems);
                selectCategory(mCropButton);
            } else {
                mShowingItems.addAll(mBackgroundItems);
                selectCategory(mBackgroundButton);
            }
        } else {
            mShowingItems.addAll(mBackgroundItems);
            selectCategory(mBackgroundButton);
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
                showProgressBar(true);
                showRefreshButton(false);
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

                        ItemPackageTable table = new ItemPackageTable(getActivity());
                        StoreItemTable storeItemTable = new StoreItemTable(getActivity());
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
                if (!already()) {
                    return;
                }

                showProgressBar(false);
                showRefreshButton(true);
                if (mError == null) {
                    if (result != null && result.size() > 0) {
                        mShowingItems.clear();
                        if (itemType != null && itemType.length() > 0) {
                            if (itemType.equalsIgnoreCase(ItemPackageTable.STICKER_TYPE) && mStickerItems.size() > 0) {
                                mShowingItems.addAll(mStickerItems);
                                selectCategory(mStickerButton);
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FRAME_TYPE) && mFrameItems.size() > 0) {
                                mShowingItems.addAll(mFrameItems);
                                selectCategory(mFrameButton);
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.FILTER_TYPE) && mEffectItems.size() > 0) {
                                mShowingItems.addAll(mEffectItems);
                                selectCategory(mEffectButton);
                            } else if (itemType.equalsIgnoreCase(ItemPackageTable.CROP_TYPE) && mCropItems.size() > 0) {
                                mShowingItems.addAll(mCropItems);
                                selectCategory(mCropButton);
                            } else {
                                mShowingItems.addAll(mBackgroundItems);
                                selectCategory(mBackgroundButton);
                            }
                        } else {
                            mShowingItems.addAll(mBackgroundItems);
                            selectCategory(mBackgroundButton);
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
        if (ItemPackageTable.STICKER_TYPE.equalsIgnoreCase(packageInfo.getType())) {
            for (StoreItem item : mStickerItems)
                if (item.getIdString().equals(packageInfo.getIdString())) {
                    item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
        } else if (ItemPackageTable.FRAME_TYPE.equalsIgnoreCase(packageInfo.getType())) {
            for (StoreItem item : mFrameItems)
                if (item.getIdString().equals(packageInfo.getIdString())) {
                    item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
        } else if (ItemPackageTable.FILTER_TYPE.equalsIgnoreCase(packageInfo.getType())) {
            for (StoreItem item : mEffectItems)
                if (item.getIdString().equals(packageInfo.getIdString())) {
                    item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
        } else if (ItemPackageTable.CROP_TYPE.equalsIgnoreCase(packageInfo.getType())) {
            for (StoreItem item : mCropItems)
                if (item.getIdString().equals(packageInfo.getIdString())) {
                    item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
        } else {
            for (StoreItem item : mBackgroundItems)
                if (item.getIdString().equals(packageInfo.getIdString())) {
                    item.setDownloadStatus(StoreItem.STATUS_DOWNLOADED);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
        }
    }

    @Override
    public void onStartDownloading(ItemPackageInfo item) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public void onNetworkAvailable() {
        if (mAdmobAdapterWrapper != null && mAdmobAdapterWrapper.getFetchedAdsCount() < 1) {
            if (mListView != null)
                mListViewState = mListView.onSaveInstanceState();
            createAdmobAdapterWrapper();
            if (mListView != null) {
                mListView.setAdapter(mAdmobAdapterWrapper);
                if (mListViewState != null)
                    mListView.onRestoreInstanceState(mListViewState);
            }
        }
    }

    @Override
    public void onNetworkUnavailable() {

    }
}
