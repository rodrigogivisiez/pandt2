package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.services.Recorder;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 10/5/2016.
 */
public class VoiceMessageControl extends Table {

    private Table _this;
    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Recorder recorder;
    private String myUserId;
    private ChatMessage chatMessage;
    private FileHandle fileHandle;

    public VoiceMessageControl(ChatMessage chatMessage, Assets assets, SoundsPlayer soundsPlayer, Recorder recorder, String myUserId) {
        this.soundsPlayer = soundsPlayer;
        this.recorder = recorder;
        this.myUserId = myUserId;
        this.chatMessage = chatMessage;
        this.assets = assets;
        _this = this;
        _this.align(Align.left);
        fileHandle = Gdx.files.absolute(chatMessage.getMessage());

        populate();
        setListener();
        autoPlayIfNeeded();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Image voiceControl = new Image(assets.getTextures().get(Textures.Name.VOICE_ICON));
                _this.add(voiceControl).size(20, 20);

                new DummyButton(_this, assets);
            }
        });
    }

    public void autoPlayIfNeeded(){
        if(!chatMessage.getSenderId().equals(myUserId)){
            playVoiceMessage();
        }
    }

    public void setListener(){
        this.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playVoiceMessage();
            }
        });
    }

    public void playVoiceMessage(){
        if(fileHandle.exists()){
            soundsPlayer.setVolume(0);
            recorder.playBack(fileHandle, new Runnable() {
                @Override
                public void run() {
                    soundsPlayer.setVolume(1);
                }
            });
        }
    }

}
