package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.helpers.services.Fonts;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public abstract class SceneAbstract {

    protected Textures _textures;
    protected Fonts _fonts;
    protected Services _services;
    protected Texts _texts;
    protected Table _root;

    public SceneAbstract(Services services) {
        _services = services;
        _textures = _services.getTextures();
        _fonts = _services.getFonts();
        _texts = _services.getTexts();
        _root = new Table();
        _root.setFillParent(true);
        populateRoot();
    }

    public abstract void populateRoot();

    public Actor getRoot(){ return _root; };



}
