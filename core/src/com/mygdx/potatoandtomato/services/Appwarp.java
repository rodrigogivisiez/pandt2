package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.utils.*;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.shaded.fasterxml.jackson.core.JsonProcessingException;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;
import com.shephertz.app42.gaming.multiplayer.client.ConnectionState;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class Appwarp extends GamingKit implements ChatRequestListener, ConnectionRequestListener, ZoneRequestListener, RoomRequestListener, NotifyListener {

    private WarpClient _warpInstance;
    private String _appKey = Terms.WARP_API_KEY();
    private String _secretKey = Terms.WARP_SECRET_KEY();
    private String _username, _realUsername, _roomId;
    private ConcurrentHashMap<String, String> _userIdToEncodeUserIdMap;
    private MultiHashMap<String, String> _msgPieces;
    private MultiHashMap<String, byte[]> _bytePieces;
    private ConcurrentHashMap<String, String> _bytePiecesOwner;
    private String _gettingRoomId, _gettingRoomIdentifier;
    private String _lockingProperty, _unlockingProperty;

    public Appwarp(String appKey, String secretKey){
        _appKey = appKey;
        _secretKey = secretKey;
        _msgPieces = new MultiHashMap();
        _bytePieces = new MultiHashMap();
        _bytePiecesOwner = new ConcurrentHashMap();
        _userIdToEncodeUserIdMap = new ConcurrentHashMap();
        init();
    }

    public Appwarp() {
        _msgPieces = new MultiHashMap();
        _bytePieces = new MultiHashMap();
        _bytePiecesOwner = new ConcurrentHashMap();
        _userIdToEncodeUserIdMap = new ConcurrentHashMap();
       init();
    }

    public void init(){
        int result =  WarpClient.initialize(_appKey, _secretKey);
        try {
            _warpInstance = WarpClient.getInstance();
            _warpInstance.setRecoveryAllowance(30);
            WarpClient.enableTrace(true);
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
    public void recoverConnection() {
        _warpInstance.RecoverConnection();
    }

    @Override
    public void connect(Profile user) {
        if(_warpInstance != null){
            _realUsername = user.getUserId();
            _username = encodeUserId(user.getUserId());
            _warpInstance.connectWithUserName(_username);
        }
    }

    @Override
    public void disconnect() {
        if(_warpInstance != null && _warpInstance.getConnectionState() == ConnectionState.CONNECTED){
            _warpInstance.disconnect();
        }
    }

    @Override
    public void getRoomInfo(String roomId, String identifier) {
        _gettingRoomId = roomId;
        _gettingRoomIdentifier = identifier;
        _warpInstance.getLiveRoomInfo(roomId);
    }

    @Override
    public void createAndJoinRoom() {
        _warpInstance.createRoom("ptgame", _username, 99, null);
    }

    @Override
    public void joinRoom(String roomId) {
        _roomId = roomId;
        _userIdToEncodeUserIdMap.clear();
        _warpInstance.joinAndSubscribeRoom(_roomId);
    }

    @Override
    public void leaveRoom() {
        _warpInstance.leaveAndUnsubscribeRoom(_roomId);
        _roomId = null;
    }

    @Override
    public void updateRoomMates(int updateRoomMatesCode, String msg) {
        ArrayList<String> results = packUpdateMessage(updateRoomMatesCode, msg);

        if(results.size() > 1){
            String id = Strings.generateUniqueRandomKey(20);
            for(int i = 0; i < results.size(); i++){
                _warpInstance.sendUpdatePeers(appendDataToPeerUpdate(results.get(i), i, results.size(), id).getBytes());
            }
        }
        else{
            for(int i = 0; i < results.size(); i++){
                _warpInstance.sendUpdatePeers(results.get(i).getBytes());
            }
        }
    }

    @Override
    public void privateUpdateRoomMates(String toUserId, int updateRoomMatesCode, String msg) {
        if(_userIdToEncodeUserIdMap.containsKey(toUserId)){
            ArrayList<String> results = packUpdateMessage(updateRoomMatesCode, msg);

            if(results.size() > 1){
                String id = Strings.generateUniqueRandomKey(20);
                for(int i = 0; i < results.size(); i++){
                    _warpInstance.sendPrivateUpdate(_userIdToEncodeUserIdMap.get(toUserId),
                            appendDataToPeerUpdate(results.get(i), i, results.size(), id).getBytes());
                }
            }
            else{
                for(int i = 0; i < results.size(); i++){
                    _warpInstance.sendPrivateUpdate(_userIdToEncodeUserIdMap.get(toUserId),
                            results.get(i).getBytes());
                }
            }
        }
    }

    private  ArrayList<String> packUpdateMessage(int updateRoomMatesCode, String msg){
        ArrayList<String> results = new ArrayList();
        JsonObj data = new JsonObj();
        data.put("code", updateRoomMatesCode);
        data.put("msg", msg);
        data.put("realUsername", _realUsername);

        String toSend = data.getJSONObject().toString();
        if(toSend.length() > 900){
            results = Strings.split(toSend, 900);
        }
        else{
            results.add(toSend);
        }
        return results;
    }

    @Override
    public void updateRoomMates(byte identifier, byte[] bytes) {
        ArrayList<byte[]> finalToSendBytes = packUpdateBytes(identifier, bytes);
        for(byte[] toSend : finalToSendBytes){
            _warpInstance.sendUpdatePeers(toSend);
        }

        Logs.show("Sending bytes chunks of size: " + finalToSendBytes.size());
    }

    @Override
    public void privateUpdateRoomMates(String toUserId, byte identifier, byte[] bytes) {
        if(_userIdToEncodeUserIdMap.containsKey(toUserId)){
            ArrayList<byte[]> finalToSendBytes = packUpdateBytes(identifier, bytes);
            for(byte[] toSend : finalToSendBytes){
                _warpInstance.sendPrivateUpdate(_userIdToEncodeUserIdMap.get(toUserId), toSend);
            }
        }
    }

    private ArrayList<byte[]> packUpdateBytes(byte identifier, byte[] bytes){
        int chunkSize = 900;
        String unique = Strings.generateUniqueRandomKey(20);
        byte[] uniqueArr = unique.getBytes();

        List<byte[]> toSendBytes = ArrayUtils.divideArray(bytes, chunkSize);
        ArrayList<byte[]> finalToSendBytes = new ArrayList();
        int i = 0;
        for(int q = 0; q < toSendBytes.size(); q++){
            byte[] data = new byte[6];
            data[0] = 98;
            data[1] = 40;
            data[2] = 25;
            data[3] = identifier;
            data[4] = (byte) (toSendBytes.size() + 1);        //plus one for meta
            data[5] = (byte) i;

            byte[] bytes1;
            if(i == 0){
                String username = _realUsername;
                if(username == null) return new ArrayList();
                bytes1 = username.getBytes();
                q = -1;
            }
            else{
                bytes1 = toSendBytes.get(q);
            }
            byte[] finalBytes = ArrayUtils.concatAll(data, uniqueArr, bytes1);
            finalToSendBytes.add(finalBytes);
            i++;
        }
        return finalToSendBytes;
    }


    @Override
    public void lockProperty(String key, String value) {
        _lockingProperty = key;
        HashMap<String, Object> table = new HashMap();
        table.put(key, value);
        _warpInstance.lockProperties(table);
    }

    @Override
    public void unLockProperty(String key) {
        _unlockingProperty = key;
        String[] keys = new String[]{key};
        _warpInstance.unlockProperties(keys);
    }

    public boolean checkIsBytesUpdate(byte[] data){
        try{
            if(data.length > 6){
                if(data[0] == 98 && data[1] == 40 && data[2] == 25){
                    byte identifier = data[3];
                    byte totalPieces = data[4];
                    byte pieceIndex = data[5];
                    byte[] unique = Arrays.copyOfRange(data, 6, 26);
                    String uniqueString = new String(unique);

                    if(pieceIndex == 0){
                        byte[] userId = Arrays.copyOfRange(data, 26, data.length);
                        String userIdString = new String(userId);
                        _bytePiecesOwner.put(uniqueString, userIdString);
                        _bytePieces.put(uniqueString, new byte[]{});
                    }
                    else{
                        byte[] bytePiece =  Arrays.copyOfRange(data, 26, data.length);

                        if(!_bytePieces.containsKey(uniqueString)){
                            _bytePieces.put(uniqueString, bytePiece);
                        }
                        else{
                            ArrayList<byte[]> currentPieces = _bytePieces.get(uniqueString);
                            if(pieceIndex > currentPieces.size()){
                                currentPieces.add(currentPieces.size(), bytePiece);
                            }
                            else{
                                currentPieces.add(pieceIndex, bytePiece);
                            }

                            if(currentPieces.size() == totalPieces){
                                byte[] result = new byte[]{};
                                for(byte[] bytes : currentPieces){
                                    result = ArrayUtils.concatenate(result, bytes);
                                }

                                onUpdateRoomMatesReceived(identifier, result, _bytePiecesOwner.get(uniqueString));
                                _bytePieces.remove(uniqueString);
                                _bytePiecesOwner.remove(uniqueString);
                            }
                        }
                    }



                    return true;
                }
            }
            return false;
        }
        catch (IndexOutOfBoundsException ex){
            return false;
        }
    }

    private String appendDataToPeerUpdate(String input, int pieceIndex, int totalPieces, String id){
        String sending = String.format("@PT_%s_%s_%s@", id, pieceIndex, totalPieces) + input;
        return sending;
    }

    @Override
    public void dispose() {
        if(_warpInstance != null){
            _warpInstance.removeConnectionRequestListener(this);
            _warpInstance.removeZoneRequestListener(this);
            _warpInstance.removeRoomRequestListener(this);
            _warpInstance.removeNotificationListener(this);
            _warpInstance.disconnect();
        }

        _warpInstance = null;
    }

    @Override
    //not using anymore, since it cant send chinese characters, now using update peers instead
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
            onConnectionChanged(_realUsername, ConnectionChangedListener.ConnectStatus.CONNECTED);
        }
        else if (connectEvent.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
            onConnectionChanged(_realUsername, ConnectionChangedListener.ConnectStatus.CONNECTED_FROM_RECOVER);
        }
        else if (connectEvent.getResult() == WarpResponseResultCode .CONNECTION_ERROR_RECOVERABLE ){
            onConnectionChanged(_realUsername, ConnectionChangedListener.ConnectStatus.DISCONNECTED_BUT_RECOVERABLE);
        }
        else {
            onConnectionChanged(_realUsername, ConnectionChangedListener.ConnectStatus.DISCONNECTED);
        }
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {

    }

    @Override
    public void onUserLeftRoom(RoomData roomData, String s) {
        if(!decodeUserId(s).equals(_realUsername)){
            if(checkAndSaveEncodeUserId(s)){
                onConnectionChanged(decodeUserId(s), ConnectionChangedListener.ConnectStatus.DISCONNECTED);
            }
        }
    }

    @Override
    public void onUserJoinedRoom(RoomData roomData, String s) {
        if(!s.equals(_username)){
            if(checkAndSaveEncodeUserId(s)){
                onConnectionChanged(decodeUserId(s), ConnectionChangedListener.ConnectStatus.CONNECTED);
            }
        }
    }

    @Override
    public void onUserPaused(String s, boolean b, String s1) {
        if(checkAndSaveEncodeUserId(s1)){
            onConnectionChanged(decodeUserId(s1), ConnectionChangedListener.ConnectStatus.DISCONNECTED_BUT_RECOVERABLE);
        }
    }

    @Override
    public void onUserResumed(String s, boolean b, String s1) {
        if(!s1.equals(_username)){
            if(checkAndSaveEncodeUserId(s1)){
                onConnectionChanged(decodeUserId(s1), ConnectionChangedListener.ConnectStatus.CONNECTED_FROM_RECOVER);
            }
        }
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
    public void onJoinAndSubscribeRoomDone(RoomEvent roomEvent) {
        if(roomEvent.getResult() == 0) {
            onRoomJoined(roomEvent.getData().getId());
        }
        else{
            onJoinRoomFail();
        }
    }

    @Override
    public void onLeaveAndUnsubscribeRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        if(liveRoomInfoEvent.getResult() == 0){
            String[] users = liveRoomInfoEvent.getJoinedUsers();
            String[] decodeUsers = new String[0];
            if(users != null){
                decodeUsers = new String[users.length];
                for(int i = 0; i < users.length; i++){
                    decodeUsers[i] = decodeUserId(users[i]);
                }
                for(String userId : users){
                    checkAndSaveEncodeUserId(userId);
                }
            }

            onRoomInfoReceivedSuccess(_gettingRoomIdentifier, liveRoomInfoEvent.getData().getId(), decodeUsers);
        }
        else{
            onRoomInfoReceivedFailed(_gettingRoomIdentifier, _gettingRoomId);
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
        unpackUpdateMessage(updateEvent.getUpdate());
    }

    @Override
    public void onPrivateUpdateReceived(String s, byte[] bytes, boolean b) {
        checkAndSaveEncodeUserId(s);
        unpackUpdateMessage(bytes);
    }

    private void unpackUpdateMessage(byte[] bytes){
        if(!checkIsBytesUpdate(bytes)){
            String msg = new String(bytes);
            if(msg.startsWith("@PT")){
                unAppendDataFromPeerMsg(new String(bytes));
            }
            else{
                JsonObj jsonObj = new JsonObj(msg);
                onUpdateRoomMatesReceived(jsonObj.getInt("code"), jsonObj.getString("msg"), jsonObj.getString("realUsername"));
            }
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
        Logs.show("Received appwarp update of total pieces: " + totalPieces);

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

    @Override
    public void onChatReceived(ChatEvent chatEvent) {
        try {
            checkAndSaveEncodeUserId(chatEvent.getSender());
            JsonObj jsonObj = new JsonObj(chatEvent.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            ChatMessage chatMessage = mapper.readValue(jsonObj.getString("msg"), ChatMessage.class);
            onRoomMessageReceived(chatMessage, chatMessage.getSenderId());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLockPropertiesDone(byte b) {
        if(b == 0){
            onLockPropertySuccess(_lockingProperty);
            _lockingProperty = "";
        }
    }

    @Override
    public void onUnlockPropertiesDone(byte b) {
        if(b == 0){
            onUnlockPropertySuccess(_unlockingProperty);
            _unlockingProperty = "";
        }
    }

    private String encodeUserId(String userId){
        return userId + "_(pandt)_" + System.currentTimeMillis();
    }

    private String decodeUserId(String userId){
        String[] tmp = decodeUserIdToArr(userId);
        if(tmp.length > 0){
            return tmp[0];
        }
        else{
            return "";
        }
    }

    private String[] decodeUserIdToArr(String userId){
        String[] tmp = userId.split("_\\(pandt\\)_");
        return tmp;
    }

    private boolean checkAndSaveEncodeUserId(String encodedUserId){
        String[] newDecodedArr = decodeUserIdToArr(encodedUserId);
        if(newDecodedArr.length > 1){
            if(_userIdToEncodeUserIdMap.containsKey(newDecodedArr[0])){
                String[] oldDecodedArr = decodeUserIdToArr(_userIdToEncodeUserIdMap.get(newDecodedArr[0]));
                if(Long.valueOf(newDecodedArr[1]) >= Long.valueOf(oldDecodedArr[1])){
                    _userIdToEncodeUserIdMap.put(newDecodedArr[0], encodedUserId);
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                _userIdToEncodeUserIdMap.put(newDecodedArr[0], encodedUserId);
                return true;
            }
        }
        else{
            return false;
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
    public void onGetAllRoomsCountDone(AllRoomsEvent allRoomsEvent) {

    }

    @Override
    public void onGetOnlineUsersCountDone(AllUsersEvent allUsersEvent) {

    }

    @Override
    public void onGetUserStatusDone(LiveUserInfoEvent liveUserInfoEvent) {

    }


    @Override
    public void onSetCustomRoomDataDone(LiveRoomInfoEvent liveRoomInfoEvent) {

    }

    @Override
    public void onUpdatePropertyDone(LiveRoomInfoEvent liveRoomInfoEvent) {

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
    public void onPrivateChatReceived(String s, String s1) {

    }

    @Override
    public void onUserChangeRoomProperty(RoomData roomData, String userName, HashMap<String, Object> tableProperties, HashMap<String, String> lockProperties) {

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
    public void onJoinRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onSubscribeRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onSendChatDone(byte b) {
        System.out.println(b);
    }

    @Override
    public void onSendPrivateChatDone(byte b) {

    }

    @Override
    public void onGetChatHistoryDone(byte b, ArrayList<ChatEvent> arrayList) {

    }
}
