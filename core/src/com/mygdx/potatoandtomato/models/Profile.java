package com.mygdx.potatoandtomato.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by SiongLeng on 9/12/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    String facebookUserId;
    String facebookName;
    String userId;
    String gameName;
    String gcmId;
    UserPlayingState userPlayingState;
    String gameNameLower;
    String token;

    public Profile() {
        reset();
    }

    @JsonIgnore
    public String getDisplayName(int limit){
        if(limit == 0) limit = 9999;
        String returnName;
        if(gameName == null || gameName.trim().equals("")) returnName = facebookName;
        else returnName = gameName;

        if(returnName == null || returnName.trim().equals("")){
            return "No name";
        }
        if(returnName.length() > limit) {
            returnName = returnName.substring(0, limit);
            returnName+="..";
        }
        return returnName;
    }

    public UserPlayingState getUserPlayingState() {
        if(userPlayingState == null ||  userPlayingState.getRoomId() == null) userPlayingState = new UserPlayingState("0", -1);
        return userPlayingState;
    }

    public void setUserPlayingState(UserPlayingState userPlayingState) {
        this.userPlayingState = userPlayingState;
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

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameNameLower(){
        if(this.gameName == null){
            return "";
        }
        else{
            return this.gameName.toLowerCase();
        }
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    @JsonIgnore
    public String getToken() {
        return token;
    }

    @JsonIgnore
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Profile){
            Profile p = (Profile) o;
            if(p.getUserId() == null && this.getUserId() == null){
                return true;
            }
            else if((p.getUserId() != null && this.getUserId() == null) || (p.getUserId() == null && this.getUserId() != null)){
                return false;
            }
            else{
                return ((Profile) o).getUserId().equals(this.getUserId());
            }


        }
        return super.equals(o);
    }

    @JsonIgnore
    public void copyToThis(Profile toCopyProfile){
        this.facebookUserId = toCopyProfile.getFacebookUserId();
        this.facebookName = toCopyProfile.getFacebookName();
        this.userId = toCopyProfile.getUserId();
        this.gameName = toCopyProfile.getGameName();
        this.gcmId = toCopyProfile.getGcmId();
        this.userPlayingState = toCopyProfile.getUserPlayingState();
        this.gameNameLower = toCopyProfile.getGameNameLower();
        this.token = toCopyProfile.getToken();
    }

    @JsonIgnore
    public void reset(){
        this.facebookUserId = "";
        this.facebookName = "";
        this.userId = "";
        this.gameName = "";
        this.gcmId ="";
        this.userPlayingState = null;
        this.gameNameLower = "";
        this.token = "";
    }



}
