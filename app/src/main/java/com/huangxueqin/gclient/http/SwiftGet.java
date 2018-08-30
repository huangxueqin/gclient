package com.huangxueqin.gclient.http;

import com.huangxueqin.gclient.utils.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangxueqin on 2017/2/8.
 */

public class SwiftGet {
    private static final String TAG = "SwiftGet";
    private static final int CONNECT_TIMEOUT = 25000;
    private static final int READ_TIMEOUT = 25000;

    private String url;
    private HashMap<String, String> headers;

    public SwiftGet(String url) {
        this.url = url;
        this.headers = new HashMap<>();
        Logger.d(TAG, "swift get start");
    }

    public void addRequestProperty(String key, String value) {
        headers.put(key, value);
    }

    public void addRequestProperty(Map<String, String> properties) {
        headers.putAll(properties);
    }

    private SwiftGet handleRedirection(HttpURLConnection conn) throws IOException {
        final URL base = new URL(url);
        final String location = conn.getHeaderField("Location");
        final String redirectedURL = new URL(base, location).toExternalForm();
        return new SwiftGet(redirectedURL);
    }

    private HttpURLConnection buildConnection() throws IOException {
        final URL base = new URL(url);
        Logger.d("TAG", url);
        HttpURLConnection conn = (HttpURLConnection) base.openConnection();
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.addRequestProperty(entry.getKey(), entry.getValue());
        }
        return conn;
    }

    public InputStream getInputStream() throws IOException {
        HttpURLConnection conn = buildConnection();
        conn.connect();

        final int responseCode = conn.getResponseCode();
        if (responseCode / 100 == 2) {
            return conn.getInputStream();
        }
        if (responseCode/100 == 3) {
            return handleRedirection(conn).getInputStream();
        }
        throw new IOException("response error, HTTP code: " + responseCode);
    }

    public SwiftData getData() throws IOException {
        HttpURLConnection conn = buildConnection();
        conn.connect();

        final int responseCode = conn.getResponseCode();
        if (responseCode / 100 == 2) {
            SwiftData data = new SwiftData(conn.getContentLength());
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = bis.read(buf)) != -1) {
                data.write(buf, 0, len);
            }
            bis.close();
            return data;
        }
        if (responseCode/100 == 3) {
            return handleRedirection(conn).getData();
        }
        throw new IOException("response error, HTTP code: " + responseCode);
    }
}
