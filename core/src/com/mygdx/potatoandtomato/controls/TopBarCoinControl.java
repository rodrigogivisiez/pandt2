package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 14/6/2016.
 */
public class TopBarCoinControl extends Table {

    private Assets assets;
    private Table _this;
    private Label coinLabel;

    public TopBarCoinControl(Assets assets) {
        this.assets = assets;
        _this = this;
        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.TOP_BAR_COIN_COUNT)));

                Table coinTable = new Table();
                coinTable.setSize(30, 30);
                coinTable.setPosition(5, 12);

                coinLabel = new Label("1",
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.PIZZA_L_REGULAR_S_a05e00_2_2), Color.WHITE));
                coinTable.add(coinLabel);

                _this.addActor(coinTable);
            }
        });
    }

    public void setCoinCount(final int newCount){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                coinLabel.setText(String.valueOf(newCount));
            }
        });
    }

}
