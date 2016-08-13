package com.potatoandtomato.common.models;

import com.potatoandtomato.common.utils.SafeDouble;

/**
 * Created by SiongLeng on 14/3/2016.
 */
public class ScoreDetails {

    SafeDouble value;
    String reason;
    boolean addOrMultiply;
    boolean canAddStreak;

    public ScoreDetails() {
    }

    public ScoreDetails(double value, String reason, boolean addOrMultiply, boolean canAddStreak) {
        this.value = new SafeDouble(value);
        this.reason = reason;
        this.addOrMultiply = addOrMultiply;
        this.canAddStreak = canAddStreak;
    }

    public boolean isCanAddStreak() {
        return canAddStreak;
    }

    public void setCanAddStreak(boolean canAddStreak) {
        this.canAddStreak = canAddStreak;
    }

    public double getValue() {
        if(this.value == null){
            this.value = new SafeDouble(0.0);
        }
        return value.getValue();
    }

    public void setValue(double value) {
        if(this.value == null){
            this.value = new SafeDouble(value);
        }
        else{
            this.value.setValue(value);
        }
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    //adding = true, multiplying = false
    public boolean isAddOrMultiply() {
        return addOrMultiply;
    }

    //adding = true, multiplying = false
    public void setAddOrMultiply(boolean addOrMultiply) {
        this.addOrMultiply = addOrMultiply;
    }

    @Override
    public int hashCode()
    {
        return reason.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return this.reason.equals(o);
    }

}
