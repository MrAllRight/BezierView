package com.example.liuyong.bezierview.addbankcard;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.liuyong.bezierview.R;


/**
 * Created by liuyong
 * Data: 2017/8/15
 * Github:https://github.com/MrAllRight
 */

public class BandCardEditText extends EditText {
    private int bankCardType=-1;
    private boolean shouldStopChange = false;
    private final String WHITE_SPACE = " ";
    private BandCardEditTextListen listener;
    public BandCardEditText(Context context) {
        this(context, null);
    }

    public BandCardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BandCardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array=getContext().obtainStyledAttributes(attrs, R.styleable.bandCardEditText,defStyleAttr,0);
        bankCardType=array.getInt(R.styleable.bandCardEditText_editTextType,bankCardType);
        array.recycle();
        format(getText());
        shouldStopChange = false;
        setFocusable(true);
        setEnabled(true);
        setFocusableInTouchMode(true);
        addTextChangedListener(new CardTextWatcher());
    }

    class CardTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          if(listener!=null)listener.beforeTextChanged(s,start,count,after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            format(editable);
            if(listener!=null&& !TextUtils.isEmpty(editable.toString()))listener.afterTextChanged(editable);
        }
    }

    private void format(Editable editable) {
        if (shouldStopChange) {
            shouldStopChange = false;
            return;
        }

        shouldStopChange = true;

        String str = editable.toString().trim().replaceAll(WHITE_SPACE, "");
        int len = str.length();
        int courPos;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(str.charAt(i));
            if ((i == 3 || i == 7 || i == 11 || i == 15)&&bankCardType==0) {
                if (i != len - 1)
                    builder.append(WHITE_SPACE);
            }else if((i==2||i==6)&&bankCardType==1){
                if (i != len - 1)
                builder.append(WHITE_SPACE);
            }
        }
        courPos = builder.length();
        setText(builder.toString());
        setSelection(courPos);

    }

    public String getBankCardText() {
        return getText().toString().trim().replaceAll(" ", "");
    }
    public interface BandCardEditTextListen{
        void beforeTextChanged(CharSequence s, int start, int count, int after);
        void afterTextChanged(Editable editable);
    }

    public void setListener(BandCardEditTextListen listener) {
        this.listener = listener;
    }

    public void setBankCardType(int bankCardType) {
        this.bankCardType = bankCardType;
    }
}
