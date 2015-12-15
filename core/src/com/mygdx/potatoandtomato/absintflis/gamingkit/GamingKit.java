package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class GamingKit {

    private Array<ConnectionChangedListener> _connectionChangedListeners;
    private Array<JoinRoomListener> _joinRoomListeners;

    public GamingKit() {
        _connectionChangedListeners = new Array<>();
        _joinRoomListeners = new Array<>();
    }

    public void addListener(Object listener){
        if(listener instanceof ConnectionChangedListener){
            _connectionChangedListeners.add((ConnectionChangedListener) listener);
        }
        else if(listener instanceof JoinRoomListener){
            _joinRoomListeners.add((JoinRoomListener) listener);
        }
    }

    public void removeListener(Object listener){
        if(listener instanceof ConnectionChangedListener){
            _connectionChangedListeners.removeValue((ConnectionChangedListener) listener, false);
        }
        else if(listener instanceof JoinRoomListener){
            _joinRoomListeners.removeValue((JoinRoomListener) listener, false);
        }
    }

    public void onConnectionChanged(final boolean connected){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(ConnectionChangedListener listener : _connectionChangedListeners){
                    listener.onChanged(connected ? ConnectionChangedListener.Status.CONNECTED : ConnectionChangedListener.Status.DISCONNECTED);
                }
            }
        });
    }

    public void onRoomJoined(final String roomId){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(JoinRoomListener listener : _joinRoomListeners){
                    listener.onRoomJoined(roomId);
                }
            }
        });
    }

    public void onJoinRoomFail(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(JoinRoomListener listener : _joinRoomListeners){
                    listener.onJoinRoomFailed();
                }
            }
        });
    }


    public abstract void connect(String username);

    public abstract void createAndJoinRoom();

    public abstract void joinRoom(String roomId);

    public abstract void leaveRoom();

}
