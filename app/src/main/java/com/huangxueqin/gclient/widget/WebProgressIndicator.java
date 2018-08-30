package com.huangxueqin.gclient.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.huangxueqin.gclient.utils.DisplayUtil;

/**
 * Created by huangxueqin on 2017/2/4.
 */

public class WebProgressIndicator extends View {
    /**
     * value from 0.0f to 1.0f
     */
    private float mProgress;

    private Paint mProgressLinePaint;

    public WebProgressIndicator(Context context) {
        this(context, null);
    }

    public WebProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mProgressLinePaint = new Paint();
        mProgressLinePaint.setStyle(Paint.Style.FILL);
        mProgressLinePaint.setColor(DisplayUtil.getPrimaryColor(getContext()));
    }

    public void setProgress(float progress) {
        if (mProgress != progress) {
            mProgress = progress;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw progress line
        int w = getWidth();
        int h = getHeight();
        mProgressLinePaint.setAlpha(128+(int)(128*mProgress));
        canvas.drawRect(0, 0, (int)(w*mProgress+0.5), h, mProgressLinePaint);
    }
}
