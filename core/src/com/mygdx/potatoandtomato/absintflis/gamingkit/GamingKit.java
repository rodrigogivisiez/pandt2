package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.models.Profile;

import java.util.HashMap;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class GamingKit {

    private HashMap<String, ConnectionChangedListener> _connectionChangedListeners;
    private HashMap<String, JoinRoomListener> _joinRoomListeners;
    private HashMap<String, UpdateRoomMatesListener> _updateRoomMatesListeners;

    public GamingKit() {
        _connectionChangedListeners = new HashMap<>();
        _joinRoomListeners = new HashMap<>();
        _updateRoomMatesListeners = new HashMap<>();
    }

    public void addListener(Object listener){
        if(listener instanceof ConnectionChangedListener){
            _connectionChangedListeners.put(Logs.getCallerClassName(), (ConnectionChangedListener) listener);
        }
        else if(listener instanceof JoinRoomListener){
            _joinRoomListeners.put(Logs.getCallerClassName(), (JoinRoomListener) listener);
        }
        else if(listener instanceof UpdateRoomMatesListener){
            _updateRoomMatesListeners.put(Logs.getCallerClassName(), (UpdateRoomMatesListener) listener);
        }
    }

    public void removeListenersByClass(Class clss){
        _connectionChangedListeners.remove(clss.getName());
        _joinRoomListeners.remove(clss.getName());
        _updateRoomMatesListeners.remove(clss.getName());
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

    public abstract void joinRoom(String roomId);

    public abstract void leaveRoom();

    public abstract void updateRoomMates(int updateRoomMatesCode, String msg);

}
