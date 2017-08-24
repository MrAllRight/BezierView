package com.example.liuyong.bezierview.updateAnim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import com.example.liuyong.bezierview.R;
import com.example.liuyong.bezierview.addbankcard.MyAnimationListener;
import com.example.liuyong.bezierview.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyong
 * Data: 2017/8/21
 * Github:https://github.com/MrAllRight
 */

public class UpdateProgressView extends View {
    private static final int INIT = 0;//初始化状态
    private static final int PREPARE = 1;//准备开始下载状态
    private static final int UPDATEING = 2;//开始下载更新状态
    private static final int SUCCESS = 3;//下载成功状态
    private static final int INSTALL = 4;//安装状态
    private Bitmap startDrawable;//开始下载图片
    private Bitmap succDrawable;//马上安装图片
    private Bitmap pbProgerssDrawable;//进度指示小图片
    private int width, height;//view宽高
    private int state = -1;//记录当前状态
    private Paint bitmapPaint, pbPaint, pbUpdatePaint, textPaint;//图片paint，更新进度背景paint，更新进度paint，文字paint
    private int pbBgColor = Color.parseColor("#EAEAEA");//进度条背景颜色
    private int pbColor = Color.parseColor("#9BD6FE");//进度条颜色
    private Matrix matrix;//图片变换
    private float bitmapScale = 1;//图片缩放
    private int startX = 0, endX = 0;//进度条的起始位置
    private Path pbPath, pbPathSec;//进度条背景path，进度path
    private PathMeasure pm;//path测量
    private List<ValueAnimator> va_List = new ArrayList<ValueAnimator>();//保存ValueAnimator，统一销毁
    Rect textRect = new Rect();//文字区域计算
    private RectF rectClickRange;//点击升级生效区域
    float[] POS = new float[2];//获取path进度位置
    private int transx = 0;//成功后进度条位移
    private boolean prepareDone = false;
    private long max = 1;//apk的大小
    private long progress;//当前下载大小
    float progressOffsetX = 0;//进度
    StartDownLoadListener startDownLoadListener;//动画结束通知界面开始下载数据
    String text;//进度文字提示
    private boolean isRotate = true;//是否旋转画布
    private boolean isSetListener = false;//是否设置downloadlistener

    /**
     * 点击开始下载监听
     */
    public interface StartDownLoadListener {
        void downLoad();
    }

    public void setStartDownLoadListener(StartDownLoadListener listener) {
        this.startDownLoadListener = listener;
    }

    //设置最大进度值
    public void setMax(long max) {
        this.max = max;
    }

    public UpdateProgressView(Context context) {
        this(context, null);
    }

    public UpdateProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //设置当前进度并不断绘制
    public void setProgress(long progress) {
        if (state != UPDATEING)
            return;
        this.progress = progress;
        if (progress == 0) {
            progressOffsetX = 0;
            return;
        }
        if (max == 0) {
            throw new RuntimeException("max不能为0!");
        }
        progressOffsetX = (progress * (endX - startX) * 1.0f / max);
        cancleValueAnimator(va_List);
        isRotate = false;
        removeCallbacks(rotateRunnable);
        postInvalidate();
    }

