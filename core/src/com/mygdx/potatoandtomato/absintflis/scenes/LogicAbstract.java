package com.mygdx.potatoandtomato.absintflis.scenes;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.models.Services;

import java.util.Objects;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public abstract class LogicAbstract {

    protected PTScreen _screen;
    protected Services _services;

    public LogicAbstract(PTScreen screen, Services services, Object... objs) {
        this._screen = screen;
        this._services = services;
    }

    public void init(){

    }

    public abstract SceneAbstract getScene();

}
