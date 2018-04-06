package com.codetho.photocollage.utils;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by admin on 5/24/2016.
 */
public class AdsHelper {
    private static final String APP_ID = "ca-app-pub-4015988808950288~6361641251";
    private static final String BANNER_ADS_ID = "ca-app-pub-4015988808950288/7838374452";
    private static final String POPUP_ADS_ID = "ca-app-pub-4015988808950288/9315107654";
    public static final String NATIVE_AD_ID = "ca-app-pub-4015988808950288/7323292459";

    public interface OnInterstitialAdListener {
        void onInterstitialAdLoaded();
    }

    private int mClickedPeriod = 15;
    private InterstitialAd mInterstitialAd;
    private int mClickedCount = 0;
    private OnInterstitialAdListener mAdListener;
    //Banner ads
    private AdView mAdView;

    public AdsHelper(Context context) {
        this(context, null);
    }

    public AdsHelper(Context context, OnInterstitialAdListener listener) {
        // Interstitial ads
        mAdListener = listener;
        try {
            MobileAds.initialize(context.getApplicationContext(), APP_ID);
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(POPUP_ADS_ID);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    // beginPlayingGame();
                }

                @Override
                public void onAdLoaded() {
                    if (mAdListener != null) {
                        mAdListener.onInterstitialAdLoaded();
                    }
                }
            });

            requestNewInterstitial();
            //banner ads
            mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId(BANNER_ADS_ID);
            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(
                    AdRequest.DEVICE_ID_EMULATOR).build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addAdsBannerView(ViewGroup parent) {
        if (mAdView != null) {
            parent.removeView(mAdView);
        }

        parent.addView(mAdView);
    }

    public void pauseAdsBanner() {
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    public void resumeAdsBanner() {
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    public void destroyAdsBanner() {
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    public void setAdListener(OnInterstitialAdListener adListener) {
        mAdListener = adListener;
    }

    public boolean showInterstitialAds() {
        try {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    public void setClickedPeriod(int clickedPeriod) {
        mClickedPeriod = clickedPeriod;
    }

    public void clickItem() {
        try {
            mClickedCount++;
            if (mClickedCount % mClickedPeriod == 0) {
                showInterstitialAds();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void requestNewInterstitial() {
        try {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            mInterstitialAd.loadAd(adRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
