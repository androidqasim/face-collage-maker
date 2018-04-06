package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class TransformFilter extends ImageFilter {
	 public static final String TRANSFORM_FILTER_VERTEX_SHADER = "" +
			 "precision highp float;\n" +
			 	"uniform vec2 translate;\n" +
			 	"uniform vec2 scale;\n" + 
	            "attribute vec4 position;\n" +
	            "attribute vec4 inputTextureCoordinate;\n" +
	            " \n" +
	            "varying vec2 textureCoordinate;\n" +
	            " \n" +
	            "void main()\n" +
	            "{\n" +
	            "    gl_Position = vec4(position.xy * scale + translate, position.zw);\n" +
	            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
	            "}";
	
	private int mTranslateLocation;
	private float[] mTranslate;
	private int mScaleLocation;
	private float[] mScale;
	
	public TransformFilter() {
		super(TRANSFORM_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
		mTranslate = new float[]{0, 0};
		mScale = new float[]{1, 1};
	}

	@Override
	public void onInit() {
		super.onInit();
		mTranslateLocation = GLES20.glGetUniformLocation(getProgram(), "translate");
		mScaleLocation = GLES20.glGetUniformLocation(getProgram(), "scale");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setTranslate(mTranslate);
		setScale(mScale);
	}

	public void setTranslate(float[] translate) {
		mTranslate = translate;
		setFloatVec2(mTranslateLocation, mTranslate);
	}
	
	public void setScale(float[] scale) {
		mScale = scale;
		setFloatVec2(mScaleLocation, mScale);
	}
	
	public float[] getTranslate() {
		return mTranslate;
	}
	
	public float[] getScale() {
		return mScale;
	}
	
	public TransformFilter copyFilter(){
		TransformFilter filter = new TransformFilter();
		filter.setTranslate(mTranslate);
		filter.setScale(mScale);
		return filter;
	}
}
