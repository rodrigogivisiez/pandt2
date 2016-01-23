package com.potatoandtomato.games;

import com.badlogic.gdx.Screen;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Sounds;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.MainScreenLogic;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    Services _services;
    MainScreenLogic _logic;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        Assets assets = new Assets(gameCoordinator);
        assets.loadAll(null);
        _services =  new Services(assets, new Texts(), new Sounds(assets, gameCoordinator));

    }

    @Override
    public void init() {
        _logic = new MainScreenLogic(_services, getGameCoordinator(), false);
        getGameCoordinator().getGame().setScreen(_logic.getMainScreen());
    }

    @Override
    public void onContinue() {
        _logic = new MainScreenLogic(_services, getGameCoordinator(), true);
        getGameCoordinator().getGame().setScreen(_logic.getMainScreen());
    }

    @Override
    public void dispose() {
        _services.getSounds().dispose();
    }

}
