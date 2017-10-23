package ttyy.com.jinviews.toy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import ttyy.com.jinviews.R;

/**
 * Author: hjq
 * Date  : 2017/10/23 21:44
 * Name  : Rotate3DImageView
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class Rotate3DImageView extends ImageView{

    Bitmap bitmap;

    public Rotate3DImageView(Context context) {
        this(context, null);
    }

    public Rotate3DImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Rotate3DImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Rotate3DImageView);
        Drawable drawable = ta.getDrawable(R.styleable.Rotate3DImageView_image);
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bmd = (BitmapDrawable) drawable;
            bitmap = bmd.getBitmap();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap == null){
            return;
        }
    }
}
