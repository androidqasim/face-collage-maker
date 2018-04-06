package dauroi.photoeditor.actions;

import java.util.List;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.colorpicker.ColorPickerDialog;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.DialogUtils;
import dauroi.photoeditor.utils.DialogUtils.OnSelectDrawEffectListener;
import dauroi.photoeditor.utils.DialogUtils.OnSelectPaintSizeListener;
import dauroi.photoeditor.view.FingerPaintView;

public class DrawAction extends MaskAction
        implements ColorPickerDialog.OnColorChangedListener, OnSelectPaintSizeListener, OnSelectDrawEffectListener {
    private View mColorView;
    private View mSizeView;
    private View mEraseView;
    private View mClearView;
    private ImageView mEraseThumbnailView;
    private TextView mEraseNameView;
    private Dialog mSelectPaintSizeDialog;
    private Dialog mSelectPaintEffectDialog;
    private ColorPickerDialog mColorPickerDialog;
    private int mCurrentColor = Color.WHITE;

    public DrawAction(ImageProcessingActivity activity) {
        super(activity);
    }

    @Override
    public void saveInstanceState(Bundle bundle) {
        super.saveInstanceState(bundle);
        bundle.putInt("dauroi.photoeditor.actions.DrawAction.mCurrentColor", mCurrentColor);
        ((FingerPaintView) mImageMaskView).saveInstanceState(bundle);
    }

    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
        mCurrentColor = bundle.getInt("dauroi.photoeditor.actions.DrawAction.mCurrentColor", mCurrentColor);
        ((FingerPaintView) mImageMaskView).restoreInstanceState(bundle);
    }

    @Override
    public void onDoneButtonClick() {
        apply(true);
    }

    @Override
    public void onApplyButtonClick() {
        apply(false);
    }

    public void apply(final boolean finish) {
        if (!isAttached()) {
            return;
        }

        ApplyFilterTask task = new ApplyFilterTask(mActivity, new ApplyFilterListener() {

            @Override
            public Bitmap applyFilter() {
                float ratio = mActivity.calculateScaleRatio();
                return ((FingerPaintView) mImageMaskView).drawImage(mActivity.getImage(), ratio);
            }

            @Override
            public void onFinishFiltering() {
                mCurrentColor = Color.WHITE;
                if (finish) {
                    done();
                }
            }

        });

        task.execute();
    }

    @Override
    public void attach() {
        super.attach();
        mActivity.attachMaskView(mMaskLayout);
        adjustImageMaskLayout();
        mActivity.applyFilter(new ImageFilter());
    }

    @Override
    public View inflateMenuView() {
        mRootActionView = mLayoutInflater.inflate(R.layout.photo_editor_action_draw, null);
        mSelectPaintSizeDialog = DialogUtils.createPreviewDrawingDialog(mActivity, this, false);
        mSelectPaintEffectDialog = DialogUtils.createDrawEffectDialog(mActivity, this, false);

        mColorView = mRootActionView.findViewById(R.id.colorView);
        mColorView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetEraseButton();
                if (mColorPickerDialog == null) {
                    mColorPickerDialog = new ColorPickerDialog(mActivity, mCurrentColor);
                    mColorPickerDialog.setOnColorChangedListener(DrawAction.this);
                }

                mColorPickerDialog.setOldColor(mCurrentColor);
                if (!mColorPickerDialog.isShowing()) {
                    mColorPickerDialog.show();
                }
            }
        });

        mSizeView = mRootActionView.findViewById(R.id.sizeView);
        mSizeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetEraseButton();
                if (!mSelectPaintSizeDialog.isShowing()) {
                    mSelectPaintSizeDialog.show();
                }
            }
        });

        mEraseView = mRootActionView.findViewById(R.id.eraseView);
        mEraseThumbnailView = (ImageView) mRootActionView.findViewById(R.id.eraseThumbnailView);
        mEraseNameView = (TextView) mRootActionView.findViewById(R.id.eraseNameView);
        mEraseView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((FingerPaintView) mImageMaskView).getEffect() != FingerPaintView.DRAW_EFFECT_ERASE) {
                    ((FingerPaintView) mImageMaskView).setPaintEffect(FingerPaintView.DRAW_EFFECT_ERASE);
                    mEraseThumbnailView.setImageResource(R.drawable.photo_editor_ic_eraser_pressed);
                    mEraseNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
                } else {
                    resetEraseButton();
                }
            }
        });

        mClearView = mRootActionView.findViewById(R.id.clearView);
        mClearView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetEraseButton();
                ((FingerPaintView) mImageMaskView).clear();
            }
        });

        return mRootActionView;
    }

    private void resetEraseButton(){
        ((FingerPaintView) mImageMaskView).setPaintEffect(FingerPaintView.DRAW_EFFECT_NORMAL);
        mEraseThumbnailView.setImageResource(R.drawable.photo_editor_ic_eraser_normal);
        mEraseNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
    }

    @Override
    public void onColorChanged(int color) {
        mCurrentColor = color;
        ((FingerPaintView) mImageMaskView).setPaintColor(color);
    }

    @Override
    public void onSelectPaintSize(float size) {
        ((FingerPaintView) mImageMaskView).setPaintSize(size);
    }

    @Override
    public void onSelectEffect(int effect) {
        ((FingerPaintView) mImageMaskView).setPaintEffect(effect);
    }

    @Override
    protected int getMaskLayoutRes() {
        return R.layout.photo_editor_finger_paint_layout;
    }

    @Override
    protected void selectNormalItem(int position) {

    }

    @Override
    protected List<? extends ItemInfo> loadNormalItems(long packageId, String packageFolder) {
        return null;
    }

    @Override
    public String getActionName() {
        return "DrawAction";
    }
}
