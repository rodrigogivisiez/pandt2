package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.potatoandtomato.common.assets.Assets;

/**
 * Created by SiongLeng on 1/5/2016.
 */
public class Badge extends Table {

    private BadgeType badgeType;
    private Assets assets;
    private Label textLabel;
    private String text;
    private int fontScale;

    public Badge(BadgeType badgeType, String text, Assets assets) {
        this(badgeType, text, assets, 1);
    }

    public Badge(BadgeType badgeType, String text, Assets assets, int fontScale) {
        this.badgeType = badgeType;
        this.assets = assets;
        this.text = text;
        this.fontScale = fontScale;
        populate();
    }


    public void populate(){
        TextureRegion badgeRegion;
        if(badgeType == BadgeType.Rank){
            badgeRegion = assets.getTextures().get(Textures.Name.RANK_ICON);
        }
        else{
            badgeRegion = assets.getTextures().get(Textures.Name.STREAK_ICON);
        }

        this.setBackground(new TextureRegionDrawable(badgeRegion));
        Label.LabelStyle labelStyle = new Label.LabelStyle(assets.getFonts().get(
                            fontScale == 1 ? Fonts.FontId.IMPACT_XS_REGULAR_S_a74828_1_1 : Fonts.FontId.IMPACT_S_REGULAR_S_a74828_1_1), null);
        textLabel = new Label(text, labelStyle);
        textLabel.setAlignment(Align.center);
        int padTop = 0;
        if(badgeType == BadgeType.Streak){
            padTop = 4;
        }
        this.add(textLabel).padTop(padTop).expand().fill().center();
    }

}
