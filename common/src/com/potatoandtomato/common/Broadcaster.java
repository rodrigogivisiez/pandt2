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

    public String subscribeOnceWithTimeout(final int event, final long timeOut, BroadcastListener listener){
        final String id = subscribe(event, listener);
        subScribeOnceArr.add(id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(subScribeOnceArr.contains(id)){
                    broadcast(event, null, BroadcastListener.Status.FAILED);
                }
            }
        }).start();
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

    public void broadcast(int event){ //overload of null object, success broadcast
        broadcast(event, null, BroadcastListener.Status.SUCCESS);
    }

    public void broadcast(int event, @Nullable Object obj){ //overload of success broadcast
        broadcast(event, obj, BroadcastListener.Status.SUCCESS);
    }

    public void broadcast(int event, @Nullable Object obj, BroadcastListener.Status status){
        ArrayList<BroadcastListener> arr = callbacks.get(event);
        if(arr != null){
            for(int i = 0; i < arr.size(); i ++){
                BroadcastListener r = arr.get(i);
                if(subScribeOnceArr.contains(arr.get(i).getId())){
                    unsubscribe(r.getId());
                }
                subScribeOnceArr.remove(r.getId());
                r.onCallback(obj, status);
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

    public boolean hasEventCallback(int event){
        return getEventCallbacks(event).size() > 0;
    }

    public ArrayList<String> getSubScribeOnceArr() {
        return subScribeOnceArr;
    }
}
