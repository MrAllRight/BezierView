package com.example.liuyong.bezierview.addbankcard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liuyong.bezierview.R;

/**
 * Created by liuyong
 * Data: 2017/8/11
 * Github:https://github.com/MrAllRight
 */

public class AddBankCardActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rlContent;//动态添加绑卡动画的layout
    private View bankCardView1, bankCardView2, bankCardView3;//绑卡的3个阶段view
    private StarView starView;//星星坠落的动画
    private BandCardEditText etInput;//自定义银行卡格式的EditText
    private TextView next;//下一步按钮
    private int step = 0;
    private Animation firstAnim;//第一次next的动画，分解为向上+向下消失的动画，Interpolator设为AccelerateInterpolator
    private Animation secondAnim;//第二页电话界面出现动画
    private Animator thirdAnim;//第三页界面出现的动画，使用的是属性动画
    private LinearLayout phoneLayout;//装载PhoneView
    private PhoneView phoneView;//你的手机号码动画
    private TextView tv1, tv2, tv3;//3个步骤指示
    private ImageView ivPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbankcard);
        initView();
    }

    private void initView() {
        tv1 = (TextView) findViewById(R.id.addbank_text1);
        tv2 = (TextView) findViewById(R.id.addbank_text2);
        tv3 = (TextView) findViewById(R.id.addbank_text3);
        next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(this);
        etInput = (BandCardEditText) findViewById(R.id.addbank_et_input);
        rlContent = (RelativeLayout) findViewById(R.id.addbank_rl_image);
        bankCardView1 = LayoutInflater.from(this).inflate(R.layout.bankcardview1, null);
        bankCardView2 = LayoutInflater.from(this).inflate(R.layout.bankcardview2, null);
        starView = (StarView) bankCardView1.findViewById(R.id.starview);
        phoneView = new PhoneView(this);
        phoneLayout = (LinearLayout) bankCardView2.findViewById(R.id.ll_tvphone);
        ivPhone = (ImageView) bankCardView2.findViewById(R.id.image_phone);
        final ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bankCardView1.setLayoutParams(param);
        bankCardView2.setLayoutParams(param);
        rlContent.addView(bankCardView1);
        //星星动画
        etInput.setListener(new BandCardEditText.BandCardEditTextListen() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                starView.setText(s);//将EditText的内容显示到第一个星星（TextView）上
            }

            @Override
            public void afterTextChanged(Editable editable) {
                starView.startAnim();//开始输入卡号的时候，执行星星坠落动画
            }
        });
        //第一步next动画,结束的时候添加bankCardView2(第二页)到rlContent,然后第二页执行向上的动画
        firstAnim = AnimationUtils.loadAnimation(this, R.anim.firstanim);
        firstAnim.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                rlContent.removeView(bankCardView1);
                rlContent.addView(bankCardView2);
                rlContent.startAnimation(secondAnim);//第二页动画
            }
        });
        //第二页界面出现动画
        secondAnim = AnimationUtils.loadAnimation(this, R.anim.secondanim);
        secondAnim.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                phoneLayout.addView(phoneView);//把phoneview添加到页面中，并调用startAnim方法
                phoneView.startAnim();
            }
        });
        //第三页界面出现动画
        thirdAnim = AnimatorInflater.loadAnimator(this, R.animator.thirdanim);
        thirdAnim.addListener(new MyAnimationListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                phoneLayout.setVisibility(View.GONE);//将上一页的phoneLayout移除
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                View viewCode = AddBankCardActivity.this.getLayoutInflater().inflate(R.layout.view_addbank_code, null);
                final ImageView ivMessage = (ImageView) viewCode.findViewById(R.id.addbank_code_image);
                final TextView tvMess1 = (TextView) viewCode.findViewById(R.id.addbank_code_tv1);
                final TextView tvMess2 = (TextView) viewCode.findViewById(R.id.addbank_code_tv2);
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                param.width = (int) (ivPhone.getWidth() - ivPhone.getWidth() * 0.116);
                param.height = (int) (ivPhone.getHeight() * 0.3);
                param.leftMargin = (int) (ivPhone.getLeft() + ivPhone.getWidth() * 0.058);
                param.topMargin = (int) (ivPhone.getTop() + ivPhone.getHeight() * 0.251);
                rlContent.addView(viewCode, param);//短信验证码layout添加到电话背景图上
                Animation ivAnim = AnimationUtils.loadAnimation(AddBankCardActivity.this, R.anim.scaleanim);
                final Animation tvAnim1 = AnimationUtils.loadAnimation(AddBankCardActivity.this, R.anim.scaleanim);
                final Animation tvAnim2 = AnimationUtils.loadAnimation(AddBankCardActivity.this, R.anim.scaleanim);
                ivMessage.startAnimation(ivAnim);
                ivAnim.setAnimationListener(new MyAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ivMessage.setVisibility(View.VISIBLE);
                        tvMess1.startAnimation(tvAnim1);
                    }
                });
                tvAnim1.setAnimationListener(new MyAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvMess1.setVisibility(View.VISIBLE);
                        tvMess2.startAnimation(tvAnim2);
                    }
                });
                tvAnim2.setAnimationListener(new MyAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvMess2.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                if (step == 0) {
                    rlContent.startAnimation(firstAnim);
                    tv1.setTextColor(getResources().getColor(R.color.colorGray));
                    tv2.setTextColor(getResources().getColor(R.color.colorBule2));
                    tv3.setTextColor(getResources().getColor(R.color.colorGray));
                    etInput.setBankCardType(1);
                    etInput.setText("");
                    etInput.setHint("请输入手机号");
                    step = 1;
                } else if (step == 1) {
                    tv1.setTextColor(getResources().getColor(R.color.colorGray));
                    tv2.setTextColor(getResources().getColor(R.color.colorGray));
                    tv3.setTextColor(getResources().getColor(R.color.colorBule2));
                    etInput.setBankCardType(-1);
                    etInput.setText("");
                    etInput.setHint("请输入验证码");
                    thirdAnim.setTarget(rlContent);
                    thirdAnim.start();
                }
                break;
        }

    }
}
