package dauroi.com.imageprocessing.filter.processing;

import android.opengl.GLES20;
import dauroi.com.imageprocessing.filter.ImageFilter;

/**
 * 
	 GLSL textureless classic 3D noise "cnoise",
	 with an RSL-style periodic variant "pnoise".
	 Author:  Stefan Gustavson (stefan.gustavson@liu.se)
	 Version: 2011-10-11
	
	 Many thanks to Ian McEwan of Ashima Arts for the
	 ideas for permutation and gradient selection.
	
	 Copyright (c) 2011 Stefan Gustavson. All rights reserved.
	 Distributed under the MIT license. See LICENSE file.
	 https://github.com/ashima/webgl-noise
	
 * @author vanhu_000
 *
 */
public class ClassicNoise3dFilter extends ImageFilter{
	private static final String CLASSIC_NOISE_3D_FRAGMENT_SHADER = 
	"#ifdef GL_ES\n" +
	"precision mediump float;\n" +
	"#endif\n" +
	
	"varying highp vec2 textureCoordinate;\n" +
	"uniform sampler2D inputImageTexture;\n" +
	"uniform float time;\n" +
	"uniform vec2 resolution;\n" +

	

	"vec3 mod289(vec3 x)\n" +
	"{\n" +
	  "return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +
	"}\n" +

	"vec4 mod289(vec4 x)\n" +
	"{\n" +
	  "return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +
	"}\n" +

	"vec4 permute(vec4 x)\n" +
	"{\n" +
	  "return mod289(((x*34.0)+1.0)*x);\n" +
	"}\n" +

	"vec4 taylorInvSqrt(vec4 r)\n" +
	"{\n" +
	  "return 1.79284291400159 - 0.85373472095314 * r;\n" +
	"}\n" +

	"vec3 fade(vec3 t) {\n" +
	  "return t*t*t*(t*(t*6.0-15.0)+10.0);\n" +
	"}\n" +

	// Classic Perlin noise
	"float cnoise(vec3 P)\n" +
	"{\n" +
	  "vec3 Pi0 = floor(P);\n" + // Integer part for indexing
	  "vec3 Pi1 = Pi0 + vec3(1.0);\n" + // Integer part + 1
	  "Pi0 = mod289(Pi0);\n" +
	  "Pi1 = mod289(Pi1);\n" +
	  "vec3 Pf0 = fract(P);\n" + // Fractional part for interpolation
	  "vec3 Pf1 = Pf0 - vec3(1.0);\n" + // Fractional part - 1.0
	  "vec4 ix = vec4(Pi0.x, Pi1.x, Pi0.x, Pi1.x);\n" +
	  "vec4 iy = vec4(Pi0.yy, Pi1.yy);\n" +
	  "vec4 iz0 = Pi0.zzzz;\n" +
	  "vec4 iz1 = Pi1.zzzz;\n" +

	  "vec4 ixy = permute(permute(ix) + iy);\n" +
	  "vec4 ixy0 = permute(ixy + iz0);\n" +
	  "vec4 ixy1 = permute(ixy + iz1);\n" +

	  "vec4 gx0 = ixy0 * (1.0 / 7.0);\n" +
	  "vec4 gy0 = fract(floor(gx0) * (1.0 / 7.0)) - 0.5;\n" +
	  "gx0 = fract(gx0);\n" +
	  "vec4 gz0 = vec4(0.5) - abs(gx0) - abs(gy0);\n" +
	  "vec4 sz0 = step(gz0, vec4(0.0));\n" +
	  "gx0 -= sz0 * (step(0.0, gx0) - 0.5);\n" +
	  "gy0 -= sz0 * (step(0.0, gy0) - 0.5);\n" +

	  "vec4 gx1 = ixy1 * (1.0 / 7.0);\n" +
	  "vec4 gy1 = fract(floor(gx1) * (1.0 / 7.0)) - 0.5;\n" +
	  "gx1 = fract(gx1);\n" +
	  "vec4 gz1 = vec4(0.5) - abs(gx1) - abs(gy1);\n" +
	  "vec4 sz1 = step(gz1, vec4(0.0));\n" +
	  "gx1 -= sz1 * (step(0.0, gx1) - 0.5);\n" +
	  "gy1 -= sz1 * (step(0.0, gy1) - 0.5);\n" +

