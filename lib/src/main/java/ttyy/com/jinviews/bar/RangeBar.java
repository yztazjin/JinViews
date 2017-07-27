package ttyy.com.jinviews.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ttyy.com.jinviews.R;

/**
 * Author: hjq
 * Date  : 2017/07/27 18:36
 * Name  : RangeBar
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class RangeBar extends FrameLayout {

    ImageView thumb_left;
    ImageView thumb_right;
    View mBarView;
    int thumb_size = 50;
    int bar_height = 8;

    int max = 100;
    int min = 0;
    int leftProgress = 0;
    int rightProgress = 100;

    Drawable thumbDrawable;
    Drawable barDrawable;

    ProgressCallback mCallback;

    public RangeBar(@NonNull Context context) {
        this(context, null);
    }

    public RangeBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        mViewDragHelper = ViewDragHelper.create(this, mDragCallback);
    }

    public RangeBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeBar);
            thumb_size = ta.getDimensionPixelSize(R.styleable.RangeBar_thumbSize, 50);
            bar_height = ta.getDimensionPixelOffset(R.styleable.RangeBar_barHeight, 8);
            thumbDrawable = ta.getDrawable(R.styleable.RangeBar_thumbDrawable);
            barDrawable = ta.getDrawable(R.styleable.RangeBar_barDrawable);
            max = ta.getInt(R.styleable.RangeBar_max, 100);
            min = ta.getInt(R.styleable.RangeBar_min, 0);
            leftProgress = ta.getInt(R.styleable.RangeBar_leftProgress, min);
            rightProgress = ta.getInt(R.styleable.RangeBar_rightProgress, max);
            ta.recycle();
        }

        initWidgets(context);


    }

    void initWidgets(Context context){

        mBarView = new View(context);
        mBarView.setBackground(barDrawable);
        FrameLayout.LayoutParams barParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                bar_height);
        barParams.gravity = Gravity.CENTER_VERTICAL;
        barParams.leftMargin = thumb_size;
        barParams.rightMargin = thumb_size;
        addView(mBarView, barParams);

        thumb_left = new ImageView(context);
        thumb_left.setImageDrawable(thumbDrawable);
        FrameLayout.LayoutParams leftParams = new LayoutParams(thumb_size, thumb_size);
        leftParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        addView(thumb_left, leftParams);

        thumb_right = new ImageView(context);
        thumb_right.setImageDrawable(thumbDrawable);
        FrameLayout.LayoutParams rightparams = new LayoutParams(thumb_size, thumb_size);
        rightparams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        addView(thumb_right, rightparams);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mViewDragHelper.shouldInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public void setProgressCallback(ProgressCallback callback){
        this.mCallback = callback;
    }

    public RangeBar setMax(int value){
        max = value;
        return this;
    }

    public RangeBar setMin(int value){
        min = value;
        return this;
    }

    public RangeBar setLeftProgress(int value){

        if(value < min
                || value > max
                || value > rightProgress){
            throw new UnsupportedOperationException("Set Right ProgressValue");
        }

        int left = (int) (((float)value - min) / (max - min) * getMeasuredWidth());
        thumb_left.layout(left, getPaddingTop(), left+thumb_size, getPaddingTop() + thumb_size);

        return this;
    }

    public RangeBar setRightProgress(int value){

        if(value < leftProgress
                || value < min
                || value > max){
            throw new UnsupportedOperationException("Set Right ProgressValue");
        }

        int left = (int) (((float)value - min) / (max - min) * getMeasuredWidth());
        thumb_right.layout(left, getPaddingTop(), left+thumb_size, getPaddingTop() + thumb_size);

        return this;
    }

    ViewDragHelper mViewDragHelper = null;
    ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if(child == thumb_left
                    || child == thumb_right){
                return true;
            }
            return false;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return thumb_size;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child == thumb_left){
                if(left < 0){
                    left = 0;
                }

                if(left + thumb_size > thumb_right.getLeft()){
                    left = thumb_right.getLeft();
                }

            }else if(child == thumb_right){

                if(left > getMeasuredWidth() - thumb_size){
                    left = getMeasuredWidth() - thumb_size;
                }

                if(left < thumb_left.getRight()){
                    left = thumb_left.getRight();
                }

            }else {
                left = 0;
            }

            if(mCallback != null
                    && left > 0){
                float thumbLeft = thumb_left.getLeft();
                float thumbRight = thumb_right.getLeft();
                int leftProgress = (int) (thumbLeft / getMeasuredWidth() * (max - min) + min);
                int rightprogress = (int) (thumbRight / getMeasuredWidth() * (max - min) + min);
                mCallback.onProgressChanged(leftProgress, rightprogress, true);
            }

            return left;
        }
    };

    public interface ProgressCallback{
        boolean onProgressChanged(int leftProgress, int rightProgress, boolean isFromUser);
    }
}
