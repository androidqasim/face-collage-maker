package com.codetho.photocollage.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.DebugOptions;
import com.codetho.photocollage.utils.AdsHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

import dauroi.photoeditor.receiver.NetworkStateReceiver;

/**
 * Created by admin on 7/10/2016.
 */
public abstract class AdsFragmentActivity extends BaseFragmentActivity implements AdsHelper.OnInterstitialAdListener, NetworkStateReceiver.NetworkStateReceiverListener {
    //Show ads
    private boolean mLoadedAds = false;
    protected boolean mLoadedData = false;
    private boolean mShownAds = false;
    private AdsHelper mAdsHelper;
    private NativeExpressAdView mNativeExpressAdView;
    private ViewGroup mAdLayout;
    private boolean mLoadedNativeAds = false;

    public boolean isShownAds() {
        return mShownAds;
    }

    public void setShownAds(boolean shownAds) {
        mShownAds = shownAds;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLoadedAds = savedInstanceState.getBoolean("mLoadedAds");
            mLoadedData = savedInstanceState.getBoolean("mLoadedData");
            mShownAds = savedInstanceState.getBoolean("mShownAds");
        }
        preCreateAdsHelper();
        if (!DebugOptions.isProVersion())
            mAdsHelper = new AdsHelper(this, this);
        NetworkStateReceiver.addListener(this);
    }

    protected void preCreateAdsHelper() {

    }

    public AdsHelper getAdsHelper() {
        return mAdsHelper;
    }

    protected void addAdsView(int adsLayout) {
        ViewGroup parent = (ViewGroup) findViewById(adsLayout);
        if (parent != null && mAdsHelper != null)
            mAdsHelper.addAdsBannerView(parent);
    }


    protected void addNativeAdView() {
        mAdLayout = (ViewGroup) findViewById(R.id.adsLayout);
        final VideoController mVideoController;
        // Locate the NativeExpressAdView.
        mNativeExpressAdView = new NativeExpressAdView(this);
        mNativeExpressAdView.setAdSize(new AdSize(AdSize.FULL_WIDTH, 140));
        mNativeExpressAdView.setAdUnitId(AdsHelper.NATIVE_AD_ID);
        // Set its video options.
        mNativeExpressAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        mVideoController = mNativeExpressAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mNativeExpressAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdLayout.removeAllViews();
                mAdLayout.addView(mNativeExpressAdView);
                mLoadedNativeAds = true;
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (!mLoadedNativeAds)
                    mAdLayout.removeAllViews();
            }
        });

        mNativeExpressAdView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onNetworkAvailable() {
        if (mNativeExpressAdView != null)
            mNativeExpressAdView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onNetworkUnavailable() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mLoadedAds", mLoadedAds);
        outState.putBoolean("mLoadedData", mLoadedData);
        outState.putBoolean("mShownAds", mShownAds);
    }

    @Override
    public void onPause() {
        if (mAdsHelper != null) {
            mAdsHelper.pauseAdsBanner();
        }
        if (mNativeExpressAdView != null) {
            mNativeExpressAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdsHelper != null)
            mAdsHelper.resumeAdsBanner();
        if (mNativeExpressAdView != null) {
            mNativeExpressAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdsHelper != null)
            mAdsHelper.destroyAdsBanner();
        if (mNativeExpressAdView != null) {
            mNativeExpressAdView.destroy();
        }
        NetworkStateReceiver.removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onInterstitialAdLoaded() {
        mLoadedAds = true;
        if (!mShownAds && mLoadedData && mAdsHelper != null) {
            mAdsHelper.showInterstitialAds();
            mShownAds = true;
        }
    }
}
