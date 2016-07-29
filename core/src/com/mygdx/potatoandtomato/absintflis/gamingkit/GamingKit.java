package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class GamingKit {

    private ConcurrentHashMap<String, ConnectionChangedListener> _connectionChangedListeners;
    private ConcurrentHashMap<String, JoinRoomListener> _joinRoomListeners;
    private ConcurrentHashMap<String, UpdateRoomMatesListener> _updateRoomMatesListeners;
    private ConcurrentHashMap<String, MessagingListener> _messagingListeners;
    private ConcurrentHashMap<String, RoomInfoListener> _roomInfoListeners;
    private ConcurrentHashMap<String, LockPropertyListener> _lockPropertyListeners;


    public GamingKit() {
        _connectionChangedListeners = new ConcurrentHashMap();
        _joinRoomListeners = new ConcurrentHashMap();
        _updateRoomMatesListeners = new ConcurrentHashMap();
        _messagingListeners = new ConcurrentHashMap();
        _roomInfoListeners = new ConcurrentHashMap();
        _lockPropertyListeners = new ConcurrentHashMap();
    }

    public void addListener(String classTag, Object listener){
        if(listener instanceof ConnectionChangedListener){
            _connectionChangedListeners.put(classTag, (ConnectionChangedListener) listener);
        }
        else if(listener instanceof JoinRoomListener){
            _joinRoomListeners.put(classTag, (JoinRoomListener) listener);
        }
        else if(listener instanceof UpdateRoomMatesListener){
            _updateRoomMatesListeners.put(classTag, (UpdateRoomMatesListener) listener);
        }
        else if(listener instanceof MessagingListener){
            _messagingListeners.put(classTag, (MessagingListener) listener);
        }
        else if(listener instanceof RoomInfoListener){
            _roomInfoListeners.put(classTag, (RoomInfoListener) listener);
        }
        else if(listener instanceof LockPropertyListener){
            _lockPropertyListeners.put(classTag, (LockPropertyListener) listener);
        }
    }

    public void removeListenersByClassTag(String classTag){
        _connectionChangedListeners.remove(classTag);
        _joinRoomListeners.remove(classTag);
        _updateRoomMatesListeners.remove(classTag);
        _messagingListeners.remove(classTag);
        _roomInfoListeners.remove(classTag);
        _lockPropertyListeners.remove(classTag);
    }

    public void removeLockPropertyListenersByClassTag(String classTag){
        _lockPropertyListeners.remove(classTag);
    }


    public void onConnectionChanged(String userId, ConnectionChangedListener.ConnectStatus status){
        if(userId != null){
            for(ConnectionChangedListener listener : _connectionChangedListeners.values()){
                listener.onChanged(userId, status);
            }
        }
    }

    public void onRoomJoined(final String roomId){
        for(JoinRoomListener listener : _joinRoomListeners.values()){
            listener.onRoomJoined(roomId);
        }
    }

    public void onJoinRoomFail(){
        for(JoinRoomListener listener : _joinRoomListeners.values()){
            listener.onJoinRoomFailed();
        }
    }

    public void onUpdateRoomMatesReceived(final int code, final String msg, final String senderId){
        for(UpdateRoomMatesListener listener : _updateRoomMatesListeners.values()){
            listener.onUpdateRoomMatesReceived(code, msg, senderId);
        }
    }

    public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId){
        for(UpdateRoomMatesListener listener : _updateRoomMatesListeners.values()){
            listener.onUpdateRoomMatesReceived(identifier, data, senderId);
        }
    }

    public void onRoomMessageReceived(final ChatMessage msg, final String senderId){
        for(MessagingListener listener : _messagingListeners.values()) {
            listener.onRoomMessageReceived(msg, senderId);
        }
    }

    public void onRoomInfoReceivedSuccess(String identifier, String roomId, String[] inRoomUserIds){
        for(RoomInfoListener listener : _roomInfoListeners.values()) {
            if(listener.getRoomId().equals(roomId) && listener.getClassTag().equals(identifier)){
                listener.onRoomInfoRetrievedSuccess(inRoomUserIds);
            }
        }
    }

    public void onRoomInfoReceivedFailed(String identifier, String roomId){
        for(RoomInfoListener listener : _roomInfoListeners.values()) {
            if(listener.getRoomId().equals(roomId) && listener.getClassTag().equals(identifier)){
                listener.onRoomInfoFailed();
            }
        }
    }

    public void onLockPropertySuccess(String property){
        for(LockPropertyListener listener : _lockPropertyListeners.values()) {
            if(listener.getProperty().equals(property)){
                listener.onLockSucceed();
            }
        }
    }

    public void onUnlockPropertySuccess(String property){
        for(LockPropertyListener listener : _lockPropertyListeners.values()) {
            if(listener.getProperty().equals(property)){
                listener.onUnLockSucceed();
            }
        }
    }

    public ConcurrentHashMap<String, ConnectionChangedListener> getConnectionChangedListeners() {
        return _connectionChangedListeners;
    }

    public ConcurrentHashMap<String, JoinRoomListener> getJoinRoomListeners() {
        return _joinRoomListeners;
    }

    public ConcurrentHashMap<String, UpdateRoomMatesListener> getUpdateRoomMatesListeners() {
        return _updateRoomMatesListeners;
    }

    public abstract void connect(Profile user);

    public abstract void disconnect();

    public abstract void createAndJoinRoom();

    public abstract void sendRoomMessage(ChatMessage msg);

    public abstract void joinRoom(String roomId);

    public abstract void leaveRoom();

    public abstract void getRoomInfo(String roomId, String identifier);

    public abstract void updateRoomMates(int updateRoomMatesCode, String msg);

    public abstract void privateUpdateRoomMates(String toUserId, int updateRoomMatesCode, String msg);

    public abstract void updateRoomMates(byte identifier, byte[] bytes);

    public abstract void privateUpdateRoomMates(String toUserId, byte identifier, byte[] bytes);

    public abstract void lockProperty(String key, String value);

    public abstract void unLockProperty(String key);

    public abstract void recoverConnection();

    public abstract void dispose();

    public abstract long getMsgSentCount();

}
