package abstracts;

import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.models.GameHistory;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import helpers.MockModel;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class MockDB implements IDatabase {

    @Override
    public void getTestTableCount(DatabaseListener<Integer> listener) {
        listener.onCallback(1, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void loginAnonymous(DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId("12345");
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener) {

    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setFacebookUserId(facebookUserId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void updateProfile(Profile profile, DatabaseListener listener) {

    }

    @Override
    public void createUserByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getAllGames(DatabaseListener<ArrayList<Game>> listener) {
        ArrayList<Game> gameList = new ArrayList();
        Game game = new Game();
        game.setVersion("1");
        game.setName("Test");
        game.setAssetUrl("");
        game.setDescription("d");
        game.setMaxPlayers("2");
        game.setMinPlayers("2");
        game.setAbbr("abb");
        game.setScreenShots(null);
        game.setGameUrl("");
        game.setIconUrl("http://www.potato-and-tomato.com/covered_chess/icon.png");
        gameList.add(game);
        listener.onCallback(gameList, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void saveRoom(Room room, @Nullable DatabaseListener<String> listener) {
        if(listener!= null) listener.onCallback(null, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener) {

    }

    @Override
    public void getRoomById(String id, DatabaseListener<Room> listener) {

    }

    @Override
    public void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener) {

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
        ArrayList<GameHistory> results = new ArrayList();
        results.add(MockModel.mockGameHistory());
        listener.onCallback(results, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void getPendingInvitationsCount(Profile profile, DatabaseListener<Integer> listener) {
        listener.onCallback(1, DatabaseListener.Status.SUCCESS);
    }

    @Override
    public void onDcSetGameStateDisconnected(Profile profile,  DatabaseListener listener) {

    }


}
