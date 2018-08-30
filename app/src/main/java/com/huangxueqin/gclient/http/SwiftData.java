package com.huangxueqin.gclient.http;

import com.huangxueqin.gclient.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by huangxueqin on 2017/2/11.
 */

public class SwiftData {
    private static final String TAG = "SWIFT_DATA";

    private ByteArrayOutputStream bos;

    SwiftData() {
        bos = new ByteArrayOutputStream();
    }

    SwiftData(int capacity) {
        if (capacity <= 0) {
            Logger.w(TAG, "init SwiftData with an invalid capacity, ignore it");
        }
        bos = capacity > 0 ? new ByteArrayOutputStream(capacity) : new ByteArrayOutputStream();
    }

    void write(byte[] b, int offset, int len) {
        bos.write(b, offset, len);
    }

    void write(byte[] b) {
        write(b, 0, b.length);
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        return bos.toString(charsetName);
    }

    @Override
    public String toString() {
        try {
            return toString("UTF-8");
    } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
