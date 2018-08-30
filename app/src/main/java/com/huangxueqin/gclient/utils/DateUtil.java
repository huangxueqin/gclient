package com.huangxueqin.gclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangxueqin on 2017/2/16.
 */

public class DateUtil {
    private static final String TAG = "DateUtil";

    public static SimpleDateFormat sourceFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.CHINA);
    public static SimpleDateFormat simpleFormatterWithYear = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat simpleFormatterWithoutYear = new SimpleDateFormat("MM月dd日");

    public static Date getDate(Date date, int before) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DAY_OF_YEAR, -before);
        return ca.getTime();
    }

    public static String parseReadableDate(String dateStr) {
        try {
            Date date = sourceFormatter.parse(dateStr);
            Date current = new Date();

            final long diffTime = current.getTime() - date.getTime();

            if (diffTime > 0) {
                if (diffTime < 1000 * 60 * 60) { // less than 1 hour
                    final long minutes = diffTime / (1000*60);
                    return "" + minutes + "分钟前";
                } else if (diffTime < 1000 * 60 * 60 * 24) { // less than 1 day
                    final long hours = diffTime / (1000*60*60);
                    return "" + hours + "小时前";
                } else {
                    return simpleFormatterWithoutYear.format(date);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
