package com.huangxueqin.commontitlebar;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import static com.huangxueqin.commontitlebar.OptionItem.TYPE_ICON;
import static com.huangxueqin.commontitlebar.OptionItem.TYPE_MORE;
import static com.huangxueqin.commontitlebar.OptionItem.TYPE_TEXT;

public class OptionButton extends FrameLayout {

    AppCompatImageView mIconView;
    TextView mTextView;
    View mRedDot;

    OptionItem mOptionItem;

    public OptionButton(@NonNull Context context, OptionItem item) {
        super(context);
        mOptionItem = item;
        mIconView = new AppCompatImageView(context);
        mIconView.setScaleType(ImageView.ScaleType.CENTER);
        addView(mIconView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setLines(1);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        int padding = DisplayUtil.dp2px(context, 5);
        mTextView.setPadding(padding, padding, padding, padding);
        addView(mTextView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        updateViews(context);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mIconView.setOnClickListener(l);
        mTextView.setOnClickListener(l);
    }

    private void updateViews(Context context) {
        OptionItem item = mOptionItem;
        mIconView.setVisibility(item.type == TYPE_ICON || item.type == TYPE_MORE ? VISIBLE : GONE);
        mTextView.setVisibility(mOptionItem.type == TYPE_TEXT ? VISIBLE : GONE);
        final int type = item.type;
        if (type == OptionItem.TYPE_TEXT) {
            mTextView.setText(item.text);
            mTextView.setTextAppearance(context, item.textAppearance);
        } else if (type == OptionItem.TYPE_ICON || type == OptionItem.TYPE_MORE) {
            mIconView.setImageResource(item.iconId);
        }
    }

    public void setOptionItem(@NonNull OptionItem item) {
        if (mOptionItem != item) {
            mOptionItem = item;
            updateViews(getContext());
        }
    }

    public void setIcon(@DrawableRes int iconRes) {
        mIconView.setImageResource(iconRes);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public int getType() {
        return mOptionItem.type;
    }

    public void showRedDot() {

    }

    public void hideRedDot() {

    }
}
