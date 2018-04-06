package dauroi.com.imageprocessing.filter.blend;

import dauroi.com.imageprocessing.filter.TwoInputFilter;

public class DestinationOverBlendFilter extends TwoInputFilter {
    public static final String NORMAL_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 c2 = texture2D(inputImageTexture, textureCoordinate);\n" +
            "\t lowp vec4 c1 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     lowp vec4 outputColor;\n" +
            "     \n" +
            "     outputColor.r = c2.r * c1.a;\n" +
            "\n" +
            "     outputColor.g = c2.g * c1.a;\n" +
            "     \n" +
            "     outputColor.b = c2.b * c1.a;\n" +
            "     \n" +
            "     outputColor.a = c2.a * c1.a;\n" +
            "     \n" +
            "     gl_FragColor = outputColor;\n" +
            " }";

    public DestinationOverBlendFilter() {
        super(NORMAL_BLEND_FRAGMENT_SHADER);
    }
}
