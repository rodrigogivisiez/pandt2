package com.mygdx.potatoandtomato.abstractions;

import com.badlogic.gdx.Screen;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract {

    PTGame _game;
    Textures _textures;

    public LogicAbstract(PTGame game, Textures textures) {
        this._game = game;
        this._textures = textures;
    }

    public abstract Screen getScreen();

    public Textures getTextures() {
        return _textures;
    }


}
