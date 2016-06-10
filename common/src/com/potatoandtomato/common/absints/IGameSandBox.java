package com.potatoandtomato.common.absints;

import com.potatoandtomato.common.enums.RoomUpdateType;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 6/1/2016.
 */
public interface IGameSandBox {

    void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable);
    void userAbandoned(String userId);
    void endGame();
    void sendUpdate(RoomUpdateType updateType, String msg);
    void sendPrivateUpdate(RoomUpdateType updateType, String toUserId, String msg);
    void vibrate(double periodInMili);
    void finalizing(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers, boolean abandoned);
    void gameFailed();

}
