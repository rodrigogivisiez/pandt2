package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.helpers.serializings.IntProfileMapDeserializer;
import com.potatoandtomato.common.Player;
import com.potatoandtomato.common.Team;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.Collection;
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

    ArrayList<Profile> invitedUsers;

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
        if(roomUsers == null){
            roomUsers = new HashMap();
        }
        return roomUsers;
    }

    public void setRoomUsers(HashMap<String, RoomUser> roomUsers) {
        this.roomUsers = roomUsers;
    }

    public ArrayList<Profile> getInvitedUsers() {
        if(invitedUsers == null){
            invitedUsers = new ArrayList();
        }
        return invitedUsers;
    }

    public void setInvitedUsers(ArrayList<Profile> invitedUsers) {
        this.invitedUsers = invitedUsers;
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
    public int getRoomUsersCount(){
        if(roomUsers == null) return 0;
        else return roomUsers.size();
    }

    @JsonIgnore
    public Profile getInvitedUserByUserId(String userId){
        for(Profile user : this.getInvitedUsers()){
            if(user.getUserId().equals(userId)){
                return user;
            }
        }
        return null;
    }

    @JsonIgnore
    public boolean addInvitedUser(Profile user){
        if(getInvitedUserByUserId(user.getUserId()) == null){
            getInvitedUsers().add(user);
            return true;
        }
        return false;
    }

    @JsonIgnore
    public void addRoomUser(Profile user){

        if(roomUsers == null) roomUsers = new HashMap();

        if(getSlotIndexByUserId(user) != -1) return;

        for(int i = 0; i < Integer.valueOf(game.getMaxPlayers()); i++){
            if(getRoomUserBySlotIndex(i) == null){
                RoomUser r = new RoomUser();
                r.setProfile(user);
                r.setSlotIndex(i);
                roomUsers.put(user.getUserId(), r);
                break;
            }
        }
    }

    @JsonIgnore
    public void addRoomUser(Profile user, int index){
        RoomUser r = new RoomUser();
        r.setProfile(user);
        r.setSlotIndex(index);
        roomUsers.put(user.getUserId(), r);
    }

    @JsonIgnore
    public RoomUser getRoomUserBySlotIndex(int slotIndex){
        for(RoomUser roomUser : this.getRoomUsers().values()){
            if(roomUser.getSlotIndex() == slotIndex){
                return roomUser;
            }
        }
        return null;
    }

    @JsonIgnore
    public Profile getProfileByUserId(String userId){
        for(RoomUser roomUser : this.getRoomUsers().values()){
            if(roomUser.getProfile().getUserId().equals(userId)){
                return roomUser.getProfile();
            }
        }
        return null;
    }

    @JsonIgnore
    public int getSlotIndexByUserId(Profile user){
        for(RoomUser roomUser : this.getRoomUsers().values()){
            if(roomUser.getProfile().equals(user)){
                return roomUser.getSlotIndex();
            }
        }
        return -1;
    }

    @JsonIgnore
    public boolean changeTeam(int toTeam, Profile user){
        int startIndex = toTeam * Integer.valueOf(this.getGame().getTeamMaxPlayers());
        boolean changed = false;
        int userSlotIndex = getSlotIndexByUserId(user);

        if(userSlotIndex != -1 && userSlotIndex >= startIndex && userSlotIndex + 1 < ((toTeam + 1) * Integer.valueOf(this.getGame().getTeamMaxPlayers()))){
            if(getRoomUserBySlotIndex(userSlotIndex+1) == null){
                changed = true;
                addRoomUser(user, userSlotIndex + 1);
            }
        }

        if(!changed){
            for(int i = 0; i< Integer.valueOf(this.getGame().getTeamMaxPlayers()); i++){
                if(getRoomUserBySlotIndex(startIndex) == null){
                    changed = true;
                    addRoomUser(user, startIndex);
                    break;
                }
                startIndex++;
            }
        }

        return changed;
    }

    @JsonIgnore
    public boolean checkAllTeamHasMinPlayers(){
        for(int i = 0; i < Integer.valueOf(this.getGame().getTeamCount()); i++){
            int playerCount = 0;

            int startIndex = i * Integer.valueOf(this.getGame().getTeamMaxPlayers());
            int endIndex = startIndex + Integer.valueOf(this.getGame().getTeamMaxPlayers());

            for(int q = startIndex; q < endIndex; q++){
                if(getRoomUserBySlotIndex(q) != null){
                    playerCount++;
                }
            }

            if(playerCount < Integer.valueOf(this.getGame().getTeamMinPlayers())){
                return false;
            }
        }

        return true;
    }

    @JsonIgnore
    public ArrayList<RoomUser> getJustLeftUsers(Room newRoom){
        return getRoomUsersDifference(this.getRoomUsers().values(), newRoom.getRoomUsers().values());
    }

    @JsonIgnore
    public ArrayList<RoomUser>  getJustJoinedUsers(Room newRoom){
        return getRoomUsersDifference(newRoom.getRoomUsers().values(), this.getRoomUsers().values());
    }

    @JsonIgnore
    public ArrayList<RoomUser> getRoomUsersDifference(Collection<RoomUser> roomUsers1, Collection<RoomUser> roomUsers2){
        ArrayList<RoomUser> results = new ArrayList();
        for(RoomUser roomUser : roomUsers1){
            boolean found = false;
            for(RoomUser roomUser2 : roomUsers2){
                if(roomUser.getProfile().equals(roomUser2.getProfile())){
                    found = true;
                    break;
                }
            }
            if(!found) results.add(roomUser);
        }
        return results;
    }

    @JsonIgnore
    private int convertSlotIndexToTeamNumber(int slotIndex){
        for(int i = 0; i < Integer.valueOf(this.getGame().getTeamCount()); i++) {
            int startIndex = i * Integer.valueOf(this.getGame().getTeamMaxPlayers());
            int endIndex = startIndex + Integer.valueOf(this.getGame().getTeamMaxPlayers());
            if(slotIndex >= startIndex && slotIndex < endIndex){
                return i;
            }
        }
        return -1;
    }

    @JsonIgnore
    public ArrayList<Team> convertRoomUsersToTeams() {
        ArrayList<Team> teams = new ArrayList();
        for (int i = 0; i < Integer.valueOf(this.getGame().getTeamCount()); i++) {
            teams.add(new Team());
        }
        for (RoomUser user : this.getRoomUsers().values()) {
            int index = convertSlotIndexToTeamNumber(user.getSlotIndex());
            teams.get(index).addPlayer(new Player(user.getProfile().getDisplayName(), user.getProfile().getUserId(),
                    user.getProfile().getMascotEnum() == MascotEnum.POTATO ? 0 : 1));
        }
        return teams;
    }



}
