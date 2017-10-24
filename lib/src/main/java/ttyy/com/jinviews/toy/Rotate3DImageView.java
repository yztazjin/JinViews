package ttyy.com.jinviews.toy;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ttyy.com.jinviews.R;

/**
 * Author: hjq
 * Date  : 2017/10/23 21:44
 * Name  : Rotate3DImageView
 * Intro : Edit By hjq
 * Version : 1.0
 */
public class Rotate3DImageView extends View {

    Bitmap bitmap;
    Camera camera;
    Paint paint;

    Drawable imageSrcDrawable;

    /**
     * 3D旋转Y旋转角度
     */
    int canvas3DRotateDegreeY = -30;

    /**
     * camera Y角度偏移值
     */
    int degreeFoldingYOffset = 0, degreeNoFoldedYOffset = 0;
    /**
     * 画布旋转角度偏移
     */
    int degreeZOffset = 0;

    ValueAnimator anim0;
    ValueAnimator anim1;
    ValueAnimator anim2;
    ValueAnimator anim3;
    ValueAnimator anim4;

    AnimatorSet animSet;

    public Rotate3DImageView(Context context) {
        this(context, null);
    }

    public Rotate3DImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Rotate3DImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Rotate3DImageView);
        imageSrcDrawable = ta.getDrawable(R.styleable.Rotate3DImageView_image);

        camera = new Camera();

        paint = new Paint();
        paint.setAntiAlias(true);

        anim0 = ValueAnimator.ofInt(0, canvas3DRotateDegreeY)
                .setDuration(300);
        anim0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degreeFoldingYOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        anim1 = ValueAnimator.ofInt(0, 270)
                .setDuration(1800);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degreeZOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        anim2 = ValueAnimator.ofInt(0, canvas3DRotateDegreeY);
        anim2.setDuration(300);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degreeNoFoldedYOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        anim3 = ValueAnimator.ofFloat(0, 1);
        anim3.setDuration(500);

        anim4 = ValueAnimator.ofInt(canvas3DRotateDegreeY, 0);
        anim4.setDuration(220);
        anim4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degreeFoldingYOffset = degreeNoFoldedYOffset = (int) animation.getAnimatedValue();

                if(degreeNoFoldedYOffset == 0){
                    degreeZOffset = 0;
                }
                postInvalidate();
            }
        });

        animSet = new AnimatorSet();
        animSet.playSequentially(anim0, anim1, anim2, anim3, anim4);
    }

    public void rollAnim(){
        if(animSet.isRunning()){
            return;
        }
        degreeFoldingYOffset = degreeNoFoldedYOffset = degreeZOffset = 0;
        animSet.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (imageSrcDrawable != null) {
            if (bitmap != null) {
                bitmap.recycle();
            }

            bitmap = Bitmap.createBitmap((int) (w * 2.8f / 4), (int) (h * 2.8f / 4), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            imageSrcDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            imageSrcDrawable.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            return;
        }

        int width = getWidth(), height = getHeight();
        int centerX = width / 2, centerY = height / 2;
        int x = (width - bitmap.getWidth()) / 2, y = (height - bitmap.getHeight()) / 2;

        // 折起的部分
        camera.save();
        canvas.save();

        canvas.translate(centerX, centerY);
        canvas.rotate(-degreeZOffset);

        camera.rotateY(degreeFoldingYOffset);
        camera.applyToCanvas(canvas);

        canvas.clipRect(0, -centerY, centerX, centerY);
        canvas.rotate(degreeZOffset);
        canvas.translate(-centerX, -centerY);
        canvas.drawBitmap(bitmap, x, y, paint);

        camera.restore();
        canvas.restore();

        // 没有折起的部分
        camera.save();
        canvas.save();

        canvas.translate(centerX, centerY);
        canvas.rotate(-degreeZOffset);

        camera.rotateY(-degreeNoFoldedYOffset);
        camera.applyToCanvas(canvas);

        canvas.clipRect(-centerX, -centerY, 0, centerY);
        canvas.rotate(degreeZOffset);
        canvas.translate(-centerX, -centerY);
        canvas.drawBitmap(bitmap, x, y, paint);

        camera.restore();
        canvas.restore();

    }
}
