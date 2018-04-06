package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class SoftLightBlendFilter extends TwoInputFilter {
    public static final String SOFT_LIGHT_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     gl_FragColor = base * (overlay.a * (base / base.a) + (2.0 * overlay * (1.0 - (base / base.a)))) + overlay * (1.0 - base.a) + base * (1.0 - overlay.a);\n" +
            " }";

    public SoftLightBlendFilter() {
        super(SOFT_LIGHT_BLEND_FRAGMENT_SHADER);
    }
}
