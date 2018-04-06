package com.codetho.photocollage.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.DebugOptions;
import com.codetho.photocollage.ui.PhotoCollageActivity;
import com.codetho.photocollage.ui.TemplateActivity;
import com.codetho.photocollage.utils.BigDAdsHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import dauroi.photoeditor.receiver.NetworkStateReceiver;

public class MainPhotoFragment extends BaseFragment implements NetworkStateReceiver.NetworkStateReceiverListener {
    private BigDAdsHelper mBigDAdsHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_photo, null);
        View photoView = rootView.findViewById(R.id.photoButton);
        photoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createFromPhoto();
                report("home/clicked_create_freely");
            }
        });

        View frameView = rootView.findViewById(R.id.frameButton);
        frameView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createFromFrame();
                report("home/clicked_frame");
            }
        });

        rootView.findViewById(R.id.imageTemplateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFromTemplate();
                report("home/clicked_template");
            }
        });
        //bigd ads
        if (!DebugOptions.isProVersion()) {
            final ViewGroup mAdsAppLayout = (ViewGroup) rootView.findViewById(R.id.appLayout);
            mBigDAdsHelper = new BigDAdsHelper(mActivity);
            mBigDAdsHelper.showSecondDetailView(false);
            mBigDAdsHelper.attach(mAdsAppLayout);
            mBigDAdsHelper.asyncLoadBigDAds();
            NetworkStateReceiver.addListener(this);
        }

        return rootView;
    }

    @Override
    protected void setTitle() {
        String mTitle = getString(R.string.home);
        setTitle(mTitle);
    }

    @Override
    public void onDestroyView() {
        NetworkStateReceiver.removeListener(this);
        super.onDestroyView();
    }

    private void report(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "home");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void createFromPhoto() {
        if (!already()) {
            return;
        }
        Intent intent = new Intent(getActivity(), PhotoCollageActivity.class);
        intent.putExtra(PhotoCollageActivity.EXTRA_CREATED_METHOD_TYPE, PhotoCollageActivity.PHOTO_TYPE);
        startActivity(intent);
    }

    public void createFromFrame() {
        if (!already()) {
            return;
        }
        Intent intent = new Intent(getActivity(), TemplateActivity.class);
        intent.putExtra(TemplateActivity.EXTRA_IS_FRAME_IMAGE, true);
        startActivity(intent);
    }

    public void createFromTemplate() {
        if (!already()) {
            return;
        }
        Intent intent = new Intent(getActivity(), TemplateActivity.class);
        intent.putExtra(PhotoCollageActivity.EXTRA_CREATED_METHOD_TYPE, PhotoCollageActivity.FRAME_TYPE);
        startActivity(intent);
    }

    @Override
    public void onNetworkAvailable() {
        if (mBigDAdsHelper != null && !mBigDAdsHelper.isVisible()) {
            mBigDAdsHelper.asyncLoadBigDAds();
        }
    }

    @Override
    public void onNetworkUnavailable() {

    }
}
