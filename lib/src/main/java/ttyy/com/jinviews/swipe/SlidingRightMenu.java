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
 * date: 2017/06/05
 * version: 0
 * mail: secret
 * desc: SlidingRightMenu
 */

public class SlidingRightMenu extends FrameLayout {

    static final int SAME = 1;
    static final int TOP = 0;
    static final int BOTTOM = 2;

    View mContentView;
    View mMenuView;

    int mLayerLevel = SAME;
    boolean isMovingLink = true;

    // 是否打开手势
    boolean boolEnableGesture = true;

    public SlidingRightMenu(Context context) {
        this(context, null);
    }

    public SlidingRightMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingRightMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mViewDragHelper = ViewDragHelper.create(this, mDragCallback);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingRightMenu);
            mLayerLevel = ta.getInt(R.styleable.SlidingRightMenu_rightMenuLayerLevel, 1);
            isMovingLink = ta.getBoolean(R.styleable.SlidingLeftMenu_leftMenuMovingLink, false);
            mSupportMultipleOpen = ta.getBoolean(R.styleable.SlidingRightMenu_rightMenuAutoClose, true);
            boolEnableGesture = ta.getBoolean(R.styleable.SlidingRightMenu_rightMenuEnableGesture, true);
            if (mLayerLevel != SAME
                    && mLayerLevel != TOP
                    && mLayerLevel != BOTTOM) {
                mLayerLevel = SAME;
            }

            ta.recycle();
        }

        if (mLayerLevel == SAME) {
            isMovingLink = true;
        }


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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mContentView = getChildAt(0);
        mMenuView = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        switch (mLayerLevel) {
            case BOTTOM:
                mContentView.bringToFront();
                break;
            case TOP:
                mMenuView.bringToFront();
                break;
            case SAME:
            default:
                mMenuView.layout(mContentView.getMeasuredWidth(),
                        0,
                        mContentView.getMeasuredWidth() + mMenuView.getMeasuredWidth(),
                        mMenuView.getMeasuredHeight());
                break;
        }

        if (isMenuOpened()) {
            // 菜单打开

            left = mContentView.getMeasuredWidth() - mMenuView.getMeasuredWidth();
            mMenuView.layout(left,
                    0,
                    left + mMenuView.getMeasuredWidth(),
                    mMenuView.getMeasuredHeight());

            if (isMovingLink) {
                // 联动
                left = -mMenuView.getMeasuredHeight();
                mContentView.layout(left,
                        0,
                        left + mContentView.getMeasuredWidth(),
                        mContentView.getMeasuredHeight());
            } else {
                // 非联动
                mContentView.layout(0,
                        0,
                        mContentView.getMeasuredWidth(),
                        mContentView.getMeasuredHeight());
            }

        } else {
            // 菜单没有打开
            mContentView.layout(0,
                    0,
                    mContentView.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());

        }

    }


    float mDownY;
    float mDownX;
    boolean isHorizontalMotionEvent;
    boolean mSupportMultipleOpen;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setParentDisallowInterceptTouchEvent(getParent(), true);
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                if (!isHorizontalMotionEvent) {
                    float dx = Math.abs(ev.getX() - mDownX);
                    float dy = Math.abs(ev.getY() - mDownY);
                    if (dx > ViewConfiguration.getTouchSlop()) {
                        if (dx > dy) {
                            isHorizontalMotionEvent = true;

                            return true;
                        } else {
                            setParentDisallowInterceptTouchEvent(getParent(), false);

                            if (!mSupportMultipleOpen)
                                revertAll();
                        }
                    }
                }


                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isHorizontalMotionEvent = false;
                setParentDisallowInterceptTouchEvent(getParent(), false);
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    void setParentDisallowInterceptTouchEvent(ViewParent parent, boolean value) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(value);
            setParentDisallowInterceptTouchEvent(parent.getParent(), value);
        }
    }

    ViewDragHelper mViewDragHelper = null;
    ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            if (mViewDragHelper.continueSettling(true)
                    || !boolEnableGesture) {
                return false;
            }

            View focusView = getFocusView();
            if (focusView == child) {

                if (isLastFocusedMenuOpening()) {
                    closeLastFocusedMenu();
                    return false;
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
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (getFocusView() == mContentView) {

                if (left > 0) {
                    return 0;
                }

                if (left < -mMenuView.getMeasuredWidth()) {
                    return -mMenuView.getMeasuredWidth();
                }

                return left;
            } else {

                if (left < mContentView.getMeasuredWidth() - mMenuView.getMeasuredWidth()) {
                    return mContentView.getMeasuredWidth() - mMenuView.getMeasuredWidth();
                }

                if (left > mContentView.getMeasuredWidth()) {
                    return mContentView.getMeasuredWidth();
                }

                return left;
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (isMovingLink) {
                if (getFocusView() == mContentView) {
                    mMenuView.layout(mMenuView.getLeft() + dx,
                            0,
                            mMenuView.getRight() + dx,
                            mMenuView.getMeasuredHeight());

                } else {
                    mContentView.layout(mContentView.getLeft() + dx,
                            0,
                            mContentView.getRight() + dx,
                            mContentView.getMeasuredHeight());

                }
            }

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            float focusX = 0;
            if (mLayerLevel == SAME
                    || mLayerLevel == BOTTOM) {
                focusX = mContentView.getRight();
            } else {
                focusX = mMenuView.getLeft();
            }

            if (focusX > mContentView.getMeasuredWidth() - mMenuView.getMeasuredWidth() / 2) {
                closeMenu();
            } else {
                openMenu();
            }

        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mViewDragHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

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

    View getFocusView() {
        if (mLayerLevel == SAME
                || mLayerLevel == BOTTOM) {
            // 焦点View mContentView
            return mContentView;
        }

        return mMenuView;
    }

    public void closeMenu() {
        View focusView = getFocusView();
        if (focusView == mContentView) {
            mViewDragHelper.smoothSlideViewTo(mContentView, 0, 0);
        } else {
            mViewDragHelper.smoothSlideViewTo(mMenuView, mContentView.getMeasuredWidth(), 0);
        }
        postInvalidate();
    }

    public void openMenu() {
        View focusView = getFocusView();
        if (focusView == mContentView) {
            mViewDragHelper.smoothSlideViewTo(mContentView, -mMenuView.getMeasuredWidth(), 0);
        } else {
            mViewDragHelper.smoothSlideViewTo(mMenuView, mContentView.getMeasuredWidth() - mMenuView.getMeasuredWidth(), 0);
        }
        postInvalidate();
    }

    public void setEnableGesture(boolean enable){
        boolEnableGesture = enable;
    }

    public boolean isGestureEnabled(){
        return boolEnableGesture;
    }

    public boolean isMenuOpened() {


        if (isMovingLink) {
            // 菜单联动模式
            return mContentView.getLeft() == -mMenuView.getMeasuredWidth()
                    && mMenuView.getRight() != 0;
        } else {
            // 费菜单联动模式
            return mMenuView.getRight() == mContentView.getMeasuredWidth()
                    && mMenuView.getRight() != 0;
        }

    }

    static LinkedList<SlidingRightMenu> menus = new LinkedList<>();
    static SlidingRightMenu mLastOpenedMenu = null;

    static void keep(SlidingRightMenu menu) {
        menus.add(menu);
    }

    static void discard(SlidingRightMenu menu) {
        menus.remove(menu);
    }

    static void revertAll() {
        for (SlidingRightMenu tmp : menus) {
            if (tmp.isMenuOpened()) {
                tmp.closeMenu();
            }
        }
    }

    static boolean isLastFocusedMenuOpening() {
        if (mLastOpenedMenu != null) {
            return mLastOpenedMenu.isMenuOpened();
        }

        return false;
    }

    static void closeLastFocusedMenu() {
        if (mLastOpenedMenu != null) {
            mLastOpenedMenu.closeMenu();
        }
    }
}
