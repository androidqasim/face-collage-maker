package com.codetho.photocollage.ui.fragment;

import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.clockbyte.admobadapter.expressads.AdmobExpressAdapterWrapper;
import com.codetho.photocollage.R;
import com.codetho.photocollage.adapter.GalleryAlbumAdapter;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.model.GalleryAlbum;
import com.codetho.photocollage.ui.BaseFragmentActivity;
import com.codetho.photocollage.utils.AdsHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import dauroi.photoeditor.receiver.NetworkStateReceiver;
import dauroi.photoeditor.utils.DateTimeUtils;

/**
 * Created by vanhu_000 on 3/26/2016.
 */
public class GalleryAlbumFragment extends BaseFragment implements NetworkStateReceiver.NetworkStateReceiverListener {
    private ListView mListView;
    private View mProgressBar;
    private ArrayList<GalleryAlbum> mAlbums;
    private GalleryAlbumAdapter mAdapter;

    private Parcelable mListViewState;
    private AdmobExpressAdapterWrapper mAdmobAdapterWrapper;


    @Override
    public void onPause() {
        // Save ListView mListViewState @ onPause
        if (mListView != null)
            mListViewState = mListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NetworkStateReceiver.removeListener(this);
        if (mAdmobAdapterWrapper != null)
            mAdmobAdapterWrapper.release();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_album, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mProgressBar = view.findViewById(R.id.progressBar);

        AsyncTask<Void, Void, ArrayList<GalleryAlbum>> task = new AsyncTask<Void, Void, ArrayList<GalleryAlbum>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<GalleryAlbum> doInBackground(Void... params) {
                ArrayList<GalleryAlbum> result = loadPhotoAlbums();
                return result;
            }

            @Override
            protected void onPostExecute(ArrayList<GalleryAlbum> galleryAlbums) {
                super.onPostExecute(galleryAlbums);
                if (already()) {
                    mProgressBar.setVisibility(View.GONE);
                    mAlbums = galleryAlbums;
                    mAdapter = new GalleryAlbumAdapter(getActivity(), mAlbums, new GalleryAlbumAdapter.OnGalleryAlbumClickListener() {
                        @Override
                        public void onGalleryAlbumClick(GalleryAlbum album) {
                            BaseFragmentActivity activity = (BaseFragmentActivity) getActivity();
                            Bundle data = new Bundle();
                            data.putStringArrayList(GalleryAlbumImageFragment.ALBUM_IMAGE_EXTRA, (ArrayList<String>) album.getImageList());
                            data.putString(GalleryAlbumImageFragment.ALBUM_NAME_EXTRA, album.getAlbumName());
                            GalleryAlbumImageFragment fragment = new GalleryAlbumImageFragment();
                            fragment.setArguments(data);
                            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                            ft.replace(R.id.frame_container, fragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                    createAdmobAdapterWrapper();
                    mListView.setAdapter(mAdmobAdapterWrapper);
                    // Restore previous mListViewState (including selected item index and scroll position)
                    if (mListViewState != null) {
                        mListView.onRestoreInstanceState(mListViewState);
                    }
                }
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setTitle(R.string.gallery_albums);
        NetworkStateReceiver.addListener(this);
        return view;
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

        if (mAlbums.size() > 2) {
            mAdmobAdapterWrapper.setFirstAdIndex(2);
        } else {
            mAdmobAdapterWrapper.setFirstAdIndex(0);
        }
    }

    public ArrayList<GalleryAlbum> loadPhotoAlbums() {
        final HashMap<Long, GalleryAlbum> map = new HashMap<>();
        // which image properties are we querying
        final String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };

// Get the base URI for the People table in the Contacts content provider.
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = null;
        try {
// Make the query.
            ContentResolver cr = getActivity().getContentResolver();
            cur = cr.query(images,
                    projection, // Which columns to return
                    "",         // Which rows to return (all rows)
                    null,       // Selection arguments (none)
                    ""          // Ordering
            );

            ALog.i("ListingImages", " query count=" + cur.getCount());

            if (cur != null && cur.moveToFirst()) {
                do {
                    // Get the field values
                    String bucketName = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    long date = cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                    String imagePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
                    long bucketId = cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                    // Do something with the values.
                    GalleryAlbum album = map.get(bucketId);
                    if (album == null) {
                        album = new GalleryAlbum(bucketId, bucketName);
                        album.setTakenDate(DateTimeUtils.toUTCDateTimeString(new Date(date)));
                        album.getImageList().add(imagePath);
                        map.put(bucketId, album);
                    } else {
                        album.getImageList().add(imagePath);
                    }
                } while (cur.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cur != null)
                cur.close();
        }

        Collection<GalleryAlbum> albums = map.values();
        ArrayList<GalleryAlbum> result = new ArrayList<>();
        result.addAll(albums);
        return result;
    }

    public void loadAlbumNames() {
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA
        };

        String BUCKET_ORDER_BY = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        Cursor imageCursor = getActivity().getContentResolver().query(images,
                projection, // Which columns to return
                BUCKET_GROUP_BY,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                BUCKET_ORDER_BY        // Ordering
        );

        ArrayList<String> imageUrls = new ArrayList<String>();
        ArrayList<String> imageBuckets = new ArrayList<String>();
        for (int i = 0; i < imageCursor.getCount(); i++) {
            imageCursor.moveToPosition(i);
            int bucketColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            String bucketDisplayName = imageCursor.getString(bucketColumnIndex);
            imageBuckets.add(bucketDisplayName);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imageCursor.getString(dataColumnIndex));
        }
        imageCursor.close();
        for (int idx = 0; idx < imageBuckets.size(); idx++) {
            ALog.d("SelectPhotoActivity", "loadAlbums, name=" + imageBuckets.get(idx) + ", imageUrl=" + imageUrls.get(idx));
        }
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
