package dauroi.photoeditor.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class WindowUtils {
	/**
	 * Show device's keyboard
	 * 
	 * @param v
	 */
	public static void showKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, 0);

	}

	public static void hideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}
