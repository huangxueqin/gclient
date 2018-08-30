package com.huangxueqin.gclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.huangxueqin.gclient.entity.lightread.LightReadFragment;
import com.huangxueqin.gclient.entity.hotpic.HotPicFragment;
import com.huangxueqin.gclient.entity.post.NewsPostFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangxueqin on 2018/4/30.
 * HOME FRAGMENT 始终在内存当中，使用hide和show，
 * 其他 Fragment 使用 attach 和 detach
 */

public class MainFragmentManager {

    private static final String TAG = "MainFragmentManager";

    public static final int ID_NEWS_POST_FRAGMENT = 0;
    public static final int ID_LIGHT_READ_FRAGMENT = 1;
    public static final int ID_HOT_PIC_FRAGMENT = 2;
    public static final int ID_ABOUT_FRAGMENT = 3;

    // key used for save and restore state
    private static final String STATE_KEY = MainFragmentManager.class.getName();

    private static final String TAG_HOME_FRAGMENT = "home_fragment";

    private FragmentManager mFragmentManager;
    private int mContainerId;
    private ArrayList<String> mFragmentStack;
    private Class<? extends Fragment> mHomeFragmentClazz;
    private Fragment mHomeFragment;

    public MainFragmentManager(FragmentManager fm, int containerId) {
        mFragmentManager = fm;
        mContainerId = containerId;
        mFragmentStack = new ArrayList<>();
        mHomeFragmentClazz = NewsPostFragment.class;
    }

    public void initHomeFragment(@Nullable Bundle argument) {
        if (!mFragmentStack.isEmpty()) return;
        mHomeFragment = newFragmentInstance(mHomeFragmentClazz, argument);
        mFragmentManager.beginTransaction().add(mContainerId, mHomeFragment, TAG_HOME_FRAGMENT).commit();
        mFragmentStack.add(TAG_HOME_FRAGMENT);
    }

    public void showHomeFragment(Bundle params) {
        showFragment(mHomeFragmentClazz, params, null);
    }

    public void showFreeReadFragment(Bundle params) {
        showFragment(LightReadFragment.class, params, null);
    }

    public void showWelfareFragment(Bundle params) {
        showFragment(HotPicFragment.class, params, null);
    }

    public void showFragment(@NonNull Class<? extends Fragment> fragmentClazz, @Nullable String tag) {
        showFragment(fragmentClazz, null, tag);
    }

    public void showFragment(@NonNull Class<? extends Fragment> fragmentClazz, @Nullable Bundle argument, @Nullable String tag) {
        if (TextUtils.isEmpty(tag)) {
            tag = getDefaultTag(fragmentClazz);
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.disallowAddToBackStack();
        // detach fragments except home and target
        for (int i = 0; i < mFragmentStack.size(); i++) {
            String tag2 = mFragmentStack.get(i);
            if (tag.equals(tag2)) {
                continue;
            }
            Fragment f = mFragmentManager.findFragmentByTag(tag2);
            if (f != null) {
                if (TAG_HOME_FRAGMENT.equals(tag2)) {
                    ft.hide(f);
                } else if (!f.isDetached()) {
                    ft.detach(f);
                }
            }
        }

        if (!mFragmentStack.contains(tag)) {
            mFragmentStack.add(tag);
        }

        Fragment f = mFragmentManager.findFragmentByTag(tag);
        if (f != null) {
            if (f.isDetached()) {
                ft.attach(f);
            }
            if (f.isHidden()) {
                ft.show(f);
            }
        } else {
            f = newFragmentInstance(fragmentClazz, argument);
            ft.add(mContainerId, f, tag);
        }
        ft.commitNow();
    }

    public boolean dispatchBackKeyPressed() {
        Fragment topFragment = mFragmentManager.findFragmentById(mContainerId);
        if (topFragment != null && topFragment == mHomeFragment) {
            return false;
        }

        FragmentTransaction ft  = mFragmentManager.beginTransaction();
        for (int i = mFragmentStack.size()-1; i > 0; i--) {
            String tag = mFragmentStack.get(i);
            Fragment f = mFragmentManager.findFragmentByTag(tag);
            if (f != null && f.isAdded()) {
                ft.detach(f);
            }
        }
        if (mHomeFragment != null) {
            ft.show(mHomeFragment);
        }
        ft.commit();
        return true;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putStringArrayList(STATE_KEY, mFragmentStack);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        mFragmentStack = bundle.getStringArrayList(STATE_KEY);

    }

    private Fragment newFragmentInstance(Class<? extends Fragment> clazz, @Nullable Bundle argument) {
        Fragment f = null;
        try {
            f = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (f != null && argument != null) {
            f.setArguments(argument);
        }
        return f;
    }

    private String getDefaultTag(@NonNull Class<? extends Fragment> fragmentClazz) {
        if (mHomeFragmentClazz.equals(fragmentClazz)) {
            return TAG_HOME_FRAGMENT;
        } else {
            return fragmentClazz.getName();
        }
    }
}
