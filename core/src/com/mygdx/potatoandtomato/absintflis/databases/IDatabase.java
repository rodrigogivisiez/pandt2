package com.mygdx.potatoandtomato.absintflis.databases;

import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public interface IDatabase {

     void saveLog(String msg);

     void authenticateUserByToken(String token, DatabaseListener<Profile> listener);

     void getProfileByGameNameLower(String gameName, DatabaseListener<Profile> listener);

     void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener);

     void getProfileByUserId(String userId, DatabaseListener<Profile> listener);

     void getUsernameByUserId(String userId, DatabaseListener<String> listener);

     void getUsernamesByUserIds(ArrayList<String> userIds, DatabaseListener<HashMap<String, String>> listener);

     void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener);

     void monitorUserCoinsCount(String userId, DatabaseListener<Integer> listener);

     void deductUserCoins(String userId, int finalCoins, DatabaseListener listener);

     void updateProfile(Profile profile, DatabaseListener listener);

     void getAllGames(DatabaseListener<ArrayList<Game>> listener);


     ///////////////all about rooms/////////////////////
     void updateRoomPlayingAndOpenState(Room room, Boolean isPlaying, Boolean isOpen, @Nullable DatabaseListener<String> listener);

     void saveRoom(Room room, boolean notify, @Nullable DatabaseListener<String> listener);    //except slot index

     void setOnDisconnectCloseRoom(Room room);

     void setInvitedUsers(ArrayList<Profile> invitedUsers, Room room, DatabaseListener listener);

     void addUserToRoom(Room room, Profile user, int slotIndex, DatabaseListener<String> listener);

     void removeUserFromRoom(Room room, Profile user, DatabaseListener listener);

     void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener);

     void setRoomUserIsReady(Room room, String userId, boolean isReady, DatabaseListener listener);

     void setRoomUserSlotIndex(Room room, String userId, int slotIndex, DatabaseListener listener);

     void setRoomState(final Room room, int roundCounter, boolean open, boolean playing, DatabaseListener listener);

     void getRoomById(String id, DatabaseListener<Room> listener);

     void monitorAllRooms(ArrayList<Room> rooms, String classTag, SpecialDatabaseListener<ArrayList<Room>, Room> listener);

     String notifyRoomChanged(Room room);

     ////////////////////////////////////

     void unauth();

     void offline();

     void online();

     void clearListenersByTag(String tag);

     void clearAllListeners();

     void clearAllOnDisconnectListenerModel();

     void savePlayedHistory(Profile profile, Room room, DatabaseListener<String> listener);

     void getPlayedHistories(Profile profile, DatabaseListener<ArrayList<GameHistory>> listener);
   
     void getPendingInvitationsCount(Profile profile, DatabaseListener<Integer> listener);

     void getGameByAbbr(String abbr, DatabaseListener<Game> listener);

     Object getGameBelongDatabase(String abbr);

     void getTeamStreak(Game game, ArrayList<String> userIds, DatabaseListener<Streak> listener);

     void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener);

     void getTeamHighestLeaderBoardRecordAndStreak(Game game, ArrayList<String> teamUserIds, DatabaseListener<LeaderboardRecord> listener);

     void getUserHighestLeaderBoardRecordAndStreak(Game game, String userId, DatabaseListener<LeaderboardRecord> listener);

     void getLeaderBoardRecordAndStreakById(Game game, String leaderboardId, DatabaseListener<LeaderboardRecord> listener);

     void deleteLeaderBoard(Game game, DatabaseListener listener);

     void checkScoreUpdated(Room room, DatabaseListener<Boolean> listener);
}
