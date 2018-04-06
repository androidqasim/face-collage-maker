package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class ScreenBlendFilter extends TwoInputFilter {
    public static final String SCREEN_BLEND_FRAGMENT_SHADER = 
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
            "     mediump vec4 whiteColor = vec4(1.0);\n" +
            "     gl_FragColor = whiteColor - ((whiteColor - textureColor2) * (whiteColor - textureColor));\n" +
            " }";

    public ScreenBlendFilter() {
        super(SCREEN_BLEND_FRAGMENT_SHADER);
    }
}
