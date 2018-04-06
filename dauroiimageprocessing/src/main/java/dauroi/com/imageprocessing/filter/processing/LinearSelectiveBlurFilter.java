package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class LinearSelectiveBlurFilter extends TwoInputFilter {
	public static final String LINEAR_FOCUS_FRAGMENT = "precision highp float;\n"
			+ "varying highp vec2 textureCoordinate;\n"
			+ "varying highp vec2 textureCoordinate2;\n"
			+ "uniform sampler2D inputImageTexture;\n"
			+ "uniform sampler2D inputImageTexture2;\n"
			+ "uniform vec3 line;\n"
			+ "uniform float radius;\n"
			+ "uniform float exclude;\n"

			+ "float distanceFromLine(vec3 line, vec2 point){\n"
			+ "	float v = abs(line.x * point.x + line.y * point.y + line.z);\n"
			+ "	float s = sqrt(line.x * line.x + line.y * line.y);\n"
			+ "	return (v / s);\n"
			+ "}\n"
			+

			"void main(){\n"
			+ "	lowp vec4 sharpImageColor = texture2D(inputImageTexture, textureCoordinate);\n"
			+ "	lowp vec4 blurredImageColor = texture2D(inputImageTexture2, textureCoordinate2);\n"
			+ "	float d = distanceFromLine(line, gl_FragCoord.xy);\n"
			+ "   if(d < radius){\n" 
        	+ "	    gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(radius - exclude, radius, d));\n"
        	+ "	 }else\n" 
			+ "		gl_FragColor = blurredImageColor;\n"
			+ "}";

	private int mGLLineLocation;
	private float[] mLine = { 1.0F, 1.0F, 1.0F };
	private int mGLRadiusLocation;
	private float mRadius = 1.0F;
	private int mGLExcludeLocation;
	private float mExclude = 0.1F;

	public LinearSelectiveBlurFilter() {
		super(LINEAR_FOCUS_FRAGMENT);
	}

	public LinearSelectiveBlurFilter(float[] line, float radius, float exclude) {
		super(LINEAR_FOCUS_FRAGMENT);
		this.mLine = line;
		this.mRadius = radius;
		this.mExclude = exclude;

	}

	public void onInit() {
		super.onInit();
		this.mGLLineLocation = GLES20
				.glGetUniformLocation(getProgram(), "line");
		this.mGLRadiusLocation = GLES20.glGetUniformLocation(getProgram(),
				"radius");
		this.mGLExcludeLocation = GLES20.glGetUniformLocation(getProgram(),
				"exclude");
	}

	public void onInitialized() {
		super.onInitialized();
		setLine(this.mLine);
		setRadius(this.mRadius);
		setExclude(this.mExclude);
	}

	public void setLine(float[] line) {
		this.mLine = line;
		setFloatVec3(this.mGLLineLocation, line);
	}

	public void setRadius(float radius) {
		this.mRadius = radius;
		setFloat(this.mGLRadiusLocation, radius);
	}

	public void setExclude(float exclude) {
		this.mExclude = exclude;
		setFloat(this.mGLExcludeLocation, exclude);
	}
	
	public float[] getLine() {
		return mLine;
	}
	
	public float getRadius() {
		return mRadius;
	}
	
	public float getExclude() {
		return mExclude;
	}
}
