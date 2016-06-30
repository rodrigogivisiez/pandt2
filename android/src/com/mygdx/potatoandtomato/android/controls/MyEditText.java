package com.mygdx.potatoandtomato.android.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by SiongLeng on 30/6/2016.
 */
public class MyEditText extends EditText {

    private Runnable onBackKeyPressedRunnable;

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(onBackKeyPressedRunnable != null) onBackKeyPressedRunnable.run();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnBackKeyPressedRunnable(Runnable onBackKeyPressedRunnable) {
        this.onBackKeyPressedRunnable = onBackKeyPressedRunnable;
    }
}
