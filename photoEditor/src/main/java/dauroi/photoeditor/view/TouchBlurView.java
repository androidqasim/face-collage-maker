package dauroi.photoeditor.view;

import dauroi.photoeditor.R;
import dauroi.photoeditor.particle.Explosion;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TouchBlurView extends ImageView {
	public static interface OnTouchBlurListener {
		public void onTouchBlur(float x, float y);
	}

	private final static int NUM_PARTICLES = 25;
	private final static int FRAME_RATE = 30;
	private Handler mHandler;
	private Explosion mExplosion;
	private int mExplosionImageWidth = 0;
	private int mExplosionImageHeight = 0;

	private OnTouchBlurListener mTouchBlurListener;

	public TouchBlurView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TouchBlurView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TouchBlurView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mHandler = new Handler();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(),
				R.drawable.photo_editor_fire, options);
		mExplosionImageWidth = options.outHeight;
		mExplosionImageHeight = options.outWidth;
	}

	public void setTouchBlurListener(OnTouchBlurListener touchBlurListener) {
		mTouchBlurListener = touchBlurListener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mExplosion != null && !mExplosion.isDead()) {
			mExplosion.update(canvas);
			mHandler.removeCallbacks(mRunner);
			mHandler.postDelayed(mRunner, FRAME_RATE);
		} else if (mExplosion != null && mExplosion.isDead()) {

		}

		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (mExplosion == null || mExplosion.isDead()) {
				mExplosion = new Explosion(NUM_PARTICLES, (int) event.getX()
						- mExplosionImageWidth / 2, (int) event.getY()
						- mExplosionImageHeight / 2, getContext());
				mHandler.removeCallbacks(mRunner);
				mHandler.post(mRunner);
			}

			if (mTouchBlurListener != null) {
				mTouchBlurListener.onTouchBlur(event.getX(), event.getY());
			}
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	private Runnable mRunner = new Runnable() {
		@Override
		public void run() {
			mHandler.removeCallbacks(mRunner);
			invalidate();
		}
	};
}
