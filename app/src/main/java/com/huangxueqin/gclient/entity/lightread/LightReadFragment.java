package com.huangxueqin.gclient.entity.lightread;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.model.LightReadCategory;
import com.huangxueqin.gclient.model.MainEvent;
import com.huangxueqin.gclient.MainFragment;
import com.huangxueqin.gclient.utils.ListUtil;
import com.huangxueqin.gclient.widget.PostStreamView;
import com.huangxueqin.commontitlebar.GeneralTitleBar;
import com.huangxueqin.commontitlebar.TitleAction;
import com.huangxueqin.slidetablayout.SlideTabLayout;

import java.util.List;

/**
 * Created by huangxueqin on 2017/8/7.
 */

public class LightReadFragment extends MainFragment {

    private GeneralTitleBar mTitleBar;
    private SlideTabLayout mSlideTabLayout;
    private ViewPager mViewPager;

    private List<LightReadContentManager> mLrContentMgrs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_light_read, container, false);
        initTitleBar(rootView);
        initSlideTabs(rootView);
        initViewPager(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (ListUtil.isEmpty(mLrContentMgrs)) {
            mLrContentMgrs = ListUtil.asList(
                    new LightReadContentManager(context, LightReadCategory.WOW),
                    new LightReadContentManager(context, LightReadCategory.APPS),
                    new LightReadContentManager(context, LightReadCategory.IM_RICH),
                    new LightReadContentManager(context, LightReadCategory.FUNNY),
                    new LightReadContentManager(context, LightReadCategory.ANDROID),
                    new LightReadContentManager(context, LightReadCategory.DIE_DIE_DIE),
                    new LightReadContentManager(context, LightReadCategory.THINKING)
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTitleBar = null;
        mSlideTabLayout = null;
        mViewPager = null;
    }

    private void initTitleBar(View rootView) {
        mTitleBar = rootView.findViewById(R.id.title_bar);
        mTitleBar.setTitleActionListener((v, action) -> {
            if (action == TitleAction.LEFT_BUTTON) {
                sendEvent(MainEvent.OPEN_MAIN_DRAWER, null);
            }
        });
    }

    private void initSlideTabs(View rootView) {
        mSlideTabLayout = rootView.findViewById(R.id.sliding_tabs);
        mSlideTabLayout.setAdapter(new SlideTabLayout.Adapter() {
            @Override
            public int getTabCount() {
                return ListUtil.sizeOf(mLrContentMgrs);
            }

            @Override
            public String getTitle(int tabNdx) {
                return mLrContentMgrs.get(tabNdx).getTitle();
            }
        });

        mSlideTabLayout.setTabSelectListener((tabNdx) ->{
            boolean smoothScroll = Math.abs(tabNdx - mViewPager.getCurrentItem()) <= 2;
            mViewPager.setCurrentItem(tabNdx, smoothScroll);
        });
    }

    private void initViewPager(View rootView) {
        mViewPager = rootView.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new LrPageAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mSlideTabLayout.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mSlideTabLayout.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mSlideTabLayout.onPageScrollStateChanged(state);
            }
        });
    }

    private class LrPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return ListUtil.sizeOf(mLrContentMgrs);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            PostStreamView streamView = (PostStreamView) LayoutInflater.from(container.getContext())
                    .inflate(R.layout.view_light_read_page, container, false);
            container.addView(streamView);
            LightReadContentManager manager = mLrContentMgrs.get(position);
            manager.attach(streamView);
            manager.setOnListItemClickListener((post, index) -> {
                openUrl(post.url);
            });
            return streamView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            LightReadContentManager manager = mLrContentMgrs.get(position);
            manager.detach();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
