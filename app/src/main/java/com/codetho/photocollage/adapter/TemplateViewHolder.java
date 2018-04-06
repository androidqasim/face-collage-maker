package com.codetho.photocollage.adapter;

/**
 * Created by vanhu_000 on 3/25/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codetho.photocollage.R;
import com.codetho.photocollage.model.TemplateItem;
import com.codetho.photocollage.utils.ImageUtils;

/**
 * Simple view holder for a single text view.
 */
public class TemplateViewHolder extends RecyclerView.ViewHolder {
    public interface OnTemplateItemClickListener {
        void onTemplateItemClick(final TemplateItem item);
    }

    private ImageView mImageView;
    private TextView mTextView;
    private int mViewType = TemplateAdapter.VIEW_TYPE_CONTENT;

    TemplateViewHolder(View view, int viewType) {
        super(view);
        mViewType = viewType;
        mTextView = (TextView) view.findViewById(R.id.text);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
    }

    public void bindItem(final TemplateItem item, final OnTemplateItemClickListener listener) {
        if (mViewType == TemplateAdapter.VIEW_TYPE_HEADER && mTextView != null) {
            mTextView.setText(item.getHeader());
        } else if (mImageView != null) {
            ImageUtils.loadImageWithPicasso(mImageView.getContext(), mImageView, item.getPreview());
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTemplateItemClick(item);
                    }
                }
            });
        }
    }
}