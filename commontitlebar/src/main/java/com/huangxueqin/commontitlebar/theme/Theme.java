package com.huangxueqin.commontitlebar.theme;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;

import com.huangxueqin.commontitlebar.R;

/**
 * Created by huangxueqin on 2018/4/14.
 */

public class Theme {

    public static final int DARK = 0;
    public static final int LIGHT = 1;

    public static int DARK_COLOR = Color.parseColor("#333333");
    public static int LIGHT_COLOR = Color.WHITE;

    public static @StyleRes int getStyleResByTheme(int theme) {
        int styleRes = R.style.GeneralTitle_Medium_Light;
        if (theme == DARK) {
            styleRes = R.style.GeneralTitle_Medium_Dark;
        }
        return styleRes;
    }
}
