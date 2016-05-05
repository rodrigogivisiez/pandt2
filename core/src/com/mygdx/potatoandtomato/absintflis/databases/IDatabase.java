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

     void getTestTableCount(DatabaseListener<Integer> listener);

     void loginAnonymous(DatabaseListener<Profile> listener);

     void getProfileByGameNameLower(String gameName, DatabaseListener<Profile> listener);

     void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener);

     void getProfileByUserId(String userId, DatabaseListener<Profile> listener);

     void getUsernameByUserId(String userId, DatabaseListener<String> listener);

     void getUsernamesByUserIds(ArrayList<String> userIds, DatabaseListener<HashMap<String, String>> listener);

     void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener);

     void updateProfile(Profile profile, DatabaseListener listener);

     void createUserByUserId(String userId, DatabaseListener<Profile> listener);

     void getAllGames(DatabaseListener<ArrayList<Game>> listener);

     void updateRoomPlayingState(Room room, boolean isPlaying, @Nullable DatabaseListener<String> listener);

     void saveRoom(Room room, boolean notify, @Nullable DatabaseListener<String> listener);    //except slot index

     void addUserToRoom(Room room, Profile user, DatabaseListener<String> listener);

     void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener);

     void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener);

     void getRoomById(String id, DatabaseListener<Room> listener);

     void monitorAllRooms(ArrayList<Room> rooms, String classTag, SpecialDatabaseListener<ArrayList<Room>, Room> listener);

     String notifyRoomChanged(Room room);

     void removeUserFromRoomOnDisconnect(String roomId, Profile user, DatabaseListener<String> listener);

     void offline();

     void online();

     void clearListenersByClassTag(String classTag);

     void clearAllListeners();

     void savePlayedHistory(Profile profile, Room room, DatabaseListener<String> listener);

     void getPlayedHistories(Profile profile, DatabaseListener<ArrayList<GameHistory>> listener);
   
     void getPendingInvitationsCount(Profile profile, DatabaseListener<Integer> listener);

     void onDcSetGameStateDisconnected(Profile profile, DatabaseListener listener);

     void getGameByAbbr(String abbr, DatabaseListener<Game> listener);

     Object getGameBelongDatabase(String abbr);

     void getUserStreak(Game game, String userId, DatabaseListener<Streak> listener);

     void getLeaderBoardAndStreak(Game game, int expectedCount, DatabaseListener<ArrayList<LeaderboardRecord>> listener);

     void saveLeaderBoardRecord(Room room, LeaderboardRecord record, DatabaseListener listener);

     void getTeamHighestLeaderBoardRecordAndStreak(Game game, ArrayList<String> teamUserIds, DatabaseListener<LeaderboardRecord> listener);

     void getUserHighestLeaderBoardRecordAndStreak(Game game, String userId, DatabaseListener<LeaderboardRecord> listener);

     void getLeaderBoardRecordById(Game game, String leaderboardId, DatabaseListener<LeaderboardRecord> listener);

     void deleteLeaderBoard(Game game, DatabaseListener listener);

     void streakRevive(ArrayList<String> userIds, Room room, DatabaseListener listener);

     void isStreakRevived(ArrayList<String> userIds, Room room, DatabaseListener<Boolean> listener);

}
