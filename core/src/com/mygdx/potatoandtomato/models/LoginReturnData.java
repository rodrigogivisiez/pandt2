package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 25/6/2016.
 */
public class LoginReturnData {

    private String userId;
    private String secret;
    private String token;

    public LoginReturnData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
