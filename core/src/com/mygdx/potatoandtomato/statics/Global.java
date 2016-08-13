package com.mygdx.potatoandtomato.statics;

import com.mygdx.potatoandtomato.services.Preferences;
import com.potatoandtomato.common.utils.Strings;

/**
 * Created by SiongLeng on 20/1/2016.
 */
public class Global {

    public static boolean ENABLE_SOUND = true;
    public static boolean AUTO_PLAY_AUDIO_MSG = true;
    public static boolean IS_POTRAIT = true;
    public static int CLIENT_VERSION = 0;
    public static boolean DEBUG = false;
    public static int LEADERBOARD_COUNT = 15;
    public static int ABANDON_TOLERANCE_SECS = 60;
    public static int USERNAME_MAX_LENGTH = 25;
    public static String SALT = "luizsuarecScoressss";
    public static String LAST_PLAY_GAME = "";
    public static String RESTAPI_KEY = "cvRm3wKa7MQ0vuAmiN60329jP2qjFvfIR5uAftlqz4YBv5g66dLlKt4Xexw497mz";

    public static void init(Preferences preferences){
        Strings.Salt = SALT;
        if(preferences.get(Terms.SOUNDS_DISABLED) != null && preferences.get(Terms.SOUNDS_DISABLED).equals("true")){
            ENABLE_SOUND = false;
        }

        if(preferences.get(Terms.AUTO_PLAY_AUDIO_DISABLED) != null && preferences.get(Terms.AUTO_PLAY_AUDIO_DISABLED).equals("true")){
            AUTO_PLAY_AUDIO_MSG = false;
        }
    }

    public static void setLastPlayGame(String gameAbbr){
        LAST_PLAY_GAME = gameAbbr;
    }

}
