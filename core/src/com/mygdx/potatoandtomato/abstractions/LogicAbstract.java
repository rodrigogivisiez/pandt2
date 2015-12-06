package com.mygdx.potatoandtomato.abstractions;

import com.badlogic.gdx.Screen;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Texts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract {

    protected PTScreen _screen;
    Assets _assets;

    public LogicAbstract(PTScreen screen, Assets assets) {
        this._screen = screen;
        this._assets = assets;
    }

    public abstract SceneAbstract getScene();

}
