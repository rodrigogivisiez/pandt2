package com.mygdx.potatoandtomato.android;

import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.GameHistory;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 8/1/2016.
 */
public class MockDB implements IDatabase {
    @Override
    public void saveLog(String msg) {

    }

    @Override
    public void authenticateUserByToken(String token, DatabaseListener<Profile> listener) {

    }


    @Override
    public void getProfileByGameNameLower(String gameName, DatabaseListener<Profile> listener) {

    }

    @Override
    public void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener) {

    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {

    }

    @Override
    public void getUsernameByUserId(String userId, DatabaseListener<String> listener) {

    }

    @Override
    public void getUsernamesByUserIds(ArrayList<String> userIds, DatabaseListener<HashMap<String, String>> listener) {

    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {

    }

    @Override
    public void updateProfile(Profile profile, DatabaseListener listener) {

    }

    @Override
    public void getAllGamesSimple(DatabaseListener<ArrayList<Game>> listener) {

    }

    @Override
    public void updateRoomPlayingAndOpenState(Room room, Boolean isPlaying, Boolean isOpen, @Nullable DatabaseListener<String> listener) {

    }

    @Override
    public void saveRoom(Room room, boolean notify, @Nullable DatabaseListener<String> listener) {

    }

    @Override
    public void setOnDisconnectCloseRoom(Room room) {

    }

    @Override
    public void setInvitedUsers(ArrayList<Profile> invitedUsers, Room room, DatabaseListener listener) {

    }

    @Override
    public void addUserToRoom(Room room, Profile user, int slotIndex, DatabaseListener<String> listener) {

    }

    @Override
    public void removeUserFromRoom(Room room, Profile user, DatabaseListener listener) {

    }


    @Override
    public void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener) {

    }

    @Override
    public void setRoomUserIsReady(Room room, String userId, boolean isReady, DatabaseListener listener) {

    }

    @Override
    public void setRoomUserSlotIndex(Room room, String userId, int slotIndex, DatabaseListener listener) {

    }

    @Override
    public void setRoomState(Room room, int roundCounter, boolean open, boolean playing, DatabaseListener listener) {

    }

    @Override
    public void getRoomById(String id, DatabaseListener<Room> listener) {

    }

    @Override
    public void monitorAllRooms(ArrayList<Room> rooms, String classTag, SpecialDatabaseListener<ArrayList<Room>, Room> listener) {

    }

    @Override
    public String notifyRoomChanged(Room room) {
        return null;
    }

    @Override
    public void unauth() {

    }


    @Override
    public void offline() {

    }

    @Override
    public void online() {

    }

    @Override
    public void clearListenersByTag(String tag) {

    }

    @Override
    public void clearAllListeners() {

    }

    @Override
    public void clearAllOnDisconnectListenerModel() {

    }

    @Override
    public void savePlayedHistory(Profile profile, Room room, DatabaseListener<String> listener) {

    }

    @Override
    public void getPlayedHistories(Profile profile, DatabaseListener<ArrayList<GameHistory>> listener) {

    }

    @Override
    public void getPendingInvitationsCount(Profile profile, DatabaseListener<Integer> listener) {

    }


    @Override
    public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {

    }

    @Override
    public Object getGameBelongDatabase(String abbr) {
        return null;
    }

    @Override
    public void getTeamStreak(Game game, ArrayList<String> userIds, DatabaseListener<Streak> listener) {

    }


    @Override
    public void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener) {

    }

    @Override
    public void getTeamHighestLeaderBoardRecordAndStreak(Game game, ArrayList<String> teamUserIds, DatabaseListener<LeaderboardRecord> listener) {

    }

    @Override
    public void getUserHighestLeaderBoardRecordAndStreak(Game game, String userId, DatabaseListener<LeaderboardRecord> listener) {

    }

    @Override
    public void getLeaderBoardRecordAndStreakById(Game game, String leaderboardId, DatabaseListener<LeaderboardRecord> listener) {

    }


    @Override
    public void deleteLeaderBoard(Game game, DatabaseListener listener) {

    }

    @Override
    public void checkScoreUpdated(Room room, DatabaseListener<Boolean> listener) {

    }

}
