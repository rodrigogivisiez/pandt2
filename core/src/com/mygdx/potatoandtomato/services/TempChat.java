package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.gamingkit.MessagingListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesCode;
import com.mygdx.potatoandtomato.absintflis.gamingkit.UpdateRoomMatesListener;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.ChatControl;
import com.mygdx.potatoandtomato.controls.DummyButton;
import com.mygdx.potatoandtomato.controls.DummyKeyboard;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.utils.Files;
import com.mygdx.potatoandtomato.utils.Logs;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class TempChat {

    private Services services;
    private Room room;
    private boolean visible, expanded;
    private IPTGame ptGame;
    private int mode;
    private String recordsPath;
    private ChatControl chatControl;

    public void setRoom(Room room) {
        this.room = room;
        recordsPath = "records/" + room.getId() + "/";
    }

    public TempChat(Services services, SpriteBatch batch, IPTGame game) {
        this.services = services;
        this.ptGame = game;
        this.mode = 1;

        chatControl = new ChatControl(services.getTexts(), services.getAssets(), batch, services.getSoundsPlayer());

        invalidate();
        setListeners();
    }

    public void setListeners(){
        chatControl.getMessageTextField().setOnscreenKeyboard(new DummyKeyboard(services.getBroadcaster()));
        chatControl.getMessageTextField().addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                services.getBroadcaster().broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo(chatControl.getMessageTextField().getText(),
                        chatControl.getMessageTextField().getCursorPosition()));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                return true;
            }
        });

        services.getBroadcaster().subscribe(BroadcastEvent.NATIVE_TEXT_CHANGED, new BroadcastListener<NativeLibgdxTextInfo>() {
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

        services.getBroadcaster().subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                invalidate();
            }
        });

        services.getBroadcaster().subscribe(BroadcastEvent.SCREEN_LAYOUT_CHANGED, new BroadcastListener<Float>() {
            @Override
            public void onCallback(final Float obj, Status st) {
                chatControl.moveChatPosition(obj);
            }
        });

        services.getGamingKit().addListener(this.getClass().getName(), new MessagingListener() {
            @Override
            public void onRoomMessageReceived(ChatMessage chatMessage, String senderId) {
                add(chatMessage, false);
            }
        });

        services.getGamingKit().addListener(this.getClass().getName(), new UpdateRoomMatesListener() {
            @Override
            public void onUpdateRoomMatesReceived(int code, String msg, String senderId) {

            }

            @Override
            public void onUpdateRoomMatesReceived(byte identifier, byte[] data, String senderId) {
                if (identifier == UpdateRoomMatesCode.AUDIO_CHAT) {
                    try {
                        if (!senderId.equals(services.getProfile().getUserId())) {
                            FileHandle oggFile = Gdx.files.local(recordsPath + Strings.generateUniqueRandomKey(15) + ".ogg");
                            Files.createIfNotExist(oggFile);
                            FileOutputStream fos = new FileOutputStream(oggFile.file().getAbsolutePath());
                            fos.write(data);
                            fos.close();
                            chatControl.add(new ChatMessage(oggFile.name(), ChatMessage.FromType.USER_VOICE, senderId),
                                    mode, room.getRoomUserByUserId(senderId));
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
                chatControl.focusMessageTextField();
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
                    chatControl.focusMessageTextField();
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
                    sendMessage();
                }
            }
        });

        chatControl.getSendTable().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sendMessage();
            }
        });

        chatControl.getBtnMic().addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                micTouchUp();
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                micTouchDown();
                return true;
            }
        });

        chatControl.getMicImage().addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                micTouchUp();
                super.touchUp(event, x, y, pointer, button);

            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                micTouchDown();
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
    }


    public void invalidate(){
         chatControl.invalidate(isVisible());
    }

    public void setMode(final int mode){
        this.mode = mode;
        chatControl.modeChanged(mode);
    }

    //expand and collapse valid for mode2 only
    public void expandMode2(){
        fadeInMode2();
    }

    //expand and collapse valid for mode2 only
    public void collapseMode2(){
        chatControl.unfocusMessageTextField();
        fadeOutMode2();
    }

    public void hide(){
        setVisible(false);
        ptGame.removeInputProcessor(chatControl.getStage());
    }

    public void show(){
        if(!isVisible()){
            setVisible(true);
            ptGame.addInputProcessor(chatControl.getStage(), 10);
        }
    }

