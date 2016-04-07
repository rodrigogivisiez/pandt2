package com.mygdx.potatoandtomato.models;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.potatoandtomato.helpers.serializings.IntProfileMapDeserializer;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
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
    String warpRoomId;  //this is APPWARP ROOMID
    Profile host;
    String id;  //this is database ID

    ArrayList<Profile> invitedUsers;
    ArrayList<RoomUser> originalRoomUsers;
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
        for(RoomUser roomUser : getRoomUsers().values()){
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
        for(RoomUser roomUser : this.getRoomUsers().values()){
            if(roomUser.getProfile().getUserId().equals(userId)){
                return roomUser.getProfile();
            }
        }
        return null;
    }

    @JsonIgnore
    public int getSlotIndexByUserId(String userId){
        for(RoomUser roomUser : this.getRoomUsers().values()){
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
                getRoomUsers().get(user.getUserId()).setSlotIndex(toIndex);
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
            teams.get(index).addPlayer(new Player(user.getProfile().getDisplayName(15), user.getProfile().getUserId(), isHost, true,
                                                getUserColorByUserId(user.getProfile().getUserId())));
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
    public Color getUserColorByUserId(String userId){
        int slotIndex = getSlotIndexByUserId(userId);
        String hex = "ffffff";

        switch (slotIndex){
            case 0:
                hex = "FF420E";
                break;
            case 1:
                hex = "89DA59";
                break;
            case 2:
                hex = "E6D72A";
                break;
            case 3:
                hex = "FAAF08";
                break;
            case 4:
                hex = "BA5536";
                break;
            case 5:
                hex = "004445";
                break;
            case 6:
                hex = "336B87";
                break;
            case 7:
                hex = "808D9E";
                break;
            case 8:
                hex = "6FB98F";
                break;
            case 9:
                hex = "90AFC5";
                break;
            case 10:
                hex = "F18D9E";
                break;
            case 11:
                hex = "F98866";
                break;
            case 12:
                hex = "86AC41";
                break;
            case 13:
                hex = "F1F1F2";
                break;
            case 14:
                hex = "BCBABE";
                break;
            case 15:
                hex = "A43820";
                break;
            case 16:
                hex = "1995AD";
                break;
            case 17:
                hex = "9A9EAB";
                break;
            case 18:
                hex = "DFE166";
                break;
            case 19:
                hex = "F0810F";
                break;
            case 20:
                hex = "E6DF44";
                break;
            case 21:
                hex = "063852";
                break;
            case 22:
                hex = "D9B44A";
                break;
            case 23:
                hex = "8EBA43";
                break;
            case 24:
                hex = "F9DC24";
                break;
            case 25:
                hex = "F52549";
                break;
            case 26:
                hex = "FFD64D";
                break;
            case 27:
                hex = "B38867";
                break;
            case 28:
                hex = "626D71";
                break;
            case 29:
                hex = "31A9B8";
                break;
            case 30:
                hex = "258039";
                break;
            case 31:
                hex = "752A07";
                break;
            case 32:
                hex = "FBCB7B";
                break;
        }

        return Color.valueOf(hex);

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
