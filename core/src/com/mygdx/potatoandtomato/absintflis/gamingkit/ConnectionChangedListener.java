package com.mygdx.potatoandtomato.absintflis.gamingkit;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public abstract class ConnectionChangedListener {

    public enum ConnectStatus {
        CONNECTED, DISCONNECTED, DISCONNECTED_BUT_RECOVERABLE, CONNECTED_FROM_RECOVER;

        public static boolean isConnected(ConnectStatus connectStatus){
            if(connectStatus == ConnectStatus.CONNECTED || connectStatus == ConnectStatus.CONNECTED_FROM_RECOVER){
                return true;
            }
            else{
                return false;
            }
        }

    }



    public ConnectionChangedListener() {

    }

    public abstract void onChanged(String userId, ConnectStatus st);

}
