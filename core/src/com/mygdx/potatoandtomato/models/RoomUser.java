package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.enums.RoomUserState;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomUser {

    Profile profile;
    Integer slotIndex;
    RoomUserState roomUserState;

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

    public RoomUserState getRoomUserState() {
        return roomUserState;
    }

    public void setRoomUserState(RoomUserState roomUserState) {
        this.roomUserState = roomUserState;
    }
}
