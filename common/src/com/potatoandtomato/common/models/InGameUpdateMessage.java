package com.potatoandtomato.common.models;

/**
 * Created by SiongLeng on 27/12/2015.
 */
public class InGameUpdateMessage {

    public String senderId;
    public String msg;

    public InGameUpdateMessage(String senderId, String msg) {
        this.senderId = senderId;
        this.msg = msg;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
