package com.potatoandtomato.common;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public abstract class GameEntrance {

    private GameCoordinator _gameCoordinator;
    private GameScreen _currentScreen;

    public GameEntrance(GameCoordinator gameCoordinator) {
        _gameCoordinator = gameCoordinator;
    }

    public GameScreen getCurrentScreen(){
        return _currentScreen;
    }

    public void setCurrentScreen(GameScreen screen){
        _currentScreen = screen;
    }

    public GameCoordinator getGameCoordinator() {
        return _gameCoordinator;
    }

    public abstract void init();

    public abstract void dispose();

}
