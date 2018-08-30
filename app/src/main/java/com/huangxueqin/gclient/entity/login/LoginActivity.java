package com.huangxueqin.gclient.entity.login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import com.huangxueqin.gclient.entity.browser.BrowserActivity;

public class LoginActivity extends BrowserActivity {

    private static final String CLIENT_ID = "5c0e4acb605d65b89fba";
    private static final String CLIENT_SECRET = "bbf73e9c1229aeabb29d17ab2204837108aa4078";
    private static final String CALLBACK_URL = "https://github.com/huangxueqin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getStartUrl() {
        return "https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID;
    }
}
