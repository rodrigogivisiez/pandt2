package com.mygdx.potatoandtomato.enums;

/**
 * Created by SiongLeng on 30/7/2016.
 */
public enum ClientConnectionStatus {
    CONNECTED, DISCONNECTED, DISCONNECTED_BUT_RECOVERABLE, CONNECTED_FROM_RECOVER;

    public static boolean isConnected(ClientConnectionStatus clientConnectionStatus){
        if(clientConnectionStatus == ClientConnectionStatus.CONNECTED || clientConnectionStatus == ClientConnectionStatus.CONNECTED_FROM_RECOVER){
            return true;
        }
        else{
            return false;
        }
    }

}