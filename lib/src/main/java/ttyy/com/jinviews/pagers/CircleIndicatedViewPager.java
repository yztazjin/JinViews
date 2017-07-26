package ttyy.com.jinviews.pagers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ttyy.com.jinviews.R;


/**
 * Author: hujinqi
 * Date  : 2016-06-29
 * Description: 带圆形引导器的ViewPager
 *              滑动时，下方的索引会跟着移动
 */
public class CircleIndicatedViewPager extends LinearLayout implements ViewPager.OnPageChangeListener{

    ViewPager mPager;

    int radius;

    int cellWidth;

    Paint paint;
    int cursorColor = Color.parseColor("#00ff00");
    int indicatorColor = Color.WHITE;
    int cursorXOffset = 0;

    int mIndicatorXOffset = -1;
    int mIndicatorYOffset = 0;


    ViewPager.OnPageChangeListener mOnPageChangeListener;

    public CircleIndicatedViewPager(Context context) {
        this(context, null);
    }

    public CircleIndicatedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicatedViewPager);
            cursorColor = ta.getColor(R.styleable.CircleIndicatedViewPager_cursorColor, cursorColor);
            indicatorColor = ta.getColor(R.styleable.CircleIndicatedViewPager_indicatorColor, indicatorColor);
            mIndicatorXOffset = ta.getDimensionPixelOffset(R.styleable.CircleIndicatedViewPager_indicatorXOffset, -1);
            mIndicatorYOffset = ta.getDimensionPixelOffset(R.styleable.CircleIndicatedViewPager_indicatorYOffset, 0);
            ta.recycle();
        }

        init(context);
    }

    protected void init(Context context){
        this.setWillNotDraw(false);
        this.setOrientation(LinearLayout.VERTICAL);

        mPager = new ViewPager(context);
        mPager.addOnPageChangeListener(this);
        // 设置Page切换动画
        mPager.setPageTransformer(true, TransformEffect.Fade);
        mPager.setId(1);

        radius = (int) (context.getResources().getDisplayMetrics().density * 5);
        cellWidth = (int) (radius * 1.5f) + radius * 2;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        this.addView(mPager, params);
    }

    /**
     * 设置ViewPager的适配器
     * @param adapter
     */
    public void setAdapter(PagerAdapter adapter){
        mPager.setAdapter(adapter);
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener){
        this.mOnPageChangeListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        cursorXOffset = (int) ((position + positionOffset) * cellWidth);

        postInvalidate();

        if(mOnPageChangeListener != null)
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        if(mOnPageChangeListener != null)
            mOnPageChangeListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(mOnPageChangeListener != null)
            mOnPageChangeListener.onPageScrollStateChanged(state);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(mPager == null
                || mPager.getAdapter() == null)
            return;


        int count = mPager.getAdapter().getCount();
        if(count == 0 || count == 1) {
            return;
        }

        canvas.save();
        try {

            // indicator总长度
            int indicatorTotalWidth = count * cellWidth;
            // 保证绘制的圆形在中央
            if(mIndicatorXOffset < 0){
                mIndicatorXOffset = (getWidth() - indicatorTotalWidth) / 2;
            }
            canvas.translate(mIndicatorXOffset, 0);

            // 开始绘制索引
            int startX = radius;
            int y = getHeight() - radius * 2 - mIndicatorYOffset;
            if(count == 1){
                // 只有一个 绘制游标
                paint.setColor(cursorColor);
                canvas.drawCircle(startX, y , radius, paint);
                return;
            }else{
                paint.setColor(indicatorColor);
                for(int i = 0 ; i < count ; i++){
                    canvas.drawCircle(startX, y, radius, paint);
                    startX += cellWidth;
                }
            }

            // 开始绘制游标
            startX = radius;
            canvas.translate(cursorXOffset, 0);
            paint.setColor(cursorColor);
            canvas.drawCircle(startX, y , radius, paint);

        }finally {
            // return保证save后一定调用resotre
            // 避免canvas.translate显示异常
            canvas.restore();
        }
    }
}
