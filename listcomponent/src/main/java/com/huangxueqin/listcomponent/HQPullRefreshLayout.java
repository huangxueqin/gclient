package com.huangxueqin.listcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.huangxueqin.listcomponent.header.IRefreshHeader;

import java.util.Arrays;

/**
 * Created by huangxueqin on 2017/7/5.
 */

public class HQPullRefreshLayout extends FrameLayout implements NestedScrollingParent, NestedScrollingChild {
    private static final String TAG = "PullRefreshLayout";

    private static final int STATE_SET = 0;
    private static final int STATE_PULL_DOWN = 1;
    private static final int STATE_REFRESHING = 2;
    private static final int STATE_REFRESH_SUCCESS = 3;
    private static final int STATE_REFRESH_FAIL = 4;

    private static final int INVALID_POINTER_ID = -1;

    private static final int ANIMATION_DURATION = 200;

    public interface OnRefreshListener {
        void onRefresh();
    }

    private View mTarget;
    private View mHeader;
    private int mHeaderLayoutId;
    private int mHeaderId;
    private int mOriginTargetTop = 0;
    private int mTargetIndex = -1;
    private boolean mHeaderPinned = false;
    private int mHeaderPinPosition = 0;

    private OnRefreshListener mRefreshListener;
    private boolean mEnableRefresh = true;

    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastMotionY;
    private float mTotalDraggedOffset;

    private int mRefreshOffset;
    private int mCurrentOffset = Integer.MIN_VALUE;

    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    private Runnable mScrollRunnable;
    private Scroller mScroller;

    private int mState = STATE_SET;

    private int mOffsetCoefficient = 500;
    private int[] mOffsetTable = new int[100];

