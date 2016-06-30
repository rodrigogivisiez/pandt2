package com.potatoandtomato.common.broadcaster;

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
    //public static final int CHAT_NEW_MESSAGE = 9;  //chat new message received
    public static final int LOGIN_GCM_REQUEST = 10; //ask for login gcm
    public static final int LOGIN_GCM_CALLBACK = 11; //ask for login gcm
    public static final int FACEBOOK_GET_FRIENDS_REQUEST = 12; //ask for fb friends
    public static final int FACEBOOK_GET_FRIENDS_RESPONSE = 13; //ask for fb friends
    public static final int DESTROY_ROOM = 14; //destroy room, clear push notification
    public static final int UPDATE_ROOM = 37; //update room notification
    public static final int LOAD_GAME_REQUEST = 15; //request to load game
    public static final int LOAD_GAME_RESPONSE = 16; //response to load game
    public static final int GAME_END = 17; //game ended
    public static final int INGAME_UPDATE_REQUEST = 21;     //imgame update request
    //public static final int INGAME_UPDATE_RESPONSE = 22;     //imgame update request
    public static final int LOAD_IMAGE_REQUEST = 23;    //load image from url
    public static final int LOAD_IMAGE_RESPONSE = 24;   //load image from url
    public static final int SHOW_NATIVE_KEYBOARD = 27; //show native keyboard
    public static final int HIDE_NATIVE_KEYBOARD = 44; //hide native keyboard
    public static final int NATIVE_KEYBOARD_CLOSED = 45; //native keyboard close callback
    public static final int NATIVE_TEXT_CHANGED = 28; //native text change notify libgdx
    public static final int LIBGDX_TEXT_CHANGED = 29; //libgdx text change notify native
    public static final int NATIVE_TEXT_DONE_CLICKED = 43; //native text change notify libgdx
    public static final int VIBRATE_DEVICE = 30; //request to vibrate device
    public static final int DEVICE_ORIENTATION = 31;    //0 to potrait, 1 to landscape
    public static final int SOUNDS_CHANGED = 32;    //sounds changed
    public static final int RECORD_START = 33;  //audio recording start
    public static final int RECORD_END = 34;  //audio recording end
    public static final int RECORD_RESPONSE = 35; //audio record respond back
    public static final int SHOW_REWARD_VIDEO = 36; //show reward video
    public static final int HAS_REWARD_VIDEO = 38; //check has reward video
    public static final int IAB_PRODUCTS_REQUEST = 39; //request for iab products
    public static final int IAB_PRODUCTS_RESPONSE = 40; //return iab products
    public static final int IAB_PRODUCT_PURCHASE = 41; //purchase and consume product
    public static final int IAB_PRODUCT_PURCHASE_RESPONSE = 42; //product purchase response
}
