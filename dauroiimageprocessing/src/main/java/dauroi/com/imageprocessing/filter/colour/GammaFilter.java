package dauroi.com.imageprocessing.filter.colour;

import dauroi.com.imageprocessing.filter.ImageFilter;
import android.opengl.GLES20;

/**
 * gamma value ranges from 0.0 to 3.0, with 1.0 as the normal level
 */
public class GammaFilter extends ImageFilter {
    public static final String GAMMA_FRAGMENT_SHADER = "" +
    		"precision highp float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float gamma;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(pow(textureColor.rgb, vec3(gamma)), textureColor.w);\n" +
            " }";

    private int mGammaLocation;
    private float mGamma;

    public GammaFilter() {
        this(1.2f);
    }

    public GammaFilter(final float gamma) {
        super(NO_FILTER_VERTEX_SHADER, GAMMA_FRAGMENT_SHADER);
        mGamma = gamma;
    }

    @Override
    public void onInit() {
        super.onInit();
        mGammaLocation = GLES20.glGetUniformLocation(getProgram(), "gamma");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setGamma(mGamma);
    }

    public void setGamma(final float gamma) {
        mGamma = gamma;
        setFloat(mGammaLocation, mGamma);
    }
}
