package com.mygdx.potatoandtomato.statics;

import com.mygdx.potatoandtomato.services.Preferences;
import com.mygdx.potatoandtomato.utils.Terms;

/**
 * Created by SiongLeng on 20/1/2016.
 */
public class Global {

    public static boolean ENABLE_SOUND = true;
    public static boolean IS_POTRAIT = true;
    public static int CLIENT_VERSION = 0;
    public static boolean DEBUG = false;
    public static int LEADERBOARD_COUNT = 15;

    public static void init(Preferences preferences){
        if(preferences.get(Terms.SOUNDS_DISABLED) != null && preferences.get(Terms.SOUNDS_DISABLED).equals("true")){
            ENABLE_SOUND = false;
        }
    }


}
