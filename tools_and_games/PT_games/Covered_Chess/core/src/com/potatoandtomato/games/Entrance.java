package com.potatoandtomato.games;

import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.CoveredChessScreen;
import com.potatoandtomato.games.screens.Logic;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        Assets assets = new Assets();
        assets.loadAll(null);
        Services services = new Services(assets, new Texts());
        Logic logic = new Logic(services, gameCoordinator);
        setCurrentScreen(logic.getScreen());
    }

}
