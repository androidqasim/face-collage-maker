package dauroi.com.imageprocessing.filter.blend;

/**
 * Mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
 */
public class DissolveBlendFilter extends MixBlendFilter{
    public static final String DISSOLVE_BLEND_FRAGMENT_SHADER = 
    		"precision highp float;\n" +
    		"varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform lowp float mixturePercent;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "    \n" +
            "    gl_FragColor = mix(textureColor, textureColor2, mixturePercent);\n" +
            " }";

    public DissolveBlendFilter() {
        super(DISSOLVE_BLEND_FRAGMENT_SHADER);
    }

    public DissolveBlendFilter(float mix) {
        super(DISSOLVE_BLEND_FRAGMENT_SHADER, mix);
    }
}
