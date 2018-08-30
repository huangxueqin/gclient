package com.huangxueqin.gclient;

import android.content.Context;
import android.os.Bundle;

import com.huangxueqin.gclient.base.BaseFragment;
import com.huangxueqin.gclient.common.Const;
import com.huangxueqin.gclient.model.MainEvent;

import org.jetbrains.annotations.NotNull;

/**
 * Created by huangxueqin on 2018/5/2.
 */

public class MainFragment extends BaseFragment {

    private int mId;
    private MainActivity mMainActivity;

    public void setId(int id) {
        mId = id;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

    protected void sendEvent(int eventId, Bundle eventParam) {
        if (mMainActivity != null) {
            mMainActivity.handleEvent(mId, eventId, eventParam);
        }
    }

    public void openDrawer() {
        sendEvent(MainEvent.OPEN_MAIN_DRAWER, null);
    }

    public void openUrl(@NotNull String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.IntentKey.URL, url);
        sendEvent(MainEvent.OPEN_URL, bundle);
    }
}
