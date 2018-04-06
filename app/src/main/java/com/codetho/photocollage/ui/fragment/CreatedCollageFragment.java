package com.codetho.photocollage.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.codetho.photocollage.R;
import com.codetho.photocollage.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.adapter.EditedImageAdaper;
import dauroi.photoeditor.model.EditedImageItem;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.ui.activity.ViewImageActivity;
import dauroi.photoeditor.utils.DialogUtils;

public class CreatedCollageFragment extends BaseFragment {
    private Animation mAnimation;
    private View mEditImageView;
    private Dialog mEditImageDialog;
    private GridView mGridView;
    private EditedImageAdaper mImageAdapter;
    private List<EditedImageItem> mImages = new ArrayList<EditedImageItem>();
    private DialogUtils.EditedImageLongClickListener mImageClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_created_collage, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent(mActivity, ViewImageActivity.class);
                data.putExtra(ViewImageActivity.IMAGE_FILE_KEY, mImages.get(position).getImage());
                startActivity(data);
            }
        });

        mImageClickListener = new DialogUtils.EditedImageLongClickListener() {

            @Override
            public void onShareButtonClick() {
                mEditImageDialog.dismiss();
                EditedImageItem item = getImageItem();
                if (item != null) {
                    String imagePath = item.getImage();
                    // postPhoto(imagePath);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
                    startActivity(Intent.createChooser(share, getString(dauroi.photoeditor.R.string.photo_editor_share_image)));
                }
            }

            @Override
            public void onEditButtonClick() {
                mEditImageDialog.dismiss();
                EditedImageItem item = getImageItem();
                if (item != null) {
                    Intent i = new Intent(mActivity, ImageProcessingActivity.class);
                    Uri mImageUri = Uri.fromFile(new File(item.getImage()));
                    i.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, mImageUri);
                    i.putExtra(ImageProcessingActivity.IS_EDITING_IMAGE_KEY, true);
                    startActivityForResult(i, REQUEST_EDIT_IMAGE);
                }
            }

            @Override
            public void onDeleteButtonClick() {
                mEditImageDialog.dismiss();
                DialogUtils.showCoolConfirmDialog(mActivity, R.string.app_name, R.string.photo_editor_confirm_delete_image, new DialogUtils.ConfirmDialogOnClickListener() {
                    @Override
                    public void onOKButtonOnClick() {
                        EditedImageItem item = getImageItem();
                        if (item != null) {
                            File file = new File(item.getThumbnail());
                            file.delete();
                            mImages.remove(item);
                            mImageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelButtonOnClick() {

                    }
                });
            }
        };

        mEditImageDialog = DialogUtils.createEditImageDialog(mActivity, mImageClickListener, false);
        mEditImageView = mEditImageDialog.findViewById(R.id.dialogEditImage);
        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mEditImageDialog != null && !mEditImageDialog.isShowing()) {
                    mImageClickListener.setImageItem(mImages.get(position));
                    mEditImageView.startAnimation(mAnimation);
                    mEditImageDialog.show();
                }
                return true;
            }
        });

        mImageAdapter = new EditedImageAdaper(mActivity, mImages);
        mGridView.setAdapter(mImageAdapter);
        mAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.photo_editor_slide_in_bottom);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserImagesAsync();
    }

    private void loadUserImagesAsync() {
        AsyncTask<Void, EditedImageItem, Void> task = new AsyncTask<Void, EditedImageItem, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File thumbnailFolder = new File(ImageUtils.OUTPUT_COLLAGE_FOLDER);
                File[] editedThumbnails = thumbnailFolder.listFiles();
                EditedImageItem[] items = null;
                if (editedThumbnails != null && editedThumbnails.length > 0) {
                    final int len = editedThumbnails.length;
                    items = new EditedImageItem[len];
                    for (int idx = 0; idx < len; idx++) {
                        File file = editedThumbnails[idx];
                        EditedImageItem item = new EditedImageItem();
                        item.setThumbnail(file.getAbsolutePath());
                        File thumbnail = new File(ImageUtils.OUTPUT_COLLAGE_FOLDER.concat("/").concat(file.getName()));
                        item.setImage(thumbnail.getAbsolutePath());
                        items[idx] = item;
                    }
                }

                publishProgress(items);
                return null;
            }

            @Override
            protected void onProgressUpdate(EditedImageItem... values) {
                super.onProgressUpdate(values);
                if (values != null && values.length > 0) {
                    mImages.clear();
                    for (EditedImageItem item : values) {
                        mImages.add(item);
                    }

                    mImageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
