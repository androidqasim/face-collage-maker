package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class GreenCircleFilter extends ImageFilter {
	public static final String RED_FILTER_FRAGMENT_SHADER = 
			"precision highp float;\n" 
			+ "uniform highp vec2 centerCircle;\n"
			+ "uniform highp float radius;\n"
			+ "uniform highp float aspectRatio;\n"
			+ "varying highp vec2 textureCoordinate;\n"
			+ " \n"
			+ "uniform sampler2D inputImageTexture;\n"
			+ " \n"
			+ "void main()\n"
			+ "{\n"
			+ "     highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n"
			+ "     highp float distanceFromCenter = distance(centerCircle, textureCoordinateToUse);\n"
			+ "     if(distanceFromCenter < radius){\n"
			+ "			gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n"
			+ "		}else\n"
			+ "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n"
			+ "}";

	private int mCenterCircleLocation;
	private float[] mCenterCircle;
	private int mRadiusLocation;
	private float mRadius;
	private int mAspectRatioLocation;
	private float mAspectRatio;

	public GreenCircleFilter() {
		super(NO_FILTER_VERTEX_SHADER, RED_FILTER_FRAGMENT_SHADER);
		mCenterCircle = new float[] { 0.5f, 0.5f };
		mRadius = 0.1f;
		mAspectRatio = 1.0f;
	}

	@Override
	public void onInit() {
		super.onInit();
		mCenterCircleLocation = GLES20.glGetUniformLocation(getProgram(),
				"centerCircle");
		mRadiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
		mAspectRatioLocation = GLES20.glGetUniformLocation(getProgram(),
				"aspectRatio");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setCenterCircle(mCenterCircle);
		setRadius(mRadius);
		setAspectRatio(mAspectRatio);
	}

	public void setCenterCircle(float[] centerCircle) {
		mCenterCircle = centerCircle;
		setFloatVec2(mCenterCircleLocation, mCenterCircle);
	}

	public void setRadius(float radius) {
		mRadius = radius;
		setFloat(mRadiusLocation, radius);
	}

	public void setAspectRatio(float aspectRatio) {
		mAspectRatio = aspectRatio;
		setFloat(mAspectRatioLocation, mAspectRatio);
	}

}
