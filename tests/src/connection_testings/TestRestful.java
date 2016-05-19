package connection_testings;

import abstracts.TestAbstract;
import com.firebase.client.*;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.UserIdSecretModel;
import com.mygdx.potatoandtomato.services.FirebaseDB;
import com.mygdx.potatoandtomato.services.RestfulApi;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.ArrayUtils;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 16/5/2016.
 */
public class TestRestful extends TestAbstract {

    private String _appTestUrl = "https://ptapptest.firebaseio.com/";
    private String _secret = "xU62Y6naxtpRUZZad429zIPu7f3rSVcVrjG4MOMp";
    Firebase firebase;

    @Override
    @Before
    public void setUp() throws Exception {
        Global.DEBUG = true;

        if(firebase == null){

            firebase = new Firebase(_appTestUrl);
            firebase.authWithCustomToken(_secret, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Threadings.oneTaskFinish();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {

                }
            });

            Threadings.waitTasks(1);
        }

    }

    @Test
    public void testCreateUser(){
        final UserIdSecretModel userIdSecretModel = createUser();

        assertFirebaseData("secret/users/" + userIdSecretModel.getUserId() + "/secret", userIdSecretModel.getSecret());
        assertFirebaseData("secret/users/" + userIdSecretModel.getUserId() + "/userId", userIdSecretModel.getUserId());
        assertFirebaseData("users/" + userIdSecretModel.getUserId() + "/userId", userIdSecretModel.getUserId());
        deleteUsersOrRoom(userIdSecretModel.getUserId());
    }

    @Test
    public void testCreateUserWithFbFailed(){
        RestfulApi restfulApi = new RestfulApi();
        FacebookProfile facebookProfile = new FacebookProfile("1", "2", "3");
        restfulApi.createNewUserWithFacebookProfile(facebookProfile, new RestfulApiListener < UserIdSecretModel > () {
            @Override
            public void onCallback (UserIdSecretModel obj, Status st){
                Assert.assertEquals(Status.FAILED, st);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
    }

    @Test
    public void testLoginUser(){

        RestfulApi restfulApi = new RestfulApi();
        restfulApi.loginUser("anyhow", "anyhowpw", new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(Status.FAILED, st);
                Threadings.oneTaskFinish();
            }
        });
        Threadings.waitTasks(1);

        final UserIdSecretModel createdUser = createUser();

        loginUser(createdUser.getUserId(), createdUser.getSecret());

        deleteUsersOrRoom(createdUser.getUserId());

    }

    @Test
    public void testSaveScoresFailed(){
        RestfulApi restfulApi = new RestfulApi();

        final Room room = MockModel.mockRoom("uniqueone");
        ArrayList<ScoreDetails> scoreDetailses = new ArrayList();
        scoreDetailses.add(new ScoreDetails(20, "no reason1", true, true));
        scoreDetailses.add(new ScoreDetails(90, "no reason2", true, true));
        scoreDetailses.add(new ScoreDetails(30, "no reason3", true, false));

        Team team = new Team();
        team.addPlayer(new Player("winner", "111", true, false, 0));

        HashMap<Team, ArrayList<ScoreDetails>> finalMap = new HashMap();
        finalMap.put(team, scoreDetailses);

        Team loserTeam = new Team();
        loserTeam.addPlayer(new Player("loser", "999", true, false, 0));
        loserTeam.addPlayer(new Player("loser2", "888", true, false, 0));

        ArrayList<Team> losers = new ArrayList();
        losers.add(loserTeam);

        restfulApi.updateScores(finalMap, losers, room, MockModel.mockProfile(), new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        assertFirebaseData("leaderboard/" + room.getGame().getAbbr() + "/" + MockModel.mockProfile().getUserId() + "/score",
                null);


        deleteUsersOrRoom(null, room);
    }

    @Test
    public void testSaveScoresSuccessAccumulate(){

        UserIdSecretModel user1 = createUser();
        UserIdSecretModel user2 = createUser();

        String user1Token = loginUser(user1.getUserId(), user1.getSecret());
        Profile user1Profile = new Profile();
        user1Profile.setUserId(user1.getUserId());
        user1Profile.setToken(user1Token);
        Profile user2Profile = new Profile();
        user2Profile.setUserId(user2.getUserId());

        final Room room = MockModel.mockRoom("testRoomId");
        deleteRoom(room);

        room.getGame().setLeaderbordTypeEnum(LeaderboardType.Accumulate);
        room.getRoomUsersMap().clear();
        room.addRoomUser(user1Profile, true);
        room.addRoomUser(user2Profile, true);
        room.storeRoomUsersToOriginalRoomUserIds();
        room.setRoundCounter(2);

        saveRoom(room);

        RestfulApi restfulApi = new RestfulApi();

        ArrayList<ScoreDetails> scoreDetailses = new ArrayList();
        scoreDetailses.add(new ScoreDetails(20, "no reason1", true, true));
        scoreDetailses.add(new ScoreDetails(90, "no reason2", true, true));
        scoreDetailses.add(new ScoreDetails(30, "no reason3", true, false));

        Profile winner = room.getRoomUserBySlotIndex(0).getProfile();
        Profile loser = room.getRoomUserBySlotIndex(1).getProfile();

        Team team = new Team();
        team.addPlayer(new Player("winner", winner.getUserId(), true, false, 0));

        HashMap<Team, ArrayList<ScoreDetails>> finalWinnerMap = new HashMap();
        finalWinnerMap.put(team, scoreDetailses);

        Team loserTeam = new Team();
        loserTeam.addPlayer(new Player("loser", loser.getUserId(), true, false, 0));

        ArrayList<Team> losers = new ArrayList();
        losers.add(loserTeam);

        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        assertFirebaseData("leaderboard/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/score",
                "140");

        assertFirebaseData("streaks/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/streakCount",
                "2");


        //rerun update, should be rejected this time
        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        assertFirebaseData("leaderboard/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/score",
                "140");

        assertFirebaseData("streaks/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/streakCount",
                "2");

        //rerun update with different round counter
        room.setRoundCounter(6);
        saveRoom(room);

        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        assertFirebaseData("leaderboard/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/score",
                "280");

        assertFirebaseData("userLeaderboardLog/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId()
                            + "/" + user1Profile.getUserId(),
                "280");

        assertFirebaseData("streaks/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/streakCount",
                "4");


        deleteUsersOrRoom(user1Profile.getUserId(), room);
        deleteUsersOrRoom(user2Profile.getUserId(), room);

    }


    @Test
    public void testSaveScoresSuccessNormal(){

        UserIdSecretModel user1 = createUser();
        UserIdSecretModel user2 = createUser();

        String user1Token = loginUser(user1.getUserId(), user1.getSecret());
        Profile user1Profile = new Profile();
        user1Profile.setUserId(user1.getUserId());
        user1Profile.setToken(user1Token);
        Profile user2Profile = new Profile();
        user2Profile.setUserId(user2.getUserId());

        final Room room = MockModel.mockRoom("testRoomId");
        deleteRoom(room);

        room.getGame().setLeaderbordTypeEnum(LeaderboardType.Normal);
        room.getRoomUsersMap().clear();
        room.addRoomUser(user1Profile, true);
        room.addRoomUser(user2Profile, true);
        room.storeRoomUsersToOriginalRoomUserIds();
        room.setRoundCounter(2);

        saveRoom(room);

        RestfulApi restfulApi = new RestfulApi();

        ArrayList<ScoreDetails> scoreDetailses = new ArrayList();
        scoreDetailses.add(new ScoreDetails(20, "no reason1", true, true));
        scoreDetailses.add(new ScoreDetails(90, "no reason2", true, true));
        scoreDetailses.add(new ScoreDetails(30, "no reason3", true, false));

        Profile winner = room.getRoomUserBySlotIndex(0).getProfile();
        Profile loser = room.getRoomUserBySlotIndex(1).getProfile();

        Team team = new Team();
        team.addPlayer(new Player("winner", winner.getUserId(), true, false, 0));

        HashMap<Team, ArrayList<ScoreDetails>> finalWinnerMap = new HashMap();
        finalWinnerMap.put(team, scoreDetailses);

        Team loserTeam = new Team();
        loserTeam.addPlayer(new Player("loser", loser.getUserId(), true, false, 0));

        ArrayList<Team> losers = new ArrayList();
        losers.add(loserTeam);

        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        assertFirebaseData("leaderboard/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/score",
                "140");

        assertFirebaseData("streaks/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/streakCount",
                "2");


        //rerun update with different round counter
        scoreDetailses.add(new ScoreDetails(10, "no reason3", true, false));
        room.setRoundCounter(6);
        saveRoom(room);

        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        assertFirebaseData("leaderboard/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/score",
                "150");

        assertFirebaseData("userLeaderboardLog/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId()
                        + "/" + user1Profile.getUserId(),
                "150");

        assertFirebaseData("streaks/" + room.getGame().getAbbr() + "/" + user1Profile.getUserId() + "/streakCount",
                "4");


        deleteUsersOrRoom(user1Profile.getUserId(), room);
        deleteUsersOrRoom(user2Profile.getUserId(), room);

    }

    @Test
    public void testScoresRetrieving(){

        final UserIdSecretModel user1 = createUser();
        final UserIdSecretModel user2 = createUser();
        final UserIdSecretModel user3 = createUser();

        String user1Token = loginUser(user1.getUserId(), user1.getSecret());
        Profile user1Profile = new Profile();
        user1Profile.setUserId(user1.getUserId());
        user1Profile.setToken(user1Token);
        Profile user2Profile = new Profile();
        user2Profile.setUserId(user2.getUserId());
        Profile user3Profile = new Profile();
        user3Profile.setUserId(user3.getUserId());


        final Room room = MockModel.mockRoom("testRoomId");
        room.getGame().setAbbr("test_game");
        deleteRoom(room);

        room.getGame().setLeaderbordTypeEnum(LeaderboardType.Normal);
        room.getRoomUsersMap().clear();
        room.addRoomUser(user1Profile, true);
        room.addRoomUser(user2Profile, true);
        room.addRoomUser(user3Profile, true);
        room.storeRoomUsersToOriginalRoomUserIds();
        room.setRoundCounter(2);

        FirebaseDB firebaseDB = new FirebaseDB(_appTestUrl);
        firebaseDB.deleteLeaderBoard(room.getGame(), new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        saveRoom(room);

        RestfulApi restfulApi = new RestfulApi();

        ArrayList<ScoreDetails> scoreDetailses = new ArrayList();
        scoreDetailses.add(new ScoreDetails(20, "no reason1", true, true));
        scoreDetailses.add(new ScoreDetails(90, "no reason2", true, true));
        scoreDetailses.add(new ScoreDetails(30, "no reason3", true, false));

        Profile winner = room.getRoomUserBySlotIndex(0).getProfile();
        Profile winner2 = room.getRoomUserBySlotIndex(2).getProfile();
        Profile loser = room.getRoomUserBySlotIndex(1).getProfile();


        Team team = new Team();
        team.addPlayer(new Player("winner", winner.getUserId(), true, false, 0));
        team.addPlayer(new Player("winner", winner2.getUserId(), true, false, 0));

        HashMap<Team, ArrayList<ScoreDetails>> finalWinnerMap = new HashMap();
        finalWinnerMap.put(team, scoreDetailses);

        Team loserTeam = new Team();
        loserTeam.addPlayer(new Player("loser", loser.getUserId(), true, false, 0));

        ArrayList<Team> losers = new ArrayList();
        losers.add(loserTeam);

        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);


        winner = room.getRoomUserBySlotIndex(1).getProfile();
        loser = room.getRoomUserBySlotIndex(0).getProfile();
        scoreDetailses.add(new ScoreDetails(10, "no reason3", true, false));
        room.setRoundCounter(6);
        saveRoom(room);

        team = new Team();
        team.addPlayer(new Player("winner", winner.getUserId(), true, false, 0));

        finalWinnerMap = new HashMap();
        finalWinnerMap.put(team, scoreDetailses);

        loserTeam = new Team();
        loserTeam.addPlayer(new Player("loser", loser.getUserId(), true, false, 0));

        losers = new ArrayList();
        losers.add(loserTeam);

        restfulApi.updateScores(finalWinnerMap, losers, room, user1Profile, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);


        firebaseDB.getLeaderBoardAndStreak(room.getGame(), 100, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(2, obj.size());
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user2.getUserId()));
                Assert.assertEquals(true, obj.get(1).getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.get(1).getUserIds().contains(user3.getUserId()));
                Assert.assertEquals(150, obj.get(0).getScore(), 0);
                Assert.assertEquals(140, obj.get(1).getScore(), 0);
                Assert.assertEquals(2, obj.get(0).getStreak().getStreakCount());
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        firebaseDB.getTeamHighestLeaderBoardRecordAndStreak(room.getGame(),
                ArrayUtils.stringsToArray(user1.getUserId()),
                new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
                    @Override
                    public void onCallback(LeaderboardRecord obj, Status st) {
                        Assert.assertEquals(Status.SUCCESS, st);
                        Assert.assertEquals(null, obj);
                        Threadings.oneTaskFinish();
                    }
                });

        Threadings.waitTasks(1);

        firebaseDB.getTeamHighestLeaderBoardRecordAndStreak(room.getGame(),
                        ArrayUtils.stringsToArray(user1.getUserId(), user3.getUserId()),
                        new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
            @Override
            public void onCallback(LeaderboardRecord obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(2, obj.getUserIds().size());
                Assert.assertEquals(true, obj.getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.getUserIds().contains(user3.getUserId()));
                Assert.assertEquals(140, obj.getScore(), 0);
                Assert.assertEquals(2, obj.getStreak().getStreakCount(), 0);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        firebaseDB.getUserHighestLeaderBoardRecordAndStreak(room.getGame(), user2.getUserId(), new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
            @Override
            public void onCallback(LeaderboardRecord obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(1, obj.getUserIds().size());
                Assert.assertEquals(true, obj.getUserIds().contains(user2.getUserId()));
                Assert.assertEquals(150, obj.getScore(), 0);
                Assert.assertEquals(2, obj.getStreak().getStreakCount(), 0);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        firebaseDB.getUserHighestLeaderBoardRecordAndStreak(room.getGame(), user3.getUserId(), new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
            @Override
            public void onCallback(LeaderboardRecord obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(2, obj.getUserIds().size());
                Assert.assertEquals(true, obj.getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.getUserIds().contains(user3.getUserId()));
                Assert.assertEquals(140, obj.getScore(), 0);
                Assert.assertEquals(2, obj.getStreak().getStreakCount(), 0);
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);


        deleteUsersOrRoom(user1Profile.getUserId() + "," + user3Profile.getUserId(), room);
        deleteUsersOrRoom(user2Profile.getUserId(), room);
    }


    private void assertFirebaseData(String relativeUrl, final String expectedValue){
        firebase.child(relativeUrl).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(expectedValue == null){
                    Assert.assertEquals(null, snapshot.getValue());
                }
                else{
                    Assert.assertEquals(expectedValue, String.valueOf(snapshot.getValue()));
                }

                Threadings.oneTaskFinish();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Threadings.waitTasks(1);
    }

    private void deleteUsersOrRoom(String userIds){
        deleteUsersOrRoom(userIds, null);
    }

    private void deleteUsersOrRoom(String userIds, Room room){
        ArrayList<String> userIdsArray = null;
        if(userIds != null){
           userIdsArray = Strings.split(userIds, ",");
        }


        if(room != null){
            deleteRoom(room);

            if(userIds != null){
                firebase.child("leaderboard/" + room.getGame().getAbbr() + "/" + userIds).setValue(null);
                firebase.child("streaks/" + room.getGame().getAbbr() + "/" + userIds).setValue(null);

                for(String userId : userIdsArray){
                    firebase.child("userLeaderboardLog/" + room.getGame().getAbbr() + "/" + userId).setValue(null);
                }
            }


        }

        if(userIds != null){
            int i = 0;
            for(String userId : userIdsArray){
                boolean last = false;
                if(i == userIdsArray.size() - 1){
                    last = true;
                }
                firebase.child("secret/users/" + userId).setValue(null);
                final boolean finalLast = last;
                firebase.child("users/" + userId).setValue(null, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if(finalLast) Threadings.oneTaskFinish();
                    }
                });
                i++;
            }
        }
        else{
            Threadings.oneTaskFinish();
        }


        Threadings.waitTasks(1);
    }

    private void deleteRoom(Room room){
        firebase.child("updatedScores/" + room.getId()).setValue(null);
        firebase.child("rooms/" + room.getId()).setValue(null);
    }

    private UserIdSecretModel createUser(){
        final UserIdSecretModel[] createdUser = new UserIdSecretModel[1];
        RestfulApi restfulApi = new RestfulApi();
        restfulApi.createNewUser(new RestfulApiListener<UserIdSecretModel>() {
            @Override
            public void onCallback(UserIdSecretModel obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(false, Strings.isEmpty(obj.getUserId()));
                Assert.assertEquals(false, Strings.isEmpty(obj.getSecret()));
                createdUser[0] = obj;
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);

        return createdUser[0];
    }

    private String loginUser(String userId, String secret){
        RestfulApi restfulApi = new RestfulApi();
        final String[] token = new String[1];
        restfulApi.loginUser(userId, secret, new RestfulApiListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                token[0] = obj;
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(false, Strings.isEmpty(obj));
                Threadings.oneTaskFinish();
            }
        });

        Threadings.waitTasks(1);
        return token[0];
    }

    private void saveRoom(Room room){
        firebase.child("rooms").child(room.getId()).setValue(room, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Threadings.oneTaskFinish();
            }
        });
        Threadings.waitTasks(1);
    }





}
