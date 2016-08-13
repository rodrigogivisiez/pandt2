package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.cachings.CacheListener;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.Flurry;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract {

    protected PTScreen _screen;
    protected Services _services;
    protected Texts _texts;
    protected boolean _saveToStack;
    private ArrayList<String> _broadcastSubscribes;
    protected Confirm _confirm;
    private String _classTag, _classTagSimple;
    private boolean _isVisible;
    private boolean _isFullyVisible;
    private Broadcaster _broadcaster;
    private boolean _settedListeners;
    private boolean _disposing, _disposed;
    private ArrayList<CacheListener> cacheListeners;

    public LogicAbstract(PTScreen screen, Services services, Object... objs) {
        setClassTag();
        this._screen = screen;
        this._services = services;
        _texts = _services.getTexts();
        _confirm = _services.getConfirm();
        _broadcaster = _services.getBroadcaster();
        setSaveToStack(true);
        _broadcastSubscribes = new ArrayList();
        cacheListeners = new ArrayList();
    }

    public void subscribeBroadcast(int event, BroadcastListener listener){
        _broadcastSubscribes.add(_broadcaster.subscribe(event, listener));
    }

    public void subscribeBroadcastOnceWithTimeout(int event, long timeoutInMil, BroadcastListener listener){
        _broadcaster.subscribeOnceWithTimeout(event, timeoutInMil, listener);
    }


    public void publishBroadcast(int event){
        _broadcaster.broadcast(event);
    }

    public void publishBroadcast(int event, Object object){
        _broadcaster.broadcast(event, object);
    }

    public CacheListener getNewCacheListener(final RunnableArgs onFinish){
        CacheListener cacheListener = new CacheListener(){
            @Override
            public void onResult(Object result) {
                onFinish.run(result);
            }
        };
        cacheListeners.add(cacheListener);
        return cacheListener;
    }

    public void setClassTag(){
        _classTag = Logs.getCallerClassName();
        _classTagSimple = _classTag;
        if(_classTag != null){
            String[] tmp = _classTag.split("\\.");
            if(tmp.length > 0) _classTagSimple = tmp[tmp.length - 1];
        }

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

    //will be called everytime scene onshow(before moving animation), whether is back or forward direction, root might not have stage parent yet
    public void onShow(){
        Flurry.logToScene(_classTagSimple);
        _isVisible = true;
        if(!_settedListeners){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    setListeners();
                }
            });

            _settedListeners = true;
        }

        if(getScene() != null) getScene().onShow();

    }

    //called everytime scene have complete moving animation, wheteher back or forward direction
    public void onShown(){
        _isFullyVisible = true;

    }

    //will be called everytime scene on hide, whether is back or forward direction
    public void onHide(){
        _isVisible = false;
        _isFullyVisible = false;
        publishBroadcast(BroadcastEvent.HIDE_NATIVE_KEYBOARD);
        if(getScene() != null) getScene().onHide();
    }

    //will be called everytime scene back to other scene
    public void onBack(){

    }

    //will only be called when scene init, must be forward direction
    public void onInit(){

    }

    //on changing scene
    public void onChangedScene(SceneEnum toScene){

    }

    protected String getClassTag(){
        return _classTag;
    }

    //scene may not finish sliding animation yet
    protected boolean isSceneVisible() {
        return _isVisible;
    }

    //scene already finish sliding animation
    protected boolean isSceneFullyVisible() {
        return _isFullyVisible;
    }


    protected boolean isDisposing() {
        return _disposing;
    }

    //will be called everytime scene leaving, not depend on the animation end
    public boolean disposeEarly(){
        if(!_disposing){
            _disposing = true;
            _services.getGamingKit().removeListenersByClassTag(getClassTag());
            _services.getDatabase().clearListenersByTag(getClassTag());
            for(String id : _broadcastSubscribes){
                _broadcaster.unsubscribe(id);
            }
            _broadcastSubscribes.clear();
            for(CacheListener cacheListener : cacheListeners){
                cacheListener.dispose();
            }
            cacheListeners.clear();
            return true;
        }
        return false;
    }

    //will be called everytime scene on hide and dispose, must be back direction/no keep in stack, depend on the animation end
    public boolean dispose() {
        if(!_disposed){
            disposeEarly();
            _disposed = true;
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    if(getScene() != null) getScene().dispose();
                }
            });
            return true;
        }
        return false;
    }


    public void setListeners(){

    }

}
