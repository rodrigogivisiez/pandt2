package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 21/12/2015.
 */
public class FacebookProfile {

    public String name;
    public String userId;


    public FacebookProfile(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}
