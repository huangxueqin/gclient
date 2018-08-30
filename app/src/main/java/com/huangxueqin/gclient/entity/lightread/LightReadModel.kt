package com.huangxueqin.gclient.entity.lightread

import com.huangxueqin.gclient.common.API
import com.huangxueqin.gclient.model.LightReadCategory
import com.huangxueqin.gclient.model.LightReadPost
import com.huangxueqin.gclient.base.ContentStreamSource
import com.huangxueqin.gclient.utils.TaskScheduler

import org.jsoup.Jsoup

import java.io.IOException
import java.util.ArrayList

/**
 * Created by huangxueqin on 2018/5/2.
 */

class LightReadModel(private val mChannel: LightReadCategory) : ContentStreamSource<LightReadPost> {

    override fun getContent(page: Int, pageSize: Int, callback: (Boolean, List<LightReadPost>?) -> Unit) {
        TaskScheduler.getInstance().executeAsync {
            val url = API.LightRead.url(mChannel, page)
            val result = parse(url)
            TaskScheduler.getInstance().runOnUIThread { callback(result != null, result) }
        }
    }

    private fun parse(url: String): List<LightReadPost>? {
        try {
            val document = Jsoup.connect(url).get()
            val items = ArrayList<LightReadPost>()
            val elements = document.select("div.xiandu_item")
            for (i in elements.indices) {
                val element = elements[i]
                val time = element.select("span>small").first()
                val siteTitle = element.select("a.site-title").first()
                val siteName = element.select("a.site-name").first()
                val siteImage = siteName.select("img").first()

                val item = LightReadPost()
                item.title = siteTitle.text()
                item.url = siteTitle.attr("href")
                item.time = time.text()
                item.sourceTitle = siteName.attr("title")
                item.sourceImage = siteImage.attr("src")

                items.add(item)
            }
            return items
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
