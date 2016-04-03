package com.potatoandtomato.common.helpers;

import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.SafeThread;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.common.absints.ConnectionMonitorListener;

import java.util.HashMap;

/**
 * Created by SiongLeng on 4/4/2016.
 */
public class ConnectionMonitor implements Disposable {

    private ConnectionMonitorListener listener;
    private HashMap<String, SafeThread> dcUserThreads;

    public ConnectionMonitor(ConnectionMonitorListener listener) {
        this.listener = listener;
        this.dcUserThreads = new HashMap<String, SafeThread>();
    }

    public void userAbandoned(String userId){
        if(dcUserThreads.containsKey(userId)){
            dcUserThreads.get(userId).kill();
            dcUserThreads.remove(userId);
        }
    }

    public void connectionChanged(final String userId, boolean connected){
        if(connected){
            if(dcUserThreads.containsKey(userId)){
                dcUserThreads.get(userId).kill();
                dcUserThreads.remove(userId);
            }
        }
        else{
            if(!dcUserThreads.containsKey(userId)){
                final SafeThread safeThread = new SafeThread();
                dcUserThreads.put(userId, safeThread);
                Threadings.runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        Threadings.sleep(60 * 1000);        //60 secs limit
                        if(safeThread.isKilled()){
                            return;                     //user successfully connect back/abandoned
                        }
                        else{                   //user exceed reconnect time limit
                            listener.onExceedReconnectLimitTime(userId);
                        }
                    }
                });
            }
        }
    }


    @Override
    public void dispose() {
        for(SafeThread safeThread : dcUserThreads.values()){
            safeThread.kill();
        }
        dcUserThreads.clear();
    }
}
