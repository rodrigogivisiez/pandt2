package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomUser {

    Profile profile;
    Integer slotIndex;
    boolean ready;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Integer getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(Integer slotIndex) {
        this.slotIndex = slotIndex;
    }

    public boolean getReady() {
        return ready;
    }

    public void setReady(boolean isReady) {
        this.ready = isReady;
    }
}
