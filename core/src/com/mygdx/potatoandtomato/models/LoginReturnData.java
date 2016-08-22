package com.mygdx.potatoandtomato.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by SiongLeng on 25/6/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginReturnData {

    private String userId;
    private String secret;
    private String token;
    private String country;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
