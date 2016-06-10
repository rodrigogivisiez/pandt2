package com.mygdx.potatoandtomato.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.absints.IRemoteHelper;
import com.potatoandtomato.common.absints.WebImageListener;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 9/5/2016.
 */
public class RemoteHelper implements IRemoteHelper{

    private Broadcaster broadcaster;
    private ArrayList<String> broadcastIds;

    public RemoteHelper(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
        broadcastIds = new ArrayList();


    }

    @Override
    public void getRemoteImage(final String url, final WebImageListener listener){

        String broadcastId = broadcaster.subscribe(BroadcastEvent.LOAD_IMAGE_RESPONSE, new BroadcastListener<Pair<String, Texture>>() {
            @Override
            public void onCallback(Pair<String, Texture> result, Status st) {
                if (st == Status.SUCCESS) {
                    if (result.getFirst().equals(url)) {
                        broadcaster.unsubscribe(this.getId());
                        listener.onLoaded(result.getSecond());
                    }
                } else {
                    if (result != null) {
                        if (result.getFirst().equals(url)) {
                            broadcaster.unsubscribe(this.getId());
                            listener.onLoaded(null);
                        }
                    }
                }
            }
        });
        broadcaster.broadcast(BroadcastEvent.LOAD_IMAGE_REQUEST, url);

        broadcastIds.add(broadcastId);
    }


    @Override
    public void dispose() {
        for(String id : broadcastIds){
            broadcaster.unsubscribe(id);
        }
    }
}
