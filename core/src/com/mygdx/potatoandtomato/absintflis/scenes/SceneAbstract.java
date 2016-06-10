package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.PTScreen;
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

    protected Assets _assets;
    protected Services _services;
    protected Texts _texts;
    protected AutoDisposeTable _root;
    protected PTScreen _screen;
    protected IPTGame _ptGame;

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

    @Override
    public void dispose() {
        if(_root != null) _root.dispose();
    }
}
