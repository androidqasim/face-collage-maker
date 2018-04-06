package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class CropFilter extends ImageFilter {
	public static final String CROP_FILTER_VERTEX_SHADER = "" +
			"precision highp float;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform float left;\n" +
            "uniform float top;\n" +
            "uniform float right;\n" +
            "uniform float bottom;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            " 	 float x = inputTextureCoordinate.x;\n" +
            " 	 float y = inputTextureCoordinate.y;\n" +
            "    if(x < left) x = left;\n" +
            "    if(x > right) x = right;\n" +
            "    if(y < top) y = top;\n" + 
            "    if(y > bottom) y = bottom;\n" +
            "    textureCoordinate = vec2(x,y);\n" +
            "}";
	
	private int mLeftLocation;
	private float mLeft;
	private int mTopLocation;
	private float mTop;
	private int mRightLocation;
	private float mRight;
	private int mBottomLocation;
	private float mBottom;

	public CropFilter(float left, float top, float right, float bottom) {
		super(CROP_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
		mLeft = left;
		mTop = top;
		mRight = right;
		mBottom = bottom;
	}

	@Override
	public void onInit() {
		super.onInit();
		mLeftLocation = GLES20.glGetUniformLocation(getProgram(), "left");
		mTopLocation = GLES20.glGetUniformLocation(getProgram(), "top");
		mRightLocation = GLES20.glGetUniformLocation(getProgram(), "right");
		mBottomLocation = GLES20.glGetUniformLocation(getProgram(), "bottom");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setLeft(mLeft);
		setTop(mTop);
		setRight(mRight);
		setBottom(mBottom);
	}

	public void setLeft(final float left) {
		mLeft = left;
		setFloat(mLeftLocation, mLeft);
	}
	
	public void setRight(float right) {
		mRight = right;
		setFloat(mRightLocation, mRight);
	}
	
	public void setTop(float top) {
		mTop = top;
		setFloat(mTopLocation, mTop);
	}
	
	public void setBottom(float bottom) {
		mBottom = bottom;
		setFloat(mBottomLocation, mBottom);
	}
}
