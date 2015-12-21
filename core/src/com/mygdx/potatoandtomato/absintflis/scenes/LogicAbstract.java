package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.models.Services;

import java.util.Objects;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract implements Disposable {

    protected PTScreen _screen;
    protected Services _services;
    protected boolean _cache, _saveToStack;

    public LogicAbstract(PTScreen screen, Services services, Object... objs) {
        this._screen = screen;
        this._services = services;
        setSaveToStack(true);
    }



    public void onQuit(OnQuitListener listener){
        listener.onResult(OnQuitListener.Result.YES);
    }

    public abstract SceneAbstract getScene();

    public void setSaveToStack(boolean _saveToStack) {
        this._saveToStack = _saveToStack;
    }

    public boolean isSaveToStack() {
        return _saveToStack;
    }

    public void setScreen(PTScreen _screen) {
        this._screen = _screen;
    }

    //will be called everytime scene onshow, whether is back or forward direction
    public void onShow(){
        getScene().onShow();

    }

    //will be called everytime scene on hide, whether is back or forward direction
    public void onHide(){

    }

    //will only be called when scene init, must be forward direction
    public void onInit(){

    }


    //will be called everytime scene on hide and dispose, must be back direction
    @Override
    public void dispose() {
        _services.getGamingKit().removeListenersByClass(this.getClass());
        _services.getDatabase().clearListenersByClass(this.getClass());
    }
}
