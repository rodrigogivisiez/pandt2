package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.enums.CastleState;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class CastleActor extends Table {

    private Table _this;
    private Services services;
    private Assets assets;

    public CastleActor(Services services) {
        _this = this;
        _this.align(Align.bottomLeft);
        this.services = services;
        this.assets = services.getAssets();

    }

    public void changeState(final CastleState castleState){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(castleState == CastleState.Normal){
                    _this.clear();
                    _this.add(new Image(assets.getTextures().get(Textures.Name.CASTLE_DOOR)));
                }
                else if(castleState == CastleState.Semi_Destroyed){
                    _this.clear();
                    _this.add(new Image(assets.getTextures().get(Textures.Name.CASTLE_SEMI_DESTROYED))).padLeft(-12);
                    services.getSoundsWrapper().playSounds(Sounds.Name.SHOCK);
                }
                else if(castleState == CastleState.Destroyed){
                    _this.clear();
                    _this.add(new Image(assets.getTextures().get(Textures.Name.CASTLE_DESTROYED))).padLeft(-12);
                    services.getSoundsWrapper().playSounds(Sounds.Name.CASTLE_DESTROYED);
                }
            }
        });

    }

}
