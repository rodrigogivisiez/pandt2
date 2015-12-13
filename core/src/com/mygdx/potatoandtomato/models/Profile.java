package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.enums.MascotEnum;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Profile {

    String facebookUserId;
    String facebookName;
    String userId;
    String gameName;
    MascotEnum mascotEnum;

    public Profile() {
    }

    public String getDisplayName(){
        if(gameName == null) return facebookName;
        else return gameName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String _userId) {
        this.userId = _userId;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public MascotEnum getMascotEnum(){
        return mascotEnum;
    }

    public void setMascotEnum(MascotEnum mascotEnum){
        this.mascotEnum = mascotEnum;
    }

}
