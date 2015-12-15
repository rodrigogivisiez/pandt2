package com.mygdx.potatoandtomato.helpers.services;

import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class Appwarp extends GamingKit implements ConnectionRequestListener, ZoneRequestListener, RoomRequestListener {

    private WarpClient _warpInstance;
    private String _appKey = "08e25748189dccf0d82070e17c87225350614c754e8e0d511128d65da9d27956";
    private String _secretKey = "ed573d5aa22d343d8b187e610007f299c9811bd3594c94d8ffe3f789a69de960";
    private String _username, _roomId;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(String username) {
        _username = username + "_" + System.currentTimeMillis();
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
}
