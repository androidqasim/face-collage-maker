package dauroi.com.imageprocessing.filter.colour;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import dauroi.com.imageprocessing.Rotation;
import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.com.imageprocessing.util.OpenGlUtils;
import dauroi.com.imageprocessing.util.TextureRotationUtil;

public class TwoInputFilter extends ImageFilter {
    private static final String VERTEX_SHADER = 
    		"precision highp float;\n" +
    		"attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "}";

    public int filterSecondTextureCoordinateAttribute;
    public int filterInputTextureUniform2;
    public int filterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    private Bitmap mBitmap;
    private boolean mRecycleBitmap = true;
    
    public TwoInputFilter(String fragmentShader) {
        this(VERTEX_SHADER, fragmentShader);
    }

    public TwoInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        filterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        filterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);

        if (mBitmap != null && !mBitmap.isRecycled()) {
            setBitmap(mBitmap);
        }
    }

    /**
     * This method doesn't recycle bitmap but when filter is destroyed then the bitmap is recycled.
     * @param bitmap
     */
    public void setBitmap(final Bitmap bitmap) {
        mBitmap = bitmap;
        runOnDraw(new Runnable() {
            public void run() {
                if (filterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    filterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{
                filterSourceTexture2
        }, 0);
        filterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
        //recycle bitmap to reclaim memory
        if(mBitmap != null && !mBitmap.isRecycled() && mRecycleBitmap){
        	mBitmap.recycle();
        	mBitmap = null;
        }
    }

    @Override
    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterSourceTexture2);
        GLES20.glUniform1i(filterInputTextureUniform2, 3);

        mTexture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(filterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture2CoordinatesBuffer = bBuffer;
    }
    
    public void setRecycleBitmap(boolean recycleBitmap) {
		mRecycleBitmap = recycleBitmap;
	}
    
    public boolean isRecycleBitmap() {
		return mRecycleBitmap;
	}
    /**
     * Should check the bitmap be recycled or not.
     * Only use this method when already set recycleBitmap false.
     * @return second bitmap which is set from setBitmap method.
     */
    public Bitmap getBitmap() {
		return mBitmap;
	}
    
    public void recycleBitmap(){
    	if(mBitmap != null && !mBitmap.isRecycled()){
    		mBitmap.recycle();
    		setBitmap(null);
    	}
    }
}
