package dauroi.photoeditor.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.crash.FirebaseCrash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import dauroi.photoeditor.R;
import dauroi.photoeditor.api.FileService;
import dauroi.photoeditor.api.response.StoreItem;

public class StoreItemAdapter extends ArrayAdapter<StoreItem> {
    public interface OnEndListListener {
        void onEndList(int position);
    }

    public static interface OnStoreItemClickListener {
        public void onPriceButtonClick(final StoreItem item);

        public void onStoreItemClick(final StoreItem item);
    }

    private LayoutInflater mInflater;
    private OnStoreItemClickListener mListener;
    private OnEndListListener mEndListListener;
    private List<StoreItem> mItems;

    public StoreItemAdapter(Context context, List<StoreItem> objects, OnStoreItemClickListener listener) {
        super(context, R.layout.photo_editor_store_item, objects);
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mItems = objects;
    }

    public void setEndListListener(OnEndListListener endListListener) {
        this.mEndListListener = endListListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.photo_editor_store_item, parent, false);
            holder = new ViewHolder();
            holder.thumbnailView = (ImageView) convertView.findViewById(R.id.thumbnailView);
            holder.titleView = (TextView) convertView.findViewById(R.id.titleView);
            holder.permissionView = (TextView) convertView.findViewById(R.id.permissionView);
            holder.priceView = (TextView) convertView.findViewById(R.id.priceView);
            holder.itemCountView = (TextView) convertView.findViewById(R.id.itemCountView);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.descriptionView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            final StoreItem item = getItem(position);
            if (item != null) {
                if (item.getThumbnail().startsWith("http://") || item.getThumbnail().startsWith("https://")) {
                    // load offline mode
                    Glide.with(getContext()).load(item.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade()
                            .into(holder.thumbnailView);
                } else {
                    final String thumnailURL = FileService.getUploadedPath(null, item.getThumbnail(), "image");
                    Glide.with(getContext()).load(thumnailURL).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {

                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    item.setOriginalThumbnail(item.getThumbnail());
                                    item.setThumbnail(thumnailURL);
                                    return false;
                                }
                            }).into(holder.thumbnailView);

                }

                holder.titleView.setText(item.getTitle());
                holder.itemCountView
                        .setText("" + item.getItemCount() + " " + getContext().getString(R.string.photo_editor_item));
                holder.descriptionView.setText(item.getDescription());
                if (item.getPermission().equalsIgnoreCase(StoreItem.VIP_PERMISSION)) {
                    holder.permissionView.setVisibility(View.VISIBLE);
                } else {
                    holder.permissionView.setVisibility(View.GONE);
                }

                if (item.getDownloadStatus() != StoreItem.STATUS_ONLINE) {
                    if (item.getDownloadStatus() == StoreItem.STATUS_DOWNLOADED) {
                        holder.priceView.setText(getContext().getString(R.string.photo_editor_used));
                    } else {
                        holder.priceView.setText(getContext().getString(R.string.photo_editor_downloading));
                    }
                    holder.priceView.setBackgroundResource(R.drawable.photo_editor_bg_price_view_use);
                } else {
                    if (item.getPrice() > 0) {
                        holder.priceView.setText(item.getPrice() + "$");
                    } else {
                        holder.priceView.setText(getContext().getString(R.string.photo_editor_free));
                    }
                    holder.priceView.setBackgroundResource(R.drawable.photo_editor_bg_price_view_normal);
                }

                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onStoreItemClick(item);
                        }
                    }
                });

                holder.priceView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onPriceButtonClick(item);
                        }
                    }
                });
            } else {
                FirebaseCrash.report(new Exception("Item is null at " + position));
            }
        } else {
            FirebaseCrash.report(new Exception("View Holder is null at " + position));
        }

        if (mEndListListener != null && position > 0 && position == mItems.size() - 1) {
            mEndListListener.onEndList(position);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView thumbnailView;
        TextView titleView;
        TextView permissionView;
        TextView priceView;
        TextView itemCountView;
        TextView descriptionView;
    }
}
