package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class LightenBlendFilter extends TwoInputFilter {
    public static final String LIGHTEN_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "    \n" +
            "    gl_FragColor = max(textureColor, textureColor2);\n" +
            " }";

    public LightenBlendFilter() {
        super(LIGHTEN_BLEND_FRAGMENT_SHADER);
    }
}
