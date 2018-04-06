package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class FlipFilter extends ImageFilter{
	public static final String FLIP_FILTER_FRAGMENT_SHADER = ""
			+ "uniform lowp int flipType;\n"
			+ "varying highp vec2 textureCoordinate;\n"
			+ "uniform sampler2D inputImageTexture;\n"
			+ "void main()\n"
			+ "{\n"
			+ "     highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, 1.0 - textureCoordinate.y);\n"
			+ "     if(flipType != 0)textureCoordinateToUse = vec2(1.0 - textureCoordinate.x, textureCoordinate.y);\n"
			+ "     gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);\n"
			+ "}";
	
	private int mFlipType;
	private int mFlipTypeLocation;
	
	public FlipFilter(int flipType){
		super(NO_FILTER_VERTEX_SHADER, FLIP_FILTER_FRAGMENT_SHADER);
		mFlipType = flipType;
	}
	
	@Override
	public void onInit() {
		super.onInit();
		mFlipTypeLocation = GLES20.glGetUniformLocation(getProgram(),
				"flipType");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setFlipType(mFlipType);
	}
	
	public void setFlipType(int flipType) {
		mFlipType = flipType;
		setInteger(mFlipTypeLocation, mFlipType);
	}
}
