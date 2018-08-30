package com.huangxueqin.gclient.utils;

import android.view.View;
import android.view.ViewStub;

/**
 * Created by huangxueqin on 2018/5/1.
 */

public class ViewStubUtil {

    public static void setStubVisibility(View parent, int layoutId, int stubId, int viewId, int visibility, boolean bringToFront) {
        View view = parent.findViewById(viewId);
        if (view == null) {
            if (visibility != View.VISIBLE) {
                return;
            }
            ViewStub stub = parent.findViewById(stubId);
            if (layoutId > 0) {
                stub.setLayoutResource(layoutId);
            }
            if (stub != null) {
                stub.inflate();
            }
            view = parent.findViewById(viewId);
        }
        if (view != null) {
            view.setVisibility(visibility);
            if (visibility == View.VISIBLE && bringToFront) {
                view.bringToFront();
            }
        }
    }

    public static void setStubVisibility(View parent, int layoutId, int stubId, int viewId, int visibility) {
        setStubVisibility(parent, layoutId, stubId, viewId, visibility, false);
    }


    public static void setStubVisibility(View parent, int stubId, int viewId, int visibility, boolean bringToFront) {
        setStubVisibility(parent, 0, stubId, viewId, visibility, bringToFront);
    }

    public static void setStubVisibility(View parent, int stubId, int viewId, int visibility) {
        setStubVisibility(parent, stubId, viewId, visibility, false);
    }


}
