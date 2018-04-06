package dauroi.com.imageprocessing.filter.effect;

import dauroi.com.imageprocessing.filter.ImageFilter;
import android.opengl.GLES20;

/**
 * exposure: The adjusted exposure (-10.0 - 10.0, with 0.0 as the default)
 */
public class ExposureFilter extends ImageFilter {
    public static final String EXPOSURE_FRAGMENT_SHADER = "" +
    		"precision highp float;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform highp float exposure;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
            " } ";

    private int mExposureLocation;
    private float mExposure;

    public ExposureFilter() {
        this(1.0f);
    }

    public ExposureFilter(final float exposure) {
        super(NO_FILTER_VERTEX_SHADER, EXPOSURE_FRAGMENT_SHADER);
        mExposure = exposure;
    }

    @Override
    public void onInit() {
        super.onInit();
        mExposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setExposure(mExposure);
    }

    public void setExposure(final float exposure) {
        mExposure = exposure;
        setFloat(mExposureLocation, mExposure);
    }
}
