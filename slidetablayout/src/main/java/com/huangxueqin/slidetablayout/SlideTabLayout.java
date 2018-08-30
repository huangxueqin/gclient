package com.huangxueqin.slidetablayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by huangxueqin on 2017/1/23.
 */

public class SlideTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    private static final String TAG = SlideTabLayout.class.getSimpleName();

    // cons for attr "tabGravity"
    public static final int GRAVITY_LEFT = 1;
    public static final int GRAVITY_RIGHT = 2;
    public static final int GRAVITY_CENTER = 3;

    // cons for attr "tabMode"
    public static final int MODE_FIT_CONTENT = 1;
    public static final int MODE_FIT_PARENT = 2;
    public static final int MODE_FIX_WIDTH = 3;

    // cons for attr "tabStyle"
    public static final int STYLE_PLAIN_TEXT = 1;
    public static final int STYLE_PLAIN_ICON = 2;
    public static final int STYLE_ICON_TEXT = 3;
    public static final int STYLE_CUSTOM = 4;

    // cons for attr tabIndicatorStyle
    public static final int STYLE_LINE_FIX_WIDTH = 1;
    public static final int STYLE_LINE_FIT_CONTENT = 2;
    public static final int STYLE_LINE_FILL_PARENT = 3;

    private static final int COLUMN_TAB_GRAVITY = R.styleable.SlideTabLayout_tab_gravity;
    private static final int COLUMN_TAB_MODE = R.styleable.SlideTabLayout_tab_mode;
    private static final int COLUMN_TAB_STYLE = R.styleable.SlideTabLayout_tab_style;
    private static final int COLUMN_TAB_WIDTH = R.styleable.SlideTabLayout_tab_width;
    private static final int COLUMN_TAB_TITLE_COLOR = R.styleable.SlideTabLayout_tab_title_color;
    private static final int COLUMN_TAB_TITLE_SIZE = R.styleable.SlideTabLayout_tab_title_size;
    private static final int COLUMN_TAB_INDICATOR_COLOR = R.styleable.SlideTabLayout_tab_indicator_color;
    private static final int COLUMN_TAB_INDICATOR_SIZE = R.styleable.SlideTabLayout_tab_indicator_size;
    private static final int COLUMN_TAB_INDICATOR_WIDTH = R.styleable.SlideTabLayout_tab_indicator_width;
    private static final int COLUMN_TAB_INDICATOR_HEIGHT = R.styleable.SlideTabLayout_tab_indicator_height;
    private static final int COLUMN_TAB_INDICATOR_BOTTOM = R.styleable.SlideTabLayout_tab_indicator_bottom;
    private static final int COLUMN_TAB_INDICATOR_STYLE = R.styleable.SlideTabLayout_tab_indicator_style;


    private static final LayoutParams TAB_PARENT_LP =
            new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private static final float INDICATOR_HEIGHT = 1.5f;
    private static final float INDICATOR_BOTTOM = 5;

    private LayoutInflater mTabCellInflater;
    private LinearLayout mTabParent;
    private int mTabGravity;
    private int mTabMode;
    private int mTabStyle;

    private int mTabWidth;
    private int mTabTitleColor;
    private int mTabTitleSize;

    private int mTabIndicatorColor;
    private int mTabIndicatorStyle;
    private int mTabIndicatorWidth;
    private int mTabIndicatorHeight;
    private int mTabIndicatorBottom;
    private Paint mIndicatorPaint;

    private Adapter mAdapter;
    private TabSelectListener mListener;
    private int mCurrentPosition = 0;

    private boolean mIdle = true;
    private int mScrollPosition;
    private float mScrollPositionOffset;

    public SlideTabLayout(Context context) {
        this(context, null);
    }

    public SlideTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);

        float density = context.getResources().getDisplayMetrics().density;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideTabLayout);
        mTabGravity = ta.getInt(COLUMN_TAB_GRAVITY, GRAVITY_CENTER);
        mTabMode = ta.getInt(COLUMN_TAB_MODE, MODE_FIT_PARENT);
        mTabStyle = ta.getInt(COLUMN_TAB_STYLE, STYLE_PLAIN_TEXT);
        mTabWidth = ta.getDimensionPixelSize(COLUMN_TAB_WIDTH, -1);
        mTabTitleColor = ta.getColor(COLUMN_TAB_TITLE_COLOR, Color.BLACK);
        mTabTitleSize = ta.getDimensionPixelSize(COLUMN_TAB_TITLE_SIZE, -1);
        mTabIndicatorColor = ta.getColor(COLUMN_TAB_INDICATOR_COLOR, Color.BLACK);
        int indicatorSize = ta.getDimensionPixelSize(COLUMN_TAB_INDICATOR_SIZE, -1);
        mTabIndicatorWidth = ta.getDimensionPixelOffset(COLUMN_TAB_INDICATOR_WIDTH, indicatorSize);
        mTabIndicatorHeight = ta.getDimensionPixelOffset(COLUMN_TAB_INDICATOR_HEIGHT, (indicatorSize > 0 ? indicatorSize : (int) (density * INDICATOR_HEIGHT)));
        mTabIndicatorBottom = ta.getDimensionPixelOffset(COLUMN_TAB_INDICATOR_BOTTOM, (int)(density* INDICATOR_BOTTOM));
        mTabIndicatorStyle = ta.getInt(COLUMN_TAB_INDICATOR_STYLE, STYLE_LINE_FIT_CONTENT);
        ta.recycle();

        mTabCellInflater = LayoutInflater.from(context);
        // create {@link mTabParent}
        mTabParent = new LinearLayout(context);
        mTabParent.setOrientation(LinearLayout.HORIZONTAL);
        super.addView(mTabParent, -1, TAB_PARENT_LP);

        initTabIndicatorPaint();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateLayoutConstraints();
    }

    private void updateLayoutConstraints() {
        if (mTabGravity == GRAVITY_CENTER) {
            mTabParent.setGravity(Gravity.CENTER);
        } else if (mTabGravity == GRAVITY_LEFT) {
            mTabParent.setGravity(Gravity.LEFT);
        } else if (mTabGravity == GRAVITY_RIGHT) {
            mTabParent.setGravity(Gravity.RIGHT);
        }

        for (int i = 0; i < mTabParent.getChildCount(); i++) {
            TabCellView cell = (TabCellView) mTabParent.getChildAt(i);
            LinearLayout.LayoutParams cellLp = (LinearLayout.LayoutParams) cell.getLayoutParams();
            cellLp.height = ViewPager.LayoutParams.MATCH_PARENT;
            if (mTabMode == MODE_FIT_PARENT) {
                cellLp.width = 0;
                cellLp.weight = 1;
            } else if (mTabMode == MODE_FIX_WIDTH && mTabWidth > 0) {
                cellLp.width = mTabWidth;
            }
            cell.setLayoutParams(cellLp);
        }
    }

    private void initTabIndicatorPaint() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStyle(Paint.Style.STROKE);
        mIndicatorPaint.setStrokeWidth(mTabIndicatorHeight);
        mIndicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        mIndicatorPaint.setColor(mTabIndicatorColor);
    }

    @Override
    public void addView(View child) {
        this.addView(child, -1);
    }

    @Override
    public void addView(View child, int index) {
        this.addView(child, index, null);
    }

    @Override
    public void addView(View child, int width, int height) {
        this.addView(child, -1, new ViewGroup.LayoutParams(width, height));
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        TabCellView tcv;
        if (!(child instanceof TabCellView)) {
            tcv = new TabCellView(getContext());
            tcv.addView(child);
        } else {
            tcv = (TabCellView) child;
        }
        tcv.setOnClickListener(mTabClickListener);
        tcv.index = mTabParent.getChildCount();
        tcv.setSelected(tcv.index == mCurrentPosition);
        if (mTabStyle == STYLE_PLAIN_TEXT || mTabStyle == STYLE_ICON_TEXT) {
            tcv.title.setTextColor(mTabTitleColor);
            tcv.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTitleSize);
        }
        mTabParent.addView(tcv, index);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return generateDefaultLayoutParams();
    }

    private TabCellView getTabCellByIndex(int index) {
        for (int i = 0, count = mTabParent.getChildCount(); i < count; i++) {
            TabCellView tcv = (TabCellView) mTabParent.getChildAt(i);
            if (tcv.index == index) {
                return tcv;
            }
        }
        return null;
    }

    private void drawIndicator(Canvas canvas) {
        if (mTabParent.getChildCount() <= 0) {
            return;
        }

        TabCellView tcv = getTabCellByIndex(mScrollPosition);
        if (tcv == null) {
            return;
        }

//        Log.d(TAG, "scrollPosition = " + mScrollPosition + ", offset = " + mScrollPositionOffset);

        final int top = getHeight()-mTabIndicatorBottom/2;
        final int bottom = top;
        final int start, end;

        final int tabWidth = tcv.getWidth();
        final int tabCenter = tcv.getLeft() + tabWidth/2;

        if (mIdle || Math.abs(mScrollPositionOffset) < 0.001) {
            if (mTabIndicatorStyle == STYLE_LINE_FIT_CONTENT) {
                start = tabCenter-tabWidth/4;
                end = tabCenter+tabWidth/4;
            } else if (mTabIndicatorStyle == STYLE_LINE_FIX_WIDTH && mTabIndicatorWidth > 0) {
                start = tabCenter - mTabIndicatorWidth/2;
                end = tabCenter + mTabIndicatorWidth/2;
            } else {
                start = tcv.getLeft();
                end = tcv.getRight();
            }
        } else {
            final int nextIndex = mScrollPosition + (mScrollPositionOffset > 0 ? 1 : -1);
//            Log.d(TAG, "nextIndex = " + nextIndex);
            final TabCellView nextTcv = getTabCellByIndex(nextIndex);
            final int nextTabWidth = nextTcv.getWidth();
            final int nextTabCenter = nextTcv.getLeft() + nextTabWidth/2;
            final int newTabCenter = (int) (tabCenter + mScrollPositionOffset*(nextTabCenter-tabCenter));
            if (mTabIndicatorStyle == STYLE_LINE_FIT_CONTENT) {
                final int width = (int) (tabWidth/2 + mScrollPositionOffset*(nextTabWidth-tabWidth)/2);
                start = newTabCenter-width/2;
                end = newTabCenter+width/2;
            } else if (mTabIndicatorStyle == STYLE_LINE_FIX_WIDTH && mTabIndicatorWidth > 0) {
                start = newTabCenter-mTabIndicatorWidth/2;
                end = newTabCenter+mTabIndicatorWidth/2;
            } else {
                start = (int) (tcv.getLeft() + mScrollPositionOffset*(nextTcv.getLeft()-tcv.getLeft()));
                end = (int) (tcv.getRight() + mScrollPositionOffset*(nextTcv.getRight()-tcv.getRight()));
            }
        }

        canvas.drawLine(start, top, end, bottom, mIndicatorPaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawIndicator(canvas);
    }

    public void setTabGravity(int gravity) {
        if (mTabGravity != gravity) {
            mTabGravity = gravity;
            updateLayoutConstraints();
        }
    }

    public void setTabMode(int tabMode) {
        if (mTabMode != tabMode) {
            mTabMode = tabMode;
            updateLayoutConstraints();
        }
    }

    public void setAdapter(Adapter adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
            mTabParent.removeAllViews();
            installTabs();
            updateLayoutConstraints();
        }
    }

    private void installTabs() {
        final int tabCount = mAdapter.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabCellView cell = null;
            if (mTabStyle == STYLE_CUSTOM) {
                cell = new TabCellView(getContext());
                cell.addView(mAdapter.createTabView(i));
            } else {
                cell = getDefaultTabView(i);
            }
            addView(cell);
        }
    }

    public void setCurrentItem(int position) {
        if (mCurrentPosition != position) {
            TabCellView prevPrimaryItem = getTabCellByIndex(mCurrentPosition);
            TabCellView currPrimaryItem = getTabCellByIndex(position);
            if (prevPrimaryItem != null) {
                prevPrimaryItem.setSelected(false);
            }
            if (currPrimaryItem != null) {
                currPrimaryItem.setSelected(true);
            }
            mCurrentPosition = position;

            if (mListener != null) {
                mListener.onTabSelected(position);
            }
        }
    }

    public void setTabSelectListener(TabSelectListener listener) {
        mListener = listener;
    }

    private OnClickListener mTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TabCellView currentCell = getTabCellByIndex(mCurrentPosition);
            if (v != currentCell) {
                mCurrentPosition = ((TabCellView)v).index;
                currentCell.setSelected(false);
                v.setSelected(true);
                if (mListener != null) {
                    mListener.onTabSelected(mCurrentPosition);
                }
            }
        }
    };

    private TabCellView getDefaultTabView(int position) {
        TabCellView cell = new TabCellView(getContext());
        View v;
        if (mTabStyle == STYLE_PLAIN_TEXT) {
            v = mTabCellInflater.inflate(R.layout.view_slide_tab_plain_text, null);
        } else if (mTabStyle == STYLE_PLAIN_ICON) {
            v = mTabCellInflater.inflate(R.layout.view_slide_tab_plain_icon, null);
        } else {
            v = mTabCellInflater.inflate(R.layout.view_slide_tab_icon_text, null);
        }
        cell.addView(v);

        String title = mAdapter.getTitle(position);
        Drawable icon = mAdapter.getIcon(position);
        if (cell.title != null) {
            cell.title.setText(mAdapter.getTitle(position));
        }

        if (cell.icon != null) {
            cell.icon.setImageDrawable(mAdapter.getIcon(position));
        }

        return cell;
    }

    /**
     * implementation of {@link ViewPager.OnPageChangeListener}
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mScrollPosition = position;
        mScrollPositionOffset = positionOffset;
        invalidate();
    }

    private void scrollTabToVisible(int index) {
        View tab = getTabCellByIndex(index);
        final int left = tab.getLeft();
        final int right = tab.getRight();
        final int leftEdge = getScrollX();
        final int rightEdge = leftEdge + getMeasuredWidth();
        int offsetX = 0;
        if (left < leftEdge) {
            offsetX = left - leftEdge;
        } else if (right > rightEdge) {
            offsetX = right - rightEdge;
        }

        Log.d("TAG", "scrollTabToVisible: offsetX = " + offsetX);

        if (offsetX != 0) {
            smoothScrollBy(offsetX, 0);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("TAG", "onPageSelected: position = " + position);
        mCurrentPosition = position;
        if (canScrollHorizontally(1) || canScrollHorizontally(-1)) { // canScrollVertically(-1) || canScrollVertically(1)
            scrollTabToVisible(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mIdle = state == ViewPager.SCROLL_STATE_IDLE;
        if (mIdle) {
            invalidate();
        }
    }

    public static abstract class Adapter {
        public abstract int getTabCount();

        public View createTabView(int tabNdx) { return null; }

        public String getTitle(int tabNdx) { return null; }

        public Drawable getIcon(int tabNdx) { return null; }
    }

    public interface TabSelectListener {
        void onTabSelected(int tabNdx);
    }

    private class TabCellView extends FrameLayout {
        int index;
        TextView title;
        ImageView icon;

        public TabCellView(@NonNull Context context) {
            super(context);
        }

        @Override
        public void addView(View child) {
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            child.setLayoutParams(lp);
            super.addView(child);
            View t = child.findViewById(R.id.title);
            View i = child.findViewById(R.id.icon);

            if (t instanceof TextView) {
                title = (TextView) t;
            }

            if (i instanceof ImageView) {
                icon = (ImageView) i;
            }
        }

        public void setSelected(boolean selected) {
            View child = getChildAt(0);
            child.setSelected(selected);
            if (title != null) {
                title.setSelected(selected);
            }
            if (icon != null) {
                icon.setSelected(selected);
            }
        }
    }
}
