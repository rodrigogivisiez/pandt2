package com.potatoandtomato.common;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by SiongLeng on 20/9/2015.
 */
public class Broadcaster {

    private static Broadcaster instance;
    public static final int USER_READY = 0;     //user profile retrieved
    public static final int WARP_READY = 1;     //warp instance ready
    public static final int WARP_CONNECTION_CHANGED = 2;   //successully connect with username / disconnect
    public static final int WARP_ROOM_CREATED = 3;   //room created

    private Hashtable<Integer, ArrayList<BroadcastListener>> callbacks;
    private ArrayList<String> subScribeOnceArr;

    public static Broadcaster getInstance(){
        if(instance == null) instance = new Broadcaster();
        return instance;
    }

    public Broadcaster() {
        callbacks = new Hashtable<Integer, ArrayList<BroadcastListener>>();
        subScribeOnceArr = new ArrayList<String>();
    }

    public String subscribeOnce(int event, BroadcastListener listener){
        String id = subscribe(event, listener);
        subScribeOnceArr.add(id);
        return id;
    }

    public String subscribe(int event, BroadcastListener listener){
        ArrayList<BroadcastListener> arr = callbacks.get(event);
        if(arr == null) arr = new ArrayList<BroadcastListener>();
        arr.add(listener);
        callbacks.put(event, arr);
        return listener.getId();
    }

    public void unsubscribe(String listenerId){

        Set<Integer> keys = callbacks.keySet();
        for(Integer event: keys){
            boolean found = false;
            ArrayList<BroadcastListener> arr = callbacks.get(event);
            if(arr != null){
                for(int i = 0; i < arr.size(); i ++){
                    if(arr.get(i).getId().equals(listenerId)){
                        arr.remove(i);
                        found = true;
                        break;
                    }
                }
            }
            if(found){
                callbacks.put(event, arr);
                return;
            }
        }
    }

    public void broadcast(int event, @Nullable Object obj){
        ArrayList<BroadcastListener> arr = callbacks.get(event);
        if(arr != null){
            for(int i = 0; i < arr.size(); i ++){
                BroadcastListener r = arr.get(i);
                if(subScribeOnceArr.contains(arr.get(i).getId())){
                    unsubscribe(r.getId());
                }
                subScribeOnceArr.remove(r.getId());
                r.onCallback(obj);
            }
        }
    }

    public ArrayList<BroadcastListener> getEventCallbacks(int event){
        ArrayList<BroadcastListener> arr = callbacks.get(event);
        return arr;
    }

    public int getEventCallbacksSize(int event){
        return getEventCallbacks(event).size();
    }

    public ArrayList<String> getSubScribeOnceArr() {
        return subScribeOnceArr;
    }
}
