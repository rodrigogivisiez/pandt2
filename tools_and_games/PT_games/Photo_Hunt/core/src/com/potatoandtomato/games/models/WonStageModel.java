package com.potatoandtomato.games.models;

import com.potatoandtomato.games.enums.StageType;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class WonStageModel {

    public int stageNumber;
    public int beforeScore;
    public int remainingSecs;
    public String nextStageId;
    public StageType nextStageType;

    public WonStageModel() {
    }

    public WonStageModel(int stageNumber, int beforeScore, int remainingSecs, String nextStageId, StageType nextStageType) {
        this.stageNumber = stageNumber;
        this.beforeScore = beforeScore;
        this.remainingSecs = remainingSecs;
        this.nextStageId = nextStageId;
        this.nextStageType = nextStageType;
    }

    public StageType getNextStageType() {
        return nextStageType;
    }

    public void setNextStageType(StageType nextStageType) {
        this.nextStageType = nextStageType;
    }

    public String getNextStageId() {
        return nextStageId;
    }

    public void setNextStageId(String nextStageId) {
        this.nextStageId = nextStageId;
    }

    public int getRemainingSecs() {
        return remainingSecs;
    }

    public void setRemainingSecs(int remainingSecs) {
        this.remainingSecs = remainingSecs;
    }

    public int getBeforeScore() {
        return beforeScore;
    }

    public void setBeforeScore(int beforeScore) {
        this.beforeScore = beforeScore;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }
}
