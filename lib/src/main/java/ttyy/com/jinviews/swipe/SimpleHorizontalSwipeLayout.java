package ttyy.com.jinviews.swipe;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: hujinqi
 * Date  : 2016-08-11
 * Description: 可以横向滑动的Layout
 * 只能有两个View
 * 第一个View是菜单
 * 第二个View是上层展示内容
 */
public class SimpleHorizontalSwipeLayout extends FrameLayout {

    private float startX;
    private float startY;

    /**
     * 当前是否是横向滑动
     */
    boolean isHorizontalMove;
    ViewDragHelper mViewDragHelper;

    /**
     * 菜单View第一个
     */
    View mMenuView;
    /**
     * 内容View在第二个
     */
    View mContentView;

    /**
     * 最小化东距离
     */
    protected int mTouchSlop = ViewConfiguration.getTouchSlop();

    boolean isMenuOpen;
    boolean isDragerEnabled = true;

    public SimpleHorizontalSwipeLayout(Context context) {
        super(context);
        init();
    }

    public SimpleHorizontalSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mDragCalback);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(1);
        mMenuView = getChildAt(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 不允许父容器拦截事件
                // 这样该View就可以获得TouchEvent从而处理滑动动作
                disallowParentInterceptTouchEvent(getParent());
                startX = ev.getX();
                startY = ev.getY();
                isHorizontalMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isHorizontalMove) {
                    float dx = ev.getX() - startX;
                    float dy = ev.getY() - startY;

                    if ((dx * dx + dy * dy) > mTouchSlop * mTouchSlop) {
                        // 认为发生了滑动
                        if (Math.abs(dx) > Math.abs(dy)) {
                            // 发生了横向滑动

                            isHorizontalMove = true;

                            return true;
                        } else {
                            // 竖向滑动不处理
                            // 事件继续转交给父容器处理
                            allowParentInterceptTouchEvent(getParent());

                            isHorizontalMove = false;

                            // 默认还原
                            shrinkAllViews();
                        }
                    }

                    startX = ev.getX();
                    startY = ev.getY();
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 不处理 交由父容器处理
                allowParentInterceptTouchEvent(getParent());
                isHorizontalMove = false;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mViewDragHelper.shouldInterceptTouchEvent(ev);
        if (isHorizontalMove) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 不允许父容器拦截事件
     *
     * @param parent
     */
    protected void disallowParentInterceptTouchEvent(ViewParent parent) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
            disallowParentInterceptTouchEvent(parent.getParent());
        }
    }

    /**
     * 允许父容器拦截事件
     *
     * @param parent
     */
    protected void allowParentInterceptTouchEvent(ViewParent parent) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
            allowParentInterceptTouchEvent(parent.getParent());
        }
    }

    public void setDragEnabled(boolean value){
        this.isDragerEnabled = value;
    }

    /**
     * ViewDragHelper callback
     */
    ViewDragHelper.Callback mDragCalback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if(!isDragerEnabled)
                return false;

            if(child == mContentView){
                return !mViewDragHelper.continueSettling(true) && !isCurrentMenuOpen();
            }else if(child == mMenuView){
                return false;
            }

            return false;
        }

        /**
         * 很重要 没设置>0 可能导致判断失败
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mMenuView.getMeasuredWidth();
        }

        /**
         * 横向能够滑动的距离
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            int menu_width = mMenuView.getWidth();
            if (left >= 0)
                left = 0;

            if (left <= -menu_width) {
                left = -menu_width;
            }

            return left;
        }

        /**
         * 释放
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            int tag = getWidth() - mMenuView.getWidth() / 2;
            if (releasedChild.getRight() > tag) {

                close();

            } else {
                open();
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addSwipeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeSwipeView(this);
    }

    /**
     * 菜单关闭
     */
    public void close() {
        mViewDragHelper.smoothSlideViewTo(mContentView, 0, 0);
        postInvalidate();
        isMenuOpen = false;
        setCurrentOpenedView(this);
    }

    /**
     * 菜单打开
     */
    public void open() {
        mViewDragHelper.smoothSlideViewTo(mContentView, -mMenuView.getWidth(), 0);
        postInvalidate();
        isMenuOpen = true;
        setCurrentOpenedView(this);
    }

    /**
     * 菜单是否打开
     * @return
     */
    public boolean isMenuOpen(){
        return isMenuOpen;
    }

    /**
     * 缓存的滑动View，这样可以方便的找到View从而进行滑动还原操作
     */
    static List<SimpleHorizontalSwipeLayout> swipelayouts = new ArrayList<>();
    static SimpleHorizontalSwipeLayout currentOpenedView = null;
    static boolean isSupportAutoClose = true;

    /**
     * 添加管理
     *
     * @param v
     */
    static void addSwipeView(SimpleHorizontalSwipeLayout v) {
        if (null == v) {
            return;
        }
        swipelayouts.add(v);
    }

    /**
     * 移除
     *
     * @param v
     */
    public static void removeSwipeView(SimpleHorizontalSwipeLayout v) {
        if (null == v) {
            return;
        }
        swipelayouts.remove(v);
    }

    /**
     * 所有View都还原
     */
    public static void shrinkAllViews() {
        for (SimpleHorizontalSwipeLayout tmp : swipelayouts) {
            tmp.close();
        }
    }

    /**
     * 设置是否支持菜单自动关闭功能
     *
     * @return
     */
    public static void setSupportAutoClose(boolean value) {
        isSupportAutoClose = value;
    }

    /**
     * 设置当前菜单打开的View
     *
     * @param view
     */
    static void setCurrentOpenedView(SimpleHorizontalSwipeLayout view) {
        if (view.isMenuOpen) {
            currentOpenedView = view;
        } else if (currentOpenedView == view) {
            currentOpenedView = null;
        }
    }

    /**
     * 当前是否有菜单打开
     *
     * @return
     */
    static boolean isCurrentMenuOpen() {
        if (isSupportAutoClose && currentOpenedView != null && currentOpenedView.isMenuOpen) {
            currentOpenedView.close();
            return true;
        } else {
            currentOpenedView = null;
        }
        return false;
    }
}
