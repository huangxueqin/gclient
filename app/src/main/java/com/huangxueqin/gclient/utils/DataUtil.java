package com.huangxueqin.gclient.utils;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class DataUtil {

    // bundle utils
    public static int getInt(Bundle bundle, String key, int dv) {
        if (bundle == null) {
            return dv;
        } else {
            return bundle.getInt(key, dv);
        }
    }

    @Nullable
    public static int[] getIntArray(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getIntArray(key);
        }
    }

    @Nullable
    public static ArrayList<Integer> getIntegerArrayList(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getIntegerArrayList(key);
        }
    }

    public static float getFloat(Bundle bundle, String key, float dv) {
        if (bundle == null) {
            return dv;
        } else {
            return bundle.getFloat(key, dv);
        }
    }

    @Nullable
    public static float[] getFloatArray(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getFloatArray(key);
        }
    }

    public static String getString(Bundle bundle, String key, String dv) {
        if (bundle == null) {
            return dv;
        } else {
            return bundle.getString(key, dv);
        }
    }

    @Nullable
    public static String[] getStringArray(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getStringArray(key);
        }
    }

    @Nullable
    public static ArrayList<String> getStringArrayList(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getStringArrayList(key);
        }
    }

    @Nullable
    public static Parcelable getParcelable(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getParcelable(key);
        }
    }

    @Nullable
    public static Parcelable[] getParcelableArray(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getParcelableArray(key);
        }
    }

    @Nullable
    public static ArrayList<Parcelable> getParcelableArrayList(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        } else {
            return bundle.getParcelableArrayList(key);
        }
    }
}
