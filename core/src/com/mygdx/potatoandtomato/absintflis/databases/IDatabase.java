package com.mygdx.potatoandtomato.absintflis.databases;

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

    void getProfileByUserId(String userId, DatabaseListener<Profile> listener);

    void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener);

    void updateProfile(Profile profile);

    void createUserByUserId(String userId, DatabaseListener<Profile> listener);

    void getAllGames(DatabaseListener<ArrayList<Game>> listener);

    void saveRoom(Room room, DatabaseListener<String> listener);    //except slot index

    void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener);

    void monitorRoomById(String id, DatabaseListener<Room> listener);

    void getRoomById(String id, DatabaseListener<Room> listener);

    void monitorAllRooms(ArrayList<Room> rooms, SpecialDatabaseListener<ArrayList<Room>, Room> listener);

    void notifyRoomChanged(Room room);

    void removeUserFromRoomOnDisconnect(Room room, Profile user, DatabaseListener<String> listener);

    void offline();

    void online();



}
