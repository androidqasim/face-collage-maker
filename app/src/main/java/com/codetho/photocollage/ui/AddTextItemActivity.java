package com.codetho.photocollage.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.codetho.photocollage.R;
import com.codetho.photocollage.adapter.FontAdapter;
import com.codetho.photocollage.listener.OnChooseColorListener;
import com.codetho.photocollage.model.FontItem;
import com.codetho.photocollage.utils.TextUtils;

import java.util.List;

import dauroi.photoeditor.colorpicker.ColorPickerDialog;

/**
 * Created by vanhu_000 on 2/29/2016.
 */
public class AddTextItemActivity extends AdsFragmentActivity implements OnChooseColorListener, ColorPickerDialog.OnColorChangedListener {
    public static final String EXTRA_TEXT_CONTENT = "content";
    public static final String EXTRA_TEXT_COLOR = "color";
    public static final String EXTRA_TEXT_FONT = "font";

    private Spinner mFontSpinner;
    private View mColorView;
    private EditText mEditText;
    private ColorPickerDialog mColorPickerDialog;

    private List<FontItem> mFontItems;
    private int mTextColor = Color.BLACK;
    private String mFontPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text_item);
        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        final ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }

        addNativeAdView();
        mColorView = findViewById(R.id.colorView);
        mColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorPickerDialog == null) {
                    mColorPickerDialog = new ColorPickerDialog(AddTextItemActivity.this, mTextColor);
                    mColorPickerDialog.setOnColorChangedListener(AddTextItemActivity.this);
                }

                mColorPickerDialog.setOldColor(mTextColor);
                if (!mColorPickerDialog.isShowing()) {
                    mColorPickerDialog.show();
                }
            }
        });

        mFontSpinner = (Spinner) findViewById(R.id.spinner);
        mEditText = (EditText) findViewById(R.id.editText);
        //In case edit text
        mTextColor = getIntent().getIntExtra(EXTRA_TEXT_COLOR, mTextColor);
        mFontPath = getIntent().getStringExtra(EXTRA_TEXT_FONT);
        String text = getIntent().getStringExtra(EXTRA_TEXT_CONTENT);
        mEditText.setTextColor(mTextColor);
        if (text != null && text.length() > 0) {
            mEditText.setText(text);
        }
        //set fonts
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mFontItems = TextUtils.loadFonts(AddTextItemActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                final FontAdapter fontAdapter = new FontAdapter(AddTextItemActivity.this, mFontItems);
                mFontSpinner.setAdapter(fontAdapter);
                mFontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mFontPath = mFontItems.get(position).getFontPath();
                        mEditText.setTypeface(TextUtils.loadTypeface(AddTextItemActivity.this, mFontPath));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (mFontPath != null && mFontPath.length() > 0) {
                    for (int idx = 0; idx < mFontItems.size(); idx++)
                        if (mFontItems.get(idx).getFontPath() != null && mFontItems.get(idx).getFontPath().equalsIgnoreCase(mFontPath)) {
                            mFontSpinner.setSelection(idx);
                            break;
                        }
                }
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_text_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_done) {
            clickDoneButton();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void clickDoneButton() {
        final String text = mEditText.getText().toString().trim();
        Intent data = new Intent();
        data.putExtra(EXTRA_TEXT_CONTENT, text);
        data.putExtra(EXTRA_TEXT_COLOR, mTextColor);
        data.putExtra(EXTRA_TEXT_FONT, mFontPath);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void setSelectedColor(int color) {
        mTextColor = color;
        mEditText.setTextColor(mTextColor);
    }

    @Override
    public int getSelectedColor() {
        return mTextColor;
    }

    @Override
    public void onColorChanged(int color) {
        mTextColor = color;
        mEditText.setTextColor(mTextColor);
    }
}
