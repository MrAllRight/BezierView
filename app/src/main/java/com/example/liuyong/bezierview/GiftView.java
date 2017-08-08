package com.example.liuyong.bezierview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by liuyong
 * Data: 2017/8/8
 * Github:https://github.com/MrAllRight
 * 直播点赞view
 */

public class GiftView extends RelativeLayout {
    private Drawable[] mDrawables;//装载3中点赞图片，红黄蓝
    private int mDrawableWidth, mDrawableHeight;//图片的宽高
    private Random mRandom = new Random();
    private LayoutParams params;//为图片设置布局，位于屏幕的右|下
    private PointF p0, p1, p2, p3;//控制图片的移动轨迹，通过3阶贝塞尔曲线
    private int screenWidth, screenHeight;//屏幕宽高

    public GiftView(Context context) {
        super(context);
        init();
    }

    public GiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight = h;
        screenWidth = w;
    }

    //初始化drawable，params
    private void init() {
        mDrawables = new Drawable[3];
        mDrawables[0] = getResources().getDrawable(R.mipmap.pl_blue);
        mDrawables[1] = getResources().getDrawable(R.mipmap.pl_red);
        mDrawables[2] = getResources().getDrawable(R.mipmap.pl_yellow);
        mDrawableWidth = mDrawables[0].getIntrinsicWidth();
        mDrawableHeight = mDrawables[0].getIntrinsicHeight();
        params = new LayoutParams(mDrawableWidth, mDrawableHeight);
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        params.addRule(ALIGN_PARENT_RIGHT, TRUE);
        params.setMargins(0, 0, 60, 60);//放置在屏幕的右下角
        //这里为了演示我们现在布局初始化的时候，放置一个imageview，颜色随机，设置点击屏幕出现点赞效果
        ImageView iv = new ImageView(getContext());
        iv.setLayoutParams(params);
        iv.setImageDrawable(mDrawables[mRandom.nextInt(mDrawables.length)]);
        addView(iv);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addGiftIv();
            }
        });
    }

    //点击图片是添加imageview到布局中，并添加动画
    private void addGiftIv() {
        final ImageView giftIv = new ImageView(getContext());
        giftIv.setLayoutParams(params);
        giftIv.setImageDrawable(mDrawables[mRandom.nextInt(mDrawables.length)]);
        addView(giftIv);
        addAnimator(giftIv);//添加动画效果，动画分两部分，第一部分是产生图片时缩放和透明度，第二部是移动图片再进行透明度变化
    }

    private void addAnimator(final ImageView iv) {
        //点击的时候，让图片经过放大，缩放效果，之后再开始沿着贝塞尔曲线的轨迹移动
        ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.3f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 0.2f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(100);
        set.playTogether(alpha, scaleX, scaleY);
        set.setTarget(iv);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //设置贝塞尔曲线移动效果
                ValueAnimator va = getValueAnimator(iv);//第二部分动画
                va.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    //初始化贝塞尔曲线的4个点
    private void initPointF() {
        p0 = new PointF(screenWidth - 60 - mDrawableWidth, screenHeight - 60 - mDrawableHeight);//起点是初始化时的点
        p1 = new PointF(mRandom.nextInt(screenWidth), mRandom.nextInt((int) p0.y));//第一个控制点必须要在起始点的上方
        p2 = new PointF(mRandom.nextInt(screenWidth), mRandom.nextInt((int) p1.y));//第二个控制点必须在第一个点的上方
        p3 = new PointF(mRandom.nextInt(screenWidth), -mDrawableHeight);//终点在屏幕的最顶部0-图片的高度
    }


    /**
     *    自定义估值器计算图片移动的轨迹
     *    计算公式参考贝塞尔曲线3阶计算公式
     *    自定义估值器的方法可百度搜索
     *    其中估值器定义返回的结果为PointF
     */
    public class BezierEvaluator implements TypeEvaluator<PointF> {
        private PointF p1, p2;

        public BezierEvaluator(PointF p1, PointF p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public PointF evaluate(float t, PointF p0, PointF p3) {
            PointF point = new PointF();
            point.x = p0.x * (1 - t) * (1 - t) * (1 - t) //
                    + 3 * p1.x * t * (1 - t) * (1 - t)//
                    + 3 * p2.x * t * t * (1 - t)//
                    + p3.x * t * t * t;//

            point.y = p0.y * (1 - t) * (1 - t) * (1 - t) //
                    + 3 * p1.y * t * (1 - t) * (1 - t)//
                    + 3 * p2.y * t * t * (1 - t)//
                    + p3.y * t * t * t;//
            return point;
        }
    }

    private ValueAnimator getValueAnimator(final ImageView iv) {
        initPointF();
        BezierEvaluator bezierEvaluator = new BezierEvaluator(p1, p2);
        ValueAnimator va = ValueAnimator.ofObject(bezierEvaluator, p0, p3);
        va.setDuration(3000);
        va.setTarget(iv);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //改变imageview位置实现移动效果
                PointF point = (PointF) animation.getAnimatedValue();
                iv.setX(point.x);
                iv.setY(point.y);
                iv.setAlpha(1 - animation.getAnimatedFraction());
                //动画结束移除imageview
                if (animation.getAnimatedFraction() >= 1) {
                    removeView(iv);
                }
            }
        });
        return va;
    }

}
