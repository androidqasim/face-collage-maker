package com.codetho.photocollage.ui.fragment;

import com.codetho.photocollage.R;
import com.codetho.photocollage.config.Constant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SelectFrameFragment extends BaseFragment {
    // private GridView mGridView;
    private int mPhotoWidth = 0;
    private int mPhotoHeight = 0;
    private ImageView mFrameView1;
    private ImageView mFrameView2;
    private ImageView mFrameView3;
    private ImageView mFrameView4;
    private ImageView mFrameView5;
    private ImageView mFrameView6;
    private ImageView mFrameView7;
    private ImageView mFrameView8;
    private ImageView mFrameView9;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(getArguments() != null) {
            mPhotoWidth = getArguments().getInt(Constant.PHOTO_VIEW_WIDTH_KEY);
            mPhotoHeight = getArguments().getInt(Constant.PHOTO_VIEW_HEIGHT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_frame,
                container, false);
        mFrameView1 = (ImageView) rootView.findViewById(R.id.frame1);
        mFrameView1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickFrame(Constant.FRAME1);
            }
        });

        mFrameView2 = (ImageView) rootView.findViewById(R.id.frame2);
        mFrameView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickFrame(Constant.FRAME2);
            }
        });

        mFrameView3 = (ImageView) rootView.findViewById(R.id.frame3);
        mFrameView3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME3);
            }
        });

        mFrameView4 = (ImageView) rootView.findViewById(R.id.frame4);
        mFrameView4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME4);
            }
        });

        mFrameView5 = (ImageView) rootView.findViewById(R.id.frame5);
        mFrameView5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME5);
            }
        });

        mFrameView6 = (ImageView) rootView.findViewById(R.id.frame6);
        mFrameView6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME6);
            }
        });

        mFrameView7 = (ImageView) rootView.findViewById(R.id.frame7);
        mFrameView7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME7);
            }
        });

        mFrameView8 = (ImageView) rootView.findViewById(R.id.frame8);
        mFrameView8.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME8);
            }
        });

        mFrameView9 = (ImageView) rootView.findViewById(R.id.frame9);
        mFrameView9.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickFrame(Constant.FRAME9);
            }
        });

        return rootView;
    }

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // View rootView = inflater.inflate(R.layout.fragment_select_frame,
    // container, false);
    // mGridView = (GridView) rootView.findViewById(R.id.gridview);
    // mGridView.setAdapter(new ImageAdapter(getActivity()));
    //
    // mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    // public void onItemClick(AdapterView<?> parent, View v,
    // int position, long id) {
    // int frame = Constant.FRAME1;
    // switch (position) {
    // case 0:
    // frame = Constant.FRAME1;
    // break;
    // case 1:
    // frame = Constant.FRAME2;
    // break;
    // case 2:
    // frame = Constant.FRAME3;
    // break;
    // case 3:
    // frame = Constant.FRAME4;
    // break;
    // case 4:
    // frame = Constant.FRAME5;
    // break;
    // case 5:
    // frame = Constant.FRAME6;
    // break;
    // case 6:
    // frame = Constant.FRAME7;
    // break;
    // case 7:
    // frame = Constant.FRAME8;
    // break;
    // case 8:
    // frame = Constant.FRAME9;
    // break;
    // default:
    // break;
    // }
    // clickFrame(frame);
    // }
    // });
    //
    // return rootView;
    // }

    public void clickFrame(int frame) {
        if (!already()) {
            return;
        }

        CreateFrameFragment fragment = new CreateFrameFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.FRAME_EXTRA_KEY, frame);
        bundle.putInt(Constant.PHOTO_VIEW_WIDTH_KEY, mPhotoWidth);
        bundle.putInt(Constant.PHOTO_VIEW_HEIGHT_KEY, mPhotoHeight);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity()
                .getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.frame_container, fragment,
                Constant.CREATE_FRAME_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        // set title
        getActivity().setTitle(R.string.create_frame);
    }
}
