package com.huangxueqin.gclient.entity.post.streamsource

/**
 * Created by huangxueqin on 2018/5/1.
 */

class RecentNews : NewsSource() {
    override fun buildUrl(page: Int, pageSize: Int): String {
        return "http://gank.io/api/data/all/$pageSize/$page"
    }
}
