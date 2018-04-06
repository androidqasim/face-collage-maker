package dauroi.photoeditor.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import dauroi.photoeditor.R;
import dauroi.photoeditor.utils.DialogUtils;
import dauroi.photoeditor.utils.ImageDecoder;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;

public class ViewImageActivity extends BaseAdActivity {
	public static final String IMAGE_FILE_KEY = "imageFile";

	private View mActionLayout;
	private View mShareView;
	private View mEditView;
	private View mDeleteView;
	private ImageView mImageView;
	private String mImagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_editor_activity_view_image);
		mImagePath = getIntent().getStringExtra(IMAGE_FILE_KEY);
		// Inflate widgets
		mActionLayout = findViewById(R.id.actionLayout);
		mShareView = findViewById(R.id.shareView);
		mShareView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mImagePath != null && mImagePath.length() > 0){
					//postPhoto(mImagePath);
					String whiteImage = mImagePath;//.substring(0, mImagePath.length() - 4).concat(PhotoUtils.EDITED_WHITE_IMAGE_SUFFIX);
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("image/jpeg");
					share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(whiteImage)));
					startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
				}
			}
		});

		mEditView = findViewById(R.id.editView);
		mEditView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ViewImageActivity.this, ImageProcessingActivity.class);
			    Uri imageUri = Uri.fromFile(new File(mImagePath));
				i.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, imageUri);
				i.putExtra(ImageProcessingActivity.IS_EDITING_IMAGE_KEY, true);
				startActivity(i);
				finish();
			}
		});

		mDeleteView = findViewById(R.id.deleteView);
		mDeleteView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtils.showCoolConfirmDialog(ViewImageActivity.this, R.string.photo_editor_app_name, R.string.photo_editor_confirm_delete_image,
						new DialogUtils.ConfirmDialogOnClickListener() {
					@Override
					public void onOKButtonOnClick() {
						File file = new File(mImagePath);
						file.delete();
//						File thumbnail = new File(Utils.EDITED_IMAGE_THUMBNAIL_FOLDER, file.getName());
//						thumbnail.delete();
//						file = new File(mImagePath.substring(0, mImagePath.length() - 4).concat(PhotoUtils.EDITED_WHITE_IMAGE_SUFFIX));
//						file.delete();
						finish();
					}

					@Override
					public void onCancelButtonOnClick() {

					}
				});
			}
		});

		mImageView = (ImageView) findViewById(R.id.imageView);
		if (mImagePath != null) {
			mImageView.setImageBitmap(ImageDecoder.decodeFileToBitmap(mImagePath));
		}

		findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
