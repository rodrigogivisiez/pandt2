package com.mygdx.potatoandtomato.helpers.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class Textures {

    TextureAtlas _backgroundsPack;

    private TextureAtlas getBackgroundsPack() {
        if(_backgroundsPack == null){
            _backgroundsPack = new TextureAtlas(Gdx.files.internal("backgrounds_pack.txt"));;
        }
        return _backgroundsPack;
    }

    public TextureRegion getBlueBg() { return getBackgroundsPack().findRegion("blue"); };
    public TextureRegion getAutumnBg() { return getBackgroundsPack().findRegion("autumn_bg"); };
    public TextureRegion getSunrise() { return getBackgroundsPack().findRegion("sunrise"); };
    public TextureRegion getSunray() { return getBackgroundsPack().findRegion("sunray"); };
    public TextureRegion getLogoNoWeapon() { return getBackgroundsPack().findRegion("logo"); };
    public TextureRegion getLogoPotatoWeapon() { return getBackgroundsPack().findRegion("potato_weapon"); };
    public TextureRegion getLogoTomatoWeapon() { return getBackgroundsPack().findRegion("tomato_weapon"); };
    public TextureRegion getGreenGround() { return getBackgroundsPack().findRegion("grass_green"); };
    public TextureRegion getAutumnGround() { return getBackgroundsPack().findRegion("grass_autumn"); };


    public TextureRegion getUprightEggButton() { return getBackgroundsPack().findRegion("upright_egg_button"); };
    public TextureRegion getPlayIcon() { return getBackgroundsPack().findRegion("play_icon"); };
    public TextureRegion getEmpty() { return getBackgroundsPack().findRegion("empty"); };
}
