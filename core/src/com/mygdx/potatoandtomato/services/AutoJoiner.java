package com.mygdx.potatoandtomato.services;

import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.potatoandtomato.common.utils.Strings;

/**
 * Created by SiongLeng on 10/8/2016.
 */
public class AutoJoiner {

    private Confirm confirm;
    private Texts texts;
    private String autoJoinRoomId;

    public AutoJoiner(String autoJoinRoomId) {
        this.autoJoinRoomId = autoJoinRoomId;
    }

    public void init(Confirm confirm, Texts texts) {
        this.confirm = confirm;
        this.texts = texts;

        if(isAutoJoining()){
            useAutoJoinRoom(this.getAutoJoinRoomId());
        }
    }

    public void useAutoJoinRoom(String roomId){
        this.autoJoinRoomId = roomId;
        confirm.show(ConfirmIdentifier.AutoJoiner, texts.autoJoiningMsg(), Confirm.Type.LOADING_WITH_CANCEL, new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                autoJoinRoomId = null;
            }
        });
    }

    public void stopAutoJoinRoom(){
        confirm.close(ConfirmIdentifier.AutoJoiner);
        autoJoinRoomId = null;
    }

    public boolean isAutoJoining(){
        return !Strings.isEmpty(autoJoinRoomId);
    }

    public String getAutoJoinRoomId() {
        return autoJoinRoomId;
    }
}
