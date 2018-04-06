package dauroi.photoeditor.utils;

public class SecureCache {
	private static SecureCache instance;

	public static SecureCache getInstance() {
		if (instance == null) {
			instance = new SecureCache();
		}

		return instance;
	}

	private String mKeycode;

	private SecureCache() {
		try {
			mKeycode = SecurityUtils.getSecurityCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String encodeHmacSHA256(String message) {
		return SecurityUtils.encodeHmacSHA256(mKeycode, message);
	}
}
