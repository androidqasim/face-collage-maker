package dauroi.photoeditor.utils;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
	/**
	 * 
	 * @return custom gson which handles normal style android.
	 */
	public static Gson createAndroidStyleGson() {
		// mapping from json to java object
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting().serializeNulls();
		builder.setFieldNamingStrategy(new FieldNamingStrategy() {

			@Override
			public String translateName(Field f) {
				return toLowerCaseWithUnderscores(f.getName(), true);
			}
		});

		Gson gson = builder.create();
		return gson;
	}

	private static String toLowerCaseWithUnderscores(String fieldName,
			boolean ignorePrefix) {
		if (fieldName == null || fieldName.length() < 1) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		if (!ignorePrefix) {
			sb.append(Character.toLowerCase(fieldName.charAt(0)));
		}

		final int len = fieldName.length();
		for (int idx = 1; idx < len; idx++) {
			char c = fieldName.charAt(idx);
			if (c >= 'A' && c <= 'Z') {
				if (idx > 1 || !ignorePrefix) {
					sb.append("_");
				}
			}

			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();
	}
}
