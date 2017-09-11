package com.example.liuyong.bezierview.refreshview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.liuyong.bezierview.R;
import com.example.liuyong.bezierview.util.UiUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuyong
 * Data: 2017/8/31
 * Github:https://github.com/MrAllRight
 */

public class RefreshCircleView extends View {
    private Paint mPaint;
    private Path pathCircle, pathTick;//圆形，对勾的path
    private PathMeasure mMeasureTick;//测量对勾
    private int viewWidth, viewHeight;
    private ValueAnimator tickAnimator;//对勾的animator
    private float tickAnimatorValue;
    private int startAngle;//圆形开始的角度
    private int minAngle = 0;
    private int sweepAngle;//圆形扫过的角度
    private int curAngle;//画布当前角度
    private int state = -1;//记录当前刷新状态
    private final int LOADING = 0;//刷新中
    private final int SUCCESS = 1;//成功

    public RefreshCircleView(Context context) {
        this(context, null);
    }

    public RefreshCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RefreshCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpec == MeasureSpec.AT_MOST && heightSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(UiUtils.dp2px(getContext(), 50), UiUtils.dp2px(getContext(), 50));
        } else if (widthSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(UiUtils.dp2px(getContext(), 50), MeasureSpec.getSize(heightMeasureSpec));
        } else if (heightSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), UiUtils.dp2px(getContext(), 50));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.refresh));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        // 外部圆环
        pathCircle = new Path();
        RectF oval2 = new RectF(-UiUtils.dp2px(getContext(), 20), -UiUtils.dp2px(getContext(), 20), UiUtils.dp2px(getContext(), 20), UiUtils.dp2px(getContext(), 20));
        pathCircle.addArc(oval2, 0, 359.9f);

        // 对勾
        pathTick = new Path();
        pathTick.moveTo(-UiUtils.dp2px(getContext(),14), UiUtils.dp2px(getContext(),0));
        pathTick.lineTo(0, UiUtils.dp2px(getContext(),10));
        pathTick.lineTo(UiUtils.dp2px(getContext(),14), UiUtils.dp2px(getContext(),-8));

        //pathMeasure
        mMeasureTick = new PathMeasure();
        mMeasureTick.setPath(pathTick, false);

        //500毫秒来划对勾
        tickAnimator=ValueAnimator.ofFloat(0,1);
        tickAnimator.setDuration(500);
        tickAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tickAnimatorValue= (float) animation.getAnimatedValue();
                state=SUCCESS;
                invalidate();
            }
        });
        state = LOADING;

        //模拟5秒钟刷新成功
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        tickAnimator.start();
                    }
                });

            }
        },5000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(viewWidth / 2, viewHeight / 2);
        //正在加载
        if (state == LOADING) {
            if (startAngle == minAngle) {
                sweepAngle += 6;
            }
            if (sweepAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (sweepAngle > 20) {
                    sweepAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                startAngle %= 360;
                minAngle = startAngle;
                sweepAngle = 20;
            }
            canvas.rotate(curAngle += 4, 0, 0);
            canvas.drawArc(new RectF(-UiUtils.dp2px(getContext(), 20), -UiUtils.dp2px(getContext(), 20), UiUtils.dp2px(getContext(), 20), UiUtils.dp2px(getContext(), 20)), startAngle, sweepAngle, false, mPaint);
            invalidate();
        } else if (state == SUCCESS) {
           canvas.drawPath(pathCircle,mPaint);
            Path dst = new Path();
            float stop2 = mMeasureTick.getLength() * tickAnimatorValue;
            float start2 = 0;
            mMeasureTick.getSegment(start2, stop2, dst, true);
            canvas.drawPath(dst, mPaint);
        }
    }
}
