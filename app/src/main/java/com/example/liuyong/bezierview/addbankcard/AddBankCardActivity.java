package com.example.liuyong.bezierview.addbankcard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liuyong.bezierview.R;

/**
 * Created by liuyong
 * Data: 2017/8/11
 * Github:https://github.com/MrAllRight
 */

public class AddBankCardActivity extends AppCompatActivity  {
    private RelativeLayout rlContent;//动态添加绑卡动画
    private View bankCardView1,bankCardView2,bankCardView3;
    private StarView starView;
    private BandCardEditText etInput;
    private TextView tvBankName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbankcard);
        initView();
    }

    private void initView() {
        etInput= (BandCardEditText) findViewById(R.id.addbank_et_input);
        rlContent = (RelativeLayout) findViewById(R.id.addbank_rl_image);
        bankCardView1= LayoutInflater.from(this).inflate(R.layout.bandcardview1,null);
        starView= (StarView) bankCardView1.findViewById(R.id.starview);
        tvBankName= (TextView)bankCardView1.findViewById(R.id.bankcard_name);
        rlContent.addView(bankCardView1);
        etInput.setListener(new BandCardEditText.BandCardEditTextListen() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                starView.startAnim();//开始输入卡号的时候，执行星星坠落动画
            }

            @Override
            public void afterTextChanged(Editable editable) {
             starView.setText(etInput.getText().toString());//将EditText的内容显示到第一个星星（TextView）上
            }
        });
    }
}
