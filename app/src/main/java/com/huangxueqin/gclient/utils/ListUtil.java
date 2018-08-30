package com.huangxueqin.gclient.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by huangxueqin on 2018/5/1.
 */

public class ListUtil {

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }

    public static <T> int sizeOf(List<T> list) {
        return list == null ? 0 : list.size();
    }

    public static <T> T firstOf(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    public static <T> T lastOf(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        if (list instanceof LinkedList) {
            LinkedList<T> linkedList = (LinkedList<T>) list;
            return linkedList.getLast();
        } else {
            int lastIndex = list.size()-1;
            return list.get(lastIndex);
        }
    }

    public static <T> List<T> asList(T... args) {
        if (ArrayUtil.isEmpty(args)) {
            return null;
        } else {
            ArrayList<T> list = new ArrayList<>();
            for (T t : args) {
                list.add(t);
            }
            return list;
        }
    }
}
