package dauroi.photoeditor.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

import dauroi.photoeditor.api.response.CheckShowingAdsResponse;

/**
 * Created by vanhu_000 on 12/21/2015.
 */
public class InterstitialAdCreator {
    public static final int MIN_CLICK_THRESHOLD = 5;
    public static final String BANNER_AD_ID = "ca-app-pub-4015988808950288/7838374452";
    private int mClickedCount = 0;
    private int mStep = MIN_CLICK_THRESHOLD;
    private Context mContext;
    private InterstitialAd mGoogleInterstitialAd;

    public InterstitialAdCreator(Context context) {
        mContext = context;
        // Create the InterstitialAd and set the adUnitId.
        mGoogleInterstitialAd = new com.google.android.gms.ads.InterstitialAd(mContext);
        mGoogleInterstitialAd.setAdUnitId("ca-app-pub-4015988808950288/9315107654");
        mGoogleInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }
        });
    }

    public boolean isEnableAds() {
        if (!StoreUtils.isPurchasedDevice()) {
//            CheckShowingAdsResponse resp = TempDataContainer.getInstance().getCheckShowingAdsResponse();
//            if (resp != null) {
//                return resp.isShowAds();
//            } else {
            return true;
//            }
        } else {
            return false;
        }
    }

    public void loadGoogleInterstitialAd() {
        if (isEnableAds()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mGoogleInterstitialAd.loadAd(adRequest);
        }
    }

    public void showGoogleInterstitialAd() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (isEnableAds() && mGoogleInterstitialAd != null && mGoogleInterstitialAd.isLoaded()) {
            mGoogleInterstitialAd.show();
        }
    }

    /**
     * Should be call to save instance state
     *
     * @param bundle
     */
    public void saveInstanceState(Bundle bundle, String clickedCountKey, String stepKey) {
        bundle.putInt(clickedCountKey, mClickedCount);
        bundle.putInt(stepKey, mStep);
    }

    /**
     * Should be call before calling attach() method if has saved instance
     * state.
     *
     * @param bundle
     */
    public void restoreInstanceState(Bundle bundle, String clickedCountKey, String stepKey) {
        mClickedCount = bundle.getInt(clickedCountKey, mClickedCount);
        mStep = bundle.getInt(stepKey, mStep);
    }

    public void onClicked() {
        mClickedCount++;
        if (mClickedCount >= mStep) {
            showGoogleInterstitialAd();
            mClickedCount = 0;
            Random random = new Random();
            mStep = random.nextInt(5) + MIN_CLICK_THRESHOLD;
            CheckShowingAdsResponse resp = TempDataContainer.getInstance().getCheckShowingAdsResponse();
            if (resp != null && resp.getStep() > 1) {
                mStep = random.nextInt(5) + resp.getStep();
            }
            loadGoogleInterstitialAd();
        }
    }

    public void setGoogleAdListener(AdListener listener) {
        if (mGoogleInterstitialAd != null)
            mGoogleInterstitialAd.setAdListener(listener);
    }

    public void destroy() {

    }
}
