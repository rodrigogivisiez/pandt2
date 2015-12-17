package com.mygdx.potatoandtomato.helpers.services;

import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.helpers.utils.JsonObj;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.*;

import java.util.HashMap;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class Appwarp extends GamingKit implements ConnectionRequestListener, ZoneRequestListener, RoomRequestListener, NotifyListener {

    private WarpClient _warpInstance;
    private String _appKey = "08e25748189dccf0d82070e17c87225350614c754e8e0d511128d65da9d27956";
    private String _secretKey = "ed573d5aa22d343d8b187e610007f299c9811bd3594c94d8ffe3f789a69de960";
    private String _username, _realUsername, _roomId;

    public Appwarp(String appKey, String secretKey){
        _appKey = appKey;
        _secretKey = secretKey;
        init();
    }

    public Appwarp() {
       init();
    }

    public void init(){
        WarpClient.initialize(_appKey, _secretKey);
        WarpClient.setRecoveryAllowance(120);
        try {
            _warpInstance = WarpClient.getInstance();
            _warpInstance.addConnectionRequestListener(this);
            _warpInstance.addZoneRequestListener(this);
            _warpInstance.addRoomRequestListener(this);
            _warpInstance.addNotificationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(String username) {
        _realUsername = username;
        _username = username + "_warp!" + System.currentTimeMillis();
        _warpInstance.connectWithUserName(_username);
    }

    @Override
    public void createAndJoinRoom() {
        _warpInstance.createRoom("ptgame", _username, 99, null);
    }

    @Override
    public void joinRoom(String roomId) {
        _roomId = roomId;
        _warpInstance.subscribeRoom(_roomId);
    }

    @Override
    public void leaveRoom() {
        _warpInstance.unsubscribeRoom(_roomId);
        _warpInstance.leaveRoom(_roomId);
        _roomId = null;
    }

    @Override
    public void updateRoomMates(int updateRoomMatesCode, String msg) {
        JsonObj data = new JsonObj();
        data.put("code", updateRoomMatesCode);
        data.put("msg", msg);
        data.put("realUsername", _realUsername);
        _warpInstance.sendUpdatePeers(data.getJSONObject().toString().getBytes());
    }

    @Override
    public void onConnectDone(ConnectEvent connectEvent) {
        onConnectionChanged(connectEvent.getResult() == 0);
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {
        onConnectionChanged(false);
    }

    @Override
    public void onCreateRoomDone(RoomEvent roomEvent) {
        if(roomEvent.getResult() == 0){
            joinRoom(roomEvent.getData().getId());
        }
        else{
            onJoinRoomFail();
        }
    }

    @Override
    public void onSubscribeRoomDone(RoomEvent roomEvent) {
        if(roomEvent.getResult() == 0){
            _warpInstance.joinRoom(roomEvent.getData().getId());
        }
        else{
            onJoinRoomFail();
        }
    }

    @Override
    public void onJoinRoomDone(RoomEvent roomEvent) {
        if(roomEvent.getResult() == 0) {
            onRoomJoined(roomEvent.getData().getId());
        }
        else{
            onJoinRoomFail();
        }
    }

    @Override
    public void onUnSubscribeRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onLeaveRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onUpdatePeersReceived(UpdateEvent updateEvent) {
        JsonObj jsonObj = new JsonObj(new String(updateEvent.getUpdate()));
        onUpdateRoomMatesReceived(jsonObj.getInt("code"), jsonObj.getString("msg"), jsonObj.getString("realUsername"));
    }




























    @Override
    public void onInitUDPDone(byte b) {

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
    public void onGetLiveUserInfoDone(LiveUserInfoEvent liveUserInfoEvent) {

    }

    @Override
    public void onSetCustomUserDataDone(LiveUserInfoEvent liveUserInfoEvent) {

    }

    @Override
    public void onGetMatchedRoomsDone(MatchedRoomsEvent matchedRoomsEvent) {

    }


    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {

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
    public void onRoomCreated(RoomData roomData) {

    }

    @Override
    public void onRoomDestroyed(RoomData roomData) {

    }

    @Override
    public void onUserLeftRoom(RoomData roomData, String s) {

    }

    @Override
    public void onUserJoinedRoom(RoomData roomData, String s) {

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

}
