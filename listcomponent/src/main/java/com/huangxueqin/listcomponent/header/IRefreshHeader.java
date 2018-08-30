package com.huangxueqin.listcomponent.header;

/**
 * Created by huangxueqin on 2017/7/12.
 */

public interface IRefreshHeader {
    void onPullDown(int offset, int refreshDist);
    void onRefreshing();
    void onRefreshDone();
    void onRefreshFail();
}
