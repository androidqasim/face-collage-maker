package com.codetho.photocollage.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by vanhu_000 on 2/23/2016.
 */
public class BaseFragmentActivity extends AppCompatActivity {
    //Analytics
    protected FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                try {
                    onBackPressed();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    finish();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
