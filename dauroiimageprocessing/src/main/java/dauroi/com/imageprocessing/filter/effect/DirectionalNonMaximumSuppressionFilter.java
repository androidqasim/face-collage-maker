package dauroi.com.imageprocessing.filter.effect;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class DirectionalNonMaximumSuppressionFilter extends ImageFilter{
	 public static final String DIRECTIONAL_NON_MAXINUM_SUPPRESSION_FRAGMENT_SHADER = "precision mediump float;\n" +
			 "varying highp vec2 textureCoordinate;\n" +
			 "uniform sampler2D inputImageTexture;\n" +
			 "uniform highp float texelWidth;\n" + 
			 "uniform highp float texelHeight;\n" + 
			 "uniform mediump float upperThreshold;\n" + 
			 "uniform mediump float lowerThreshold;\n" + 
			 "void main()\n" +
			 "{\n" +
			 "	vec3 currentGradientAndDirection = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
			 "	vec2 gradientDirection = ((currentGradientAndDirection.gb * 2.0) - 1.0) * vec2(texelWidth, texelHeight);\n" +
			 "	float firstSampledGradientMagnitude = texture2D(inputImageTexture, textureCoordinate + gradientDirection).r;\n" +
			 "	float secondSampledGradientMagnitude = texture2D(inputImageTexture, textureCoordinate - gradientDirection).r;\n" +
			 "	float multiplier = step(firstSampledGradientMagnitude, currentGradientAndDirection.r);\n" +
			 "	multiplier = multiplier * step(secondSampledGradientMagnitude, currentGradientAndDirection.r);\n" +
			 "	float thresholdCompliance = smoothstep(lowerThreshold, upperThreshold, currentGradientAndDirection.r);\n" +
			 "	multiplier = multiplier * thresholdCompliance;\n" +
			 "	gl_FragColor = vec4(multiplier, multiplier, multiplier, 1.0);\n" +
 			"}";
	 
	 private int mTexelWidthLocation;
	 private float mTexelWidth;
	 private int mTexelHeightLocation;
	 private float mTexelHeight;
	 private int mUpperThresholdLocation;
	 private float mUpperThreshold;
	 private int mLowerThresholdLocation;
	 private float mLowerThreshold;
	 
	 public DirectionalNonMaximumSuppressionFilter(){
		 super(NO_FILTER_VERTEX_SHADER, DIRECTIONAL_NON_MAXINUM_SUPPRESSION_FRAGMENT_SHADER);
	 }
	 
	@Override
	public void onInit() {
		super.onInit();
		mTexelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
		mTexelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
		mUpperThresholdLocation = GLES20.glGetUniformLocation(getProgram(), "upperThreshold");
		mLowerThresholdLocation = GLES20.glGetUniformLocation(getProgram(), "lowerThreshold");
	}
	
	@Override
	public void onInitialized() {
		super.onInitialized();
		setTexelWidth(mTexelWidth);
		setTexelHeight(mTexelHeight);
		setUpperThreshold(mUpperThreshold);
		setLowerThreshold(mLowerThreshold);
	}
	
	public void setTexelWidth(float texelWidth) {
		mTexelWidth = texelWidth;
		setFloat(mTexelWidthLocation, mTexelWidth);
	}
	
	public void setTexelHeight(float texelHeight) {
		mTexelHeight = texelHeight;
		setFloat(mTexelHeightLocation, mTexelHeight);
	}
	
	public void setUpperThreshold(float upperThreshold) {
		mUpperThreshold = upperThreshold;
		setFloat(mUpperThresholdLocation, mUpperThreshold);
	}
	
	public void setLowerThreshold(float lowerThreshold) {
		mLowerThreshold = lowerThreshold;
		setFloat(mLowerThresholdLocation, mLowerThreshold);
	}
}