package com.mygdx.potatoandtomato.helpers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.flurry.android.FlurryAgent;
import com.mygdx.potatoandtomato.enums.FlurryEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 29/7/2016.
 */
public class Flurry {

    public static boolean active;

    public static void log(FlurryEvent flurryEvent){
        if(!active) return;

        FlurryAgent.logEvent(flurryEvent.name());
    }

    public static void log(FlurryEvent flurryEvent, int value){
        if(!active) return;

        Map<String, String> param = new HashMap<String, String>();
        param.put("value", String.valueOf(value));
        FlurryAgent.logEvent(flurryEvent.name(), param);
    }

    public static void log(FlurryEvent flurryEvent, String key, String value){
        if(!active) return;

        Map<String, String> param = new HashMap<String, String>();
        param.put(key, String.valueOf(value));
        FlurryAgent.logEvent(flurryEvent.name(), param);
    }

    public static void log(FlurryEvent flurryEvent,  Map<String, String> param){
        if(!active) return;

        FlurryAgent.logEvent(flurryEvent.name(), param);
    }

    public static void logToScene(String sceneLogicName){
        if(!active) return;

        FlurryAgent.logEvent(FlurryEvent.ToScene.name() + "_" + sceneLogicName);
    }

    public static void logTimeStart(FlurryEvent flurryEvent){
        if(!active) return;

        FlurryAgent.logEvent(flurryEvent.name(), true);
    }

    public static void logTimeEnd(FlurryEvent flurryEvent){
        if(!active) return;

        FlurryAgent.endTimedEvent(flurryEvent.name());
    }


    public static void setActive(boolean active) {
        Flurry.active = active;
    }
}
