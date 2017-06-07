package ttyy.com.jinviews.chat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Shader;

/**
 * author: admin
 * date: 2017/06/06
 * version: 0
 * mail: secret
 * desc: ChatBubbleIntf
 */

interface ChatBubbleIntf {

    int ARROW_LOCATION_LEFT = 0;
    int ARROW_LOCATION_RIGHT = 1;
    int ARROW_LOCATION_TOP = 2;
    int ARROW_LOCATION_BOTTOM = 3;

    int getArrowLocation();

    /**
     * 对话框背景图
     * @return
     */
    Shader getPaintBubbleRectShader();

    /**
     * 对话框背景色
     * @return
     */
    int getPaintBubbleRectColor();

    /**
     * 聊天对话框图片资源
     * @param resId
     */
    void setBubbleRectImageResource(int resId);

    /**
     * 聊天框图片资源
     * @param mBitmap
     */
    void setBubbleRectImageBitmap(Bitmap mBitmap);

    /**
     * 聊天框颜色资源
     * @param color
     */
    void setBubbleRectStrokeColor(int color);

    void drawChatBubbleRect(Canvas canvas);

    /**
     * 当ArrowLocation为以下时，无效的取值
     * ARROW_LOCATION_LEFT
     * ARROW_LOCATION_RIGHT
     *
     * 当ArrowLocation为以下时
     * ARROW_LOCATION_TOP
     * ARROW_LOCATION_BOTTOM
     * XDist > 0 箭头绘画从左侧计算距离
     * XDist < 0 箭头绘画从右侧计算记录
     * @return
     */
    int getOffsetXDist();

    /**
     * 当ArrowLocation为以下时，无效的取值
     * ARROW_LOCATION_TOP
     * ARROW_LOCATION_BOTTOM
     *
     * 当ArrowLocation为以下时
     * ARROW_LOCATION_LEFT
     * ARROW_LOCATION_RIGHT
     * YDist > 0 箭头绘画从顶部计算距离
     * YDist < 0 箭头绘画从底部计算记录
     * @return
     */
    int getOffsetYDist();

    /**
     * 箭头的宽度
     * @return
     */
    int getArrowDimensionSizeInX();

    /**
     * 箭头的高度
     * @return
     */
    int getArrowDimensionSizeInY();

    /**
     * 聊天气泡框角弧度
     * @return
     */
    int getBubbleRectRadius();

    /**
     * 获取带箭头的聊天气泡框
     * @return
     */
    Path getBubbleRectPath();
}
