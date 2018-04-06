package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class TouchPixelFilter extends ImageFilter{
	public static final String TOUCH_FILTER_FRAGMENT_SHADER = 
			"precision highp float;\n" +
			"uniform vec2 centerCircle;\n" +
			"uniform float radius;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "	highp float distanceFromCenter = distance(gl_FragCoord.xy, centerCircle);\n" +
        	"   if(distanceFromCenter < radius){\n" +
        	"		gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n" +
        	"		}else\n" +
        	"     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";
	
	private int mCenterCircleLocation;
	private float[] mCenterCircle;
	private int mRadiusLocation;
	private float mRadius;
	
	public TouchPixelFilter(){
		super(NO_FILTER_VERTEX_SHADER, TOUCH_FILTER_FRAGMENT_SHADER);
		mCenterCircle = new float[] { 0, 0 };
		mRadius = 1;
	}
	
	@Override
	public void onInit() {
		super.onInit();
		mCenterCircleLocation = GLES20.glGetUniformLocation(getProgram(),
				"centerCircle");
		mRadiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setCenterCircle(mCenterCircle);
		setRadius(mRadius);
	}
	
	public void setCenterCircle(float[] centerCircle) {
		mCenterCircle = centerCircle;
		setFloatVec2(mCenterCircleLocation, mCenterCircle);
	}

	public void setRadius(float radius) {
		mRadius = radius;
		setFloat(mRadiusLocation, radius);
	}
	
	public float getRadius() {
		return mRadius;
	}
	
	public float[] getCenterCircle() {
		return mCenterCircle;
	}
}
