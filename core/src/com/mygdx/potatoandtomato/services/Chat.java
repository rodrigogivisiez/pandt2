package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.mygdx.potatoandtomato.absintflis.controls.ChatTemplateSelectedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.enums.GameConnectionStatus;
import com.mygdx.potatoandtomato.enums.FlurryEvent;
import com.mygdx.potatoandtomato.enums.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.controls.ChatControl;
import com.mygdx.potatoandtomato.controls.ChatTemplateControl;
import com.mygdx.potatoandtomato.controls.DummyKeyboard;
import com.mygdx.potatoandtomato.helpers.Flurry;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.utils.BytesUtils;
import com.mygdx.potatoandtomato.utils.Files;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class Chat {

    private Broadcaster broadcaster;
    private GamingKit gamingKit;
    private Recorder recorder;
    private SoundsPlayer soundsPlayer;
    private Room room;
    private boolean visible, mode2ChatLock;
    private IPTGame ptGame;
    private int mode;
    private String recordsPath;
    private String myUserId;
    private ChatControl chatControl;
    private ChatTemplateControl chatTemplateControl;
    private float originalTouchedPositionY = 0;
    private float softKeyboardHeight = 0;
    private boolean focused;

    public Chat(Broadcaster broadcaster, GamingKit gamingKit, Texts texts, Assets assets,
                        SoundsPlayer soundsPlayer, Recorder recorder, SpriteBatch batch, IPTGame game, Preferences preferences) {
        this.broadcaster = broadcaster;
        this.ptGame = game;
        this.gamingKit = gamingKit;
        this.recorder = recorder;
        this.soundsPlayer = soundsPlayer;
        this.mode = 1;

        chatTemplateControl = new ChatTemplateControl(assets, preferences, texts, soundsPlayer);
        chatControl = new ChatControl(game, texts, assets, soundsPlayer, recorder, batch, chatTemplateControl);

        setListeners();
        invalidate();
    }

    public void initChat(Room room, String myUserId) {
        this.room = room;
        this.myUserId = myUserId;
        recordsPath = "records/" + room.getId() + "/";
        mode2ChatLock = false;
    }

    public void invalidate(){
         chatControl.invalidate(isVisible());
    }

    public void setMode(final int newMode){
        this.mode = newMode;
        chatControl.modeChanged(newMode);
    }

    public void messageTextFieldFocused(){
        if(mode == 2){
            setMode2ChatLock(true);
            chatControl.fadeInMode2();
        }
    }

    public void messageTextFieldUnfocus(){
        if(focused){
            chatControl.unfocusMessageTextField();
            focused = false;
        }

        if(mode == 2){
            chatControl.fadeOutMode2(2);
            setMode2ChatLock(false);
        }
    }

    public void showChat(){
        if(!isVisible()){
            chatControl.getRoot().setVisible(true);
            setVisible(true);
            chatControl.scrollToBottom();
        }
    }

    public void hideChat(){
        chatControl.getRoot().setVisible(false);
        setVisible(false);
        chatControl.scrollToBottom();
    }

    private void startRecord(){
        if(recorder.isCanRecord()){
            chatControl.resetRecordDesign();
            soundsPlayer.setVolume(1);
            soundsPlayer.playSoundEffect(Sounds.Name.MIC);
            Gdx.input.vibrate(200);

            Threadings.delay(300, new Runnable() {
                @Override
                public void run() {
                    soundsPlayer.setVolume(0);
                    Threadings.delay(200, new Runnable() {
                        @Override
                        public void run() {
                            recorder.recordToFile(recordsPath, new RecordListener(null) {
                                @Override
                                public void onStart() {
                                    chatControl.showRecording();
                                }

                                @Override
                                public void onRecording(int volumeLevel, int remainingSecs) {
                                    chatControl.updateRecordStatus(volumeLevel, remainingSecs);
                                    if(remainingSecs <= 0){
                                        stopRecord(0);
                                    }
                                }

                                @Override
                                public void onPreSuccessRecord(FileHandle resultFile) {
                                    sendVoiceMessage(resultFile, -1);
                                }

                                @Override
                                public void onFinishedRecord(FileHandle resultFile, int totalSecs, Status status) {
                                    if (status == Status.SUCCESS) {
                                        sendVoiceMessage(resultFile, totalSecs);
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void stopRecord(int delay){
        Threadings.delay(delay, new Runnable() {
            @Override
            public void run() {
                chatControl.hideRecording();
                recorder.stopRecording();
                Threadings.delay(1000, new Runnable() {
                    @Override
                    public void run() {
                        if(!recorder.isRecording()) {
                            chatControl.hideRecording();
                            soundsPlayer.setVolume(1);
                        }
                    }
                });
            }
        });
    }

    private void cancelRecord(){
        chatControl.hideRecording();
        recorder.cancelRecord();
        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                soundsPlayer.setVolume(1);
            }
        });
    }

    public void messageFieldSendMessage(){
        String msg = chatControl.getMessageTextField().getText().trim();
        if(!msg.equals("")){
            sendMessage(msg, false);
            setMessageFieldText("");
        }
    }

    public void setMessageFieldText(String newMessage){
        chatControl.setMessageTextFieldMsg(newMessage, -1);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                broadcaster.broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo(chatControl.getMessageTextField().getText(),
                        chatControl.getMessageTextField().getCursorPosition()));
            }
        });
    }

    public void sendMessage(String msg, boolean fromTemplate){
        ChatMessage chatMessage = new ChatMessage(msg, ChatMessage.FromType.USER, myUserId, "");
        gamingKit.updateRoomMates((byte) UpdateRoomMatesCode.TEXT_CHAT, chatMessage.toBytes());
        newMessage(chatMessage);

        if(fromTemplate){
            Flurry.log(FlurryEvent.SendChatTemplate);
        }
        else{
            Flurry.log(FlurryEvent.SendChatText);
        }
    }

    public void sendVoiceMessage(final FileHandle file, int totalSecs){
        byte[] data = Files.fileToByte(file.file());
        byte[] secsInByte = new byte[]{(byte) totalSecs};
        gamingKit.updateRoomMates((byte) UpdateRoomMatesCode.AUDIO_CHAT, BytesUtils.prependBytes(data, secsInByte));
        final ChatMessage chatMessage = new ChatMessage(file.file().getAbsolutePath(),
                                        ChatMessage.FromType.USER_VOICE, myUserId, String.valueOf(totalSecs));
        newMessage(chatMessage);

        Flurry.log(FlurryEvent.SendChatAudio);
    }

    public void newMessage(ChatMessage chatMessage){
        RoomUser roomUser = null;
        if(chatMessage.getSenderId() != null){
            roomUser =  room.getRoomUserByUserId(chatMessage.getSenderId());
            if(roomUser != null){
                chatControl.add(chatMessage, mode, roomUser.getProfile().getUserId(),
                        roomUser.getProfile().getDisplayName(99), roomUser.getSlotIndex(), myUserId);
            }
            else{
                Player player = room.getPlayerByUserId(chatMessage.getSenderId());
                if(player != null){
                    chatControl.add(chatMessage, mode, player.getUserId(),
                            player.getName(), player.getSlotIndex(), myUserId);
                }
            }
        }
        else{
            chatControl.add(chatMessage, mode, myUserId);
        }


        if(mode == 2){
            chatControl.fadeInMode2();
            if(!isMode2ChatLock()){
                chatControl.fadeOutMode2(5);
            }
        }

        chatControl.scrollToBottom();
    }

    public void refreshRoomUsersConnectionStatus(ArrayList<Pair<String, GameConnectionStatus>> playersConnectionStatusPairs) {
        chatControl.refreshRoomUsersPopupDesign(playersConnectionStatusPairs);
    }

    public void resetChat() {
        setMessageFieldText("");
        chatControl.clearChat(mode);
    }

    public void resetAllChat() {
        setMessageFieldText("");
        chatControl.clearChat(1);
        chatControl.clearChat(2);
    }

    public void render(float delta){
        if(isVisible()){
            chatControl.render(delta);
        }
    }

    public void resize(int width, int height){
        chatControl.resize(width, height);
    }

    public void screenTouched(float x, float y){
        if(isVisible()){
            y = Positions.getHeight() - Positions.screenYToGdxY(y) - softKeyboardHeight;
            x = Positions.screenXToGdxX(x);

            chatControl.hidePopupsIfNotTouching(x, y);

            if(!chatControl.positionHasChatElement(mode, x, y)){
                messageTextFieldUnfocus();
            }

            if(mode == 2) chatControl.fadeOutMode2IfApplicable();
        }
    }

    public void setListeners(){
        chatControl.getMessageTextField().setOnscreenKeyboard(new DummyKeyboard(broadcaster));
        chatControl.getMessageTextField().addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                broadcaster.broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo(chatControl.getMessageTextField().getText(),
                        chatControl.getMessageTextField().getCursorPosition()));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == Input.Keys.BACK){
                    messageTextFieldUnfocus();
                }
                return super.keyDown(event, keycode);
            }
        });

        chatControl.getMessageTextField().addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean _focused) {
                super.keyboardFocusChanged(event, actor, _focused);
                if(_focused){
                    focused = true;
                    messageTextFieldFocused();
                }
                else{
                    focused = false;
                    messageTextFieldUnfocus();
                }
            }
        });


        broadcaster.subscribe(BroadcastEvent.NATIVE_TEXT_CHANGED, new BroadcastListener<NativeLibgdxTextInfo>() {
            @Override
            public void onCallback(final NativeLibgdxTextInfo obj, Status st) {
                chatControl.setMessageTextFieldMsg(obj.getText(), obj.getCursorPosition());
            }
        });

        broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                invalidate();
            }
        });

        broadcaster.subscribe(BroadcastEvent.SCREEN_LAYOUT_CHANGED, new BroadcastListener<Float>() {
            @Override
            public void onCallback(final Float obj, Status st) {
                softKeyboardHeight = obj;
                chatControl.moveChatPosition(obj);
            }
        });

        broadcaster.subscribe(BroadcastEvent.NATIVE_TEXT_DONE_CLICKED, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                messageFieldSendMessage();
            }
        });

        broadcaster.subscribe(BroadcastEvent.NATIVE_KEYBOARD_CLOSED, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                messageTextFieldUnfocus();
            }
        });

        gamingKit.addListener(this.getClass().getName(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {

            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {
                if (identifier == UpdateRoomMatesCode.AUDIO_CHAT) {
                    try {
                        if (!senderId.equals(myUserId) && data.length > 1) {
                            Pair<byte[], byte[]> pair = BytesUtils.splitBytes(data, 1);

                            FileHandle oggFile = Gdx.files.local(recordsPath + Strings.generateUniqueRandomKey(15) + ".ogg");
                            Files.createIfNotExist(oggFile);
                            FileOutputStream fos = new FileOutputStream(oggFile.file().getAbsolutePath());
                            fos.write(pair.getSecond());
                            fos.close();

                            int totalSecs = pair.getFirst()[0];
                            ChatMessage chatMessage = new ChatMessage(oggFile.file().getAbsolutePath(), ChatMessage.FromType.USER_VOICE,
                                    senderId, String.valueOf(totalSecs));
                            newMessage(chatMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(identifier == UpdateRoomMatesCode.TEXT_CHAT){
                    if (!senderId.equals(myUserId)) {
                        ChatMessage chatMessage = new ChatMessage(data);
                        newMessage(chatMessage);
                    }
                }

            }
        });

        chatControl.getMessageTextField().setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                // Handle a newline properly. If not handled here, the TextField
                // will advance to the next field.
                if (c == '\r') {
                    messageFieldSendMessage();
                }
            }
        });

        for(Actor sendButton : chatControl.getSendButtons()){
            sendButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    messageFieldSendMessage();
                }
            });
        }

        for(Actor micButton : chatControl.getMicButtons()){
            micButton.addListener(new InputListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    stopRecord(700);
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    originalTouchedPositionY = y;
                    startRecord();
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    super.touchDragged(event, x, y, pointer);
                    if (y - originalTouchedPositionY > 30) {
                        cancelRecord();
                    }
                }
            });
        }

        chatTemplateControl.setChatTemplateSelectedListener(new ChatTemplateSelectedListener() {
            @Override
            public void onSelected(String template) {
                sendMessage(template, true);
                chatControl.hideChatTemplatePopup();
            }
        });

    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isMode2ChatLock() {
        return mode2ChatLock;
    }

    public void setMode2ChatLock(boolean mode2ChatLock) {
        this.mode2ChatLock = mode2ChatLock;
    }

    public ChatControl getChatControl() {
        return chatControl;
    }

    public int getCurrentMode() {
        return mode;
    }

    public String getClassTag(){
        return this.getClass().getName();
    }
}
