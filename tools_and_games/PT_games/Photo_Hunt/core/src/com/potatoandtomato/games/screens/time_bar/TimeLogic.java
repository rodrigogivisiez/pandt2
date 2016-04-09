package com.potatoandtomato.games.screens.time_bar;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.TimeLogicListener;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.statics.Global;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class TimeLogic implements Disposable {

    private GameModel gameModel;
    private GameCoordinator gameCoordinator;
    private boolean timeRunning;
    private SafeThread timeThread;
    private TimeLogicListener listener;

    public TimeLogic(GameModel gameModel, GameCoordinator gameCoordinator, TimeLogicListener listener) {
        this.gameModel = gameModel;
        this.gameCoordinator = gameCoordinator;
        this.listener = listener;
    }

    public void start(){
        timeRunning = true;
        timeThread = new SafeThread();
        if(Global.REVIEW_MODE){
            return;
        }

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(timeThread.isKilled() || gameModel.getRemainingSecs() <= 0) break;
                    else{
                        gameModel.setRemainingSecs(gameModel.getRemainingSecs() - 1);
                        Logs.show(String.valueOf(gameModel.getRemainingSecs()));
                        Threadings.sleep(1000);
                    }
                }

                if(gameModel.getRemainingSecs() <= 0 && !timeThread.isKilled()){
                    timeRunning = false;
                    listener.onTimeFinished();
                    Logs.show("Time up!");
                }
            }
        });
    }

    public void stop(){
        timeThread.kill();
        timeRunning = false;
    }

    public boolean isTimeRunning() {
        return timeRunning;
    }

    public void reduceTime(){
        gameModel.setRemainingSecs(gameModel.getRemainingSecs() - 2);
    }

    public int getRemainingSecs() {
        return gameModel.getRemainingSecs();
    }

    @Override
    public void dispose() {
        stop();
        listener = null;
    }
}
