package com.mygdx.potatoandtomato.absintflis.databases;

import com.badlogic.gdx.utils.Array;
import com.firebase.client.Query;
import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.models.GameHistory;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public interface IDatabase {
    
     void getTestTableCount(DatabaseListener<Integer> listener);

     void loginAnonymous(DatabaseListener<Profile> listener);

     void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener);

     void getProfileByUserId(String userId, DatabaseListener<Profile> listener);

     void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener);

     void updateProfile(Profile profile, DatabaseListener listener);

     void createUserByUserId(String userId, DatabaseListener<Profile> listener);

     void getAllGames(DatabaseListener<ArrayList<Game>> listener);

     void saveRoom(Room room, @Nullable DatabaseListener<String> listener);    //except slot index

     void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener);

     void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener);

     void getRoomById(String id, DatabaseListener<Room> listener);

     void monitorAllRooms(ArrayList<Room> rooms, String classTag, SpecialDatabaseListener<ArrayList<Room>, Room> listener);

     String notifyRoomChanged(Room room);

     void removeUserFromRoomOnDisconnect(Room room, Profile user, DatabaseListener<String> listener);

     void offline();

     void online();

     void clearListenersByClassTag(String classTag);

     void savePlayedHistory(Profile profile, Room room, DatabaseListener<String> listener);

     void getPlayedHistories(Profile profile, DatabaseListener<ArrayList<GameHistory>> listener);
   
     void getPendingInvitationsCount(Profile profile, DatabaseListener<Integer> listener);

     void onDcSetGameStateDisconnected(Profile profile, DatabaseListener listener);

     void getGameByAbbr(String abbr, DatabaseListener<Game> listener);

     Object getGameBelongDatabase(String abbr);

}
