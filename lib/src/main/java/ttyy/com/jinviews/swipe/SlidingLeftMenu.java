package ttyy.com.jinviews.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.LinkedList;

import ttyy.com.jinviews.R;

/**
 * author: admin
 * date: 2017/06/03
 * version: 0
 * mail: secret
 * desc: 左侧侧滑菜单父布局
 * 父布局容纳两个子View,Cihld0 Content,Child1 Menu
 */

public class SlidingLeftMenu extends FrameLayout {

    static final int SAME = 1;
    static final int TOP = 0;
    static final int BOTTOM = 2;

    View mMenuView;
    View mContentView;

    int mLayerLevel;//0 覆盖在Content上; 1 Content同级; 2 Content下面

    public SlidingLeftMenu(Context context) {
        this(context, null);
    }

    public SlidingLeftMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLeftMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, mDragCallback);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingLeftMenu);
            mLayerLevel = ta.getInt(R.styleable.SlidingLeftMenu_leftMenuLayerLevel, 1);
            ta.recycle();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView = getChildAt(1);
        mContentView = getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        switch (mLayerLevel) {
            case BOTTOM:
                mContentView.bringToFront();
                mMenuView.layout(0, 0, mMenuView.getMeasuredWidth(), mMenuView.getMeasuredHeight());
                break;
            case TOP:
                mMenuView.bringToFront();
                mMenuView.layout(-mMenuView.getMeasuredWidth(), 0, 0, mMenuView.getMeasuredHeight());
                break;
            case SAME:
            default:
                mMenuView.layout(-mMenuView.getMeasuredWidth(), 0, 0, mMenuView.getMeasuredHeight());
                break;

        }
        mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        keep(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        discard(this);
    }

    boolean isHorizontalMotionEvent;
    float mTouchDownX;
    float mTouchDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                setParentDisallowInterceptTouchEvent(getParent(), true);
                mTouchDownX = ev.getX();
                mTouchDownY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:

                if (!isHorizontalMotionEvent) {
                    float dx = ev.getX() - mTouchDownX;
                    float dy = ev.getY() - mTouchDownY;

                    if (Math.abs(dx) > ViewConfiguration.getTouchSlop()
                            || Math.abs(dy) > ViewConfiguration.getTouchSlop()) {

                        if (Math.abs(dx) > Math.abs(dy)) {
                            // 发生了横向位移
                            isHorizontalMotionEvent = true;
                            return true;
                        } else {
                            // 发生了竖向位移
                            isHorizontalMotionEvent = false;
                            setParentDisallowInterceptTouchEvent(getParent(), false);

                            if (!mSupportMultipleOpen)
                                revertAll();
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                isHorizontalMotionEvent = false;
                setParentDisallowInterceptTouchEvent(getParent(), false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // 是否支持多个菜单同时打开
    boolean mSupportMultipleOpen = false;
    ViewDragHelper mViewDragHelper;
    ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            if (mViewDragHelper.continueSettling(true)) {
                // 动画过程中 不可锁定View
                return false;
            }

            View focusView = getFocusView();

            if (child == focusView) {
                if (!mSupportMultipleOpen) {

                    if (isLastFocusedMenuOpening()) {
                        closeLastFocusedMenu();
                        return false;
                    }

                }

                return true;
            }

            return false;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mMenuView.getMeasuredWidth();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if(mLayerLevel == SAME){
                mMenuView.layout(mMenuView.getLeft() + dx,
                        0,
                        mMenuView.getRight() + dx,
                        mMenuView.getMeasuredHeight());
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (left < 0) {
                left = 0;
            }

            if (left > mMenuView.getMeasuredWidth()) {
                left = mMenuView.getMeasuredWidth();
            }

            return left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            float focusX;
            if(mLayerLevel == SAME
                    || mLayerLevel == BOTTOM){
                // 焦点View mContentView
                focusX = mContentView.getLeft();
            }else{
                // 焦点View mMenuView
                focusX = mMenuView.getRight();
            }

            if (focusX > mMenuView.getMeasuredWidth() / 2) {
                // 打开菜单
                openMenu();
            } else {
                // 关闭菜单
                closeMenu();
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mViewDragHelper.shouldInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev) || isHorizontalMotionEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    void setParentDisallowInterceptTouchEvent(ViewParent parent, boolean value) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(value);
            setParentDisallowInterceptTouchEvent(parent.getParent(), value);
        }
    }

    View getFocusView(){
        if(mLayerLevel == SAME
                || mLayerLevel == BOTTOM){
            // 焦点View mContentView
            return mContentView;
        }else {
            // 焦点View mMenuView
            return mMenuView;
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

    public void closeMenu() {

        if(getFocusView() == mContentView){
            mViewDragHelper.smoothSlideViewTo(mContentView, 0, 0);
        }else {
            mViewDragHelper.smoothSlideViewTo(mMenuView, -mMenuView.getMeasuredWidth(), 0);
        }

        postInvalidate();

        mLastOpeningMenu = null;
    }

    public void openMenu() {

        if(getFocusView() == mContentView){
            mViewDragHelper.smoothSlideViewTo(mContentView, mMenuView.getMeasuredWidth(), 0);
        }else {
            mViewDragHelper.smoothSlideViewTo(mMenuView, 0, 0);
        }

        postInvalidate();

        mLastOpeningMenu = this;
    }

    public boolean isMenuOpend() {

        if(mLayerLevel == SAME){
            return mMenuView.getLeft() == 0;
        }else if(mLayerLevel == TOP){
            return mMenuView.getLeft() == 0;
        }else if(mLayerLevel == BOTTOM){
            return mContentView.getLeft() == mMenuView.getMeasuredWidth();
        }

        return false;
    }

    static LinkedList<SlidingLeftMenu> menus = new LinkedList<>();
    static SlidingLeftMenu mLastOpeningMenu = null;

    static void keep(SlidingLeftMenu menu) {
        menus.add(menu);
    }

    static void discard(SlidingLeftMenu menu) {
        menus.remove(menu);
    }

    static void revertAll() {
        for (SlidingLeftMenu tmp : menus) {
            if (tmp.isMenuOpend()) {
                tmp.closeMenu();
            }
        }
    }

    static boolean isLastFocusedMenuOpening() {
        if (mLastOpeningMenu != null) {
            return mLastOpeningMenu.isMenuOpend();
        }

        return false;
    }

    static void closeLastFocusedMenu() {
        if (mLastOpeningMenu != null) {
            mLastOpeningMenu.closeMenu();
        }
    }

}
