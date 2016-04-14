package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class TimeActor extends Table {

    private MyAssets assets;
    private Services services;
    private GameCoordinator coordinator;

    public TimeActor(Services services, GameCoordinator gameCoordinator) {
        this.services = services;
        this.assets = services.getAssets();
        this.coordinator = gameCoordinator;
        this.align(Align.left);
    }

    public void populate(KingActor kingActor, CastleActor castleActor, final KnightActor knightActor){
        this.clear();
        this.add(kingActor).width(30);
        this.add(castleActor).padLeft(-5);
        this.add(knightActor).expand().fill();

    }

}
