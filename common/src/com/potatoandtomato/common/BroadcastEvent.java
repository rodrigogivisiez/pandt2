package com.potatoandtomato.common;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class BroadcastEvent {

    public static final int USER_READY = 0;     //user profile retrieved
    public static final int WARP_READY = 1;     //warp instance ready
    public static final int WARP_CONNECTION_CHANGED = 2;   //successully connect with username / disconnect
    public static final int WARP_ROOM_CREATED = 3;   //room created
    public static final int LOGIN_FACEBOOK_REQUEST = 4; //ask for login facebook
    public static final int LOGIN_FACEBOOK_CALLBACK = 5; //ask for login facebook
    public static final int LOGOUT_FACEBOOK_REQUEST = 6; //ask for login facebook
    public static final int LOGOUT_FACEBOOK_CALLBACK = 7; //ask for login facebook
    public static final int SCREEN_LAYOUT_CHANGED = 8;  //screen layout changed, move chat box position
    public static final int CHAT_NEW_MESSAGE = 9;  //chat new message received
    public static final int LOGIN_GCM_REQUEST = 10; //ask for login gcm
    public static final int LOGIN_GCM_CALLBACK = 11; //ask for login gcm
    public static final int FACEBOOK_GET_FRIENDS_REQUEST = 12; //ask for fb friends
    public static final int FACEBOOK_GET_FRIENDS_RESPONSE = 13; //ask for fb friends
    public static final int DESTROY_ROOM = 14; //destroy room, clear push notification
    public static final int LOAD_GAME_REQUEST = 15; //request to load game
    public static final int LOAD_GAME_RESPONSE = 16; //response to load game
    public static final int GAME_END = 17; //game ended\
    public static final int GAME_PAUSED = 19;   //notify game that game is paused
    public static final int GAME_RESUME = 20;   //notify game that game is resumed
    public static final int INGAME_UPDATE_REQUEST = 21;     //imgame update request
    public static final int INGAME_UPDATE_RESPONSE = 22;     //imgame update request
    public static final int LOAD_IMAGE_REQUEST = 23;    //load image from url
    public static final int LOAD_IMAGE_RESPONSE = 24;   //load image from url
    public static final int KEEP_APPS_ALIVE = 25; //keep apps alive
    public static final int REMOVE_APPS_ALIVE = 26; //remove keep apps alive


}
