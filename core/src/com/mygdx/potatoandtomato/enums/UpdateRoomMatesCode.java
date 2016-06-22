package com.mygdx.potatoandtomato.enums;

import com.potatoandtomato.common.enums.RoomUpdateType;

/**
 * Created by SiongLeng on 17/12/2015.
 */
public class UpdateRoomMatesCode {

    public final static int UPDATE_DOWNLOAD = 0;
    public final static int JOIN_ROOM = 10;
    public final static int LEFT_ROOM = 11;
    public final static int MOVE_SLOT = 12;
    public final static int INVTE_USERS = 13;
    public final static int KICK_USER = 14;
    public final static int START_GAME = 1;
    public final static int PLAYER_CANCEL_START_GAME = 9;
    public final static int USER_IS_READY = 2;
    public final static int ASK_FOR_USER_READY = 3;
    public final static int IN_GAME_UPDATE = 4;
    public final static int UPDATE_USER_READY = 5;
    public final static int GAME_OUTDATED = 6;
    public final static int GAME_STARTED = 7;
    public final static int ALL_PLAYERS_LOADED_GAME_SUCCESS = 8;
    public final static int LOAD_FAILED = 15;
    public final static int AUDIO_CHAT = 16;
    public final static int LOCK_PROPERTY = 17;
    public final static int GAME_DATA = 18;
    public final static int GAME_DATA_REQUEST = 22;
    public final static int DECISION_MAKER = 19;
    public final static int USER_ABANDON = 20;
    public final static int USER_CONNECTED = 21;
    public final static int PUT_COIN = 22;
    public final static int COINS_DEDUCTED_SUCCESS = 23;
    public final static int COINS_DEDUCTED_FAILED = 24;
    public final static int REQUEST_COINS_STATE = 25;
    public final static int COINS_STATE_RESPONSE = 26;

    public static int roomUpdateTypeToUpdateRoomMatesCode(RoomUpdateType roomUpdateType){
        if(roomUpdateType == RoomUpdateType.InGame){
            return IN_GAME_UPDATE;
        }
        else if(roomUpdateType == RoomUpdateType.GameData){
            return GAME_DATA;
        }
        else if(roomUpdateType == RoomUpdateType.GameDataRequest){
            return GAME_DATA_REQUEST;
        }
        else if(roomUpdateType == RoomUpdateType.DecisionMakerUpdate){
            return DECISION_MAKER;
        }
        else{
            return -1;
        }
    }

}
