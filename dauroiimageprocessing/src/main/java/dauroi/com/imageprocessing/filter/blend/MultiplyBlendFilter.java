package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class MultiplyBlendFilter extends TwoInputFilter {
    public static final String MULTIPLY_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp vec4 overlayer = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "          \n" +
            "     gl_FragColor = overlayer * base + overlayer * (1.0 - base.a) + base * (1.0 - overlayer.a);\n" +
            " }";

    public MultiplyBlendFilter() {
        super(MULTIPLY_BLEND_FRAGMENT_SHADER);
    }
}
