package com.potatoandtomato.games.actors.plates;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.helpers.Assets;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class PlateActor extends Table {

    private Assets _assets;

    public PlateActor(Assets _assets) {
        this._assets = _assets;

        this.setBackground(new TextureRegionDrawable(_assets.getBlackBgTrans()));

    }




}
