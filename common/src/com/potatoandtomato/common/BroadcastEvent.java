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
    public static final int SCREEN_LAYOUT_CHANGED = 6;  //screen layout changed, move chat box position
    public static final int CHAT_NEW_MESSAGE = 7;  //chat new message received

}