	  "vec3 g000 = vec3(gx0.x,gy0.x,gz0.x);\n" +
	  "vec3 g100 = vec3(gx0.y,gy0.y,gz0.y);\n" +
	  "vec3 g010 = vec3(gx0.z,gy0.z,gz0.z);\n" +
	  "vec3 g110 = vec3(gx0.w,gy0.w,gz0.w);\n" +
	  "vec3 g001 = vec3(gx1.x,gy1.x,gz1.x);\n" +
	  "vec3 g101 = vec3(gx1.y,gy1.y,gz1.y);\n" +
	  "vec3 g011 = vec3(gx1.z,gy1.z,gz1.z);\n" +
	  "vec3 g111 = vec3(gx1.w,gy1.w,gz1.w);\n" +

	  "vec4 norm0 = taylorInvSqrt(vec4(dot(g000, g000), dot(g010, g010), dot(g100, g100), dot(g110, g110)));\n" +
	  "g000 *= norm0.x;\n" +
	  "g010 *= norm0.y;\n" +
	  "g100 *= norm0.z;\n" +
	  "g110 *= norm0.w;\n" +
	  "vec4 norm1 = taylorInvSqrt(vec4(dot(g001, g001), dot(g011, g011), dot(g101, g101), dot(g111, g111)));\n" +
	  "g001 *= norm1.x;\n" +
	  "g011 *= norm1.y;\n" +
	  "g101 *= norm1.z;\n" +
	  "g111 *= norm1.w;\n" +

	  "float n000 = dot(g000, Pf0);\n" +
	  "float n100 = dot(g100, vec3(Pf1.x, Pf0.yz));\n" +
	  "float n010 = dot(g010, vec3(Pf0.x, Pf1.y, Pf0.z));\n" +
	  "float n110 = dot(g110, vec3(Pf1.xy, Pf0.z));\n" +
	  "float n001 = dot(g001, vec3(Pf0.xy, Pf1.z));\n" +
	  "float n101 = dot(g101, vec3(Pf1.x, Pf0.y, Pf1.z));\n" +
	  "float n011 = dot(g011, vec3(Pf0.x, Pf1.yz));\n" +
	  "float n111 = dot(g111, Pf1);\n" +

	  "vec3 fade_xyz = fade(Pf0);\n" +
	  "vec4 n_z = mix(vec4(n000, n100, n010, n110), vec4(n001, n101, n011, n111), fade_xyz.z);\n" +
	  "vec2 n_yz = mix(n_z.xy, n_z.zw, fade_xyz.y);\n" +
	  "float n_xyz = mix(n_yz.x, n_yz.y, fade_xyz.x);\n" +
	  "return 2.2 * n_xyz;\n" +
	"}\n" +

	"float surface3 ( vec3 coord ) {\n" +
		
		"float frequency = 4.0;\n" +
		"float n = 0.0;\n" +
			
		"n += 1.0	* abs( cnoise( coord * frequency ) );\n" +
		"n += 0.5	* abs( cnoise( coord * frequency * 2.0 ) );\n" +
		"n += 0.25	* abs( cnoise( coord * frequency * 4.0 ) );\n" +
		
		"return n;\n" +
	"}\n" +
		
	"void main( void ) {\n" +
		
		"vec2 position = gl_FragCoord.xy / resolution.xy;\n" +
		"vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
		"float gray = surface3(vec3(position, (color.r + color.g + color.b) / 3.0));\n" +
		
		"gl_FragColor = vec4(gray, gray, gray, color.a);\n" +
	"}\n";
	
	private int mResolutionLocation;
	private float[] mResolution;
	private float mTime;
	private int mTimeLocation;
	
	public ClassicNoise3dFilter(float[] resolution){
		super(NO_FILTER_VERTEX_SHADER, CLASSIC_NOISE_3D_FRAGMENT_SHADER);
		mResolution = resolution;
		mTime = 1.0f;
	}
	
	@Override
	public void onInit() {
		super.onInit();
		mResolutionLocation = GLES20.glGetUniformLocation(getProgram(), "resolution");
		mTimeLocation = GLES20.glGetUniformLocation(getProgram(), "time");
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		setResolution(mResolution);
		setTime(mTime);
	}
	
	public void setResolution(float[] resolution) {
		mResolution = resolution;
		setFloatVec2(mResolutionLocation, mResolution);
	}
	
	public void setTime(float time) {
		mTime = time;
		setFloat(mTimeLocation, mTime);
	}
}
