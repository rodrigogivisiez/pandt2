package com.potatoandtomato.games.controls;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.helpers.Assets;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class DummyButton extends Button {

    public DummyButton(Table table, Assets assets){
        super(new TextureRegionDrawable(assets.getTextures().getEmpty()));
        this.setFillParent(true);
        table.addActor(this);
    }



}
