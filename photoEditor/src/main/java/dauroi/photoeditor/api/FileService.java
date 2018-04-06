package dauroi.photoeditor.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import dauroi.photoeditor.api.response.UploadResponse;
import dauroi.photoeditor.utils.FileUtils;
import dauroi.photoeditor.utils.NetworkUtils;
import dauroi.photoeditor.utils.NetworkUtils.AbortFlag;
import dauroi.photoeditor.utils.SecurityUtils;
import dauroi.photoeditor.utils.SecurityUtils.Signature;
import dauroi.photoeditor.utils.Utils;

/**
 * Created by Wisekey on 4/27/2015.
 */
public class FileService extends BaseService {
	protected static final String TAG = FileService.class.getSimpleName();
	public static final String IMAGE_TYPE = "image";
	public static final String VIDEO_TYPE = "video";
	
	public static UploadResponse updateUserProfile(String sessionToken, String localPath, String cloudPath,
			AbortFlag abortFlag) throws Exception {
		String path = buildCommonPath("/file/upload", sessionToken);
		Signature signature = SecurityUtils.signSimplePath(path, "POST");
		return NetworkUtils.upload(encodeUrl(getBaseURL().concat(signature.signedPath)), localPath, cloudPath,
				abortFlag);
	}

	public static String getUploadedPath(String sessionToken, String pathOnCloud, String fileType) {
		if(pathOnCloud == null || pathOnCloud.length() < 1){
			return null;
		}

		String path = buildCommonPath("/file/download", sessionToken).concat("&filename=").concat(pathOnCloud);
		if (fileType != null && fileType.length() > 0) {
			path = path.concat("&type=").concat(fileType);
		}

		Signature signature = SecurityUtils.signSimplePath(path, "GET");
		return NetworkUtils.encodeUrl(getBaseURL().concat(signature.signedPath));
	}

	public static File downloadFile(String urlText) {
		return downloadFile(urlText, null);
	}

	public static File downloadFile(String urlText, String outName) {
		File tempFile = null, file = null;
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			String name = outName;
			if (name == null || name.length() < 1) {
				name = SecurityUtils.sha256s(urlText);
			}

			file = new File(Utils.FILE_FOLDER, name);
			if (file.exists()) {
				tempFile = new File(Utils.TEMP_FOLDER, name);
				FileUtils.copyFile(file, tempFile);
			} else {
				file.getParentFile().mkdirs();
			}
			
			URL url = new URL(urlText);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			int responseCode = urlConnection.getResponseCode();
			System.out.println("download responseCode:" + responseCode);
			if (responseCode != 200) {
				InputStream error = urlConnection.getErrorStream();
				if (error != null) {
					String errorMsg = readIn(error);
					System.out.println("download error:" + errorMsg);
					return null;
				}
			}

			final int buffLen = 2048;
			byte[] buff = new byte[buffLen];
			in = new BufferedInputStream(urlConnection.getInputStream(), buffLen);
			out = new BufferedOutputStream(new FileOutputStream(file), buffLen);
			int len = -1;
			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}

			out.flush();
			return file;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (tempFile != null) {
				FileUtils.copyFile(tempFile, file);
				tempFile.delete();
			}
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}

		return null;
	}

	public static File downloadFile(String sessionToken, String cloudPath, String fileType) {
		try {
			String name = SecurityUtils.sha256s(cloudPath);
			// download file
			String urlText = getUploadedPath(sessionToken, cloudPath, fileType);
			return downloadFile(urlText, name);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
