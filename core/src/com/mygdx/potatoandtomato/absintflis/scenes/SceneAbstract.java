package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public abstract class SceneAbstract {

    protected Assets _assets;
    protected Services _services;
    protected Texts _texts;
    protected Table _root;
    protected PTScreen _screen;

    public SceneAbstract(Services services, PTScreen screen) {
        _services = services;
        _screen = screen;
        _assets = _services.getTextures();
        _texts = _services.getTexts();
        _root = new Table();
        _root.setFillParent(true);
        populateRoot();
    }

    public abstract void populateRoot();

    public Actor getRoot(){ return _root; };

    public void onShow(){

    }

    public void dispose(){

    }

}
