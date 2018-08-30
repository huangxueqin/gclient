package com.huangxueqin.gclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.huangxueqin.gclient.base.BaseActivity;
import com.huangxueqin.gclient.entity.about.AboutFragment;
import com.huangxueqin.gclient.entity.login.LoginActivity;
import com.huangxueqin.gclient.entity.picpreview.PicPreviewActivity;
import com.huangxueqin.gclient.model.MainEvent;
import com.huangxueqin.gclient.entity.browser.BrowserActivity;
import com.huangxueqin.gclient.utils.DisplayUtil;
import com.huangxueqin.gclient.utils.TaskScheduler;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    // NavigationView 继承自 ScrimInsetsFrameLayout 导致有一个状态栏高度的变暗。。。
    // 没有办法取消，如果不希望有这个效果，则不要使用它
    private NavigationView mNavigationView;
    private View mNavigationHeader;
    private View mStatusBarAdjustView;
    private FrameLayout mContentView;

    private int mFragmentContainerId;
    private MainFragmentManager mMainFragmentMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initFragments(savedInstanceState);
        openTransparentStatusBarSupport();
    }

    private void initViews() {
        // drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        // init navigation view
        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationHeader = mNavigationView.getHeaderView(0);
        mNavigationHeader.findViewById(R.id.note).setOnClickListener((v) -> {
            startLogin();
        });
        // init content view
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initContentViewLollipop();
        } else {
            initContentViewLollipop();
        }
        // init fragment container Id
        mFragmentContainerId = DisplayUtil.generateViewId();
        mContentView.setId(mFragmentContainerId);
    }

    private void initContentViewPreLollipop() {
        mContentView = new FrameLayout(this);
        mDrawerLayout.addView(mContentView, 0,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initContentViewLollipop() {
        int statusBarHeight = DisplayUtil.getStatusBarHeight(this);
        if (statusBarHeight <= 0) {
            initContentViewPreLollipop();
        } else {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            mDrawerLayout.addView(linearLayout, 0,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mStatusBarAdjustView = new View(this);
            linearLayout.addView(mStatusBarAdjustView,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
            mContentView = new FrameLayout(this);
            linearLayout.addView(mContentView,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            openTransparentStatusBarSupport();
        }
    }

    private void openTransparentStatusBarSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void setStatusBarColor(@ColorInt int color) {
        if (mStatusBarAdjustView != null) {
            mStatusBarAdjustView.setBackgroundColor(color);
        }
    }

    private void initFragments(Bundle savedInstanceState) {
        mMainFragmentMgr = new MainFragmentManager(getSupportFragmentManager(), mFragmentContainerId);
        // add init fragment
        if (savedInstanceState == null) {
            mMainFragmentMgr.initHomeFragment(null);
        } else {
            mMainFragmentMgr.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMainFragmentMgr.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mNavigationView.setCheckedItem(item.getItemId());
        switch (item.getItemId()) {
            case R.id.nav_home:
                mMainFragmentMgr.showHomeFragment(null);
                break;
            case R.id.nav_reading:
                mMainFragmentMgr.showFreeReadFragment(null);
                break;
            case R.id.nav_hot_pic:
                mMainFragmentMgr.showWelfareFragment(null);
                break;
            case R.id.nav_about:
                mMainFragmentMgr.showFragment(AboutFragment.class, null);
                break;
        }
        TaskScheduler.getInstance().runOnUIThread(()->{
            closeDrawer();
        }, 250);
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public void onBackPressed() {
        if (mMainFragmentMgr.dispatchBackKeyPressed()) {
            mNavigationView.setCheckedItem(R.id.nav_home);
            return;
        }
        moveTaskToBack(true);
    }

    public void startBrowserActivity(Bundle bundle) {
        Intent i = new Intent(this, BrowserActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void startPicPreviewActivity(Bundle bundle) {
        Intent i = new Intent(this, PicPreviewActivity.class);
        i.putExtras(bundle);
        startActivity(i);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void handleEvent(int fragmentId, int eventId, Bundle params) {
        switch (eventId) {
            case MainEvent.OPEN_URL:
                startBrowserActivity(params);
                break;
            case MainEvent.OPEN_MAIN_DRAWER:
                openDrawer();
                break;
            case MainEvent.PIC_PREVIEW:
                startPicPreviewActivity(params);
                break;
        }
    }
}
