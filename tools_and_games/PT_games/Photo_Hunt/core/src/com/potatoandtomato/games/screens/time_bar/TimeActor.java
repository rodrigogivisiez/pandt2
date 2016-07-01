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

    private Table _this;
    private MyAssets assets;
    private Services services;
    private GameCoordinator coordinator;

    public TimeActor(Services services, GameCoordinator gameCoordinator) {
        this.services = services;
        this.assets = services.getAssets();
        this.coordinator = gameCoordinator;
        _this = this;

    }

    public void populate(final KingActor kingActor, final CastleActor castleActor, final KnightActor knightActor){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.align(Align.left);
                _this.clear();
                _this.add(kingActor).width(30);
                _this.add(castleActor).padLeft(-5).size(70, 55);
                _this.add(knightActor).expand().fill();

            }
        });

    }

}
