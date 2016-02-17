package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.helpers.serializings.IntProfileMapDeserializer;
import com.potatoandtomato.common.Player;
import com.potatoandtomato.common.Team;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shaded.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
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
    String roomId;  //this is APPWARP ROOMID
    Profile host;
    String id;  //this is database ID

    ArrayList<Profile> invitedUsers;
    ArrayList<String> originalRoomUserIds;
    ArrayList<Team> teams;

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

    public ArrayList<String> getOriginalRoomUserIds() {
        if(originalRoomUserIds == null){
            originalRoomUserIds = new ArrayList();
        }
        return originalRoomUserIds;
    }

    public void setOriginalRoomUserIds(ArrayList<String> originalRoomUserIds) {
        this.originalRoomUserIds = originalRoomUserIds;
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

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    @JsonIgnore
    public void storeRoomUsersToOriginalRoomUserIds(){
        ArrayList<String> result = new ArrayList();
        for(RoomUser roomUser : getRoomUsers().values()){
            result.add(roomUser.getProfile().getUserId());
        }
        setOriginalRoomUserIds(result);
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
        for(RoomUser user : this.getRoomUsers().values()){
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

        if(roomUsers == null) roomUsers = new HashMap();

        if(getSlotIndexByUserId(user) != -1) return;

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
    public void addRoomUser(Profile user, int index, boolean isReady){
        RoomUser r = new RoomUser();
        r.setProfile(user);
        r.setSlotIndex(index);
        r.setReady(isReady);
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
    public void changeSlotIndex(int toIndex, Profile user){
        if(getRoomUserBySlotIndex(toIndex) == null){
            if(getRoomUserByUserId(user.getUserId()) != null){
                getRoomUsers().get(user.getUserId()).setSlotIndex(toIndex);
            }
        }
    }

    @JsonIgnore
    public boolean changeTeam(int toTeam, Profile user){
        int startIndex = toTeam * Integer.valueOf(this.getGame().getTeamMaxPlayers());
        boolean changed = false;
        int userSlotIndex = getSlotIndexByUserId(user);

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
        return getRoomUsersDifference(this.getRoomUsers().values(), newRoom.getRoomUsers().values());
    }

    @JsonIgnore
    public ArrayList<RoomUser>  getJustJoinedUsers(Room newRoom){
        if(newRoom == null) return new ArrayList<RoomUser>();
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
    public void convertRoomUsersToTeams() {
        ArrayList<Team> teams = new ArrayList();
        for (int i = 0; i < Integer.valueOf(this.getGame().getTeamCount()); i++) {
            teams.add(new Team());
        }
        for (RoomUser user : this.getRoomUsers().values()) {
            int index = convertSlotIndexToTeamNumber(user.getSlotIndex());
            boolean isHost = false;
            if(user.getProfile().equals(this.getHost())) isHost = true;
            teams.get(index).addPlayer(new Player(user.getProfile().getDisplayName(15), user.getProfile().getUserId(), isHost, true));
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
        if(getRoomUsers().size() > 0 && isPlaying() && !isOpen() &&
                getRoundCounter() == myProfile.getUserPlayingState().getRoundCounter() &&
                getId().equals(myProfile.getUserPlayingState().getRoomId()) &&
                !myProfile.getUserPlayingState().getAbandon()){
            if(getRoomUsers().size() == 1 && getRoomUserByUserId(myProfile.getUserId()) != null){
                return false;
            }
            return true;
        }
        return false;
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
