package ttyy.com.jinviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * author: admin
 * date: 2017/04/10
 * version: 0
 * mail: secret
 * desc: BeizerCircleIndicatedViewPager
 */

public class BeizerCircleIndicatedViewPager extends FrameLayout implements ViewPager.OnPageChangeListener {

    ViewPager mViewPager;

    ViewPager.OnPageChangeListener mPageChangeListener;

    Paint mPaint;

    IndicateInfo mIndicateInfo;

    public BeizerCircleIndicatedViewPager(Context context) {
        this(context, null);
    }

    public BeizerCircleIndicatedViewPager(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeizerCircleIndicatedViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mViewPager = new NestableViewPager(context);
        LayoutParams params = new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        mViewPager.setId("BeizerIndicatePager".hashCode());
        this.addView(mViewPager, params);
        mViewPager.addOnPageChangeListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mIndicateInfo = new IndicateInfo(context, attrs);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if(mIndicateInfo.mPagerCurrentIndex != position){
            mIndicateInfo.direction = mIndicateInfo.mPagerCurrentIndex > position ? -1 : 1;
            mIndicateInfo.mPagerCurrentIndex = position;
        }

        if(positionOffset == 0){
            mIndicateInfo.mPagerCurrentIndex = position;
            mIndicateInfo.direction = 1;
        }

        if (mIndicateInfo.direction > 0) {
            // pager向左
            mIndicateInfo.mPagerScrollingOffset = positionOffset;
        } else {
            // pager向右
            mIndicateInfo.mPagerScrollingOffset = 1 - positionOffset;
        }

        postInvalidate();

        if(mPageChangeListener != null){
            mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(mPageChangeListener != null){
            mPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(mPageChangeListener != null){
            mPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    public void setCurrentItem(int item){
        mViewPager.setCurrentItem(item, true);
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener){
        mPageChangeListener = listener;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mViewPager.getAdapter() == null
                || mViewPager.getAdapter().getCount() == 0) {
            return;
        }

        drawAllIndicatedCircles(canvas);

        drawIndicatingCircle(canvas);
    }

    void drawAllIndicatedCircles(Canvas canvas) {

        int mViewPagerCount = mViewPager.getAdapter().getCount();

        int indicateTotalWidth = (int) (mViewPagerCount * mIndicateInfo.getIndicateCircleRadius() * 2 + (mViewPagerCount - 1) * mIndicateInfo.getIndicatedDistance());

        float startX = getWidth() / 2 - indicateTotalWidth / 2 + mIndicateInfo.getIndicateCircleRadius();
        float startY = getHeight() - mIndicateInfo.getIndicatedBottomMargin();

        mPaint.setColor(mIndicateInfo.getIndicateColor());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mIndicateInfo.getIndicateCircleLineWidth());
        for (int i = 0; i < mViewPagerCount; i++) {
            canvas.save();
            canvas.translate(startX, startY);
            startX += mIndicateInfo.getIndicatedDistance() + mIndicateInfo.getIndicateCircleRadius() * 2;

            mIndicatingPath.reset();
            mIndicatingPath.moveTo(mIndicateInfo.mCircleRightLine.middle.x, mIndicateInfo.mCircleRightLine.middle.y);
            mIndicatingPath.cubicTo(mIndicateInfo.mCircleRightLine.top.x, mIndicateInfo.mCircleRightLine.top.y,
                    mIndicateInfo.mCircleTopLine.right.x, mIndicateInfo.mCircleTopLine.right.y,
                    mIndicateInfo.mCircleTopLine.middle.x, mIndicateInfo.mCircleTopLine.middle.y);
            mIndicatingPath.cubicTo(mIndicateInfo.mCircleTopLine.left.x, mIndicateInfo.mCircleTopLine.left.y,
                    mIndicateInfo.mCircleLeftLine.top.x, mIndicateInfo.mCircleLeftLine.top.y,
                    mIndicateInfo.mCircleLeftLine.middle.x, mIndicateInfo.mCircleLeftLine.middle.y);
            mIndicatingPath.cubicTo(mIndicateInfo.mCircleLeftLine.bottom.x, mIndicateInfo.mCircleLeftLine.bottom.y,
                    mIndicateInfo.mCircleBottomLine.left.x, mIndicateInfo.mCircleBottomLine.left.y,
                    mIndicateInfo.mCircleBottomLine.middle.x, mIndicateInfo.mCircleBottomLine.middle.y);
            mIndicatingPath.cubicTo(mIndicateInfo.mCircleBottomLine.right.x, mIndicateInfo.mCircleBottomLine.right.y,
                    mIndicateInfo.mCircleRightLine.bottom.x, mIndicateInfo.mCircleRightLine.bottom.y,
                    mIndicateInfo.mCircleRightLine.middle.x, mIndicateInfo.mCircleRightLine.middle.y);

            canvas.drawPath(mIndicatingPath, mPaint);
            canvas.restore();
        }
    }

    Path mIndicatingPath = new Path();
    Matrix matrix = new Matrix() {
        {
            postScale(-1, 1);
        }
    };

    void drawIndicatingCircle(Canvas canvas) {
        int mViewPagerCount = mViewPager.getAdapter().getCount();

        mPaint.setColor(mIndicateInfo.getIndicatingColor());
        mPaint.setStyle(Paint.Style.FILL);
        if (mViewPagerCount == 1) {
            canvas.drawCircle(getWidth() / 2, getHeight() - mIndicateInfo.getIndicatedBottomMargin(), mIndicateInfo.getIndicateCircleRadius(), mPaint);
        } else {
            try {
                canvas.save();

                float indicateTotalWidth = mViewPagerCount * mIndicateInfo.getIndicateCircleRadius() * 2 + (mViewPagerCount - 1) * mIndicateInfo.getIndicatedDistance();

                int realPagerIndex = mIndicateInfo.direction > 0 ? mIndicateInfo.mPagerCurrentIndex : mIndicateInfo.mPagerCurrentIndex + 1;
                float deltaX = realPagerIndex * (mIndicateInfo.getIndicateCircleRadius() * 2 + mIndicateInfo.getIndicatedDistance());
                float indicateMargin = mIndicateInfo.getIndicatedDistance() + mIndicateInfo.getIndicateCircleRadius() * 2;

                float translateX = getWidth() / 2 - indicateTotalWidth / 2 + mIndicateInfo.getIndicateCircleRadius() + deltaX;
                float translateY = getHeight() - mIndicateInfo.getIndicatedBottomMargin();

                float offset = mIndicateInfo.mPagerScrollingOffset;

                if (offset == 0) {
                    // 初始状态
                    mIndicateInfo.mRightLine.setX(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mRightLine.setY(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mTopLine.setY(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mBottomLine.setY(-mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mLeftLine.setX(-mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mLeftLine.setY(mIndicateInfo.getIndicateCircleRadius());

                } else if (offset <= 0.2f) {
                    // 右侧开始拉伸出圆圈
                    mIndicateInfo.mLeftLine.setX(-mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mLeftLine.setY(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mTopLine.setY(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mBottomLine.setY(-mIndicateInfo.getIndicateCircleRadius());

                    mIndicateInfo.mRightLine.setX(mIndicateInfo.getIndicateCircleRadius() + mIndicateInfo.mPagerScrollingOffset * indicateMargin);
                    mIndicateInfo.mRightLine.setY(mIndicateInfo.getIndicateCircleRadius() * (1 + offset));

                } else if (offset > 0.2f && offset <= 0.4f) {
                    // 左侧开始拉伸出圆圈
                    float leftOffset = mIndicateInfo.mPagerScrollingOffset - 0.2f;
                    leftOffset = leftOffset > 0.2f ? 0.2f : leftOffset;

                    mIndicateInfo.mRightLine.setX(mIndicateInfo.getIndicateCircleRadius() + 0.2f * indicateMargin);
                    mIndicateInfo.mRightLine.setY(mIndicateInfo.getIndicateCircleRadius() * 1.2f);
                    mIndicateInfo.mLeftLine.setX(-mIndicateInfo.getIndicateCircleRadius() - leftOffset * indicateMargin);
                    mIndicateInfo.mLeftLine.setY(mIndicateInfo.getIndicateCircleRadius() * (1 + leftOffset));

                    leftOffset = 1 - leftOffset;
                    mIndicateInfo.mTopLine.setY(mIndicateInfo.getIndicateCircleRadius() * leftOffset);
                    mIndicateInfo.mBottomLine.setY(-mIndicateInfo.getIndicateCircleRadius() * leftOffset);

                    translateX += mIndicateInfo.direction * indicateMargin * 1.25f * (offset - 0.2f);

                } else if(offset > 0.4f && offset <= 0.6f){
                    // 整个在拉伸状态平移
                    mIndicateInfo.mRightLine.setX(mIndicateInfo.getIndicateCircleRadius() + 0.2f * indicateMargin);
                    mIndicateInfo.mRightLine.setY(mIndicateInfo.getIndicateCircleRadius() * 1.2f);
                    mIndicateInfo.mLeftLine.setX(-mIndicateInfo.getIndicateCircleRadius() - 0.2f * indicateMargin);
                    mIndicateInfo.mLeftLine.setY(mIndicateInfo.getIndicateCircleRadius() * 1.2f);

                    mIndicateInfo.mTopLine.setY(mIndicateInfo.getIndicateCircleRadius() * 0.8f);
                    mIndicateInfo.mBottomLine.setY(-mIndicateInfo.getIndicateCircleRadius() * 0.8f);

                    translateX += mIndicateInfo.direction * indicateMargin * 1.25f * (offset - 0.2f);

                }else if (offset > 0.6f && offset <= 0.8f) {
                    // 右侧开始回拢
                    float rightOffset = 0.8f - offset;

                    mIndicateInfo.mLeftLine.setX(-mIndicateInfo.getIndicateCircleRadius() - 0.2f * indicateMargin);
                    mIndicateInfo.mLeftLine.setY(mIndicateInfo.getIndicateCircleRadius() * 1.2f);
                    mIndicateInfo.mRightLine.setX(mIndicateInfo.getIndicateCircleRadius() + rightOffset * indicateMargin);
                    mIndicateInfo.mRightLine.setY(mIndicateInfo.getIndicateCircleRadius() * (1 + rightOffset));

                    rightOffset = 1 - rightOffset;
                    mIndicateInfo.mTopLine.setY(mIndicateInfo.getIndicateCircleRadius() * rightOffset);
                    mIndicateInfo.mBottomLine.setY(-mIndicateInfo.getIndicateCircleRadius() * rightOffset);

                    translateX += mIndicateInfo.direction * indicateMargin * 1.25f * (offset - 0.2f);

                } else {
                    // 左侧回拢
                    float leftOffset = 1 - offset;

                    mIndicateInfo.mRightLine.setX(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mRightLine.setY(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mTopLine.setY(mIndicateInfo.getIndicateCircleRadius());
                    mIndicateInfo.mBottomLine.setY(-mIndicateInfo.getIndicateCircleRadius());

                    mIndicateInfo.mLeftLine.setX(-mIndicateInfo.getIndicateCircleRadius() - leftOffset * indicateMargin);
                    mIndicateInfo.mLeftLine.setY(mIndicateInfo.getIndicateCircleRadius() * (1 + leftOffset));

                    translateX += mIndicateInfo.direction * indicateMargin * 1.25f * (offset - 0.2f);
                }

                canvas.translate(translateX, translateY);

                mIndicatingPath.reset();
                mIndicatingPath.moveTo(mIndicateInfo.mRightLine.middle.x, mIndicateInfo.mRightLine.middle.y);
                mIndicatingPath.cubicTo(mIndicateInfo.mRightLine.top.x, mIndicateInfo.mRightLine.top.y,
                        mIndicateInfo.mTopLine.right.x, mIndicateInfo.mTopLine.right.y,
                        mIndicateInfo.mTopLine.middle.x, mIndicateInfo.mTopLine.middle.y);
                mIndicatingPath.cubicTo(mIndicateInfo.mTopLine.left.x, mIndicateInfo.mTopLine.left.y,
                        mIndicateInfo.mLeftLine.top.x, mIndicateInfo.mLeftLine.top.y,
                        mIndicateInfo.mLeftLine.middle.x, mIndicateInfo.mLeftLine.middle.y);
                mIndicatingPath.cubicTo(mIndicateInfo.mLeftLine.bottom.x, mIndicateInfo.mLeftLine.bottom.y,
                        mIndicateInfo.mBottomLine.left.x, mIndicateInfo.mBottomLine.left.y,
                        mIndicateInfo.mBottomLine.middle.x, mIndicateInfo.mBottomLine.middle.y);
                mIndicatingPath.cubicTo(mIndicateInfo.mBottomLine.right.x, mIndicateInfo.mBottomLine.right.y,
                        mIndicateInfo.mRightLine.bottom.x, mIndicateInfo.mRightLine.bottom.y,
                        mIndicateInfo.mRightLine.middle.x, mIndicateInfo.mRightLine.middle.y);

                // 反方向进行镜像矩阵变换
                if (mIndicateInfo.direction < 0) {
                    mIndicatingPath.transform(matrix);
                }

                canvas.drawPath(mIndicatingPath, mPaint);

            } finally {
                canvas.restore();
            }
        }
    }

    static class IndicateInfo {

        float mIndicateCircleRadius;
        float mIndicateCircleLineWidth;

        float mIndicateDistance;
        float mIndicateBottomMargin;

        int mIndicateColor;
        int mIndicatingColor;

        int mPagerCurrentIndex = -1;
        float mPagerScrollingOffset;// [0,1)
        int direction;// >0 pager向左 <0 pager向右

        CubicVerticalLine mRightLine;
        CubicVerticalLine mLeftLine;
        CubicHorizontalLine mTopLine;
        CubicHorizontalLine mBottomLine;

        CubicVerticalLine mCircleRightLine;
        CubicVerticalLine mCircleLeftLine;
        CubicHorizontalLine mCircleTopLine;
        CubicHorizontalLine mCircleBottomLine;

        private IndicateInfo(Context context, AttributeSet attrs) {
            mIndicateCircleRadius = context.getResources().getDisplayMetrics().density * 8;
            mIndicateCircleLineWidth = 2;

            mIndicateDistance = 2f * mIndicateCircleRadius;
            mIndicateBottomMargin = 2f * mIndicateCircleRadius;

            mIndicateColor = Color.WHITE;
            mIndicatingColor = Color.GREEN;

            if(attrs != null){
                TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BeizerCircleIndicatedViewPager);

                mIndicateCircleRadius = ta.getDimension(R.styleable.BeizerCircleIndicatedViewPager_indicateRadius, mIndicateCircleRadius);
                mIndicateCircleLineWidth = ta.getDimension(R.styleable.BeizerCircleIndicatedViewPager_strokeWidth, mIndicateCircleLineWidth);

                mIndicateDistance = ta.getDimension(R.styleable.BeizerCircleIndicatedViewPager_indicateDistance, mIndicateDistance);
                mIndicateBottomMargin = ta.getDimension(R.styleable.BeizerCircleIndicatedViewPager_indicateBottomMargin, mIndicateBottomMargin);

                mIndicateColor = ta.getColor(R.styleable.BeizerCircleIndicatedViewPager_strokeColor, mIndicateColor);
                mIndicatingColor = ta.getColor(R.styleable.BeizerCircleIndicatedViewPager_indicateColor, mIndicatingColor);
            }
            mIndicateCircleRadius += 0.5f;

            mRightLine = new CubicVerticalLine(mIndicateCircleRadius, mIndicateCircleRadius);
            mLeftLine = new CubicVerticalLine(-mIndicateCircleRadius, mIndicateCircleRadius);
            mTopLine = new CubicHorizontalLine(mIndicateCircleRadius, mIndicateCircleRadius);
            mBottomLine = new CubicHorizontalLine(mIndicateCircleRadius, -mIndicateCircleRadius);

            float mNormalCircleRadius = mIndicateCircleRadius - mIndicateCircleLineWidth / 2 - 0.5f;
            mCircleRightLine = new CubicVerticalLine(mNormalCircleRadius, mNormalCircleRadius);
            mCircleLeftLine = new CubicVerticalLine(-mNormalCircleRadius, mNormalCircleRadius);
            mCircleTopLine = new CubicHorizontalLine(mNormalCircleRadius, mNormalCircleRadius);
            mCircleBottomLine = new CubicHorizontalLine(mNormalCircleRadius, -mNormalCircleRadius);
        }

        float getIndicateCircleRadius() {
            return mIndicateCircleRadius;
        }

        float getIndicateCircleLineWidth() {
            return mIndicateCircleLineWidth;
        }

        float getIndicatedDistance() {
            return mIndicateDistance;
        }

        float getIndicatedBottomMargin() {
            return mIndicateBottomMargin;
        }

        int getIndicateColor() {
            return mIndicateColor;
        }

        int getIndicatingColor() {
            return mIndicatingColor;
        }
    }

    static class CubicHorizontalLine {

        PointF right = new PointF();
        PointF middle = new PointF();
        PointF left = new PointF();

        double beizerCircleControlFactor = 0.551915024494;

        private CubicHorizontalLine(float x, float y) {

            right.y = y;
            right.x = (float) (x * beizerCircleControlFactor);

            middle.y = y;
            middle.x = 0;

            left.y = y;
            left.x = (float) (-x * beizerCircleControlFactor);

        }

        void setY(float y) {
            right.y = y;
            middle.y = y;
            left.y = y;
        }

        void setX(float x) {
            right.x = (float) (x * beizerCircleControlFactor);

            middle.x = 0;

            left.x = (float) (-x * beizerCircleControlFactor);
        }

    }

    static class CubicVerticalLine {

        PointF top = new PointF();
        PointF middle = new PointF();
        PointF bottom = new PointF();

        double beizerCircleControlFactor = 0.551915024494;

        private CubicVerticalLine(float x, float y) {

            top.x = x;
            top.y = (float) (y * beizerCircleControlFactor);

            middle.x = x;
            middle.y = 0;

            bottom.x = x;
            bottom.y = (float) (-y * beizerCircleControlFactor);
        }

        void setX(float x) {
            top.x = x;
            middle.x = x;
            bottom.x = x;
        }

        void setY(float y) {
            top.y = (float) (y * beizerCircleControlFactor);
            middle.y = 0;
            bottom.y = (float) (-y * beizerCircleControlFactor);
        }

    }
}
