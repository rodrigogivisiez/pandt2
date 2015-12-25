package com.potatoandtomato.common;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class Player {

    public String name;
    public String userId;
    public int potatoOrTomato;

    public Player(String name, String userId, int potatoOrTomato) {
        this.name = name;
        this.userId = userId;
        this.potatoOrTomato = potatoOrTomato;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPotatoOrTomato() {
        return potatoOrTomato;
    }

    public void setPotatoOrTomato(int potatoOrTomato) {
        this.potatoOrTomato = potatoOrTomato;
    }
}
