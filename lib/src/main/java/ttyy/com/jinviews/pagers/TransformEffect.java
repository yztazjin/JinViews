package ttyy.com.jinviews.pagers;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Author: hujinqi
 * Date  : 2016-08-23
 * Description:
 */
public enum TransformEffect implements ViewPager.PageTransformer {
    /**
     * 普通模式
     */
    Normal(){
        @Override
        public void transformPage(View page, float position) {

        }
    },

    /**
     * 淡入淡出
     * 带略微的scale缩放动画
     */
    Fade(){
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;
        @Override
        public void transformPage(View view, float position) {
            if (position < -1) {
                // 已经滑到左侧以外了

            } else if (position <= 1) {
                // [-1,1]
                // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
                if (position < 0) {
                    //滑动中左边页面
                    view.setPivotX(view.getMeasuredWidth());
                    view.setRotationY(position*45);
                } else {
                    //滑动中右边页面
                    view.setPivotX(0);
                    view.setRotationY(position*45);
                }

            } else {
                // (1,+Infinity]
                // 已经滑到右侧以外了
            }

        }
    };

    @Override
    public abstract void transformPage(View page, float position);
}
