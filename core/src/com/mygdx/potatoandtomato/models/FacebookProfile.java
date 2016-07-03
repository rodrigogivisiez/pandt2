package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 21/12/2015.
 */
public class FacebookProfile {

    public String name;
    public String userId;
    public String token;


    public FacebookProfile(String name, String userId, String token) {
        this.name = name;
        this.userId = userId;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfilePicUrl(){
        return "https://graph.facebook.com/"+getUserId()+"/picture";
    }

}
