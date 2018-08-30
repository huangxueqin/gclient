package com.huangxueqin.gclient.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by huangxueqin on 2018/5/5.
 */

public class Md5Util {

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : bytes) {
                String s = Integer.toHexString(0xff & b);
                if (s.length() == 1) {
                    sb.append("0");
                }
                sb.append(s);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
