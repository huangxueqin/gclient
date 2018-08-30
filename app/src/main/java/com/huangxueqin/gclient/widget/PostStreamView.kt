package com.huangxueqin.gclient.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.huangxueqin.gclient.R
import com.huangxueqin.gclient.utils.ViewStubUtil
import com.huangxueqin.listcomponent.HQPullRefreshLayout

/**
 * Created by huangxueqin on 2018/6/25.
 */
class PostStreamView : FrameLayout {

    var pullRefreshLayout: HQPullRefreshLayout
    var postListView: RecyclerView
    var errorViewClickHandler: (()->Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_news_stream, this, true)
        pullRefreshLayout = findViewById(R.id.pull_refresh_layout)
        postListView = findViewById(R.id.recycler_view)
    }

    fun showEmptyView() {
        pullRefreshLayout.visibility = View.GONE
        ViewStubUtil.setStubVisibility(this, R.id.stub_empty, R.id.empty_view, View.VISIBLE)
        ViewStubUtil.setStubVisibility(this, R.id.stub_init_loading, R.id.init_loading_view, View.GONE)
        findViewById<View>(R.id.empty_view).setOnClickListener {
            errorViewClickHandler?.invoke()
        }
    }

    fun showInitLoadView() {
        pullRefreshLayout.visibility = View.GONE
        ViewStubUtil.setStubVisibility(this, R.id.stub_empty, R.id.empty_view, View.GONE)
        ViewStubUtil.setStubVisibility(this, R.id.stub_init_loading, R.id.init_loading_view, View.VISIBLE)
    }

    fun showContentView() {
        pullRefreshLayout.visibility = View.VISIBLE
        ViewStubUtil.setStubVisibility(this, R.id.stub_empty, R.id.empty_view, View.GONE)
        ViewStubUtil.setStubVisibility(this, R.id.stub_init_loading, R.id.init_loading_view, View.GONE)
    }

    fun destroy() {
        postListView.adapter = null
    }

}