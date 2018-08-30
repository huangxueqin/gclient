package com.huangxueqin.gclient.entity.hotpic

import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils

import com.bumptech.glide.Glide
import com.huangxueqin.gclient.base.ContentStreamSource
import com.huangxueqin.gclient.entity.post.streamsource.NewsSource
import com.huangxueqin.gclient.common.API
import com.huangxueqin.gclient.model.NewsPostCategory
import com.huangxueqin.gclient.model.HotPicPost
import com.huangxueqin.gclient.model.NewsPost
import com.huangxueqin.gclient.utils.ListUtil
import com.huangxueqin.gclient.utils.TaskScheduler

import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ExecutionException

/**
 * Created by huangxueqin on 2018/5/5.
 */

class HotPicModel(private val mContext: Context) : ContentStreamSource<HotPicPost> {

    private val mPostModel = object : NewsSource() {
        override fun buildUrl(page: Int, pageSize: Int): String {
            return API.Post.url(NewsPostCategory.Welfare, page, pageSize)
        }
    }

    override fun getContent(page: Int, pageSize: Int, callback: (Boolean, List<HotPicPost>?) -> Unit) {
        mPostModel.getContent(page, pageSize) { success, postInfoList ->
            if (!success) {
                callback(false, null)
            } else {
                TaskScheduler.getInstance().executeAsync {
                    val hotPicList = ArrayList<HotPicPost>()
                    resolveHotPicInfo(postInfoList.orEmpty(), hotPicList)
                    TaskScheduler.getInstance().runOnUIThread { callback(true, hotPicList) }
                }
            }
        }
    }

    private fun resolveHotPicInfo(postItemInfoList: List<NewsPost>, hotPicList: MutableList<HotPicPost>) {
        if (ListUtil.isNotEmpty(postItemInfoList)) {
            for (postItemInfo in postItemInfoList) {
                if (TextUtils.isEmpty(postItemInfo.url)) {
                    continue
                }
                var sizeInfo: SizeInfo? = null
                if (sPicSizeMap.containsKey(postItemInfo.url)) {
                    sizeInfo = sPicSizeMap[postItemInfo.url]
                } else {
                    sizeInfo = downloadAndGetSize(postItemInfo.url)
                }
                if (sizeInfo == null) {
                    continue
                }
                hotPicList.add(HotPicPost(postItemInfo.url, sizeInfo.width, sizeInfo.height))
            }
        }
    }

    private fun downloadAndGetSize(url: String): SizeInfo? {
        val future = Glide.with(mContext).download(url).submit()
        try {
            val f = future.get()
            val ops = BitmapFactory.Options()
            ops.inJustDecodeBounds = true
            BitmapFactory.decodeFile(f.absolutePath, ops)
            if (ops.outHeight > 0 && ops.outWidth > 0) {
                val sizeInfo = SizeInfo(ops.outWidth, ops.outHeight)
                sPicSizeMap[url] = sizeInfo
                return sizeInfo
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return null
    }

    private class SizeInfo(internal var width: Int, internal var height: Int)

    companion object {

        private val sPicSizeMap = HashMap<String, SizeInfo>()
    }
}
