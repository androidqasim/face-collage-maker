package dauroi.com.imageprocessing.filter.colour;

import dauroi.com.imageprocessing.filter.ImageFilter;
import android.opengl.GLES20;

/**
 * Adjusts the alpha channel of the incoming image
 * opacity: The value to multiply the incoming alpha channel for each pixel by (0.0 - 1.0, with 1.0 as the default)
*/
public class OpacityFilter extends ImageFilter {
    public static final String OPACITY_FRAGMENT_SHADER = "" +
    		"precision highp float;\n" +
            "  varying highp vec2 textureCoordinate;\n" +
            "  \n" +
            "  uniform sampler2D inputImageTexture;\n" +
            "  uniform lowp float opacity;\n" +
            "  \n" +
            "  void main()\n" +
            "  {\n" +
            "      lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.rgb, textureColor.a * opacity);\n" +
            "  }\n";

    private int mOpacityLocation;
    private float mOpacity;

    public OpacityFilter() {
        this(1.0f);
    }

    public OpacityFilter(final float opacity) {
        super(NO_FILTER_VERTEX_SHADER, OPACITY_FRAGMENT_SHADER);
        mOpacity = opacity;
    }

    @Override
    public void onInit() {
        super.onInit();
        mOpacityLocation = GLES20.glGetUniformLocation(getProgram(), "opacity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setOpacity(mOpacity);
    }

    public void setOpacity(final float opacity) {
        mOpacity = opacity;
        setFloat(mOpacityLocation, mOpacity);
    }
}
