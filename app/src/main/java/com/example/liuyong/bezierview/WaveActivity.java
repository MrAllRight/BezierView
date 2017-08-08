package com.example.liuyong.bezierview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class WaveActivity extends AppCompatActivity implements WaveView.onWaveAnimationListener {
    private WaveView waveView;
    private ImageView iv1,iv2,iv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveView= (WaveView) findViewById(R.id.waveview);
        iv1= (ImageView) findViewById(R.id.iv1);
        iv2= (ImageView) findViewById(R.id.iv2);
        iv3= (ImageView) findViewById(R.id.iv3);
        waveView.setListener(this);
    }

    @Override
    public void onWaveAnimation(float left, float middle, float right) {
        FrameLayout.LayoutParams pa1= (FrameLayout.LayoutParams) iv1.getLayoutParams();
        pa1.bottomMargin= (int) left;
        iv1.setLayoutParams(pa1);

        FrameLayout.LayoutParams pa2= (FrameLayout.LayoutParams) iv2.getLayoutParams();
        pa2.bottomMargin= (int) middle;
        iv2.setLayoutParams(pa2);

        FrameLayout.LayoutParams pa3= (FrameLayout.LayoutParams) iv3.getLayoutParams();
        pa3.bottomMargin= (int) right;
        iv3.setLayoutParams(pa3);
    }
}