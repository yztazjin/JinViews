package ttyy.com.jinviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: hjq
 * Date  : 2017/03/30 20:04
 * Name  : CircleLayout
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class CircleLayout extends ViewGroup implements CircleLayoutAdapter.NotifyDataSetChanger {

    protected CircleLayoutAdapter mAdapter;

    private View.OnClickListener mInnerClickListener;

    protected View.OnClickListener mOnItemClickListener;

    protected int mStartDegreeOffset = 0;

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInnerClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onClick(view);
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null) {
            return;
        }

        float stepDegree = 360f / mAdapter.getCount();
        float startDegree = mStartDegreeOffset == 0 ? 90 : mStartDegreeOffset;
        int parentWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        int parentHeight = getHeight() - getPaddingBottom() - getPaddingTop();

        int circleX = parentWidth / 2 + l + getPaddingLeft();
        int circleY = parentHeight / 2 + t + getPaddingTop();
        int circleR = parentWidth < parentHeight ? parentWidth / 2 : parentHeight / 2;

        View template = getChildAt(0);
        int templateDelta = template.getMeasuredHeight() > template.getMeasuredWidth() ? template.getMeasuredHeight() : template.getMeasuredWidth();
        circleR -= templateDelta / 2;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View childView = getChildAt(i);

            int x = (int) (circleX - Math.cos(Math.toRadians(startDegree)) * circleR) - childView.getMeasuredWidth() / 2;
            int y = (int) (circleY - Math.sin(Math.toRadians(startDegree)) * circleR) - childView.getMeasuredHeight() / 2;
            childView.layout(x, y, x + childView.getMeasuredWidth(), y + childView.getMeasuredHeight());

            startDegree += stepDegree;
        }


    }

    @Override
    public void notifyDataSetChanged() {
        removeAllViews();

        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                View view = mAdapter.getView(i, this);
                view.setOnClickListener(mInnerClickListener);
                addView(view);
            }
            requestLayout();
        }
    }

    public CircleLayout setOnItemClickListener(View.OnClickListener listener){
        this.mOnItemClickListener = listener;
        return this;
    }

    public CircleLayout setStartDegreeOffset(int degree){
        this.mStartDegreeOffset = degree;
        return this;
    }

    public CircleLayout setAdapter(CircleLayoutAdapter adapter) {
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            this.mAdapter.mNotifyDataSetChanger = this;
        }
        notifyDataSetChanged();
        return this;
    }
}
