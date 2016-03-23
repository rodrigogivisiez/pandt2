package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.helpers.services.FirebaseDB;
import com.potatoandtomato.common.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.Status;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.models.Streak;
import helpers.T_Threadings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class TestFireBase extends TestAbstract {

    private String _unitTestUrl = "https://forunittest.firebaseio.com";
    IDatabase databases;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        databases = new FirebaseDB(_unitTestUrl);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        databases.clearAllListeners();
    }

    @Test
    public void testConnectFirebase(){
        final boolean[] waiting = {true};
        databases.getTestTableCount(new DatabaseListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                waiting[0] = false;
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, obj > 0);
            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }
    }

    @Test
    public void testRemoveListeners(){
        final boolean[] waiting = {true};
        final boolean[] cleared = {false};

        final Room r = MockModel.mockRoom(null);
        r.setRoundCounter(22);
        r.setOpen(true);

        databases.saveRoom(r, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        final int[] trigger = {0};
        waiting[0] = true;

        databases.monitorAllRooms(new ArrayList<Room>(), getClassTag(),  new SpecialDatabaseListener<ArrayList<Room>, Room>(Room.class) {
            @Override
            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
                waiting[0] = false;
            }

            @Override
            public void onCallbackTypeTwo(Room obj, Status st) {
                if(cleared[0]){
                    trigger[0]++;
                }

            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;

        databases.monitorRoomById(r.getId(), getClassTag(),  new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                waiting[0] = false;
                trigger[0]++;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        databases.clearListenersByClassTag(getClassTag());
        cleared[0] = true;

        waiting[0] = true;

        r.setOpen(true);
        databases.saveRoom(r, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }
        Threadings.sleep(1000);

        Assert.assertEquals(0, trigger[0] - 1); //minus one for initial call of monitorRoomById

    }


    @Test
    public void testCreateUserAndMonitorUser(){
        final int[] count = {0};

        databases.loginAnonymous(new DatabaseListener<Profile>() {
            @Override
            public void onCallback(Profile obj, Status st) {
                Assert.assertEquals(false, st == Status.FAILED);
                Assert.assertEquals(false, obj == null);
                databases.createUserByUserId(obj.getUserId(), new DatabaseListener<Profile>() {
                    @Override
                    public void onCallback(Profile obj, Status st) {
                        Assert.assertEquals(false, st == Status.FAILED);
                        Assert.assertEquals(false, obj == null);
                        databases.getProfileByUserId(obj.getUserId(), new DatabaseListener<Profile>(Profile.class) {
                            @Override
                            public void onCallback(Profile obj, Status st) {
                                Assert.assertEquals(false, st == Status.FAILED);
                                Assert.assertEquals(false, obj == null);
                                Assert.assertEquals(false, obj.getUserId() == null);


                                databases.monitorProfileByUserId(obj.getUserId(), getClassTag(),  new DatabaseListener<Profile>(Profile.class) {
                                    @Override
                                    public void onCallback(Profile obj, Status st) {
                                        if(obj.getUserPlayingState() != null)
                                            Assert.assertEquals("1", obj.getUserPlayingState().getRoomId());
                                            Assert.assertEquals(true, obj.getUserPlayingState().getConnected());
                                            count[0] = 2;
                                        }
                                });

                                obj.setUserPlayingState(new UserPlayingState("1", true, 0));
                                databases.updateProfile(obj, null);
                            }
                        });
                    }
                });

            }
        });

        while(count[0] < 1){
            T_Threadings.sleep(100);
        }
    }

    @Test
    public void testGameNameUnique(){
        final boolean[] waiting = {true};
        final Profile profile = MockModel.mockProfile();
        databases.updateProfile(profile, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;

        databases.getProfileByGameNameLower(profile.getGameName(), new DatabaseListener<Profile>(Profile.class) {
            @Override
            public void onCallback(Profile result, Status st) {
                Assert.assertEquals(profile.getGameName(), result.getGameName());
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

    }


    @Test
    public void testGetAllGames(){
        final boolean[] waiting = {true};
        databases.getAllGames(new DatabaseListener<ArrayList<Game>>(Game.class) {
            @Override
            public void onCallback(ArrayList<Game> obj, Status st) {
                Assert.assertEquals(false, st == Status.FAILED);
                Assert.assertEquals(false, obj == null);
                waiting[0] = false;
            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }
    }


    @Test
    public void testSave_MonitorSingleRoom_OnDisconnectRoom(){

        final int[] monitorCount = {0};
        final boolean[] waiting = {true};

        //create room
        final Room r = MockModel.mockRoom(null);

        Assert.assertEquals(true, r.getId() == null);


        databases.saveRoom(r, true, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(true, st == Status.SUCCESS);
                waiting[0] = false;
            }
        });

        Assert.assertEquals(false, r.getId() == null);

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        //create end

        waiting[0] = true;

        databases.monitorRoomById(r.getId(), getClassTag(),  new DatabaseListener<Room>(Room.class) {
            @Override
            public void onCallback(Room obj, Status st) {
                Assert.assertEquals(true, st == Status.SUCCESS);
                Assert.assertEquals(r.getId(), obj.getId());

                Assert.assertEquals(obj.getGame().getAbbr(), r.getGame().getAbbr());
                Assert.assertTrue(obj.getHost().equals(r.getHost()));
                Assert.assertEquals(obj.getRoomUsers().size(), r.getRoomUsers().size());
                for (Map.Entry<String, RoomUser> entry : obj.getRoomUsers().entrySet()) {
                    String key = entry.getKey();
                    RoomUser user1 = obj.getRoomUsers().get(key);
                    RoomUser user2 = r.getRoomUsers().get(key);

                    Assert.assertTrue((user1.getProfile().equals(user2.getProfile())));
                    Assert.assertEquals(user1.getSlotIndex(), user2.getSlotIndex());
                }
                Assert.assertEquals(r.getRoomId(), obj.getRoomId());
                Assert.assertEquals(r.getRoundCounter(), obj.getRoundCounter());
                Assert.assertEquals(r.isOpen(), obj.isOpen());
                Assert.assertEquals(r.isPlaying(), obj.isPlaying());
                monitorCount[0]++;
                waiting[0] = false;
            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }


        //update room
        waiting[0] = true;
        r.setRoomId("999");
        databases.saveRoom(r, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(true, st == Status.SUCCESS);
                waiting[0] = false;
            }
        });
        while(waiting[0]){
            T_Threadings.sleep(100);
        }
        //update end

        Assert.assertEquals(2, monitorCount[0]);

        T_Threadings.sleep(500);
        //disconnected
        waiting[0] = true;
        databases.removeUserFromRoomOnDisconnect(r.getId(), r.getHost(), new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(st, Status.SUCCESS);
                databases.offline();
                r.getRoomUsers().remove(r.getHost().getUserId());
                r.setOpen(false);
                databases.online();
            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }

        Assert.assertEquals(3, monitorCount[0]);

        waiting[0] = true;

        r.getRoomUsers().get("another").setSlotIndex(20);
        databases.saveRoom(r, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {

            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }
        Assert.assertEquals(4, monitorCount[0]);
    }

    @Test
    public void TestMonitorAllRoom(){
        final int[] monitorCount = {0};
        final boolean[] waiting = {true};
        final boolean[] waiting2 = {true};
        ArrayList<Room> rooms = new ArrayList();

        final Room r = MockModel.mockRoom(null);
        r.setOpen(true);

        databases.saveRoom(r, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                waiting[0] = false;
            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }


        waiting[0] = true;


        databases.monitorAllRooms(rooms, getClassTag(),  new SpecialDatabaseListener<ArrayList<Room>, Room>() {
            @Override
            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
                Assert.assertEquals(st , Status.SUCCESS);
                Assert.assertEquals(true , obj.size()>0);
                waiting2[0] = false;
            }

            @Override
            public void onCallbackTypeTwo(Room obj, Status st) {
                Assert.assertEquals(st , Status.SUCCESS);
                monitorCount[0]++;
                waiting[0] = false;
            }
        });

        while(waiting2[0]){
            T_Threadings.sleep(100);
        }

        Assert.assertEquals(true , rooms.size()>0);

        waiting[0] =true;
        waiting2[0] = true;
        r.setOpen(false);

        databases.saveRoom(r, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {

            }
        });

        while(waiting[0]){
            T_Threadings.sleep(100);
        }


        Assert.assertEquals(1, monitorCount[0]);

        for(Room r1 : rooms){
            if(r1.getId().equals(r.getId())){
                Assert.assertEquals(r1.isOpen(), r.isOpen());
            }
        }
    }




    @Test
    public void testOnDcSetGameStateDisconnected(){
        final boolean[] waiting = {true};
        Profile profile = MockModel.mockProfile();
        profile.setUserPlayingState(new UserPlayingState("1", true, 0));
        databases.updateProfile(profile, null);

        databases.onDcSetGameStateDisconnected(profile, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                databases.offline();
                databases.online();
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;

        databases.getProfileByUserId(profile.getUserId(), new DatabaseListener<Profile>(Profile.class) {
            @Override
            public void onCallback(Profile obj, Status st) {
                Assert.assertEquals(false, obj.getUserPlayingState().getConnected());
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

    }

    @Test
    public void testSaveGameHistoryAndRetrieve(){

        Room room = MockModel.mockRoom("1");
        Profile myProfile =((RoomUser) room.getRoomUsers().values().toArray()[0]).getProfile();
        final Profile anotherProfile = ((RoomUser) room.getRoomUsers().values().toArray()[1]).getProfile();
        anotherProfile.setGameName("first");

        final boolean[] waiting = {true};

        databases.updateProfile(myProfile, null);
        databases.updateProfile(anotherProfile, null);

        databases.savePlayedHistory(myProfile, room, new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] =false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;
        anotherProfile.setGameName("second");
        databases.updateProfile(anotherProfile, null);

        databases.getPlayedHistories(myProfile, new DatabaseListener<ArrayList<GameHistory>>(GameHistory.class) {
            @Override
            public void onCallback(ArrayList<GameHistory> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(1, obj.size());
                GameHistory history = obj.get(0);
                Assert.assertEquals(false, history.getCreationDateLong() == null);
                Assert.assertEquals(true, Integer.valueOf(history.getCreationDateAgo().split(" ")[0]) < 30);
                waiting[0] = false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

    }


    @Test
    public void testGetPendingInviteCount(){

        Room room = MockModel.mockRoom("1");
        room.setOpen(true);
        Profile myProfile =MockModel.mockProfile("33");
        room.addInvitedUser(myProfile);

        final boolean[] waiting = {true};

        databases.saveRoom(room, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] =false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;

        databases.getPendingInvitationsCount(myProfile, new DatabaseListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                Assert.assertEquals(true, obj == 1);
                waiting[0] =false;
            }
        });


        while (waiting[0]){
            Threadings.sleep(100);
        }

        waiting[0] = true;
        room.setOpen(false);
        databases.saveRoom(room, true,  new DatabaseListener<String>() {
            @Override
            public void onCallback(String obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                waiting[0] =false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }
        waiting[0] = true;

        databases.getPendingInvitationsCount(myProfile, new DatabaseListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                Assert.assertEquals(true, obj == 0);
                waiting[0] =false;
            }
        });

        while (waiting[0]){
            Threadings.sleep(100);
        }

    }


    @Test
    public void testLeaderboardWithAccumulate(){
        Room room = MockModel.mockRoom("1");
        Game game = MockModel.mockGame();
        game.setLeaderbordTypeEnum(LeaderboardType.Accumulate);
        room.setGame(game);

        final Profile user1 = MockModel.mockProfile();
        user1.setGameName("user1");
        final Profile user2 = MockModel.mockProfile("user2");
        user2.setGameName("user2");

        databases.updateProfile(user1, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        databases.updateProfile(user2, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        databases.deleteLeaderBoard(game, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(3);

        final LeaderboardRecord leaderboardRecord = new LeaderboardRecord();
        leaderboardRecord.setScore(1000);
        leaderboardRecord.addUserId(user1.getUserId());
        leaderboardRecord.addUserId(user2.getUserId());

        Streak streak = new Streak();
        streak.setStreakCount(5);
        leaderboardRecord.setStreak(streak);


        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.getLeaderBoardAndStreak(game, 100, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(1, obj.size());
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user2.getUserId()));
                Assert.assertEquals(1000, obj.get(0).getScore());
                Assert.assertEquals(5, obj.get(0).getStreak().getStreakCount());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        leaderboardRecord.getStreak().addStreakCount();
        leaderboardRecord.setScore(1100);

        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.getLeaderBoardAndStreak(game, 100, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(1, obj.size());
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user2.getUserId()));
                Assert.assertEquals(1100, obj.get(0).getScore());
                Assert.assertEquals(6, obj.get(0).getStreak().getStreakCount());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);


        databases.getAccLeaderBoardRecordAndStreak(room, leaderboardRecord.getUserIds(), new DatabaseListener<LeaderboardRecord>(LeaderboardRecord.class) {
            @Override
            public void onCallback(LeaderboardRecord obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, obj.getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.getUserIds().contains(user2.getUserId()));
                Assert.assertEquals(1100, obj.getScore());
                Assert.assertEquals(6, obj.getStreak().getStreakCount());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

    }


    @Test
    public void testLeaderboardNormal(){
        Room room = MockModel.mockRoom("1");
        Game game = MockModel.mockGame();
        game.setLeaderbordTypeEnum(LeaderboardType.Normal);
        room.setGame(game);

        final Profile user1 = MockModel.mockProfile();
        user1.setGameName("user1");
        final Profile user2 = MockModel.mockProfile("user2");
        user2.setGameName("user2");

        databases.updateProfile(user1, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        databases.updateProfile(user2, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        databases.deleteLeaderBoard(game, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(3);

        final LeaderboardRecord leaderboardRecord = new LeaderboardRecord();
        leaderboardRecord.setScore(1000);
        leaderboardRecord.addUserId(user1.getUserId());
        leaderboardRecord.addUserId(user2.getUserId());

        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        leaderboardRecord.setScore(1100);

        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        leaderboardRecord.setScore(500);

        room.setId("2");
        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.getLeaderBoardAndStreak(game, 100, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(2, obj.size());
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user1.getUserId()));
                Assert.assertEquals(true, obj.get(0).getUserIds().contains(user2.getUserId()));
                Assert.assertEquals(true, obj.get(0).getUserNames().contains(user1.getGameName()));
                Assert.assertEquals(true, obj.get(0).getUserNames().contains(user2.getGameName()));
                Assert.assertEquals(1100, obj.get(0).getScore());
                Assert.assertEquals(500, obj.get(1).getScore());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);


        databases.getLeaderBoardAndStreak(game, 1, new DatabaseListener<ArrayList<LeaderboardRecord>>(LeaderboardRecord.class) {
            @Override
            public void onCallback(ArrayList<LeaderboardRecord> obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(1, obj.size());
                Assert.assertEquals(1100, obj.get(0).getScore());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

    }


    @Test
    public void testStreaks(){
        final Room room = MockModel.mockRoom(String.valueOf(System.currentTimeMillis() / 1000L));
        Game game = MockModel.mockGame();
        game.setLeaderbordTypeEnum(LeaderboardType.Accumulate);
        room.setGame(game);

        final Profile user1 = MockModel.mockProfile();
        user1.setGameName("user1");
        final Profile user2 = MockModel.mockProfile("user2");
        user2.setGameName("user2");

        databases.updateProfile(user1, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        databases.updateProfile(user2, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        databases.deleteLeaderBoard(game, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(3);

        final LeaderboardRecord leaderboardRecord = new LeaderboardRecord();
        leaderboardRecord.setScore(1000);
        leaderboardRecord.addUserId(user1.getUserId());
        leaderboardRecord.addUserId(user2.getUserId());
        leaderboardRecord.resetStreak();

        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.isStreakRevived(leaderboardRecord.getUserIds(), room, new DatabaseListener<Boolean>() {
            @Override
            public void onCallback(Boolean obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(false, obj);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.streakRevive(leaderboardRecord.getUserIds(), room, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.isStreakRevived(leaderboardRecord.getUserIds(), room, new DatabaseListener<Boolean>() {
            @Override
            public void onCallback(Boolean obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, obj);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        final Streak streak = leaderboardRecord.getStreak();
        streak.setStreakCount(5);
        streak.setLastLoseRoomId(room.getId());
        streak.setLastLoseRoundNumber(room.getRoundCounter());
        Assert.assertEquals(false, streak.hasValidStreak());

        databases.saveLeaderBoardRecord(room, leaderboardRecord, new DatabaseListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.getAccLeaderBoardRecordAndStreak(room, leaderboardRecord.getUserIds(), new DatabaseListener<LeaderboardRecord>() {
            @Override
            public void onCallback(LeaderboardRecord result, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(true, result.getStreak().hasValidStreak());
                Assert.assertEquals(streak.getStreakCount(), result.getStreak().getStreakCount());
                Assert.assertEquals(room.getRoundCounter(), result.getStreak().getLastReviveRoundNumber());
                Assert.assertEquals(room.getId(), result.getStreak().getLastReviveRoomId());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

        databases.getUserStreak(room.getGame(), user1.getUserId(), new DatabaseListener<Streak>(Streak.class) {
            @Override
            public void onCallback(Streak obj, Status st) {
                Assert.assertEquals(Status.SUCCESS, st);
                Assert.assertEquals(room.getId(), obj.getLastLoseRoomId());
                T_Threadings.oneTaskFinish();
            }
        });

        T_Threadings.waitTasks(1);

    }
















}
