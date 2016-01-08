package com.potatoandtomato.common;

import java.util.Random;

/**
 * Created by SiongLeng on 20/9/2015.
 */
public abstract class BroadcastListener<T> {
    
    private String id;

    public BroadcastListener() {
        this.id = randomString(10);
    }

    public String getId() {
        return id;
    }

    public abstract void onCallback(T obj, Status st);

    public String randomString(int len)
    {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

}
