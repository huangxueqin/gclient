package com.huangxueqin.gclient.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by huangxueqin on 2018/5/20.
 */

public class ImageUtil {

    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    /**
     * load image with fixed size
     * @param imageView
     * @param url
     * @param width request image width
     * @param height request image height
     */
    public static void loadImage(ImageView imageView, String url, int width, int height) {
        if (width > 0 && height > 0) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(RequestOptions.overrideOf(width, height))
                    .into(imageView);
        } else {
            loadImage(imageView, url);
        }
    }

}
