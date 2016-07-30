package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.scenes.PlayerConnectionStateListener;
import com.mygdx.potatoandtomato.enums.GameConnectionStatus;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 7/6/2016.
 */
public class PlayerConnectionState implements Disposable {

    private GameConnectionStatus gameConnectionStatus;
    private SafeThread safeThread;
    private Player player;
    private PlayerConnectionStateListener playerConnectionStateListener;

    public PlayerConnectionState(Player player, PlayerConnectionStateListener playerConnectionStateListener) {
        this.player = player;
        this.gameConnectionStatus = GameConnectionStatus.Connected;
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


    public boolean setConnectionStatus(GameConnectionStatus newGameConnectionStatus) {
        if(this.gameConnectionStatus != newGameConnectionStatus){

            GameConnectionStatus oldGameConnectionStatus = this.gameConnectionStatus;

            if(newGameConnectionStatus == GameConnectionStatus.Disconnected){
                if(oldGameConnectionStatus == GameConnectionStatus.Abandoned
                        || oldGameConnectionStatus == GameConnectionStatus.Disconnected_No_CountDown){
                    return false;
                }
                if(oldGameConnectionStatus == GameConnectionStatus.Connected){
                    playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newGameConnectionStatus);
                }
            }
            else if(newGameConnectionStatus == GameConnectionStatus.Disconnected_No_CountDown){
                if(oldGameConnectionStatus == GameConnectionStatus.Abandoned){
                    return false;
                }
                if(oldGameConnectionStatus == GameConnectionStatus.Connected){
                    playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newGameConnectionStatus);
                }
            }
            else if(newGameConnectionStatus == GameConnectionStatus.Abandoned){
                playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newGameConnectionStatus);
            }
            else if(newGameConnectionStatus == GameConnectionStatus.Connected){
                if(oldGameConnectionStatus == GameConnectionStatus.Abandoned){
                    return false;
                }
                playerConnectionStateListener.onPlayerConnectionChanged(player.getUserId(), newGameConnectionStatus);
            }


            if(newGameConnectionStatus != GameConnectionStatus.Disconnected){
                stopDisconnectTimeoutThread();
            }
            else{
                startDisconnectTimeoutThread();
            }

            this.gameConnectionStatus = newGameConnectionStatus;

            return true;
        }
        return false;
    }

    public GameConnectionStatus getGameConnectionStatus() {
        return gameConnectionStatus;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
    }


}
