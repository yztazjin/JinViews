package ttyy.com.jinviews.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ttyy.com.jinviews.R;

/**
 * author: admin
 * date: 2017/06/06
 * version: 0
 * mail: secret
 * desc: ChatBubblesLinearLayout
 */

public class ChatBubblesLinearLayout extends LinearLayout implements ChatBubbleIntf {

    Paint mBubblePaint;

    Path mBubbleRectPath;

    int mBubbleStuffColor;
    Shader mBubblePaintShader;
    Bitmap mBitmap;

    int mBubbleRectStrokeColor;
    int mBubbleRectStrokeWidth;

    // 聊天气泡框圆弧
    int mBubbleRectRadius;
    // 箭头位置
    int mArrowLocation;
    // 箭头在Y轴方向上的大小
    int mArrowDimensionSizeInY;
    // 箭头在X轴方向上的大小
    int mArrowDimensionSizeInX;

    // 箭头在X轴上的偏移
    int mArrowOffsetXDist;
    // 箭头在Y轴上的偏移
    int mArrowOffsetYDist;

    public ChatBubblesLinearLayout(Context context) {
        this(context, null);
    }

    public ChatBubblesLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatBubblesLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChatBubblesLinearLayout);
            mBubbleRectStrokeColor = ta.getColor(R.styleable.ChatBubblesLinearLayout_llBubbleStrokeColor, Color.parseColor("#cccccc"));
            mBubbleRectStrokeWidth = ta.getDimensionPixelSize(R.styleable.ChatBubblesLinearLayout_llBubbleStrokeWidth, 0);
            mBubbleStuffColor = ta.getColor(R.styleable.ChatBubblesLinearLayout_llBubbleStuffColor, Color.WHITE);
            mBubbleRectRadius = ta.getDimensionPixelOffset(R.styleable.ChatBubblesLinearLayout_llBubbleRectRadius, 30);

            mArrowDimensionSizeInY = ta.getDimensionPixelSize(R.styleable.ChatBubblesLinearLayout_llBubbleArrowDimensionInY, 24);
            mArrowDimensionSizeInX = ta.getDimensionPixelSize(R.styleable.ChatBubblesLinearLayout_llBubbleArrowDimensionInX, 20);

            int bubbleRectImageResId = ta.getResourceId(R.styleable.ChatBubblesLinearLayout_llBubbleRectImage, -1);
            if(bubbleRectImageResId != -1){
                mBitmap = BitmapFactory.decodeResource(getResources(), bubbleRectImageResId);
            }

            mArrowLocation = ta.getInt(R.styleable.ChatBubblesLinearLayout_llBubbleArrowLocation, ARROW_LOCATION_LEFT);
            mArrowOffsetXDist = ta.getDimensionPixelOffset(R.styleable.ChatBubblesLinearLayout_llBubbleArrowOffsetXDist, 30);
            mArrowOffsetYDist = ta.getDimensionPixelOffset(R.styleable.ChatBubblesLinearLayout_llBubbleArrowOffsetYDist, 30);

            ta.recycle();
        } else {
            mBubbleRectStrokeColor = Color.parseColor("#cccccc");
            mBubbleRectStrokeWidth = 0;
            mBubbleStuffColor = Color.WHITE;
            mBubbleRectRadius = 30;

            mArrowDimensionSizeInY = 24;
            mArrowDimensionSizeInX = 20;

            mArrowLocation = ARROW_LOCATION_LEFT;
            mArrowOffsetXDist = 30;
            mArrowOffsetYDist = 30;
        }

        switch (mArrowLocation){
            case ARROW_LOCATION_LEFT:
                setPadding(getPaddingLeft() + getArrowDimensionSizeInX(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
                break;
            case ARROW_LOCATION_RIGHT:
                setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + getArrowDimensionSizeInX(), getPaddingBottom());
                break;
            case ARROW_LOCATION_TOP:
                setPadding(getPaddingLeft(), getPaddingTop() + getArrowDimensionSizeInY(), getPaddingRight(), getPaddingBottom());
                break;
            case ARROW_LOCATION_BOTTOM:
                setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + getArrowDimensionSizeInY());
                break;
        }

        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setFilterBitmap(true);
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setStrokeWidth(mBubbleRectStrokeWidth);
    }

    @Override
    public void setBubbleRectImageResource(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        postInvalidate();
    }

    @Override
    public void setBubbleRectImageBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        postInvalidate();
    }

    @Override
    public void setBubbleRectStrokeColor(int color) {
        mBubbleRectStrokeColor = color;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mBubbleRectPath = null;
        mBubblePaintShader = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBubblePaint.setStyle(Paint.Style.FILL);
        if (getPaintBubbleRectShader() != null) {
            mBubblePaint.setShader(mBubblePaintShader);
        } else {
            mBubblePaint.setColor(mBubbleStuffColor);
        }
        drawChatBubbleRect(canvas);

        if (mBubbleRectStrokeWidth > 0) {
            mBubblePaint.setColor(mBubbleRectStrokeColor);
            mBubblePaint.setShader(null);
            mBubblePaint.setStyle(Paint.Style.STROKE);
            drawChatBubbleRect(canvas);
        }
    }

    @Override
    public int getArrowLocation() {
        return mArrowLocation;
    }

    @Override
    public Shader getPaintBubbleRectShader() {

        if (mBubblePaintShader != null) {
            return mBubblePaintShader;
        }

        if (mBitmap == null) {
            return null;
        }

        // 适配处理
        Matrix matrix = new Matrix();

        float bmWidth = mBitmap.getWidth();
        float bmHeight = mBitmap.getHeight();

        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();

        float sx = viewWidth / bmWidth;
        float sy = viewHeight / bmHeight;

        matrix.postScale(sx, sy);

        Bitmap tmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        Canvas tmpCanvas = new Canvas(tmp);
        tmpCanvas.drawColor(Color.WHITE);
        tmpCanvas.drawBitmap(mBitmap, matrix, mBubblePaint);

        mBubblePaintShader = new BitmapShader(tmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmap.recycle();
        tmp.recycle();
        return mBubblePaintShader;
    }

    @Override
    public int getPaintBubbleRectColor() {
        return mBubblePaint.getColor();
    }

    @Override
    public void drawChatBubbleRect(Canvas canvas) {
        canvas.drawPath(getBubbleRectPath(), mBubblePaint);
    }

    @Override
    public int getOffsetXDist() {
        return mArrowOffsetXDist;
    }

    @Override
    public int getOffsetYDist() {
        return mArrowOffsetYDist;
    }

    @Override
    public int getArrowDimensionSizeInX() {
        return mArrowDimensionSizeInX;
    }

    @Override
    public int getArrowDimensionSizeInY() {
        return mArrowDimensionSizeInY;
    }

    @Override
    public int getBubbleRectRadius() {
        return mBubbleRectRadius;
    }

    @Override
    public Path getBubbleRectPath() {

        if (mBubbleRectPath == null) {

            mBubbleRectPath = new Path();
            int ARROW_LOCATION = getArrowLocation();
            switch (ARROW_LOCATION) {
                case ARROW_LOCATION_LEFT:

                    mBubbleRectPath.moveTo(getArrowDimensionSizeInX(), getBubbleRectRadius() / 2);
                    // 左上角
                    mBubbleRectPath.arcTo(new RectF(getArrowDimensionSizeInX(), 0, getBubbleRectRadius() + getArrowDimensionSizeInX(), getBubbleRectRadius()),
                            180, 90);
                    // 上横线
//                    mBubbleRectPath.lineTo(getWidth() - getBubbleRectRadius() / 2, 0);
                    // 右上角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius(), 0, getWidth(), getBubbleRectRadius()),
                            270, 90);
                    // 右竖线
//                    mBubbleRectPath.lineTo(getWidth(), getHeight() - getBubbleRectRadius() / 2);
                    // 右下角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius(), getHeight() - getBubbleRectRadius(), getWidth(), getHeight()),
                            0, 90);
                    // 下横线
//                    mBubbleRectPath.lineTo(getBubbleRectRadius() / 2+getArrowDimensionSizeInX(), getHeight());
                    // 左下角
                    mBubbleRectPath.arcTo(new RectF(getArrowDimensionSizeInX(), getHeight() - getBubbleRectRadius(), getBubbleRectRadius() + getArrowDimensionSizeInX(), getHeight()),
                            90, 90);
                    // 左竖线 + 左箭头
                    if (getOffsetYDist() >= 0) {
                        // 箭头居上
                        mBubbleRectPath.lineTo(getArrowDimensionSizeInX(), getOffsetYDist() + getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getArrowDimensionSizeInX(), getOffsetYDist() + getArrowDimensionSizeInY(),
                                0, getOffsetYDist() + getArrowDimensionSizeInY() / 2);
                        mBubbleRectPath.quadTo(0, getOffsetYDist() + getArrowDimensionSizeInY() / 2,
                                getArrowDimensionSizeInX(), getOffsetYDist());
                    } else {
                        // 箭头居下
                        mBubbleRectPath.lineTo(getArrowDimensionSizeInX(), getHeight() + getOffsetYDist() - getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getArrowDimensionSizeInX(), getHeight() + getOffsetYDist() - getArrowDimensionSizeInY(),
                                0, getHeight() + getOffsetYDist() - getArrowDimensionSizeInY() / 2);
                        mBubbleRectPath.quadTo(0, getHeight() + getOffsetYDist() - getArrowDimensionSizeInY() / 2,
                                getArrowDimensionSizeInX(), getHeight() + getOffsetYDist());
                    }

                    mBubbleRectPath.lineTo(getArrowDimensionSizeInX(), getBubbleRectRadius() / 2);

                    break;
                case ARROW_LOCATION_RIGHT:

                    mBubbleRectPath.moveTo(0, getBubbleRectRadius() / 2);
                    // 左上角
                    mBubbleRectPath.arcTo(new RectF(0, 0, getBubbleRectRadius(), getBubbleRectRadius()),
                            180, 90);
                    // 上横线
//                    mBubbleRectPath.lineTo(getWidth() - getBubbleRectRadius() / 2, 0);
                    // 右上角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius() - getArrowDimensionSizeInX(), 0, getWidth() - getArrowDimensionSizeInX(), getBubbleRectRadius()),
                            270, 90);
                    // 右竖线 + 右箭头
                    if (getOffsetYDist() >= 0) {
                        // 箭头居上
                        mBubbleRectPath.lineTo(getWidth() - getArrowDimensionSizeInX(), getOffsetYDist());
                        mBubbleRectPath.quadTo(getWidth() - getArrowDimensionSizeInX(), getOffsetYDist(),
                                getWidth(), getOffsetYDist() + getArrowDimensionSizeInY() / 2);
                        mBubbleRectPath.quadTo(getWidth(), getOffsetYDist() + getArrowDimensionSizeInY() / 2,
                                getWidth() - getArrowDimensionSizeInX(), getOffsetYDist() + getArrowDimensionSizeInY());
                    } else {
                        // 箭头居下
                        mBubbleRectPath.lineTo(getWidth() - getArrowDimensionSizeInX(), getHeight() + getOffsetYDist() - getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getWidth() - getArrowDimensionSizeInX(), getHeight() + getOffsetYDist() - getArrowDimensionSizeInY(),
                                getWidth(), getHeight() + getOffsetYDist() - getArrowDimensionSizeInY() / 2);
                        mBubbleRectPath.quadTo(getWidth(), getHeight() + getOffsetYDist() - getArrowDimensionSizeInY() / 2,
                                getWidth() - getArrowDimensionSizeInX(), getHeight() + getOffsetYDist());
                    }
                    mBubbleRectPath.lineTo(getWidth() - getArrowDimensionSizeInX(), getHeight() - getBubbleRectRadius() / 2);

                    // 右下角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius() - getArrowDimensionSizeInX(), getHeight() - getBubbleRectRadius(), getWidth() - getArrowDimensionSizeInX(), getHeight()),
                            0, 90);
                    // 下横线
