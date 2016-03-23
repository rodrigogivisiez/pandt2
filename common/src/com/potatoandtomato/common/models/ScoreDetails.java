package com.potatoandtomato.common.models;

/**
 * Created by SiongLeng on 14/3/2016.
 */
public class ScoreDetails {

    long value;
    String reason;
    boolean addOrMultiply;
    boolean canAddStreak;

    public ScoreDetails(long value, String reason, boolean addOrMultiply, boolean canAddStreak) {
        this.value = value;
        this.reason = reason;
        this.addOrMultiply = addOrMultiply;
        this.canAddStreak = canAddStreak;
    }

    public boolean canAddStreak() {
        return canAddStreak;
    }

    public void setCanAddStreak(boolean canAddStreak) {
        this.canAddStreak = canAddStreak;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isAddOrMultiply() {
        return addOrMultiply;
    }

    public void setAddOrMultiply(boolean addOrMultiply) {
        this.addOrMultiply = addOrMultiply;
    }
}
