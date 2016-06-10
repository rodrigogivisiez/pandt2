package com.mygdx.potatoandtomato.utils;

import com.mygdx.potatoandtomato.statics.Terms;
import com.potatoandtomato.common.utils.Threadings;
import com.shephertz.app42.gaming.multiplayer.client.ConnectionState;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

/**
 * Created by SiongLeng on 31/5/2016.
 */
public class ForAppwarpTesting implements ConnectionRequestListener {

    private WarpClient _warpInstance;
    private String _appKey = Terms.WARP_API_KEY();
    private String _secretKey = Terms.WARP_SECRET_KEY();
    private boolean connecting = false;

    public void init(){
        WarpClient.enableTrace(true);
        int result =  WarpClient.initialize(_appKey, _secretKey);
        System.out.println("initialization result " + result);

        try {
            _warpInstance = WarpClient.getInstance();
            _warpInstance.addConnectionRequestListener(this);

            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        monitor();

    }

    private void monitor(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(!connecting) continue;
                    Threadings.sleep(5000);
                    if(_warpInstance.getConnectionState() == ConnectionState.CONNECTED){
                        _warpInstance.disconnect();
                    }
                }
            }
        });
    }

    private void connect(){
        String username = String.valueOf(System.currentTimeMillis());
        System.out.println("Connecting...");
        _warpInstance.connectWithUserName(username);
        connecting = true;
    }

    private void disconnect(){
        if(_warpInstance.getConnectionState() == ConnectionState.CONNECTED) {
            _warpInstance.disconnect();
            connecting = false;
        }
    }

    @Override
    public void onConnectDone(ConnectEvent connectEvent) {
        System.out.println("onConnectDone: " + connectEvent.getResult());
        disconnect();
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {
        System.out.println("onDisconnectDone: " + connectEvent.getResult());
        connect();
    }

    @Override
    public void onInitUDPDone(byte b) {

    }
}
