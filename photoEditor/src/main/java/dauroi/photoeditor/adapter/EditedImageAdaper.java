package dauroi.photoeditor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import dauroi.photoeditor.R;
import dauroi.photoeditor.model.EditedImageItem;
import dauroi.photoeditor.utils.PhotoUtils;

public class EditedImageAdaper extends ArrayAdapter<EditedImageItem> {
	private LayoutInflater mInflater;

	public EditedImageAdaper(Context context, List<EditedImageItem> objects) {
		super(context, R.layout.photo_editor_edited_image_item, objects);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.photo_editor_edited_image_item, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final EditedImageItem item = getItem(position);
		PhotoUtils.loadImageWithGlide(getContext(), holder.imageView, item.getThumbnail());
		return convertView;
	}

	private class ViewHolder {
		ImageView imageView;
	}
}