//    private void micTouchUp(){
//        _recorder.stopRecording();
//        Threadings.delay(1000, new Runnable() {
//            @Override
//            public void run() {
//                _soundsPlayer.setVolume(1);
//            }
//        });
//
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                _bigMicTable.setVisible(false);
//                _bigMicTable.clearActions();
//            }
//        });
//    }
//
//    private void micTouchDown(){
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                _soundsPlayer.playSoundEffect(Sounds.Name.MIC);
//                _bigMicTable.addAction(sequence(fadeOut(0f), forever(sequence(fadeOut(0.6f), fadeIn(0.6f)))));
//                _bigMicTable.setVisible(true);
//            }
//        });
//
//        Threadings.delay(500, new Runnable() {
//            @Override
//            public void run() {
//                _soundsPlayer.setVolume(0);
//                _recorder.recordToFile(recordsPath, new RecordListener(){
//                    @Override
//                    public void onRecording(int volumeLevel) {
//                        Logs.show(volumeLevel);
//                    }
//
//                    @Override
//                    public void onFinishedRecord(FileHandle resultFile, Status status) {
//                        if(status == Status.SUCCESS){
//                            sendVoiceMessage(resultFile);
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void setVoiceListener(Actor voice, final String fileName, boolean autoPlay){
//        final FileHandle fileHandle = Gdx.files.local(recordsPath + fileName);
//        voice.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);
//                if(fileHandle.exists()){
//                    playVoiceMessage(fileHandle);
//                }
//            }
//        });
//        if(autoPlay){
//            playVoiceMessage(fileHandle);
//        }
//    }
//
//    public void playVoiceMessage(FileHandle fileHandle){
//        _soundsPlayer.setVolume(0);
//        _recorder.playBack(fileHandle, new Runnable() {
//            @Override
//            public void run() {
//                _soundsPlayer.setVolume(1);
//            }
//        });
//    }
//
//    public void setMessage(final String msg){
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                _messageTextField.setText(msg);
//            }
//        });
//    }
//
//    public void sendMessage(){
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                String msg = _messageTextField.getText().trim();
//                if(!msg.equals("")){
//                    ChatMessage chatMessage = new ChatMessage(msg, ChatMessage.FromType.USER, _userId);
//                    _gamingKit.sendRoomMessage(chatMessage);
//                    add(chatMessage, true);
//                }
//                clearMessageTextField();
//            }
//        });
//    }
//
//    public void sendVoiceMessage(final FileHandle file){
//
//        byte[] data = Files.fileToByte(file.file());
//        _gamingKit.updateRoomMates((byte) UpdateRoomMatesCode.AUDIO_CHAT, data);
//
//        final ChatMessage c = new ChatMessage(file.file().getName(), ChatMessage.FromType.USER_VOICE, _userId);
//        add(c, true);
////        _uploader.uploadFile(file, new UploadListener<String>() {
////            @Override
////            public void onCallBack(String result, Status status) {
////                if(status == Status.SUCCESS) {
////                    _gamingKit.sendRoomMessage(c);
////                }
////            }
////        });
//
//    }
//
//    public void add(final ChatMessage msg, boolean force){
//
//        if(msg.getSenderId() != null && msg.getSenderId().equals(_userId) && !force){
//            return;
//        }
//
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if((msg.getFromType() == ChatMessage.FromType.USER_VOICE && !msg.getSenderId().equals(_userId))){
//                    playVoiceMessage(Gdx.files.local(recordsPath + msg.getMessage()));
//                }
//
//                _soundsPlayer.playSoundEffect(Sounds.Name.MESSAGING);
//
//                if(mode == 1){
//                    Table chatTable = new Table();
//                    if (!_mode1NotFirstMessage) {
//                        chatTable.padTop(12);
//                        _mode1NotFirstMessage = true;
//                    }
//
//                    ////////////////
//                    //Styles
//                    ///////////////
//                    Label.LabelStyle lblUsernameStyle = new Label.LabelStyle();
//                    lblUsernameStyle.fontColor = Color.BLACK;
//                    lblUsernameStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD);
//
//                    Label.LabelStyle lblMessageStyle = new Label.LabelStyle();
//                    lblMessageStyle.fontColor = Color.BLACK;
//                    lblMessageStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
//
//                    Label.LabelStyle lblInfoStyle = new Label.LabelStyle();
//                    lblInfoStyle.fontColor = Color.valueOf("11b1bf");
//                    lblInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
//
//                    Label.LabelStyle lblImportantStyle = new Label.LabelStyle();
//                    lblImportantStyle.fontColor = Color.valueOf("F56C57");
//                    lblImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
//
//                    if (msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
//                        Profile sender = room.getProfileByUserId(msg.getSenderId());
//                        if (sender == null) return;
//
//                        Label lblUsername = new Label(sender.getDisplayName(30) + ": ", lblUsernameStyle);
//                        chatTable.add(lblUsername).minHeight(20).padRight(5).padLeft(5);
//
//                        if(msg.getFromType() == ChatMessage.FromType.USER){
//                            Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
//                            lblMessage.setWrap(true);
//                            chatTable.add(lblMessage).expandX().fillX().minHeight(20);
//                            chatTable.row();
//                        }
//                        else if(msg.getFromType() == ChatMessage.FromType.USER_VOICE){
//                            Image imgVoice = new Image(_assets.getTextures().get(Textures.Name.VOICE_ICON));
//                            chatTable.add(imgVoice).size(20, 20).expandX().left();
//                            chatTable.row();
//                            setVoiceListener(imgVoice, msg.getMessage(), !msg.getSenderId().equals(_userId));
//                        }
//                    } else {
//                        // Image icon = new Image(msg.getFromType() == ChatMessage.FromType.SYSTEM ? _assets.getInfoIcon() : _assets.getImportantIcon());
//                        Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);
//                        lblMessage.setWrap(true);
//                        // chatTable.add(icon).size(20, 20).padRight(5).padLeft(5);
//                        chatTable.add(lblMessage).colspan(2).expandX().fillX().minHeight(20).padLeft(5).padRight(5);
//                        chatTable.row();
//                    }
//
//                    Image separator = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
//                    chatTable.add(separator).colspan(3).padTop(5).padBottom(5).expandX().fillX();
//                    chatTable.row();
//
//                    _mode1MessagesContentTable.add(chatTable).expandX().fillX();
//                    _mode1MessagesContentTable.row();
//                }
//                else if(mode == 2){
//
//                    fadeInMode2();
//
//                    Table chatTable = new Table();
//                    chatTable.align(Align.left);
//                    if (!_mode2NotFirstMessage) {
//                        chatTable.padTop(12);
//                        _mode2NotFirstMessage = true;
//                    }
//
//                    Label.LabelStyle labelStyle = new Label.LabelStyle();
//                    labelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR_B_ffffff_000000_1);
//                    if(room != null && _userId != null) labelStyle.fontColor = room.getUserColorByUserId(msg.getSenderId());
//
//                    Label.LabelStyle labelInfoStyle = new Label.LabelStyle();
//                    labelInfoStyle.fontColor = Color.valueOf("11b1bf");
//                    labelInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
//
//                    Label.LabelStyle labelImportantStyle = new Label.LabelStyle();
//                    labelImportantStyle.fontColor = Color.valueOf("F56C57");
//                    labelImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
//
//                    if(msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
//                        Profile sender = room.getProfileByUserId(msg.getSenderId());
//                        if (sender == null) return;
//
//                        Label userNameLabel = new Label(sender.getDisplayName(30) + ":", labelStyle);
//                        chatTable.add(userNameLabel).top().padRight(5);
//                    }
//
//                    if(msg.getFromType() == ChatMessage.FromType.USER){
//                        Label.LabelStyle labelStyle2 = new Label.LabelStyle();
//                        labelStyle2.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
//                        labelStyle2.fontColor = Color.WHITE;
//                        Label messageLabel = new Label(msg.getMessage(), labelStyle2);
//                        messageLabel.setWrap(true);
//                        messageLabel.setAlignment(Align.left);
//                        chatTable.add(messageLabel).expandX().fillX();
//                    }
//                    else if(msg.getFromType() == ChatMessage.FromType.USER_VOICE){
//                        Image imgVoice = new Image(_assets.getTextures().get(Textures.Name.VOICE_ICON));
//                        chatTable.add(imgVoice).size(15, 15).expandX().left();
//                        chatTable.row();
//                        setVoiceListener(imgVoice, msg.getMessage(), !msg.getSenderId().equals(_userId));
//                    }
//                    else{
//                        Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? labelInfoStyle : labelImportantStyle);
//                        lblMessage.setWrap(true);
//                        lblMessage.setAlignment(Align.left);
//                        chatTable.add(lblMessage).colspan(2).expandX().fillX();
//                        chatTable.row();
//                    }
//
//                    _mode2MessagesContentTable.add(chatTable).expandX().fillX();
//                    _mode2MessagesContentTable.row();
//                }
//
//                scrollToBottom();
//            }
//        };
//
////        if(msg.getFromType() == ChatMessage.FromType.USER_VOICE && !msg.getSenderId().equals(_userId)){
////            String fileName = msg.getMessage();
////            final FileHandle fileHandle = Gdx.files.local(recordsPath + fileName);
////            _uploader.getUploadedFile(fileName, fileHandle, new UploadListener<FileHandle>() {
////                @Override
////                public void onCallBack(FileHandle result, Status status) {
////                    if(status == Status.SUCCESS){
////                        Threadings.postRunnable(runnable);
////                    }
////                }
////            });
////        }
////        else{
//        Threadings.postRunnable(runnable);
//        //  }
//
//    }
//
//    public void scrollToBottom(){
//        Threadings.delay(100, new Runnable() {
//            @Override
//            public void run() {
//                if (mode == 1) {
//                    _mode1ChatScroll.setScrollPercentY(100);
//                } else if (mode == 2) {
//                    _mode2ChatScroll.setScrollPercentY(100);
//                }
//            }
//        });
//    }
//
//    private void fadeInMode2(){
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                _fading = false;
//                _mode2MessagesContentTable.clearActions();
//                _mode2MessagesContentTable.getColor().a = 1;
//                fadeOutMode2();
//            }
//        });
//    }
//
//    private void fadeOutMode2(){
//
//        if(_fading) return;
//
//        if(!_expanded){
//            _fading = true;
//            Threadings.postRunnable(new Runnable() {
//                @Override
//                public void run() {
//                    _mode2MessagesContentTable.addAction(sequence(delay(5), fadeOut(0.3f), new RunnableAction() {
//                        @Override
//                        public void run() {
//                            _fading = false;
//                        }
//                    }));
//                }
//            });
//
//        }
//    }
//
//
//    public void resetChat() {
//        _expanded = false;
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                clearMessageTextField();
//
//                if(mode == 1){
//                    _mode1MessagesContentTable.clear();
//                    _mode1NotFirstMessage = false;
//                }
//                else if(mode == 2){
//                    _mode2MessagesContentTable.clear();
//                    _mode2NotFirstMessage = false;
//                }
//            }
//        });
//
//
//
//    }
//
//    public void render(float delta){
//        if(isVisible()){
//            try{
//                _stage.act(delta);
//                _stage.draw();
//            }
//            catch (Exception e){
//
//            }
//        }
//    }
//
//    public void clearMessageTextField(){
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                _messageTextField.setText("");
//                _broadcaster.broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo("", 0));
//            }
//        });
//    }
//
//
//    public void screenTouched(float x, float y){
//        if(isVisible() && mode == 2){
//            y = Positions.getHeight() - y;
//            if(y > _messageBoxTable.getPrefHeight()){
//                collapsed();
//            }
//        }
//    }

//    public void resize(int width, int height){
//        _stage.getViewport().update(width, height);
//    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
