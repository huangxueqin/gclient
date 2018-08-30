package com.huangxueqin.gclient.model;

/**
 * Created by huangxueqin on 2018/5/7.
 */

public enum NewsPostCategory {

    Android("Android"),
    iOS("iOS"),
    FrontEnd("前端"),
    APP("App"),
    ExtendRes("拓展资源"),
    Video("休息视频"),
    Welfare("福利"),
    Other("瞎推荐");

    public final String channel;

    NewsPostCategory(String name) {
        this.channel = name;
    }
}
