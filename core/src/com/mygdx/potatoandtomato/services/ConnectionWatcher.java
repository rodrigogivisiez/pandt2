package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.controls.DisconnectedOverlay;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.absints.IDisconnectOverlayControl;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 27/5/2016.
 */
public class ConnectionWatcher implements IDisconnectOverlayControl {

    private GamingKit gamingKit;
    private Broadcaster broadcaster;
    private PTScreen ptScreen;
    private Confirm confirm;
    private Texts texts;
    private boolean playingGame;
    private Room room;
    private Profile profile;
    private ArrayList<ConnectionWatcherListener> connectionWatcherListeners;
    private DisconnectedOverlay disconnectedOverlay;
    private int count;
    private boolean showResumingGame;

    public ConnectionWatcher(GamingKit gamingKit, SpriteBatch spriteBatch, Assets assets,
                             Broadcaster broadcaster, Confirm confirm,
                             Texts texts, IPTGame iptGame, Profile profile) {
        this.gamingKit = gamingKit;
        this.broadcaster = broadcaster;
        this.confirm = confirm;
        this.texts = texts;
        this.profile = profile;
        connectionWatcherListeners = new ArrayList();
        this.disconnectedOverlay = new DisconnectedOverlay(spriteBatch, assets, broadcaster, texts, iptGame);
        setListeners();
    }


    public void gameStarted(Room room){
        playingGame = true;
        this.room = room;
    }

    public void gameEnded(){
        playingGame = false;
        room = null;
        connectionWatcherListeners.clear();
    }

    public void resetAndBackToBoot(){
        showResumingGame = false;
        disconnectedOverlay.resetText();
        broadcaster.broadcast(BroadcastEvent.DESTROY_ROOM);
        ptScreen.backToBoot();
        confirm.show(texts.noConnection(), Confirm.Type.YES, null);
        count = 0;
    }

    public void addConnectionWatcherListener(ConnectionWatcherListener listener){
        connectionWatcherListeners.add(listener);
    }

    public void resize(int width, int height){
        disconnectedOverlay.resize(width, height);
    }


    public void render(float delta){
        disconnectedOverlay.render(delta);
    }

    public void setListeners(){
        gamingKit.addListener(this.getClass().getName(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ConnectStatus st) {
                if(profile != null && userId != null && userId.equals(profile.getUserId())){
                    if(!playingGame){
                        if(st == ConnectStatus.DISCONNECTED || st == ConnectStatus.DISCONNECTED_BUT_RECOVERABLE){
                            resetAndBackToBoot();
                        }
                    }
                    else{
                        if(st == ConnectStatus.DISCONNECTED){
                            resetAndBackToBoot();
                        }
                        else if(st == ConnectStatus.DISCONNECTED_BUT_RECOVERABLE){
                            if(count == 0){
                                for(ConnectionWatcherListener connectionWatcherListener : connectionWatcherListeners){
                                    connectionWatcherListener.onConnectionHalt();
                                }
                            }
                            if(count < 5){
                                Threadings.delay(5000, new Runnable() {
                                    @Override
                                    public void run() {
                                        gamingKit.recoverConnection();
                                    }
                                });
                                count++;
                                disconnectedOverlay.setVisible(true);
                            }
                            else{
                                resetAndBackToBoot();
                            }
                        }
                        else if(st == ConnectStatus.CONNECTED_FROM_RECOVER){
                            count = 0;
                            for(ConnectionWatcherListener connectionWatcherListener : connectionWatcherListeners){
                                connectionWatcherListener.onConnectionResume();
                            }

                            Threadings.delay(1000, new Runnable() {
                                @Override
                                public void run() {
                                    if(!showResumingGame){
                                        disconnectedOverlay.setVisible(false);
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }

    public void setPtScreen(PTScreen ptScreen) {
        this.ptScreen = ptScreen;
    }

    @Override
    public void showResumingGameOverlay(int remainingSecs) {
        disconnectedOverlay.setVisible(true);
        showResumingGame = true;
        disconnectedOverlay.showResumingGameText(remainingSecs);
    }

    @Override
    public void hideOverlay() {
        disconnectedOverlay.setVisible(false);
        disconnectedOverlay.resetText();
        showResumingGame = false;
    }



}
