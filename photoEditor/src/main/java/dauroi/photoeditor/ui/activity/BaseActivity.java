package dauroi.photoeditor.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import dauroi.photoeditor.R;

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.photo_editor_slice_in_left, R.anim.photo_editor_slice_out_right);
    }
}
