package com.mygdx.potatoandtomato.helpers.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
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
            _backgroundsPack = new TextureAtlas(Gdx.files.internal("ui.atlas"));;
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
    public TextureRegion getSocialIcon() { return getBackgroundsPack().findRegion("social"); };
    public TextureRegion getLoginIcon() { return getBackgroundsPack().findRegion("login_icon"); };
    public TextureRegion getTick() { return getBackgroundsPack().findRegion("tick"); };
    public TextureRegion getCross() { return getBackgroundsPack().findRegion("cross"); };
    public TextureRegion getWoodBgTall() { return getBackgroundsPack().findRegion("wood_bg_tall"); };
    public TextureRegion getWoodBgFat() { return getBackgroundsPack().findRegion("wood_bg_fat"); };

    public TextureRegion getDownwardEggButton() { return getBackgroundsPack().findRegion("downward_egg_btn"); };
    public TextureRegion getTopBarBg() { return getBackgroundsPack().findRegion("topbar_bg"); };
    public TextureRegion getQuitIcon() { return getBackgroundsPack().findRegion("quit_icon"); };
    public TextureRegion getGameListHighlight() { return getBackgroundsPack().findRegion("gamelist_highlight"); };
    public TextureRegion getGameListTitleSeparator() { return getBackgroundsPack().findRegion("gamelist_title_separator"); };
    public TextureRegion getGameListBg() { return getBackgroundsPack().findRegion("gamelist_bg"); };
    public TextureRegion getGameListTitleBg() { return getBackgroundsPack().findRegion("gamelist_titlebg"); };
    public TextureRegion getBtnWhiteRound() { return getBackgroundsPack().findRegion("btn_white_round"); };
    public NinePatch getBlackRoundedBg() { return getBackgroundsPack().createPatch("black_rounded_bg"); };
    public TextureRegion getRatingIcon() { return getBackgroundsPack().findRegion("rating_icon"); };
    public TextureRegion getSettingsIcon() { return getBackgroundsPack().findRegion("settings_icon"); };
    public TextureRegion getTomatoIcon() { return getBackgroundsPack().findRegion("tomato_icon"); };
    public TextureRegion getPotatoIcon() { return getBackgroundsPack().findRegion("potato_icon"); };
    public TextureRegion getBackIcon() { return getBackgroundsPack().findRegion("back_icon"); };
    public NinePatch getIrregularBg() { return getBackgroundsPack().createPatch("irregular_bg"); }
    public TextureRegion getComingSoon() { return getBackgroundsPack().findRegion("coming_soon"); };
    public NinePatch getScrollVerticalHandle() { return getBackgroundsPack().createPatch("scrollbar_handle"); }
    public TextureRegion getNoImage() { return getBackgroundsPack().findRegion("noimage"); };


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
