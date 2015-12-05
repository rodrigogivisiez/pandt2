package com.mygdx.potatoandtomato.abstractions;

import com.badlogic.gdx.Screen;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract {

    protected PTScreen _screen;
    Textures _textures;
    Fonts _fonts;

    public LogicAbstract(PTScreen screen, Textures textures, Fonts fonts) {
        this._screen = screen;
        this._textures = textures;
        this._fonts = fonts;
    }



    public abstract SceneAbstract getScene();

    public Textures getTextures() {
        return _textures;
    }

    public Fonts getFonts() { return _fonts; }
}
