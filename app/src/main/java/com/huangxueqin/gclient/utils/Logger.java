package com.huangxueqin.gclient.utils;

import android.util.Log;

import com.huangxueqin.gclient.BuildConfig;

/**
 * Created by huangxueqin on 2017/3/30.
 */

public class Logger {

    private final static boolean ENABLE_LOG = BuildConfig.DEBUG;


    public static void d(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ENABLE_LOG) {
            Log.e(tag, msg);
        }
    }
}
