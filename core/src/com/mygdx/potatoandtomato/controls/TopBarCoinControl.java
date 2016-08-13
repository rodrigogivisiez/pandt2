package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.enums.ShopProducts;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

/**
 * Created by SiongLeng on 14/6/2016.
 */
public class TopBarCoinControl extends Table {

    private Assets assets;
    private Table _this;
    private Label coinLabel;
    private PTScreen ptScreen;
    private SoundsPlayer soundsPlayer;
    private boolean disableClick;
    private boolean shown;
    private Image freeCoinPointImage;


    public TopBarCoinControl(Assets assets, int myCoinCount, boolean disableClick, PTScreen ptScreen, SoundsPlayer soundsPlayer) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
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

    public void setCoinCount(final int newCount, final ShopProducts chosenShopProduct){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(chosenShopProduct != null && shown){
                    TextureRegion region = null;
                    switch (chosenShopProduct){
                        case ONE_COIN:
                            region = assets.getTextures().get(Textures.Name.COIN_ONE);
                            break;
                        case FIVE_COINS:
                            region = assets.getTextures().get(Textures.Name.COIN_FIVE);
                            break;
                        case FIFTEEN_COINS:
                            region = assets.getTextures().get(Textures.Name.COIN_FIFTEEN);
                            break;
                        case FIFTY_COINS:
                            region = assets.getTextures().get(Textures.Name.COIN_BAG);
                            break;
                        case PURSE:
                            region = assets.getTextures().get(Textures.Name.COIN_PURSE);
                            break;
                    }
                    final Image floatInImage = new Image(region);
                    Vector2 size = Sizes.resizeByWidthWithMaxWidth(40, region);
                    floatInImage.setSize(size.x, size.y);
                    floatInImage.setPosition(0, -size.y);
                    floatInImage.addAction(sequence(parallel(fadeOut(0.3f), Actions.moveBy(0, size.y, 0.3f)), new RunnableAction(){
                        @Override
                        public void run() {
                            floatInImage.remove();
                            Threadings.runInBackground(new Runnable() {
                                @Override
                                public void run() {
                                    int originalCount = Integer.valueOf(coinLabel.getText().toString());
                                    int toAdd = newCount - originalCount;

                                    int step = Math.max(toAdd / 10, 1);
                                    while (toAdd > 0){
                                        if(!shown) break;

                                        originalCount += Math.min(step, toAdd);
                                        final int finalOriginalCount = originalCount;
                                        Threadings.postRunnable(new Runnable() {
                                            @Override
                                            public void run() {
                                                coinLabel.setText(String.valueOf(finalOriginalCount));
                                                soundsPlayer.playSoundEffect(Sounds.Name.COIN_ADDING);
                                            }
                                        });
                                        Threadings.sleep(100);
                                        toAdd -= step;
                                    }

                                    Threadings.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            coinLabel.setText(String.valueOf(newCount));
                                        }
                                    });

                                }
                            });

                        }
                    }));

                    soundsPlayer.playSoundEffect(Sounds.Name.COIN_PURCHASED);
                    _this.addActor(floatInImage);
                }
                else{
                    coinLabel.setText(String.valueOf(newCount));
                }
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

    public void showFreeCoinPointing(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                freeCoinPointImage = new Image(assets.getTextures().get(Textures.Name.FREE_COIN_POINT_RIGHT));
                freeCoinPointImage.setPosition(-freeCoinPointImage.getPrefWidth() - 20, -_this.getPrefHeight() /2);
                freeCoinPointImage.addAction(forever(sequence(Actions.moveBy(10f, 0f, 0.7f), Actions.moveBy(-10f, 0f, 0.7f))));
                _this.addActor(freeCoinPointImage);
            }
        });
    }

    public void hideFreeCoinPointing(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(freeCoinPointImage != null){
                    freeCoinPointImage.remove();
                }
            }
        });
    }


    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
