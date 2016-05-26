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
import com.mygdx.potatoandtomato.absintflis.controls.StageChangedListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.MessagingListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.controls.ChatControl;
import com.mygdx.potatoandtomato.controls.ChatTemplateControl;
import com.mygdx.potatoandtomato.controls.DummyKeyboard;
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
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.io.FileOutputStream;
import java.io.IOException;

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

    public Chat(Broadcaster broadcaster, GamingKit gamingKit, Texts texts, Assets assets,
                        SoundsPlayer soundsPlayer, Recorder recorder, SpriteBatch batch, IPTGame game, Preferences preferences) {
        this.broadcaster = broadcaster;
        this.ptGame = game;
        this.gamingKit = gamingKit;
        this.recorder = recorder;
        this.soundsPlayer = soundsPlayer;
        this.mode = 1;

        chatTemplateControl = new ChatTemplateControl(assets, preferences);
        chatControl = new ChatControl(texts, assets, soundsPlayer, recorder, batch, chatTemplateControl);


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

    //expand and collapse valid for mode2 only
    public void expandMode2(){
        setMode2ChatLock(true);
        chatControl.fadeInMode2();
    }

    //expand and collapse valid for mode2 only
    public void collapseMode2(){
        chatControl.unfocusMessageTextField();
        chatControl.fadeOutMode2(5);
        setMode2ChatLock(false);
    }

    public void focusOnMessageTextField(){
        if(mode == 2) expandMode2();
        chatControl.focusMessageTextField();
    }

    public void showChat(){
        if(!isVisible()){
            setVisible(true);
            chatControl.scrollToBottom();
            ptGame.addInputProcessor(chatControl.getStage(), 10);
        }
    }

    public void hideChat(){
        setVisible(false);
        chatControl.scrollToBottom();
        ptGame.removeInputProcessor(chatControl.getStage());
    }

    public void stageChanged(Stage oldStage, Stage newStage){
        if(oldStage != null) ptGame.removeInputProcessor(oldStage);
        if(isVisible()) ptGame.addInputProcessor(newStage, 10);
    }

    private void startRecord(){
        if(recorder.isCanRecord()){
            chatControl.setRecordingSoundsLevel(0);
            chatControl.showRecording();

            Threadings.delay(500, new Runnable() {
                @Override
                public void run() {
                    soundsPlayer.setVolume(0);
                    Threadings.delay(200, new Runnable() {
                        @Override
                        public void run() {
                            recorder.recordToFile(recordsPath, new RecordListener() {
                                @Override
                                public void onRecording(int volumeLevel) {
                                    chatControl.setRecordingSoundsLevel(volumeLevel);
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

    private void stopRecord(){
        Threadings.delay(700, new Runnable() {
            @Override
            public void run() {
                chatControl.hideRecording();
                recorder.stopRecording();
                Threadings.delay(1000, new Runnable() {
                    @Override
                    public void run() {
                        soundsPlayer.setVolume(1);
                    }
                });
            }
        });
    }

    public void messageFieldSendMessage(){
        String msg = chatControl.getMessageTextField().getText().trim();
        if(!msg.equals("")){
            sendMessage(msg);
            setMessageFieldText("");
        }
    }

    public void setMessageFieldText(String newMessage){
        chatControl.setMessageTextFieldMsg(newMessage);
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                broadcaster.broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo(chatControl.getMessageTextField().getText(),
                        chatControl.getMessageTextField().getCursorPosition()));
            }
        });
    }

    public void sendMessage(String msg){
        ChatMessage chatMessage = new ChatMessage(msg, ChatMessage.FromType.USER, myUserId, "");
        gamingKit.sendRoomMessage(chatMessage);
        newMessage(chatMessage);
    }

    public void sendVoiceMessage(final FileHandle file, int totalSecs){
        byte[] data = Files.fileToByte(file.file());
        byte[] secsInByte = new byte[]{(byte) totalSecs};
        gamingKit.updateRoomMates((byte) UpdateRoomMatesCode.AUDIO_CHAT, BytesUtils.prependBytes(data, secsInByte));
        final ChatMessage chatMessage = new ChatMessage(file.file().getAbsolutePath(),
                                        ChatMessage.FromType.USER_VOICE, myUserId, String.valueOf(totalSecs));
        newMessage(chatMessage);
    }

    public void newMessage(ChatMessage chatMessage){
        RoomUser roomUser = null;
        if(chatMessage.getSenderId() != null){
            roomUser =  room.getRoomUserByUserId(chatMessage.getSenderId());
        }
        chatControl.add(chatMessage, mode, roomUser, myUserId);

        if(mode == 2){
            chatControl.fadeInMode2();
            if(!isMode2ChatLock()){
                chatControl.fadeOutMode2(5);
            }
        }

        chatControl.scrollToBottom();
    }

    public void resetChat() {
        setMessageFieldText("");
        chatControl.clearChat(mode);
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
            y = Positions.getHeight() - Positions.screenYToGdxY(y);

            boolean result = chatControl.positionHasChatElement(mode, x, y);

            if(!chatControl.positionHasChatElement(mode, x, y)){
                collapseMode2();
                chatControl.unfocusMessageTextField();
            }
            chatTemplateControl.screenTouched(x, y);
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
                    chatControl.unfocusMessageTextField();
                }
                return super.keyDown(event, keycode);
            }
        });

        broadcaster.subscribe(BroadcastEvent.NATIVE_TEXT_CHANGED, new BroadcastListener<NativeLibgdxTextInfo>() {
            @Override
            public void onCallback(final NativeLibgdxTextInfo obj, Status st) {
                Threadings.renderFor(0.2f);
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        chatControl.getMessageTextField().setText(obj.getText());
                        chatControl.getMessageTextField().setCursorPosition(obj.getCursorPosition());
                    }
                });
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
                chatControl.moveChatPosition(obj);
            }
        });

        gamingKit.addListener(this.getClass().getName(), new MessagingListener() {
            @Override
            public void onRoomMessageReceived(ChatMessage chatMessage, String senderId) {
                if (!senderId.equals(myUserId)) {
                    newMessage(chatMessage);
                }
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
            }
        });

        chatControl.getChatBoxTable().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                focusOnMessageTextField();
            }
        });

        chatControl.getMessageTextField().addListener(new FocusListener() {
            @Override
            public boolean handle(Event event) {
                String eventString = "";



                if (event instanceof FocusEvent) {
                    FocusEvent focusEvent = (FocusEvent) event;
                    eventString = focusEvent.getType().name();
                    if (!focusEvent.isFocused()) {
                        if (mode == 1) Threadings.setContinuousRenderLock(false);
                        return false;
                    }
                } else {
                    eventString = event.toString();
                }

                if (eventString.equals("keyboard") ||
                        eventString.equals("touchDown") || eventString.equals("touchUp")) {
                    focusOnMessageTextField();
                    if (mode == 1) Threadings.setContinuousRenderLock(true);
                    return true;
                }
                if (mode == 1) Threadings.setContinuousRenderLock(false);
                return false;
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

        chatControl.getSendTable().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                messageFieldSendMessage();
            }
        });

        chatControl.getBtnMic().addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                stopRecord();
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startRecord();
                return true;
            }
        });

        chatControl.getMicImage().addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                stopRecord();
                super.touchUp(event, x, y, pointer, button);

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startRecord();
                return true;
            }
        });

        chatControl.getCloseKeyboardImage().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                chatControl.animateHideForMode2();
            }
        });

        chatControl.getBtnKeyboard().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                chatControl.animateShowForMode2();
            }
        });

        chatControl.getBtnTemplate().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                chatTemplateControl.setShowing(chatControl.getBtnTemplate(), true);
            }
        });

        chatControl.setStageChangedListener(new StageChangedListener() {
            @Override
            public void onChanged(Stage oldStage, Stage newStage) {
                stageChanged(oldStage, newStage);
            }
        });

        chatTemplateControl.setChatTemplateSelectedListener(new ChatTemplateSelectedListener() {
            @Override
            public void onSelected(String template) {
                sendMessage(template);
                chatTemplateControl.setShowing(false);
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
}