//                    mBubbleRectPath.lineTo(getBubbleRectRadius() / 2+getArrowDimensionSizeInX(), getHeight());
                    // 左下角
                    mBubbleRectPath.arcTo(new RectF(0, getHeight() - getBubbleRectRadius(), getBubbleRectRadius(), getHeight()),
                            90, 90);
                    // 左竖线
//                    mBubbleRectPath.lineTo(getArrowDimensionSizeInX(), getBubbleRectRadius() / 2);

                    break;
                case ARROW_LOCATION_TOP:

                    mBubbleRectPath.moveTo(0, getBubbleRectRadius() / 2 + getArrowDimensionSizeInY());
                    // 左上角
                    mBubbleRectPath.arcTo(new RectF(0, getArrowDimensionSizeInY(), getBubbleRectRadius(), getBubbleRectRadius() + getArrowDimensionSizeInY()),
                            180, 90);
                    // 上横线 + 上箭头
                    if (getOffsetXDist() >= 0) {
                        // 箭头居左
                        mBubbleRectPath.lineTo(getOffsetXDist(), getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getOffsetXDist(), getArrowDimensionSizeInY(),
                                getOffsetXDist() + getArrowDimensionSizeInX() / 2, 0);
                        mBubbleRectPath.quadTo(getOffsetXDist() + getArrowDimensionSizeInX() / 2, 0,
                                getOffsetXDist() + getArrowDimensionSizeInX(), getArrowDimensionSizeInY());
                    } else {
                        // 箭头居右
                        mBubbleRectPath.lineTo(getWidth() + getOffsetXDist() - getArrowDimensionSizeInX(), getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getWidth() + getOffsetXDist() - getArrowDimensionSizeInX(), getArrowDimensionSizeInY(),
                                getWidth() + getOffsetXDist() - getArrowDimensionSizeInX() / 2, 0);
                        mBubbleRectPath.quadTo(getWidth() + getOffsetXDist() - getArrowDimensionSizeInX() / 2, 0,
                                getWidth() + getOffsetXDist(), getArrowDimensionSizeInY());
                    }
                    mBubbleRectPath.lineTo(getWidth() - getBubbleRectRadius() / 2, getArrowDimensionSizeInY());
                    // 右上角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius(), getArrowDimensionSizeInY(), getWidth(), getBubbleRectRadius() + getArrowDimensionSizeInY()),
                            270, 90);
                    // 右竖线
