package com.huangxueqin.commontitlebar;

import android.content.Context;

/**
 * Created by huangxueqin on 2018/4/14.
 */

public class DisplayUtil {

    public static int px2sp(Context context, int px) {
        return Math.round(px / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int sp2px(Context context, int sp) {
        return Math.round(context.getResources().getDisplayMetrics().scaledDensity * sp);
    }

    public static int px2dp(Context context, int px) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }

    public static int dp2px(Context context, int dp) {
        return Math.round(context.getResources().getDisplayMetrics().density * dp);
    }

}
