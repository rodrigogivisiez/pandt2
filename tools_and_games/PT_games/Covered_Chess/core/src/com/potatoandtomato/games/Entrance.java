package com.potatoandtomato.games;

import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.BattleReference;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.MainScreenLogic;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    MainScreenLogic _logic;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        Assets assets = new Assets(gameCoordinator);
        assets.loadAll(null);
        Services services = new Services(assets, new Texts(), new BattleReference());
        _logic = new MainScreenLogic(services, gameCoordinator);
        setCurrentScreen(_logic.getScreen());
    }

    @Override
    public void init() {
        _logic.init();
    }
}
