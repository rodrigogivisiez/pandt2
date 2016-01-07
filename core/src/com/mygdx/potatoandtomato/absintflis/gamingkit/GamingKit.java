package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.badlogic.gdx.Gdx;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.Broadcaster;

import java.util.HashMap;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class GamingKit {

    private HashMap<String, ConnectionChangedListener> _connectionChangedListeners;
    private HashMap<String, JoinRoomListener> _joinRoomListeners;
    private HashMap<String, UpdateRoomMatesListener> _updateRoomMatesListeners;
    private HashMap<String, MessagingListener> _messagingListeners;

    public GamingKit() {
        _connectionChangedListeners = new HashMap();
        _joinRoomListeners = new HashMap();
        _updateRoomMatesListeners = new HashMap();
        _messagingListeners = new HashMap();
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
    }

    public void removeListenersByClassTag(String classTag){
        _connectionChangedListeners.remove(classTag);
        _joinRoomListeners.remove(classTag);
        _updateRoomMatesListeners.remove(classTag);
        _messagingListeners.remove(classTag);
    }


    public void onConnectionChanged(final boolean connected){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(ConnectionChangedListener listener : _connectionChangedListeners.values()){
                    listener.onChanged(connected ? ConnectionChangedListener.Status.CONNECTED : ConnectionChangedListener.Status.DISCONNECTED);
                }
            }
        });
    }

    public void onRoomJoined(final String roomId){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(JoinRoomListener listener : _joinRoomListeners.values()){
                    listener.onRoomJoined(roomId);
                }
            }
        });
    }

    public void onJoinRoomFail(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(JoinRoomListener listener : _joinRoomListeners.values()){
                    listener.onJoinRoomFailed();
                }
            }
        });
    }

    public void onUpdateRoomMatesReceived(final int code, final String msg, final String senderId){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(UpdateRoomMatesListener listener : _updateRoomMatesListeners.values()){
                    listener.onUpdateRoomMatesReceived(code, msg, senderId);
                }
            }
        });
    }

    public void onRoomMessageReceived(final String msg, final String senderId){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(MessagingListener listener : _messagingListeners.values()){
                    listener.onRoomMessageReceived(msg, senderId);
                }
            }
        });
        Broadcaster.getInstance().broadcast(BroadcastEvent.CHAT_NEW_MESSAGE, new ChatMessage(msg, ChatMessage.FromType.USER, senderId));
    }

    public HashMap<String, ConnectionChangedListener> getConnectionChangedListeners() {
        return _connectionChangedListeners;
    }

    public HashMap<String, JoinRoomListener> getJoinRoomListeners() {
        return _joinRoomListeners;
    }

    public HashMap<String, UpdateRoomMatesListener> getUpdateRoomMatesListeners() {
        return _updateRoomMatesListeners;
    }

    public abstract void connect(Profile user);

    public abstract void disconnect();

    public abstract void createAndJoinRoom();

    public abstract void sendRoomMessage(String msg);

    public abstract void joinRoom(String roomId);

    public abstract void leaveRoom();

    public abstract void updateRoomMates(int updateRoomMatesCode, String msg);

}
