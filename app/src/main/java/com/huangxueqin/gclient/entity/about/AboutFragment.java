package com.huangxueqin.gclient.entity.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.MainFragment;
import com.huangxueqin.gclient.utils.ActivityUtil;
import com.huangxueqin.commontitlebar.GeneralTitleBar;
import com.huangxueqin.commontitlebar.TitleAction;
import com.huangxueqin.commontitlebar.TitleActionListener;

/**
 * Created by huangxueqin on 2018/5/5.
 */

public class AboutFragment extends MainFragment {

    private GeneralTitleBar mTitleBar;
    private TextView mVersionText;
    private TextView mAppIntroText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        mVersionText = rootView.findViewById(R.id.app_version);
        mAppIntroText = rootView.findViewById(R.id.app_introduce);
        mTitleBar = rootView.findViewById(R.id.title_bar);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init title bar
        mTitleBar.setTitleActionListener(mTitleActionListener);
        // init version info
        String versionName = ActivityUtil.getAppVersionName(getContext());
        if (TextUtils.isEmpty(versionName)) {
            mVersionText.setVisibility(View.GONE);
        } else {
            mVersionText.setText("v" + versionName);
        }
        // init introduce info
        interceptEmail(mAppIntroText);
    }

    private TitleActionListener mTitleActionListener = new TitleActionListener() {
        @Override
        public void onAction(View view, int action) {
            if (action == TitleAction.LEFT_BUTTON) {
                openDrawer();
            }
        }
    };

    private void interceptEmail(TextView tv) {
        tv.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spannable = (Spannable) tv.getText();
        URLSpan[] urlSpans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spannable);
        for (URLSpan urlSpan : urlSpans) {
            String url = urlSpan.getURL();
            if (url.startsWith("mailto:")) {
                int spanStart = ssb.getSpanStart(urlSpan);
                int spanEnd = ssb.getSpanEnd(urlSpan);
                ssb.replace(spanStart, spanEnd, "点我发送");
                ssb.setSpan(new EmailSpan(getContext(), url.substring("mailto:".length())), spanStart, spanStart+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        tv.setText(ssb);
    }

    private static class EmailSpan extends ClickableSpan {
        Context context;
        String emailAddr;

        public EmailSpan(Context context, String emailAddr) {
            this.context = context;
            this.emailAddr = emailAddr;
        }

        @Override
        public void onClick(View widget) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailAddr));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "意见反馈");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddr);
            context.getApplicationContext().startActivity(Intent.createChooser(emailIntent, "发送邮件"));
        }
    }
}
