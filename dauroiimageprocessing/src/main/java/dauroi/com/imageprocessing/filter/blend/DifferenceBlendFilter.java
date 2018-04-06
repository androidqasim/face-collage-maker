package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class DifferenceBlendFilter extends TwoInputFilter {
    public static final String DIFFERENCE_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     gl_FragColor = vec4(abs(textureColor2.rgb - textureColor.rgb), textureColor.a);\n" +
            " }";

    public DifferenceBlendFilter() {
        super(DIFFERENCE_BLEND_FRAGMENT_SHADER);
    }
}
