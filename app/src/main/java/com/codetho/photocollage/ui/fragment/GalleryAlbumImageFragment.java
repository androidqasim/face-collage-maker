package com.codetho.photocollage.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.codetho.photocollage.R;
import com.codetho.photocollage.adapter.GalleryAlbumImageAdapter;

import java.util.ArrayList;

/**
 * Created by vanhu_000 on 3/26/2016.
 */
public class GalleryAlbumImageFragment extends BaseFragment {
    public interface OnSelectImageListener {
        void onSelectImage(String image);
    }

    public static final String ALBUM_IMAGE_EXTRA = "albumImage";
    public static final String ALBUM_NAME_EXTRA = "albumName";

    private GridView mGridView;
    private ArrayList<String> mImages;
    private OnSelectImageListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OnSelectImageListener) {
            mListener = (OnSelectImageListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_gallery_photo, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        String albumName = getString(R.string.album_image);
        if (getArguments() != null) {
            mImages = getArguments().getStringArrayList(ALBUM_IMAGE_EXTRA);
            albumName = getArguments().getString(ALBUM_NAME_EXTRA);
            if (mImages != null) {
                GalleryAlbumImageAdapter adapter = new GalleryAlbumImageAdapter(getActivity(), mImages);
                mGridView.setAdapter(adapter);
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(mListener != null){
                            mListener.onSelectImage(mImages.get(position));
                        }
                    }
                });
            }
        }
        setTitle(albumName);
        return view;
    }
}
