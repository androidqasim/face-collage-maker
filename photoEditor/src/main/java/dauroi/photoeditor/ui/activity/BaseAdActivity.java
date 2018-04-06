package dauroi.photoeditor.ui.activity;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import dauroi.photoeditor.config.DebugOptions;
import dauroi.photoeditor.utils.InterstitialAdCreator;

/**
 * Created by vanhu_000 on 12/23/2015.
 */
public class BaseAdActivity extends BaseActivity {
    //Ads
    private InterstitialAdCreator mAdCreator;
    //Banner ads
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //banner ads
        if (!DebugOptions.isProVersion(this)) {
            mAdView = new AdView(this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId(InterstitialAdCreator.BANNER_AD_ID);
            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(
                    AdRequest.DEVICE_ID_EMULATOR).build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
            //Interstitial Ad Creator
            mAdCreator = new InterstitialAdCreator(this);
            mAdCreator.loadGoogleInterstitialAd();
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        if (mAdCreator != null) {
            mAdCreator.destroy();
        }
        super.onDestroy();
    }

    public AdView getAdView() {
        return mAdView;
    }

    public InterstitialAdCreator getAdCreator() {
        return mAdCreator;
    }
}
