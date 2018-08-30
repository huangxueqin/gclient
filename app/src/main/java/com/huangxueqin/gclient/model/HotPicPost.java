package com.huangxueqin.gclient.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by huangxueqin on 2018/5/5.
 */

public class HotPicPost implements Parcelable {

    public String url;
    public int width;
    public int height;

    public HotPicPost(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    protected HotPicPost(Parcel in) {
        url = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HotPicPost> CREATOR = new Creator<HotPicPost>() {
        @Override
        public HotPicPost createFromParcel(Parcel in) {
            return new HotPicPost(in);
        }

        @Override
        public HotPicPost[] newArray(int size) {
            return new HotPicPost[size];
        }
    };
}
