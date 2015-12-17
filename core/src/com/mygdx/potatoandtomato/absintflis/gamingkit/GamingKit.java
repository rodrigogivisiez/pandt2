package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class GamingKit {

    private Array<ConnectionChangedListener> _connectionChangedListeners;
    private Array<JoinRoomListener> _joinRoomListeners;
    private Array<UpdateRoomMatesListener> _updateRoomMatesListeners;

    public GamingKit() {
        _connectionChangedListeners = new Array<>();
        _joinRoomListeners = new Array<>();
        _updateRoomMatesListeners = new Array<>();
    }

    public void addListener(Object listener){
        if(listener instanceof ConnectionChangedListener){
            _connectionChangedListeners.add((ConnectionChangedListener) listener);
        }
        else if(listener instanceof JoinRoomListener){
            _joinRoomListeners.add((JoinRoomListener) listener);
        }
        else if(listener instanceof UpdateRoomMatesListener){
            _updateRoomMatesListeners.add((UpdateRoomMatesListener) listener);
        }
    }

    public void removeListener(Object listener){
        if(listener instanceof ConnectionChangedListener){
            _connectionChangedListeners.removeValue((ConnectionChangedListener) listener, false);
        }
        else if(listener instanceof JoinRoomListener){
            _joinRoomListeners.removeValue((JoinRoomListener) listener, false);
        }
        else if(listener instanceof UpdateRoomMatesListener){
            _updateRoomMatesListeners.removeValue((UpdateRoomMatesListener) listener, false);
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

    public void onUpdateRoomMatesReceived(final int code, final String msg, final String senderId){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(UpdateRoomMatesListener listener : _updateRoomMatesListeners){
                    listener.onUpdateRoomMatesReceived(code, msg, senderId);
                }
            }
        });
    }


    public abstract void connect(String username);

    public abstract void createAndJoinRoom();

    public abstract void joinRoom(String roomId);

    public abstract void leaveRoom();

    public abstract void updateRoomMates(int updateRoomMatesCode, String msg);

}
