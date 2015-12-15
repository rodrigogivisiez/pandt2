package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.helpers.services.Textures;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class DummyButton extends Button {

    public DummyButton(Table table, Textures textures){
        super(new TextureRegionDrawable(textures.getEmpty()));
        this.setFillParent(true);
        table.addActor(this);
    }



}
