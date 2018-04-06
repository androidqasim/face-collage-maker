package com.codetho.photocollage.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.ALog;
import com.codetho.photocollage.config.DebugOptions;
import com.codetho.photocollage.receiver.PackageInstallReceiver;
import com.codetho.photocollage.ui.fragment.BaseFragment;
import com.codetho.photocollage.ui.fragment.CreatedCollageFragment;
import com.codetho.photocollage.ui.fragment.MainPhotoFragment;
import com.codetho.photocollage.ui.fragment.StoreFragment;
import com.codetho.photocollage.utils.BigDAdsHelper;
import com.codetho.photocollage.utils.DialogUtils;
import com.codetho.photocollage.utils.ResultContainer;
import com.google.firebase.crash.FirebaseCrash;

import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.database.DatabaseManager;
import dauroi.photoeditor.utils.StoreUtils;

public class MainActivity extends AdsFragmentActivity {
    public static final String RATE_APP_PREF_NAME = "rateAppPref";
    public static final String RATED_APP_KEY = "ratedApp";
    public static final String OPEN_APP_COUNT_KEY = "openAppCount";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerlayout;
    private String mTitle;
    private ViewGroup mAdLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!DatabaseManager.getInstance(this).isDbFileExisted()) {
            DatabaseManager.getInstance(this).createDb();
        } else {
            boolean isOpen = DatabaseManager.getInstance(this).openDb();
            ALog.d("MainActivity", "onCreate, database isOpen=" + isOpen);
        }

        if (savedInstanceState == null) {
            PackageInstallReceiver.clickedApp = null;
            PackageInstallReceiver.reportedMap.clear();
            BigDAdsHelper.clearInstalledApp();
        }

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.home);
        }

        mTitle = getString(R.string.home);
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("mTitle");
        }

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, // host activity
                mDrawerlayout, // drawerlayout object
                toolbar, // toolbar
                R.string.navigation_drawer_open, // open drawer description required!
                R.string.navigation_drawer_close) { // closed drawer description

            // called once the drawer has closed.
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to
            }

            // called when the drawer is now open.
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.syncState();
        // To disable the icon for the drawer, change this to false
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerlayout.setDrawerListener(mDrawerToggle);

        mAdLayout = (ViewGroup) findViewById(R.id.adsLayout);

        findViewById(R.id.homeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawer(GravityCompat.START);
                mTitle = getString(R.string.home);
                getSupportActionBar().setTitle(mTitle);
                if (getAdsHelper() != null)
                    getAdsHelper().addAdsBannerView(mAdLayout);
                onHomeItemMenuClickListener();
            }
        });

        findViewById(R.id.rateAppView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawer(GravityCompat.START);
                onRateAppButtonClick();
            }
        });

        findViewById(R.id.storeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawer(GravityCompat.START);
                mAdLayout.removeAllViews();
                mTitle = getString(R.string.store);
                getSupportActionBar().setTitle(mTitle);
                onStoreItemMenuClickListener();
            }
        });

        findViewById(R.id.albumView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawer(GravityCompat.START);
                mTitle = getString(R.string.album);
                getSupportActionBar().setTitle(mTitle);
                if (getAdsHelper() != null)
                    getAdsHelper().addAdsBannerView(mAdLayout);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new CreatedCollageFragment(), "CreatedCollageFragment")
                        .commit();
            }
        });

        findViewById(R.id.photoEditorView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawer(GravityCompat.START);
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("market://details?id=dauroi.photoeditor"));
                    startActivity(i);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final View removeAdsView = findViewById(R.id.removeAdsView);
        removeAdsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawer(GravityCompat.START);
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("market://details?id=com.codetho.photocollagepro"));
                    startActivity(i);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (DebugOptions.isProVersion()) {
            removeAdsView.setVisibility(View.GONE);
        }

        if (getAdsHelper() != null)
            getAdsHelper().addAdsBannerView(mAdLayout);
        //set view
        if (savedInstanceState == null) {
            ResultContainer.getInstance().clearAll();
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new MainPhotoFragment(), "MainPhotoFragment")
                    .commit();
        }
        //Redownload all unsuccessful items
        try {
            StoreUtils.redownloadItems();
        } catch (Exception ex) {
            ex.printStackTrace();
            FirebaseCrash.report(ex);
        }
        //Handle pushed notification
        if (getIntent().getExtras() != null) {
            String itemType = getIntent().getExtras().getString("type");
            if (itemType != null && itemType.length() > 0) {
                itemType = itemType.trim();
                if (itemType.equalsIgnoreCase("update")) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
                    }
                } else if (itemType.equalsIgnoreCase("ad")) {
                    ALog.d("MainActivity", "show ad");
                } else {
                    try {
                        StoreFragment fragment = new StoreFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(StoreItem.EXTRA_ITEM_TYPE_KEY, itemType);
                        fragment.setArguments(bundle);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, fragment, "StoreFragment")
                                .commit();
                        mLoadedData = true;
                        if (getAdsHelper() != null)
                            getAdsHelper().showInterstitialAds();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FirebaseCrash.report(ex);
                    }
                }
            }
        }
        // [END handle_data_extras]
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mTitle", mTitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getSupportActionBar().setTitle(mTitle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onHomeItemMenuClickListener() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new MainPhotoFragment(), "MainPhotoFragment")
                .commit();
    }

    public void onRateAppButtonClick() {
        try {
            final SharedPreferences preferences = getSharedPreferences(RATE_APP_PREF_NAME, Context.MODE_PRIVATE);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(i);
            // save result
            preferences.edit().putBoolean(RATED_APP_KEY, true).commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.app_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void onStoreItemMenuClickListener() {
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new StoreFragment(), "StoreFragment")
                .commit();
    }

    public void rateApp(final boolean finish) {
        final SharedPreferences preferences = getSharedPreferences(RATE_APP_PREF_NAME, Context.MODE_PRIVATE);
        boolean rated = preferences.getBoolean(RATED_APP_KEY, false);
        int count = preferences.getInt(OPEN_APP_COUNT_KEY, 0) + 1;
        ALog.d("NetworkUtils.rateApp", "rated=" + rated + ", count=" + count);
        preferences.edit().putInt(OPEN_APP_COUNT_KEY, count).commit();
        if (!rated && (count % 5 == 2)) {
            DialogUtils.showConfirmDialog(this, R.string.rate_app, R.string.photo_editor_rate_app,
                    new DialogUtils.ConfirmDialogOnClickListener() {

                        @Override
                        public void onOKButtonOnClick() {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("market://details?id=" + getPackageName()));
                                startActivity(i);
                                // save result
                                preferences.edit().putBoolean(RATED_APP_KEY, true).commit();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            if (finish) {
                                finish();
                            }
                        }

                        @Override
                        public void onCancelButtonOnClick() {
                            if (finish) {
                                finish();
                            }
                        }
                    });
        } else if (finish) {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            BaseFragment fragment = (BaseFragment) getVisibleFragment();
            if (fragment instanceof MainPhotoFragment) {
                rateApp(true);
            } else {
                try {
                    mTitle = getString(R.string.home);
                    if (getSupportActionBar() != null)
                        getSupportActionBar().setTitle(mTitle);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (getAdsHelper() != null)
                    getAdsHelper().addAdsBannerView(mAdLayout);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new MainPhotoFragment(), "MainPhotoFragment")
                        .commit();
            }
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        return fragmentManager.findFragmentById(R.id.frame_container);
    }
}
