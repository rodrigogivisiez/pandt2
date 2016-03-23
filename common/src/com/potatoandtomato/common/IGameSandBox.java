package com.potatoandtomato.common;

import com.potatoandtomato.common.models.ScoreDetails;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 6/1/2016.
 */
public interface IGameSandBox {

    void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable);
    void userAbandoned();
    void onGameLoaded();
    void endGame();
    void inGameUpdateRequest(String msg);
    void updateScores(HashMap<Team, ArrayList<ScoreDetails>> winners, ArrayList<Team> losers);


}
