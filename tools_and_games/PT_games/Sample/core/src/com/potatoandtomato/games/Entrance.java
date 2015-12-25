package com.potatoandtomato.games;

import com.badlogic.gdx.Screen;
import com.potatoandtomato.common.GameEntranceAbstract;
import com.potatoandtomato.common.GameLibCoordinator;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntranceAbstract {

    private static GameLibCoordinator _gameLibCoordinator;
    private Screen _firstScreen;

    public Entrance(GameLibCoordinator gameLibCoordinator) {
        super(gameLibCoordinator);
        this._gameLibCoordinator = gameLibCoordinator;
        _firstScreen = new SampleScreen();
    }

    @Override
    public Screen getFirstScreen() {
        return _firstScreen;
    }

    public static GameLibCoordinator getCoordinator() {
        return _gameLibCoordinator;
    }

    public static void setGameLibCoordinator(GameLibCoordinator _gameLibCoordinator) {
        Entrance._gameLibCoordinator = _gameLibCoordinator;
    }
}
