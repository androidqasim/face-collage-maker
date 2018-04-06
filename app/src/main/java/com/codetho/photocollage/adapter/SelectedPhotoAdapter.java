package com.codetho.photocollage.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codetho.photocollage.R;

import java.util.ArrayList;

import dauroi.photoeditor.utils.PhotoUtils;

/**
 * Created by vanhu_000 on 3/27/2016.
 */
public class SelectedPhotoAdapter extends RecyclerView.Adapter<SelectedPhotoAdapter.SelectedPhotoViewHolder> {
    public static class SelectedPhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private View mDeleteView;

        SelectedPhotoViewHolder(View itemView, boolean imageFitCenter) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.selectedImage);
            mDeleteView = itemView.findViewById(R.id.deleteView);
            if (imageFitCenter) {
                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    public static interface OnDeleteButtonClickListener {
        void onDeleteButtonClick(String image);
    }

    private ArrayList<String> mImages;
    private OnDeleteButtonClickListener mListener;
    private boolean mImageFitCenter = false;

    public SelectedPhotoAdapter(ArrayList<String> images, OnDeleteButtonClickListener listener) {
        mImages = images;
        mListener = listener;
    }

    public void setImageFitCenter(boolean imageFitCenter) {
        mImageFitCenter = imageFitCenter;
        notifyDataSetChanged();
    }

    @Override
    public SelectedPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_photo, parent, false);
        return new SelectedPhotoViewHolder(v, mImageFitCenter);
    }

    @Override
    public void onBindViewHolder(SelectedPhotoViewHolder holder, final int position) {
        PhotoUtils.loadImageWithGlide(holder.mImageView.getContext(), holder.mImageView, mImages.get(position));
        holder.mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeleteButtonClick(mImages.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }
}
