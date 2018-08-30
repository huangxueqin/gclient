package com.huangxueqin.gclient.common;

/**
 * Created by huangxueqin on 2017/6/17.
 */

public class Const {
    public interface IntentKey {
        String URL = "content-url";
        String HOT_PIC_LIST = "hot_pic_list";
        String INIT_POSITION = "init_position";
        String NEWS_SOURCE_TYPE = "news_source_type";
        String NEWS_CHANNEL = "news_channel";
    }

    public interface PageEvent {
        int OPEN_MAIN_DRAWER = 0;
        int CLOSE_MAIN_DRAWER = 1;
        int PAGE_ATTACH = 2;
        int PAGE_DETACH = 3;
        int OPEN_BROWSER = 4;
        int HOT_PIC_PREVIEW = 5;
    }
}
