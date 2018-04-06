package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class LinearBurnBlendFilter extends TwoInputFilter {
    public static final String LINEAR_BURN_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     gl_FragColor = vec4(clamp(textureColor.rgb + textureColor2.rgb - vec3(1.0), vec3(0.0), vec3(1.0)), textureColor.a);\n" +
            " }";

    public LinearBurnBlendFilter() {
        super(LINEAR_BURN_BLEND_FRAGMENT_SHADER);
    }
}
