package dauroi.com.imageprocessing.filter.colour;

import dauroi.com.imageprocessing.filter.ImageFilter;

/**
 * Invert all the colors in the image.
 */
public class ColorInvertFilter extends ImageFilter {
    public static final String COLOR_INVERT_FRAGMENT_SHADER = "" +
    		"precision highp float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    \n" +
            "    gl_FragColor = vec4((1.0 - textureColor.rgb), textureColor.w);\n" +
            "}";

    public ColorInvertFilter() {
        super(NO_FILTER_VERTEX_SHADER, COLOR_INVERT_FRAGMENT_SHADER);
    }
}
