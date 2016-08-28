package com.mygdx.potatoandtomato.helpers;

import com.mygdx.potatoandtomato.absintflis.analytics.ITracker;
import com.mygdx.potatoandtomato.enums.AnalyticEvent;
import com.potatoandtomato.common.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 29/7/2016.
 */
public class Analytics {

    private static ITracker tracker;

    public static void log(AnalyticEvent analyticEvent){
        log(analyticEvent.name(), null);
    }

    public static void log(AnalyticEvent analyticEvent, int value){
        Map<String, String> param = new HashMap<String, String>();
        param.put("value", String.valueOf(value));
        log(analyticEvent.name(), param);
    }

    public static void log(AnalyticEvent analyticEvent, String key, String value){
        Map<String, String> param = new HashMap<String, String>();
        param.put(key, String.valueOf(value));
        log(analyticEvent, param);
    }

    public static void logToScene(String sceneLogicName){
        log(AnalyticEvent.ToScene.name() + "_" + sceneLogicName, null);
    }

    public static void log(AnalyticEvent analyticEvent,  Map<String, String> param){
       log(analyticEvent.name(), param);
    }

    public static void log(String eventName, Map<String, String> param){
        if(tracker == null) return;

        String label = "null";

        if(param != null && param.size() > 0){
            ArrayList<String> result = new ArrayList();
            for(String key : param.keySet()){
                result.add(key + " -> " + param.get(key));
            }

            label = Strings.joinArr(result, " || ");
        }

        tracker.trackEvent(eventName, label);
    }


    public static void setTracker(ITracker tracker) {
        Analytics.tracker = tracker;
    }
}
