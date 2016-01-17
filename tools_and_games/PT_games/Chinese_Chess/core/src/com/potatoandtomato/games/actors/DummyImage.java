package com.potatoandtomato.games.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.potatoandtomato.games.helpers.Assets;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class DummyImage extends Image {

    public DummyImage(Table root, Assets assets) {
        this.setDrawable(new TextureRegionDrawable(assets.getEmpty()));
        this.setFillParent(true);
        root.addActor(this);
    }
}
