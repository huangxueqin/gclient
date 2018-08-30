package com.huangxueqin.commontitlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangxueqin.commontitlebar.theme.AppearanceConf;

/**
 * Created by huangxueqin on 2018/3/26.
 */

public class GeneralTitleBar extends LinearLayout implements View.OnClickListener {

    private static final int TITLE_STYLE_DEFAULT = 0;
    private static final int TITLE_STYLE_SEARCH = 1;
    private static final int TITLE_STYLE_CUSTOM = 20;

    // views
    ImageView mBackButton;
    ImageView mCloseButton;
    View mDividerBar;
    ImageView mLeftButton;
    View mContentView;
    TextView mTitleView;
    OptionButton mMainOptionButton;
    OptionButton mSubOptionButton;

    // configs
    int mStyle;
    boolean mShowBackButton;
    boolean mShowCloseButton;
    boolean mShowSeparator;
    int mContentLayoutId;
    int mLeftIconId;
    int mTitleViewId;
    String mInitTitleText;
    String mMainOptionText;
    int mMainOptionTextStyle;
    int mMainOptionIcon;
    String mSubOptionText;
    int mSubOptionTextStyle;
    int mSubOptionIcon;
    // appearance
    private AppearanceConf mAppearanceConf;

    private TitleActionListener mActionListener;
    private OptionItem mMainOptionItem;
    private OptionItem mSubOptionItem;


    public GeneralTitleBar(Context context) {
        this(context, null);
    }

