package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public abstract class RoomInfoListener {

    private String roomId;

    public RoomInfoListener(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public abstract void onRoomInfoRetrievedSuccess(String[] inRoomUserIds);

    public abstract void onRoomInfoFailed();

}
