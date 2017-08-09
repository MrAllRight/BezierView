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
    private int mFixCircleRadius = 20;
    private int mDragCircleRadius = 20;
    private PointF mFixCirclePoint;
    private PointF mDragCirclePoint;
    private Paint paint;
    private float distance;
    private boolean isDrag = false;
    private boolean isFixCircleShow = true;
    private boolean isUp = false;

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
                isDrag(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDrag) return true;
                if (event.getX() - mFixCirclePoint.x > mFixCircleRadius || event.getY() - mFixCirclePoint.y > mFixCircleRadius) {
                    mDragCirclePoint.x = event.getX();
                    mDragCirclePoint.y = event.getY();
                    float dx = mFixCirclePoint.x - mDragCirclePoint.x;
                    float dy = mFixCirclePoint.y - mDragCirclePoint.y;
                    distance = (float) Math.hypot(Math.abs(dx), Math.abs(dy));
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
            mFixCircleRadius = (int) (20 - distance / 14);
            if (!isDrag)
                canvas.drawCircle(mFixCirclePoint.x, mFixCirclePoint.y, mFixCircleRadius, paint);
            else {
                if (mFixCircleRadius <= 6) {
                    isFixCircleShow = false;
                }
                if (mFixCircleRadius > 6 && isFixCircleShow) {
                    canvas.drawCircle(mFixCirclePoint.x, mFixCirclePoint.y, mFixCircleRadius, paint);
                    drawBezer(canvas);
                }
                canvas.drawCircle(mDragCirclePoint.x, mDragCirclePoint.y, mDragCircleRadius, paint);
            }
        } else {
            if (isFixCircleShow)
                canvas.drawCircle(mFixCirclePoint.x, mFixCirclePoint.y, mFixCircleRadius, paint);
        }
    }

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
