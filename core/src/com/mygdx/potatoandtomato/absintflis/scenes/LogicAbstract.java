package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.helpers.utils.SafeThread;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract implements Disposable {

    protected PTScreen _screen;
    protected Services _services;
    protected Texts _texts;
    protected boolean _saveToStack;
    private SafeThread _keepAlive;
    private boolean _alive;
    private ArrayList<String> _broadcastSubscribes;
    protected Confirm _confirm;
    private String _classTag;
    private boolean _isVisible;
    private Broadcaster _broadcaster;

    public LogicAbstract(PTScreen screen, Services services, Object... objs) {
        setClassTag();
        this._screen = screen;
        this._services = services;
        _texts = _services.getTexts();
        _confirm = _services.getConfirm();
        _broadcaster = _services.getBroadcaster();
        setSaveToStack(true);
        _broadcastSubscribes = new ArrayList();
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

    public Broadcaster getBroadcaster() {
        return _broadcaster;
    }

    public void setClassTag(){
        _classTag = Logs.getCallerClassName();
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

    //will be called everytime scene onshow, whether is back or forward direction, root might not have stage parent yet
    public void onShow(){
        _isVisible = true;
    }

    //will be called everytime scene on hide, whether is back or forward direction
    public void onHide(){
        _isVisible = false;
    }

    //will only be called when scene init, must be forward direction
    public void onInit(){
        _alive = true;
    }

    protected String getClassTag(){
        return _classTag;
    }

    protected boolean isSceneVisible() {
        return _isVisible;
    }

    protected void keepAlive(){
        _keepAlive = new SafeThread();
    }

    protected void killKeepAlive(){
        _keepAlive.kill();
    }

    public boolean isAlive() {
        return _alive;
    }

    //will be called everytime scene on hide and dispose, must be back direction
    @Override
    public void dispose() {
        if(_keepAlive == null){
            disposeEverything();
        }
        else{
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if(_keepAlive.isKilled()) break;
                        Threadings.sleep(1000);
                    }
                    disposeEverything();
                }
            });
        }

    }

    private void disposeEverything(){
        _services.getGamingKit().removeListenersByClassTag(getClassTag());
        _services.getDatabase().clearListenersByClassTag(getClassTag());
        _alive = false;
        for(String id : _broadcastSubscribes){
            _broadcaster.unsubscribe(id);
        }
        _broadcastSubscribes.clear();
    }

}
