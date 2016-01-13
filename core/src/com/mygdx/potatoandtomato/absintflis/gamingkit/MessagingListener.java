package com.mygdx.potatoandtomato.absintflis.gamingkit;

import com.mygdx.potatoandtomato.models.ChatMessage;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public abstract class MessagingListener {

    public abstract void onRoomMessageReceived(ChatMessage chatMessage, String senderId);

}
