package com.potatoandtomato.common.models;

import com.potatoandtomato.common.utils.Strings;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 15/3/2016.
 */
public class Streak {

    private int streakCount;
    private String lastReviveRoomId;
    private int lastReviveRoundNumber;
    private String lastLoseRoomId;
    private int lastLoseRoundNumber;

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public String getLastReviveRoomId() {
        if(lastReviveRoomId == null) lastReviveRoomId = "";
        return lastReviveRoomId;
    }

    public void setLastReviveRoomId(String lastReviveRoomId) {
        this.lastReviveRoomId = lastReviveRoomId;
    }

    public int getLastReviveRoundNumber() {
        return lastReviveRoundNumber;
    }

    public void setLastReviveRoundNumber(int lastReviveRoundNumber) {
        this.lastReviveRoundNumber = lastReviveRoundNumber;
    }

    public String getLastLoseRoomId() {
        if(lastLoseRoomId == null) lastLoseRoomId = "";
        return lastLoseRoomId;
    }

    public void setLastLoseRoomId(String lastLoseRoomId) {
        this.lastLoseRoomId = lastLoseRoomId;
    }

    public int getLastLoseRoundNumber() {
        return lastLoseRoundNumber;
    }

    public void setLastLoseRoundNumber(int lastLoseRoundNumber) {
        this.lastLoseRoundNumber = lastLoseRoundNumber;
    }

    public void addStreakCount(int count){
        streakCount += count;
    }

    public void addStreakCount(){
        addStreakCount(1);
    }

    @JsonIgnore
    public boolean canRevive(){
        return lastReviveRoomId == null || lastReviveRoomId.equals("");
    }

    @JsonIgnore
    public boolean hasValidStreak(){
        if(getStreakCount() >= 2){
            if(Strings.isEmpty(getLastLoseRoomId())){
                return true;
            }
            else if(getLastLoseRoomId().equals(getLastReviveRoomId()) && getLastLoseRoundNumber() == getLastReviveRoundNumber()){
                return true;
            }
            else if(isLastReviveLargerThanLastLose()){
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isLastReviveLargerThanLastLose(){
        if(getLastReviveRoomId().equals(getLastLoseRoomId())){
            return getLastReviveRoundNumber() >= getLastLoseRoundNumber();
        }
        else{
            if(Strings.isLargerLexically(getLastReviveRoomId(), getLastLoseRoomId())){
                return true;
            }
        }
       return false;
    }


}
