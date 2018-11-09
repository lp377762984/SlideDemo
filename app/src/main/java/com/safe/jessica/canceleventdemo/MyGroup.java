package com.safe.jessica.canceleventdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
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
    public boolean isBeingDrag;
    public boolean isOpen;

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
        //Log.d(TAG, "onMeasure: " + leftWidth + "*" + leftHeight);
        setMeasuredDimension(leftWidth, leftHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //Log.d(TAG, "onLayout: " + l + "*" + t + "*" + r + "*" + b);
        leftView.layout(0, 0, leftWidth, leftHeight);
        delView.layout(leftWidth, 0, leftWidth + delWidth, 0 + delHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: " + ev.getAction());
        int action = ev.getAction();
        if (isOpen) {
            float evX = ev.getX();
            Log.d(TAG, "onInterceptTouchEvent: " + evX);
            if (evX > getMeasuredWidth() - delWidth) {//如果点击区域是delView，则不拦截事件
                return false;
            } else {
                return true;
            }
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
                    parentNotIntercept();//滑动过程中不允许父view拦截事件
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

    boolean isLeftSlideAndOpen;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mScroller.computeScrollOffset()) {//滑动中 不进行任何操作直接return
            Log.d(TAG, "onInterceptTouchEvent: " + ev.getAction() + "," + true);
            return true;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isBeingDrag) {//正在左滑->拿起手指 自然滑到最左侧
                    if (Math.abs(moveX) > 0.5 * delWidth) {//超过一半关闭
                        smoothScrollToFinal(delWidth - Math.abs(moveX));
                        isBeingDrag = false;
                        isOpen = true;
                    } else {//不超过一半 打开
                        smoothScrollToFinal(-moveX);
                        isBeingDrag = false;
                        isOpen = false;
                    }
                    parentNotIntercept();
                } else {
                    if (isOpen) {//打开状态下，点击后关闭
                        if (isLeftSlideAndOpen) {
                            isLeftSlideAndOpen = false;
                        } else {
                            smoothScrollToFinal(-delWidth);
                            isOpen = false;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float mCurX = ev.getX();
                int diffX = (int) (mCurX - mLastX);
                if (Math.abs(diffX) > scaledTouchSlop) {
                    if (diffX < 0) {//左滑
                        if (isOpen) {
                            isLeftSlideAndOpen = true;
                            Log.d(TAG, "onInterceptTouchEvent: " + ev.getAction() + "," + false);
                            return false;
                        }
                        isBeingDrag = true;
                        if (Math.abs(diffX) >= delWidth) {
                            diffX = -delWidth;
                            isOpen = true;
                        }
                        moveX = -diffX;
                        scrollTo(moveX, 0);
                        parentNotIntercept();
                    } else {//右滑
                        isBeingDrag = true;
                        if (Math.abs(diffX) >= delWidth) {
                            diffX = delWidth;
                            isOpen = false;
                        }
                        moveX = delWidth - diffX;
                        //Log.d(TAG, "onTouchEvent: " + moveX);
                        scrollTo(moveX, 0);
                        parentNotIntercept();
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                if (isOpen) {//左滑打开-> 放下手指  关闭左滑
                    //smoothScrollToFinal(-delWidth);
                    //isOpen = false;
                }
                break;
        }
        Log.d(TAG, "onTouchEvent: " + ev.getAction() + "," + true);
        return true;
    }

    private void parentNotIntercept() {
        ViewParent parent = getParent();//滑动过程中不允许父view拦截事件
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private void smoothScrollToFinal(int distance) {
        mScroller.forceFinished(true);
        int scrollX = getScrollX();
        //Log.d(TAG, "smoothScrollToFinal: " + scrollX + "---" + distance);
        mScroller.startScroll(scrollX, 0, distance, 0, 250);
        invalidate();
    }

    /**
     * 关闭侧滑
     */
    public void smoothScrollToFinal() {
        smoothScrollToFinal(-delWidth);
        isOpen = false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            int currY = mScroller.getCurrY();
            //Log.d(TAG, "currX-->" + currX + ",currY-->" + currY);
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
