package com.huangxueqin.commontitlebar;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;

public class OptionItem {

    public static int TYPE_ICON = 0;
    public static int TYPE_TEXT = 1;
    public static int TYPE_MORE = 2;

    public int type;
    @DrawableRes
    public int iconId;
    public String text;
    @StyleRes
    public int textAppearance;

    public OptionItem(int type, int iconId, String text, int textAppearance) {
        this.type = type;
        this.iconId = iconId;
        this.text = text;
        this.textAppearance = textAppearance;
    }

    public OptionItem(String text) {
        this(TYPE_TEXT, 0, text, 0);
    }

    public OptionItem(String text, int textAppearance) {
        this(TYPE_TEXT, 0, text, textAppearance);
    }

    public OptionItem(int type, int icon) {
        this(type, icon, null, 0);
    }

    public OptionItem(int icon) {
        this(TYPE_ICON, icon, null, 0);
    }
}
