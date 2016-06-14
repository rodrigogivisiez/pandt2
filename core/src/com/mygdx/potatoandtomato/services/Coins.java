package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.controls.CoinMachineControl;
import com.mygdx.potatoandtomato.controls.TopBarCoinControl;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 13/6/2016.
 */
public class Coins {

    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Texts texts;
    private IPTGame iptGame;
    private CoinMachineControl coinMachineControl;
    private ArrayList<TopBarCoinControl> topBarCoinControls;

    public Coins(Broadcaster broadcaster, Assets assets,
                 SoundsPlayer soundsPlayer, Texts texts, IPTGame iptGame, SpriteBatch batch) {
        this.assets = assets;
        this.soundsPlayer = soundsPlayer;
        this.texts = texts;
        this.iptGame = iptGame;

        coinMachineControl = new CoinMachineControl(broadcaster, assets, soundsPlayer, texts, iptGame, batch);
        topBarCoinControls = new ArrayList();

        setListeners();
    }

    public void showCoinMachine(){
        coinMachineControl.show();
    }

    public void hideCoinMachine(){
        coinMachineControl.hide();
    }

    public void setListeners(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                coinMachineControl.getCoinInsertRootTable().addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        coinMachineControl.putCoinAnimation(null);
                    }
                });


            }
        });
    }

    public void render(float delta){
        coinMachineControl.render(delta);
    }

    public void resize(int width, int height){
        coinMachineControl.resize(width, height);
    }


    public TopBarCoinControl getNewTopBarCoinControl() {
        TopBarCoinControl topBarCoinControl = new TopBarCoinControl(assets);
        topBarCoinControls.add(topBarCoinControl);
        return topBarCoinControl;
    }
}
