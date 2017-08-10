package com.example.liuyong.bezierview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by liuyong
 * Data: 2017/8/9
 * Github:https://github.com/MrAllRight
 * qq未读消息拖拽消失
 */

public class BubbleView extends View {
    private int mFixCircleRadius = 20;//固定圆的半径，其半径会随着拉伸变小，我们设定最小是6，小于6的时候固定圆消失
    private int mDragCircleRadius = 20;//拖拽时显示的园
    private PointF mFixCirclePoint;//固定圆圆心，本文只是演示，具体使用时要自己计算view的大小，再设置合适的圆心和半径
    private PointF mDragCirclePoint;//拖拽圆的圆心，随着拖拽不断变化
    private Paint paint;//画笔
    private float distance;//两个圆心的距离
    private boolean isDrag = false;//判断手指按下时是否落在固定圆的区域
    private boolean isFixCircleShow = true;//固定圆是否显示
    private boolean isUp = false;//手指是否弹起，弹起时如果固定圆显示这时候要回弹回来，如果固定圆消失了，此时应该执行拖拽圆爆炸消失效果，本文演示只做消失效果

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //初始化画笔，圆心
    private void init() {
        mFixCirclePoint = new PointF(300, 300);
        mDragCirclePoint = new PointF(300, 300);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getResources().getColor(R.color.red));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX = event.getX();
        float downY = event.getY();
        isUp = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag(downX, downY);//判断是否在固定圆区域，不在不显示拖拽效果
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDrag) return true;
                //判断是否出现拉伸效果
                if (Math.abs(event.getX() - mFixCirclePoint.x )> mFixCircleRadius || Math.abs(event.getY() - mFixCirclePoint.y) > mFixCircleRadius) {
                    mDragCirclePoint.x = event.getX();//不断改变拖拽圆的圆心
                    mDragCirclePoint.y = event.getY();
                    float dx = mFixCirclePoint.x - mDragCirclePoint.x;
                    float dy = mFixCirclePoint.y - mDragCirclePoint.y;
                    distance = (float) Math.hypot(Math.abs(dx), Math.abs(dy));//计算拖拽的距离
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isDrag = false;
                isUp = true;
                mFixCircleRadius = 20;
                mDragCirclePoint.x = event.getX();
                mDragCirclePoint.y = event.getY();
                postInvalidate();
                break;
        }

        return true;
    }

    //判断手指是否落在了固定的小圆内
    private void isDrag(float x, float y) {
        if (x >= mFixCirclePoint.x - mFixCircleRadius-20 && x <= mFixCirclePoint.x + mFixCircleRadius+20 && y >= mFixCirclePoint.y - mFixCircleRadius-20 && y <= mFixCirclePoint.y + mFixCircleRadius+20)
            isDrag = true;
        else isDrag = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isUp) {
            //手指没有弹起时
            mFixCircleRadius = (int) (20 - distance / 14);
            //初始化时只画固定圆
            if (!isDrag)
                canvas.drawCircle(mFixCirclePoint.x, mFixCirclePoint.y, mFixCircleRadius, paint);
            else {
                //拖拽时不断减小固定圆半径，如果小于6时，固定圆消失
                if (mFixCircleRadius <= 6) {
                    isFixCircleShow = false;
                }
                if (mFixCircleRadius > 6 && isFixCircleShow) {
                    canvas.drawCircle(mFixCirclePoint.x, mFixCirclePoint.y, mFixCircleRadius, paint);
                    drawBezer(canvas);//绘制拖拽的效果
                }
                canvas.drawCircle(mDragCirclePoint.x, mDragCirclePoint.y, mDragCircleRadius, paint);
            }
        } else {
            //手指弹起时，如果固定圆没有消失，显示回弹效果
            if (isFixCircleShow)
                canvas.drawCircle(mFixCirclePoint.x, mFixCirclePoint.y, mFixCircleRadius, paint);
        }
    }
   //绘制拉伸的效果
    private void drawBezer(Canvas canvas) {
        Path path = new Path();
        //计算4个控制点
        float dx = mFixCirclePoint.x - mDragCirclePoint.x;
        float dy = mFixCirclePoint.y - mDragCirclePoint.y;
        if (dx == 0) {
            dx = 0.001f;
        }
        float tan = dy / dx;
        // 获取角a度值
        float arcTanA = (float) Math.atan(tan);

        // 依次计算 p0 , p1 , p2 , p3 点的位置
        float P0X = (float) (mFixCirclePoint.x + mFixCircleRadius * Math.sin(arcTanA));
        float P0Y = (float) (mFixCirclePoint.y - mFixCircleRadius * Math.cos(arcTanA));

        float P1X = (float) (mDragCirclePoint.x + mDragCircleRadius * Math.sin(arcTanA));
        float P1Y = (float) (mDragCirclePoint.y - mDragCircleRadius * Math.cos(arcTanA));

        float P2X = (float) (mDragCirclePoint.x - mDragCircleRadius * Math.sin(arcTanA));
        float P2Y = (float) (mDragCirclePoint.y + mDragCircleRadius * Math.cos(arcTanA));

        float P3X = (float) (mFixCirclePoint.x - mFixCircleRadius * Math.sin(arcTanA));
        float P3Y = (float) (mFixCirclePoint.y + mFixCircleRadius * Math.cos(arcTanA));
        PointF controlPoint = new PointF(mFixCirclePoint.x + (mDragCirclePoint.x - mFixCirclePoint.x) / 2, (mFixCirclePoint.y + (mDragCirclePoint.y - mFixCirclePoint.y) / 2));
        // 整合贝塞尔曲线路径
        path.moveTo(P0X, P0Y);
        path.quadTo(controlPoint.x, controlPoint.y, P1X, P1Y);
        path.lineTo(P2X, P2Y);
        path.quadTo(controlPoint.x, controlPoint.y, P3X, P3Y);
        path.close();
        canvas.drawPath(path, paint);
    }
}
