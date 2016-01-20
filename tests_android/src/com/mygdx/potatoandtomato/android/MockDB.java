package com.mygdx.potatoandtomato.android;

import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.GameHistory;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 8/1/2016.
 */
public class MockDB implements IDatabase {
    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {

    }

    @Override
    public void loginAnonymous(DatabaseListener<Profile> listener) {

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
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {

    }

    @Override
    public void updateProfile(Profile profile, DatabaseListener listener) {

    }

    @Override
    public void createUserByUserId(String userId, DatabaseListener<Profile> listener) {

    }

    @Override
    public void getAllGames(DatabaseListener<ArrayList<Game>> listener) {

    }

    @Override
    public void saveRoom(Room room, boolean notify, @Nullable DatabaseListener<String> listener) {

    }

    @Override
    public void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener) {

    }

    @Override
    public void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener) {

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
    public void removeUserFromRoomOnDisconnect(Room room, Profile user, DatabaseListener<String> listener) {

    }

    @Override
    public void offline() {

    }

    @Override
    public void online() {

    }

    @Override
    public void clearListenersByClassTag(String classTag) {

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
    public void onDcSetGameStateDisconnected(Profile profile, DatabaseListener listener) {

    }

    @Override
    public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {

    }

    @Override
    public Object getGameBelongDatabase(String abbr) {
        return null;
    }
}
