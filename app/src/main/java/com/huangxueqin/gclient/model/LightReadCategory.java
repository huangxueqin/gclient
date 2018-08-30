package com.huangxueqin.gclient.model;

/**
 * Created by huangxueqin on 2018/5/7.
 */

public enum LightReadCategory {

    WOW("wow", "科技资讯"),
    APPS("apps", "趣味软件／游戏"),
    IM_RICH("imrich", "装备党"),
    FUNNY("funny", "草根新闻"),
    ANDROID("android", "Android"),
    DIE_DIE_DIE("diediedie", "创业新闻"),
    THINKING("thinking", "独立思想"),
    IOS("ios", "iOS"),
    TEAM_BLOG("teamblog", "团队博客");


    public String id;
    public String title;

    LightReadCategory(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
