package dauroi.com.imageprocessing.util;

public class ImageUtils {
	static {
		System.loadLibrary("yuv-decoder");
	}

	public static native void YUVtoRBGA(byte[] yuv, int width, int height,
			int[] out);

	public static native void YUVtoARBG(byte[] yuv, int width, int height,
			int[] out);
}
