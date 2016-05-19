package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 16/5/2016.
 */
public class UserIdSecretModel {

    public String userId;
    public String secret;

    public UserIdSecretModel(String userId, String secret) {
        this.userId = userId;
        this.secret = secret;
    }

    public String getUserId() {
        return userId;
    }

    public String getSecret() {
        return secret;
    }
}
