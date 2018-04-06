package dauroi.com.imageprocessing.filter.colour;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class ChannelLookupFilter extends ImageFilter {
	 public static final String CHANNEL_LOOKUP_FRAGMENT_SHADER = "" +
			 	"precision highp float;\n" +
	            "varying highp vec2 textureCoordinate;\n" + 
	            " \n" + 
	            " uniform sampler2D inputImageTexture;\n" + 
	            " uniform vec4 red;\n" + 
	            " uniform vec4 green;\n" + 
	            " uniform vec4 blue;\n" + 
	            " uniform vec4 alpha;\n" + 
	            " \n" + 
	            " void main()\n" + 
	            " {\n" + 
	            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
	            "     float r = red.x * textureColor.r * textureColor.r * textureColor.r + red.y * textureColor.r * textureColor.r + red.z * textureColor.r + red.w;\n" +
	            "     if(r < 0.0) r = 0.0;\n" +
	            "     if(r > 1.0) r = 1.0;\n" +
	            
	            "     float g = green.x * textureColor.g * textureColor.g * textureColor.g + green.y * textureColor.g * textureColor.g + green.z * textureColor.g + green.w;\n" +
	            "     if(g < 0.0) g = 0.0;\n" +
	            "     if(g > 1.0) g = 1.0;\n" +
	            
	            "     float b = blue.x * textureColor.b * textureColor.b * textureColor.b + blue.y * textureColor.b * textureColor.b + blue.z * textureColor.b + blue.w;\n" +
	            "     if(b < 0.0) b = 0.0;\n" +
	            "     if(b > 1.0) b = 1.0;\n" +
	            
	            "     float a = alpha.x * textureColor.a * textureColor.a * textureColor.a + alpha.y * textureColor.a * textureColor.a + alpha.z * textureColor.a + alpha.w;\n" +
	            "     if(a < 0.0) a = 0.0;\n" + 
	            "     if(a > 1.0) a = 1.0;\n" +
	            
	            "     gl_FragColor = vec4(r, g, b, a);\n" + 
	            " }";
	 
	private int mRedLocation;
	private float[] mRed;
	private int mGreenLocation;
	private float[] mGreen;
	private int mBlueLocation;
	private float[] mBlue;
	private int mAlphaLocation;
	private float[] mAlpha;
	/**
	 * Values of array in range 0..1
	 * @param red
	 * @param green
	 * @param blue
	 */
	public ChannelLookupFilter(float[] redCoeff, float[] greenCoeff, float[] blueCoeff, float[] alphaCoeff) {
		super(NO_FILTER_VERTEX_SHADER, CHANNEL_LOOKUP_FRAGMENT_SHADER);
		mRed = redCoeff;
		mGreen = greenCoeff;
		mBlue = blueCoeff;
		mAlpha = alphaCoeff;
	}

	@Override
	public void onInit() {
		super.onInit();
		mRedLocation = GLES20.glGetUniformLocation(getProgram(),
				"red");
		mGreenLocation = GLES20.glGetUniformLocation(getProgram(),
				"green");
		mBlueLocation = GLES20.glGetUniformLocation(getProgram(),
				"blue");
		mAlphaLocation = GLES20.glGetUniformLocation(getProgram(),
				"alpha");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setRed(mRed);
		setGreen(mGreen);
		setBlue(mBlue);
		setAlpha(mAlpha);
	}
	
	public void setRed(float[] red) {
		mRed = red;
		setFloatVec4(mRedLocation, mRed);
	}
	
	public void setGreen(float[] green) {
		mGreen = green;
		setFloatVec4(mGreenLocation, mGreen);
	}
	
	public void setBlue(float[] blue) {
		mBlue = blue;
		setFloatVec4(mBlueLocation, mBlue);
	}
	
	public void setAlpha(float[] alpha) {
		mAlpha = alpha;
		setFloatVec4(mAlphaLocation, mAlpha);
	}
}
