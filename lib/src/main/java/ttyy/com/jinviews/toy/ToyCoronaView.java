package ttyy.com.jinviews.toy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author: admin
 * date: 2017/05/24
 * version: 0
 * mail: secret
 * desc: 轮盘View
 */

public class ToyCoronaView extends ImageView {

    RectF mInnerRect;

    RectF mOuterRect;

    Path mTriangleArrow;

    Paint paint;

    int mTextColor = Color.parseColor("#333333");
    TextPaint mTextPaint;

    String mText = "巡航";

    PressListener mPressListener;

    public ToyCoronaView(Context context) {
        this(context, null);
    }

    public ToyCoronaView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ToyCoronaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Paint csp = new Paint();
        // Android5.0 解决裁剪完成之后其他区域为黑色的问题
        csp.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, csp);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int r = getRadius();

        mOuterRect = new RectF(0, 0, r * 8 / 10, r * 8 / 10);

        mInnerRect = new RectF(0, 0, r * 1 / 2, r * 1 / 2);

        mTriangleArrow = new Path();

        mTextPaint.setTextSize(r * 0.14f);

        float density = getResources().getDisplayMetrics().density;

        float triangle1X = -3.5f * density;
        float triangle1Y = r / 10f + 2.5f * density;

        float triangle2X = 3.5f * density;
        float triangle2Y = r / 10f + 2.5f * density;

        float triangle3X = 0;
        float triangle3Y = r / 10f - 2.5f * density;

        mTriangleArrow.moveTo(triangle1X, triangle1Y);
        mTriangleArrow.quadTo(triangle1X, triangle1Y, triangle2X, triangle2Y);
        mTriangleArrow.quadTo(triangle2X, triangle2Y, triangle3X, triangle3Y);
        mTriangleArrow.quadTo(triangle3X, triangle3Y, triangle1X, triangle1Y);
        mTriangleArrow.close();
    }


    int getRadius() {
        return getMeasuredWidth() > getMeasuredHeight() ? getMaxHeight() : getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int dist = getDistanceToCircleCenter(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if(mPressingTimer != null){
                    mPressingTimer.cancel();
                    mPressingTimer = null;
                }

                if (dist < getRadius() * 3f / 10 - 10
                        || dist > getRadius() / 2f + 10) {

                    currentFocusIndex = -1;
                    postInvalidate();

                } else {

                    currentFocusIndex = getPressedItem(event);

                    if(mPressListener != null){
                        mPressingTimer = new Timer();
                        mPressingTimer.schedule(new ContinuePressTask(currentFocusIndex),0,360);
                    }
                    postInvalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if (dist < getRadius() * 3f / 10 - 10
                        || dist > getRadius() / 2f + 10) {

                    if(mPressingTimer != null){
                        mPressingTimer.cancel();
                        mPressingTimer = null;
                    }

                    if(currentFocusIndex != -1){
                        currentFocusIndex = -1;
                        postInvalidate();
                    }

                } else {

                    int index = getPressedItem(event);
                    if(index != currentFocusIndex
                            && currentFocusIndex != -1){

                        if(mPressingTimer != null){
                            mPressingTimer.cancel();
                            mPressingTimer = null;
                        }

                        currentFocusIndex = -1;
                        postInvalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:

                if(mPressingTimer != null){
                    mPressingTimer.cancel();
                    mPressingTimer = null;
                }

                int item = getPressedItem(event);
                if(item == currentFocusIndex){
                    if(mPressListener != null){
                        mPressListener.onSingleTap(item);
                    }
                }

                currentFocusIndex = -1;
                postInvalidate();
                break;
        }

        return true;
    }

    float getDegree(MotionEvent event) {
        int c = getDistanceToCircleCenter(event);
        double a = event.getY() - getMeasuredHeight() / 2;
        double sin = a / c;
        if (sin < -1) {
            sin = -1;
        }
        if (sin > 1) {
            sin = 1;
        }

        float degree = (int) (Math.asin(sin) / Math.PI * 180);

        int quadrant = getQuadrant(event);

        switch (quadrant) {
            case 1:
                degree = 112.5f + degree;
                break;
            case 2:
                if (degree < -67.5) {
                    degree = degree + 112.5f;
                } else {
                    degree = 360 + degree + 22.5f;
                }

                break;
            case 3:
                degree = 180 - degree + 112.5f;
                break;
            case 4:
                if (degree < 0) {
                    degree = 90 + degree + 112.5f;
                } else {
                    degree += 112.5f;
                }

                break;
        }

        return degree;
    }

    int getPressedItem(MotionEvent event) {
        float degree = getDegree(event);

        int index = (int) Math.floor(degree / 45f);

        return index;
    }

    /**
     * 获取象限
     *
     * @param event
     * @return
     */
    int getQuadrant(MotionEvent event) {

        float centerX = getMeasuredWidth() / 2f;
        float centerY = getMeasuredHeight() / 2f;

        float x = event.getX();
        float y = event.getY();

        if (x >= centerX
                && y <= centerY) {
            return 1;
        } else if (x < centerX && y < centerY) {

            return 2;
        } else if (x >= centerX
                && y >= centerY) {

            return 4;
        } else {

            return 3;
        }
    }

    int getDistanceToCircleCenter(MotionEvent event) {

        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        int dist = (int) Math.sqrt(Math.pow(event.getX() - centerX, 2) + Math.pow(event.getY() - centerY, 2));
        return dist;
    }

    public void setPressListener(PressListener listener){
        this.mPressListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawOuterBtns(canvas);

        drawInnerBtns(canvas);
    }

    int currentFocusIndex = -1;

    int mCircleBarColor = Color.WHITE;
    int mCenterCircleColor = Color.WHITE;
    int mDividerColor = Color.parseColor("#cccccc");
    int mArrowColor = Color.parseColor("#333333");

    void drawOuterBtns(Canvas canvas) {

        int degree = 45;
        float hafDegree = 45f / 2;
        int radius = getRadius() / 5;
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;

        canvas.save();
        canvas.translate((getMeasuredWidth() - mOuterRect.width()) / 2, (getMeasuredHeight() - mOuterRect.width()) / 2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius);
        paint.setColor(mCircleBarColor);
        canvas.drawOval(mOuterRect, paint);
        canvas.restore();

        for (int i = 0; i < 8; i++) {
            // 画选中
            if (currentFocusIndex == i) {

                paint.setColor(mDividerColor);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(radius);

                canvas.save();
                canvas.rotate(degree * i - hafDegree, centerX, centerY);
                canvas.translate((getMeasuredWidth() - mOuterRect.width()) / 2, (getMeasuredHeight() - mOuterRect.width()) / 2);
                canvas.drawArc(mOuterRect, -90, degree, false, paint);
                canvas.restore();
            }

            // 画箭头
            canvas.save();
            canvas.rotate(degree * i, centerX, centerY);
            canvas.translate(getMeasuredWidth() / 2, 0);
            paint.setColor(mArrowColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mTriangleArrow, paint);
            canvas.restore();

            // 画分界线
            paint.setColor(mDividerColor);
            paint.setStrokeWidth(1.4f);
            paint.setStyle(Paint.Style.STROKE);

            canvas.save();
            canvas.rotate(degree * i - hafDegree, centerX, centerY);
            canvas.drawLine(getMeasuredWidth() / 2, 0, getMeasuredWidth() / 2, radius, paint);

            canvas.rotate(degree, centerX, centerY);
            canvas.drawLine(getMeasuredWidth() / 2, 0, getMeasuredWidth() / 2, radius, paint);
            canvas.restore();
        }

    }

    void drawInnerBtns(Canvas canvas) {

        canvas.save();
        canvas.translate((getMeasuredWidth() - mInnerRect.width()) / 2, (getMeasuredHeight() - mInnerRect.width()) / 2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mCenterCircleColor);
        canvas.drawOval(mInnerRect, paint);
        canvas.restore();

        float cy = getMeasuredHeight() / 2f;
        float cx = getMeasuredWidth() /2f;
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        cy = cy - (fm.top + fm.bottom) / 2f;
        canvas.drawText(mText, 0, mText.length(), cx, cy, mTextPaint);

        Log.e("Test", "cx "+cx+" cy "+cy);

    }

    public interface PressListener{

        void onPressing(int item);

        void onSingleTap(int item);

    }

    Timer mPressingTimer;

    class ContinuePressTask extends TimerTask{

        int item;

        public ContinuePressTask(int item){
            this.item = item;
        }

        @Override
        public void run() {
            if(mPressListener != null
                    && item == currentFocusIndex){

                mPressListener.onPressing(item);
            }
        }
    }

}
