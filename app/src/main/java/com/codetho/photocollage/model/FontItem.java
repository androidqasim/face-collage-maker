package com.codetho.photocollage.model;

/**
 * Created by vanhu_000 on 2/29/2016.
 */
public class FontItem {
    private String mFontName;
    private String mFontPath;

    public FontItem(String fontName, String fontPath) {
        mFontName = fontName;
        mFontPath = fontPath;
    }

    public String getFontName() {
        return mFontName;
    }

    public String getFontPath() {
        return mFontPath;
    }
}
