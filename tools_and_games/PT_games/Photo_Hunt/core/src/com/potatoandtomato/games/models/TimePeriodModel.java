package com.potatoandtomato.games.models;

/**
 * Created by SiongLeng on 19/4/2016.
 */
public class TimePeriodModel {

    private float start;
    private float end;

    public TimePeriodModel() {
    }

    public TimePeriodModel(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }
}
