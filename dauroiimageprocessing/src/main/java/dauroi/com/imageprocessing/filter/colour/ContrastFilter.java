package dauroi.com.imageprocessing.filter.colour;

import dauroi.com.imageprocessing.filter.ImageFilter;
import android.opengl.GLES20;

/**
 * Changes the contrast of the image.<br />
 * <br />
 * contrast value ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
public class ContrastFilter extends ImageFilter {
    public static final String CONTRAST_FRAGMENT_SHADER = "" +
    		"precision highp float;\n" +
            "varying highp vec2 textureCoordinate;\n" + 
            " \n" + 
            " uniform sampler2D inputImageTexture;\n" + 
            " uniform lowp float contrast;\n" + 
            " \n" + 
            " void main()\n" + 
            " {\n" + 
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" + 
            "     \n" + 
            "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" + 
            " }";

    private int mContrastLocation;
    private float mContrast;

    public ContrastFilter() {
        this(1.2f);
    }
    
    public ContrastFilter(float contrast) {
        super(NO_FILTER_VERTEX_SHADER, CONTRAST_FRAGMENT_SHADER);
        mContrast = contrast;
    }

    @Override
    public void onInit() {
        super.onInit();
        mContrastLocation = GLES20.glGetUniformLocation(getProgram(), "contrast");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setContrast(mContrast);
    }

    public void setContrast(final float contrast) {
        mContrast = contrast;
        setFloat(mContrastLocation, mContrast);
    }
}
