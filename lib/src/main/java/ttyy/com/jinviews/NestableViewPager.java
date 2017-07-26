package ttyy.com.jinviews;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

/**
 * Author: hujinqi
 * Date  : 2016-07-13
 * Description: 可嵌套的ViewPager
 *              1.这是一个对requestParentDisallowInterceptTouchEvent的应用
 *                  true 父容器不拦截事件
 *                  false 父容器拦截事件
 *              2.事件总是会先走到dispatchTouchEvent对事件进行分发,所有在Dispatch中判断是最好的地方
 */
public class NestableViewPager extends ViewPager {

    float startX;
    float startY;
    int mTouchSlop = ViewConfiguration.getTouchSlop();

    public NestableViewPager(Context context) {
        super(context);
    }

    public NestableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 事件会优先从这人开始处理
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int childCount = getChildCount();
        if(childCount < 1){
            // ViewPager是空的 不处理
            return super.onTouchEvent(ev);
        }

        int currentIndex = getCurrentItem();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                disallowInterceptTouchEvent(getParent());
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = ev.getX() - startX;
                float dy = ev.getY() - startY;

                // 认为发生了滑动
                if((dx*dx + dy*dy) > (mTouchSlop * mTouchSlop)){

                    if(Math.abs(dx) > Math.abs(dy)) {
                        // 发生了横向滑动
                        if(dx > 0){
                            // 向右滑动
                            if(currentIndex == 0){
                                // 从第一个Item向右滑动
                                allowParentInterceptTouchEvent(getParent());
                            }

                        }else{
                            // 向左滑动
                            if(currentIndex == getAdapter().getCount() - 1){
                                // 从最后一个Item向左滑动
                                allowParentInterceptTouchEvent(getParent());
                            }
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                allowParentInterceptTouchEvent(getParent());
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 不允许父容器拦截TouchEvent
     * @param parent
     */
    void disallowInterceptTouchEvent(ViewParent parent){
        if(parent != null){
            parent.requestDisallowInterceptTouchEvent(true);
            disallowInterceptTouchEvent(parent.getParent());
        }
    }

    /**
     * 允许父容器拦截TouchEvent
     * @param parent
     */
    void allowParentInterceptTouchEvent(ViewParent parent){
        if(parent != null){
            parent.requestDisallowInterceptTouchEvent(false);
            allowParentInterceptTouchEvent(parent.getParent());
        }
    }
}
