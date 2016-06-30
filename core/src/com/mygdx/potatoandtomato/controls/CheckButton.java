package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mygdx.potatoandtomato.absintflis.controls.CheckStateListener;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 27/6/2016.
 */
public class CheckButton extends Button {

    private CheckButton _this;
    private ArrayList<CheckStateListener> checkStateListeners;

    public CheckButton(Drawable up, Drawable down, Drawable checked) {
        super(up, down, checked);
        _this = this;
        this.checkStateListeners = new ArrayList();
        setListeners();
    }


    private void setListeners(){
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                for(CheckStateListener listener : checkStateListeners){
                    listener.onChanged(_this.isChecked());
                }
            }
        });
    }

    @Override
    public void setChecked(boolean isChecked) {
        super.setChecked(isChecked);
        for(CheckStateListener listener : checkStateListeners){
            listener.onChanged(isChecked);
        }
    }

    public void addCheckStateListeners(CheckStateListener checkStateListener){
        checkStateListeners.add(checkStateListener);
    }

    public void removeCheckStateListeners(CheckStateListener checkStateListener){
        checkStateListeners.remove(checkStateListener);
    }
}
