package com.potatoandtomato.games.statics;

import com.potatoandtomato.games.helpers.Logs;

/**
 * Created by SiongLeng on 22/2/2016.
 */
public class Global {

    public static boolean DEBUG = false;         //default false
    public static boolean NO_ENTRANCE = false;
    public static boolean BOT_MATCH = false;
    public static int ANIMATION_COUNT;

    public static void increaseAnimationCount(){
        if(ANIMATION_COUNT < 0) ANIMATION_COUNT = 0;
        ANIMATION_COUNT++;
        Logs.show(String.valueOf(ANIMATION_COUNT));
    }

    public static void decreaseAnimationCount(){
        ANIMATION_COUNT--;
        if(ANIMATION_COUNT < 0) ANIMATION_COUNT = 0;
        Logs.show(String.valueOf(ANIMATION_COUNT));
    }

}
