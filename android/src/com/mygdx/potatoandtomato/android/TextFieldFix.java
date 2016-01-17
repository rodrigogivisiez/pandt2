package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.models.NativeLibgdxTextInfo;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.Status;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class TextFieldFix {

    private Activity _activity;
    private EditText _editText;
    private View _gameView;

    public TextFieldFix(Activity activity, final EditText editText, View gameView) {
        _activity = activity;
        _editText = editText;
        _gameView = gameView;

        _editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                Broadcaster.getInstance().broadcast(BroadcastEvent.NATIVE_TEXT_CHANGED, new NativeLibgdxTextInfo(s1, _editText.getSelectionStart()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        Broadcaster.getInstance().subscribe(BroadcastEvent.LIBGDX_TEXT_CHANGED, new BroadcastListener<NativeLibgdxTextInfo>() {
            @Override
            public void onCallback(final NativeLibgdxTextInfo obj, Status st) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _editText.setText(obj.getText());
                        _editText.setSelection(obj.getCursorPosition());
                    }
                });


            }
        });

        Broadcaster.getInstance().subscribe(BroadcastEvent.SHOW_NATIVE_KEYBOARD, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _editText.requestFocus();
                        InputMethodManager keyboard = (InputMethodManager) _activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(_editText, 0);
                    }
                });
            }
        });

        Broadcaster.getInstance().subscribe(BroadcastEvent.SCREEN_LAYOUT_CHANGED, new BroadcastListener<Float>() {
            @Override
            public void onCallback(Float obj, Status st) {
                if(obj == 0){
                    _activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _gameView.requestFocus();
                        }
                    });

                }
            }
        });


    }



}
