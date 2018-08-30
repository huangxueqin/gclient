package com.huangxueqin.gclient.entity.hotpic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.adapters.HotPicListAdapter;
import com.huangxueqin.gclient.common.Const;
import com.huangxueqin.gclient.model.MainEvent;
import com.huangxueqin.gclient.model.HotPicPost;
import com.huangxueqin.gclient.MainFragment;
import com.huangxueqin.gclient.utils.Logger;
import com.huangxueqin.gclient.utils.TaskScheduler;
import com.huangxueqin.gclient.widget.DoubleColumnSpaceDecoration;
import com.huangxueqin.gclient.utils.DisplayUtil;
import com.huangxueqin.gclient.utils.ListUtil;
import com.huangxueqin.gclient.utils.LoadingListener;
import com.huangxueqin.gclient.widget.PostStreamView;
import com.huangxueqin.commontitlebar.GeneralTitleBar;
import com.huangxueqin.commontitlebar.TitleAction;
import com.huangxueqin.listcomponent.HQPullRefreshLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;

/**
 * Created by huangxueqin on 2017/2/1.
 */

public class HotPicFragment extends MainFragment {

    private static final String TAG = "HotPicFragment";

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 20;

    private GeneralTitleBar mTitleBar;
    private PostStreamView mStreamView;

    private HotPicListAdapter mHotPicAdapter;
    private HotPicModel mDataSource;
    private boolean mIsFirst = true;
    private boolean mIsLoading = false;
    private boolean mIsComplete = false;
    // start page from 1 NOT 0
    private int mPage = 1;
    private AtomicInteger mReqCount = new AtomicInteger(0);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hot_pic, container, false);
        initTitleBar(rootView);
        initContentView(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mHotPicAdapter == null) {
            int hotPicGridSpace = getHotPicSpace();
            int hotPicWidth = DisplayUtil.getScreenWidth(context) / 2 - hotPicGridSpace;
            mHotPicAdapter = new HotPicListAdapter(getContext(), hotPicWidth);
            mHotPicAdapter.setActionListener((v, position) -> {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Const.IntentKey.HOT_PIC_LIST, (ArrayList<HotPicPost>) mHotPicAdapter.getHotPicList());
                bundle.putInt(Const.IntentKey.INIT_POSITION, position);
                sendEvent(MainEvent.PIC_PREVIEW, bundle);
            });
        }
        if (mDataSource == null) {
            mDataSource = new HotPicModel(context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // detach recyclerView and it's adapter
        if (mStreamView != null) {
            mStreamView.destroy();
            mStreamView = null;
        }
        mTitleBar = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFirst) {
            TaskScheduler.getInstance().runOnUIThread(() -> {
                if (isDetached()) {
                    return;
                }
                if (mIsFirst) {
                    mIsFirst = false;
                    mStreamView.showInitLoadView();
                    reload();
                }
            });
        }
    }

    private void initContentView(Context context, View rootView) {
        mStreamView = rootView.findViewById(R.id.stream_view);
        HQPullRefreshLayout pullRefreshLayout = mStreamView.getPullRefreshLayout();
        pullRefreshLayout.setOnRefreshListener(() -> {
            if (mIsLoading) {
                pullRefreshLayout.stopRefresh(false);
            } else {
                reload();
            }
        });
        RecyclerView hotPicListView = mStreamView.getPostListView();
        hotPicListView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        hotPicListView.addItemDecoration(new DoubleColumnSpaceDecoration(getHotPicSpace()));
        hotPicListView.addOnScrollListener(new LoadingListener() {
            @Override
            public void performLoadingAction() {
                loadNext();
            }

            @Override
            public boolean allowLoadingNow() {
                return !mIsLoading && !mIsComplete;
            }
        });
        mStreamView.setErrorViewClickHandler(() -> {
            mStreamView.showInitLoadView();
            reload();
            return Unit.INSTANCE;
        });
        hotPicListView.setAdapter(mHotPicAdapter);

    }

    private void initTitleBar(View rootView) {
        mTitleBar = rootView.findViewById(R.id.title_bar);
        mTitleBar.setTitleActionListener((v, action) -> {
            if (action == TitleAction.LEFT_BUTTON) {
                sendEvent(MainEvent.OPEN_MAIN_DRAWER, null);
            }
        });
    }

    private int getHotPicSpace() {
        return getResources().getDimensionPixelSize(R.dimen.hot_pic_space);
    }

    private void reload() {
        if (mIsLoading) {
            Logger.d(TAG, "request reload, but mIsLoading says there is already a loading process ongoing");
            return;
        }
        mIsLoading = true;
        int savedReqCount = mReqCount.incrementAndGet();
        mDataSource.getContent(INIT_PAGE, PAGE_SIZE, (success, data) -> {
            if (isDetached()) {
                Logger.d(TAG, "reload: fragment detached before finish");
                return Unit.INSTANCE;
            }
            if (!mIsLoading || mReqCount.get() != savedReqCount) {
                Logger.w(TAG, "reload: error loading state after news content fetched");
                return Unit.INSTANCE;
            }
            if (success && ListUtil.isNotEmpty(data)) {
                mHotPicAdapter.setImages(data);
                mStreamView.showContentView();
                mIsComplete = ListUtil.isEmpty(data);
            } else {
                Logger.d(TAG, "reload: no data found for this reload");
                if (mHotPicAdapter.getItemCount() > 0) {
                    // just toast user this reload fails
                    Toast.makeText(getContext(), R.string.reload_fail_retry, Toast.LENGTH_SHORT).show();
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
                    mHotPicAdapter.appendImages(data);
                    mPage += 1;
                } else {
                    mIsComplete = true;
                }
            } else {
                Snackbar.make(mStreamView, "加载失败，请重试", Snackbar.LENGTH_SHORT);
            }
            mIsLoading = false;
            return Unit.INSTANCE;
        });
    }
}
