package com.mygdx.potatoandtomato.helpers.assets;

import com.mygdx.potatoandtomato.enums.MascotEnum;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Profile {

    MascotEnum mascot;
    String facebookUserId;
    String facebookName;
    String userId;
    String displayName;

    public Profile() {
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
}
