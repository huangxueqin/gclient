package com.huangxueqin.gclient.model;

/**
 * Created by huangxueqin on 2017/8/7.
 */

public class LightReadPost {

    public String title;
    public String url;
    public String time;
    public String sourceTitle;
    public String sourceImage;

    @Override
    public String toString() {
        return "title = " + title +
                ", url = " + url +
                ", time = " + time +
                ", sourceTitle = " + sourceTitle +
                ", sourceImage = " + sourceImage;
    }
}
