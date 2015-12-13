package com.mygdx.potatoandtomato.absintflis.scenes;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.models.Assets;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract {

    protected PTScreen _screen;
    protected Assets _assets;

    public LogicAbstract(PTScreen screen, Assets assets) {
        this._screen = screen;
        this._assets = assets;
    }

    public abstract SceneAbstract getScene();

}
