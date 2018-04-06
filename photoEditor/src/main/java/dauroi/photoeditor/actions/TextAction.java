package dauroi.photoeditor.actions;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.photoeditor.R;
import dauroi.photoeditor.listener.ApplyFilterListener;
import dauroi.photoeditor.model.ItemInfo;
import dauroi.photoeditor.task.ApplyFilterTask;
import dauroi.photoeditor.ui.activity.ImageProcessingActivity;
import dauroi.photoeditor.utils.WindowUtils;
import dauroi.photoeditor.view.CaptionImageView;
import dauroi.photoeditor.view.CaptionImageView.OnDrawCaptionListener;
import dauroi.photoeditor.view.CaptionImageView.OnChangeDirectionListener;

public class TextAction extends MaskAction implements OnChangeDirectionListener, OnDrawCaptionListener {
	private View mMemeView;
	private View mCenterView;
	private TextView mMemeNameView;
	private ImageView mMemeThumbnailView;
	private TextView mCenterNameView;
	private ImageView mCenterThumbnailView;

	private EditText mTopCaptionView;
	private EditText mCenterCaptionView;
	private EditText mBottomCaptionView;
	private CaptionImageView mCaptionView;
	private boolean mIsMeme = true;

	public TextAction(ImageProcessingActivity activity) {
		super(activity);
	}

	@Override
	public void onActivityResume() {
		super.onActivityResume();
		if(isAttached()){
			mActivity.applyFilter(new ImageFilter());
		}
	}

	@Override
	public void saveInstanceState(Bundle bundle) {
		super.saveInstanceState(bundle);
		mCaptionView.saveInstanceState(bundle);
		bundle.putString("dauroi.photoeditor.actions.TextAction.mTopCaption", mTopCaptionView.getText().toString());
		bundle.putString("dauroi.photoeditor.actions.TextAction.mCenterCaptionView",
				mCenterCaptionView.getText().toString());
		bundle.putString("dauroi.photoeditor.actions.TextAction.mBottomCaptionView",
				mBottomCaptionView.getText().toString());
		bundle.putBoolean("dauroi.photoeditor.actions.TextAction.mIsMeme", mCaptionView.isMeme());
	}

	@Override
	public void restoreInstanceState(Bundle bundle) {
		super.restoreInstanceState(bundle);
		mCaptionView.restoreInstanceState(bundle);
		String text = bundle.getString("dauroi.photoeditor.actions.TextAction.mTopCaption");
		if (text != null && text.length() > 0) {
			mTopCaptionView.setText(text);
		}

		text = bundle.getString("dauroi.photoeditor.actions.TextAction.mCenterCaptionView");
		if (text != null && text.length() > 0) {
			mCenterCaptionView.setText(text);
		}

		text = bundle.getString("dauroi.photoeditor.actions.TextAction.mBottomCaptionView");
		if (text != null && text.length() > 0) {
			mBottomCaptionView.setText(text);
		}

		mIsMeme = bundle.getBoolean("dauroi.photoeditor.actions.TextAction.mIsMeme", true);
	}

