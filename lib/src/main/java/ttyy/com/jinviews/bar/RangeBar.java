package ttyy.com.jinviews.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
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

    ImageView mThumbLeftView;
    ImageView mThumbRightView;
    View mBarView;

    int thumbPixelSize = 50;
    int barPixelHeight = 8;

    int max = 100;
    int min = 0;
    int mLeftProgress = 0;
    int mRightProgress = 100;

    RangeBarProgressDrawable mProgressDrawable;
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

        Drawable thumbDrawable = null;
        int mUnRangeColor = Color.parseColor("#eeeeee");
        int mRangeColor = Color.parseColor("#ff00ff");

        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RangeBar);
            thumbPixelSize = ta.getDimensionPixelSize(R.styleable.RangeBar_thumbSize, 50);
            barPixelHeight = ta.getDimensionPixelOffset(R.styleable.RangeBar_barHeight, 8);
            thumbDrawable = ta.getDrawable(R.styleable.RangeBar_thumbDrawable);
            mUnRangeColor = ta.getColor(R.styleable.RangeBar_barUnRangeColor, mUnRangeColor);
            mRangeColor = ta.getColor(R.styleable.RangeBar_barRangeColor, mRangeColor);
            max = ta.getInt(R.styleable.RangeBar_max, 100);
            min = ta.getInt(R.styleable.RangeBar_min, 0);
            mLeftProgress = ta.getInt(R.styleable.RangeBar_leftProgress, min);
            mRightProgress = ta.getInt(R.styleable.RangeBar_rightProgress, max);
            ta.recycle();
        }


        initWidgets(context);

        mProgressDrawable = new RangeBarProgressDrawable();
        mProgressDrawable.setRangeColor(mRangeColor)
                .setUnRangeColor(mUnRangeColor)
                .setCorners(barPixelHeight / 2)
                .setRangeLeftValue(thumbPixelSize / 2)
                .setRangeRightValue(thumbPixelSize / 2);

        if(thumbDrawable == null){
            GradientDrawable tmp = new GradientDrawable();
            tmp.setCornerRadius(thumbPixelSize / 2);
            tmp.setColor(Color.parseColor("#ff00ff"));
            thumbDrawable = tmp;
        }

        mBarView.setBackground(mProgressDrawable);
        mThumbLeftView.setImageDrawable(thumbDrawable);
        mThumbRightView.setImageDrawable(thumbDrawable);

    }

    void initWidgets(Context context){

        mBarView = new View(context);
        FrameLayout.LayoutParams barParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                barPixelHeight);
        barParams.gravity = Gravity.CENTER_VERTICAL;
        addView(mBarView, barParams);

        mThumbLeftView = new ImageView(context);
        FrameLayout.LayoutParams leftParams = new LayoutParams(thumbPixelSize, thumbPixelSize);
        leftParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        addView(mThumbLeftView, leftParams);

        mThumbRightView = new ImageView(context);
        FrameLayout.LayoutParams rightparams = new LayoutParams(thumbPixelSize, thumbPixelSize);
        rightparams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        addView(mThumbRightView, rightparams);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        setLeftProgress(mLeftProgress);
        setRightProgress(mRightProgress);
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

    public RangeBar setProgressCallback(ProgressCallback callback){
        this.mCallback = callback;
        return this;
    }

    public RangeBar setMax(int value){
        max = value;
        return this;
    }

    public RangeBar setMin(int value){
        min = value;
        return this;
    }

    ViewDragHelper mViewDragHelper = null;
    ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if(child == mThumbLeftView
                    || child == mThumbRightView){
                return true;
            }
            return false;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return thumbPixelSize;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if(child == mThumbLeftView){

                if(left < 0){
                    left = 0;
                }

                if(left + thumbPixelSize > mThumbRightView.getLeft()){
                    left = mThumbRightView.getLeft() - thumbPixelSize;
                }

                int relDis = mThumbLeftView.getRight() - mBarView.getLeft() - thumbPixelSize / 2;
                mProgressDrawable.setRangeLeftValue(relDis)
                        .invalidateSelf();

            }else if(child == mThumbRightView){

                if(left > getMeasuredWidth() - thumbPixelSize){
                    left = getMeasuredWidth() - thumbPixelSize;
                }

                if(left < mThumbLeftView.getRight()){
                    left = mThumbLeftView.getRight();
                }

                int relDis = mBarView.getRight() - mThumbRightView.getLeft() - thumbPixelSize / 2;
                mProgressDrawable.setRangeRightValue(relDis)
                        .invalidateSelf();

            } else {
                left = 0;
            }

            if(left > 0){
                mLeftProgress = getLeftProgress();
                mRightProgress = getRightProgress();
                if(mCallback != null){
                    mCallback.onProgressChanged(mLeftProgress, mRightProgress, true);
                }
            }

            return left;
        }
    };


    public int getLeftProgress(){
        float relDis = mThumbLeftView.getRight() - mBarView.getLeft() - thumbPixelSize;
        float percent = relDis / (mBarView.getMeasuredWidth() - thumbPixelSize * 2);
        return (int) (percent * (max - min) + min);
    }

    public int getRightProgress(){
        float relDis = mThumbRightView.getLeft() - mBarView.getLeft() - thumbPixelSize;
        float percent = relDis / (mBarView.getMeasuredWidth() - thumbPixelSize * 2);
        return (int) (percent * (max - min) + min);
    }

    public RangeBar setLeftProgress(int value){

        if(value < min
                || value > max
                || value > mRightProgress){
            throw new UnsupportedOperationException("Set Right ProgressValue");
        }

        mLeftProgress = value;
        if(!ViewCompat.isAttachedToWindow(this)){
            return this;
        }

        float percent = (float)(value - min) / (max - min);
        int right = (int) (percent * (mBarView.getMeasuredWidth() - thumbPixelSize * 2) + mBarView.getLeft() + thumbPixelSize);
        mThumbLeftView.layout(right-thumbPixelSize, getPaddingTop(), right, getPaddingTop() + thumbPixelSize);

        int relDis = mThumbLeftView.getRight() - mBarView.getLeft() - thumbPixelSize / 2;
        mProgressDrawable.setRangeLeftValue(relDis)
                .invalidateSelf();

        return this;
    }

    public RangeBar setRightProgress(int value){

        if(value < mLeftProgress
                || value < min
                || value > max){
            throw new UnsupportedOperationException("Set Right ProgressValue");
        }

        mRightProgress = value;
        if(!ViewCompat.isAttachedToWindow(this)){
            return this;
        }

        float percent = (float)(value - min) / (max - min);
        int left = (int) (percent * (mBarView.getMeasuredWidth() - thumbPixelSize * 2) + mBarView.getLeft() + thumbPixelSize);
        mThumbRightView.layout(left, getPaddingTop(), left+ thumbPixelSize, getPaddingTop() + thumbPixelSize);

        int relDis = mBarView.getRight() - mThumbRightView.getLeft() - thumbPixelSize / 2;
        mProgressDrawable.setRangeRightValue(relDis)
                .invalidateSelf();

        return this;
    }

    public interface ProgressCallback{
        boolean onProgressChanged(int leftProgress, int rightProgress, boolean isFromUser);
    }
}
