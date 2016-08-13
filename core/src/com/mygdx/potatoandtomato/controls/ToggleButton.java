package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mygdx.potatoandtomato.absintflis.controls.CheckStateListener;
import com.mygdx.potatoandtomato.absintflis.controls.ToggleStateListener;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 10/8/2016.
 */
public class ToggleButton extends Image {

    private boolean stateOn;
    private Drawable onDrawable, offDrawable;
    private ToggleButton _this;
    private ArrayList<ToggleStateListener> toggleStateListeners;

    public ToggleButton(Drawable on, Drawable off) {
        _this = this;
        onDrawable = on;
        offDrawable = off;
        this.toggleStateListeners = new ArrayList();
        this.setDrawable(off);
        setListeners();
    }


    private void setListeners(){
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                stateOn = !stateOn;
                changeDesign();
                for(ToggleStateListener listener : toggleStateListeners){
                    listener.onToggle(stateOn);
                }
            }
        });
    }

    public void setToggleOn(boolean isOn) {
        stateOn = isOn;
        changeDesign();

        for(ToggleStateListener listener : toggleStateListeners){
            listener.onToggle(stateOn);
        }
    }

    private void changeDesign(){
        if(stateOn){
            this.setDrawable(onDrawable);
        }
        else{
            this.setDrawable(offDrawable);
        }
    }

    public void addToggleStateListener(ToggleStateListener toggleStateListener){
        toggleStateListeners.add(toggleStateListener);
    }

    public void removeToggleStateListeners(ToggleStateListener toggleStateListener){
        toggleStateListeners.remove(toggleStateListener);
    }


}
