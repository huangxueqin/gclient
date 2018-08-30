package com.huangxueqin.commontitlebar;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

/**
 * Created by huangxueqin on 2018/3/26.
 */

public class TitleBarUtils {

    public static void hideStubView(ViewGroup parent, int stubId, int viewId) {
        View v = parent.findViewById(viewId);
        if (v != null) {
            v.setVisibility(View.GONE);
        }
    }

    public static void showStubView(ViewGroup parent, int stubId, int viewId) {
        View v = parent.findViewById(viewId);
        if (v == null) {
            ViewStub stub = parent.findViewById(stubId);
            if (stub != null) {
                stub.inflate();
            }
            v = parent.findViewById(viewId);
        }
        if (v != null) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static <T extends View> T getViewFromViewStub(ViewGroup parent, int stubId, int viewId) {
        return getViewFromViewStub(parent, 0, stubId, viewId);
    }

    public static <T extends View> T getViewFromViewStub(ViewGroup parent, int layoutResId, int stubId, int viewId) {
        T view = null;
        ViewStub stub = parent.findViewById(stubId);
        if (stub != null) {
            if (layoutResId != 0) {
                stub.setLayoutResource(layoutResId);
            }
            stub.inflate();
            view = parent.findViewById(viewId);
        }
        return view;
    }
}
