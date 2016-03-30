package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class DummyButton extends Button {

    public DummyButton(Table table, Assets assets){
        super(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.EMPTY)));
        this.setFillParent(true);
        table.addActor(this);
    }



}
