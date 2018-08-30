package com.huangxueqin.gclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.huangxueqin.gclient.R;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huangxueqin on 2018/4/29.
 */

public class DisplayUtil {

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    /**
     * {@link View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN} 使View全屏
     * {@link View#SYSTEM_UI_FLAG_FULLSCREEN} 使Window全屏
     * @param activity
     */
    public static void hideStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static void showStatusBar(Activity activity) {
        Window window = activity.getWindow();
        int visibility = window.getDecorView().getSystemUiVisibility();
        visibility &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
        visibility &= ~View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.getDecorView().setSystemUiVisibility(visibility);
    }

    public static void setPaddingLeft(View v, int padding) {
        v.setPadding(padding, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
    }

    public static void setPaddingTop(View v, int padding) {
        v.setPadding(v.getPaddingLeft(), padding, v.getPaddingRight(), v.getPaddingBottom());
    }

    public static void setViewTopMargin(View v, int margin) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp != null && (lp instanceof ViewGroup.MarginLayoutParams)) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.topMargin = margin;
        }
    }

    public static int getPrimaryColor(Context context) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, tv, true);
        return tv.data;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        } else {
            for (;;) {
                final int resultId = sNextGeneratedId.get();
                int nextId = resultId + 1;
                if (nextId > 0x00FFFFFF) nextId = 1;
                if (sNextGeneratedId.compareAndSet(resultId, nextId)) return resultId;
            }
        }
    }
}
