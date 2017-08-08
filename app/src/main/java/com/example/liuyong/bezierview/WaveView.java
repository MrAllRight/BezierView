package com.example.liuyong.bezierview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by liuyong
 * Data: 2017/7/28
 * Github:https://github.com/MrAllRight
 */

public class WaveView extends View {
    private Paint wavePaint;//波浪画笔
    private Path wavePath;//波浪路径
    private int waveLength=1000;//一个完整波浪长度，长度越大,波纹越平缓
    private int waveCount;//波纹的个数
    private int offSet;//波纹从屏幕左侧不断向右平移的偏移量
    private int cetenterY;//波纹的中间轴
    private int screenWidth,screenHeight;//屏幕的宽高
    private PathMeasure measure;
    private float[] pos1=new float[2];//左边的鱼位置
    private float[] pos2=new float[2];//中间鱼的位置
    private float[] pos3=new float[2];//右边鱼的位置
    private onWaveAnimationListener listener;
    private Context context;

    public void setListener(onWaveAnimationListener listener) {
        this.listener = listener;
    }

    public interface  onWaveAnimationListener{
        void onWaveAnimation(float left,float middle,float right);
    }
    public WaveView(Context context) {
        super(context);
        this.context=context;
        initPaint();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initPaint();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initPaint();

    }

    private void initPaint() {
        wavePath=new Path();
        measure=new PathMeasure();
        wavePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setColor(getResources().getColor(R.color.colorPrimary));
        wavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        initAinm();
    }

    private void initAinm() {
        ValueAnimator animator = ValueAnimator.ofInt(0, waveLength);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offSet = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight=h;
        screenWidth=w;
        waveCount= (int) Math.round(screenWidth/waveLength+1.5);
        cetenterY=screenHeight/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.white));
        wavePath.reset();
        wavePath.moveTo(-waveLength+offSet,cetenterY);
        for(int i=0;i<waveCount;i++){
            wavePath.quadTo((-waveLength * 3 / 4) + (i * waveLength) + offSet, cetenterY + 60, (-waveLength / 2) + (i * waveLength) + offSet, cetenterY);
            wavePath.quadTo((-waveLength / 4) + (i * waveLength) + offSet, cetenterY - 60, i * waveLength + offSet, cetenterY);

        }
        wavePath.lineTo(screenWidth,screenHeight);
        wavePath.lineTo(0,screenHeight);
        wavePath.close();
        measure.setPath(wavePath,true);
        float totaLength=measure.getLength();//波纹路径总长度
        float mLength=totaLength/waveCount;//单个波纹路径的长度
//        float ScreenLength= screenWidth/waveLength*mLength;//屏幕中波纹路径的长度
        measure.getPosTan(waveLength+offSet,pos1,null);
        measure.getPosTan(waveLength+screenWidth/2+offSet,pos2,null);
        measure.getPosTan(waveLength+screenWidth-dp2px(85)+offSet,pos3,null);
        listener.onWaveAnimation(pos1[1]-cetenterY,pos2[1]-cetenterY,pos3[1]-cetenterY);
        canvas.drawPath(wavePath,wavePaint);
    }
    public  int dp2px(float dpValue) {
        final float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