    public GeneralTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GeneralTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);
        setPadding(0, 0, DisplayUtil.dp2px(context, 5), 0);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GeneralTitleBar);
        // parse appearance
        mAppearanceConf = ThemeUtil.loadAppearanceConf(context, ta);
        // parse config
        mStyle = ta.getInt(R.styleable.GeneralTitleBar_gtbStyle, TITLE_STYLE_DEFAULT);
        if (mStyle == TITLE_STYLE_CUSTOM) {
            mTitleViewId = ta.getResourceId(R.styleable.GeneralTitleBar_titleViewId, 0);
            mContentLayoutId = ta.getResourceId(R.styleable.GeneralTitleBar_contentLayout, 0);
        }
        mShowBackButton = ta.getBoolean(R.styleable.GeneralTitleBar_showBack, false);
        mShowCloseButton = ta.getBoolean(R.styleable.GeneralTitleBar_showClose, false);
        mShowSeparator = ta.getBoolean(R.styleable.GeneralTitleBar_showSeparator, false);
        mLeftIconId = ta.getResourceId(R.styleable.GeneralTitleBar_leftIcon, 0);
        mInitTitleText = ta.getString(R.styleable.GeneralTitleBar_titleText);
        mMainOptionText = ta.getString(R.styleable.GeneralTitleBar_mainOptionText);
        mMainOptionTextStyle = ta.getResourceId(R.styleable.GeneralTitleBar_mainOptionTextStyle, mAppearanceConf.mainOptionTextStyle);
        mMainOptionIcon = ta.getResourceId(R.styleable.GeneralTitleBar_mainOptionIcon, 0);
        mSubOptionText = ta.getString(R.styleable.GeneralTitleBar_subOptionText);
        mSubOptionTextStyle = ta.getResourceId(R.styleable.GeneralTitleBar_subOptionTextStyle, mAppearanceConf.subOptionTextStyle);
        mSubOptionIcon = ta.getResourceId(R.styleable.GeneralTitleBar_subOptionIcon, 0);
        ta.recycle();

        initLeftSideItems(context);
        initLeftButton(context);
        initContentView(context);
        initOptionButtons(context);

        ThemeUtil.applyAppearanceConf(this, mAppearanceConf);
    }

    /**
     * init left items e.g. back button, close button, separator ...
     * @param context
     */
    private void initLeftSideItems(Context context) {
        int index = 0;
        if (mShowBackButton) {
            mBackButton = generateTitleIconButton(context, 0);
            addView(mBackButton, index);
            mBackButton.setOnClickListener(this);
            ++index;
        }
        if (mShowCloseButton) {
            mCloseButton = generateTitleIconButton(context, 0);
            addView(mCloseButton, index);
            mCloseButton.setOnClickListener(this);
            ++index;
        }
        if ((mShowBackButton || mShowCloseButton) && mShowSeparator) {
            mDividerBar = new View(context);
            LayoutParams lp = generateDefaultLayoutParams();
            lp.rightMargin = DisplayUtil.dp2px(context, 10);
            addView(mDividerBar, index, lp);
            ++index;
        }
    }

    private void initLeftButton(Context context) {
        if (mLeftIconId > 0) {
            mLeftButton = generateTitleIconButton(context, mLeftIconId);
            addView(mLeftButton);
            mLeftButton.setOnClickListener(this);
        }
    }

    /**
     * init main content view according to {@link #mStyle}
     */
    private void initContentView(Context context) {
        if (mStyle == TITLE_STYLE_DEFAULT) {
            mContentView = mTitleView = generateTitleContentView(context);
        } else if (mStyle == TITLE_STYLE_SEARCH) {
            mContentView = generateSearchContentView(context);
        } else if (mStyle == TITLE_STYLE_CUSTOM) {
            if (mContentLayoutId > 0) {
                mContentView = LayoutInflater.from(context).inflate(mContentLayoutId, this, false);
                if (mTitleViewId > 0) {
                    mTitleView = mContentView.findViewById(mTitleViewId);
                }
            } else {
                mContentView = new View(context);
            }
        }
        if (mTitleView != null) {
            mTitleView.setText(mInitTitleText);
        }
        addView(mContentView, generateContentViewLayoutParam());
    }

    private void initOptionButtons(Context context) {
        int index = indexOfChild(mContentView) + 1;
        // init sub option button
        if (!TextUtils.isEmpty(mSubOptionText) || mSubOptionIcon > 0) {
            int type;
            if (mSubOptionIcon > 0) {
                type = OptionItem.TYPE_ICON;
            } else {
                type = OptionItem.TYPE_TEXT;
            }
            mSubOptionItem = new OptionItem(type, mSubOptionIcon, mSubOptionText, mSubOptionTextStyle);
            mSubOptionButton = new OptionButton(context, mSubOptionItem);
            addView(mSubOptionButton, index);
            mSubOptionButton.setOnClickListener(this);
            ++index;
        }
        if (!TextUtils.isEmpty(mMainOptionText) || mMainOptionIcon > 0) {
            int type;
            if (mMainOptionIcon > 0) {
                type = OptionItem.TYPE_ICON;
            } else {
                type = OptionItem.TYPE_TEXT;
            }
            mMainOptionItem = new OptionItem(type, mMainOptionIcon, mMainOptionText, mMainOptionTextStyle);
            mMainOptionButton = new OptionButton(context, mMainOptionItem);
            addView(mSubOptionButton, index);
            mMainOptionButton.setOnClickListener(this);
            ++index;
        }
    }

    private void validateOptionItem(OptionItem item, boolean isMain) {
        if (item.textAppearance <= 0) {
            item.textAppearance = isMain ? mAppearanceConf.mainOptionTextStyle : mAppearanceConf.subOptionTextStyle;
        }
    }

    private AppCompatImageView generateTitleIconButton(Context context, int iconRes) {
        AppCompatImageView imageView = new AppCompatImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(iconRes);
        return imageView;
    }

    private TextView generateTitleContentView(Context context) {
        TextView titleView = new TextView(context);
        titleView.setLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        return titleView;
    }

    private View generateSearchContentView(Context context) {
        // FIXME: 2018/4/15 not implemented yet
        return new View(context);
    }

    private LayoutParams generateContentViewLayoutParam() {
        LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        return lp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updateChildViewLayoutParams(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateChildViewLayoutParams(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // update 添加时没有设置 Layout 的 View
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child == mBackButton || child == mCloseButton || child == mLeftButton) {
                lp.width = lp.height = Math.min(width, height);
            } else if (child == mDividerBar) {
                lp.width = 1;
                lp.height = Math.min(width, height) / 2;
            } else if (child instanceof OptionButton) {
                lp.height = Math.min(width, height);
                int type = ((OptionButton) child).getType();
                if (type == OptionItem.TYPE_ICON || type == OptionItem.TYPE_MORE) {
                    lp.width = Math.min(width, height);
                } else {
                    lp.width = LayoutParams.WRAP_CONTENT;
                }
            }
        }
        // update ContentView 边距
        if (mContentView != null) {
            // 如果 mContentView 在最左边 contentView.index == 0
            int leftMargin = indexOfChild(mContentView) == 0 ? DisplayUtil.dp2px(getContext(), 10) : 0;
            // 如果 mContentView 在最右边
            int rightMargin = indexOfChild(mContentView) == getChildCount()-1 ? DisplayUtil.dp2px(getContext(), 10) : 0;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
            lp.leftMargin = leftMargin;
            lp.rightMargin = rightMargin;
        }
    }

    @Override
    public void onClick(View v) {
        if (mActionListener == null) {
            return;
        }
        if (v == mBackButton) {
            Context context = getContext();
            if (context instanceof Activity) {
                ((Activity) context).onBackPressed();
            } else {
                mActionListener.onAction(mBackButton, TitleAction.BACK);
            }
        } else if (v == mCloseButton) {
            mActionListener.onAction(mCloseButton, TitleAction.CLOSE);
        } else if (v == mLeftButton) {
            mActionListener.onAction(mLeftButton, TitleAction.LEFT_BUTTON);
        } else if (v == mMainOptionButton || v.getParent() == mMainOptionButton) {
            mActionListener.onAction(mMainOptionButton, TitleAction.MAIN_OPTION_BUTTON);
        } else if (v == mSubOptionButton || v.getParent() == mSubOptionButton) {
            mActionListener.onAction(mSubOptionButton, TitleAction.SUB_OPTION_BUTTON);
        }
    }

    public void setTitle(String text) {
        if (mTitleView != null) {
            mTitleView.setText(text);
        }
    }

    public void setTitle(@StringRes int textRes) {
        if (mTitleView != null) {
            mTitleView.setText(textRes);
        }
    }

    public void setTitleColor(@ColorInt int color) {
        mAppearanceConf.titleColor = color;
        ThemeUtil.applyTitleViewAppearanceConf(this, mAppearanceConf);
    }

    public void setTitleSize(@Dimension(unit = Dimension.PX) int textSize) {
        mAppearanceConf.titleSize = textSize;
        ThemeUtil.applyTitleViewAppearanceConf(this, mAppearanceConf);
    }

    public void setTitleActionListener(TitleActionListener listener) {
        mActionListener = listener;
    }

    public void setBackButtonVisibility(boolean visible) {
        if (visible) {
            if (mBackButton != null) {
                mBackButton.setVisibility(VISIBLE);
            } else {
                mBackButton = generateTitleIconButton(getContext(), 0);
                addView(mBackButton, 0);
                ThemeUtil.applyBackButtonAppearanceConf(this, mAppearanceConf);
            }
        } else if (mBackButton != null) {
            mBackButton.setVisibility(GONE);
        }
    }

    public void setCloseButtonVisibility(boolean visible) {
        if (visible) {
            if (mCloseButton != null) {
                mCloseButton.setVisibility(VISIBLE);
            } else {
                mCloseButton = generateTitleIconButton(getContext(), 0);
                // find index to insert
                int index = mBackButton == null ? 0 : indexOfChild(mBackButton)+1;
                addView(mCloseButton, index);
                ThemeUtil.applyBackButtonAppearanceConf(this, mAppearanceConf);
            }
        } else if (mCloseButton != null) {
            mCloseButton.setVisibility(GONE);
        }
    }

    private boolean shouldShowSeparator() {
        return mBackButton != null && mBackButton.getVisibility() == VISIBLE ||
                mCloseButton != null && mCloseButton.getVisibility() == VISIBLE;
    }

    private int findSeparatorViewIndex() {
        int index = 0;
        if (mBackButton != null) ++index;
        if (mCloseButton != null) ++index;
        return index;
    }

    public void setSeparatorViewVisibility(boolean visible) {
        if (visible && shouldShowSeparator()) {
            if (mDividerBar != null) {
                mDividerBar.setVisibility(VISIBLE);
            } else {
                mDividerBar = new View(getContext());
                addView(mDividerBar, findSeparatorViewIndex());
                ThemeUtil.applyDividerBarAppearanceConf(this, mAppearanceConf);
            }
        } else if (mDividerBar != null) {
            mDividerBar.setVisibility(GONE);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    public void setOptionButtons(@NonNull OptionItem mainOptionItem, @NonNull OptionItem subOptionItem) {
        if (mMainOptionButton != null) {
            removeView(mMainOptionButton);
        }
        if (mSubOptionButton != null) {
            removeView(mSubOptionButton);
        }
        int index = indexOfChild(mContentView)+1;
        mSubOptionItem = subOptionItem;
        validateOptionItem(mSubOptionItem, false);
        mSubOptionButton = new OptionButton(getContext(), subOptionItem);
        addView(mSubOptionButton, index);
        mSubOptionButton.setOnClickListener(this);
        ++index;
        mMainOptionItem = mainOptionItem;
        validateOptionItem(mMainOptionItem, true);
        mMainOptionButton = new OptionButton(getContext(), mainOptionItem);
        addView(mMainOptionButton, index);
        mMainOptionButton.setOnClickListener(this);
    }

    public void setOptionButton(@NonNull OptionItem optionItem) {
        if (mMainOptionButton != null) {
            removeView(mMainOptionButton);
        }
        if (mSubOptionButton != null) {
            removeView(mSubOptionButton);
        }
        mSubOptionItem = null;
        mSubOptionButton = null;
        mMainOptionItem = optionItem;
        validateOptionItem(mMainOptionItem, true);
        mMainOptionButton = new OptionButton(getContext(), optionItem);
        addView(mMainOptionButton, indexOfChild(mContentView));
        mMainOptionButton.setOnClickListener(this);
    }

    public void setMainOptionButtonText(String text) {
        if (mMainOptionItem != null && mMainOptionItem.type == OptionItem.TYPE_TEXT && mMainOptionButton != null) {
            mMainOptionItem.text = text;
            mMainOptionButton.setOptionItem(mMainOptionItem);
        }
    }

    public void setMainOptionButtonIcon(int iconRes) {
        if (mMainOptionItem != null && mMainOptionItem.type == OptionItem.TYPE_ICON && mMainOptionButton != null) {
            mMainOptionItem.iconId = iconRes;
            mMainOptionButton.setOptionItem(mMainOptionItem);
        }
    }

    public void setSubOptionButtonText(String text) {
        if (mSubOptionItem != null && mSubOptionItem.type == OptionItem.TYPE_TEXT && mSubOptionButton != null) {
            mSubOptionItem.text = text;
            mSubOptionButton.setOptionItem(mSubOptionItem);
        }
    }

    public void setSubOptionButtonIcon(int iconRes) {
        if (mSubOptionItem != null && mSubOptionItem.type == OptionItem.TYPE_ICON && mSubOptionButton != null) {
            mSubOptionItem.iconId = iconRes;
            mSubOptionButton.setOptionItem(mSubOptionItem);
        }
    }
}
