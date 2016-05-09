package com.potatoandtomato.common.broadcaster;

import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by SiongLeng on 20/9/2015.
 */
public abstract class BroadcastListener<T> implements Serializable {
    
    private String id;

    public BroadcastListener() {
        this.id = Strings.generateUniqueRandomKey(40);
    }

    public String getId() {
        return id;
    }

    public abstract void onCallback(T obj, Status st);
}
