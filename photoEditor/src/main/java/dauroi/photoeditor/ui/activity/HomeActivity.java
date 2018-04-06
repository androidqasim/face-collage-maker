package dauroi.photoeditor.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.R;
import dauroi.photoeditor.adapter.EditedImageAdaper;
import dauroi.photoeditor.api.StoreItemService;
import dauroi.photoeditor.api.response.ListStoreItemResponse;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.model.EditedImageItem;
import dauroi.photoeditor.utils.DialogUtils;
import dauroi.photoeditor.utils.DialogUtils.OnAddImageButtonClickListener;
import dauroi.photoeditor.utils.GsonUtils;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.TempDataContainer;
import dauroi.photoeditor.utils.Utils;

public class HomeActivity extends BaseAdActivity implements OnAddImageButtonClickListener {
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_CAPTURE_IMAGE = 2;
    private static final int REQUEST_EDIT_IMAGE = 3;

    private Animation mAnimation;
    private View mAddImageView;
    private Dialog mAddImageDialog;
    private View mEditImageView;
    private Dialog mEditImageDialog;
    private Uri mImageUri;
    private View mNewItemView;
    private GridView mGridView;
    private EditedImageAdaper mImageAdapter;
    private List<EditedImageItem> mImages = new ArrayList<EditedImageItem>();
    private DialogUtils.EditedImageLongClickListener mImageClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_editor_activity_home);
        mNewItemView = findViewById(R.id.newItemView);
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent(HomeActivity.this, ViewImageActivity.class);
                data.putExtra(ViewImageActivity.IMAGE_FILE_KEY, mImages.get(position).getImage());
                startActivity(data);
            }
        });

        findViewById(R.id.storeLayout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, StoreActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.addImageButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAddImageDialog != null && !mAddImageDialog.isShowing()) {
                    mAddImageView.startAnimation(mAnimation);
                    mAddImageDialog.show();
                }
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAddImageDialog = DialogUtils.createAddImageDialog(this, this, false);
        mAddImageView = mAddImageDialog.findViewById(R.id.dialogAddImage);
        mImageClickListener = new DialogUtils.EditedImageLongClickListener() {

            @Override
            public void onShareButtonClick() {
                mEditImageDialog.dismiss();
                EditedImageItem item = getImageItem();
                if (item != null) {
                    String imagePath = item.getImage().substring(0, item.getImage().length() - 4).concat(PhotoUtils.EDITED_WHITE_IMAGE_SUFFIX);
                    // postPhoto(imagePath);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
                    startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
                }
            }

            @Override
            public void onEditButtonClick() {
                mEditImageDialog.dismiss();
                EditedImageItem item = getImageItem();
                if (item != null) {
                    Intent i = new Intent(HomeActivity.this, ImageProcessingActivity.class);
                    mImageUri = Uri.fromFile(new File(item.getImage()));
                    i.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, mImageUri);
                    i.putExtra(ImageProcessingActivity.IS_EDITING_IMAGE_KEY, true);
                    startActivityForResult(i, REQUEST_EDIT_IMAGE);
                }
            }

            @Override
            public void onDeleteButtonClick() {
                mEditImageDialog.dismiss();
                DialogUtils.showCoolConfirmDialog(HomeActivity.this, R.string.photo_editor_app_name, R.string.photo_editor_confirm_delete_image, new DialogUtils.ConfirmDialogOnClickListener() {
                    @Override
                    public void onOKButtonOnClick() {
                        EditedImageItem item = getImageItem();
                        if (item != null) {
                            File file = new File(item.getThumbnail());
                            file.delete();
                            file = new File(item.getImage());
                            file.delete();
                            file = new File(item.getImage().substring(0, item.getImage().length() - 4).concat(PhotoUtils.EDITED_WHITE_IMAGE_SUFFIX));
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

        mEditImageDialog = DialogUtils.createEditImageDialog(this, mImageClickListener, false);

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

        mImageAdapter = new EditedImageAdaper(this, mImages);
        mGridView.setAdapter(mImageAdapter);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.photo_editor_slide_in_bottom);
    }

    @Override
    public void finish() {
        if (getAdCreator() != null)
            getAdCreator().showGoogleInterstitialAd();
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserImagesAsync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TempDataContainer.getInstance().setCheckShowingAdsResponse(null);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                    Intent i = new Intent(HomeActivity.this, ImageProcessingActivity.class);
                    i.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, mImageUri);
                    startActivityForResult(i, REQUEST_EDIT_IMAGE);
                }
                break;
            case REQUEST_CAPTURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                    Intent i = new Intent(HomeActivity.this, ImageProcessingActivity.class);
                    i.putExtra(ImageProcessingActivity.ROTATION_KEY, 90);
                    i.putExtra(ImageProcessingActivity.EXTRA_FLIP_IMAGE, data.getBooleanExtra(CameraActivity.EXTRA_FLIP_IMAGE, false));
                    i.putExtra(ImageProcessingActivity.IMAGE_URI_KEY, mImageUri);
                    startActivityForResult(i, REQUEST_EDIT_IMAGE);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    @Override
    public void onCameraButtonClick() {
        mAddImageDialog.dismiss();
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, REQUEST_CAPTURE_IMAGE);
    }

    @Override
    public void onGalleryButtonClick() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
        mAddImageDialog.dismiss();
    }

    private void loadUserImagesAsync() {
        AsyncTask<Void, EditedImageItem, Void> task = new AsyncTask<Void, EditedImageItem, Void>() {
            boolean newItem = false;

            @Override
            protected Void doInBackground(Void... params) {
                File thumbnailFolder = new File(Utils.EDITED_IMAGE_THUMBNAIL_FOLDER);
                File[] editedThumbnails = thumbnailFolder.listFiles();
                EditedImageItem[] items = null;
                if (editedThumbnails != null && editedThumbnails.length > 0) {
                    final int len = editedThumbnails.length;
                    items = new EditedImageItem[len];
                    for (int idx = 0; idx < len; idx++) {
                        File file = editedThumbnails[idx];
                        EditedImageItem item = new EditedImageItem();
                        item.setThumbnail(file.getAbsolutePath());
                        File thumbnail = new File(
                                Utils.EDITED_IMAGE_FOLDER.concat("/").concat(file.getName()));
                        item.setImage(thumbnail.getAbsolutePath());
                        items[idx] = item;
                    }
                }

                publishProgress(items);

                try {
                    newItem = checkNewItem();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                if (newItem) {
                    mNewItemView.setVisibility(View.VISIBLE);
                } else {
                    mNewItemView.setVisibility(View.GONE);
                }
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean checkNewItem() throws Exception {
        final ListStoreItemResponse resp = StoreItemService.getStoreItems(null, null, StoreActivity.LANGUAGE, 0, 10);
        List<StoreItem> newItems = new ArrayList<StoreItem>();
        if (resp != null) {
            newItems = resp.getItems();
        }

        if (newItems == null || newItems.isEmpty()) {
            return false;
        }
        // offline items
//        SharedPreferences pref = getSharedPreferences(StoreActivity.STORE_ITEM_PREF, Context.MODE_PRIVATE);
//        String text = pref.getString(StoreActivity.STORE_ITEM_KEY, null);
//        if (text != null && text.length() > 0) {
//            Gson gson = GsonUtils.createAndroidStyleGson();
//            Type collectionType = new TypeToken<List<StoreItem>>() {
//            }.getType();
//            List<StoreItem> items = gson.fromJson(text, collectionType);
//            if (newItems != null && newItems.size() > 0) {
//                for (StoreItem item : items)
//                    if (item.getIdString().equals(newItems.get(0).getIdString())) {
//                        return false;
//                    }
//            }
//        }

        return true;
    }
}
