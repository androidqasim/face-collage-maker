package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class SubtractBlendFilter extends TwoInputFilter {
    public static final String SUBTRACT_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "\n" +
            "   gl_FragColor = vec4(textureColor.rgb - textureColor2.rgb, textureColor.a);\n" +
            " }";

    public SubtractBlendFilter() {
        super(SUBTRACT_BLEND_FRAGMENT_SHADER);
    }
}
