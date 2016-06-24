package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 14/6/2016.
 */
public class TopBarCoinControl extends Table {

    private Assets assets;
    private Table _this;
    private Label coinLabel;
    private PTScreen ptScreen;
    private boolean disableClick;

    public TopBarCoinControl(Assets assets, int myCoinCount, boolean disableClick, PTScreen ptScreen) {
        this.assets = assets;
        _this = this;
        this.ptScreen = ptScreen;
        this.disableClick = disableClick;
        populate(myCoinCount);
        setListeners();
    }

    public void populate(final int myCoinCount){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.TOP_BAR_COIN_COUNT)));
                new DummyButton(_this, assets);

                Table coinTable = new Table();
                coinTable.setSize(30, 30);
                coinTable.setPosition(5, 12);

                coinLabel = new Label(String.valueOf(myCoinCount),
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

    public void setListeners(){
        if(!this.disableClick){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _this.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            ptScreen.toScene(SceneEnum.SHOP);
                        }
                    });
                }
            });
        }
    }

}
