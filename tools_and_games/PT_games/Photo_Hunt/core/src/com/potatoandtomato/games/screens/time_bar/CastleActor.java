package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.CastleState;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class CastleActor extends Table {

    private Services services;
    private Assets assets;

    public CastleActor(Services services) {
        this.services = services;
        this.assets = services.getAssets();

    }

    public void changeState(CastleState castleState){
        if(castleState == CastleState.Normal){
            this.clear();
            this.add(new Image(assets.getTextures().get(Textures.Name.CASTLE_DOOR)));
        }
    }

}
