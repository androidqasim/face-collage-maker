package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class PinchDistortionFilter extends ImageFilter {
	public static final String PINCH_DISTORTION_FRAGMENT_SHADER = 
	  "precision highp float;\n" +
	 "varying highp vec2 textureCoordinate;\n" +
	 "uniform sampler2D inputImageTexture;\n" +
			 
	 "uniform highp float aspectRatio;\n" +
	 "uniform highp vec2 center;\n" +
	 "uniform highp float radius;\n" + 
	 "uniform highp float scale;\n" + 
	 
	 "void main()\n" +
	 "{\n" +
	     "highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
	     "highp float dist = distance(center, textureCoordinateToUse);\n" +
	     "textureCoordinateToUse = textureCoordinate;\n" +
	     
	     "if (dist < radius)\n" +
	     "{\n" +
	         "textureCoordinateToUse -= center;\n" +
	         "highp float percent = 1.0 + ((0.5 - dist) / 0.5) * scale;\n" +
	         "textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
	         "textureCoordinateToUse += center;\n" +
	         
	         "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );\n" + 
	     "}else{\n" + 
	         "gl_FragColor = texture2D(inputImageTexture, textureCoordinate );\n" + 
	     "}\n" + 
	 "}";
	
	private int mAspectRatioLocation;
	private float mAspectRatio;
	private int mCenterLocation;
	private float[] mCenter;
	private int mRadiusLocation;
	private float mRadius;
	private int mScaleLocation;
	private float mScale;

	public PinchDistortionFilter() {
		super(NO_FILTER_VERTEX_SHADER, PINCH_DISTORTION_FRAGMENT_SHADER);
		mAspectRatio = 1.0f;
		mCenter = new float[]{0.5f, 0.5f};
		mRadius = 1.0f;
		mScale = 0.5f;
	}

	@Override
	public void onInit() {
		super.onInit();
		mAspectRatioLocation = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
		mCenterLocation = GLES20.glGetUniformLocation(getProgram(), "center");
		mRadiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
		mScaleLocation = GLES20.glGetUniformLocation(getProgram(), "scale");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setAspectRatio(mAspectRatio);
		setCenter(mCenter);
		setRadius(mRadius);
		setScale(mScale);
	}

	public void setAspectRatio(float aspectRatio) {
		mAspectRatio = aspectRatio;
		setFloat(mAspectRatioLocation, mAspectRatio);
	}
	
	public void setCenter(float[] center) {
		mCenter = center;
		setFloatVec2(mCenterLocation, mCenter);
	}
	
	public void setRadius(float radius) {
		mRadius = radius;
		setFloat(mRadiusLocation, mRadius);
	}
	
	public void setScale(float scale) {
		mScale = scale;
		setFloat(mScaleLocation, mScale);
	}
	
}
