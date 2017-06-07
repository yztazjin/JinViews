package ttyy.com.jinviews.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import ttyy.com.jinviews.R;

/**
 * author: admin
 * date: 2017/06/06
 * version: 0
 * mail: secret
 * desc: ChatBubblesImageView
 */

public class ChatBubblesImageView extends ImageView implements ChatBubbleIntf {

    Paint mBubblePaint;

    Path mBubbleRectPath;

    int mBubbleStuffColor;
    Shader mBubblePaintShader;
    Bitmap mBitmap;
    Drawable mShaderSourceDrawable;

    int mBubbleRectStrokeColor;
    int mBubbleRectStrokeWidth;

    // 气泡框图片最大显示范围
    int mImageBubbleRectRangeMax;
    // 气泡框图片最小显示范围
    int mImageBubbleRectRangeMin;

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

    public ChatBubblesImageView(Context context) {
        this(context, null);
    }

    public ChatBubblesImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatBubblesImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChatBubblesImageView);
            mBubbleRectStrokeColor = ta.getColor(R.styleable.ChatBubblesImageView_ivBubbleStrokeColor, Color.parseColor("#cccccc"));
            mBubbleRectStrokeWidth = ta.getDimensionPixelSize(R.styleable.ChatBubblesImageView_ivBubbleStrokeWidth, 0);
            mBubbleStuffColor = ta.getColor(R.styleable.ChatBubblesImageView_ivBubbleStuffColor, Color.WHITE);
            mBubbleRectRadius = ta.getDimensionPixelOffset(R.styleable.ChatBubblesImageView_ivBubbleRectRadius, 30);

            mImageBubbleRectRangeMax = ta.getDimensionPixelSize(R.styleable.ChatBubblesImageView_ivBubbleImageRangeMax, 350);
            mImageBubbleRectRangeMin = ta.getDimensionPixelSize(R.styleable.ChatBubblesImageView_ivBubbleImageRangeMin, 160);
            int bubbleRectImageResId = ta.getResourceId(R.styleable.ChatBubblesImageView_ivBubbleRectImage, -1);
            if(bubbleRectImageResId != -1){
                Drawable drawable = getResources().getDrawable(bubbleRectImageResId);
                if(drawable instanceof BitmapDrawable){
                    mBitmap = ((BitmapDrawable)drawable).getBitmap();
                }else {
                    mShaderSourceDrawable = drawable;
                }
            }

            mArrowDimensionSizeInY = ta.getDimensionPixelSize(R.styleable.ChatBubblesImageView_ivBubbleArrowDimensionInY, 24);
            mArrowDimensionSizeInX = ta.getDimensionPixelSize(R.styleable.ChatBubblesImageView_ivBubbleArrowDimensionInX, 20);

            mArrowLocation = ta.getInt(R.styleable.ChatBubblesImageView_ivBubbleArrowLocation, ARROW_LOCATION_LEFT);
            mArrowOffsetXDist = ta.getDimensionPixelOffset(R.styleable.ChatBubblesImageView_ivBubbleArrowOffsetXDist, 30);
            mArrowOffsetYDist = ta.getDimensionPixelOffset(R.styleable.ChatBubblesImageView_ivBubbleArrowOffsetYDist, 30);

            ta.recycle();
        }else {
            mBubbleRectStrokeColor = Color.parseColor("#cccccc");
            mBubbleRectStrokeWidth = 0;
            mBubbleStuffColor = Color.WHITE;
            mBubbleRectRadius = 30;

            mImageBubbleRectRangeMax = 350;
            mImageBubbleRectRangeMin = 160;

            mArrowDimensionSizeInY = 24;
            mArrowDimensionSizeInX = 20;

            mArrowLocation = ARROW_LOCATION_LEFT;
            mArrowOffsetXDist = 30;
            mArrowOffsetYDist = 30;
        }

        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setFilterBitmap(true);
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setStrokeWidth(mBubbleRectStrokeWidth);
    }

    @Override
    public void setBubbleRectImageResource(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        setBackgroundDrawable(drawable);
    }

    @Override
    public void setBubbleRectImageBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        requestLayout();
    }

    @Override
    public void setBubbleRectStrokeColor(int color) {
        mBubbleRectStrokeColor = color;
        postInvalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.mBitmap = bm;
        requestLayout();
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if(background instanceof BitmapDrawable){
            mBitmap = ((BitmapDrawable)background).getBitmap();
            requestLayout();
        }else if(background instanceof ColorDrawable){
            mBubbleStuffColor = ((ColorDrawable)background).getColor();
            requestLayout();
        }else {
            mBubbleRectPath = null;
            mBubblePaintShader = null;
            mShaderSourceDrawable = background;
            postInvalidate();
        }
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        Drawable drawable = getResources().getDrawable(resid);
        setBackgroundDrawable(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mBitmap != null){
            float bmWidth = mBitmap.getWidth();
            float bmHeight = mBitmap.getHeight();

            int realWidth = 0;
            int realHeight = 0;
            if(bmWidth > mImageBubbleRectRangeMax
                    || bmHeight > mImageBubbleRectRangeMax){
                float sx = mImageBubbleRectRangeMax / bmWidth;
                float sy = mImageBubbleRectRangeMax / bmHeight;

                if(sx < sy){
                    // bitmap width > height
                    realWidth = (int) (bmWidth * sx);
                    realHeight = (int) (bmHeight * sx);
                }else {
                    realWidth = (int) (bmWidth * sy);
                    realHeight = (int) (bmHeight * sy);
                }
            }else if(bmWidth < mImageBubbleRectRangeMin
                    || bmHeight < mImageBubbleRectRangeMin){
                float sx = mImageBubbleRectRangeMin / bmWidth;
                float sy = mImageBubbleRectRangeMin / bmHeight;

                if(sx > sy){
                    // bitmap width > height
                    realWidth = (int) (bmWidth * sx);
                    realHeight = (int) (bmHeight * sx);
                }else {
                    realWidth = (int) (bmWidth * sy);
                    realHeight = (int) (bmHeight * sy);
                }
            }else {
                realWidth = (int) bmWidth;
                realHeight = (int) bmHeight;
            }

            mBubbleRectPath = null;
            mBubblePaintShader = null;

            setMeasuredDimension(realWidth + MeasureSpec.EXACTLY,
                    realHeight + MeasureSpec.EXACTLY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBubblePaint.setStyle(Paint.Style.FILL);
        if (getPaintBubbleRectShader() != null) {
            mBubblePaint.setShader(mBubblePaintShader);
        }else {
            mBubblePaint.setColor(mBubbleStuffColor);
        }
        drawChatBubbleRect(canvas);

        if(mBubbleRectStrokeWidth > 0){
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
        if(mBubblePaintShader != null){
            return mBubblePaintShader;
        }

        if(mBitmap == null
                && mShaderSourceDrawable == null){
            return null;
        }

        Bitmap tmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        Canvas tmpCanvas = new Canvas(tmp);
        tmpCanvas.drawColor(Color.WHITE);

        if(mBitmap != null){
            // 适配处理
            Matrix matrix = new Matrix();

            float bmWidth = mBitmap.getWidth();
            float bmHeight = mBitmap.getHeight();


            float sx = 0;
            float sy = 0;
            if(bmWidth > mImageBubbleRectRangeMax
                    || bmHeight > mImageBubbleRectRangeMax){
                sx = mImageBubbleRectRangeMax / bmWidth;
                sy = mImageBubbleRectRangeMax / bmHeight;

            }else if(bmWidth < mImageBubbleRectRangeMin
                    || bmHeight < mImageBubbleRectRangeMin){
                sx = mImageBubbleRectRangeMin / bmWidth;
                sy = mImageBubbleRectRangeMin / bmHeight;

            }else {

                sx = 1;
                sy = 1;
            }

            if(sx < sy){
                matrix.postScale(sx, sx);
            }else {
                matrix.postScale(sy, sy);
            }

            tmpCanvas.drawBitmap(mBitmap, matrix, mBubblePaint);

            mBitmap.recycle();
            mBitmap = null;
        }else {

            mShaderSourceDrawable.setBounds(0, 0, getWidth(), getHeight());
            mShaderSourceDrawable.draw(tmpCanvas);

            mShaderSourceDrawable = null;
        }

        mBubblePaintShader = new BitmapShader(tmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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
