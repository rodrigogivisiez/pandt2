package com.potatoandtomato.games.absintf;

import com.potatoandtomato.games.enums.BonusType;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.TouchedPoint;
import com.potatoandtomato.games.models.WonStageModel;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public abstract class RoomMsgListener {


    public abstract void onTouched(TouchedPoint touchedPoint, String userId);

    public abstract void onLose(GameModel loseGameModel);

    public abstract void onWon(WonStageModel wonStageModel);

    public abstract void onDownloadImageRequest(ArrayList<String> ids);

    public abstract void onGoToNextStage(String id, int stageNumber, StageType stageType, BonusType bonusType, String extra);

    public abstract void onStartPlaying();
}
