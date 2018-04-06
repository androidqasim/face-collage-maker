package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class TouchBlurFilter extends TwoInputFilter {
	public static final String SELECTIVE_BLUR_FRAGMENT_SHADER = 
			"precision highp float;\n" +
			"varying highp vec2 textureCoordinate;\n"
			+ "varying highp vec2 textureCoordinate2;\n"
			+ "uniform sampler2D inputImageTexture;\n"
			+ "uniform sampler2D inputImageTexture2;\n"
			+ "uniform lowp float excludeCircleRadius;\n"
			+ "uniform lowp vec2 excludeCirclePoint;\n"
			+ "uniform lowp float excludeBlurSize;\n"

			+ "void main(){\n"
			+ "lowp vec4 sharpImageColor = texture2D(inputImageTexture, textureCoordinate);\n"
			+ "lowp vec4 blurredImageColor = texture2D(inputImageTexture2, textureCoordinate2);\n"
			+ "highp float distanceFromCenter = distance(gl_FragCoord.xy, excludeCirclePoint);\n"
			+ "   if(distanceFromCenter < excludeCircleRadius){\n"
			+ "		gl_FragColor = mix(blurredImageColor, sharpImageColor, smoothstep(excludeCircleRadius - excludeBlurSize, excludeCircleRadius, distanceFromCenter));\n"
			+ "	  }else\n" + "    gl_FragColor = sharpImageColor;\n" + "}";

	private int mRadiusLocation;
	private float mRadius;
	private int mCenterPointLocation;
	private float[] mCenterPoint;
	private int mBlurSizeLocation;
	private float mBlurSize;

	public TouchBlurFilter(float[] center, float radius,
			float blurSize) {
		super(SELECTIVE_BLUR_FRAGMENT_SHADER);
		mRadius = radius;
		mCenterPoint = center;
		mBlurSize = blurSize;
	}

	public TouchBlurFilter() {
		super(SELECTIVE_BLUR_FRAGMENT_SHADER);
		mRadius = 0.1f;
		mCenterPoint = new float[] { 0.5f, 0.5f };
		mBlurSize = 0.1f;
	}

	@Override
	public void onInit() {
		super.onInit();
		mRadiusLocation = GLES20.glGetUniformLocation(getProgram(),
				"excludeCircleRadius");
		mCenterPointLocation = GLES20.glGetUniformLocation(getProgram(),
				"excludeCirclePoint");
		mBlurSizeLocation = GLES20.glGetUniformLocation(getProgram(),
				"excludeBlurSize");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setRadius(mRadius);
		setCenterPoint(mCenterPoint);
		setBlurSize(mBlurSize);
	}

	public void setRadius(float radius) {
		mRadius = radius;
		setFloat(mRadiusLocation, mRadius);
	}

	public void setCenterPoint(float[] centerPoint) {
		mCenterPoint = centerPoint;
		setFloatVec2(mCenterPointLocation, mCenterPoint);
	}

	public void setBlurSize(float blurSize) {
		mBlurSize = blurSize;
		setFloat(mBlurSizeLocation, mBlurSize);
	}

	public float getRadius() {
		return mRadius;
	}

	public float getBlurSize() {
		return mBlurSize;
	}

	public float[] getCenterPoint() {
		return mCenterPoint;
	}
}