//                    mBubbleRectPath.lineTo(getWidth() - getArrowDimensionSizeInX(), getHeight() - getBubbleRectRadius() / 2);
                    // 右下角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius(), getHeight() - getBubbleRectRadius(), getWidth(), getHeight()),
                            0, 90);
                    // 下横线
//                    mBubbleRectPath.lineTo(getBubbleRectRadius() / 2+getArrowDimensionSizeInX(), getHeight());
                    // 左下角
                    mBubbleRectPath.arcTo(new RectF(0, getHeight() - getBubbleRectRadius(), getBubbleRectRadius(), getHeight()),
                            90, 90);
                    // 左竖线
//                    mBubbleRectPath.lineTo(getArrowDimensionSizeInX(), getBubbleRectRadius() / 2);

                    break;
                case ARROW_LOCATION_BOTTOM:

                    mBubbleRectPath.moveTo(0, getBubbleRectRadius() / 2);
                    // 左上角
                    mBubbleRectPath.arcTo(new RectF(0, 0, getBubbleRectRadius(), getBubbleRectRadius()),
                            180, 90);
                    // 上横线
//                    mBubbleRectPath.lineTo(getWidth() - getBubbleRectRadius() / 2, 0);
                    // 右上角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius(), 0, getWidth(), getBubbleRectRadius()),
                            270, 90);
                    // 右竖线
