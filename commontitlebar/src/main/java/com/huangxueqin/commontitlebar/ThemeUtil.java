package com.huangxueqin.commontitlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.huangxueqin.commontitlebar.theme.AppearanceConf;
import com.huangxueqin.commontitlebar.theme.Theme;

/**
 * Created by huangxueqin on 2018/4/15.
 */

public class ThemeUtil {

    public static AppearanceConf loadAppearanceConf(Context context, TypedArray ta) {
        AppearanceConf conf = getAppearanceConf(context, ta.getInt(R.styleable.GeneralTitleBar_gtbTheme, Theme.LIGHT));
        setAppearanceConf(ta, conf, conf);
        return conf;
    }

    private static  AppearanceConf getAppearanceConf(Context context, int theme) {
        AppearanceConf conf = new AppearanceConf();
        conf.theme = theme;
        TypedArray ta = context.obtainStyledAttributes(Theme.getStyleResByTheme(theme), R.styleable.GeneralTitleBar);
        setAppearanceConf(ta, conf, null);
        return conf;
    }

    private static void setAppearanceConf(TypedArray ta, AppearanceConf conf, AppearanceConf refConf) {
        conf.titleSize = ta.getDimensionPixelSize(R.styleable.GeneralTitleBar_titleSize, refConf == null ? 0 : refConf.titleSize);
        conf.titleColor = ta.getColor(R.styleable.GeneralTitleBar_titleColor, refConf == null ? 0 : refConf.titleColor);
        conf.backIcon = ta.getResourceId(R.styleable.GeneralTitleBar_backIcon, refConf == null ? 0 : refConf.backIcon);
        conf.closeIcon = ta.getResourceId(R.styleable.GeneralTitleBar_closeIcon, refConf == null ? 0 : refConf.closeIcon);
        conf.moreIcon = ta.getResourceId(R.styleable.GeneralTitleBar_moreIcon, refConf == null ? 0 : refConf.moreIcon);
        conf.separatorColor = ta.getColor(R.styleable.GeneralTitleBar_separatorColor, refConf == null ? 0 : refConf.separatorColor);
        conf.mainOptionTextStyle = ta.getResourceId(R.styleable.GeneralTitleBar_mainOptionTextStyle, refConf == null ? 0 : refConf.mainOptionTextStyle);
        conf.subOptionTextStyle = ta.getResourceId(R.styleable.GeneralTitleBar_subOptionTextStyle, refConf == null ? 0 : refConf.subOptionTextStyle);
    }

    public static void applyAppearanceConf(GeneralTitleBar titleBar, AppearanceConf conf) {
        applyBackButtonAppearanceConf(titleBar, conf);
        applyCloseButtonAppearanceConf(titleBar, conf);
        applyDividerBarAppearanceConf(titleBar, conf);
        applyTitleViewAppearanceConf(titleBar, conf);
    }

    public static void applyTitleViewAppearanceConf(GeneralTitleBar titleBar, AppearanceConf conf) {
        if (titleBar.mTitleView != null) {
            titleBar.mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, conf.titleSize);
            titleBar.mTitleView.setTextColor(conf.titleColor);
        }
    }

    public static void applyBackButtonAppearanceConf(GeneralTitleBar titleBar, AppearanceConf conf) {
        if (titleBar.mBackButton != null) {
            titleBar.mBackButton.setImageResource(conf.backIcon);
        }
    }

    public static void applyCloseButtonAppearanceConf(GeneralTitleBar titleBar, AppearanceConf conf) {
        if (titleBar.mCloseButton != null) {
            titleBar.mCloseButton.setImageResource(conf.closeIcon);
        }
    }

    public static void applyDividerBarAppearanceConf(GeneralTitleBar titleBar, AppearanceConf conf) {
        if (titleBar.mDividerBar != null) {
            titleBar.mDividerBar.setBackgroundColor(conf.separatorColor);
        }
    }
}