    //每隔一段时间刷新界面，如果进度没有更新，将画布旋转回来
    private Runnable rotateRunnable = new Runnable() {
        @Override
        public void run() {
            isRotate = true;
            invalidate();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(UiUtils.dp2px(getContext(), 300), startDrawable.getHeight() * 3);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(UiUtils.dp2px(getContext(), 300), heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, startDrawable.getHeight() * 3);
        }
        rectClickRange = new RectF(getWidth() / 2 - startDrawable.getWidth() / 2, getHeight() / 2 - startDrawable.getHeight() / 2, getWidth() / 2 + startDrawable.getWidth() / 2, getHeight() / 2 + startDrawable.getHeight() / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void init() {
        startDrawable = BitmapFactory.decodeResource(getResources(), R.mipmap.button_normal);
        succDrawable = BitmapFactory.decodeResource(getResources(), R.mipmap.button_install);
        pbProgerssDrawable = BitmapFactory.decodeResource(getResources(), R.mipmap.dl_progress_bg);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pbPaint.setStrokeCap(Paint.Cap.ROUND);
        pbPaint.setColor(pbBgColor);
        pbPaint.setStyle(Paint.Style.STROKE);
        pbPaint.setStrokeWidth(UiUtils.dp2px(getContext(), 10));

        pbUpdatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pbUpdatePaint.setStrokeCap(Paint.Cap.ROUND);
        pbUpdatePaint.setColor(pbColor);
        pbUpdatePaint.setStyle(Paint.Style.STROKE);
        pbUpdatePaint.setStrokeWidth(UiUtils.dp2px(getContext(), 10));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(UiUtils.dp2px(getContext(), 8));

        matrix = new Matrix();
        pbPath = new Path();
        pbPathSec = new Path();
        pm = new PathMeasure();
        initStateData();
        state = INIT;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (state) {
            case INIT:
                //如果点击生效，执行动画
                if (rectClickRange.contains(event.getX(), event.getY()))
                    startBtnDisappear();
                break;
            case SUCCESS:
                //调用安装apk
                break;
        }
        return true;
    }

    /**
     * 点击立即升级的时候，立即升级按钮执行消失动画
     * 动画效果是按钮放大一点之后缩小至消失
     * 根据效果选择插值器AnticipateInterpolator（开始的时候向后然后向前甩）
     * 将bitmapscale设置到立即升级图片上
     * 动画结束后状态更新为准备状态
     */

    private void startBtnDisappear() {
        ValueAnimator va = ValueAnimator.ofInt(0, 1);
        va.setInterpolator(new AnticipateInterpolator());
        va.setDuration(800);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bitmapScale = 1 - animation.getAnimatedFraction();
                invalidate();
            }
        });
        va.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cancleValueAnimator(va_List);
                state = PREPARE;
                toPrepare();
            }
        });
        va.start();
        va_List.add(va);
    }

    /**
     * 初始化状态
     */
    private void initStateData() {
        state=INIT;
        bitmapScale = 1;
        text = "";
        startX = 0;
        endX = 0;
        state = INIT;
        progressOffsetX = 0;
        va_List.clear();
        isRotate = true;
        progress = 0;
        max=1;
        transx=0;
        isSetListener=false;
        prepareDone=false;
    }

    private void cancleValueAnimator(List<ValueAnimator> va_List) {
        if (va_List != null && va_List.size() > 0) {
            for (int i = 0; i < va_List.size(); i++) va_List.get(i).cancel();
        }
    }

    /**
     * PREPARE状态
     * 进度条从中间向两端扩散
     * 具体做法是不断改变path的起点和终点坐标
     * 动画结束的时候开始下载更新
     */

    private void toPrepare() {
        final ValueAnimator va = ValueAnimator.ofFloat(0, width / 2 - pbPaint.getStrokeWidth() * 2 - pbProgerssDrawable.getWidth());
        va.setInterpolator(new LinearInterpolator());
        va.setDuration(200);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                startX = (int) (width / 2 - value);
                endX = (int) (width / 2 + value);
                if (animation.getAnimatedFraction() == 1) prepareDone = true;
                invalidate();
            }
        });
        va.addListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                if (startDownLoadListener != null && !isSetListener) {
                    isSetListener = true;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            state = UPDATEING;
                            cancleValueAnimator(va_List);
                            startDownLoadListener.downLoad();//动画结束，通知界面开始下载apk
                            text = progress * 100 / max + "%";
                        }
                    }, 200);
                }
            }
        });
        va.start();
        va_List.add(va);
    }

    //下载进度达到100时，进度提示图片进行缩放
    private void toSuccBitmapScale() {
        cancleValueAnimator(va_List);
        ValueAnimator va = ValueAnimator.ofFloat(1, 0);
        va.setInterpolator(new AccelerateInterpolator());
        va.setDuration(100);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bitmapScale = (Float) animation.getAnimatedValue();
                state = SUCCESS;
                invalidate();
            }
        });
        va.start();
        va.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                toSuccPathAnim();
            }
        });
        va_List.add(va);
    }

    //成功后进度条缩放动画
    private void toSuccPathAnim() {
        cancleValueAnimator(va_List);
        ValueAnimator va = ValueAnimator.ofInt(0, (endX - startX) / 2);
        va.setInterpolator(new AccelerateInterpolator());
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                transx = (int) animation.getAnimatedValue();
                state = SUCCESS;
                invalidate();
            }
        });
        va.start();
        va.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                toInstall();
            }
        });
        va_List.add(va);
    }
    //显示马上安装图片动画
    private void toInstall() {
        cancleValueAnimator(va_List);
        ValueAnimator va = ValueAnimator.ofInt(0, 1);
        va.setInterpolator(new LinearInterpolator());
        va.setDuration(400);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bitmapScale = animation.getAnimatedFraction();
                state = INSTALL;
                invalidate();
            }
        });
        va.start();
        va_List.add(va);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (state) {
            case INIT:
                matrix.reset();
                matrix.setScale(bitmapScale, bitmapScale);//缩放图片
                matrix.preTranslate(0, 0);
                matrix.postTranslate(width / 2 - startDrawable.getWidth() / 2 * bitmapScale, height / 2 - startDrawable.getHeight() / 2 * bitmapScale);//不断的改变缩放的中心点
                canvas.drawBitmap(startDrawable, matrix, bitmapPaint);
                break;
            case PREPARE:
                pbPath.reset();
                pbPath.moveTo(startX, height / 2);
                pbPath.lineTo(endX, height / 2);
                canvas.drawPath(pbPath, pbPaint);//绘制path
                //进度条完全显示后，画进度提示图片和文字
                if (prepareDone) {
                    canvas.drawBitmap(pbProgerssDrawable, startX - pbProgerssDrawable.getWidth() / 2, height / 2 - pbProgerssDrawable.getHeight() - pbPaint.getStrokeWidth(), bitmapPaint);
                    String text = "0%";
                    textPaint.getTextBounds(text.toCharArray(), 0, text.toCharArray().length, textRect);
                    canvas.drawText(text, startX - textRect.right / 2, height / 2 - pbProgerssDrawable.getHeight() / 2 - pbPaint.getStrokeWidth() + textRect.bottom, textPaint);
                }
                break;
            case UPDATEING:
                pbPath.reset();
                pbPath.moveTo(startX, height / 2);
                pbPath.lineTo(endX, height / 2);
                pm.setPath(pbPath, false);
                //不断截取进度到pbPathSec并绘制
                if (progressOffsetX >= pm.getLength()) {
                    pm.getSegment(0, pm.getLength(), pbPathSec, true);
                    pm.getPosTan(pm.getLength(), POS, null);
                } else {
                    pm.getSegment(0, progressOffsetX, pbPathSec, true);
                    pm.getPosTan(progressOffsetX, POS, null);
                }
                matrix.reset();
                matrix.setTranslate(POS[0] - pbProgerssDrawable.getWidth() / 2, POS[1] - pbProgerssDrawable.getHeight() - pbPaint.getStrokeWidth());
                canvas.drawPath(pbPath, pbPaint);
                canvas.drawPath(pbPathSec, pbUpdatePaint);
                canvas.save();
                //如果进度没有到达100%，并且进度在更新的时候，画布旋转，然后画进度提示图片和文字
                if (progressOffsetX < pm.getLength() && !isRotate) {
                    canvas.rotate(-15, POS[0], POS[1] - pbPaint.getStrokeWidth() / 2);
                }
                canvas.drawBitmap(pbProgerssDrawable, matrix, bitmapPaint);
                if (progressOffsetX >= pm.getLength())
                    progressOffsetX = pm.getLength();
                text = (int) (progressOffsetX * 100 / pm.getLength()) + "%";
                textPaint.getTextBounds(text.toCharArray(), 0, text.toCharArray().length, textRect);
                canvas.drawText(text, progressOffsetX + startX - textRect.right / 2, height / 2 - pbProgerssDrawable.getHeight() / 2 - pbPaint.getStrokeWidth() + textRect.bottom, textPaint);
                //我们启动一个线程，如果300毫秒进度没有更新，将画布旋转回来画进度提示图片和文字
                if (progressOffsetX < pm.getLength()) postDelayed(rotateRunnable, 300);
                else toSuccBitmapScale();
                canvas.restore();
                break;
            case SUCCESS:
                pbPath.reset();
                pbPath.moveTo(startX + transx, height / 2);//不断的改变起点
                pbPath.lineTo(endX - transx, height / 2);//改变终点
                pm.setPath(pbPath, false);
                pm.getSegment(0, (endX - startX), pbPathSec, true);
                pm.getPosTan(endX - startX, POS, null);
                matrix.reset();
                matrix.preTranslate(POS[0] - pbProgerssDrawable.getWidth() / 2, POS[1] - pbProgerssDrawable.getHeight() - pbPaint.getStrokeWidth());
                matrix.postScale(bitmapScale, bitmapScale, POS[0], POS[1] - pbPaint.getStrokeWidth());
                canvas.drawPath(pbPath, pbUpdatePaint);//path缩放动画
                canvas.drawBitmap(pbProgerssDrawable, matrix, bitmapPaint);//bitmap缩放动画
                break;
            case INSTALL:
                matrix.reset();
                matrix.setScale(bitmapScale, bitmapScale);
                matrix.preTranslate(0, 0);
                matrix.postTranslate(width / 2 - succDrawable.getWidth() / 2 * bitmapScale, height / 2 - succDrawable.getHeight() / 2 * bitmapScale);
                canvas.drawBitmap(succDrawable, matrix, bitmapPaint);
                break;
        }
    }
}
