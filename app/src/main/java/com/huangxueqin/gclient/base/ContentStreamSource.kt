package com.huangxueqin.gclient.base

/**
 * Created by huangxueqin on 2018/5/1.
 */

interface ContentStreamSource<out T> {
    /**
     * 获取阅读流网络数据，异步接口，返回信息在 callback 中
     * @param page
     * @param pageSize
     * @param callback
     */
    fun getContent(page: Int, pageSize: Int, callback: (Boolean, List<T>?)->Unit)
}
