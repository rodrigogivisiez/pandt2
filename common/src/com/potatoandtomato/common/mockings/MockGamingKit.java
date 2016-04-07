package com.potatoandtomato.common.mockings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.models.InGameUpdateMessage;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.JsonObj;
import com.potatoandtomato.common.utils.MultiHashMap;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by SiongLeng on 29/7/2015.
 */
public class MockGamingKit {

    private final String _appKey = "9d6c174f8ea9985f9b4be630845bd8e57a91f9df73a27d65d64f6e00d2ed4202";
    private final String _secretKey = "e8d0cda539241037828634e01504aa017b28bfb0519b8884f6ebfafc0062fc96";
    private WarpClient _warpInstance;
    private String _userId;
    private int _expectedTeamCount, _eachTeamExpectedPlayers;
    private Runnable _onReady;
    private GameCoordinator _coordinator;
    private Broadcaster _broadcaster;
    private MultiHashMap<String, String> _msgPieces;


    public MockGamingKit(GameCoordinator coordinator, int expectedTeamCount,
                         int eachTeamExpectedPlayers,
                         Broadcaster broadcaster, Runnable onReady) {
        this._onReady = onReady;
        this._broadcaster = broadcaster;
        this._expectedTeamCount = expectedTeamCount;
        this._eachTeamExpectedPlayers = eachTeamExpectedPlayers;
        this._coordinator = coordinator;
        this._msgPieces = new MultiHashMap();

        long unixTime = System.currentTimeMillis() / 1000L;
        _userId = String.valueOf(unixTime);

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


        _warpInstance.connectWithUserName(_userId);



    }

    public void sendUpdate(String msg){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", _userId);
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(_eachTeamExpectedPlayers == 0 || _expectedTeamCount == 0){
            _broadcaster.broadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE,
                    new InGameUpdateMessage(_userId, msg));
        }
        else{
            String toSend = jsonObject.toString();
            if(toSend.length() > 800){
                ArrayList<String> results = Strings.split(toSend, 800);
                String id = Strings.generateRandomKey(10);
                for(int i = 0; i < results.size(); i++){
                    _warpInstance.sendUpdatePeers(appendDataToPeerUpdate(results.get(i), i, results.size(), id).getBytes());
                }
            }
            else{
                _warpInstance.sendUpdatePeers(toSend.getBytes());
            }

        }
    }

    private String appendDataToPeerUpdate(String input, int pieceIndex, int totalPieces, String id){
        String sending = String.format("@PT_%s_%s_%s@", id, pieceIndex, totalPieces) + input;
        return sending;
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
             System.out.println("onJoinRoomDone: " + roomEvent.getData().getId());
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
            unAppendDataFromPeerMsg(update);
        }

        private void updateRoomMatesReceived(String update){
            try {
                final JSONObject updateJson = new JSONObject(update);
                if(updateJson.getString("msg").equals("SURRENDER")){
                    Threadings.delay(5000, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                _coordinator.userAbandon(updateJson.getString("userId"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    _broadcaster.broadcast(BroadcastEvent.INGAME_UPDATE_RESPONSE,
                            new InGameUpdateMessage(updateJson.getString("userId"), updateJson.getString("msg")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private synchronized void unAppendDataFromPeerMsg(String msg){
            if(msg.startsWith("@PT")){
                int endIndex = msg.indexOf("@", 2);
                String stripped = msg.substring(0, endIndex);
                String realMsg = msg.substring(endIndex + 1);
                String meta[] = stripped.split("_");
                String id = meta[1];
                int pieceIndex = Integer.valueOf(meta[2]);
                int totalPieces = Integer.valueOf(meta[3]);

                if(totalPieces == 1){
                    updateRoomMatesReceived(realMsg);
                } else {
                    if (!_msgPieces.containsKey(id)){
                        _msgPieces.put(id, realMsg);
                    }
                    else{
                        ArrayList<String> currentPieces = _msgPieces.get(id);
                        if(pieceIndex > currentPieces.size()){
                            currentPieces.add(currentPieces.size(), realMsg);
                        }
                        else{
                            currentPieces.add(pieceIndex, realMsg);
                        }

                        if(currentPieces.size() == totalPieces){
                            updateRoomMatesReceived(Strings.joinArr(currentPieces, ""));
                            _msgPieces.remove(id);
                        }
                    }
                }
            }
            else{
                updateRoomMatesReceived(msg);
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
                boolean isHost = true;
                int i = 0;
                Team team = new Team();

                ArrayList<String> users = new ArrayList();
                for(String user : liveRoomInfoEvent.getJoinedUsers()){
                    users.add(user);
                }

                Collections.sort(users);

                for(String user : users) {
                    team.addPlayer(new Player(user, user, isHost, true, Color.RED));
                    isHost = false;
                    i++;
                    if(i == _eachTeamExpectedPlayers){
                        teams.add(team);
                        team = new Team();
                        i=0;
                    }
                }
                _coordinator.setMyUserId(_userId);
                _coordinator.setTeams(teams);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if(_onReady != null) _onReady.run();
                        _onReady = null;
                    }
                });

            }
        }

        @Override
        public void onUserJoinedRoom(RoomData roomData, final String s) {
            System.out.println("onUserJoinedRoom");
            _warpInstance.getLiveRoomInfo(roomData.getId());
            Threadings.delay(5000, new Runnable() {
                @Override
                public void run() {
                    _coordinator.userConnectionChanged(s, true);
                }
            });

        }

        @Override
        public void onSendUpdateDone(byte b) {
            System.out.println("onSendUpdateDone:" + b);
        }

        @Override
        public void onUserLeftRoom(RoomData roomData, String s) {
            _coordinator.userConnectionChanged(s, false);
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
