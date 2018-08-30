package com.huangxueqin.gclient.utils;

/**
 * Created by huangxueqin on 2018/5/1.
 */

public class ArrayUtil {

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(int[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isNotEmpty(float[] array) {
        return array != null && array.length > 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return array != null && array.length > 0;
    }

    public static int sizeOf(int[] array) {
        return array == null ? 0 : array.length;
    }

    public static int sizeOf(float[] array) {
        return array == null ? 0 : array.length;
    }

    public static <T> int sizeOf(T[] array) {
        return array == null ? 0 : array.length;
    }

    public static int[] asIntArray(int... args) {
        return args;
    }

    public static float[] asFloatArray(float... args) {
        return args;
    }

    public static <T> T[] asArray(T... args) {
        return args;
    }
}
