package com.huangxueqin.gclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangxueqin on 2018/5/6.
 */

public class ZhihuDailyModel {
    public int type;
    public long id;
    public String title;
    public String[] images;
    public String image;
    public String body;
    @SerializedName("image_source")
    public String imageSource;
    @SerializedName("share_url")
    public String shareURL;
    public String[] js;
    public String[] css;
    public Recommender[] recommenders;
    public Section section;

    public static class Recommender {
        public String avatar;
    }

    public static class Section {
        public String thumbnail;
        public long id;
        public String name;
    }
}