//                    mBubbleRectPath.lineTo(getWidth() - getArrowDimensionSizeInX(), getHeight() - getBubbleRectRadius() / 2);
                    // 右下角
                    mBubbleRectPath.arcTo(new RectF(getWidth() - getBubbleRectRadius(), getHeight() - getBubbleRectRadius() - getArrowDimensionSizeInY(), getWidth(), getHeight() - getArrowDimensionSizeInY()),
                            0, 90);
                    // 下横线 + 下箭头
                    if (getOffsetXDist() >= 0) {
                        // 箭头居左侧
                        mBubbleRectPath.lineTo(getOffsetXDist(), getHeight() - getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getOffsetXDist(), getHeight() - getArrowDimensionSizeInY(),
                                getOffsetXDist() + getArrowDimensionSizeInX() / 2, getHeight());
                        mBubbleRectPath.quadTo(getOffsetXDist() + getArrowDimensionSizeInX() / 2, getHeight(),
                                getOffsetXDist() + getArrowDimensionSizeInX(), getHeight() - getArrowDimensionSizeInY());
                    } else {
                        // 箭头居右侧
                        mBubbleRectPath.lineTo(getWidth() + getOffsetXDist(), getHeight() - getArrowDimensionSizeInY());
                        mBubbleRectPath.quadTo(getWidth() + getOffsetXDist(), getHeight() - getArrowDimensionSizeInY(),
                                getWidth() + getOffsetXDist() - getArrowDimensionSizeInX() / 2, getHeight());
                        mBubbleRectPath.quadTo(getWidth() + getOffsetXDist() - getArrowDimensionSizeInX() / 2, getHeight(),
                                getWidth() + getOffsetXDist() - getArrowDimensionSizeInX(), getHeight() - getArrowDimensionSizeInY());
                    }
                    mBubbleRectPath.lineTo(getBubbleRectRadius() / 2, getHeight() - getArrowDimensionSizeInY());
                    // 左下角
                    mBubbleRectPath.arcTo(new RectF(0, getHeight() - getBubbleRectRadius() - getArrowDimensionSizeInY(), getBubbleRectRadius(), getHeight() - getArrowDimensionSizeInY()),
                            90, 90);
                    // 左竖线
//                    mBubbleRectPath.lineTo(getArrowDimensionSizeInX(), getBubbleRectRadius() / 2);

                    break;
            }

            mBubbleRectPath.close();
        }

        return mBubbleRectPath;
    }
}
