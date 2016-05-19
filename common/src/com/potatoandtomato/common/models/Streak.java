package com.potatoandtomato.common.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 18/5/2016.
 */
public class Streak {

    public int streakCount;
    public int beforeStreakCount;

    public Streak(int streakCount, int beforeStreakCount) {
        this.streakCount = streakCount;
        this.beforeStreakCount = beforeStreakCount;
    }

    public Streak() {
    }


    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public int getBeforeStreakCount() {
        return beforeStreakCount;
    }

    public void setBeforeStreakCount(int beforeStreakCount) {
        this.beforeStreakCount = beforeStreakCount;
    }

    @JsonIgnore
    public void addStreak(int streak) {
        this.streakCount += streak;
    }

    @JsonIgnore
    public void resetStreak(){
        this.setStreakCount(0);
    }

    @JsonIgnore
    public boolean hasValidStreak(){
        return streakCount >= 3;
    }

    @Override
    public Streak clone(){
        Streak streak = new Streak(this.streakCount, this.beforeStreakCount);
        return streak;
    }

}
