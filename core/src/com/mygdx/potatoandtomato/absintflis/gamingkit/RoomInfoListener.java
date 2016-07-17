package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 6/6/2016.
 */
public abstract class RoomInfoListener {

    private String roomId;
    private String classTag;

    public RoomInfoListener(String roomId, String classTag) {
        this.roomId = roomId;
        this.classTag = classTag;
    }

    public String getClassTag() {
        return classTag;
    }

    public String getRoomId() {
        return roomId;
    }

    public abstract void onRoomInfoRetrievedSuccess(String[] inRoomUserIds);

    public abstract void onRoomInfoFailed();

}
