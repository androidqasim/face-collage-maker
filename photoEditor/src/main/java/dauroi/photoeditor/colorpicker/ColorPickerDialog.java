package dauroi.photoeditor.colorpicker;

import java.util.Locale;

import dauroi.photoeditor.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColorPickerDialog extends Dialog implements
		ColorPickerView.OnColorChangedListener, View.OnClickListener {

	private ColorPickerView mColorPicker;

	private ColorPickerPanelView mOldColor;
	private ColorPickerPanelView mNewColor;

	private EditText mHexVal;
	private boolean mHexValueEnabled = false;
	private ColorStateList mHexDefaultTextColor;

	private OnColorChangedListener mListener;

	public interface OnColorChangedListener {
		public void onColorChanged(int color);
	}

	public ColorPickerDialog(Context context, int initialColor) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		init(initialColor);
	}

	private void init(int color) {
		// To fight color banding.
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);

	}

	private void setUp(int color) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.photo_editor_dialog_color_picker, null);

		setContentView(layout);

		setTitle(R.string.photo_editor_dialog_color_picker);

		mColorPicker = (ColorPickerView) layout
				.findViewById(R.id.color_picker_view);
		mOldColor = (ColorPickerPanelView) layout
				.findViewById(R.id.old_color_panel);
		mNewColor = (ColorPickerPanelView) layout
				.findViewById(R.id.new_color_panel);

		mHexVal = (EditText) layout.findViewById(R.id.hex_val);
		mHexVal.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		mHexDefaultTextColor = mHexVal.getTextColors();

		mHexVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					String s = mHexVal.getText().toString();
					if (s.length() > 5 || s.length() < 10) {
						try {
							int c = convertToColorInt(s.toString());
							mColorPicker.setColor(c, true);
							mHexVal.setTextColor(mHexDefaultTextColor);
						} catch (IllegalArgumentException e) {
							mHexVal.setTextColor(Color.RED);
						}
					} else {
						mHexVal.setTextColor(Color.RED);
					}
					return true;
				}
				return false;
			}
		});

		((LinearLayout) mOldColor.getParent()).setPadding(
				Math.round(mColorPicker.getDrawingOffset()), 0,
				Math.round(mColorPicker.getDrawingOffset()), 0);

		mOldColor.setOnClickListener(this);
		mNewColor.setOnClickListener(this);
		mColorPicker.setOnColorChangedListener(this);
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);

	}

	public void setOldColor(int color){
		mOldColor.setColor(color);
		mColorPicker.setColor(color, true);
	}
	
	@Override
	public void onColorChanged(int color) {

		mNewColor.setColor(color);

		if (mHexValueEnabled)
			updateHexValue(color);

		/*
		 * if (mListener != null) { mListener.onColorChanged(color); }
		 */

	}

	public void setHexValueEnabled(boolean enable) {
		mHexValueEnabled = enable;
		if (enable) {
			mHexVal.setVisibility(View.VISIBLE);
			updateHexLengthFilter();
			updateHexValue(getColor());
		} else
			mHexVal.setVisibility(View.GONE);
	}

	public boolean getHexValueEnabled() {
		return mHexValueEnabled;
	}

	private void updateHexLengthFilter() {
		if (getAlphaSliderVisible())
			mHexVal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					9) });
		else
			mHexVal.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					7) });
	}

	private void updateHexValue(int color) {
		if (getAlphaSliderVisible()) {
			mHexVal.setText(convertToARGB(color).toUpperCase(
					Locale.getDefault()));
		} else {
			mHexVal.setText(convertToRGB(color)
					.toUpperCase(Locale.getDefault()));
		}
		mHexVal.setTextColor(mHexDefaultTextColor);
	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
		if (mHexValueEnabled) {
			updateHexLengthFilter();
			updateHexValue(getColor());
		}
	}

	public boolean getAlphaSliderVisible() {
		return mColorPicker.getAlphaSliderVisible();
	}

	/**
	 * Set a OnColorChangedListener to get notified when the color selected by
	 * the user has changed.
	 * 
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mListener = listener;
	}

	public int getColor() {
		return mColorPicker.getColor();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_color_panel) {
			if (mListener != null) {
				mListener.onColorChanged(mNewColor.getColor());
			}
		}
		dismiss();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt("old_color", mOldColor.getColor());
		state.putInt("new_color", mNewColor.getColor());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mOldColor.setColor(savedInstanceState.getInt("old_color"));
		mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
	}

	/**
	 * For custom purposes. Not used by ColorPickerPreferrence
	 * 
	 * @param color
	 */
	public static String convertToARGB(int color) {
		String alpha = Integer.toHexString(Color.alpha(color));
		String red = Integer.toHexString(Color.red(color));
		String green = Integer.toHexString(Color.green(color));
		String blue = Integer.toHexString(Color.blue(color));

		if (alpha.length() == 1) {
			alpha = "0" + alpha;
		}

		if (red.length() == 1) {
			red = "0" + red;
		}

		if (green.length() == 1) {
			green = "0" + green;
		}

		if (blue.length() == 1) {
			blue = "0" + blue;
		}

		return "#" + alpha + red + green + blue;
	}

	/**
	 * For custom purposes. Not used by ColorPickerPreference
	 * 
	 * @param color
	 * @return A string representing the hex value of color, without the alpha
	 *         value
	 */
	public static String convertToRGB(int color) {
		String red = Integer.toHexString(Color.red(color));
		String green = Integer.toHexString(Color.green(color));
		String blue = Integer.toHexString(Color.blue(color));

		if (red.length() == 1) {
			red = "0" + red;
		}

		if (green.length() == 1) {
			green = "0" + green;
		}

		if (blue.length() == 1) {
			blue = "0" + blue;
		}

		return "#" + red + green + blue;
	}

	/**
	 * For custom purposes. Not used by ColorPickerPreferrence
	 * 
	 * @param argb
	 * @throws NumberFormatException
	 */
	public static int convertToColorInt(String argb)
			throws IllegalArgumentException {

		if (!argb.startsWith("#")) {
			argb = "#" + argb;
		}

		return Color.parseColor(argb);
	}
}
