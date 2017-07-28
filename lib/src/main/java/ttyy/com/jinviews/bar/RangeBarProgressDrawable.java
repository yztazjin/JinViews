package ttyy.com.jinviews.bar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * author: admin
 * date: 2017/07/28
 * version: 0
 * mail: secret
 * desc: RangeDrawable
 */

class RangeBarProgressDrawable extends Drawable{

    Paint mPaint;

    RectF mUnRangedRect;
    RectF mRangedRect;
    int corners;

    int mRangedColor;
    int mUnRangedColor;

    int mRangeLeftValue;
    int mRangeRightValue;

    RangeBarProgressDrawable(){
        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);

        mRangedColor = Color.parseColor("#ff00ff");
        mUnRangedColor = Color.parseColor("#eeeeee");
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        mUnRangedRect = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
        mRangedRect = new RectF(bounds.left + mRangeLeftValue, bounds.top, bounds.right - mRangeRightValue, bounds.bottom);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mUnRangedRect = new RectF(left, top, right, bottom);
        mRangedRect = new RectF(left + mRangeLeftValue, top, right - mRangeRightValue, bottom);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(mUnRangedColor);
        canvas.drawRoundRect(mUnRangedRect, corners, corners, mPaint);

        mPaint.setColor(mRangedColor);
        canvas.drawRoundRect(mRangedRect, corners, corners, mPaint);
    }

    protected RangeBarProgressDrawable setRangeColor(int color){
        mRangedColor =color;
        return this;
    }

    protected RangeBarProgressDrawable setUnRangeColor(int color){
        mUnRangedColor = color;
        return this;
    }

    protected RangeBarProgressDrawable setCorners(int value){
        corners = value;
        return this;
    }

    protected RangeBarProgressDrawable setRangeLeftValue(int value){
        mRangeLeftValue = value;
        if(mRangedRect != null){
            mRangedRect.left = mUnRangedRect.left + value;
        }
        return this;
    }

    protected RangeBarProgressDrawable setRangeRightValue(int value){
        mRangeRightValue = value;
        if(mRangedRect != null){
            mRangedRect.right = mUnRangedRect.right - value;
        }
        return this;
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
