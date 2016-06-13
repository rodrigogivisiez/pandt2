package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.controls.CoinMachineControl;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 13/6/2016.
 */
public class Coins {

    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private IPTGame iptGame;
    private CoinMachineControl coinMachineControl;
    private boolean visible;

    public Coins(Assets assets, SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.iptGame = iptGame;

        coinMachineControl = new CoinMachineControl(assets, soundsPlayer, texts, iptGame);
        show();

        setListeners();
    }

    public void show(){
        visible = true;
    }

    public void hide(){

    }

    public void setListeners(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                coinMachineControl.getCoinInsertRootTable().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        coinMachineControl.putCoin();
                    }
                });


            }
        });
    }







    public void render(float delta){
        if(isVisible()){
            coinMachineControl.render(delta);
        }
    }

    public void resize(int width, int height){
        coinMachineControl.resize(width, height);
    }



    public boolean isVisible() {
        return visible;
    }
}
