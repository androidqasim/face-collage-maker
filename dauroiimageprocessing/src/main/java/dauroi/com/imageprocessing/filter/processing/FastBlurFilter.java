package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class FastBlurFilter extends ImageFilter{
	private static final String FAST_BLUR_FRAGMENT_SHADER =  "precision mediump float;\n" 
			+"uniform sampler2D inputImageTexture;\n"  
			+"varying vec2 textureCoordinate;\n"	
			+"uniform float texelWidth;\n"
			+"uniform float texelHeight;\n"
	  		+"void main(){\n"
	  		+"   vec2 firstOffset = vec2(1.3846153846 * texelWidth, 1.3846153846 * texelHeight);\n"
	  		+"   vec2 secondOffset = vec2(3.2307692308 * texelWidth, 3.2307692308 * texelHeight);\n"
			+"   vec3 sum = vec3(0,0,0);\n"
	  		+"   vec4 color = texture2D(inputImageTexture, textureCoordinate);\n"
			+"   sum += color.rgb * 0.2270270270;\n"
			+"   sum += texture2D(inputImageTexture, textureCoordinate - firstOffset).rgb * 0.3162162162;\n"
			+"   sum += texture2D(inputImageTexture, textureCoordinate + firstOffset).rgb * 0.3162162162;\n"
			+"   sum += texture2D(inputImageTexture, textureCoordinate - secondOffset).rgb * 0.0702702703;\n"
			+"   sum += texture2D(inputImageTexture, textureCoordinate + secondOffset).rgb * 0.0702702703;\n"
	  		+"   gl_FragColor = vec4(sum, color.a);\n"
	  		+"}\n";
	
	private int mTexelWidthLocation;
	private float mTexelWidth;
	private int mTexelHeightLocation;
	private float mTexelHeight;
	
	public FastBlurFilter(float texelWidth, float texelHeight) {
		super(NO_FILTER_VERTEX_SHADER, FAST_BLUR_FRAGMENT_SHADER);
		mTexelWidth = texelWidth;
		mTexelHeight = texelHeight;
	}
	
	@Override
	public void onInit() {
		super.onInit();
		mTexelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
		mTexelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setTexelWidth(mTexelWidth);
		setTexelHeight(mTexelHeight);
	}
	
	public void setTexelWidth(float texelWidth) {
		mTexelWidth = texelWidth;
		setFloat(mTexelWidthLocation, mTexelWidth);
	}
	
	public void setTexelHeight(float texelHeight) {
		mTexelHeight = texelHeight;
		setFloat(mTexelHeightLocation, mTexelHeight);
	}
	
}
