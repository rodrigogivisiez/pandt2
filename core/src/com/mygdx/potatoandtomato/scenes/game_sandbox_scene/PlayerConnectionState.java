package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.scenes.PlayerConnectionStateListener;
import com.mygdx.potatoandtomato.enums.ConnectionStatus;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

import javax.xml.bind.annotation.XmlElementDecl;

/**
 * Created by SiongLeng on 7/6/2016.
 */
public class PlayerConnectionState implements Disposable {

    private ConnectionStatus connectionStatus;
    private SafeThread safeThread;
    private Player player;
    private PlayerConnectionStateListener playerConnectionStateListener;

    public PlayerConnectionState(Player player, PlayerConnectionStateListener playerConnectionStateListener) {
        this.player = player;
        this.connectionStatus = ConnectionStatus.Connected;
        this.playerConnectionStateListener = playerConnectionStateListener;
    }

    public void startDisconnectTimeoutThread(){
        safeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int i = Global.ABANDON_TOLERANCE_SECS;
                while (i > 0){
                    if (safeThread.isKilled()) return;
                    i--;
                    Threadings.sleep(1000);
                    Logs.show(i);
                }

                //disconnect timeout, abandon user now
                playerConnectionStateListener.onPlayerDisconnectTimeout(player.getUserId());
            }
        });
    }

    public void stopDisconnectTimeoutThread(){
        if(safeThread != null) safeThread.kill();
    }


    public boolean setConnectionStatus(ConnectionStatus newConnectionStatus) {
        if(this.connectionStatus != newConnectionStatus){

            ConnectionStatus oldConnectionStatus = this.connectionStatus;

            if(newConnectionStatus == ConnectionStatus.Disconnected){
                if(oldConnectionStatus == ConnectionStatus.Abandoned
                        || oldConnectionStatus == ConnectionStatus.Disconnected_No_CountDown){
                    return false;
                }
                if(oldConnectionStatus == ConnectionStatus.Connected){
                    playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newConnectionStatus);
                }
            }
            else if(newConnectionStatus == ConnectionStatus.Disconnected_No_CountDown){
                if(oldConnectionStatus == ConnectionStatus.Abandoned){
                    return false;
                }
                if(oldConnectionStatus == ConnectionStatus.Connected){
                    playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newConnectionStatus);
                }
            }
            else if(newConnectionStatus == ConnectionStatus.Abandoned){
                playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newConnectionStatus);
            }
            else if(newConnectionStatus == ConnectionStatus.Connected){
                if(oldConnectionStatus == ConnectionStatus.Abandoned){
                    return false;
                }
                playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newConnectionStatus);
            }


            if(newConnectionStatus != ConnectionStatus.Disconnected){
                stopDisconnectTimeoutThread();
            }
            else{
                startDisconnectTimeoutThread();
            }

            this.connectionStatus = newConnectionStatus;

            return true;
        }
        return false;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
    }


}
