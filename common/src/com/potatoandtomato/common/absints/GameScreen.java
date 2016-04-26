package com.potatoandtomato.common.absints;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.potatoandtomato.common.GameCoordinator;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public abstract class GameScreen implements Screen{

    private GameCoordinator _coordinator;
    private BackKeyListener _backKeyListener;

    public GameScreen(GameCoordinator gameCoordinator) {
        this._coordinator = gameCoordinator;
    }

    public GameScreen(GameCoordinator gameCoordinator, BackKeyListener backKeyListener) {
        this._coordinator = gameCoordinator;
        this._backKeyListener = backKeyListener;
    }

    public void setBackKeyListener(BackKeyListener _backKeyListener) {
        this._backKeyListener = _backKeyListener;
    }

    public GameCoordinator getCoordinator() {
        return _coordinator;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            if(_backKeyListener != null) _backKeyListener.backPressed();
        }
    }
}
