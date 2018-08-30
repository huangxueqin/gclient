package com.huangxueqin.gclient.entity.post.streamsource

import java.net.URLEncoder

/**
 * Created by huangxueqin on 2018/6/24.
 */
class ChannelNews(val type: String) : NewsSource() {
    override fun buildUrl(page: Int, pageSize: Int): String {
        return "http://gank.io/api/data/${URLEncoder.encode(type, "utf-8")}/$pageSize/$page"
    }
}