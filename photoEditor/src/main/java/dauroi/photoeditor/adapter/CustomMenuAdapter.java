package dauroi.photoeditor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dauroi.photoeditor.R;
import dauroi.photoeditor.listener.OnBottomMenuItemClickListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.utils.PhotoUtils;

public class CustomMenuAdapter extends ArrayAdapter<ItemInfo> {
	private LayoutInflater mInflater;
	private boolean mBottomMenu = false;
	private OnBottomMenuItemClickListener mListener;
	private Animation mAnim;
	private boolean mShaking = false;

	public CustomMenuAdapter(Context context, List<ItemInfo> objects, boolean bottom) {
		super(context, R.layout.photo_editor_item_bottom_menu, objects);
		mInflater = LayoutInflater.from(context);
		mBottomMenu = bottom;
		mAnim = AnimationUtils.loadAnimation(getContext(), R.anim.photo_editor_shaking);
	}

	public void setListener(OnBottomMenuItemClickListener listener) {
		mListener = listener;
	}

	public void setShaking(boolean shaking) {
		mShaking = shaking;
		notifyDataSetChanged();
	}

	public boolean isShaking() {
		return mShaking;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ItemInfo item = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			if (mBottomMenu) {
				convertView = mInflater.inflate(R.layout.photo_editor_item_bottom_menu, parent, false);

				holder.normalLayout = convertView.findViewById(R.id.normalLayout);
				holder.thumbnailView = (ImageView) convertView.findViewById(R.id.thumbnailView);
				holder.selectedView = convertView.findViewById(R.id.selectedView);
				holder.nameView = (TextView) convertView.findViewById(R.id.nameView);

				holder.packageLayout = convertView.findViewById(R.id.packageLayout);
				holder.packageSeparatorView = convertView.findViewById(R.id.separatorView);
				holder.packageThumbnailView = (ImageView) convertView.findViewById(R.id.packageThumbnailView);
				holder.packageNameView = (TextView) convertView.findViewById(R.id.packageNameView);
				holder.packageSelectedView = convertView.findViewById(R.id.packageSelectedView);
				holder.packageDeleteView = convertView.findViewById(R.id.deleteView);

				holder.storeItemLayout = convertView.findViewById(R.id.storeItemLayout);
			} else {
				convertView = mInflater.inflate(R.layout.photo_editor_item_topbar_menu, parent, false);
				holder.thumbnailView = (ImageView) convertView.findViewById(R.id.thumbnailView);
				holder.selectedView = convertView.findViewById(R.id.selectedView);
				holder.nameView = (TextView) convertView.findViewById(R.id.nameView);
			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final int selectedColor = getContext().getResources().getColor(R.color.photo_editor_selected_text_main_topbar);
		final int normalColor = getContext().getResources().getColor(R.color.photo_editor_normal_text_main_topbar);

		if (!mBottomMenu) {
			holder.nameView.setText(item.getTitle());
			if (item.isSelected()) {
				holder.nameView.setTextColor(selectedColor);
				holder.selectedView.setVisibility(View.GONE);
				PhotoUtils.loadImageWithGlide(getContext(), holder.thumbnailView, item.getSelectedThumbnail());
			} else {
				holder.nameView.setTextColor(normalColor);
				holder.selectedView.setVisibility(View.GONE);
				PhotoUtils.loadImageWithGlide(getContext(), holder.thumbnailView, item.getThumbnail());
			}
		} else {
			holder.normalLayout.setVisibility(View.GONE);
			holder.packageLayout.setVisibility(View.GONE);
			holder.storeItemLayout.setVisibility(View.GONE);
			holder.packageSeparatorView.setVisibility(View.GONE);

			if (item.getShowingType() == ItemInfo.ADD_ITEM_TYPE) {
				holder.storeItemLayout.setVisibility(View.VISIBLE);
			} else if (item.getShowingType() == ItemInfo.PACKAGE_ITEM_TYPE) {
				PhotoUtils.loadImageWithGlide(getContext(), holder.packageThumbnailView, item.getThumbnail());
				holder.packageLayout.setVisibility(View.VISIBLE);
				holder.packageNameView.setText(item.getTitle());
				if (item.isSelected()) {
					holder.packageNameView.setTextColor(selectedColor);
					holder.packageSelectedView.setVisibility(View.GONE);
				} else {
					holder.packageNameView.setTextColor(normalColor);
					holder.packageSelectedView.setVisibility(View.GONE);
				}
			} else {
				holder.normalLayout.setVisibility(View.VISIBLE);
				holder.nameView.setText(item.getTitle());
				if (item.isSelected()) {
					holder.nameView.setTextColor(selectedColor);
					if(item.getSelectedThumbnail() != null && item.getSelectedThumbnail().length() > 0) {
						holder.selectedView.setVisibility(View.GONE);
						PhotoUtils.loadImageWithGlide(getContext(), holder.thumbnailView, item.getSelectedThumbnail());
					}else{
						holder.selectedView.setVisibility(View.GONE);
						PhotoUtils.loadImageWithGlide(getContext(), holder.thumbnailView, item.getThumbnail());
					}
				} else {
					holder.nameView.setTextColor(normalColor);
					holder.selectedView.setVisibility(View.GONE);
					PhotoUtils.loadImageWithGlide(getContext(), holder.thumbnailView, item.getThumbnail());
				}

				if (position + 1 < getCount() && getItem(position + 1).getShowingType() == ItemInfo.PACKAGE_ITEM_TYPE) {
					holder.packageSeparatorView.setVisibility(View.VISIBLE);
				} else {
					holder.packageSeparatorView.setVisibility(View.GONE);
				}
			}

			if (item.getShowingType() == ItemInfo.PACKAGE_ITEM_TYPE) {
				if (mShaking) {
					if (mAnim.hasEnded()) {
						mAnim.reset();
					}
					holder.packageDeleteView.setVisibility(View.VISIBLE);
					convertView.startAnimation(mAnim);
				} else {
					holder.packageDeleteView.setVisibility(View.GONE);
					convertView.clearAnimation();
				}
			} else {
				holder.packageDeleteView.setVisibility(View.GONE);
				convertView.clearAnimation();
			}

			if (mListener != null) {
				holder.packageThumbnailView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mListener.onMenuItemClick(position, item);
					}
				});

				holder.packageThumbnailView.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						mListener.onMenuItemLongClick(position, item);
						return true;
					}
				});

				holder.packageDeleteView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mListener.onDeleteButtonClick(position, item);
					}
				});

				holder.normalLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mListener.onMenuItemClick(position, item);
					}
				});

				holder.storeItemLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mListener.onMenuItemClick(position, item);
					}
				});
			}
		}

		return convertView;
	}

	private class ViewHolder {
		View normalLayout;
		ImageView thumbnailView;
		View selectedView;
		TextView nameView;

		View packageLayout;
		View packageSeparatorView;
		ImageView packageThumbnailView;
		TextView packageNameView;
		View packageSelectedView;
		View packageDeleteView;

		View storeItemLayout;
	}
}
