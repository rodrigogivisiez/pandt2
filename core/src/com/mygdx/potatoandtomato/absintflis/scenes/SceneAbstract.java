package com.mygdx.potatoandtomato.absintflis.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Texts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public abstract class SceneAbstract {

    protected Textures _textures;
    protected Fonts _fonts;
    protected Assets _assets;
    protected Texts _texts;
    protected Table _root;

    public SceneAbstract(Assets assets) {
        _assets = assets;
        _textures = _assets.getTextures();
        _fonts = _assets.getFonts();
        _texts = _assets.getTexts();
        _root = new Table();
        _root.setFillParent(true);
        populateRoot();
    }

    public abstract void populateRoot();

    public Actor getRoot(){ return _root; };

}
