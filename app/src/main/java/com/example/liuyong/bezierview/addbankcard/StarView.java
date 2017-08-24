package com.example.liuyong.bezierview.addbankcard;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.liuyong.bezierview.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuyong
 * Data: 2017/8/11
 * Github:https://github.com/MrAllRight
 */

public class StarView extends LinearLayout {
    TranslateAnimation tvTranslation;//位移动画
    final int[] count = new int[2];//记录子view的个数
    private boolean isDone = false;//记录动画是否执行过
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (count[0] > 0) {
                getChildAt(count[0]).startAnimation(tvTranslation);
            }
        }
    };

    public StarView(Context context) {
        this(context, null);
    }

    public StarView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public StarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //首先是添加16个textview,然后每各4个加一个padding（银行卡显示效果）
    private void init() {
        for (int i = 0; i < 16; i++) {
            TextView tv = new TextView(getContext());
            tv.setTextColor(getResources().getColor(R.color.white));
            if (i % 4 == 0)
                tv.setPadding(10, 0, 0, 0);
            tv.setText("*");
            tv.setTextSize(20);
            addView(tv);
        }

    }

    //星星坠落的动画
    public void startAnim() {
        if (isDone) return;//执行过不在执行
        isDone = true;
        count[0] = getChildCount();
        final Timer time = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                tvTranslation = new TranslateAnimation(0, 0, 0, 500);
                tvTranslation.setDuration(500);
                tvTranslation.setFillAfter(true);
                count[0]--;
                handler.sendEmptyMessage(0);
            }
        };
        time.schedule(task, 0, 50);//添加定时器，间隔50毫秒对textview执行坠落动画，
    }

    public void setText(CharSequence s) {
        ((TextView) getChildAt(0)).setText(s);
    }//用第一个textview显示卡号
}
