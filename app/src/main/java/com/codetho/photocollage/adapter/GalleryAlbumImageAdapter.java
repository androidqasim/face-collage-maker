package com.codetho.photocollage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codetho.photocollage.R;

import java.util.List;

import dauroi.photoeditor.utils.PhotoUtils;

/**
 * Created by vanhu_000 on 3/26/2016.
 */
public class GalleryAlbumImageAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private boolean mImageFitCenter = false;

    public GalleryAlbumImageAdapter(Context context, List<String> objects) {
        super(context, R.layout.item_gallery_photo, objects);
        mInflater = LayoutInflater.from(context);
    }

    public void setImageFitCenter(boolean imageFitCenter) {
        mImageFitCenter = imageFitCenter;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gallery_photo, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            if(mImageFitCenter){
                holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PhotoUtils.loadImageWithGlide(getContext(), holder.imageView, getItem(position));
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}
