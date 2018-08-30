package com.huangxueqin.gclient.common;

import com.huangxueqin.gclient.model.LightReadCategory;
import com.huangxueqin.gclient.model.NewsPostCategory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by huangxueqin on 2017/2/3.
 */

public final class API {

    public static final String BASE_URL = "http://gank.io";

    public static final String POST_END_POINT = BASE_URL + "/api";

    public static final String LIGHT_READ_END_POINT = BASE_URL + "/xiandu";

    public static final class Post {
        // url to get post list with date
        public static String url(int year, int month, int day) {
            return String.format(POST_END_POINT + "/history/content/day/%d/%d/%d", year, month, day);
        }

        // url to get post list with NewsPostCategory
        public static String url(NewsPostCategory type, int page, int pageSize) {
            try {
                return String.format(POST_END_POINT + "/data/%s/%d/%d", URLEncoder.encode(type.channel, "utf-8"), pageSize, page);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public final static class LightRead {
        // start with 1
        public static String url(LightReadCategory channel, int page) {
            String url = LIGHT_READ_END_POINT + "/" + channel.id;
            if (page > 1) {
                url += "/page/" + page;
            }
            return url;
        }
    }

    public static final class Image {
        /**
         * refer to https://developer.qiniu.com/dora/api/basic-processing-images-imageview2
         * @param option: 0, 1, 2, 3, 4, 5
         * @param w
         * @param h
         * @return parametered url with option and w, h specified
         */
        public static String withOption(String source, int option, int w, int h) {
            return source + "?imageView2/" + option + "/w/" + w + "/h/" + h;
        }

        public static String withInfo(String source) {
            return source + "?imageInfo";
        }
    }
}
