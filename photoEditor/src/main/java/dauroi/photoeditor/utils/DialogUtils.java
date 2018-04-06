package dauroi.photoeditor.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import dauroi.photoeditor.R;
import dauroi.photoeditor.model.EditedImageItem;
import dauroi.photoeditor.view.FingerPaintView;
import dauroi.photoeditor.view.PreviewDrawingView;

/**
 * Show custom dialog. Custom dialog with it own style!
 */
@SuppressLint("InflateParams")
public class DialogUtils {
    /**
     * using in confirm dialog
     */
    public static interface InputDialogOnClickListener {
        public void onOKButtonOnClick(String text);

        public void onCancelButtonOnClick();
    }

    public static interface OnAddImageButtonClickListener {
        public void onCameraButtonClick();

        public void onGalleryButtonClick();
    }

    public static abstract class EditedImageLongClickListener {
        private EditedImageItem mImageItem;

        public void setImageItem(EditedImageItem imageItem) {
            mImageItem = imageItem;
        }

        public EditedImageItem getImageItem() {
            return mImageItem;
        }

        public abstract void onShareButtonClick();

        public abstract void onEditButtonClick();

        public abstract void onDeleteButtonClick();
    }

    public static Dialog showCoolConfirmDialog(Context context, int titleResId, int messageResId,
                                               final ConfirmDialogOnClickListener listener) {
        String title = context.getResources().getString(titleResId);
        String message = context.getResources().getString(messageResId);
        if (Build.VERSION.SDK_INT > 20) {
            return showConfirmDialog(context, title, message, listener);
        } else {
            Dialog dialog = createCustomConfirmDialog(context, title, message, listener);
            dialog.show();
            return dialog;
        }
    }

    public static Dialog createCustomProgressDialog(final Activity context, final String title, final String content,
                                                    final String buttonName, final DialogOnClickListener listener, boolean cancelable) {
        final Dialog myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setTitle(title);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.photo_editor_dialog_progress_with_button_custom, null);
        myDialog.setContentView(rootView);
        myDialog.setCancelable(cancelable);
        final TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        titleView.setText(title);
        final TextView contentView = (TextView) rootView.findViewById(R.id.contentView);
        contentView.setText(content);
        final View cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setVisibility(View.GONE);

