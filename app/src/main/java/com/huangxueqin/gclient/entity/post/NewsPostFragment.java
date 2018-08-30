package com.huangxueqin.gclient.entity.post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.adapters.PostListAdapter;
import com.huangxueqin.gclient.common.Const;
import com.huangxueqin.gclient.entity.post.streamsource.NewsSource;
import com.huangxueqin.gclient.entity.post.streamsource.ChannelNews;
import com.huangxueqin.gclient.entity.post.streamsource.RecentNews;
import com.huangxueqin.gclient.MainFragment;
import com.huangxueqin.gclient.utils.DataUtil;
import com.huangxueqin.gclient.utils.ListUtil;
import com.huangxueqin.gclient.utils.LoadingListener;
import com.huangxueqin.gclient.utils.Logger;
import com.huangxueqin.gclient.utils.TaskScheduler;
import com.huangxueqin.gclient.widget.PostStreamView;
import com.huangxueqin.commontitlebar.GeneralTitleBar;
import com.huangxueqin.commontitlebar.TitleAction;
import com.huangxueqin.listcomponent.HQPullRefreshLayout;

import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;

/**
 * Created by huangxueqin on 2018/5/1.
 */

public class NewsPostFragment extends MainFragment {

    private static final String TAG = "NewsPostFragment";

    public static final String SOURCE_TYPE_RECENT = "recent";
    public static final String SOURCE_TYPE_CHANNEL = "channel";

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 20;

    private GeneralTitleBar mTitleBar;
    private PostStreamView mNewsStreamView;

    private NewsSource mNewsSource;
    private PostListAdapter mNewsListAdapter;

    private boolean mIsFirst = true;
    private boolean mIsLoading = false;
    // start page from 1 NOT 0
    private int mPage = 1;
    private AtomicInteger mReqCount = new AtomicInteger(0);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsSource = parseNewsSource(getArguments());
    }

    private NewsSource parseNewsSource(Bundle argument) {
        String sourceType = DataUtil.getString(argument, Const.IntentKey.NEWS_SOURCE_TYPE, SOURCE_TYPE_RECENT);
        if (SOURCE_TYPE_CHANNEL.equals(sourceType)) {
            return new ChannelNews(DataUtil.getString(argument, Const.IntentKey.NEWS_CHANNEL, "null"));
        } else {
            return new RecentNews();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // init mNewsListAdapter
        if (mNewsListAdapter == null) {
            mNewsListAdapter = new PostListAdapter(context);
            mNewsListAdapter.setOnItemClickListener((post, position) -> {
                openUrl(post.url);
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initTitleBar(rootView);
        initNewsStreamView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // do first load
        TaskScheduler.getInstance().runOnUIThread(() -> {
            if (isDetached()) {
                return;
            }
            if (mIsFirst) {
                mIsFirst = false;
                mNewsStreamView.showInitLoadView();
                reload();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // detach recyclerView and it's adapter
        if (mNewsStreamView != null) {
            mNewsStreamView.getPostListView().setAdapter(null);
        }
        mNewsStreamView = null;
        mTitleBar = null;
    }

    private void initTitleBar(View rootView) {
        mTitleBar = rootView.findViewById(R.id.title_bar);
        mTitleBar.setTitleActionListener((view, action) -> {
            if (action == TitleAction.LEFT_BUTTON) {
                openDrawer();
            }
        });
    }

    private void initNewsStreamView(View rootView) {
        Context context = getContext();
        if (context == null) {
            Logger.w(TAG, "fatal error: initNewsStreamView but context is null");
            context = rootView.getContext();
        }
        mNewsStreamView = rootView.findViewById(R.id.news_stream_view);
        HQPullRefreshLayout pullRefreshLayout = mNewsStreamView.getPullRefreshLayout();
        pullRefreshLayout.setOnRefreshListener(() -> {
            if (mIsLoading) {
                pullRefreshLayout.stopRefresh(false);
            } else {
                reload();
            }
        });
        RecyclerView newsListView = mNewsStreamView.getPostListView();
        newsListView.setLayoutManager(new LinearLayoutManager(context));
        newsListView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        newsListView.addOnScrollListener(new LoadingListener() {
            @Override
            public void performLoadingAction() {
                loadMore();
            }

            @Override
            public boolean allowLoadingNow() {
                return !mIsLoading;
            }
        });
        mNewsStreamView.setErrorViewClickHandler(() -> {
            mNewsStreamView.showInitLoadView();
            reload();
            return Unit.INSTANCE;
        });
        newsListView.setAdapter(mNewsListAdapter);
    }

    private void reload() {
        if (mIsLoading) {
            Logger.d(TAG, "request reload, but mIsLoading says there is already a loading process ongoing");
            return;
        }
        mIsLoading = true;
        int savedReqCount = mReqCount.incrementAndGet();
        mNewsSource.getContent(INIT_PAGE, PAGE_SIZE, (success, data) -> {
            if (isDetached()) {
                Logger.d(TAG, "reload: fragment detached before finish");
                return Unit.INSTANCE;
            }
            if (!mIsLoading || mReqCount.get() != savedReqCount) {
                Logger.w(TAG, "reload: error loading state after news content fetched");
                return Unit.INSTANCE;
            }
            if (success && ListUtil.isNotEmpty(data)) {
                mNewsListAdapter.setDataList(data, data.size() < PAGE_SIZE);
                mNewsStreamView.showContentView();
            } else {
                Logger.d(TAG, "reload: no data found for this reload");
                if (mNewsListAdapter.getDataItemCount() > 0) {
                    // just toast user this reload fails
                    Toast.makeText(getContext(), R.string.reload_fail_retry, Toast.LENGTH_SHORT).show();
                } else {
                    // show empty view
                    mNewsStreamView.showEmptyView();
                }
            }
            mPage = INIT_PAGE+1;
            if (mNewsStreamView.getPullRefreshLayout().isRefreshing()) {
                mNewsStreamView.getPullRefreshLayout().stopRefresh(success);
            }
            mIsLoading = false;
            return Unit.INSTANCE;
        });
    }

    private void loadMore() {
        mIsLoading = true;
        int savedReqCount = mReqCount.incrementAndGet();
        mNewsSource.getContent(mPage, PAGE_SIZE, (success, data) -> {
            if (isDetached()) {
                Logger.d(TAG, "loadmore: fragment detached before load more finish");
                return Unit.INSTANCE;
            }
            if (!mIsLoading || mReqCount.get() != savedReqCount) {
                Logger.w(TAG, "loadmore: error loading state after news content fetched");
                return Unit.INSTANCE;
            }
            if (success) {
                if (ListUtil.isNotEmpty(data)) {
                    mNewsListAdapter.appendDataList(data, data.size() < PAGE_SIZE);
                    mPage += 1;
                } else {
                    mNewsListAdapter.setCompleted(true);
                }
            } else {
                Snackbar.make(mNewsStreamView, "加载失败，请重试", Snackbar.LENGTH_SHORT);
            }
            mIsLoading = false;
            return Unit.INSTANCE;
        });
    }
}
