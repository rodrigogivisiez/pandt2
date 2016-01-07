package com.potatoandtomato.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.potatoandtomato.common.*;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 29/7/2015.
 */
public class MockGamingKit {

    private final String _appKey = "34d20eca11ae247b144d561dce54d4f86b69690375469ed4c8700067bea1f6c2";
    private final String _secretKey = "bc419f9ac2c9c098375f4ad01f2a1758ce88a4fd14ff210fb188fb8bfe88606d";
    private WarpClient _warpInstance;
    private String _userId;
    private int _expectedTeamCount, _eachTeamExpectedPlayers;
    private Runnable _onReady;
    private GameCoordinator _coordinator;

    public MockGamingKit(GameCoordinator coordinator, int expectedTeamCount,
                         int eachTeamExpectedPlayers, Runnable onReady) {
        this._onReady = onReady;
        this._expectedTeamCount = expectedTeamCount;
        this._eachTeamExpectedPlayers = eachTeamExpectedPlayers;
        this._coordinator = coordinator;

        if(eachTeamExpectedPlayers == 0){
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _onReady.run();
                }
            });
            return;
        }


        WarpClient.initialize(_appKey, _secretKey);

        try {
            _warpInstance = WarpClient.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        WarpListeners listeners = new WarpListeners();
        _warpInstance.addRoomRequestListener(listeners);
        _warpInstance.addConnectionRequestListener(listeners);
        _warpInstance.addZoneRequestListener(listeners);
        _warpInstance.addNotificationListener(listeners);
        _warpInstance.addUpdateRequestListener(listeners);

        _userId = String.valueOf(MathUtils.random(1, 999999));
        _warpInstance.connectWithUserName(_userId);

        Broadcaster.getInstance().subscribe(BroadcastEvent.INGAME_UPDATE_REQUEST, new BroadcastListener<String>() {
            @Override
            public void onCallback(String msg, Status st) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", _userId);
                    jsonObject.put("msg", msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                _warpInstance.sendUpdatePeers(jsonObject.toString().getBytes());
            }
        });

    }

    public String getUserId() {
        return _userId;
    }

    public class WarpListeners implements ConnectionRequestListener, RoomRequestListener, ZoneRequestListener, NotifyListener, UpdateRequestListener {

        @Override
        public void onConnectDone(ConnectEvent connectEvent) {
            System.out.println("onConnectDone");
            if (connectEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                _warpInstance.getRoomInRange(1, 100);
            }
        }

        @Override
        public void onJoinRoomDone(RoomEvent roomEvent) {
             System.out.println("onJoinRoomDone");
            _warpInstance.getLiveRoomInfo(roomEvent.getData().getId());
        }

        @Override
        public void onGetMatchedRoomsDone(MatchedRoomsEvent matchedRoomsEvent) {
            System.out.println("onGetMatchedRoomsDone");
            RoomData[] rooms = matchedRoomsEvent.getRoomsData();
            if(rooms.length > 0){
                _warpInstance.joinRoom(rooms[0].getId());
                _warpInstance.subscribeRoom(rooms[0].getId());
            }
            else{
                _warpInstance.createRoom("temp", "temp", 100, null);
            }
        }

        @Override
        public void onCreateRoomDone(RoomEvent roomEvent) {
            System.out.println("onCreateRoomDone");
            _warpInstance.joinRoom(roomEvent.getData().getId());
            _warpInstance.subscribeRoom(roomEvent.getData().getId());
        }

        @Override
        public void onUpdatePeersReceived(final UpdateEvent updateEvent) {
            System.out.println("onUpdatePeersReceived");
            String update = new String(updateEvent.getUpdate());
            try {
                JSONObject updateJson = new JSONObject(update);
                Broadcaster.getInstance().broadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE,
                        new InGameUpdateMessage(updateJson.getString("userId"), updateJson.getString("msg")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent liveRoomInfoEvent) {
            System.out.println("onGetLiveRoomInfoDone, current user count: " + liveRoomInfoEvent.getJoinedUsers().length);
            if(liveRoomInfoEvent.getJoinedUsers().length > (_expectedTeamCount * _eachTeamExpectedPlayers)){
                _warpInstance.deleteRoom(liveRoomInfoEvent.getData().getId());
                System.out.println("Corrupted room destroyed, please restart");
            }

            if(liveRoomInfoEvent.getJoinedUsers().length == (_expectedTeamCount * _eachTeamExpectedPlayers)){
                ArrayList<Team> teams = new ArrayList<Team>();
                int i = 0;
                Team team = new Team();
                for(String user : liveRoomInfoEvent.getJoinedUsers()) {
                    team.addPlayer(new Player(user, user, 0, user.equals(_userId), i==0));
                    i++;
                    if(i == _eachTeamExpectedPlayers){
                        teams.add(team);
                        team = new Team();
                        i=0;
                    }
                }
                _coordinator.setTeams(teams);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _onReady.run();
                    }
                });

            }
        }

        @Override
        public void onUserJoinedRoom(RoomData roomData, String s) {
            System.out.println("onUserJoinedRoom");
            _warpInstance.getLiveRoomInfo(roomData.getId());
        }

        @Override
        public void onSendUpdateDone(byte b) {
            System.out.println("onSendUpdateDone:" + b);
        }












        @Override
        public void onGetLiveUserInfoDone(LiveUserInfoEvent liveUserInfoEvent) {

        }

        @Override
        public void onDisconnectDone(ConnectEvent connectEvent) {

        }

        @Override
        public void onInitUDPDone(byte b) {

        }


        @Override
        public void onSubscribeRoomDone(RoomEvent roomEvent) {

        }

        @Override
        public void onUnSubscribeRoomDone(RoomEvent roomEvent) {

        }

        @Override
        public void onLeaveRoomDone(RoomEvent roomEvent) {

        }



        @Override
        public void onSetCustomRoomDataDone(LiveRoomInfoEvent liveRoomInfoEvent) {

        }

        @Override
        public void onUpdatePropertyDone(LiveRoomInfoEvent liveRoomInfoEvent) {

        }

        @Override
        public void onLockPropertiesDone(byte b) {

        }

        @Override
        public void onUnlockPropertiesDone(byte b) {

        }

        @Override
        public void onDeleteRoomDone(RoomEvent roomEvent) {

        }

        @Override
        public void onGetAllRoomsDone(AllRoomsEvent allRoomsEvent) {

        }

        @Override
        public void onGetOnlineUsersDone(AllUsersEvent allUsersEvent) {

        }

        @Override
        public void onSetCustomUserDataDone(LiveUserInfoEvent liveUserInfoEvent) {

        }


        @Override
        public void onRoomCreated(RoomData roomData) {

        }

        @Override
        public void onRoomDestroyed(RoomData roomData) {

        }

        @Override
        public void onUserLeftRoom(RoomData roomData, String s) {

        }

        @Override
        public void onUserLeftLobby(LobbyData lobbyData, String s) {

        }

        @Override
        public void onUserJoinedLobby(LobbyData lobbyData, String s) {

        }

        @Override
        public void onChatReceived(ChatEvent chatEvent) {

        }

        @Override
        public void onPrivateChatReceived(String s, String s1) {

        }

        @Override
        public void onPrivateUpdateReceived(String s, byte[] bytes, boolean b) {

        }

        @Override
        public void onUserChangeRoomProperty(RoomData roomData, String s, HashMap<String, Object> hashMap, HashMap<String, String> hashMap1) {

        }

        @Override
        public void onMoveCompleted(MoveEvent moveEvent) {

        }

        @Override
        public void onGameStarted(String s, String s1, String s2) {

        }

        @Override
        public void onGameStopped(String s, String s1) {

        }

        @Override
        public void onUserPaused(String s, boolean b, String s1) {

        }

        @Override
        public void onUserResumed(String s, boolean b, String s1) {

        }

        @Override
        public void onNextTurnRequest(String s) {

        }

        @Override
        public void onSendPrivateUpdateDone(byte b) {

        }
    }
}
