package com.mygdx.potatoandtomato.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 12/8/2016.
 */
public class InboxMessage {

    private String title;
    private String msg;
    private String id;
    private boolean read;

    public InboxMessage() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isRead() {
        return read;
    }

    @JsonIgnore
    public void setRead(boolean read) {
        this.read = read;
    }
}
