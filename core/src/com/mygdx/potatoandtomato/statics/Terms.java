package com.mygdx.potatoandtomato.statics;

import com.mygdx.potatoandtomato.statics.Global;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Terms {

    public static String USERID = "userId";
    public static String USER_SECRET = "userSecret";
    public static String USERID_2 = "userId2";
    public static String USER_SECRET_2 = "userSecret2";
    public static String FACEBOOK_USERID = "facebookUserId";
    public static String PREF_NAME = "potatoandtomato";
    public static String PREF_BASIC_TUTORIAL = "basicTutorials";
    public static String PREF_INVITE_TUTORIAL = "inviteTutorials";
    public static String FACEBOOK_USERNAME = "facebookUsername";
    public static String FACEBOOK_TOKEN = "facebookToken";
    public static String GAME_ENTRANCE = "com.potatoandtomato.games.Entrance";
    public static String SOUNDS_DISABLED = "soundsDisabled";
    public static String AUTO_PLAY_AUDIO_DISABLED = "autoPlayAudioMsgDisabled";
    public static String BROADCAST_RECEIVER_FOR_DISPOSE = "broadcastReceiverForDispose";
    public static String PREF_CHAT_TEMPLATE = "chatTemplates";
    public static String LOCAL_HOST = "localhost";
    public static String WATCH_ADS_ID = "WATCH_ADS";
    public static String PUSH_CODE = "pushCode";
    public static String GCM_PROJECT_NUMBER = "171699917132";
    public static String GCM_KEY = "AIzaSyAsjcqh5WJFtwEOYPqrddtMjZZgEIpEZAQ";
    public static String TOTAL_GAME_COUNT = "totalGameCount";
    public static String WATCH_ADS_SECRET = "3E4933FD7CDD337AF874E53FD975BECD1F629276CE6CB4346A6F1B2C12E786B59195311647F7D6F3926CA417ED2CE7E4BFB8F2EEA12FE84BF6B3";

    public static String FIREBASE_URL(){
        return !Global.DEBUG ? "https://glaring-inferno-8572.firebaseIO.com" : "https://ptapptest.firebaseio.com/";
    }

    public static String RESTFUL_URL(){
        return !Global.DEBUG ? "https://pandt-1050.appspot.com/" : "http://"+LOCAL_HOST+":10080/";
    }

    public static String WARP_API_KEY(){
        return !Global.DEBUG ? "08e25748189dccf0d82070e17c87225350614c754e8e0d511128d65da9d27956" :
                "9d6c174f8ea9985f9b4be630845bd8e57a91f9df73a27d65d64f6e00d2ed4202";
    }

    public static String WARP_SECRET_KEY(){
        return  !Global.DEBUG ? "ed573d5aa22d343d8b187e610007f299c9811bd3594c94d8ffe3f789a69de960" :
                "e8d0cda539241037828634e01504aa017b28bfb0519b8884f6ebfafc0062fc96";
    }

    public static String FLURRY_KEY(){
        return !Global.DEBUG ? "GHZ8NVQHWMN24MSFYKMR" : "";
    }

}
