package connection_testings;

import abstracts.TestAbstract;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.services.FirebaseDB;
import com.potatoandtomato.common.utils.Threadings;
import com.mygdx.potatoandtomato.models.*;
import com.potatoandtomato.common.enums.Status;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import helpers.T_Threadings;
import org.junit.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class TestFireBase extends TestAbstract {
//    private String _unitTestUrl = "https://forunittest.firebaseio.com";
//    IDatabase databases;
//
//
//    @Override
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        databases = new FirebaseDB(_unitTestUrl);
//    }
//
//    @Override
//    @After
//    public void tearDown() throws Exception {
//        super.tearDown();
//        databases.clearAllListeners();
//    }
//
//    @Test
//    public void testRemoveListeners(){
//        final boolean[] waiting = {true};
//        final boolean[] cleared = {false};
//
//        final Room r = MockModel.mockRoom(null);
//        r.setRoundCounter(22);
//        r.setOpen(true);
//
//        databases.saveRoom(r, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        final int[] trigger = {0};
//        waiting[0] = true;
//
//        databases.monitorAllRooms(new ArrayList<Room>(), getClassTag(),  new SpecialDatabaseListener<ArrayList<Room>, Room>(Room.class) {
//            @Override
//            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
//                waiting[0] = false;
//            }
//
//            @Override
//            public void onCallbackTypeTwo(Room obj, Status st) {
//                if(cleared[0]){
//                    trigger[0]++;
//                }
//
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        waiting[0] = true;
//
//        databases.monitorRoomById(r.getId(), getClassTag(),  new DatabaseListener<Room>(Room.class) {
//            @Override
//            public void onCallback(Room obj, Status st) {
//                waiting[0] = false;
//                trigger[0]++;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        databases.clearListenersByClassTag(getClassTag());
//        cleared[0] = true;
//
//        waiting[0] = true;
//
//        r.setOpen(true);
//        databases.saveRoom(r, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//        Threadings.sleep(1000);
//
//        Assert.assertEquals(0, trigger[0] - 1); //minus one for initial call of monitorRoomById
//
//    }
//
//
//    @Test
//    public void testCreateUserAndMonitorUser(){
//        final int[] count = {0};
//
//        final Profile p = MockModel.mockProfile();
//        databases.updateProfile(p, new DatabaseListener() {
//            @Override
//            public void onCallback(Object obj, Status st) {
//                Assert.assertEquals(false, st == Status.FAILED);
//                databases.getProfileByUserId(p.getUserId(), new DatabaseListener<Profile>(Profile.class) {
//                    @Override
//                    public void onCallback(Profile obj, Status st) {
//                        Assert.assertEquals(false, st == Status.FAILED);
//                        Assert.assertEquals(false, obj == null);
//                        Assert.assertEquals(false, obj.getUserId() == null);
//
//
//                        databases.monitorProfileByUserId(obj.getUserId(), getClassTag(),  new DatabaseListener<Profile>(Profile.class) {
//                            @Override
//                            public void onCallback(Profile obj, Status st) {
//                                if(obj.getUserPlayingState() != null)
//                                    Assert.assertEquals("1", obj.getUserPlayingState().getRoomId());
//                                Assert.assertEquals(true, obj.getUserPlayingState().getConnected());
//                                count[0] = 2;
//                            }
//                        });
//
//                        obj.setUserPlayingState(new UserPlayingState("1", true, 0));
//                        databases.updateProfile(obj, null);
//                    }
//                });
//            }
//        });
//
//        while(count[0] < 1){
//            T_Threadings.sleep(100);
//        }
//    }
//
//    @Test
//    public void testGameNameUnique(){
//        final boolean[] waiting = {true};
//        final Profile profile = MockModel.mockProfile();
//        databases.updateProfile(profile, new DatabaseListener() {
//            @Override
//            public void onCallback(Object obj, Status st) {
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        waiting[0] = true;
//
//        databases.getProfileByGameNameLower(profile.getGameName(), new DatabaseListener<Profile>(Profile.class) {
//            @Override
//            public void onCallback(Profile result, Status st) {
//                Assert.assertEquals(profile.getGameName(), result.getGameName());
//                Assert.assertEquals(Status.SUCCESS, st);
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//    }
//
//
//    @Test
//    public void testGetAllGames(){
//        final boolean[] waiting = {true};
//        databases.getAllGames(new DatabaseListener<ArrayList<Game>>(Game.class) {
//            @Override
//            public void onCallback(ArrayList<Game> obj, Status st) {
//                Assert.assertEquals(false, st == Status.FAILED);
//                Assert.assertEquals(false, obj == null);
//                waiting[0] = false;
//            }
//        });
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//    }
//
//
//    @Test
//    public void testSave_MonitorSingleRoom_OnDisconnectRoom(){
//
//        final int[] monitorCount = {0};
//        final boolean[] waiting = {true};
//
//        //create room
//        final Room r = MockModel.mockRoom(null);
//
//        Assert.assertEquals(true, r.getId() == null);
//
//
//        databases.saveRoom(r, true, new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                Assert.assertEquals(true, st == Status.SUCCESS);
//                waiting[0] = false;
//            }
//        });
//
//        Assert.assertEquals(false, r.getId() == null);
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//
//        //create end
//
//        waiting[0] = true;
//
//        databases.monitorRoomById(r.getId(), getClassTag(),  new DatabaseListener<Room>(Room.class) {
//            @Override
//            public void onCallback(Room obj, Status st) {
//                Assert.assertEquals(true, st == Status.SUCCESS);
//                Assert.assertEquals(r.getId(), obj.getId());
//
//                Assert.assertEquals(obj.getGame().getAbbr(), r.getGame().getAbbr());
//                Assert.assertTrue(obj.getHost().equals(r.getHost()));
//                Assert.assertEquals(obj.getRoomUsersMap().size(), r.getRoomUsersMap().size());
//                for (Map.Entry<String, RoomUser> entry : obj.getRoomUsersMap().entrySet()) {
//                    String key = entry.getKey();
//                    RoomUser user1 = obj.getRoomUsersMap().get(key);
//                    RoomUser user2 = r.getRoomUsersMap().get(key);
//
//                    Assert.assertTrue((user1.getProfile().equals(user2.getProfile())));
//                    Assert.assertEquals(user1.getSlotIndex(), user2.getSlotIndex());
//                }
//                Assert.assertEquals(r.getWarpRoomId(), obj.getWarpRoomId());
//                Assert.assertEquals(r.getRoundCounter(), obj.getRoundCounter());
//                Assert.assertEquals(r.isOpen(), obj.isOpen());
//                Assert.assertEquals(r.isPlaying(), obj.isPlaying());
//                monitorCount[0]++;
//                waiting[0] = false;
//            }
//        });
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//
//
//        //update room
//        waiting[0] = true;
//        r.setWarpRoomId("999");
//        databases.saveRoom(r, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                Assert.assertEquals(true, st == Status.SUCCESS);
//                waiting[0] = false;
//            }
//        });
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//        //update end
//
//        Assert.assertEquals(2, monitorCount[0]);
//
//        T_Threadings.sleep(500);
//        //disconnected
//        waiting[0] = true;
//        databases.removeUserFromRoomOnDisconnect(r.getId(), r.getHost(), new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                Assert.assertEquals(st, Status.SUCCESS);
//                databases.offline();
//                r.getRoomUsersMap().remove(r.getHost().getUserId());
//                r.setOpen(false);
//                databases.online();
//                waiting[0] = false;
//            }
//        });
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//
//        Assert.assertEquals(3, monitorCount[0]);
//
//        waiting[0] = true;
//
//        r.getRoomUsersMap().get("another").setSlotIndex(20);
//        databases.saveRoom(r, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//
//            }
//        });
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//        Assert.assertEquals(4, monitorCount[0]);
//    }
//
//    @Test
//    public void TestMonitorAllRoom(){
//        final int[] monitorCount = {0};
//        final boolean[] waiting = {true};
//        final boolean[] waiting2 = {true};
//        ArrayList<Room> rooms = new ArrayList();
//
//        final Room r = MockModel.mockRoom(null);
//        r.setOpen(true);
//
//        databases.saveRoom(r, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                waiting[0] = false;
//            }
//        });
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//
//
//        waiting[0] = true;
//
//
//        databases.monitorAllRooms(rooms, getClassTag(),  new SpecialDatabaseListener<ArrayList<Room>, Room>() {
//            @Override
//            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
//                Assert.assertEquals(st , Status.SUCCESS);
//                Assert.assertEquals(true , obj.size()>0);
//                waiting2[0] = false;
//            }
//
//            @Override
//            public void onCallbackTypeTwo(Room obj, Status st) {
//                Assert.assertEquals(st , Status.SUCCESS);
//                monitorCount[0]++;
//                waiting[0] = false;
//            }
//        });
//
//        while(waiting2[0]){
//            T_Threadings.sleep(100);
//        }
//
//        Assert.assertEquals(true , rooms.size()>0);
//
//        waiting[0] =true;
//        waiting2[0] = true;
//        r.setOpen(false);
//
//        databases.saveRoom(r, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//
//            }
//        });
//
//        while(waiting[0]){
//            T_Threadings.sleep(100);
//        }
//
//
//        Assert.assertEquals(1, monitorCount[0]);
//
//        for(Room r1 : rooms){
//            if(r1.getId().equals(r.getId())){
//                Assert.assertEquals(r1.isOpen(), r.isOpen());
//            }
//        }
//    }
//
//
//
//
//    @Test
//    public void testOnDcSetGameStateDisconnected(){
//        final boolean[] waiting = {true};
//        Profile profile = MockModel.mockProfile();
//        profile.setUserPlayingState(new UserPlayingState("1", true, 0));
//        databases.updateProfile(profile, null);
//
//        databases.onDcSetGameStateDisconnected(profile, new DatabaseListener() {
//            @Override
//            public void onCallback(Object obj, Status st) {
//                databases.offline();
//                databases.online();
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        waiting[0] = true;
//
//        databases.getProfileByUserId(profile.getUserId(), new DatabaseListener<Profile>(Profile.class) {
//            @Override
//            public void onCallback(Profile obj, Status st) {
//                Assert.assertEquals(false, obj.getUserPlayingState().getConnected());
//                Assert.assertEquals(Status.SUCCESS, st);
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//    }
//
//    @Test
//    public void testSaveGameHistoryAndRetrieve(){
//
//        Room room = MockModel.mockRoom("1");
//        Profile myProfile =((RoomUser) room.getRoomUsersMap().values().toArray()[0]).getProfile();
//        final Profile anotherProfile = ((RoomUser) room.getRoomUsersMap().values().toArray()[1]).getProfile();
//        anotherProfile.setGameName("first");
//
//        final boolean[] waiting = {true};
//
//        databases.updateProfile(myProfile, null);
//        databases.updateProfile(anotherProfile, null);
//
//        databases.savePlayedHistory(myProfile, room, new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                Assert.assertEquals(Status.SUCCESS, st);
//                waiting[0] =false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        waiting[0] = true;
//        anotherProfile.setGameName("second");
//        databases.updateProfile(anotherProfile, null);
//
//        databases.getPlayedHistories(myProfile, new DatabaseListener<ArrayList<GameHistory>>(GameHistory.class) {
//            @Override
//            public void onCallback(ArrayList<GameHistory> obj, Status st) {
//                Assert.assertEquals(Status.SUCCESS, st);
//                Assert.assertEquals(1, obj.size());
//                GameHistory history = obj.get(0);
//                Assert.assertEquals(false, history.getCreationDateLong() == null);
//                Assert.assertEquals(true, Integer.valueOf(history.getCreationDateAgo().split(" ")[0]) < 30);
//                waiting[0] = false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//    }
//
//
//    @Test
//    public void testGetPendingInviteCount(){
//
//        Room room = MockModel.mockRoom("1");
//        room.setOpen(true);
//        Profile myProfile =MockModel.mockProfile("33");
//        room.addInvitedUser(myProfile);
//
//        final boolean[] waiting = {true};
//
//        databases.saveRoom(room, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                Assert.assertEquals(Status.SUCCESS, st);
//                waiting[0] =false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        waiting[0] = true;
//
//        databases.getPendingInvitationsCount(myProfile, new DatabaseListener<Integer>() {
//            @Override
//            public void onCallback(Integer obj, Status st) {
//                Assert.assertEquals(true, obj == 1);
//                waiting[0] =false;
//            }
//        });
//
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//        waiting[0] = true;
//        room.setOpen(false);
//        databases.saveRoom(room, true,  new DatabaseListener<String>() {
//            @Override
//            public void onCallback(String obj, Status st) {
//                Assert.assertEquals(Status.SUCCESS, st);
//                waiting[0] =false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//        waiting[0] = true;
//
//        databases.getPendingInvitationsCount(myProfile, new DatabaseListener<Integer>() {
//            @Override
//            public void onCallback(Integer obj, Status st) {
//                Assert.assertEquals(true, obj == 0);
//                waiting[0] =false;
//            }
//        });
//
//        while (waiting[0]){
//            Threadings.sleep(100);
//        }
//
//    }
}
