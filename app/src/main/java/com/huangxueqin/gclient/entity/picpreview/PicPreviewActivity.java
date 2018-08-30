package com.huangxueqin.gclient.entity.picpreview;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.adapters.HotPicPagerAdapter;
import com.huangxueqin.gclient.common.Const;
import com.huangxueqin.gclient.model.HotPicPost;
import com.huangxueqin.gclient.base.BaseActivity;
import com.huangxueqin.gclient.utils.DisplayUtil;
import com.huangxueqin.gclient.utils.ListUtil;
import com.huangxueqin.commontitlebar.GeneralTitleBar;
import com.huangxueqin.commontitlebar.TitleAction;
import com.huangxueqin.commontitlebar.TitleActionListener;

import java.util.List;

/**
 * Created by huangxueqin on 2017/2/12.
 */

public class PicPreviewActivity extends BaseActivity {

    private static final String TAG = "PicPreviewActivity";

    private GeneralTitleBar mTitleBar;
    private ViewPager mViewPager;

    private List<HotPicPost> mHotPicList;
    private int mInitPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        parsePreviewData(getIntent().getExtras());
        if (ListUtil.isEmpty(mHotPicList)) {
            showNoPreviewDataDialog();
            return;
        }
        // init title
        mTitleBar = findViewById(R.id.title_bar);
        setPreviewTitle(mInitPosition);
        mTitleBar.setTitleActionListener(mTitleActionListener);
        // init view pager
        mViewPager = findViewById(R.id.view_pager);
        HotPicPagerAdapter adapter = new HotPicPagerAdapter(this, mHotPicList);
        adapter.setActionListener(mHotPicPreviewListener);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mInitPosition);
        mViewPager.addOnPageChangeListener(mViewPagerChangeListener);
        // set full screen
        openTranslucentStatusBar();
    }

    private void parsePreviewData(Bundle bundle) {
        if (bundle == null) return;
        mHotPicList = bundle.getParcelableArrayList(Const.IntentKey.HOT_PIC_LIST);
        mInitPosition = bundle.getInt(Const.IntentKey.INIT_POSITION);
        if (ListUtil.isNotEmpty(mHotPicList)) {
            mInitPosition = Math.min(mHotPicList.size(), Math.max(mInitPosition, 0));
        }
    }

    private void showNoPreviewDataDialog() {
        new AlertDialog.Builder(this)
                .setMessage("没有可预览的数据")
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    finish();
                })
                .show();
    }

    private void openTranslucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
            int statusBarHeight = DisplayUtil.getStatusBarHeight(this);
            if (statusBarHeight > 0) {
                DisplayUtil.setPaddingTop(mTitleBar, statusBarHeight);
            }
        }
    }

    private void toggleFullScreen() {
        boolean show = mTitleBar.getVisibility() != View.VISIBLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (show) {
                DisplayUtil.showStatusBar(this);
            } else {
                DisplayUtil.hideStatusBar(this);
            }
        }
        if (show) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.title_bar_slide_in);
            mTitleBar.setVisibility(View.VISIBLE);
            mTitleBar.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.title_bar_slide_out);
            mTitleBar.setVisibility(View.INVISIBLE);
            mTitleBar.startAnimation(animation);
        }
    }

    private void setPreviewTitle(int position) {
        mTitleBar.setTitle(getString(R.string.activity_preview_title, position+1, mHotPicList.size()));
    }

    private ViewPager.OnPageChangeListener mViewPagerChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            setPreviewTitle(position);
        }
    };

    private HotPicPagerAdapter.ActionListener mHotPicPreviewListener = new HotPicPagerAdapter.SimpleActionListener() {
        @Override
        public void onItemClicked(View v, int position) {
            toggleFullScreen();
        }
    };

    private TitleActionListener mTitleActionListener = new TitleActionListener() {
        @Override
        public void onAction(View view, int action) {
            if (action == TitleAction.BACK) {
                onBackPressed();
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }
}