	@Override
	public void apply(final boolean finish) {
		if (!isAttached()) {
			return;
		}

		ApplyFilterTask task = new ApplyFilterTask(mActivity, new ApplyFilterListener() {

			@Override
			public Bitmap applyFilter() {
				float ratio = mActivity.calculateScaleRatio();
				return mCaptionView.getTextBitmap(mActivity.getImage(), ratio);
			}

			@Override
			public void onFinishFiltering() {
				mTopCaptionView.setText("");
				mCenterCaptionView.setText("");
				mBottomCaptionView.setText("");
				mIsMeme = true;

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
		if (mIsMeme) {
			clickMemeView();
		} else {
			clickCenterView();
		}
	}

	@Override
	public View inflateMenuView() {
		mRootActionView = mLayoutInflater.inflate(R.layout.photo_editor_action_text, null);
		mMemeView = mRootActionView.findViewById(R.id.memeView);
		mMemeNameView = (TextView) mRootActionView.findViewById(R.id.memeNameView);
		mMemeThumbnailView = (ImageView) mRootActionView.findViewById(R.id.memeThumbnailView);
		mMemeView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clickMemeView();
				onClicked();
			}
		});

		mCenterView =  mRootActionView.findViewById(R.id.centerView);
		mCenterNameView = (TextView) mRootActionView.findViewById(R.id.centerNameView);
		mCenterThumbnailView = (ImageView) mRootActionView.findViewById(R.id.centerThumbnailView);
		mCenterView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clickCenterView();
				onClicked();
			}
		});

		mTopCaptionView = (EditText) mMaskLayout.findViewById(R.id.topCaptionView);
		mTopCaptionView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyenEvent) {
				processTopEditorAction(actionId);
				return true;
			}
		});

		mCenterCaptionView = (EditText) mMaskLayout.findViewById(R.id.centerCaptionView);
		mCenterCaptionView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyenEvent) {
				processCenterEditorAction(actionId);
				return true;
			}
		});

		mBottomCaptionView = (EditText) mMaskLayout.findViewById(R.id.bottomCaptionView);
		mBottomCaptionView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyenEvent) {
				processBottomEditorAction(actionId);
				return true;
			}
		});

		mCaptionView = (CaptionImageView) mMaskLayout.findViewById(R.id.captionView);
		mCaptionView.setOnChangeDirectionListener(this);
		mCaptionView.setOnDrawCaptionListener(this);

		return mRootActionView;
	}

	private void processTopEditorAction(int actionId) {
		if (actionId == EditorInfo.IME_ACTION_NEXT) {
			final String text = mTopCaptionView.getText().toString().trim();
			mCaptionView.setText(text);
			WindowUtils.hideKeyboard(mTopCaptionView);
			if (text != null && text.length() > 0) {
				mTopCaptionView.setVisibility(View.GONE);
			}

			WindowUtils.showKeyboard(mBottomCaptionView);
		}
	}

	private void processCenterEditorAction(int actionId) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			final String text = mCenterCaptionView.getText().toString().trim();
			mCaptionView.setText(text);
			WindowUtils.hideKeyboard(mCenterCaptionView);
			if (text != null && text.length() > 0)
				mCenterCaptionView.setVisibility(View.GONE);
		}
	}

	private void processBottomEditorAction(int actionId) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			final String text = mBottomCaptionView.getText().toString().trim();
			mCaptionView.setText2(text);
			WindowUtils.hideKeyboard(mBottomCaptionView);
			if (text != null && text.length() > 0)
				mBottomCaptionView.setVisibility(View.GONE);
		}
	}

	private void selectBottomActionView(boolean isMeme){
		mMemeThumbnailView.setImageResource(R.drawable.photo_editor_ic_meme_normal);
		mMemeNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
		mCenterThumbnailView.setImageResource(R.drawable.photo_editor_ic_center_normal);
		mCenterNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_normal_text_main_topbar));
		if(isMeme){
			mMemeThumbnailView.setImageResource(R.drawable.photo_editor_ic_meme_pressed);
			mMemeNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
		}else{
			mCenterThumbnailView.setImageResource(R.drawable.photo_editor_ic_center_pressed);
			mCenterNameView.setTextColor(mActivity.getResources().getColor(R.color.photo_editor_selected_text_main_topbar));
		}
	}

	private void clickMemeView() {
		selectBottomActionView(true);
		final String topText = mTopCaptionView.getText().toString().trim();
		final String bottomText = mBottomCaptionView.getText().toString().trim();

		mCaptionView.setIsMeme(true);
		mCaptionView.setText(topText);
		mCaptionView.setText2(bottomText);
		if (topText != null && topText.length() > 0) {
			mTopCaptionView.setVisibility(View.GONE);
		} else {
			mTopCaptionView.setVisibility(View.VISIBLE);
		}

		mCenterCaptionView.setVisibility(View.GONE);

		if (bottomText != null && bottomText.length() > 0) {
			mBottomCaptionView.setVisibility(View.GONE);
		} else {
			mBottomCaptionView.setVisibility(View.VISIBLE);
		}
	}

	private void clickCenterView() {
		selectBottomActionView(false);
		final String centerText = mCenterCaptionView.getText().toString().trim();
		mCaptionView.setText(centerText);
		mCaptionView.setText2("");
		mCaptionView.setIsMeme(false);
		mTopCaptionView.setVisibility(View.GONE);

		if (centerText != null && centerText.length() > 0) {
			mCenterCaptionView.setVisibility(View.GONE);
		} else {
			mCenterCaptionView.setVisibility(View.VISIBLE);
		}

		mBottomCaptionView.setVisibility(View.GONE);
	}

	@Override
	protected int getMaskLayoutRes() {
		return R.layout.photo_editor_text_mask_layout;
	}

	@Override
	public void changeDirection(int direction) {

	}

	@Override
	public void clickAt(float x, float y) {
		final int height = mCaptionView.getHeight();
		if (y <= 3 * height / 16.0f) {
			if (mTopCaptionView.getVisibility() != View.VISIBLE && mCaptionView.isMeme()) {
				mTopCaptionView.setVisibility(View.VISIBLE);
				mCaptionView.setText("");
			}

			if (!mCaptionView.isMeme()) {
				processCenterEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		} else if (y > 6 * height / 16.0f && y < 10 * height / 16.0f) {
			if (mCenterCaptionView.getVisibility() != View.VISIBLE && !mCaptionView.isMeme()) {
				mCenterCaptionView.setVisibility(View.VISIBLE);
				mCaptionView.setText("");
			}
			if (mCaptionView.isMeme()) {
				processTopEditorAction(EditorInfo.IME_ACTION_NEXT);
				processBottomEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		} else if (y >= 13 * height / 16f) {
			if (mBottomCaptionView.getVisibility() != View.VISIBLE && mCaptionView.isMeme()) {
				mBottomCaptionView.setVisibility(View.VISIBLE);
				mCaptionView.setText2("");
			}
			if (!mCaptionView.isMeme()) {
				processCenterEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		}
	}

	@Override
	public void textTooLong(int characterCount, float textSize) {
		Toast.makeText(mActivity, mActivity.getString(R.string.photo_editor_text_so_long), Toast.LENGTH_SHORT).show();
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
		return "TextAction";
	}
}
