package com.potatoandtomato.common.mockings;

import com.badlogic.gdx.Gdx;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.enums.SelfConnectionStatus;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.*;
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
    private MultiHashMap<String, String> _msgPieces;
    private int _delay;
    private boolean _pausing;
    private boolean gameStarted;
    private ArrayList<Runnable> onGameStartedRunnables;


    public MockGamingKit(GameCoordinator coordinator, int expectedTeamCount,
                         int eachTeamExpectedPlayers, int delay) {

        this._expectedTeamCount = expectedTeamCount;
        this._eachTeamExpectedPlayers = eachTeamExpectedPlayers;
        this._coordinator = coordinator;
        this._msgPieces = new MultiHashMap();
        this._delay = delay;
        this.onGameStartedRunnables = new ArrayList();

        long unixTime = System.currentTimeMillis() / 1000L;
        _userId = String.valueOf(unixTime);

        if(eachTeamExpectedPlayers == 0){
            return;
        }


        WarpClient.initialize(_appKey, _secretKey);
        WarpClient.setRecoveryAllowance(30);

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


    }

    public void connect(Runnable onReady){
        this._onReady = onReady;
        if(_eachTeamExpectedPlayers == 0){
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _onReady.run();
                }
            });
            return;
        }

        System.out.println("Connecting..");
        _warpInstance.connectWithUserName(_userId);
    }

    public void sendUpdate(final int code, final String msg, boolean isPrivate, String toUserId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", _userId);
            jsonObject.put("msg", msg);
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(_eachTeamExpectedPlayers == 0 || _expectedTeamCount == 0){
            Threadings.delay(_delay, new Runnable() {
                @Override
                public void run() {
                    if(msg.equals("SURRENDER")){
                        _coordinator.userAbandon(_userId);
                    }
                    else{
                        if(code == -1){
                            _coordinator.receivedRoomUpdate(msg, _userId);
                        }
                        else if(code == 0){
                            _coordinator.getGameDataHelper().receivedGameData(msg);
                        }
                        else if(code == 1){
                            _coordinator.getDecisionsMaker().receivedDecisionMakerUpdate(msg);
                        }
                    }

                }
            });

        }
        else{
            String toSend = jsonObject.toString();
            if(toSend.length() > 800){
                ArrayList<String> results = Strings.split(toSend, 800);
                String id = Strings.generateUniqueRandomKey(20);
                for(int i = 0; i < results.size(); i++){
                    if(isPrivate){
                        _warpInstance.sendPrivateUpdate(toUserId, appendDataToPeerUpdate(results.get(i), i, results.size(), id).getBytes());
                    }
                    else{
                        _warpInstance.sendUpdatePeers(appendDataToPeerUpdate(results.get(i), i, results.size(), id).getBytes());
                    }

                }
            }
            else{
                if(isPrivate){
                    _warpInstance.sendPrivateUpdate(toUserId, toSend.getBytes());
                }
                else{
                    _warpInstance.sendUpdatePeers(toSend.getBytes());
                }

            }
        }
    }

    public void disconnect(){
        if(_warpInstance != null)  _warpInstance.disconnect();
    }

    private String appendDataToPeerUpdate(String input, int pieceIndex, int totalPieces, String id){
        String sending = String.format("@PT_%s_%s_%s@", id, pieceIndex, totalPieces) + input;
        return sending;
    }

    public String getUserId() {
        return _userId;
    }

    public void setGameStarted(boolean gameStarted){
        if(gameStarted){
            this.gameStarted = true;
            for(Runnable runnable : onGameStartedRunnables){
                runnable.run();
            }
            onGameStartedRunnables.clear();
        }
    }

    public void setPausing(boolean pause){
        if(_pausing != pause){
            if(!pause){
                _coordinator.userConnectionChanged(_userId, true);
                sendUpdate(-1, "ConnectionRecoverd", false, "");
            }
            else{
                _coordinator.userConnectionChanged(_userId, false);
                sendUpdate(-1, "DisconnectedButRecoverable", false, "");
            }
            _pausing = pause;
        }

    }

    public class WarpListeners implements ConnectionRequestListener, RoomRequestListener, ZoneRequestListener, NotifyListener, UpdateRequestListener {

        @Override
        public void onConnectDone(ConnectEvent connectEvent) {
            System.out.println("onConnectDone: " + connectEvent);
            if (connectEvent.getResult() == WarpResponseResultCode.SUCCESS) {
                _warpInstance.getRoomInRange(1, 100);
            }
            else if(connectEvent.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
                _coordinator.userConnectionChanged(_userId, false);
            }
            else if(connectEvent.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
                _coordinator.userConnectionChanged(_userId, true);
            }
        }


        @Override
        public void onJoinAndSubscribeRoomDone(RoomEvent roomEvent) {
            System.out.println("onJoinRoomDone: " + roomEvent.getData().getId());
            _warpInstance.getLiveRoomInfo(roomEvent.getData().getId());
        }


        @Override
        public void onGetMatchedRoomsDone(MatchedRoomsEvent matchedRoomsEvent) {
            System.out.println("onGetMatchedRoomsDone");
            RoomData[] rooms = matchedRoomsEvent.getRoomsData();
            if(rooms.length > 0){
                _warpInstance.joinAndSubscribeRoom(rooms[0].getId());
            }
            else{
                _warpInstance.createRoom("temp", "temp", 100, null);
            }
        }

        @Override
        public void onGetAllRoomsCountDone(AllRoomsEvent allRoomsEvent) {

        }

        @Override
        public void onGetOnlineUsersCountDone(AllUsersEvent allUsersEvent) {

        }

        @Override
        public void onGetUserStatusDone(LiveUserInfoEvent liveUserInfoEvent) {

        }

        @Override
        public void onCreateRoomDone(RoomEvent roomEvent) {
            System.out.println("onCreateRoomDone");
            _warpInstance.joinAndSubscribeRoom(roomEvent.getData().getId());
        }

        @Override
        public void onUpdatePeersReceived(final UpdateEvent updateEvent) {
            if(_pausing){
                return;
            }

            System.out.println("onUpdatePeersReceived");
            String update = new String(updateEvent.getUpdate());
            if(update.startsWith("@PT")){
                unAppendDataFromPeerMsg(update);
            }
            else{
                updateRoomMatesReceived(update);
            }
        }

        @Override
        public void onPrivateUpdateReceived(final String s, final byte[] bytes, final boolean b) {
            if(_pausing){
                return;
            }

            if(!gameStarted){
                onGameStartedRunnables.add(new Runnable() {
                    @Override
                    public void run() {
                        onPrivateUpdateReceived(s, bytes, b);
                    }
                });
                return;
            }

            System.out.println("onPrivateUpdateReceived");
            String update = new String(bytes);
            if(update.startsWith("@PT")){
                unAppendDataFromPeerMsg(update);
            }
            else{
                updateRoomMatesReceived(update);
            }
        }

        private void updateRoomMatesReceived(final String update){
            try {

                if(!gameStarted){
                    onGameStartedRunnables.add(new Runnable() {
                        @Override
                        public void run() {
                            updateRoomMatesReceived(update);
                        }
                    });
                    return;
                }


                final JSONObject updateJson = new JSONObject(update);
                int code = updateJson.getInt("code");

                if(code == 0){
                    _coordinator.getGameDataHelper().receivedGameData(updateJson.getString("msg"));
                }
                else if(code == 1){
                    _coordinator.getDecisionsMaker().receivedDecisionMakerUpdate(updateJson.getString("msg"));
                }
                else{
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
                    else if(updateJson.getString("msg").equals("ConnectionRecoverd") || updateJson.getString("msg").equals("DisconnectedButRecoverable")){
                        handleConnectionResiliencyMock(updateJson);
                    }
                    else{
                        _coordinator.receivedRoomUpdate(updateJson.getString("msg"), updateJson.getString("userId"));
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void handleConnectionResiliencyMock(JSONObject updateJson){
            try {
                if(!updateJson.getString("userId").equals(_userId)){
                    if(updateJson.getString("msg").equals("ConnectionRecoverd")){
                        _coordinator.userConnectionChanged(updateJson.getString("userId"), true);
                    }
                    else{
                        _coordinator.userConnectionChanged(updateJson.getString("userId"), false);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private synchronized void unAppendDataFromPeerMsg(String msg){
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

                int slotIndex = 0;
                for(String user : users) {
                    team.addPlayer(new Player(user, user, isHost, slotIndex));
                    isHost = false;
                    i++;
                    slotIndex++;
                    if(i == _eachTeamExpectedPlayers){
                        teams.add(team);
                        team = new Team();
                        i=0;
                    }
                }
                _coordinator.setMyUserId(_userId);
                _coordinator.setTeams(teams);
                //only run team changed once
                if(_onReady != null){
                    _coordinator.getGameDataHelper().teamsInit(teams);
                    _coordinator.getDecisionsMaker().teamsInit(teams);
                }
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
            if(!s.equals(_userId)){
                System.out.println("onUserJoinedRoom");
                _warpInstance.getLiveRoomInfo(roomData.getId());
                Threadings.delay(5000, new Runnable() {
                    @Override
                    public void run() {
                        _coordinator.userConnectionChanged(s, true);
                    }
                });
            }
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
        public void onUserPaused(String s, boolean b, String s1) {
            _coordinator.userConnectionChanged(s, false);
        }

        @Override
        public void onUserResumed(String s, boolean b, String s1) {
            _coordinator.userConnectionChanged(s, true);
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
        public void onLeaveAndUnsubscribeRoomDone(RoomEvent roomEvent) {

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
        public void onNextTurnRequest(String s) {

        }

        @Override
        public void onSendPrivateUpdateDone(byte b) {

        }

        @Override
        public void onJoinRoomDone(RoomEvent roomEvent) {

        }


    }
}
