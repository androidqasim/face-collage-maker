package dauroi.photoeditor.particle;

import android.content.Context;
import android.graphics.Canvas;

public class Explosion {

	public static final int ALIVE = 0;
	public static final int DEAD = 1;
	private final static int LIFETIME = 50;
	private final static int MAX_SCALE = 4;
	private final static int MAX_SPEED = 30;

	private Particle[] mParticles;
	private int mState;

	public Explosion(int numberOfParticles, int x, int y, Context c) {
		mState = ALIVE;
		mParticles = new Particle[numberOfParticles];
		for (int i = 0; i < mParticles.length; i++) {
			Particle p = new Particle(x, y, LIFETIME, MAX_SPEED, MAX_SCALE, c);
			mParticles[i] = p;
		}
	}

	public boolean isDead() {
		return mState == DEAD;
	}

	public void update(Canvas canvas) {
		if (mState != DEAD) {
			boolean isDead = true;
			for (int i = 0; i < mParticles.length; i++) {
				if (mParticles[i].isAlive()) {
					mParticles[i].update();
					isDead = false;
				}
			}
			if (isDead)
				mState = DEAD;
		}
		draw(canvas);
	}

	public void draw(Canvas canvas) {
		for (int i = 0; i < mParticles.length; i++) {
			if (mParticles[i].isAlive()) {
				mParticles[i].draw(canvas);
			}
		}
	}
}