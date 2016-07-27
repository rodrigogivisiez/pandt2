package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.assets.MyAssets;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.controls.AutoDisposeTable;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public abstract class SceneAbstract implements Disposable {

    protected MyAssets _assets;
    protected Services _services;
    protected Texts _texts;
    protected AutoDisposeTable _root;
    protected PTScreen _screen;
    protected IPTGame _ptGame;
    protected boolean disposed;
    protected TopBar topBar;

    public SceneAbstract(Services services, PTScreen screen) {
        _services = services;
        _screen = screen;
        _ptGame = _screen.getGame();
        _assets = _services.getAssets();
        _texts = _services.getTexts();
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _root = new AutoDisposeTable();
                _root.setFillParent(true);
                populateRoot();
            }
        });
    }

    public abstract void populateRoot();

    public Actor getRoot(){ return _root; };

    public void onShow(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(topBar != null){
                    topBar.onShow();
                }
            }
        });
    }

    public void onHide(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(topBar != null){
                    topBar.onHide();
                }
            }
        });
    }

    @Override
    public void dispose() {
        disposed = true;
        if(_root != null) _root.dispose();
    }

    public TopBar getTopBar() {
        return topBar;
    }
}
