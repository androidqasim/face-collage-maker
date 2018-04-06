package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class SourceOverBlendFilter extends TwoInputFilter {
    public static final String SOURCE_OVER_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate);\n" +
            "   \n" +
            "   gl_FragColor = mix(textureColor, textureColor2, textureColor2.a);\n" +
            " }";

    public SourceOverBlendFilter() {
        super(SOURCE_OVER_BLEND_FRAGMENT_SHADER);
    }
}
