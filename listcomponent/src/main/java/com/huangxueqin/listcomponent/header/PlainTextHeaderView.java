package com.huangxueqin.listcomponent.header;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.huangxueqin.listcomponent.R;

/**
 * Created by huangxueqin on 2017/7/8.
 */

public class PlainTextHeaderView extends FrameLayout implements IRefreshHeader {
    private static final int DEFAULT_TITLE_SIZE_SP = 13;
    private static final int DEFAULT_TITLE_COLOR = Color.GRAY;

    private TextView mTitle;
    private SpinKitView mSpinner;
    private float mTitleSize;
    private int mTitleColor;
    private int mSpinnerColor;

    private int mCurrentStringId;

    public PlainTextHeaderView(Context context) {
        this(context, null);
    }

    public PlainTextHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlainTextHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            initAttrs(context, attrs);
        }
        createTitleView(context);
    }

    private void createTitleView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_plain_text_header, this, false);
        addView(v);
        mTitle = (TextView) v.findViewById(R.id.title);
        mSpinner = (SpinKitView) v.findViewById(R.id.spinner);
        mTitle.setTextColor(mTitleColor);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
        mTitle.setText(R.string.refresh_prompt_pull_down);
        mTitle.setGravity(Gravity.CENTER);
        mSpinner.setColor(mSpinnerColor);
        mSpinner.setVisibility(GONE);
        mCurrentStringId = R.string.refresh_prompt_pull_down;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        Resources res = context.getResources();
        int defaultTitleSize = (int) (DEFAULT_TITLE_SIZE_SP * res.getDisplayMetrics().scaledDensity);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlainTextHeaderView);
        mTitleSize = ta.getDimensionPixelSize(R.styleable.PlainTextHeaderView_titleSize, defaultTitleSize);
        mTitleColor = ta.getColor(R.styleable.PlainTextHeaderView_titleColor, DEFAULT_TITLE_COLOR);
        mSpinnerColor = ta.getColor(R.styleable.PlainTextHeaderView_spinnerColor, DEFAULT_TITLE_COLOR);
        ta.recycle();
    }

    @Override
    public void onPullDown(int offset, int refreshDist) {
        final int stringId;
        if (offset < refreshDist) {
            stringId = R.string.refresh_prompt_pull_down;
        } else {
            stringId = R.string.refresh_prompt_release;
        }
        // avoid request layout frequently
        mSpinner.setVisibility(GONE);
        if (mCurrentStringId != stringId) {
            mCurrentStringId = stringId;
            mTitle.setText(stringId);
        }
    }

    @Override
    public void onRefreshing() {
        if (mCurrentStringId != R.string.refresh_ongoing) {
            mCurrentStringId = R.string.refresh_ongoing;
            mTitle.setText(R.string.refresh_ongoing);
        }
        mSpinner.setVisibility(VISIBLE);
    }

    @Override
    public void onRefreshDone() {
        if (mCurrentStringId != R.string.refresh_complete) {
            mCurrentStringId = R.string.refresh_complete;
            mTitle.setText(R.string.refresh_complete);
        }
        mSpinner.setVisibility(GONE);
    }

    @Override
    public void onRefreshFail() {
        // do nothing
    }
}
