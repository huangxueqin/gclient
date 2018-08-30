package com.huangxueqin.gclient.entity.post.streamsource

import com.google.gson.Gson
import com.huangxueqin.gclient.base.ContentStreamSource
import com.huangxueqin.gclient.http.SwiftGet
import com.huangxueqin.gclient.model.NewsPost
import com.huangxueqin.gclient.model.NewsPostRes
import com.huangxueqin.gclient.utils.Logger
import com.huangxueqin.gclient.utils.TaskScheduler

import java.io.IOException

/**
 * Created by huangxueqin on 2018/5/1.
 */

abstract class NewsSource : ContentStreamSource<NewsPost> {

    abstract fun buildUrl(page: Int, pageSize: Int): String

    override fun getContent(page: Int, pageSize: Int, callback: (Boolean, List<NewsPost>?) -> Unit) {
        TaskScheduler.getInstance().executeAsync {
            var response: NewsPostRes? = null
            val url = buildUrl(page, pageSize)
            try {
                val responseStr = SwiftGet(url).data.toString()
                response = Gson().fromJson(responseStr, NewsPostRes::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            TaskScheduler.getInstance().runOnUIThread {
                callback(!(response?.error ?: true), response?.results)
            }
        }
    }
}
