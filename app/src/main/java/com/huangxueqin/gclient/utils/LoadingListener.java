package com.huangxueqin.gclient.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by huangxueqin on 2017/2/24.
 */

public abstract class LoadingListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "LoadingListener";

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0 && allowLoadingNow()) {
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            final int visibleItemCount = lm.getChildCount();
            final int totalItemCount = lm.getItemCount();
            int firstVisiblePosition = -1;
            if (lm instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) lm;
                firstVisiblePosition = llm.findFirstVisibleItemPosition();
            } else if (lm instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) lm;
                int[] pos = {-1, -1};
                sglm.findFirstVisibleItemPositions(pos);
                firstVisiblePosition = pos[0];
            }
            if (firstVisiblePosition >= 0) {
                if (firstVisiblePosition + visibleItemCount >= totalItemCount) {
                    performLoadingAction();
                }
            }
        }
    }

    public abstract boolean allowLoadingNow();

    public abstract void performLoadingAction();
}
