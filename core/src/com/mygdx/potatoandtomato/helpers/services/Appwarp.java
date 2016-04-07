package com.mygdx.potatoandtomato.helpers.services;

import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.potatoandtomato.common.utils.JsonObj;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.utils.MultiHashMap;
import com.potatoandtomato.common.utils.Strings;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class Appwarp extends GamingKit implements ConnectionRequestListener, ZoneRequestListener, RoomRequestListener, NotifyListener {

    private WarpClient _warpInstance;
    private String _appKey = Terms.WARP_API_KEY;
    private String _secretKey = Terms.WARP_SECRET_KEY;
    private String _username, _realUsername, _roomId;
    private MultiHashMap<String, String> _msgPieces;

    public Appwarp(String appKey, String secretKey){
        _appKey = appKey;
        _secretKey = secretKey;
        _msgPieces = new MultiHashMap();
        init();
    }

    public Appwarp() {
        _msgPieces = new MultiHashMap();
       init();
    }

    public void init(){
        int result =  WarpClient.initialize(_appKey, _secretKey);
        //WarpClient.setRecoveryAllowance(120);
        try {
            _warpInstance = WarpClient.getInstance();

            reflectionClearListeners();

            _warpInstance.addConnectionRequestListener(this);
            _warpInstance.addZoneRequestListener(this);
            _warpInstance.addRoomRequestListener(this);
            _warpInstance.addNotificationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reflectionClearListeners(){
        try {
            Field field = WarpClient.class.getDeclaredField("ConnectionRequestListeners");
            field.setAccessible(true);
            Set<ConnectionRequestListener> value = (Set<ConnectionRequestListener>) field.get(_warpInstance);
            value.clear();

            Field field1 = WarpClient.class.getDeclaredField("zoneRequestListeners");
            field1.setAccessible(true);
            Set<ZoneRequestListener> value1 = (Set<ZoneRequestListener>) field1.get(_warpInstance);
            value1.clear();

            Field field2 = WarpClient.class.getDeclaredField("roomRequestListeners");
            field2.setAccessible(true);
            Set<RoomRequestListener> value2 = (Set<RoomRequestListener>) field2.get(_warpInstance);
            value2.clear();

            Field field3 = WarpClient.class.getDeclaredField("notifyListeners");
            field3.setAccessible(true);
            Set<NotifyListener> value3 = (Set<NotifyListener>) field3.get(_warpInstance);
            value3.clear();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void connect(Profile user) {
        _realUsername = user.getUserId();
        _username = user.getUserId().replace("-", "") + "_" + System.currentTimeMillis();
        _warpInstance.connectWithUserName(_username);
    }

    @Override
    public void disconnect() {
        _warpInstance.disconnect();
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

        String toSend = data.getJSONObject().toString();
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

    private String appendDataToPeerUpdate(String input, int pieceIndex, int totalPieces, String id){
        String sending = String.format("@PT_%s_%s_%s@", id, pieceIndex, totalPieces) + input;
        return sending;
    }

    @Override
    public void dispose() {
        _warpInstance.removeConnectionRequestListener(this);
        _warpInstance.removeZoneRequestListener(this);
        _warpInstance.removeRoomRequestListener(this);
        _warpInstance.removeNotificationListener(this);
        _warpInstance.disconnect();
        _warpInstance = null;
    }

    @Override
    public void sendRoomMessage(ChatMessage msg) {
        try {
            JsonObj data = new JsonObj();
            msg.setSenderId(_realUsername);
            ObjectMapper mapper = new ObjectMapper();
            data.put("msg", mapper.writeValueAsString(msg));
            _warpInstance.sendChat(data.getJSONObject().toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectDone(ConnectEvent connectEvent) {
        if (connectEvent.getResult() == WarpResponseResultCode.SUCCESS ){
            onConnectionChanged(true);
        }
        else if (connectEvent.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED ){
            onConnectionChanged(true);
        }

//        else if (connectEvent.getResult() == WarpResponseResultCode .CONNECTION_ERROR_RECOVERABLE ){
//            System.out.println("Try connect again.");
//            Threadings.delay(5000, new Runnable() {
//                @Override
//                public void run() {
//                    _warpInstance.RecoverConnection();
//                }
//            });
//        }
        else {
            onConnectionChanged(false);
        }
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {
        //onConnectionChanged(false);
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
        unAppendDataFromPeerMsg(new String(updateEvent.getUpdate()));
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
                JsonObj jsonObj = new JsonObj(realMsg);
                onUpdateRoomMatesReceived(jsonObj.getInt("code"), jsonObj.getString("msg"), jsonObj.getString("realUsername"));
            }
            else{
                if(!_msgPieces.containsKey(id)){
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
                        JsonObj jsonObj = new JsonObj(Strings.joinArr(currentPieces, ""));
                        onUpdateRoomMatesReceived(jsonObj.getInt("code"), jsonObj.getString("msg"), jsonObj.getString("realUsername"));
                        _msgPieces.remove(id);
                    }
                }
            }
        }
        else{
            JsonObj jsonObj = new JsonObj(msg);
            onUpdateRoomMatesReceived(jsonObj.getInt("code"), jsonObj.getString("msg"), jsonObj.getString("realUsername"));
        }
    }

    @Override
    public void onChatReceived(ChatEvent chatEvent) {
        try {
            JsonObj jsonObj = new JsonObj(chatEvent.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            ChatMessage chatMessage = mapper.readValue(jsonObj.getString("msg"), ChatMessage.class);
            onRoomMessageReceived(chatMessage, chatMessage.getSenderId());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
