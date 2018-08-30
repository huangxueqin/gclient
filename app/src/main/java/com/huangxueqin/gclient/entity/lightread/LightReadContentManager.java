package com.huangxueqin.gclient.entity.lightread;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.adapters.LightReadListAdapter;
import com.huangxueqin.gclient.adapters.BaseFlowListAdapter;
import com.huangxueqin.gclient.model.LightReadCategory;
import com.huangxueqin.gclient.model.LightReadPost;
import com.huangxueqin.gclient.utils.ListUtil;
import com.huangxueqin.gclient.utils.LoadingListener;
import com.huangxueqin.gclient.utils.Logger;
import com.huangxueqin.gclient.utils.TaskScheduler;
import com.huangxueqin.gclient.widget.PostStreamView;
import com.huangxueqin.listcomponent.HQPullRefreshLayout;

import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;

public class LightReadContentManager {

    private static final String TAG = "LightReadContentManager";

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 20;

    private Context mContext;
    private LightReadCategory mCategory;
    private LightReadModel mDataSource;
    private boolean mFirst = true;
    private boolean mIsLoading;
    private int mPage;
    private AtomicInteger mReqCount = new AtomicInteger(0);

    private LightReadListAdapter mAdapter;
    private PostStreamView mStreamView;

    public LightReadContentManager(Context context, LightReadCategory category) {
        mContext = context;
        mCategory = category;
        mDataSource = new LightReadModel(category);
        mAdapter = new LightReadListAdapter(context);
    }

    public String getTitle() {
        return mCategory.title;
    }

    public void setOnListItemClickListener(BaseFlowListAdapter.OnItemClickListener<LightReadPost> listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    public void attach(PostStreamView streamView) {
        mStreamView = streamView;
        initStreamView();
        if (mFirst) {
            TaskScheduler.getInstance().runOnUIThread(() -> {
                if (mStreamView == null) {
                    return;
                }
                if (mFirst) {
                    mFirst = false;
                    mStreamView.showInitLoadView();
                    reload();
                }
            });
        }
    }

    public void detach() {
        if (mStreamView != null) {
            mStreamView.destroy();
            mStreamView = null;
        }
        mAdapter.setOnItemClickListener(null);
    }

    private void initStreamView() {
        HQPullRefreshLayout pullRefreshLayout = mStreamView.getPullRefreshLayout();
        pullRefreshLayout.setOnRefreshListener(() -> {
            if (mIsLoading) {
                pullRefreshLayout.stopRefresh(false);
            } else {
                reload();
            }
        });
        RecyclerView postListView = mStreamView.getPostListView();
        postListView.setLayoutManager(new LinearLayoutManager(mContext));
        postListView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        postListView.addOnScrollListener(new LoadingListener() {
            @Override
            public void performLoadingAction() {
                loadNext();
            }

            @Override
            public boolean allowLoadingNow() {
                return !mIsLoading;
            }
        });
        mStreamView.setErrorViewClickHandler(() -> {
            mStreamView.showInitLoadView();
            reload();
            return Unit.INSTANCE;
        });
        postListView.setAdapter(mAdapter);
    }

    private void reload() {
        if (mIsLoading) {
            Logger.d(TAG, "request reload, but mIsLoading says there is already a loading process ongoing");
            return;
        }
        mIsLoading = true;
        int savedReqCount = mReqCount.incrementAndGet();
        mDataSource.getContent(INIT_PAGE, PAGE_SIZE, (success, data) -> {
            if (mStreamView == null) {
                Logger.d(TAG, "reload: fragment detached before finish");
                return Unit.INSTANCE;
            }
            if (!mIsLoading || mReqCount.get() != savedReqCount) {
                Logger.w(TAG, "reload: error loading state after news content fetched");
                return Unit.INSTANCE;
            }
            if (success && ListUtil.isNotEmpty(data)) {
                mAdapter.setDataList(data, data.size() < PAGE_SIZE);
                mStreamView.showContentView();
            } else {
                Logger.d(TAG, "reload: no data found for this reload");
                if (mAdapter.getDataItemCount() > 0) {
                    // just toast user this reload fails
                    Toast.makeText(mContext, R.string.reload_fail_retry, Toast.LENGTH_SHORT).show();
                } else {
                    // show empty view
                    mStreamView.showEmptyView();
                }
            }
            mPage = INIT_PAGE+1;
            if (mStreamView.getPullRefreshLayout().isRefreshing()) {
                mStreamView.getPullRefreshLayout().stopRefresh(success);
            }
            mIsLoading = false;
            return Unit.INSTANCE;
        });
    }

    private void loadNext() {
        mIsLoading = true;
        int savedReqCount = mReqCount.incrementAndGet();
        mDataSource.getContent(mPage, PAGE_SIZE, (success, data) -> {
            if (mStreamView == null) {
                Logger.d(TAG, "loadmore: fragment detached before load more finish");
                return Unit.INSTANCE;
            }
            if (!mIsLoading || mReqCount.get() != savedReqCount) {
                Logger.w(TAG, "loadmore: error loading state after news content fetched");
                return Unit.INSTANCE;
            }
            if (success) {
                if (ListUtil.isNotEmpty(data)) {
                    mAdapter.appendDataList(data, data.size() < PAGE_SIZE);
                    mPage += 1;
                } else {
                    mAdapter.setCompleted(true);
                }
            } else {
                Snackbar.make(mStreamView, "加载失败，请重试", Snackbar.LENGTH_SHORT);
            }
            mIsLoading = false;
            return Unit.INSTANCE;
        });
    }
}
