package com.codetho.photocollage.utils;

import com.codetho.photocollage.R;
import com.codetho.photocollage.ui.custom.OptionBorderView;
import com.codetho.photocollage.ui.custom.OptionShadowView;
import com.codetho.photocollage.ui.fragment.CreateFrameFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

/**
 * Show custom dialog. Custom dialog with it own style!
 */
@SuppressLint("InflateParams")
public class DialogUtils {
    public static final int ITEM_DIALOG_TYPE = 0;
    public static final int STICKER_DIALOG_TYPE = 2;
    public static final int CREATE_FRAME_DIALOG_TYPE = 3;

    @SuppressLint("InflateParams")
    public static Dialog createSelectPhotoDialog(final Context context,
                                                 final OnEditImageMenuClickListener listener,
                                                 final OnAddImageButtonClickListener addImageListener, boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.dialog_select_photo, null);
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancelEdit();
                }
            }
        });

        View cameraButton = rootView.findViewById(R.id.cameraView);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (addImageListener != null) {
                    addImageListener.onCameraButtonClick();
                }
            }
        });

        View libraryView = rootView.findViewById(R.id.libraryView);
        libraryView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (addImageListener != null) {
                    addImageListener.onGalleryButtonClick();
                }
            }
        });

        View stickerView = rootView.findViewById(R.id.stickerView);
        stickerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (addImageListener != null) {
                    addImageListener.onStickerButtonClick();
                }
            }
        });

        View alterBackground = rootView.findViewById(R.id.alterBackgroundView);
        alterBackground.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onAlterBackgroundButtonClick();
                }
            }
        });

        View borderView = rootView.findViewById(R.id.borderShadowView);
        borderView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onBorderAndShaderButtonClick();
                }
            }
        });

        View colorView = rootView.findViewById(R.id.borderColorView);
        colorView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onColorBorderButtonClick();
                }
            }
        });

        View cancelView = rootView.findViewById(R.id.cancelView);
        cancelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancelEdit();
                }
            }
        });

        if (show) {
            try {
                Animation anim = AnimationUtils.loadAnimation(context,
                        R.anim.slide_in_bottom);
                rootView.startAnimation(anim);
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return dialog;
    }

    public static Dialog createGuideDialog(Context context, boolean show) {
        // LayoutInflater inflater = LayoutInflater.from(context);
        // View rootView = inflater.inflate(R.layout.dialog_gesture, null);
        // AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // builder.setView(rootView);
        // final Dialog dialog = builder.create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.dialog_gesture, null);
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(true);

        if (show) {
            Animation anim = AnimationUtils.loadAnimation(context,
                    R.anim.slide_in_bottom);
            rootView.startAnimation(anim);
            dialog.show();
        }

//        rootView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });

        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @SuppressLint("NewApi")
    public static Dialog createBorderAndShadowOptionDialog(
            final Context context, final OnBorderShadowOptionListener listener,
            boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.dialog_border_shadow, null);
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);
        dialog.setCanceledOnTouchOutside(true);
        // inflate views
        final float maxBorder = context.getResources().getDimension(
                R.dimen.max_border_size);
        final OptionBorderView borderView = (OptionBorderView) rootView
                .findViewById(R.id.borderView);
        final SeekBar borderSeekBar = (SeekBar) rootView
                .findViewById(R.id.borderSeekBar);
        borderSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekbar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekbar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekbar,
                                                  int progress, boolean fromUser) {
                        float border = maxBorder * (progress / 100.0f);
                        borderView.setBorderSize(border);
                    }
                });

        final SeekBar shadowSeekBar = (SeekBar) rootView
                .findViewById(R.id.shadowSeekBar);
        final OptionShadowView shadowView = (OptionShadowView) rootView
                .findViewById(R.id.shadowView);
        final float maxShadow = context.getResources().getDimension(
                R.dimen.max_shadow_size);
        shadowSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekbar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekbar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekbar,
                                                  int progress, boolean fromUser) {
                        int shadow = (int) (maxShadow * (progress / 100.0f));
                        shadowView.setShadowSize(shadow);
                    }
                });

        borderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        final float shadowSize = context.getResources()
                                .getDimension(R.dimen.shadow_view_size);
                        rect.top = (int) ((shadowSize - borderView.getHeight()) / 2.0f);
                        rect.left = (int) ((shadowSize - borderView.getWidth()) / 2.0f);
                        rect.right = rect.left + borderView.getWidth();
                        rect.bottom = rect.top + borderView.getHeight();
                        shadowView.setDrawableBounds(rect);
                        // remove listener
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            borderView.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            borderView.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });

        final View cancelButton = rootView.findViewById(R.id.cancelView);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        final View okButton = rootView.findViewById(R.id.okView);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                float border = maxBorder
                        * (borderSeekBar.getProgress() / 100.0f);
                int shadow = (int) (maxShadow * (shadowSeekBar.getProgress() / 100.0f));
                dialog.dismiss();

                if (listener != null) {
                    listener.onBorderSizeChange(border);
                    listener.onShadowSizeChange(shadow);
                }

            }
        });
        // show or not
        if (show) {
            try {
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        rootView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog createAddImageDialog(final Context context,
                                              final OnAddImageButtonClickListener listener, boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.dialog_add_image, null);
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
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

        View stickerView = rootView.findViewById(R.id.stickerView);
        stickerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStickerButtonClick();
                }
            }
        });

        View textView = rootView.findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTextButtonClick();
                }
            }
        });

        rootView.findViewById(R.id.alterBackgroundView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null){
                    listener.onBackgroundPhotoButtonClick();
                }
            }
        });

        rootView.findViewById(R.id.alterBackgroundColorView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null){
                    listener.onBackgroundColorButtonClick();
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

        if (listener != null && listener instanceof CreateFrameFragment) {
            stickerView.setVisibility(View.GONE);
        }

        if (show) {
            try {
                Animation anim = AnimationUtils.loadAnimation(context,
                        R.anim.slide_in_bottom);
                rootView.startAnimation(anim);
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return dialog;
    }

    public static Dialog createEditImageDialog(final Context context,
                                               final OnEditImageMenuClickListener listener, int dialogType,
                                               boolean show) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.dialog_edit_image, null);
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(rootView);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null) {
                    listener.onCancelEdit();
                }
            }
        });

        View removeButton = rootView.findViewById(R.id.removeView);
        removeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRemoveButtonClick();
                }
            }
        });

        View alterBackground = rootView.findViewById(R.id.alterBackgroundView);
        alterBackground.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAlterBackgroundButtonClick();
                }
            }
        });

        View borderView = rootView.findViewById(R.id.borderShadowView);
        borderView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBorderAndShaderButtonClick();
                }
            }
        });

        View cancelView = rootView.findViewById(R.id.cancelView);
        cancelView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancelEdit();
                }
            }
        });

        // sticker menu
        View editView = rootView.findViewById(R.id.editView);
        editView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditButtonClick();
                }
            }
        });

        View colorView = rootView.findViewById(R.id.borderColorView);
        colorView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onColorBorderButtonClick();
                }
            }
        });

        if (dialogType == STICKER_DIALOG_TYPE) {
            editView.setVisibility(View.GONE);
            colorView.setVisibility(View.GONE);
        } else if (dialogType == CREATE_FRAME_DIALOG_TYPE) {
            alterBackground.setVisibility(View.GONE);
            // borderView.setVisibility(View.GONE);
        }

        if (show) {
            try {
                dialog.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
    public static Dialog showDialog(Context context, int titleResId,
                                    int messageResId) {
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
    public static Dialog showDialog(Context context, int titleResId,
                                    int messageResId, DialogOnClickListener listener) {
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
    public static Dialog showDialog(Context context, String title,
                                    String message, final DialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
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
     * @param context      context that dialog will be shown
     * @param titleResId   title of dialog
     * @param messageResId message of dialog
     * @return
     */
    public static Dialog showDialogOkClick(Context context, int titleResId,
                                           int messageResId, int titleOk,
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
        builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton(titleOk, clickListener);
        AlertDialog alert = builder.create();
        alert.show();

        return alert;
    }

    /**
     * Show confirm dialog with string resource (Yes/No dialog)
     *
     * @param context      that dialog will be shown
     * @param titleResId   title string resource id
     * @param messageResId message string resource id
     * @param listener     handle event when click button Yes/No
     * @return
     */
    public static Dialog showConfirmDialog(Context context, int titleResId,
                                           int messageResId, final ConfirmDialogOnClickListener listener) {
        String title = context.getResources().getString(titleResId);
        String message = context.getResources().getString(messageResId);

        return showConfirmDialog(context, title, message, listener);
    }

    /**
     * Show confirm dialog (Yes/No dialog)
     *
     * @param context      context that dialog will be shown
     * @param titleResId   title of dialog
     * @param messageResId message of dialog
     * @param okResId
     * @param cancelResId
     * @param listener     handle event when click button Yes/No
     * @return
     */
    public static Dialog showConfirmDialog(Context context, int titleResId,
                                           int messageResId, int okResId, int cancelResId,
                                           final ConfirmDialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResId)
                .setMessage(messageResId)
                .setCancelable(false)
                .setPositiveButton(okResId,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (listener != null)
                                    listener.onOKButtonOnClick();
                            }
                        })
                .setNegativeButton(cancelResId,
                        new DialogInterface.OnClickListener() {
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
    public static Dialog showConfirmDialog(Context context, String title,
                                           String message, final ConfirmDialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (listener != null)
                                    listener.onOKButtonOnClick();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
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

    public static interface OnEditImageMenuClickListener {
        public void onRemoveButtonClick();

        public void onAlterBackgroundButtonClick();

        public void onBorderAndShaderButtonClick();

        public void onEditButtonClick();

        public void onColorBorderButtonClick();

        public void onCancelEdit();
    }

    public static interface OnAddImageButtonClickListener {
        public void onCameraButtonClick();

        public void onGalleryButtonClick();

        public void onStickerButtonClick();

        public void onTextButtonClick();

        public void onBackgroundPhotoButtonClick();

        public void onBackgroundColorButtonClick();
    }

    public static interface OnBorderShadowOptionListener {
        public void onBorderSizeChange(float borderSize);

        public void onShadowSizeChange(float shadowSize);
    }
}
