package com.mygdx.potatoandtomato.abstractions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public abstract class SceneAbstract {

    protected LogicAbstract _logic;
    protected Textures _textures;
    protected Fonts _fonts;
    protected Table _root;

    public SceneAbstract(LogicAbstract logic) {
        _logic = logic;
        _textures = logic.getTextures();
        _fonts = logic.getFonts();
        _root = new Table();
        _root.setFillParent(true);
    }

    public Actor getRoot(){ return _root; };

}
