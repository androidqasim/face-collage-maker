package dauroi.photoeditor.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import dauroi.photoeditor.config.DebugOptions;
import dauroi.photoeditor.utils.NetworkUtils;

public class BaseService {
    public static final String PURE_DOMAIN_NAME = "codetho.com";
    public static String getBaseURL() {
        if (DebugOptions.ENABLE_FOR_DEV) {
            return "http://dev.".concat(PURE_DOMAIN_NAME);
        } else {
            return "https://".concat(PURE_DOMAIN_NAME);
        }
    }

    public static String getAppId() {
        if (DebugOptions.ENABLE_FOR_DEV) {
            return "51ccc00c85dcf4500e46886405ced1be";
        } else {
            return "a47706dd5f24dc5693b894b2088ac6de";
        }
    }

    public static String requestPath(String path, String method, String content) throws Exception {
        final String urlStr = getBaseURL() + path;
        return request(urlStr, method, content);
    }

    public static String request(String urlText, String method, String content) throws Exception {
        URL url = new URL(encodeUrl(urlText));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (content != null && content.length() > 0) {
            urlConnection.setDoOutput(true);
        }
        urlConnection.setReadTimeout(30000 /* milliseconds */);
        urlConnection.setConnectTimeout(45000 /* milliseconds */);
        urlConnection.setRequestMethod(method.toUpperCase(Locale.US));

        urlConnection.setDoInput(true);
        urlConnection.addRequestProperty("Content-Type", "application/json");
        urlConnection.addRequestProperty("Accept-Charset", "UTF-8");
        urlConnection.connect();

        if (content != null && content.length() > 0) {
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
            writer.print(content);
            writer.flush();
            writer.close();
        }

        int responseCode = urlConnection.getResponseCode();
        if (responseCode != 201 || responseCode != 200) {
            InputStream error = urlConnection.getErrorStream();
            if (error != null) {
                String errorMsg = readIn(error);
                System.out.println("BaseService.request, errorMsg=" + errorMsg);
                throw new Exception(errorMsg);
            }
        }

        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String s = readIn(in);
        return s;
    }

    protected static String buildCommonPath(String path, String sessionToken) {
        String temp = path.concat("?appId=").concat(getAppId());
        if (sessionToken != null && sessionToken.length() > 0) {
            temp = temp.concat("&sessionToken=").concat(sessionToken);
        }

        return temp;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    protected static String readIn(InputStream in) throws IOException {
        return NetworkUtils.readIn(in);
    }

    protected static String encodeUrl(String urlStr) {
        return NetworkUtils.encodeUrl(urlStr);
    }
}
