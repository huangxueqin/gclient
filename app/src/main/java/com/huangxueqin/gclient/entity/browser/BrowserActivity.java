package com.huangxueqin.gclient.entity.browser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.common.Const;
import com.huangxueqin.gclient.base.BaseActivity;
import com.huangxueqin.gclient.utils.DataUtil;
import com.huangxueqin.gclient.utils.Logger;
import com.huangxueqin.gclient.widget.WebProgressIndicator;
import com.huangxueqin.commontitlebar.GeneralTitleBar;
import com.huangxueqin.commontitlebar.TitleAction;

/**
 * Created by huangxueqin on 2017/2/3.
 */

public class BrowserActivity extends BaseActivity {

    private static final String TAG = "BrowserActivity";

    private static final int LOAD_PROGRESS_DONE = 100;

    private GeneralTitleBar mTitleBar;
    private WebView mWebView;
    private WebProgressIndicator progressIndicator;

    private String mInitUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        initTitleBar();
        initWebView();
        progressIndicator = findViewById(R.id.progress_indicator);
    }

    private void initWebView() {
        mWebView = findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        // init url
        mInitUrl = getStartUrl();
        runOnUiThread(() -> {
            mWebView.loadUrl(mInitUrl);
        });
    }

    protected String getStartUrl() {
        return DataUtil.getString(getIntent().getExtras(), Const.IntentKey.URL, "");
    }

    private void initTitleBar() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setTitleActionListener((v, action) -> {
            if (action == TitleAction.BACK) {
                goBack();
            } else if (action == TitleAction.CLOSE) {
                super.onBackPressed();
            }
        });
    }

    private void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            mTitleBar.setCloseButtonVisibility(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTitleBar.setTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == LOAD_PROGRESS_DONE) {
                progressIndicator.setProgress(1f);
                progressIndicator.animate().alpha(0)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                progressIndicator.setVisibility(View.GONE);
                            }
                        })
                        .start();

            } else {
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setProgress(1f*newProgress/LOAD_PROGRESS_DONE);
            }
        }
    };

    private WebViewClient mWebViewClient = new WebViewClient() {
        /**
         * shouldOverrideUrlLoading 在主动调用WebView#loadUrl时并不调用，只有在
         */

        @TargetApi(21)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @TargetApi(21)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    };
}
