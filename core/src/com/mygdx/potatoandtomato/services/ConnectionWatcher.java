package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.JoinRoomListener;
import com.mygdx.potatoandtomato.absintflis.services.ConnectionWatcherListener;
import com.mygdx.potatoandtomato.enums.ClientConnectionStatus;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.absints.IDisconnectOverlayControl;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

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
    private ConcurrentHashMap<String, ConnectionWatcherListener> connectionWatcherListenersMap;
    private boolean showingResumeGame, showingLostConnection, confirmDisconnected;
    private SafeThread safeThread;

    public ConnectionWatcher(GamingKit gamingKit, Broadcaster broadcaster, Confirm confirm,
                             Texts texts, Profile profile) {
        this.gamingKit = gamingKit;
        this.broadcaster = broadcaster;
        this.confirm = confirm;
        this.texts = texts;
        this.profile = profile;
        connectionWatcherListenersMap = new ConcurrentHashMap();
        setListeners();
    }


    public void joinedRoom(Room room){
        this.room = room;
    }

    public void leftRoom(){
        this.room = null;
    }

    public void resetAndBackToBoot(boolean showDisconnectedMsg){
        gamingKit.disconnect();
        showingResumeGame = false;
        showingLostConnection = false;
        confirmDisconnected = false;
        confirm.close(ConfirmIdentifier.Resiliency);
        confirm.close(ConfirmIdentifier.ResumingGameSession);

        broadcaster.broadcast(BroadcastEvent.DESTROY_ROOM);
        ptScreen.backToBoot();

        if(showDisconnectedMsg){
            confirm.show(ConfirmIdentifier.Resiliency, texts.confirmNoConnection(), Confirm.Type.YES, null);
        }

        if(safeThread != null) safeThread.kill();
    }

    public void addConnectionWatcherListener(String classTag, ConnectionWatcherListener listener){
        connectionWatcherListenersMap.put(classTag, listener);
    }

    public void clearConnectionWatcherListenerByClassTag(String classTag){
        connectionWatcherListenersMap.remove(classTag);
    }

    public void setListeners(){
        gamingKit.addListener(this.getClass().getName(), new ConnectionChangedListener() {
            @Override
            public void onChanged(String userId, ClientConnectionStatus st) {
                if(profile != null && userId != null && userId.equals(profile.getUserId())){
                    if(ClientConnectionStatus.isConnected(st)){
                        if(safeThread != null) safeThread.kill();

                        if(confirmDisconnected){
                            if(room != null){
                                gamingKit.joinRoom(room.getWarpRoomId());
                            }
                            else{
                                onSuccessConnectedBack();
                            }
                        }
                        else{
                            onSuccessConnectedBack();
                        }
                    }
                    else{
                        if(st == ClientConnectionStatus.DISCONNECTED){
                            confirmDisconnected = true;
                        }
                        onDisconnected();

                    }
                }
            }
        });

        gamingKit.addListener(this.getClass().getName(), new JoinRoomListener() {
            @Override
            public void onRoomJoined(String roomId) {
                if(confirmDisconnected){
                    confirmDisconnected = false;
                    onSuccessConnectedBack();
                }
            }

            @Override
            public void onJoinRoomFailed() {
                if(confirmDisconnected){
                    onDisconnected();
                }
            }
        });

    }

    private void onSuccessConnectedBack(){
        for(ConnectionWatcherListener connectionWatcherListener : connectionWatcherListenersMap.values()){
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

    private void onDisconnected(){
        if(safeThread == null || safeThread.isKilled()){
            for(ConnectionWatcherListener connectionWatcherListener : connectionWatcherListenersMap.values()){
                connectionWatcherListener.onConnectionHalt();
            }

            safeThread = new SafeThread();
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    int totalCount = 60;
                    showLostConnection(totalCount);

                    while (count < totalCount){
                        Threadings.sleep(1000);
                        if(safeThread.isKilled()) return;

                        count++;
                        if(count % 3 == 0){
                            if(!confirmDisconnected){
                                gamingKit.recoverConnection();
                            }
                            else{
                                Logs.show("Recover via reconnect username");
                                gamingKit.connect(profile);
                            }
                        }
                        showLostConnection(totalCount - count);
                    }

                    resetAndBackToBoot(true);
                }
            });
        }
    }

    public void setPtScreen(PTScreen ptScreen) {
        this.ptScreen = ptScreen;
    }

    public void showLostConnection(int remainingSecs){
        String msg = String.format(texts.confirmLostConnection(), remainingSecs);
        if(!showingLostConnection){
            showingLostConnection = true;
            confirm.show(ConfirmIdentifier.Resiliency, msg, Confirm.Type.LOADING_WITH_CANCEL, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result == Result.CANCEL) {
                        resetAndBackToBoot(false);
                    }
                }
            }, texts.btnTextClickToDisconnect());
        }
        else{
            confirm.updateMessage(msg);
        }
    }

    public void hideLostConnection(){
        showingLostConnection = false;
        confirm.close(ConfirmIdentifier.Resiliency);

    }

    @Override
    public void showResumingGameOverlay(int remainingSecs) {
        String msg = String.format(texts.confirmConnectionRecovered(), remainingSecs);
        if(!showingResumeGame && !showingLostConnection){
            showingResumeGame = true;
            confirm.show(ConfirmIdentifier.ResumingGameSession, msg, Confirm.Type.LOADING_WITH_CANCEL, new ConfirmResultListener() {
                @Override
                public void onResult(Result result) {
                    if (result == Result.CANCEL) {
                        resetAndBackToBoot(false);
                    }
                }
            }, texts.btnTextClickToDisconnect());
        }
        else{
            confirm.updateMessage(msg);
        }
    }

    @Override
    public void hideOverlay() {
        confirm.close(ConfirmIdentifier.ResumingGameSession);
        showingResumeGame = false;
    }



}
