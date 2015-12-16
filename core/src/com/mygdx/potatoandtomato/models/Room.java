package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.helpers.serializings.IntProfileMapDeserializer;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class Room {

    Game game;
    boolean open;
    int roundCounter;
    boolean playing;
    String roomId;
    Profile host;
    String id;

    @JsonDeserialize(using = IntProfileMapDeserializer.class)
    HashMap<String, RoomUser> roomUsers;


    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getRoundCounter() {
        return roundCounter;
    }

    public void setRoundCounter(int roundCounter) {
        this.roundCounter = roundCounter;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public HashMap<String, RoomUser> getRoomUsers() {
        return roomUsers;
    }

    public void setRoomUsers(HashMap<String, RoomUser> roomUsers) {
        this.roomUsers = roomUsers;
    }

    public Profile getHost() {
        return host;
    }

    public void setHost(Profile host) {
        this.host = host;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @JsonIgnore
    public void addUser(Profile user){
        for(int i = 0; i < Integer.valueOf(game.getMaxPlayers()); i++){
            boolean occupied = false;
            for (RoomUser roomUser : roomUsers.values()) {
                if(roomUser.getSlotIndex() == i){
                    occupied = true;
                    break;
                }
            }
            if(!occupied){
                RoomUser r = new RoomUser();
                r.setProfile(user);
                r.setSlotIndex(i);
                roomUsers.put(user.getUserId(), r);
                break;
            }
        }
    }


}
