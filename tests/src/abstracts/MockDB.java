package abstracts;

import com.firebase.client.annotations.Nullable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.enums.RoomUserState;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class MockDB implements IDatabase {

    @Override
    public void saveLog(String msg) {

    }

    @Override
    public void authenticateUserByToken(String token, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId("999");
        p.setToken("12345");
        listener.onCallback(p, Status.SUCCESS);
    }


    @Override
    public void getProfileByGameNameLower(String gameName, DatabaseListener<Profile> listener) {

    }

    @Override
    public void monitorProfileByUserId(String userId, String classTag, DatabaseListener<Profile> listener) {

    }

    @Override
    public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setUserId(userId);
        listener.onCallback(p, Status.SUCCESS);
    }

    @Override
    public void getUsernameByUserId(String userId, DatabaseListener<String> listener) {

    }

    @Override
    public void getUsernamesByUserIds(ArrayList<String> userIds, DatabaseListener<HashMap<String, String>> listener) {

    }

    @Override
    public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {
        Profile p = new Profile();
        p.setFacebookUserId(facebookUserId);
        listener.onCallback(p, Status.SUCCESS);
    }

    @Override
    public void monitorUserCoinsCount(String userId, DatabaseListener<Integer> listener) {

    }

    @Override
    public void signCoinDecreaseAgreement(String userId, String transactionId, DatabaseListener listener) {

    }

    @Override
    public void updateProfile(Profile profile, DatabaseListener listener) {

    }

    @Override
    public void getAllGamesSimple(DatabaseListener<ArrayList<Game>> listener) {
        ArrayList<Game> gameList = new ArrayList();
        Game game = new Game();
        game.setVersion("1");
        game.setName("Test");
        game.setDescription("d");
        game.setMaxPlayers("2");
        game.setMinPlayers("2");
        game.setAbbr("abb");
        game.setIconUrl("http://www.potato-and-tomato.com/covered_chess/icon.png");
        gameList.add(game);
        listener.onCallback(gameList, Status.SUCCESS);
    }

    @Override
    public void getAllProducts(DatabaseListener<ArrayList<CoinProduct>> listener) {

    }

    @Override
    public void updateRoomPlayingAndOpenState(Room room, Boolean isPlaying, Boolean isOpen, @Nullable DatabaseListener<String> listener) {

    }


    @Override
    public void saveRoom(Room room, boolean notify, @Nullable DatabaseListener<String> listener) {
        if(listener!= null) listener.onCallback(null, Status.SUCCESS);
    }

    @Override
    public void setOnDisconnectCloseRoom(Room room) {

    }

    @Override
    public void setInvitedUsers(ArrayList<String> invitedUserIds, Room room, DatabaseListener listener) {

    }


    @Override
    public void addUserToRoom(Room room, Profile user, int slotIndex, RoomUserState roomUserState, DatabaseListener<String> listener) {

    }


    @Override
    public void removeUserFromRoom(Room room, Profile user, DatabaseListener listener) {

    }

    @Override
    public void monitorRoomById(String id, String classTag, DatabaseListener<Room> listener) {

    }

    @Override
    public void setRoomUserState(Room room, String userId, RoomUserState roomUserState, DatabaseListener listener) {

    }



    @Override
    public void setRoomUserSlotIndex(Room room, String userId, int slotIndex, DatabaseListener listener) {

    }



    @Override
    public void getRoomById(String id, DatabaseListener<Room> listener) {

    }


    @Override
    public void monitorAllRooms(ArrayList<Room> rooms, String classTag, SpecialDatabaseListener<ArrayList<Room>, Room> listener) {

    }

    @Override
    public void monitorRoomInvitations(String roomId, String classTag, DatabaseListener listener) {

    }

    @Override
    public void checkRoomInvitationResponseExist(String roomId, String userId, DatabaseListener<Boolean> listener) {

    }

    @Override
    public void getPendingInvitationRoomIds(Profile profile, DatabaseListener<ArrayList<String>> listener) {

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
        ArrayList<GameHistory> results = new ArrayList();
        results.add(MockModel.mockGameHistory());
        listener.onCallback(results, Status.SUCCESS);
    }


    @Override
    public void getGameByAbbr(String abbr, DatabaseListener<Game> listener) {

    }

    @Override
    public void getGameSimpleByAbbr(String abbr, DatabaseListener<Game> listener) {

    }

    @Override
    public Object getGameBelongDatabase(String abbr) {
        return null;
    }

    @Override
    public void getTeamStreak(Game game, ArrayList<String> userIds, DatabaseListener<Streak> listener) {

    }

    @Override
    public void resetUserStreak(Game game, String userId, DatabaseListener listener) {

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
