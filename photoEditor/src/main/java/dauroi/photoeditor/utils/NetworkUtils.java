package dauroi.photoeditor.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;
import dauroi.photoeditor.api.response.UploadResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.google.gson.Gson;

public class NetworkUtils {
    private static final int PART_SIZE = 5 * 1048576;

    public static class AbortFlag {
        private boolean mAborted = false;

        public synchronized void abort() {
            mAborted = true;
        }

        public synchronized boolean isAborted() {
            return mAborted;
        }
    }

    public static UploadResponse upload(final String uploadURL, final String localPath, final String cloudPath,
                                        final AbortFlag abortionFlag) throws Exception {
        RandomAccessFile raf = null;
        String boundary = "*****"; // Just
        // generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by
        // multipart/form-data.
        HttpURLConnection urlConnection = null;

        try {
            raf = new RandomAccessFile(new File(localPath), "r");
            final long length = raf.length();
            final String contentMD5 = FileUtils.generateMD5(new FileInputStream(new File(localPath)));

            URL url = new URL(uploadURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(45000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConnection.addRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.addRequestProperty("Content-MD5", contentMD5);

            String stringForLength = "";
            stringForLength = stringForLength + ("--" + boundary) + (CRLF);
            stringForLength = stringForLength + "Content-Disposition: form-data; name=\"filedata\"; filename=\""
                    + cloudPath + "\"" + (CRLF);
            stringForLength = stringForLength + "Content-Type: " + "application/octet-stream" + CRLF;
            stringForLength = stringForLength + "Content-Transfer-Encoding: binary" + CRLF;
            stringForLength = stringForLength + CRLF;
            stringForLength = stringForLength + CRLF;
            stringForLength = stringForLength + "--" + boundary + "--" + CRLF;

            urlConnection.setFixedLengthStreamingMode((int) length + stringForLength.length());
            urlConnection.connect();
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
                // Send binary file.
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"filedata\"; filename=\"" + cloudPath + "\"")
                        .append(CRLF);
                writer.append("Content-Type: " + "application/octet-stream" + CRLF);
                // writer.append(CRLF);
                writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                writer.append(CRLF).flush();
                try {
                    long uploaded = 0;
                    byte[] buffer = new byte[1024];
                    for (int len = 0; (len = raf.read(buffer)) > 0;) {
                        if (abortionFlag != null && abortionFlag.isAborted()) {
                            throw new Exception("aborted");
                        }

                        out.write(buffer, 0, len);
                        uploaded = uploaded + len;
                    }
                    out.flush();
                } finally {
                    try {
                        raf.close();
                    } catch (IOException logOrIgnore) {
                    }
                }
                writer.append(CRLF).flush();

                writer.append("--" + boundary + "--").append(CRLF);

            } finally {
                if (writer != null)
                    writer.close();
            }

            final String result = getResponse(urlConnection);
            if (result != null) {
                Gson gson = GsonUtils.createAndroidStyleGson();
                UploadResponse resp = gson.fromJson(result, UploadResponse.class);
                return resp;
            } else {
                return null;
            }
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    /**
     * Using POST method to upload large files.
     *
     * @param raf
     * @param abortFlag
     * @param uploadURL
     * @param pathOnCloud
     * @param finalPart
     * @param md5
     * @param from
     * @param to
     * @return message from server
     */
    public static UploadResponse uploadPart(final RandomAccessFile raf, final AbortFlag abortFlag,
                                            final String uploadURL, final String pathOnCloud, boolean finalPart, String md5, long from, long to) {
        HttpURLConnection urlConnection = null;
        PrintWriter writer = null;
        String boundary = "*****";
        String CRLF = "\r\n";
        try {
            URL url = new URL(uploadURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(45000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.addRequestProperty("Content-MD5", md5);
            urlConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConnection.addRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.addRequestProperty("Range", Long.toString(from) + "-" + Long.toString(to));
            int tmpLenght = PART_SIZE + 148 + pathOnCloud.length();

            if (!finalPart)
                urlConnection.setFixedLengthStreamingMode(tmpLenght);

            urlConnection.connect();
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);

            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"filedata\"; filename=\"" + pathOnCloud + "\"")
                    .append(CRLF);

            writer.append("Content-Type: " + "application/octet-stream" + CRLF);

            // writer.append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);

            writer.append(CRLF).flush();

            int uploaded = 0;
            byte[] buffer = new byte[1024];
            int len = 0;

            raf.seek(from);

            while ((len = raf.read(buffer)) > 0 && uploaded < PART_SIZE) {
                if (abortFlag != null && abortFlag.isAborted()) {
                    throw new Exception("aborted");
                }

                out.write(buffer, 0, len);

                uploaded = uploaded + len;
            }

            out.flush();
            if (finalPart) {
                raf.close();
            }

            if (writer != null) {
                writer.append(CRLF).flush();
                writer.append("--" + boundary + "--").append(CRLF);
                writer.close();
            }

            String result = getResponse(urlConnection);
            Gson gson = GsonUtils.createAndroidStyleGson();
            UploadResponse resp = gson.fromJson(result, UploadResponse.class);
            return resp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private static String getResponse(HttpURLConnection urlConnection) throws Exception {
        int responseCode = urlConnection.getResponseCode();
        if (responseCode != 200 && responseCode != 201) {
            InputStream error = urlConnection.getErrorStream();
            if (error != null) {
                return readIn(error);
            } else {
                throw new Exception("Unknown error. Error code=" + responseCode);
            }
        } else {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String s = readIn(in);
            return s;
        }
    }

    /**
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String readIn(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 2048);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public static String encodeUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            return uri.toASCIIString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlStr;
    }
    // Check network available
    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    /**
     * @param activity
     * @param googlePlay true if Google Play, false if Amazone Store
     */
    public static void goToApp(Context activity, String packageName,
                               boolean googlePlay) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse((googlePlay ? "market://details?id="
                            : "amzn://apps/android?p=") + packageName)));
        } catch (ActivityNotFoundException e1) {
            try {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse((googlePlay ? "http://play.google.com/store/apps/details?id="
                                : "http://www.amazon.com/gp/mas/dl/android?p=")
                                + packageName)));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(activity,
                        "You don't have any app that can open this link",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
