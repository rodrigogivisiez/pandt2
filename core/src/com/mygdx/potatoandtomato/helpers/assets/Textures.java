package com.mygdx.potatoandtomato.helpers.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 2/12/2015.
 */
public class Textures {

    TextureAtlas _backgroundsPack;
    TextureAtlas _potatoHiAnimation, _tomatoHiAnimation;

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
    public TextureRegion getTomatoHi() { return getBackgroundsPack().findRegion("tomato_hi"); };
    public TextureRegion getPotatoHi() { return getBackgroundsPack().findRegion("potato_hi"); };
    public TextureRegion getWoodBgTall() { return getBackgroundsPack().findRegion("wood_bg_tall"); };


    public Array<? extends TextureRegion> getPotatoHiAnimation() {
        if(_potatoHiAnimation == null){
            _potatoHiAnimation = new TextureAtlas(Gdx.files.internal("animations/potato_hi.txt"));;
        }
        return _potatoHiAnimation.getRegions();
    }

    public Array<? extends TextureRegion> getTomatoHiAnimation() {
        if(_tomatoHiAnimation == null){
            _tomatoHiAnimation = new TextureAtlas(Gdx.files.internal("animations/tomato_hi.txt"));;
        }
        return _tomatoHiAnimation.getRegions();
    }

    public void disposeHiAnimation() {
        if(_potatoHiAnimation != null){
            _potatoHiAnimation.dispose();
            _potatoHiAnimation = null;
        }
        if(_tomatoHiAnimation != null){
            _tomatoHiAnimation.dispose();
            _tomatoHiAnimation = null;
        }
    }

}
