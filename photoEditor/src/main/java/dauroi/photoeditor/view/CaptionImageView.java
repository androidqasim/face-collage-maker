package dauroi.photoeditor.view;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class CaptionImageView extends ImageView {
	public static final int MOVE_UP = 0;
	public static final int MOVE_DOWN = 1;
	public static final int MOVE_LEFT = 2;
	public static final int MOVE_RIGHT = 3;

	private static final int MIN_TOUCH_DIST_DP = 15;
	public static final String BASE_FONT_ASSET_FOLDER = "fonts/";
	public static final int MAX_TEXT_LINES = 3;
	public static final int MAX_CHARACTER_COUNT = 128;

	private float mMinTextSize = 12;
	private float mMaxTextSize = 48;
	// mText is the top caption or center caption or url title.
	private String mText = "";
	private int mTextColor = Color.WHITE;
	private float mOriginalTextSize = 24f;
	private float mFirstTextSize = mOriginalTextSize;
	private float mSecondTextSize = mOriginalTextSize;

	private Typeface mFontTf = Typeface.DEFAULT;

	private Paint mTextPaint = new Paint();

	private float mTopMarginCaption = 10;
	private float mBottomMarginCaption = 10;
	private float mLeftRightMarginCaption = 10;
	private float mStrokeWidth = 4;

	private int mMinTouchDist = MIN_TOUCH_DIST_DP;
	private PointF mLastTouch = new PointF();
	private OnChangeDirectionListener mChangeDirectionListener;
	private OnDrawCaptionListener mDrawCaptionListener;

	private Paint mStrokePaint = new Paint();
	private int mStrokeColor = Color.BLACK;
	private boolean mDrawStroke = true;
	// Meme mode
	private boolean mIsMeme = true;
	private String mText2 = "";
	protected float mTextX = 0;
	protected float mTextY = 0;
	protected float mTextX2 = 0;
	protected float mTextY2 = 20;

	private String mFontName = "";

	public CaptionImageView(Context context) {
		super(context);
		init();
	}

	public CaptionImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CaptionImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void saveInstanceState(Bundle bundle) {
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mMinTextSize", mMinTextSize);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mMaxTextSize", mMaxTextSize);
		bundle.putString("dauroi.photoeditor.view.CaptionImageView.mText", mText);
		bundle.putInt("dauroi.photoeditor.view.CaptionImageView.mTextColor", mTextColor);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mOriginalTextSize", mOriginalTextSize);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mFirstTextSize", mFirstTextSize);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mSecondTextSize", mSecondTextSize);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mTopMarginCaption", mTopMarginCaption);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mBottomMarginCaption", mBottomMarginCaption);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mLeftRightMarginCaption", mLeftRightMarginCaption);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mStrokeWidth", mStrokeWidth);
		bundle.putInt("dauroi.photoeditor.view.CaptionImageView.mMinTouchDist", mMinTouchDist);
		bundle.putParcelable("dauroi.photoeditor.view.CaptionImageView.mLastTouch", mLastTouch);
		bundle.putInt("dauroi.photoeditor.view.CaptionImageView.mStrokeColor", mStrokeColor);
		bundle.putBoolean("dauroi.photoeditor.view.CaptionImageView.mDrawStroke", mDrawStroke);
		bundle.putBoolean("dauroi.photoeditor.view.CaptionImageView.mIsMeme", mIsMeme);
		bundle.putString("dauroi.photoeditor.view.CaptionImageView.mText2", mText2);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mTextX", mTextX);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mTextY", mTextY);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mTextX2", mTextX2);
		bundle.putFloat("dauroi.photoeditor.view.CaptionImageView.mTextY2", mTextY2);
		bundle.putString("dauroi.photoeditor.view.CaptionImageView.mFontName", mFontName);
	}

	public void restoreInstanceState(Bundle bundle) {
		mMinTextSize = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mMinTextSize", mMinTextSize);
		mMaxTextSize = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mMaxTextSize", mMaxTextSize);
		mText = bundle.getString("dauroi.photoeditor.view.CaptionImageView.mText");
		mTextColor = bundle.getInt("dauroi.photoeditor.view.CaptionImageView.mTextColor", mTextColor);
		mOriginalTextSize = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mOriginalTextSize",
				mOriginalTextSize);
		mFirstTextSize = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mFirstTextSize", mFirstTextSize);
		mSecondTextSize = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mSecondTextSize", mSecondTextSize);
		mTopMarginCaption = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mTopMarginCaption",
				mTopMarginCaption);
		mBottomMarginCaption = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mBottomMarginCaption",
				mBottomMarginCaption);
		mLeftRightMarginCaption = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mLeftRightMarginCaption",
				mLeftRightMarginCaption);
		mStrokeWidth = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mStrokeWidth", mStrokeWidth);
		mMinTouchDist = bundle.getInt("dauroi.photoeditor.view.CaptionImageView.mMinTouchDist", mMinTouchDist);
		PointF p = bundle.getParcelable("dauroi.photoeditor.view.CaptionImageView.mLastTouch");
		if (p != null) {
			mLastTouch = p;
		}

		mStrokeColor = bundle.getInt("dauroi.photoeditor.view.CaptionImageView.mStrokeColor", mStrokeColor);
		mDrawStroke = bundle.getBoolean("dauroi.photoeditor.view.CaptionImageView.mDrawStroke", mDrawStroke);
		mIsMeme = bundle.getBoolean("dauroi.photoeditor.view.CaptionImageView.mIsMeme", mIsMeme);
		String text = bundle.getString("dauroi.photoeditor.view.CaptionImageView.mText2");
		if (text != null && text.length() > 0) {
			mText2 = text;
		}

		mTextX = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mTextX", mTextX);
		mTextY = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mTextY", mTextY);
		mTextX2 = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mTextX2", mTextX2);
		mTextY2 = bundle.getFloat("dauroi.photoeditor.view.CaptionImageView.mTextY2", mTextY2);
		text = bundle.getString("dauroi.photoeditor.view.CaptionImageView.mFontName");
		if (text != null && text.length() > 0) {
			mFontName = text;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (getWidth() < 5 || getHeight() < 5) {
			return;
		}
		if (!mIsMeme) {
			mFirstTextSize = drawCenterCaption(canvas, getWidth(), getHeight(), mLeftRightMarginCaption, mText);
		} else {
			mFirstTextSize = drawTopCaption(canvas, getWidth(), getHeight(), mTopMarginCaption, mLeftRightMarginCaption,
					mText);
			mSecondTextSize = drawBottomCaption(canvas, getWidth(), getHeight(), mBottomMarginCaption,
					mLeftRightMarginCaption, mText2);
		}

	}

	public Bitmap getTextBitmap(Bitmap savedBitmap, float scaleRatio) {
		mStrokeWidth = mStrokeWidth * scaleRatio;
		mTextPaint.setTextSize(mFirstTextSize * scaleRatio);
		mStrokePaint.setTextSize(mFirstTextSize * scaleRatio);
		mStrokePaint.setStrokeWidth(mStrokeWidth);

		Bitmap result = Bitmap.createBitmap(savedBitmap.getWidth(), savedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		final float topMargin = mTopMarginCaption;
		final float leftRightMargin = mLeftRightMarginCaption * scaleRatio;
		final float bottomMargin = mBottomMarginCaption;
		mMinTextSize = mMinTextSize * scaleRatio;
		mMaxTextSize = mMaxTextSize * scaleRatio;

		canvas.drawBitmap(savedBitmap, 0, 0, mTextPaint);
		if (!mIsMeme) {
			drawCenterCaption(canvas, result.getWidth(), result.getHeight(), leftRightMargin, mText);
		} else {
			drawTopCaption(canvas, result.getWidth(), result.getHeight(), topMargin, leftRightMargin, mText);
			mTextPaint.setTextSize(mSecondTextSize * scaleRatio);
			mStrokePaint.setTextSize(mSecondTextSize * scaleRatio);
			drawBottomCaption(canvas, result.getWidth(), result.getHeight(), bottomMargin, leftRightMargin, mText2);
		}

		return result;
	}

	private void initPaints() {
		mStrokePaint.setColor(mStrokeColor);
		mStrokePaint.setTextAlign(Paint.Align.LEFT);
		mStrokePaint.setTextSize(mOriginalTextSize);
		mStrokePaint.setTypeface(mFontTf);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(mStrokeWidth);
		mStrokePaint.setAntiAlias(true);

		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextAlign(Paint.Align.LEFT);
		mTextPaint.setTextSize(mOriginalTextSize);
		mTextPaint.setTypeface(mFontTf);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setShadowLayer(6, 3, 3, Color.BLACK);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastTouch.x = event.getX();
			mLastTouch.y = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			PointF currentTouch = new PointF(event.getX(), event.getY());
			float dist = (currentTouch.x - mLastTouch.x) * (currentTouch.x - mLastTouch.x)
					+ (currentTouch.y - mLastTouch.y) * (currentTouch.y - mLastTouch.y);
			if (dist >= mMinTouchDist * mMinTouchDist) {
				int direction = findDirectionMovement(mLastTouch, currentTouch);

				if (mChangeDirectionListener != null) {
					mChangeDirectionListener.changeDirection(direction);
				}
			} else if (mChangeDirectionListener != null) {
				mChangeDirectionListener.clickAt(event.getX(), event.getY());
			}
			break;
		default:
			break;
		}

		return true;
	}

	public void loadFontFromAsset(final String fontName) {
		final String path = BASE_FONT_ASSET_FOLDER.concat(fontName);
		mFontTf = Typeface.createFromAsset(getContext().getAssets(), path);
		mTextPaint.setTypeface(mFontTf);
		mStrokePaint.setTypeface(mFontTf);
		invalidate();
		mFontName = fontName;
	}

	// ////////////////////set and get methods//////////////
	/**
	 * Text is the top caption in meme mode or center caption or url title.
	 * 
	 * @param text
	 */
	public void setText(String text) {
		mText = text;
		invalidate();
	}

	public void setTextColor(int textColor) {
		mTextColor = textColor;
		mTextPaint.setColor(textColor);
		invalidate();
	}

	public void setStrokeTextColor(int strokeColor) {
		mStrokeColor = strokeColor;
		mStrokePaint.setColor(strokeColor);
		invalidate();
	}

	public void setTextSize(float textSize) {
		mOriginalTextSize = textSize;
		mFirstTextSize = mOriginalTextSize;
		mSecondTextSize = mOriginalTextSize;
		mTextPaint.setTextSize(textSize);
		mStrokePaint.setTextSize(textSize);
		invalidate();
	}

	public void setFontTf(Typeface fontTf) {
		mFontTf = fontTf;
		mTextPaint.setTypeface(fontTf);
		mStrokePaint.setTypeface(fontTf);
		invalidate();
	}

	// meme mode
	public void setIsMeme(boolean isMeme) {
		mIsMeme = isMeme;
		invalidate();
	}

	public boolean isMeme() {
		return mIsMeme;
	}

	public void setTextPosition(float textX, float textY) {
		mTextX = textX;
		mTextY = textY;
		invalidate();
	}

	public void setText2Position(float textX2, float textY2) {
		mTextX2 = textX2;
		mTextY2 = textY2;
		invalidate();
	}

	public String getText2() {
		return mText2;
	}

	public void clearAllTexts() {
		mText = "";
		mText2 = "";
		invalidate();
	}

	public float getTextSize() {
		return mOriginalTextSize;
	}

	public String getFontName() {
		return mFontName;
	}

	/**
	 * Two positions of two caption are used in meme mode.
	 * 
	 * @param textX
	 * @param textY
	 * @param textX2
	 * @param textY2
	 */
	public void setAllTextPositions(float textX, float textY, float textX2, float textY2) {
		mTextX = textX;
		mTextY = textY;
		mTextX2 = textX2;
		mTextY2 = textY2;
		invalidate();
	}

	/**
	 * Used in meme mode. This is the bottom caption.
	 * 
	 * @param text2
	 */
	public void setText2(String text2) {
		mText2 = text2;
		invalidate();
	}

	/**
	 * Two captions are used in meme mode.
	 * 
	 * @param text
	 * @param text2
	 */
	public void setAllTexts(String text, String text2) {
		mText = text;
		mText2 = text2;
		invalidate();
	}

	public String getText() {
		return mText;
	}

	public Typeface getFontTf() {
		return mFontTf;
	}

	public int getTextColor() {
		return mTextColor;
	}

	public void setOnChangeDirectionListener(OnChangeDirectionListener changeDirection) {
		mChangeDirectionListener = changeDirection;
	}

	public void setOnDrawCaptionListener(OnDrawCaptionListener drawCaption) {
		mDrawCaptionListener = drawCaption;
	}

	/**
	 * 
	 * @param canvas
	 * @param bitmapWidth
	 * @param bitmapHeight
	 * @param textMargin
	 *            is the top margin and left margin, right margin. Distance from
	 *            text and bounds.
	 * @param text
	 * @return text size, return -1 if text is too long.
	 */
	public float drawTopCaption(Canvas canvas, final int bitmapWidth, final int bitmapHeight, float topMargin,
			final float leftRightMargin, final String text) {
		if (text == null || text.length() == 0) {
			return -1;
		}
		if (text.length() > MAX_CHARACTER_COUNT) {
			if (mDrawCaptionListener != null) {
				mDrawCaptionListener.textTooLong(text.length(), mOriginalTextSize);
			}
			return mOriginalTextSize;
		}

		final List<String> wordList = findWords(text);
		// set text size
		float textSize = mOriginalTextSize;
		Paint paint = new Paint();
		paint.setTextSize(mOriginalTextSize);

		final int maxLen = (int) (bitmapWidth - 2 * leftRightMargin);
		if (wordList.size() > 0) {
			textSize = findTextSize(mTextPaint, wordList, MAX_TEXT_LINES, maxLen, bitmapHeight / 4.0f, mMinTextSize,
					mMaxTextSize);

			if (textSize > 0) {
				mTextPaint.setTextSize(textSize);
				mStrokePaint.setTextSize(textSize);
			} else {
				if (mDrawCaptionListener != null) {
					mDrawCaptionListener.textTooLong(text.length(), textSize);
				}
				mTextPaint.setTextSize(mOriginalTextSize);
				mStrokePaint.setTextSize(mOriginalTextSize);
				return textSize;
			}
		}

		final Rect textBound = new Rect();
		mTextPaint.getTextBounds(text, 0, text.length(), textBound);
		final int textHeight = textBound.height();
		mTextPaint.getTextBounds("m", 0, 1, textBound);
		final int delta = (int) (0.5 * textBound.height() + textHeight);
		float y = topMargin;
		final int wordCount = wordList.size();
		int startIndex = 0;
		boolean isFirstLine = true;
		while (startIndex < wordCount) {
			// find number of words on a line.
			int endIndex = startIndex;
			for (int idx = wordCount - 1; idx >= startIndex; idx--) {
				String sentence = toNormalSentence(wordList, startIndex, idx);
				float textLen = mTextPaint.measureText(sentence);
				if (textLen < maxLen) {
					endIndex = idx;
					break;
				}
			}
			// draw text
			if (isFirstLine) {
				y += textHeight;
				isFirstLine = false;
			} else {
				y += delta;
			}

			String sentence = toNormalSentence(wordList, startIndex, endIndex);
			float x = (bitmapWidth - mTextPaint.measureText(sentence)) / 2.0f;
			if (mDrawStroke) {
				canvas.drawText(sentence, x, y, mStrokePaint);
			}
			canvas.drawText(sentence, x, y, mTextPaint);
			// update infor
			startIndex = endIndex + 1;
		}

		mTextPaint.setTextSize(mOriginalTextSize);
		mStrokePaint.setTextSize(mOriginalTextSize);

		return textSize;
	}

	/**
	 * 
	 * @param canvas
	 * @param bitmapWidth
	 * @param bitmapHeight
	 * @param leftRightMargin
	 * @param text
	 * @return text size
	 */
	public float drawCenterCaption(Canvas canvas, final int bitmapWidth, final int bitmapHeight,
			final float leftRightMargin, String text) {
		if (text == null || text.length() == 0) {
			return -1;
		}
		if (text.length() > 2 * MAX_CHARACTER_COUNT) {
			if (mDrawCaptionListener != null) {
				mDrawCaptionListener.textTooLong(text.length(), mOriginalTextSize);
			}
			return mOriginalTextSize;
		}

		final List<String> wordList = findWords(text);
		Paint paint = new Paint();
		paint.setTextSize(mOriginalTextSize);
		// final float margin = paint.measureText("m");
		final int maxLen = (int) (bitmapWidth - 2 * leftRightMargin);// - 2 *
																		// margin);
		float textSize = mOriginalTextSize;
		if (wordList.size() > 0) {
			if (text.length() <= MAX_CHARACTER_COUNT) {
				textSize = findTextSize(mTextPaint, wordList, MAX_TEXT_LINES, maxLen, bitmapHeight / 4.0f, mMinTextSize,
						mMaxTextSize);
			} else {
				textSize = findTextSize(mTextPaint, wordList, 2 * MAX_TEXT_LINES, maxLen, bitmapHeight / 2.0f,
						mMinTextSize, mMaxTextSize);
			}

			if (textSize < 0) {
				if (mDrawCaptionListener != null) {
					mDrawCaptionListener.textTooLong(text.length(), textSize);
				}
				return textSize;
			} else {
				mTextPaint.setTextSize(textSize);
				mStrokePaint.setTextSize(textSize);
			}
		}

		final Rect textBound = new Rect();
		mTextPaint.getTextBounds(text, 0, text.length(), textBound);
		final int textHeight = textBound.height();
		mTextPaint.getTextBounds("m", 0, 1, textBound);
		final int delta = (int) (0.5 * textBound.height() + textHeight);
		float height = findSentenceHeight(mTextPaint, maxLen, text);
		float y = (bitmapHeight - height) / 2.0f + textHeight;
		final int wordCount = wordList.size();
		int startIndex = 0;
		while (startIndex < wordCount) {
			// find number of words on a line.
			int endIndex = startIndex;
			for (int idx = wordCount - 1; idx >= startIndex; idx--) {
				String sentence = toNormalSentence(wordList, startIndex, idx);
				float textLen = mTextPaint.measureText(sentence);
				if (textLen < maxLen) {
					endIndex = idx;
					break;
				}
			}
			// draw text
			String sentence = toNormalSentence(wordList, startIndex, endIndex);
			float x = (bitmapWidth - mTextPaint.measureText(sentence)) / 2.0f;
			if (mDrawStroke) {
				canvas.drawText(sentence, x, y, mStrokePaint);
			}
			canvas.drawText(sentence, x, y, mTextPaint);

			y += delta;
			// update infor
			startIndex = endIndex + 1;
		}
		mTextPaint.setTextSize(mOriginalTextSize);
		mStrokePaint.setTextSize(mOriginalTextSize);

		return textSize;
	}

	/**
	 * 
	 * @param canvas
	 * @param bitmapWidth
	 * @param bitmapHeight
	 * @param bottomMargin
	 * @param leftRightMargin
	 * @param text
	 * @return text size
	 */
	public float drawBottomCaption(Canvas canvas, final int bitmapWidth, final int bitmapHeight, float bottomMargin,
			final float leftRightMargin, final String text) {
		if (text == null || text.length() == 0) {
			return -1;
		}
		if (text.length() > MAX_CHARACTER_COUNT) {
			if (mDrawCaptionListener != null) {
				mDrawCaptionListener.textTooLong(text.length(), mOriginalTextSize);
			}
			return mOriginalTextSize;
		}

		final List<String> wordList = findWords(text);
		// set text size
		float textSize = mOriginalTextSize;
		Paint paint = new Paint();
		paint.setTextSize(mOriginalTextSize);
		// final float margin = paint.measureText("m");
		final int maxLen = (int) (bitmapWidth - 2 * leftRightMargin);// - 2 *
																		// margin);
		if (wordList.size() > 0) {
			textSize = findTextSize(mTextPaint, wordList, MAX_TEXT_LINES, maxLen, bitmapHeight / 4.0f, mMinTextSize,
					mMaxTextSize);
			if (textSize > 0) {
				mTextPaint.setTextSize(textSize);
				mStrokePaint.setTextSize(textSize);
			} else {
				if (mDrawCaptionListener != null) {
					mDrawCaptionListener.textTooLong(text.length(), textSize);
				}
				mTextPaint.setTextSize(mOriginalTextSize);
				mStrokePaint.setTextSize(mOriginalTextSize);
				return textSize;
			}
		}

		final Rect textBound = new Rect();
		mTextPaint.getTextBounds(text, 0, text.length(), textBound);
		final int textHeight = textBound.height();
		mTextPaint.getTextBounds("m", 0, 1, textBound);
		final int delta = (int) (0.5 * textBound.height() + textHeight);
		// draw texts
		final int wordCount = wordList.size();
		int endIndex = wordCount - 1;
		float y = bitmapHeight - bottomMargin;
		// find start y
		while (endIndex >= 0) {
			// find number of words on a line.
			int startIndex = endIndex;
			for (int idx = 0; idx <= endIndex; idx++) {
				String sentence = toNormalSentence(wordList, idx, endIndex);
				float textLen = mTextPaint.measureText(sentence);
				if (textLen < maxLen) {
					startIndex = idx;
					break;
				}
			}

			y -= delta;
			// update infor
			endIndex = startIndex - 1;
		}
		// draw text
		int startIndex = 0;
		y = y + delta;
		while (startIndex < wordCount) {
			// find number of words on a line.
			endIndex = startIndex;
			for (int idx = wordCount - 1; idx >= startIndex; idx--) {
				String sentence = toNormalSentence(wordList, startIndex, idx);
				float textLen = mTextPaint.measureText(sentence);
				if (textLen < maxLen) {
					endIndex = idx;
					break;
				}
			}
			// draw text
			String sentence = toNormalSentence(wordList, startIndex, endIndex);
			float x = (bitmapWidth - mTextPaint.measureText(sentence)) / 2.0f;
			if (mDrawStroke) {
				canvas.drawText(sentence, x, y, mStrokePaint);
			}
			canvas.drawText(sentence, x, y, mTextPaint);

			y += delta;
			// update infor
			startIndex = endIndex + 1;
		}

		mTextPaint.setTextSize(mOriginalTextSize);
		mStrokePaint.setTextSize(mOriginalTextSize);

		return textSize;
	}

	/**
	 * 
	 * @param paint
	 * @param wordList
	 * @param widthBound
	 * @return number of lines texts occupied.
	 */
	public static int findTextLineCount(final Paint paint, List<String> wordList, final float widthBound) {
		final int wordCount = wordList.size();
		int lineCount = 0;
		int startIndex = 0;
		while (startIndex < wordCount) {
			// find number of words on a line.
			int endIndex = startIndex;
			for (int idx = wordCount - 1; idx >= startIndex; idx--) {
				String sentence = toNormalSentence(wordList, startIndex, idx);
				float textLen = paint.measureText(sentence);
				if (textLen < widthBound) {
					endIndex = idx;
					break;
				}
			}
			// update infor
			startIndex = endIndex + 1;
			lineCount++;
		}

		return lineCount;
	}

	public static float findTextSizeWithEnter(final Paint paint, final String text, final int maxTextLines,
			final float widthBound, final float heightBound, final float minTextSize, final float maxTextSize) {
		List<String> lineList = findLines(text);
		float minSize = minTextSize;
		for (String line : lineList) {
			List<String> wordList = findWords(line);
			float size = findTextSize(paint, wordList, 1, widthBound, heightBound, minTextSize, maxTextSize);
			if (size < 0)
				size = minTextSize;
			if (size < minSize)
				minSize = size;
		}

		return minSize;
	}

	/**
	 * 
	 * @param paint
	 * @param wordList
	 * @param maxTextLines
	 * @param widthBound
	 * @param minTextSize
	 * @param maxTextSize
	 * @return text size, return -1 if text is too long.
	 */
	public static float findTextSize(final Paint paint, List<String> wordList, final int maxTextLines,
			final float widthBound, final float heightBound, final float minTextSize, final float maxTextSize) {
		final float originalTextSize = paint.getTextSize();
		float textSize = minTextSize;
		float result = minTextSize;
		final float delta = 1.0f;
		while (textSize <= maxTextSize) {
			paint.setTextSize(textSize);
			int lineCount = findTextLineCount(paint, wordList, widthBound);
			if (lineCount > maxTextLines) {
				break;
			}
			float height = findSentenceHeight(paint, widthBound, wordList);
			if (height > heightBound) {
				break;
			}
			result = textSize;
			textSize += delta;
		}

		paint.setTextSize(result);
		int lineCount = findTextLineCount(paint, wordList, widthBound);
		paint.setTextSize(originalTextSize);
		if (lineCount > maxTextLines) {
			return -1;
		}

		return result;
	}

	public static List<String> findLines(String text) {
		List<String> lines = new ArrayList<String>();
		final int len = text.length();
		int idx = 0;
		StringBuilder builder = new StringBuilder();
		while (idx < len) {
			if (text.charAt(idx) != '\n')
				builder.append(text.charAt(idx));

			if ((text.charAt(idx) == '\n' || idx == len - 1) && builder.length() > 0) {
				lines.add(builder.toString());
				builder = new StringBuilder();
			}

			idx++;
		}

		return lines;
	}

	public static float findSentenceHeight(Paint paint, final float bitmapWidth, final float leftRightMargin,
			List<String> wordList) {
		final String text = toNormalSentence(wordList, 0, wordList.size() - 1);
		return findSentenceHeight(paint, bitmapWidth, leftRightMargin, text);
	}

	public static float findSentenceHeight(Paint paint, final float widthBound, List<String> wordList) {
		final String text = toNormalSentence(wordList, 0, wordList.size() - 1);
		return findSentenceHeight(paint, widthBound, text);
	}

	public static float findSentenceHeight(Paint paint, final float widthBound, String text) {
		final List<String> wordList = findWords(text);
		final Rect textBound = new Rect();
		paint.getTextBounds(text, 0, text.length(), textBound);
		final int textHeight = textBound.height();
		paint.getTextBounds("m", 0, 1, textBound);
		final int delta = (int) (0.5 * textBound.height() + textHeight);
		float height = 0;
		final int wordCount = wordList.size();
		int startIndex = 0;
		while (startIndex < wordCount) {
			// find number of words on a line.
			int endIndex = startIndex;
			for (int idx = wordCount - 1; idx >= startIndex; idx--) {
				String sentence = toNormalSentence(wordList, startIndex, idx);
				float textLen = paint.measureText(sentence);
				if (textLen <= widthBound) {
					endIndex = idx;
					break;
				}
			}
			// draw text
			height += delta;
			// update infor
			startIndex = endIndex + 1;
		}

		return height;
	}

	public static float findSentenceHeight(Paint paint, final float bitmapWidth, final float leftRightMargin,
			String text) {
		final List<String> wordList = findWords(text);

		final Rect textBound = new Rect();
		paint.getTextBounds(text, 0, text.length(), textBound);
		final int textHeight = textBound.height();
		paint.getTextBounds("m", 0, 1, textBound);
		final int delta = (int) (0.5 * textBound.height() + textHeight);
		final int maxLen = (int) (bitmapWidth - 2 * leftRightMargin - textBound.width());
		float height = 0;
		final int wordCount = wordList.size();
		int startIndex = 0;
		while (startIndex < wordCount) {
			// find number of words on a line.
			int endIndex = startIndex;
			for (int idx = wordCount - 1; idx >= startIndex; idx--) {
				String sentence = toNormalSentence(wordList, startIndex, idx);
				float textLen = paint.measureText(sentence);
				if (textLen < maxLen) {
					endIndex = idx;
					break;
				}
			}
			// draw text
			height += delta;
			// update infor
			startIndex = endIndex + 1;
		}

		return height;
	}

	public float findSentenceHeight(final int bitmapWidth, final float leftRightMargin, String text) {
		return findSentenceHeight(mTextPaint, bitmapWidth, leftRightMargin, text);
	}

	public static List<String> findWords(String sentence) {
		final List<String> result = new ArrayList<String>();
		final int len = sentence.length();
		StringBuilder word = new StringBuilder();
		for (int idx = 0; idx < len; idx++) {
			char c = sentence.charAt(idx);
			if (c != ' ') {
				word.append(c);
			} else {
				if (word.length() > 0) {
					result.add(word.toString());
					word = new StringBuilder();
				}
			}
		}

		if (word.length() > 0) {
			result.add(word.toString());
		}

		return result;
	}

	public static String toNormalSentence(List<String> wordList) {
		String result = wordList.get(0);
		for (int idx = 1; idx < wordList.size(); idx++) {
			result = result.concat(" ").concat(wordList.get(idx));
		}

		return result;
	}

	public static String toNormalSentence(List<String> wordList, int startIndex, int endIndex) {
		String result = wordList.get(startIndex);
		for (int idx = startIndex + 1; idx <= endIndex; idx++) {
			result = result.concat(" ").concat(wordList.get(idx));
		}
		return result;
	}

	/**
	 * Find direction when touch. Direction is to move up or down or left or
	 * right.
	 * 
	 * @param first
	 * @param second
	 * @return direction. It is MOVE_UP or MOVE_DOWN or MOVE_LEFT or MOVE_RIGHT
	 */
	public static int findDirectionMovement(PointF first, PointF second) {
		final float x = second.x - first.x;
		final float y = second.y - first.y;
		float px = Math.abs(x);
		float py = Math.abs(y);
		if (y * x >= 0) {
			if (x < 0) {
				if (px >= py) {
					return MOVE_LEFT;
				} else {
					return MOVE_UP;
				}
			} else {
				if (px >= py) {
					return MOVE_RIGHT;
				} else {
					return MOVE_DOWN;
				}
			}
		} else {
			if (x < 0) {
				if (px >= py) {
					return MOVE_LEFT;
				} else {
					return MOVE_DOWN;
				}
			} else {
				if (px >= py) {
					return MOVE_RIGHT;
				} else {
					return MOVE_UP;
				}
			}
		}
	}

	public void resetAllValues() {
		mText = "";
		mTextColor = Color.WHITE;
		mOriginalTextSize = getContext().getResources().getDimension(R.dimen.photo_editor_text_default_size);

		mFontTf = Typeface.DEFAULT;
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTypeface(mFontTf);
		mTextPaint.setTextSize(mOriginalTextSize);

		mStrokeColor = Color.BLACK;
		mStrokePaint.setColor(mTextColor);
		mStrokePaint.setTypeface(mFontTf);
		mStrokePaint.setTextSize(mOriginalTextSize);

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void init() {
		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		// final String fontName = "impact.ttf";
		// final String path = BASE_FONT_ASSET_FOLDER.concat(fontName);
		// mFontTf = Typeface.createFromAsset(getContext().getAssets(), path);
		// mTextPaint.setTypeface(mFontTf);
		// mStrokePaint.setTypeface(mFontTf);
		// mFontName = fontName;

		mOriginalTextSize = getContext().getResources().getDimension(R.dimen.photo_editor_text_default_size);
		mFirstTextSize = mOriginalTextSize;
		mSecondTextSize = mOriginalTextSize;
		mMinTextSize = getContext().getResources().getDimension(R.dimen.photo_editor_text_min_size);
		mMaxTextSize = getContext().getResources().getDimension(R.dimen.photo_editor_text_max_size);

		initPaints();

		float scale = getContext().getResources().getDisplayMetrics().density;
		mTopMarginCaption = getResources().getDimension(R.dimen.photo_editor_top_margin_caption);
		mLeftRightMarginCaption = getResources().getDimension(R.dimen.photo_editor_left_right_margin_caption);
		mBottomMarginCaption = getResources().getDimension(R.dimen.photo_editor_bottom_margin_caption);
		mStrokeWidth = getResources().getDimension(R.dimen.photo_editor_stroke_width);
		mMinTouchDist = (int) (MIN_TOUCH_DIST_DP * scale + 0.5f);
	}

	public interface OnDrawCaptionListener {
		/**
		 * Call when characterCount > allowed max character count or textSize <
		 * 0
		 * 
		 * @param characterCount
		 * @param textSize
		 */
		public void textTooLong(int characterCount, float textSize);
	}

	public interface OnChangeDirectionListener {
		/**
		 * @param direction
		 *            It is MOVE_UP or MOVE_DOWN or MOVE_LEFT or MOVE_RIGHT
		 */
		public void changeDirection(int direction);

		public void clickAt(float x, float y);
	}

	public static class CaptionImageInfos {
		public static final String TEXT_SIZE_KEY = "textSize";
		public static final String TEXT_COLOR_KEY = "textCorlor";
		public static final String FONT_NAME_KEY = "fontName";
		public static final String TEXT_KEY = "text";
		public static final String TEXT2_KEY = "text2";
		public static final String IS_MEME_KEY = "isMeme";

		public float textSize = 20f;
		public int textColor = Color.BLACK;
		public String fontName;
		public String text = "";
		public String text2 = "";
		public boolean isMeme = true;
	}
}
