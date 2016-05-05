package com.mygdx.potatoandtomato.models;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.potatoandtomato.comparators.RoomUserSlotIndexComparator;
import com.mygdx.potatoandtomato.helpers.serializings.IntProfileMapDeserializer;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.ColorUtils;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shaded.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class Room {

    Game game;
    boolean open;
    int roundCounter;
    boolean playing;
    String warpRoomId;  //this is APPWARP ROOMID
    Profile host;
    String id;  //this is database ID

    ArrayList<Profile> invitedUsers;
    ArrayList<RoomUser> originalRoomUsers;
    ArrayList<Team> teams;

    @JsonDeserialize(using = IntProfileMapDeserializer.class)
    ConcurrentHashMap<String, RoomUser> roomUsers;



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

    public String getWarpRoomId() {
        return warpRoomId;
    }

    public void setWarpRoomId(String warpRoomId) {
        this.warpRoomId = warpRoomId;
    }

    public ArrayList<RoomUser> getOriginalRoomUsers() {
        if(originalRoomUsers == null){
            originalRoomUsers = new ArrayList();
        }
        return originalRoomUsers;
    }

    public void setOriginalRoomUsers(ArrayList<RoomUser> originalRoomUsers) {
        this.originalRoomUsers = originalRoomUsers;
    }


    @JsonIgnore
    public ArrayList<RoomUser> getRoomUsersSortBySlotIndex() {
        ConcurrentHashMap<String, RoomUser> roomUserHashMap = getRoomUsersMap();
        ArrayList<RoomUser> results = new ArrayList();

        for(RoomUser roomUser : roomUserHashMap.values()){
            results.add(roomUser);
        }

        Collections.sort(results, new RoomUserSlotIndexComparator());

        return results;
    }

    @JsonIgnore
    public ConcurrentHashMap<String, RoomUser> getRoomUsersMap() {
        if(roomUsers == null){
            roomUsers = new ConcurrentHashMap();
        }
        return roomUsers;
    }

    public void setRoomUsers(ConcurrentHashMap<String, RoomUser> roomUsers) {
        this.roomUsers = roomUsers;
    }

    public ConcurrentHashMap<String, RoomUser> getRoomUsers() {
        return roomUsers;
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

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    @JsonIgnore
    public Team getUserTeam(String userId){
        for(Team team : getTeams()){
            if(team.hasUser(userId)){
                return team;
            }
        }
        return null;
    }

    @JsonIgnore
    public void storeRoomUsersToOriginalRoomUserIds(){
        ArrayList<RoomUser> result = new ArrayList();
        for(RoomUser roomUser : getRoomUsersMap().values()){
            result.add(roomUser);
        }
        setOriginalRoomUsers(result);
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
    public RoomUser getRoomUserByUserId(String userId){
        for(RoomUser user : this.getRoomUsersMap().values()){
            if(user.getProfile().getUserId().equals(userId)){
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
    public void addRoomUser(Profile user, boolean isReady){
        if(roomUsers == null) roomUsers = new ConcurrentHashMap();

        if(getSlotIndexByUserId(user.getUserId()) != -1) return;

        for(int i = 0; i < Integer.valueOf(game.getMaxPlayers()); i++){
            if(getRoomUserBySlotIndex(i) == null){
                RoomUser r = new RoomUser();
                r.setProfile(user);
                r.setSlotIndex(i);
                r.setReady(isReady);
                roomUsers.put(user.getUserId(), r);
                break;
            }
        }
    }

    @JsonIgnore
    public void setRoomUsersIndexIfNoIndex(Room room){
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            int index = room.getRoomUserByUserId(roomUser.getProfile().getUserId()).getSlotIndex();
            if(index != -1){
                RoomUser thisRoomUser = this.getRoomUserByUserId(roomUser.getProfile().getUserId());
                if(thisRoomUser != null && thisRoomUser.getSlotIndex() == -1){
                    this.getRoomUserByUserId(roomUser.getProfile().getUserId()).setSlotIndex(index);
                }
            }
        }
    }

    @JsonIgnore
    public void addRoomUser(Profile user, int index, boolean isReady){
        RoomUser r = new RoomUser();
        r.setProfile(user);
        r.setSlotIndex(index);
        r.setReady(isReady);
        roomUsers.put(user.getUserId(), r);
    }

    @JsonIgnore
    public RoomUser getRoomUserBySlotIndex(int slotIndex){
        for(RoomUser roomUser : this.getRoomUsersMap().values()){
            if(roomUser.getSlotIndex() == slotIndex){
                return roomUser;
            }
        }
        return null;
    }

    @JsonIgnore
    public RoomUser getOriginalRoomUserByUserId(String userId){
        for(RoomUser roomUser : this.getOriginalRoomUsers()){
            if(roomUser.getProfile().getUserId().equals(userId)){
                return roomUser;
            }
        }
        return null;
    }

    @JsonIgnore
    public Profile getProfileByUserId(String userId){
        for(RoomUser roomUser : this.getRoomUsersMap().values()){
            if(roomUser.getProfile().getUserId().equals(userId)){
                return roomUser.getProfile();
            }
        }
        return null;
    }

    @JsonIgnore
    public int getSlotIndexByUserId(String userId){
        for(RoomUser roomUser : this.getRoomUsersMap().values()){
            if(roomUser.getProfile().getUserId().equals(userId)){
                return roomUser.getSlotIndex();
            }
        }
        return -1;
    }

    @JsonIgnore
    public void changeSlotIndex(int toIndex, Profile user){
        if(getRoomUserBySlotIndex(toIndex) == null){
            if(getRoomUserByUserId(user.getUserId()) != null){
                getRoomUsersMap().get(user.getUserId()).setSlotIndex(toIndex);
            }
        }
    }

    @JsonIgnore
    public boolean changeTeam(int toTeam, Profile user){
        int startIndex = toTeam * Integer.valueOf(this.getGame().getTeamMaxPlayers());
        boolean changed = false;
        int userSlotIndex = getSlotIndexByUserId(user.getUserId());

        if(userSlotIndex != -1 && userSlotIndex >= startIndex && userSlotIndex + 1 < ((toTeam + 1) * Integer.valueOf(this.getGame().getTeamMaxPlayers()))){
            if(getRoomUserBySlotIndex(userSlotIndex+1) == null){
                changed = true;
                addRoomUser(user, userSlotIndex + 1, true);
            }
        }

        if(!changed){
            for(int i = 0; i< Integer.valueOf(this.getGame().getTeamMaxPlayers()); i++){
                if(getRoomUserBySlotIndex(startIndex) == null){
                    changed = true;
                    addRoomUser(user, startIndex, true);
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
        if(newRoom == null) return new ArrayList<RoomUser>();
        return getRoomUsersDifference(this.getRoomUsersMap().values(), newRoom.getRoomUsersMap().values());
    }

    @JsonIgnore
    public ArrayList<RoomUser>  getJustJoinedUsers(Room newRoom){
        if(newRoom == null) return new ArrayList<RoomUser>();
        return getRoomUsersDifference(newRoom.getRoomUsersMap().values(), this.getRoomUsersMap().values());
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
    public void convertRoomUsersToTeams() {
        ArrayList<Team> teams = new ArrayList();
        for (int i = 0; i < Integer.valueOf(this.getGame().getTeamCount()); i++) {
            teams.add(new Team());
        }
        for (RoomUser user : this.getRoomUsersSortBySlotIndex()) {
            int teamNumber = convertSlotIndexToTeamNumber(user.getSlotIndex());
            if(teamNumber != -1){
                boolean isHost = false;
                if(user.getProfile().equals(this.getHost())) isHost = true;
                teams.get(teamNumber).addPlayer(new Player(user.getProfile().getDisplayName(15),
                                user.getProfile().getUserId(), isHost, true, user.getSlotIndex()));
            }
        }
        this.teams = teams;
    }

    @JsonIgnore
    public boolean checkAllFairTeam(){
        convertRoomUsersToTeams();
        int lastCount = 0;
        for(Team team : teams){
            if(team.getPlayers().size() != 0){
                if(lastCount == 0) lastCount = team.getPlayers().size();

                if(team.getPlayers().size() != lastCount && team.getPlayers().size() !=0){
                    return false;
                }
            }
        }
        return true;
    }


    @JsonIgnore
    public int getNotYetReadyCount(){
        int count = 0;
        for(RoomUser roomUser : this.roomUsers.values()){
            if(!roomUser.getReady()) count++;
        }
        return count;
    }

    @JsonIgnore
    public void setRoomUserReady(String userId, boolean isReady){
        for(RoomUser roomUser : this.roomUsers.values()){
            if(roomUser.getProfile().getUserId().equals(userId)){
                roomUser.setReady(isReady);
                break;
            }
        }
    }

    @JsonIgnore
    public boolean canContinue(Profile myProfile){
        if(getRoomUsersMap().size() > 0 && isPlaying() && !isOpen() &&
                getRoundCounter() == myProfile.getUserPlayingState().getRoundCounter() &&
                getId().equals(myProfile.getUserPlayingState().getRoomId()) &&
                !myProfile.getUserPlayingState().getAbandon()){
            if(getRoomUsersMap().size() == 1 && getRoomUserByUserId(myProfile.getUserId()) != null){
                return false;
            }
            return true;
        }
        return false;
    }

    @JsonIgnore
    public Color getUserColorByUserId(String userId){
        int slotIndex = getSlotIndexByUserId(userId);
        return ColorUtils.getUserColorByIndex(slotIndex);
    }

    @JsonIgnore
    public Room clone(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(this);
            ObjectMapper mapper2 = new ObjectMapper();
            return mapper2.readValue(json, Room.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
