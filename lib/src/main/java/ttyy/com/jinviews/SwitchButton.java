package ttyy.com.jinviews;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * author: admin
 * date: 2017/05/10
 * version: 0
 * mail: secret
 * desc: ToggleButton
 */

public class SwitchButton extends View {

    ArgbEvaluator mArgbEvalutar;
    ValueAnimator mAnimator;

    int mToggledTrueColor;
    int mToggledFalseColor;

    float mStrokeWidth;
    int mStrokeColor;

    Paint mTogglePaint;

    float mToggleBarHeight;

    float mToggleCircleRadius;
    float mToggleCircleMargin;
    int mToggleCircleColor;

    boolean mIsToggledTrue;

    OnClickListener mInnerClickListener;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Paint csp = new Paint();
        // Android5.0 解决裁剪完成之后其他区域为黑色的问题
        csp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, csp);
        mInnerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        };
        super.setOnClickListener(mInnerClickListener);

        load(attrs);
    }

    void load(AttributeSet attrs) {

        mToggledFalseColor = Color.parseColor("#999999");
        mToggledTrueColor = Color.parseColor("#00ff00");
        mStrokeColor = Color.TRANSPARENT;
        mToggleCircleColor = Color.WHITE;

        mArgbEvalutar = new ArgbEvaluator();
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) (animation.getAnimatedValue());
                if (!mIsToggledTrue) {
                    f = 1 - f;
                }

                mCircleXOffset = (getMeasuredWidth() - mCircleRect.width() - mToggleCircleMargin * 2) * f + mToggleCircleMargin;
                postInvalidate();
            }
        });

        mTogglePaint = new Paint();
        mTogglePaint.setFilterBitmap(true);
        mTogglePaint.setAntiAlias(true);

        mStrokeWidth = 1.3f;
        mIsToggledTrue = false;

        mToggleCircleMargin = 10;

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchButton);

            mStrokeWidth = ta.getDimension(R.styleable.SwitchButton_toggleStrokeWidth, mStrokeWidth);
            mStrokeColor = ta.getColor(R.styleable.SwitchButton_toggleStrokeColor, mStrokeColor);

            mToggleCircleRadius = ta.getDimension(R.styleable.SwitchButton_toggleCircleRadius, mToggleCircleRadius);
            mToggleCircleColor = ta.getColor(R.styleable.SwitchButton_toggleCircleColor, mToggleCircleColor);
            mToggleCircleMargin = ta.getDimension(R.styleable.SwitchButton_toggleCircleMargin, mToggleCircleMargin);

            mToggleBarHeight = ta.getDimension(R.styleable.SwitchButton_toggleBarHeight, mToggleBarHeight);

            mToggledTrueColor = ta.getColor(R.styleable.SwitchButton_toggleTrueColor, mToggledTrueColor);
            mToggledFalseColor = ta.getColor(R.styleable.SwitchButton_toggleFalseColor, mToggledFalseColor);

            mIsToggledTrue = ta.getBoolean(R.styleable.SwitchButton_toggleStatus, mIsToggledTrue);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mToggleCircleRadius <= 0) {
            float width = getMeasuredWidth();
            int height = getMeasuredHeight();

            float fraction = width / height;

            if (fraction > 2.5) {
                mToggleCircleRadius = height / 2 - mToggleCircleMargin;
            } else {
                mToggleCircleRadius = width / 2.5f - mToggleCircleMargin;
            }
        }

        if (mToggleBarHeight <= 0) {
            float width = getMeasuredWidth();
            int height = getMeasuredHeight();

            float fraction = width / height;

            if (fraction > 2.5) {
                mToggleBarHeight = height;
            } else {
                mToggleBarHeight = width / 2.5f * 2;
            }
        }

        if (mBarPath == null
                || mCircleRect == null) {
            float mBarX = 0;
            float mBarY = getMeasuredHeight() / 2 - mToggleBarHeight / 2;

            // 左侧条形bar圆弧
            RectF mBarLeftRect = new RectF(mBarX, mBarY, mBarX + mToggleBarHeight, mBarY + mToggleBarHeight);
            // 右侧条形bar圆弧
            mBarX = getMeasuredWidth();
            RectF mBarRightRect = new RectF(mBarX - mToggleBarHeight, mBarY, mBarX, mBarY + mToggleBarHeight);

            mBarPath = new Path();
            mBarPath.moveTo(mBarLeftRect.right / 2, getHeight() / 2 - mBarLeftRect.height() / 2);
            mBarPath.arcTo(mBarLeftRect, 270, -180);
            mBarPath.arcTo(mBarRightRect, 90, -180);
            mBarPath.close();

            // 移动的toggle circle
            mCircleRect = new RectF(0, 0, mToggleCircleRadius * 2, mToggleCircleRadius * 2);
        }

        if (mIsToggledTrue) {
            // circle显示在右侧为true
            mCircleXOffset = getMeasuredWidth() - mCircleRect.width() - mToggleCircleMargin;
        } else {
            // circle显示在左侧为false
            mCircleXOffset = mToggleCircleMargin;
        }

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
    }

    public void toggle() {

        setToggle(!mIsToggledTrue);
    }

    public void setToggle(boolean value) {

        if (mIsToggledTrue == value) {
            return;
        }

        if(isViewAttachToWindow()){
            if (mAnimator.isRunning()) {
                return;
            } else {
                mIsToggledTrue = value;
                mAnimator.start();
            }
        }else {
            mIsToggledTrue = value;
            if(mCircleRect == null
                    || mBarPath == null){
                // View 还没有测量
                return;
            }

            if(mIsToggledTrue){
                mCircleXOffset = getMeasuredWidth() - mCircleRect.width() - mToggleCircleMargin;
            }else {
                mCircleXOffset = mToggleCircleMargin;
            }
            postInvalidate();
        }
    }

    boolean isViewAttachToWindow(){
        return getWindowToken() != null;
    }

    Path mBarPath;
    RectF mCircleRect;
    float mCircleXOffset;

    boolean isFirstDraw = true;
    @Override
    protected void onDraw(Canvas canvas) {
        int color = -1;
        if(!isFirstDraw){
            if (mIsToggledTrue) {
                // 从左向右
                color = (int) mArgbEvalutar.evaluate((float) mAnimator.getAnimatedValue(), mToggledFalseColor, mToggledTrueColor);
            } else {
                // 从右向左
                color = (int) mArgbEvalutar.evaluate((float) mAnimator.getAnimatedValue(), mToggledTrueColor, mToggledFalseColor);
            }
        }else {
            if (mIsToggledTrue) {
                // 从左向右
                color = mToggledTrueColor;
            } else {
                // 从右向左
                color = mToggledFalseColor;
            }
            isFirstDraw = false;
        }

        canvas.save();
        canvas.clipPath(mBarPath);
        canvas.drawColor(color);
        canvas.restore();

        drawToggleBar(canvas);
        drawToggleCircle(canvas);
    }

    void drawToggleBar(Canvas canvas) {

        mTogglePaint.setStyle(Paint.Style.STROKE);
        mTogglePaint.setStrokeWidth(mStrokeWidth);
        mTogglePaint.setColor(mStrokeColor);

        canvas.drawPath(mBarPath, mTogglePaint);
    }

    void drawToggleCircle(Canvas canvas) {
        canvas.save();

        canvas.translate(mCircleXOffset, getMeasuredHeight() / 2 - mCircleRect.height() / 2);

        mTogglePaint.setStyle(Paint.Style.STROKE);
        mTogglePaint.setStrokeWidth(mStrokeWidth);
        mTogglePaint.setColor(mStrokeColor);
        canvas.drawArc(mCircleRect, 0, 360, true, mTogglePaint);

        mTogglePaint.setStyle(Paint.Style.FILL);
        mTogglePaint.setColor(mToggleCircleColor);
        canvas.drawArc(mCircleRect, 0, 360, true, mTogglePaint);

        canvas.restore();
    }

}
