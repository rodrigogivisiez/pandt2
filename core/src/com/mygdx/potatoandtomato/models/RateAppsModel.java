package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 12/8/2016.
 */
public class RateAppsModel {

    private boolean liked;
    private String reason;


    public RateAppsModel(boolean liked, String reason) {
        this.liked = liked;
        this.reason = reason;
    }


    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