    public HQPullRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public HQPullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HQPullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        if (mHeaderLayoutId > 0) {
            createHeaderView();
        }
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mScroller = new Scroller(context, new DecelerateInterpolator());
        setNestedScrollingEnabled(true);
        setChildrenDrawingOrderEnabled(true);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HQPullRefreshLayout);
        mHeaderLayoutId = ta.getResourceId(R.styleable.HQPullRefreshLayout_headerViewLayoutId, -1);
        mHeaderId = ta.getResourceId(R.styleable.HQPullRefreshLayout_headerViewId, -1);
        mRefreshOffset = ta.getDimensionPixelSize(R.styleable.HQPullRefreshLayout_refreshOffset, -1);
        mHeaderPinned = ta.getBoolean(R.styleable.HQPullRefreshLayout_pinHeader, false);
        mHeaderPinPosition = ta.getDimensionPixelSize(R.styleable.HQPullRefreshLayout_headerPinPosition, 0);
        ta.recycle();
    }

    private void createHeaderView() {
        mHeader = LayoutInflater.from(getContext()).inflate(mHeaderLayoutId, this, false);
        addView(mHeader);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeader == null && mHeaderId > 0) {
            mHeader = findViewById(mHeaderId);
        }
        if (mHeader == null) {
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof IRefreshHeader) {
                    mHeader = child;
                    break;
                }
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mTargetIndex < 0) {
            return i;
        }
        if (i == childCount-1) {
            return mTargetIndex;
        } else if (i > mTargetIndex) {
            return i+1;
        } else {
            return i;
        }
    }

    private void ensureTarget() {
        if(mTarget == null) {
            for(int i = 0; i < getChildCount(); i++) {
                View v = getChildAt(i);
                if(v != mHeader) {
                    mTarget = v;
                    break;
                }
            }
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setEnableRefresh(boolean enableRefresh) {
        mEnableRefresh = enableRefresh;
    }

    public boolean isRefreshing() {
        return mState == STATE_REFRESHING;
    }

    public void startRefresh() {
        if (!isRefreshing()) {
            setState(STATE_REFRESHING);
            startScroll(computeCurrentOffset(), mRefreshOffset, null, -1);
        }
    }

    /**
     * @param success refresh task is success or not
     */
    public void stopRefresh(boolean success) {
        if (computeCurrentOffset() == 0) {
            setState(STATE_SET);
        } else {
            setState(success ? STATE_REFRESH_SUCCESS : STATE_REFRESH_FAIL);
        }
    }

    public void cancelRefresh() {
        setState(STATE_SET);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        for (int i = 0, count = getChildCount(); i < count; i++) {
            if (getChildAt(i) == mTarget) {
                mTargetIndex = i;
                break;
            }
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if(getChildCount() == 0) {
            return;
        }
        if(mTarget == null) {
            ensureTarget();
        }
        if(mTarget == null) {
            return;
        }

        int headerTop = mHeaderPinned ? mHeaderPinPosition : -mHeader.getMeasuredHeight();
        mHeader.layout(width/2-mHeader.getMeasuredWidth()/2,
                headerTop,
                width/2+mHeader.getMeasuredWidth()/2,
                headerTop + mHeader.getMeasuredHeight());

        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childWidth = width-childLeft-getPaddingRight();
        int childHeight = height-childTop-getPaddingBottom();
        mTarget.layout(childLeft, childTop, childLeft+childWidth, childTop+childHeight);

        // update OriginTargetTop and other init data
        mOriginTargetTop = mTarget.getTop();
        if (mCurrentOffset <= Integer.MIN_VALUE) {
            mCurrentOffset = mOriginTargetTop;
        }
        if (mRefreshOffset <= 0) {
            mRefreshOffset = mHeader.getMeasuredHeight();
        }
        mOffsetCoefficient = getHeight() / 3;
        Arrays.fill(mOffsetTable, -1);

        if (mCurrentOffset != mOriginTargetTop) {
            onTargetOffsetChange(mCurrentOffset);
        }
    }

    private int computeCurrentOffset() {
        return mTarget.getTop() - mOriginTargetTop;
    }

    private void updateHeaderOnOffsetChange (int deltaOffset, final int targetOffset) {
        if (mHeader instanceof IRefreshHeader) {
            final IRefreshHeader refreshHeader = (IRefreshHeader) mHeader;
            if (mState == STATE_PULL_DOWN) {
                refreshHeader.onPullDown(targetOffset, mRefreshOffset);
            }
        }
        if (!mHeaderPinned) {
            mHeader.offsetTopAndBottom(deltaOffset);
        }
    }

    private void updateHeaderForState(int state) {
        if (mHeader instanceof IRefreshHeader) {
            IRefreshHeader header = (IRefreshHeader) mHeader;
            if (state == STATE_REFRESHING) {
                header.onRefreshing();
            } else if (state == STATE_REFRESH_SUCCESS) {
                header.onRefreshDone();
            } else if (state == STATE_REFRESH_FAIL) {
                header.onRefreshFail();
            } else if (state == STATE_SET) {
                header.onPullDown(mOriginTargetTop, mRefreshOffset);
            }
        }
    }

    private void setState(int state) {
        if (mState == state) return;
        mState = state;
        updateHeaderForState(state);
    }

    private void onTargetOffsetChange(int targetOffset) {
        mCurrentOffset = targetOffset;
        final int currentOffset = computeCurrentOffset();
        if (currentOffset != targetOffset) {
            final int deltaOffset = targetOffset-currentOffset;
//            D("deltaOffset = " + deltaOffset);
            // update target top and bottom
            mTarget.offsetTopAndBottom(targetOffset - currentOffset);
            // update header view
            updateHeaderOnOffsetChange(deltaOffset, targetOffset);
        }
    }

    /**
     * FIXME: maybe using A*arctan(B*x) is more elegant, but constants A and B are hard to decide
     * translate finger drag distance to target view offset distance.
     * n = dragOffset / mOffsetCoefficient;
     * targetOffset = mOffsetCoefficient/2 + mOffsetCoefficient/3 + mOffsetCoefficient/4 + ... +
     * (dragOffset - mOffsetCoefficient*n) / (n+2);
     * @param dragOffset total drag offset of finger
     * @return target view offset distance
     */
    private int dragOffset2TargetOffset(int dragOffset) {
        final boolean negTag = dragOffset < 0;
        dragOffset = Math.abs(dragOffset);
        final int n = dragOffset / mOffsetCoefficient;
        // 优先使用缓存
        int result = mOffsetTable[n];
        // 如果缓存不存在，则重新计算
        if (result == -1) {
            result = 0;
            for (int i = 0; i < n; i++) {
                result += mOffsetCoefficient / (i+2);
            }
            mOffsetTable[n] = result;
        }
        result += (dragOffset - mOffsetCoefficient *n) / (n+2);

        return negTag ? -result : result;
    }

    /**
     * reverse computation of {@link #dragOffset2TargetOffset}
     * @param targetOffset
     * @return
     */
    private int targetOffset2DragOffset(int targetOffset) {
        final boolean negTag = targetOffset < 0;
        targetOffset = Math.abs(targetOffset);
        int result = 0;
        int div = 2;
        while (targetOffset > 0) {
            if (targetOffset >= mOffsetCoefficient/div) {
                result += mOffsetCoefficient;
            } else {
                result += targetOffset * div;
            }
            targetOffset -= mOffsetCoefficient/div;
            div += 1;
        }
        return negTag ? -result : result;
    }

    private void onDragOffsetChange(int draggedOffset) {
        final int targetOffset = dragOffset2TargetOffset(Math.abs(draggedOffset));
        onTargetOffsetChange(draggedOffset >= 0 ? targetOffset : -targetOffset);
    }

    private void startDrag(float motionY) {
        if (!mIsBeingDragged && motionY - mLastMotionY > mTouchSlop) {
            mIsBeingDragged = true;
            mLastMotionY += mTouchSlop;
            mTotalDraggedOffset = 0;
            if (mState == STATE_SET) {
                mState = STATE_PULL_DOWN;
            }
            cancelScrollAnimation();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        if(mTarget == null || canChildScrollUp() || mNestedScrollInProgress) {
            return false;
        }

        final int action = ev.getActionMasked();
        int pointerIndex;
//        D("onInterceptTouchEvent: ", ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = mTarget.getTop() != mOriginTargetTop;
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mLastMotionY = ev.getY(pointerIndex);
                if (mIsBeingDragged) {
                    cancelScrollAnimation();
                    mTotalDraggedOffset = targetOffset2DragOffset(computeCurrentOffset());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER_ID) {
                    D("Got ACTION_MOVE, but has no valid pointer id");
                    return false;
                }
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                startDrag(ev.getY(pointerIndex));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondPointerUp(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER_ID;
                if (mTarget.getTop() != mOriginTargetTop) {
                    releaseDrag();
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTarget == null || canChildScrollUp()) {
            return false;
        }
        final int action = event.getActionMasked();
        int pointerIndex = -1;
//        D("onTouchEvent: ", event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mIsBeingDragged = mIsBeingDragged || (mTarget.getTop() != mOriginTargetTop);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    D("Got ACTION_MOVE but has no valid pointer id");
                    return false;
                }
                final float y = event.getY(pointerIndex);
                startDrag(y);
                if (mIsBeingDragged) {
                    mTotalDraggedOffset = Math.max(0, mTotalDraggedOffset + (y-mLastMotionY));
//                    mTotalDraggedOffset += (y - mLastMotionY);
                    onDragOffsetChange((int) mTotalDraggedOffset);
//                    D("mTotalDragOffset = " + mTotalDraggedOffset);
                    mLastMotionY = y;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = event.getActionIndex();
                if (pointerIndex < 0) {
                    D("Got ACTION_POINTER_DOWN but has no valid pointer index");
                    return false;
                }
                mActivePointerId = event.getPointerId(pointerIndex);
                if (mIsBeingDragged) {
                    mLastMotionY = event.getY(pointerIndex);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondPointerUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER_ID;
                releaseDrag();
                break;
        }
        return true;
    }

    private void onSecondPointerUp(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(newPointerIndex);
            if (mIsBeingDragged) {
                mLastMotionY = event.getY(newPointerIndex);
            }
        }
    }

    public boolean canChildScrollUp() {
        if(Build.VERSION.SDK_INT < 14) {
            if(mTarget instanceof AbsListView) {
                AbsListView list = (AbsListView) mTarget;
                return list.getChildCount() > 0 && list.getChildAt(0).getTop() < list.getPaddingTop();
            }
        }
        return ViewCompat.canScrollHorizontally(mTarget, -1);
    }

    private void cancelScrollAnimation() {
        removeCallbacks(mScrollRunnable);
        mScrollRunnable = null;
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    // ### NestedScrollingParent Start ###
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) > 0;
    }

    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
//        D("onNestedScrollAccepted");
        mNestedScrollInProgress = true;
        mTotalDraggedOffset = targetOffset2DragOffset(computeCurrentOffset());
//        D("recovered totalDraggedOffset is: " + mTotalDraggedOffset);
        cancelScrollAnimation();
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL);
        if (mState == STATE_SET) {
            setState(STATE_PULL_DOWN);
        }
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        D("dx = " + dx + ", dy = " + dy);
        if (dy > 0 && mTotalDraggedOffset > 0) {
            if (dy > mTotalDraggedOffset) {
                consumed[1] = (int) mTotalDraggedOffset;
                mTotalDraggedOffset = 0;
            } else {
                mTotalDraggedOffset -= dy;
                consumed[1] = dy;
            }
//            D("mTotalDraggedOffset = " + mTotalDraggedOffset);
            onDragOffsetChange((int) mTotalDraggedOffset);
        }
        final int[] parentConsumed = mParentScrollConsumed;
//        D("before dispatch, consumedY = " + consumed[1]);
        if (dispatchNestedPreScroll(dx-consumed[0], dy-consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
//            D("after dispatch, consumedY = " + consumed[1]);
        }
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
//        D("onNestedScroll: dyUnConsumed = " + dyUnconsumed);
        dispatchNestedScroll(dxConsumed, dyConsumed, dyUnconsumed, dyUnconsumed, mParentOffsetInWindow);
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
//        D("onNestedScroll: dyUnConsumed + mParentOffsetInWindow = " + dy);
        if (dy < 0) {
            mTotalDraggedOffset -= dy;
            onDragOffsetChange((int) mTotalDraggedOffset);
        }
    }

    public void onStopNestedScroll(View target) {
//        D("onStopNestedScroll called");
        mNestedScrollInProgress = false;
        releaseDrag();
        stopNestedScroll();
//        D("scrollEnd: mTotalOffset = " + mTotalDraggedOffset);
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (mTotalDraggedOffset > 0) {
            return true;
        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    // ### NestedScrollingParent End ###


    // ### NestedScrollingChild Start ###
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    // ### NestedScrollingChild End ###

    /**
     * delay <= 0, means no delay
     * @param startOffset
     * @param endOffset
     * @param action
     * @param delay
     */
    private void startScroll(final int startOffset, final int endOffset, final Runnable action, long delay) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mScroller.computeScrollOffset()) {
                    onTargetOffsetChange(mScroller.getCurrY());
                    postOnAnimation(this);
                } else {
                    mScrollRunnable = null;
                    if (action != null) {
                        action.run();
                    }
                }
            }
        };

        mScrollRunnable = r;

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                if (mScrollRunnable != r) return;

                mScroller.startScroll(0, startOffset, 0, endOffset-startOffset, ANIMATION_DURATION);
                if (mScroller.computeScrollOffset()) {
                    postOnAnimation(mScrollRunnable);
                } else {
                    mScrollRunnable = null;
                    action.run();
                }
            }
        };

        if (delay > 0) {
            postDelayed(r1, delay);
        } else {
            r1.run();
        }
    }

    private void scrollToReset(int startOffset, int delay) {
        startScroll(startOffset, 0, new Runnable() {
            @Override
            public void run() {
                if (mState == STATE_PULL_DOWN || mState == STATE_REFRESH_SUCCESS || mState == STATE_REFRESH_FAIL) {
                    setState(STATE_SET);
                }
            }
        }, delay);
    }

    private void releaseDrag() {
        final int savedState = mState;
        if (computeCurrentOffset() >= mRefreshOffset && mState == STATE_PULL_DOWN && mEnableRefresh) {
            setState(STATE_REFRESHING);
            if (mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
        }

        final int currentOffset = computeCurrentOffset();
        if (mState == STATE_PULL_DOWN ||
                mState == STATE_REFRESH_SUCCESS ||
                mState == STATE_REFRESH_FAIL ||
                (savedState == STATE_REFRESHING && mState == STATE_REFRESHING)) {
            if (mScrollRunnable != null) {
                removeCallbacks(mScrollRunnable);
                mScroller.abortAnimation();
                mScrollRunnable = null;
            }
            scrollToReset(currentOffset, -1);
        }
        else if (savedState == STATE_PULL_DOWN && mState == STATE_REFRESHING) {
            startScroll(currentOffset, mRefreshOffset, new Runnable() {
                @Override
                public void run() {
                    scrollToReset(computeCurrentOffset(), 500);
                }
            }, -1);
        }
    }

    private static void D(String msg) {
        Log.d(TAG, msg);
    }

    private static void D(String str, MotionEvent ev) {
        String msg = "";
        switch(ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                msg = "action_down";
                break;
            case MotionEvent.ACTION_MOVE:
                msg = "action_move";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                msg = "action_pointer_down";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                msg = "action_pointer_up";
                break;
            case MotionEvent.ACTION_UP:
                msg = "action_up";
                break;
            case MotionEvent.ACTION_CANCEL:
                msg = "action_cancel";
                break;
        }
        D(str + msg);
    }
}
