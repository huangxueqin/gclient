package com.huangxueqin.gclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by huangxueqin on 2017/2/3.
 */

public class NewsPost {
    @SerializedName("_id")
    public String id;

    public String createAt;

    public String desc;

    public String[] images;

    public String publishedAt;

    public String source;

    public String type;

    public String url;

    public String used;

    public String who;

    @Override
    public int hashCode() {
        return Arrays.hashCode(new String[] {type, id});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        NewsPost bc = (NewsPost) obj;
        return bc.type.equals(type) && bc.id.equals(id);
    }
}
