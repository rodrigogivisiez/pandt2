package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.potatoandtomato.common.absints.IDisconnectOverlayControl;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;

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
    private int count;
    private boolean showingResumeGame, showingLostConnection;
    private SafeThread safeThread;

    public ConnectionWatcher(GamingKit gamingKit, Broadcaster broadcaster, Confirm confirm,
                             Texts texts, Profile profile) {
        this.gamingKit = gamingKit;
        this.broadcaster = broadcaster;
        this.confirm = confirm;
        this.texts = texts;
        this.profile = profile;
        connectionWatcherListeners = new ArrayList();
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

    public void resetAndBackToBoot(boolean showDisconnectedMsg){
        showingResumeGame = false;
        showingLostConnection = false;
        confirm.close();
        broadcaster.broadcast(BroadcastEvent.DESTROY_ROOM);
        ptScreen.backToBoot();

        if(showDisconnectedMsg){
            confirm.show(texts.noConnection(), Confirm.Type.YES, null);
        }

        count = 0;
        if(safeThread != null) safeThread.kill();
    }

    public void addConnectionWatcherListener(ConnectionWatcherListener listener){
        connectionWatcherListeners.add(listener);
    }


    public void setListeners(){
        gamingKit.addListener(this.getClass().getName(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ConnectStatus st) {
                if(profile != null && userId != null && userId.equals(profile.getUserId())){
                    if(st == ConnectStatus.DISCONNECTED){
                        resetAndBackToBoot(true);
                    }
                    else if(st == ConnectStatus.DISCONNECTED_BUT_RECOVERABLE){
                        if(count == 0){
                            for(ConnectionWatcherListener connectionWatcherListener : connectionWatcherListeners){
                                connectionWatcherListener.onConnectionHalt();
                            }

                            safeThread = new SafeThread();
                            Threadings.runInBackground(new Runnable() {
                                @Override
                                public void run() {
                                    int totalCount = 25;
                                    showLostConnection(totalCount);

                                    while (count < totalCount){
                                        Threadings.sleep(1000);
                                        if(safeThread.isKilled()) return;

                                        count++;
                                        if(count % 3 == 0){
                                            gamingKit.recoverConnection();
                                        }
                                        showLostConnection(totalCount - count);
                                    }

                                    resetAndBackToBoot(true);
                                }
                            });

                        }
                    }
                    else if(st == ConnectStatus.CONNECTED_FROM_RECOVER){
                        count = 0;
                        if(safeThread != null) safeThread.kill();

                        for(ConnectionWatcherListener connectionWatcherListener : connectionWatcherListeners){
                            connectionWatcherListener.onConnectionResume();
                        }

                        Threadings.delay(1000, new Runnable() {
                            @Override
                            public void run() {
                                if(!showingResumeGame){
                                    hideLostConnection();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void setPtScreen(PTScreen ptScreen) {
        this.ptScreen = ptScreen;
    }

    public void showLostConnection(int remainingSecs){
        String msg = String.format(texts.lostConnection(), remainingSecs);
        if(!showingLostConnection){
            showingLostConnection = true;
            confirm.show(msg, Confirm.Type.LOADING_WITH_CANCEL, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result == Result.CANCEL) {
                        resetAndBackToBoot(false);
                    }
                }
            }, texts.clickToDisconnect());
        }
        else{
            confirm.updateMessage(msg);
        }
    }

    public void hideLostConnection(){
        showingLostConnection = false;
        confirm.close();

    }

    @Override
    public void showResumingGameOverlay(int remainingSecs) {
        String msg = String.format(texts.connectionRecovered(), remainingSecs);
        if(!showingResumeGame && !showingLostConnection){
            showingResumeGame = true;
            confirm.show(msg, Confirm.Type.LOADING_WITH_CANCEL, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result == Result.CANCEL) {
                        resetAndBackToBoot(false);
                    }
                }
            }, texts.clickToDisconnect());
        }
        else{
            confirm.updateMessage(msg);
        }
    }

    @Override
    public void hideOverlay() {
        confirm.close();
        showingResumeGame = false;
    }



}
