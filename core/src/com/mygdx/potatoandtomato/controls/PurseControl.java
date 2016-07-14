package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.assets.Textures;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 13/7/2016.
 */
public class PurseControl extends Table {

    private Table _this;
    private Assets assets;
    private int coinOffsetY;

    public PurseControl(Assets assets, int coinOffsetY) {
        this.assets = assets;
        _this = this;
        _this.setSize(140, 200);
        this.coinOffsetY = coinOffsetY;
        populate();
    }

    private void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _this.align(Align.top);

                Image backPurseImage = new Image(assets.getTextures().get(Textures.Name.PURSE_BACK));
                Image frontPurseImage = new Image(assets.getTextures().get(Textures.Name.PURSE_FRONT));

                Image coin1 = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
                coin1.setName("coin1");
                coin1.setVisible(false);
                coin1.setPosition(15, 150 + coinOffsetY);

                Image coin2 = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
                coin2.setName("coin2");
                coin2.setVisible(false);
                coin2.setPosition(36, 147 + coinOffsetY);

                Image coin3 = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_NORMAL));
                coin3.setName("coin3");
                coin3.setVisible(false);
                coin3.setPosition(63, 155 + coinOffsetY);

                Image coin4 = new Image(assets.getTextures().get(Textures.Name.PURSE_COIN_SLEEP));
                coin4.setName("coin4");
                coin4.setVisible(false);
                coin4.setPosition(40, 187 + coinOffsetY);

                _this.add(backPurseImage).padLeft(-10);
                _this.row();

                _this.addActor(coin1);
                _this.addActor(coin3);
                _this.addActor(coin2);
                _this.addActor(coin4);

                _this.add(frontPurseImage).padTop(-20);
            }
        });
    }

    public void changeCoinsNumber(final int retrievableCoinsCount){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Actor coin1 = _this.findActor("coin1");
                Actor coin2 = _this.findActor("coin2");
                Actor coin3 = _this.findActor("coin3");
                Actor coin4 = _this.findActor("coin4");

                coin1.setVisible(false);
                coin2.setVisible(false);
                coin3.setVisible(false);
                coin4.setVisible(false);

                if(retrievableCoinsCount > 0 && retrievableCoinsCount <= 1){
                    coin1.setVisible(true);
                }
                else if(retrievableCoinsCount > 1 && retrievableCoinsCount <= 2){
                    coin1.setVisible(true);
                    coin2.setVisible(true);
                }
                else if(retrievableCoinsCount > 2 && retrievableCoinsCount <= 3){
                    coin1.setVisible(true);
                    coin2.setVisible(true);
                    coin3.setVisible(true);
                }
                else if(retrievableCoinsCount > 3){
                    coin1.setVisible(true);
                    coin2.setVisible(true);
                    coin3.setVisible(true);
                    coin4.setVisible(true);
                }
            }
        });
    }

}
