package com.mygdx.potatoandtomato.models;

import com.firebase.client.ServerValue;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shaded.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 19/1/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomNotification {

    public HashMap<String, String> roomInfo;

    public RoomNotification() {
    }

    public RoomNotification(String roomId) {
        this.roomInfo = new HashMap<String, String>();
        roomInfo.put("roomId", roomId);
    }

    @JsonIgnore
    public String getRoomId(){
        if(roomInfo.containsKey("roomId")){
            return roomInfo.get("roomId");
        }
        else{
            return null;
        }
    }

    public HashMap<String, String> getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(HashMap<String, String> roomInfo) {
        this.roomInfo = roomInfo;
    }

    @JsonProperty("timestamp")
    public Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    @JsonIgnore
    public void setTimestamp(Map<String, String> timestamp) {

    }
}
