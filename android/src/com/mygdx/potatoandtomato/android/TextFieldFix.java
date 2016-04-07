package com.mygdx.potatoandtomato.android;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.mygdx.potatoandtomato.models.NativeLibgdxTextInfo;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class TextFieldFix {

    private Activity _activity;
    private EditText _editText;
    private View _gameView;
    private Broadcaster _broadcaster;

    public TextFieldFix(Activity activity, final EditText editText, View gameView, Broadcaster broadcaster) {
        _activity = activity;
        _editText = editText;
        _gameView = gameView;
        _broadcaster = broadcaster;

        _editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                _broadcaster.broadcast(BroadcastEvent.NATIVE_TEXT_CHANGED, new NativeLibgdxTextInfo(s1, _editText.getSelectionStart()));
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

        _broadcaster.subscribe(BroadcastEvent.LIBGDX_TEXT_CHANGED, new BroadcastListener<NativeLibgdxTextInfo>() {
            @Override
            public void onCallback(final NativeLibgdxTextInfo obj, Status st) {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!_editText.getText().toString().equals(obj.getText())){
                            _editText.setText(obj.getText());
                        }
                        _editText.setSelection(obj.getCursorPosition());
                    }
                });


            }
        });

        _broadcaster.subscribe(BroadcastEvent.SHOW_NATIVE_KEYBOARD, new BroadcastListener() {
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

        _broadcaster.subscribe(BroadcastEvent.SCREEN_LAYOUT_CHANGED, new BroadcastListener<Float>() {
            @Override
            public void onCallback(Float obj, Status st) {
                if (obj == 0) {
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
