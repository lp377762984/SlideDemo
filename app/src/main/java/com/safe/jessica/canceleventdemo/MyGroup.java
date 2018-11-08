package com.safe.jessica.canceleventdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyGroup extends ViewGroup {
    String TAG = "Mine_Group";
    private View leftView;
    private View delView;
    private int moveX;
    private int leftWidth;
    private int leftHeight;
    private int delWidth;
    private int delHeight;
    private Scroller mScroller;
    private ViewConfiguration mConfiguration;
    private int scaledTouchSlop;
    private float mLastX;
    private boolean isBeingDrag;
    private boolean isOpen;

    public MyGroup(Context context) {
        super(context);
        init(context);
    }

    public MyGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        mConfiguration = ViewConfiguration.get(context);
        scaledTouchSlop = mConfiguration.getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        leftView = getChildAt(0);
        delView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        leftWidth = leftView.getMeasuredWidth();
        leftHeight = leftView.getMeasuredHeight();
        delWidth = delView.getMeasuredWidth();
        delHeight = delView.getMeasuredHeight();
        Log.d(TAG, "onMeasure: " + leftWidth + "*" + leftHeight);
        setMeasuredDimension(leftWidth, leftHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: " + l + "*" + t + "*" + r + "*" + b);
        leftView.layout(0, 0, leftWidth, leftHeight);
        delView.layout(leftWidth, 0, leftWidth + delWidth, 0 + delHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO: 2018/11/8  侧滑打开时：如果点击左侧，拦截事件，关闭侧滑；如果点击右侧view，不拦截事件
        int action = ev.getAction();
        if (isOpen) {
            float x = delView.getX();
            float y = delView.getY();

            return false;
        }
        if (isBeingDrag) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onInterceptTouchEvent: " + action + "," + false);
                isBeingDrag = false;
                return false;
            case MotionEvent.ACTION_MOVE:
                float mCurX = ev.getX();
                int diffX = (int) (mCurX - mLastX);
                if (diffX < 0 && Math.abs(diffX) > scaledTouchSlop) {//左滑
                    isBeingDrag = true;
                    Log.d(TAG, "onInterceptTouchEvent: " + action + "," + true);
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                break;
        }
        Log.d(TAG, "onInterceptTouchEvent: " + action + "," + false);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mScroller.computeScrollOffset()) {//滑动中 不进行任何操作直接return
            return true;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isBeingDrag) {//正在左滑->拿起手指 自然滑到最左侧
                    if (Math.abs(moveX) > 0.5 * delWidth) {
                        smoothScrollToFinal(delWidth - Math.abs(moveX));
                        isBeingDrag = false;
                        isOpen = true;
                    } else {
                        smoothScrollToFinal(-moveX);
                        isBeingDrag = false;
                        isOpen = false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float mCurX = ev.getX();
                int diffX = (int) (mCurX - mLastX);
                if (diffX < 0 && Math.abs(diffX) > scaledTouchSlop) {//左滑
                    isBeingDrag = true;
                    if (Math.abs(diffX) >= delWidth) {
                        diffX = -delWidth;
                        isOpen = true;
                    }
                    moveX = -diffX;
                    scrollTo(moveX, 0);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                if (isOpen) {//左滑打开-> 放下手指  关闭左滑
                    smoothScrollToFinal(-delWidth);
                    isOpen = false;
                }
                break;
        }
        Log.d(TAG, "onTouchEvent: " + ev.getAction() + "," + true);
        return true;
    }

    private void smoothScrollToFinal(int distance) {
        mScroller.forceFinished(true);
        int scrollX = getScrollX();
        Log.d(TAG, "smoothScrollToFinal: " + scrollX + "---" + distance);
        mScroller.startScroll(scrollX, 0, distance, 0, 250);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();
            Log.d(TAG, "currX-->" + currX + ",currY-->" + currY);
            scrollTo(currX, currY);
            invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent start: " + ev.getAction());
        boolean b = super.dispatchTouchEvent(ev);
        Log.d(TAG, "dispatchTouchEvent end: " + ev.getAction() + "," + b);
        return b;
    }
}
