package com.codetho.photocollage.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codetho.photocollage.R;
import com.codetho.photocollage.model.TemplateItem;

import java.util.ArrayList;

import dauroi.photoeditor.utils.PhotoUtils;

/**
 * Created by vanhu_000 on 3/28/2016.
 */
public class HorizontalPreviewTemplateAdapter extends RecyclerView.Adapter<HorizontalPreviewTemplateAdapter.PreviewTemplateViewHolder> {
    public static class PreviewTemplateViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private View mSelectedView;

        PreviewTemplateViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mSelectedView = itemView.findViewById(R.id.selectedView);
        }
    }

    public static interface OnPreviewTemplateClickListener {
        void onPreviewTemplateClick(TemplateItem item);
    }

    private ArrayList<TemplateItem> mTemplateItems;
    private OnPreviewTemplateClickListener mListener;

    public HorizontalPreviewTemplateAdapter(ArrayList<TemplateItem> items, OnPreviewTemplateClickListener listener) {
        mTemplateItems = items;
        mListener = listener;
    }

    @Override
    public PreviewTemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_template_hor, parent, false);
        return new PreviewTemplateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PreviewTemplateViewHolder holder, final int position) {
        PhotoUtils.loadImageWithGlide(holder.mImageView.getContext(), holder.mImageView, mTemplateItems.get(position).getPreview());
        if (mTemplateItems.get(position).isSelected()) {
            holder.mSelectedView.setVisibility(View.VISIBLE);
        } else {
            holder.mSelectedView.setVisibility(View.GONE);
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPreviewTemplateClick(mTemplateItems.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTemplateItems.size();
    }
}
