package com.potatoandtomato.games;

import com.firebase.client.Firebase;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.BattleReference;
import com.potatoandtomato.games.helpers.Sounds;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.MainScreenLogic;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    MainScreenLogic _logic;
    Services _services;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        Assets assets = new Assets(gameCoordinator);
        assets.loadAll(null);
        _services =  new Services(assets, new Texts(), new Sounds(assets), new BattleReference());

    }

    @Override
    public void init() {
        _logic = new MainScreenLogic(_services, getGameCoordinator(), false);
        _logic.init();
        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
        Firebase db = getGameCoordinator().getFirebase();
    }

    @Override
    public void onContinue() {
        _logic = new MainScreenLogic(_services, getGameCoordinator(), true);
        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
    }

    @Override
    public void dispose() {

    }


}
