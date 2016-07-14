package com.potatoandtomato.common.models;

import com.potatoandtomato.common.enums.SpeechActionType;

/**
 * Created by SiongLeng on 12/7/2016.
 */
public class SpeechAction {

    private String msg;
    private SpeechActionType speechActionType;
    private long eachCharDuration;

    public SpeechAction(SpeechActionType speechActionType) {
        this.speechActionType = speechActionType;
    }

    public SpeechAction(SpeechActionType speechActionType, long duration) {
        this.speechActionType = speechActionType;
        this.eachCharDuration = duration;
    }

    public SpeechAction(String msg, SpeechActionType speechActionType) {
        this.msg = msg;
        this.speechActionType = speechActionType;
        eachCharDuration = 100;
    }

    public SpeechAction(String msg, SpeechActionType speechActionType, long eachCharDuration) {
        this.msg = msg;
        this.speechActionType = speechActionType;
        this.eachCharDuration = eachCharDuration;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SpeechActionType getSpeechActionType() {
        return speechActionType;
    }

    public void setSpeechActionType(SpeechActionType speechActionType) {
        this.speechActionType = speechActionType;
    }

    public long getEachCharDuration() {
        return eachCharDuration;
    }

    public void setEachCharDuration(long eachCharDuration) {
        this.eachCharDuration = eachCharDuration;
    }
}