        final TextView okButton = (TextView) rootView.findViewById(R.id.okButton);
        okButton.setText(buttonName);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (listener != null) {
                    listener.onOKButtonOnClick();
                }
            }
        });
        return myDialog;
    }


    public static Dialog createCustomProgressDialog(final Activity context, final String title, final String content, final boolean showTitle, boolean cancelable) {
        final Dialog myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setTitle(title);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.photo_editor_dialog_progress_custom, null);
        myDialog.setContentView(rootView);
        myDialog.setCancelable(cancelable);

        final TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        titleView.setText(title);
        final TextView contentView = (TextView) rootView.findViewById(R.id.contentView);
        contentView.setText(content);
        if (showTitle) {
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }
        return myDialog;
    }

    public static Dialog createCustomInputDialog(final Activity context, final String title, final String content, final InputDialogOnClickListener listener) {
        return createCustomInputDialog(context, title, content, context.getString(R.string.photo_editor_ok), context.getString(R.string.photo_editor_cancel), listener);
    }

    public static Dialog createCustomInputDialog(final Activity context, final String title, final String content,
                                                 String confirmText, String cancelText, final InputDialogOnClickListener listener) {
        final Dialog myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.photo_editor_dialog_input, null);
        final TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        titleView.setText(title);
        final EditText contentView = (EditText) rootView.findViewById(R.id.contentView);
        if (content != null && content.length() > 0) {
            contentView.setText(content);
            try {
                contentView.setSelection(content.length());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        final TextView cancelButton = (TextView) rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (listener != null) {
                    listener.onCancelButtonOnClick();
                }
            }
        });

        final TextView okButton = (TextView) rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (listener != null) {
                    listener.onOKButtonOnClick(contentView.getText().toString());
                }
            }
        });

        cancelButton.setText(cancelText);
        okButton.setText(confirmText);

        myDialog.setTitle(title);
        myDialog.setContentView(rootView);
        myDialog.setCancelable(true);
        return myDialog;
    }

    public static Dialog createCustomOkDialog(final Activity context, final String title, final String content) {
        return createCustomOkDialog(context, title, content, new DialogOnClickListener() {
            @Override
            public void onOKButtonOnClick() {

            }
        });
    }

    public static Dialog createCustomOkDialog(final Activity context, final String title, final String content, final DialogOnClickListener listener) {
        final Dialog myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setTitle(title);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.photo_editor_dialog_confirm, null);
        myDialog.setContentView(rootView);
        myDialog.setCancelable(true);
        final TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        titleView.setText(title);
        final TextView contentView = (TextView) rootView.findViewById(R.id.contentView);
        contentView.setText(content);
        final View cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setVisibility(View.GONE);

        final View okButton = rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (listener != null) {
                    listener.onOKButtonOnClick();
                }
            }
        });
        return myDialog;
    }

    public static Dialog createCustomConfirmDialog(final Context context, final String title, final String content, final ConfirmDialogOnClickListener listener) {
        return createCustomConfirmDialog(context, title, content, context.getString(R.string.photo_editor_ok), context.getString(R.string.photo_editor_cancel), listener);
    }

    public static Dialog createCustomConfirmDialog(final Context context, final String title, final String content,
                                                   String confirmText, String cancelText, final ConfirmDialogOnClickListener listener) {
        final Dialog myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.photo_editor_dialog_confirm, null);
        final TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        titleView.setText(title);
        final TextView contentView = (TextView) rootView.findViewById(R.id.contentView);
        contentView.setText(content);
        final TextView cancelButton = (TextView) rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (listener != null) {
                    listener.onCancelButtonOnClick();
                }
            }
        });

        final TextView okButton = (TextView) rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                if (listener != null) {
                    listener.onOKButtonOnClick();
                }
            }
        });

        cancelButton.setText(cancelText);
        okButton.setText(confirmText);
        myDialog.setTitle(title);
        myDialog.setContentView(rootView);
        myDialog.setCancelable(true);
        return myDialog;
    }

    public static Dialog createEditImageDialog(final Context context, final EditedImageLongClickListener listener,
                                               boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.photo_editor_dialog_edited_image, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);
        View shareButton = rootView.findViewById(R.id.shareView);
        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShareButtonClick();
                }
            }
        });

        View editButton = rootView.findViewById(R.id.editView);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditButtonClick();
                }
            }
        });

        View deleteButton = rootView.findViewById(R.id.deleteView);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteButtonClick();
                }
            }
        });

        View cancelView = rootView.findViewById(R.id.cancelView);
        cancelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (show) {
            try {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.photo_editor_slide_in_bottom);
                rootView.startAnimation(anim);
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return dialog;
    }

    public static Dialog createAddImageDialog(final Context context, final OnAddImageButtonClickListener listener,
                                              boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.photo_editor_dialog_add_image, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);
        View cameraButton = rootView.findViewById(R.id.cameraView);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCameraButtonClick();
                }
            }
        });

        View galleryButton = rootView.findViewById(R.id.galleryView);
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGalleryButtonClick();
                }
            }
        });

        View cancelView = rootView.findViewById(R.id.cancelView);
        cancelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (show) {
            try {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.photo_editor_slide_in_bottom);
                rootView.startAnimation(anim);
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return dialog;
    }

    public static Dialog createDrawEffectDialog(final Context context, final OnSelectDrawEffectListener listener,
                                                boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.photo_editor_dialog_paint_effect, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);

        rootView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        RadioButton normalButton = (RadioButton) rootView.findViewById(R.id.normalButton);
        normalButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && listener != null) {
                    listener.onSelectEffect(FingerPaintView.DRAW_EFFECT_NORMAL);
                }
            }
        });

        RadioButton blurButton = (RadioButton) rootView.findViewById(R.id.blurButton);
        blurButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && listener != null) {
                    listener.onSelectEffect(FingerPaintView.DRAW_EFFECT_BLUR);
                }
            }
        });

        RadioButton embossButton = (RadioButton) rootView.findViewById(R.id.embossButton);
        embossButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && listener != null) {
                    listener.onSelectEffect(FingerPaintView.DRAW_EFFECT_EMBOSS);
                }
            }
        });

        RadioButton srcButton = (RadioButton) rootView.findViewById(R.id.srcATopButton);
        srcButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && listener != null) {
                    listener.onSelectEffect(FingerPaintView.DRAW_EFFECT_SRC_A_TOP);
                }
            }
        });

        if (show) {
            dialog.show();
        }

        return dialog;
    }

    public static Dialog createPreviewDrawingDialog(final Context context, final OnSelectPaintSizeListener listener,
                                                    boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.photo_editor_dialog_select_paint_size, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);

        rootView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final float maxWidth = context.getResources().getDimension(R.dimen.photo_editor_max_finger_width);
        final float minWidth = context.getResources().getDimension(R.dimen.photo_editor_min_finger_width);

        final PreviewDrawingView drawingView = (PreviewDrawingView) rootView.findViewById(R.id.drawingView);
        drawingView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                drawingView.init(drawingView.getWidth(), drawingView.getHeight());
                // remove listener
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    drawingView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    drawingView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        final SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = (maxWidth - minWidth) * (progress / 100.0f) + minWidth;
                drawingView.setPaintSize(size);
            }
        });

        View okButton = rootView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onSelectPaintSize(drawingView.getPaintSize());
                }
            }
        });

        View cancelButton = rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (show) {
            dialog.show();
        }

        return dialog;
    }

    /**
     * Show dialog with string resource
     *
     * @param context      : context that dialog will be shown
     * @param titleResId   : title string resource id
     * @param messageResId : message string resource id
     * @return
     */
    public static Dialog showDialog(Context context, int titleResId, int messageResId) {
        return showDialog(context, titleResId, messageResId, null);
    }

    /**
     * Show dialog with string resource
     *
     * @param context      : context that dialog will be shown
     * @param titleResId   : title string resource id
     * @param messageResId : message string resource id
     * @param listener
     * @return
     */
    public static Dialog showDialog(Context context, int titleResId, int messageResId, DialogOnClickListener listener) {
        String title = context.getResources().getString(titleResId);
        String message = context.getResources().getString(messageResId);

        return showDialog(context, title, message, listener);
    }

    /**
     * Show dialog
     *
     * @param context context that dialog will be shown
     * @param title   title of dialog
     * @param message message of dialog
     * @return
     */
    public static Dialog showDialog(Context context, String title, String message,
                                    final DialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onOKButtonOnClick();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        return alert;
    }

    /**
     * Show dialog click ok action
     *
     * @param context context that dialog will be shown
     * @return
     */
    public static Dialog showDialogOkClick(Context context, int titleResId, int messageResId, int titleOk,
                                           DialogInterface.OnClickListener clickListener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        String title = context.getResources().getString(titleResId);
        String message = context.getResources().getString(messageResId);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(titleOk, clickListener);
        AlertDialog alert = builder.create();
        alert.show();

        return alert;
    }

    /**
     * Show confirm dialog with string resource (Yes/No dialog)
     *
     * @param titleResId   title string resource id
     * @param messageResId message string resource id
     * @param listener     handle event when click button Yes/No
     * @return
     */
    public static Dialog showConfirmDialog(Context context, int titleResId, int messageResId,
                                           final ConfirmDialogOnClickListener listener) {
        String title = context.getResources().getString(titleResId);
        String message = context.getResources().getString(messageResId);

        return showConfirmDialog(context, title, message, listener);
    }

    /**
     * Show confirm dialog (Yes/No dialog)
     *
     * @param context     context that dialog will be shown
     * @param okResId
     * @param cancelResId
     * @param listener    handle event when click button Yes/No
     * @return
     */
    public static Dialog showConfirmDialog(Context context, int titleResId, int messageResId, int okResId,
                                           int cancelResId, final ConfirmDialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResId).setMessage(messageResId).setCancelable(false)
                .setPositiveButton(okResId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null)
                            listener.onOKButtonOnClick();
                    }
                }).setNegativeButton(cancelResId, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (listener != null)
                    listener.onCancelButtonOnClick();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        return alert;
    }

    /**
     * Show confirm dialog (Yes/No dialog)
     *
     * @param context  context that dialog will be shown
     * @param title    title of dialog
     * @param message  message of dialog
     * @param listener handle event when click button Yes/No
     * @return
     */
    public static Dialog showConfirmDialog(Context context, String title, String message,
                                           final ConfirmDialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null)
                            listener.onOKButtonOnClick();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (listener != null)
                    listener.onCancelButtonOnClick();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        return alert;
    }

    /**
     * using in confirm dialog
     */
    public static interface ConfirmDialogOnClickListener {
        public void onOKButtonOnClick();

        public void onCancelButtonOnClick();
    }

    /**
     * using in normal dialog
     */
    public static interface DialogOnClickListener {
        public void onOKButtonOnClick();
    }

    public static interface OnSelectPaintSizeListener {
        public void onSelectPaintSize(float size);
    }

    public static interface OnSelectDrawEffectListener {
        public void onSelectEffect(int effect);
    }
}
