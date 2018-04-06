package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

public class RotationFilter extends ImageFilter{
	public static final String CIRCLE_FILTER_FRAGMENT_SHADER = "" +
			"precision highp float;\n" +
			"uniform highp float angle;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     mat2 rotation = mat2( cos(angle), sin(angle),-sin(angle), cos(angle));\n" +
            "     vec2 pos = rotation *(textureCoordinate - vec2(0.5, 0.5)) + vec2(0.5, 0.5);\n" +
            "     if(pos.x < 0.0 || pos.x > 1.0 || pos.y < 0.0 || pos.y > 1.0) gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);\n else \n" +
            "     gl_FragColor = texture2D(inputImageTexture, pos);\n" +
            "}";
	
	private int mAngleLocation;
	private float mAngle;
	
	public RotationFilter(float angle){
		super(NO_FILTER_VERTEX_SHADER, CIRCLE_FILTER_FRAGMENT_SHADER);
		mAngle = angle;
	}
	
	@Override
	public void onInit() {
		super.onInit();
		mAngleLocation = GLES20.glGetUniformLocation(getProgram(),
				"angle");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setAngle(mAngle);
	}
	
	public void setAngle(float angle) {
		mAngle = angle;
		setFloat(mAngleLocation, mAngle);
	}
}
