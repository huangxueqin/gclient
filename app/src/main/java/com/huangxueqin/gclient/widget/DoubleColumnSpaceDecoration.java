package com.huangxueqin.gclient.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by huangxueqin on 2017/2/4.
 */

public class DoubleColumnSpaceDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public DoubleColumnSpaceDecoration(int spacePixel) {
        spacing = spacePixel;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(spacing/2, spacing/2, spacing/2, spacing/2);
    }
}
