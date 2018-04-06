package com.codetho.photocollage.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.listener.OnChooseColorListener;
import com.codetho.photocollage.listener.OnShareImageListener;
import com.codetho.photocollage.ui.fragment.BaseFragment;
import com.codetho.photocollage.ui.fragment.PhotoCollageFragment;
import com.codetho.photocollage.ui.fragment.SelectFrameFragment;

/**
 * Created by vanhu_000 on 2/23/2016.
 */
public class PhotoCollageActivity extends AdsFragmentActivity implements
        OnShareImageListener, OnChooseColorListener {
    public static final int PHOTO_TYPE = 1;
    public static final int FRAME_TYPE = 2;
    public static final String EXTRA_CREATED_METHOD_TYPE = "methodType";

    private int mSelectedColor = Color.GREEN;
    private boolean mClickedShareButton = false;

    @Override
    protected void preCreateAdsHelper() {
        mLoadedData = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photocollage);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }

        final ViewGroup adsLayout = (ViewGroup) findViewById(R.id.adsLayout);
        if (getAdsHelper() != null)
            getAdsHelper().addAdsBannerView(adsLayout);
        if (savedInstanceState == null) {
            BaseFragment fragment = null;
            int type = getIntent().getIntExtra(EXTRA_CREATED_METHOD_TYPE, PHOTO_TYPE);
            if (type == PHOTO_TYPE) {
                fragment = new PhotoCollageFragment();
            } else {
                fragment = new SelectFrameFragment();
            }

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } else {
            mClickedShareButton = savedInstanceState.getBoolean("mClickedShareButton", false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mClickedShareButton", mClickedShareButton);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mClickedShareButton) {
            mClickedShareButton = false;
            if (getAdsHelper() != null) {
                getAdsHelper().showInterstitialAds();
            }
        }
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) getVisibleFragment();
        if (fragment instanceof PhotoCollageFragment || fragment instanceof SelectFrameFragment) {
            super.onBackPressed();
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onShareImage(String imagePath) {
        mClickedShareButton = true;
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        return fragmentManager.findFragmentById(R.id.frame_container);
    }

    @Override
    public void onShareFrame(String imagePath) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Shared image frame: " + imagePath,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setSelectedColor(int color) {
        mSelectedColor = color;
    }

    @Override
    public int getSelectedColor() {
        return mSelectedColor;
    }
}

