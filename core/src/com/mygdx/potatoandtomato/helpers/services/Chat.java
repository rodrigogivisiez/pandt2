package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.gamingkit.MessagingListener;
import com.mygdx.potatoandtomato.absintflis.recorder.RecordListener;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.absintflis.uploader.UploadListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.controls.DummyButton;
import com.mygdx.potatoandtomato.helpers.controls.DummyKeyboard;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.NativeLibgdxTextInfo;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class Chat {

    private Table _chatRoot, _mode1AllMessagesTable, _messageBoxTable;
    private Table _mode2AllMessagesTable;


    private Stage _stage;
    private Assets _assets;
    private Texts _texts;
    private GamingKit _gamingKit;
    private TextField _messageTextField;
    private Image _textFieldFocusImage, _textFieldNotFocusImage;
    private Image _btnMic, _btnKeyboard;
    private boolean _expanded;
    private Table _boxChildTable, _mode1MessagesContentTable, _sendTable;
    private Table _mode2MessagesContentTable;
    private Table _bigMicTable;
    private Room _room;
    private ScrollPane _mode1ChatScroll, _mode2ChatScroll;
    private Label _sendLabel;
    private boolean _mode1NotFirstMessage, _mode2NotFirstMessage;
    private boolean _visible;
    private SpriteBatch _batch;
    private IPTGame _game;
    private int _mode;
    private Image _micImage, _closeKeyboardImage;
    private Recorder _recorder;
    private IUploader _uploader;
    private String _recordsPath;
    private String _userId;
    private boolean _fading;
    private SoundsPlayer _soundsWrapper;
    private Broadcaster _broadcaster;

    public void setRoom(Room _room) {
        this._room = _room;
        _recordsPath = "records/" + _room.getId() + "/";
    }

    public void setUserId(String userId){
        _userId = userId;
    }

    public Chat(GamingKit gamingKit, Texts texts, Assets assets, SpriteBatch batch,
                IPTGame game, Recorder recorder, IUploader uploader, SoundsPlayer soundsWrapper,
                Broadcaster broadcaster) {
        this._broadcaster = broadcaster;
        this._gamingKit = gamingKit;
        this._soundsWrapper = soundsWrapper;
        this._texts = texts;
        this._assets = assets;
        this._batch = batch;
        this._game = game;
        this._recorder = recorder;
        this._uploader = uploader;
        this._mode = 1;

        if(batch == null) return;

        //////////////////////////////
        //Big Mic Table
        /////////////////////////////
        _bigMicTable = new Table();
        Image bigMicImage = new Image(_assets.getTextures().get(Textures.Name.MIC_BIG_ICON));
        _bigMicTable.add(bigMicImage).width(150).height(300);
        _bigMicTable.setVisible(false);

        _chatRoot = new Table();

        ////////////////////////
        //All Messages Table Mode 1
        ///////////////////////
        _mode1AllMessagesTable = new Table();
        _mode1AllMessagesTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.CHAT_CONTAINER)));
        _mode1AllMessagesTable.align(Align.top);

        _mode1MessagesContentTable = new Table();
        _mode1MessagesContentTable.align(Align.top);
        _mode1ChatScroll = new ScrollPane(_mode1MessagesContentTable);
        _mode1ChatScroll.setScrollingDisabled(true, false);
        _mode1AllMessagesTable.add(_mode1ChatScroll).expand().fill().padLeft(15).padRight(15).padTop(3);

        /////////////////////////////
        //All Messages Table Mode 2
        /////////////////////////////
        _mode2AllMessagesTable = new Table();
        _mode2AllMessagesTable.setTouchable(Touchable.disabled);
        _mode2AllMessagesTable.setPosition(0, 70);

        _mode2MessagesContentTable = new Table();
        _mode2MessagesContentTable.align(Align.bottomLeft);
        _mode2ChatScroll = new ScrollPane(_mode2MessagesContentTable);
        _mode2AllMessagesTable.add(_mode2ChatScroll).expand().fill().padLeft(80).padRight(20);

        ////////////////////////////////
        //Bottom message box
        ///////////////////////////////////
        _messageBoxTable = new Table();
        _messageBoxTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));
        _boxChildTable = new Table();
        _boxChildTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.CHAT_BOX)));
        new DummyButton(_boxChildTable, _assets);
        _messageBoxTable.add(_boxChildTable).expandX().fillX().padLeft(15).padRight(15).padTop(3).padBottom(3);

        _textFieldFocusImage = new Image(_assets.getTextures().get(Textures.Name.ORANGE_HORIZONTAL_LINE));
        _textFieldNotFocusImage = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
        _textFieldFocusImage.setVisible(false);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.CURSOR_BLACK));
        _messageTextField = new TextField("", textFieldStyle);
        _messageTextField.setOnscreenKeyboard(new DummyKeyboard(_broadcaster));
        _messageTextField.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                _broadcaster.broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo(_messageTextField.getText(),
                        _messageTextField.getCursorPosition()));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                return true;
            }
        });

        _broadcaster.subscribe(BroadcastEvent.NATIVE_TEXT_CHANGED, new BroadcastListener<NativeLibgdxTextInfo>() {
            @Override
            public void onCallback(final NativeLibgdxTextInfo obj, Status st) {
                Threadings.renderFor(0.2f);
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _messageTextField.setText(obj.getText());
                        _messageTextField.setCursorPosition(obj.getCursorPosition());
                    }
                });
            }
        });

        _micImage = new Image(_assets.getTextures().get(Textures.Name.MIC_ICON));
        _closeKeyboardImage = new Image(_assets.getTextures().get(Textures.Name.CLOSE_KEYBOARD_ICON));

        ///////////////////////////////
        //Send Label
        /////////////////////////////////
        _sendTable = new Table();
        new DummyButton(_sendTable, _assets);

        Label.LabelStyle sendLabelStyle = new Label.LabelStyle();
        sendLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXXL_REGULAR);
        sendLabelStyle.fontColor = Color.valueOf("f05837");
        _sendLabel = new Label(_texts.send(), sendLabelStyle);
        _sendTable.add(_sendLabel);

        _boxChildTable.add(_messageTextField).expandX().fillX().padLeft(15).padRight(40).padTop(5).padBottom(5);
        _boxChildTable.add(_sendTable).width(70).expandY().fillY();

        ////////////////////////////////////
        //hidden micbutton/keyboard button
        ////////////////////////////////////
        Table hiddenTable = new Table();
        _btnKeyboard = new Image(_assets.getTextures().get(Textures.Name.KEYBOARD_BUTTON));
        _btnMic = new Image(_assets.getTextures().get(Textures.Name.MIC_BUTTON));
        hiddenTable.add(_btnKeyboard).size(35, 35).padLeft(5);
        hiddenTable.add(_btnMic).size(35, 35);
        hiddenTable.setPosition(Positions.getHeight(), 0);      //use height because it only appear in landscape mode
        hiddenTable.setSize(75, 35);
        _messageBoxTable.addActor(hiddenTable);


        //////////////////////////////////////
        //populating
        /////////////////////////////////////
        _chatRoot.add(_mode1AllMessagesTable).expandX().fillX().height(130);
        _chatRoot.row();
        _chatRoot.add(_messageBoxTable).expandX().fillX().height(60);

        _chatRoot.setBounds(0, 0, Positions.getWidth(), _chatRoot.getPrefHeight());

        invalidate();

        attachListeners();

        broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener<Integer>() {
            @Override
            public void onCallback(Integer obj, Status st) {
                invalidate();
            }
        });

    }

    public void invalidate(){
        boolean isShowing = isVisible();

        if(_stage != null){
            if(isShowing) hide();
            _stage.dispose();
            _chatRoot.remove();
            _textFieldFocusImage.remove();
            _textFieldNotFocusImage.remove();
            _bigMicTable.remove();
            _micImage.remove();
            _closeKeyboardImage.remove();
            _mode2AllMessagesTable.remove();
        }

        final StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _bigMicTable.setSize(Positions.getWidth(), Positions.getHeight());
        _bigMicTable.setPosition(0, 0);

        _textFieldFocusImage.setWidth(Global.IS_POTRAIT ? 246 : 520);
        _textFieldFocusImage.setHeight(1);
        _textFieldFocusImage.setPosition(10, 5);
        _boxChildTable.addActor(_textFieldFocusImage);
        _textFieldNotFocusImage.setWidth(Global.IS_POTRAIT ? 246 : 520);
        _textFieldNotFocusImage.setHeight(1);
        _textFieldNotFocusImage.setPosition(10, 5);
        _boxChildTable.addActor(_textFieldNotFocusImage);
        _micImage.setSize(18, 30);
        _micImage.setPosition(Global.IS_POTRAIT ? 227 : 510, 13);
        _boxChildTable.addActor(_micImage);
        if(!Global.IS_POTRAIT){
            _closeKeyboardImage.setSize(40, 30);
            _closeKeyboardImage.setPosition(460 ,13);
            _boxChildTable.addActor(_closeKeyboardImage);
        }


        _mode2AllMessagesTable.setSize(Positions.getWidth(), Global.IS_POTRAIT ? 80 : 70);
        _stage.addActor(_mode2AllMessagesTable);

        _chatRoot.setPosition(0, 0);
        _chatRoot.setSize(Positions.getWidth(), _chatRoot.getPrefHeight());
        _chatRoot.addActor(_bigMicTable);

        _stage.addActor(_chatRoot);

        if(isShowing) {
            show();
            scrollToBottom();
        }
    }

    public void setMode(int mode){
        _mode = mode;
        if(mode == 1){
            _mode1AllMessagesTable.setVisible(true);
            _mode2AllMessagesTable.setVisible(false);
        }
        else if(mode == 2){
            _mode2AllMessagesTable.setVisible(true);
            _mode1AllMessagesTable.setVisible(false);
        }
    }

    public void expanded(){
        _expanded = true;

        if(_mode == 2){
           fadeInMode2();
            _mode2AllMessagesTable.setTouchable(Touchable.enabled);
        }
    }

    public void collapsed(){
        _expanded = false;
        _stage.setKeyboardFocus(_stage.getActors().get(0));
        _textFieldNotFocusImage.setVisible(true);
        _textFieldFocusImage.setVisible(false);

        if(_mode == 2){
            fadeOutMode2();
            _mode2AllMessagesTable.setTouchable(Touchable.disabled);
        }

    }

    public void hide(){
        _visible = false;
        _game.removeInputProcessor(_stage);
    }

    public void show(){
        if(!_visible){
            _visible = true;
            _game.addInputProcessor(_stage, 10);
        }
    }

    public void animateHideForMode2(){
        if(_mode == 2){
            _messageBoxTable.clearActions();
            _messageBoxTable.addAction(sequence(moveTo(-Positions.getWidth(), 0, 1f, Interpolation.exp10Out), new Action() {
                @Override
                public boolean act(float delta) {
                    collapsed();
                    return true;
                }
            }));

        }
    }

    public void animateShowForMode2(){
        if(_mode == 2){
            _messageBoxTable.clearActions();
            _messageBoxTable.addAction(moveTo(0, 0, 0.3f));
            expanded();
        }
    }


    public boolean isVisible() {
        return _visible;
    }

    private void moveChatPosition(float newY){
        _chatRoot.setPosition(0, newY);
        _mode2AllMessagesTable.setPosition(0, Global.IS_POTRAIT ? 80 + newY : 50 + newY);
        if(newY > 0){
            expanded();
        }
        else{
            collapsed();
        }
    }


    private void attachListeners(){

        _broadcaster.subscribe(BroadcastEvent.SCREEN_LAYOUT_CHANGED, new BroadcastListener<Float>() {
            @Override
            public void onCallback(final Float obj, Status st) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        moveChatPosition(obj);
                    }
                });
            }
        });

        _gamingKit.addListener(this.getClass().getName(), new MessagingListener() {
            @Override
            public void onRoomMessageReceived(ChatMessage chatMessage, String senderId) {
                add(chatMessage, false);
            }
        });

        _boxChildTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _stage.setKeyboardFocus(_messageTextField);
            }
        });

        _messageTextField.addListener(new FocusListener() {
            @Override
            public boolean handle(Event event) {
                String eventString = "";
                if(event instanceof FocusEvent){
                    FocusEvent focusEvent = (FocusEvent) event;
                    eventString = focusEvent.getType().name();
                    if(!focusEvent.isFocused()){
                        if(_mode == 1) Threadings.setContinuousRenderLock(false);
                        return false;
                    }
                }
                else{
                    eventString = event.toString();
                }

                if (eventString.equals("keyboard") ||
                        eventString.equals("touchDown") || eventString.equals("touchUp")) {
                    _textFieldFocusImage.setVisible(true);
                    _textFieldNotFocusImage.setVisible(false);
                    if(!_expanded) expanded();
                    if(_mode == 1) Threadings.setContinuousRenderLock(true);
                    return true;
                }
                if(_mode == 1)  Threadings.setContinuousRenderLock(false);
                return false;
            }
        });

        _messageTextField.setTextFieldListener(new TextField.TextFieldListener()
        {
            @Override
            public void keyTyped(TextField textField, char c)
            {
                // Handle a newline properly. If not handled here, the TextField
                // will advance to the next field.
                if (c == '\r')
                {
                    sendMessage();
                }
            }
        });

        _sendTable.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                sendMessage();
            }
        });

        _btnKeyboard.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                animateShowForMode2();
            }
        });

        _btnMic.addListener(new InputListener(){
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

        _micImage.addListener(new InputListener(){
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

        _closeKeyboardImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                animateHideForMode2();
            }
        });

    }

    private void micTouchUp(){
        _recorder.stopRecording();
        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                _soundsWrapper.setVolume(1);
            }
        });

        _bigMicTable.setVisible(false);
        _bigMicTable.clearActions();
    }

    private void micTouchDown(){
        _soundsWrapper.playSoundEffect(Sounds.Name.MIC);
        _bigMicTable.addAction(sequence(fadeOut(0f), forever(sequence(fadeOut(0.6f), fadeIn(0.6f)))));
        _bigMicTable.setVisible(true);
        final String fileName =  System.currentTimeMillis() + "_" + MathUtils.random(0, 10000) + ".bin";
        final FileHandle file = Gdx.files.local(_recordsPath + fileName);

        Threadings.delay(500, new Runnable() {
            @Override
            public void run() {
                _soundsWrapper.setVolume(0);
                _recorder.recordToFile(file, new RecordListener(){
                    @Override
                    public void onFinishedRecord(FileHandle resultFile, Status status) {
                        if(status == Status.SUCCESS){
                            sendVoiceMessage(file);
                        }
                    }
                });
            }
        });
    }

    private void setVoiceListener(Actor voice, final String fileName, boolean autoPlay){
        final FileHandle fileHandle = Gdx.files.local(_recordsPath + fileName);
        voice.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(fileHandle.exists()){
                    playVoiceMessage(fileHandle);
                }
            }
        });
        if(autoPlay){
            playVoiceMessage(fileHandle);
        }
    }

    public void playVoiceMessage(FileHandle fileHandle){
        _soundsWrapper.setVolume(0);
        _recorder.playBack(fileHandle, new Runnable() {
            @Override
            public void run() {
                _soundsWrapper.setVolume(1);
            }
        });
    }

    public void setMessage(String msg){
        _messageTextField.setText(msg);
    }

    public void sendMessage(){
        String msg = _messageTextField.getText().trim();
        if(!msg.equals("")){
            ChatMessage chatMessage = new ChatMessage(msg, ChatMessage.FromType.USER, _userId);
            _gamingKit.sendRoomMessage(chatMessage);
            add(chatMessage, true);
        }
        clearMessageTextField();
    }

    public void sendVoiceMessage(final FileHandle file){
        final ChatMessage c = new ChatMessage(file.file().getName(), ChatMessage.FromType.USER_VOICE, _userId);
        add(c, true);
        _uploader.uploadFile(file, new UploadListener<String>() {
            @Override
            public void onCallBack(String result, Status status) {
                if(status == Status.SUCCESS) {
                    _gamingKit.sendRoomMessage(c);
                }
            }
        });

    }

    public void add(final ChatMessage msg, boolean force){

        if(msg.getSenderId() != null && msg.getSenderId().equals(_userId) && !force){
            return;
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if((msg.getFromType() == ChatMessage.FromType.USER_VOICE && !msg.getSenderId().equals(_userId))){
                    playVoiceMessage(Gdx.files.local(_recordsPath + msg.getMessage()));
                }

                _soundsWrapper.playSoundEffect(Sounds.Name.MESSAGING);

                if(_mode == 1){
                    Table chatTable = new Table();
                    if (!_mode1NotFirstMessage) {
                        chatTable.padTop(12);
                        _mode1NotFirstMessage = true;
                    }

                    ////////////////
                    //Styles
                    ///////////////
                    Label.LabelStyle lblUsernameStyle = new Label.LabelStyle();
                    lblUsernameStyle.fontColor = Color.BLACK;
                    lblUsernameStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD);

                    Label.LabelStyle lblMessageStyle = new Label.LabelStyle();
                    lblMessageStyle.fontColor = Color.BLACK;
                    lblMessageStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

                    Label.LabelStyle lblInfoStyle = new Label.LabelStyle();
                    lblInfoStyle.fontColor = Color.valueOf("11b1bf");
                    lblInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

                    Label.LabelStyle lblImportantStyle = new Label.LabelStyle();
                    lblImportantStyle.fontColor = Color.valueOf("F56C57");
                    lblImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

                    if (msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
                        Profile sender = _room.getProfileByUserId(msg.getSenderId());
                        if (sender == null) return;

                        Label lblUsername = new Label(sender.getDisplayName(30) + ": ", lblUsernameStyle);
                        chatTable.add(lblUsername).minHeight(20).padRight(5).padLeft(5);

                        if(msg.getFromType() == ChatMessage.FromType.USER){
                            Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
                            lblMessage.setWrap(true);
                            chatTable.add(lblMessage).expandX().fillX().minHeight(20);
                            chatTable.row();
                        }
                        else if(msg.getFromType() == ChatMessage.FromType.USER_VOICE){
                            Image imgVoice = new Image(_assets.getTextures().get(Textures.Name.VOICE_ICON));
                            chatTable.add(imgVoice).size(20, 20).expandX().left();
                            chatTable.row();
                            setVoiceListener(imgVoice, msg.getMessage(), !msg.getSenderId().equals(_userId));
                        }
                    } else {
                        // Image icon = new Image(msg.getFromType() == ChatMessage.FromType.SYSTEM ? _assets.getInfoIcon() : _assets.getImportantIcon());
                        Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);
                        lblMessage.setWrap(true);
                        // chatTable.add(icon).size(20, 20).padRight(5).padLeft(5);
                        chatTable.add(lblMessage).colspan(2).expandX().fillX().minHeight(20).padLeft(5).padRight(5);
                        chatTable.row();
                    }

                    Image separator = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
                    chatTable.add(separator).colspan(3).padTop(5).padBottom(5).expandX().fillX();
                    chatTable.row();

                    _mode1MessagesContentTable.add(chatTable).expandX().fillX();
                    _mode1MessagesContentTable.row();
                }
                else if(_mode == 2){

                    fadeInMode2();

                    Table chatTable = new Table();
                    chatTable.align(Align.left);
                    if (!_mode2NotFirstMessage) {
                        chatTable.padTop(12);
                        _mode2NotFirstMessage = true;
                    }

                    Label.LabelStyle labelStyle = new Label.LabelStyle();
                    labelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR_B_ffffff_000000_1);
                    if(_room != null && _userId != null) labelStyle.fontColor = _room.getUserColorByUserId(msg.getSenderId());

                    Label.LabelStyle labelInfoStyle = new Label.LabelStyle();
                    labelInfoStyle.fontColor = Color.valueOf("11b1bf");
                    labelInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);

                    Label.LabelStyle labelImportantStyle = new Label.LabelStyle();
                    labelImportantStyle.fontColor = Color.valueOf("F56C57");
                    labelImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);

                    if(msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
                        Profile sender = _room.getProfileByUserId(msg.getSenderId());
                        if (sender == null) return;

                        Label userNameLabel = new Label(sender.getDisplayName(30) + ":", labelStyle);
                        chatTable.add(userNameLabel).top().padRight(5);
                    }

                    if(msg.getFromType() == ChatMessage.FromType.USER){
                        Label.LabelStyle labelStyle2 = new Label.LabelStyle();
                        labelStyle2.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
                        labelStyle2.fontColor = Color.WHITE;
                        Label messageLabel = new Label(msg.getMessage(), labelStyle2);
                        messageLabel.setWrap(true);
                        messageLabel.setAlignment(Align.left);
                        chatTable.add(messageLabel).expandX().fillX();
                    }
                    else if(msg.getFromType() == ChatMessage.FromType.USER_VOICE){
                        Image imgVoice = new Image(_assets.getTextures().get(Textures.Name.VOICE_ICON));
                        chatTable.add(imgVoice).size(15, 15).expandX().left();
                        chatTable.row();
                        setVoiceListener(imgVoice, msg.getMessage(), !msg.getSenderId().equals(_userId));
                    }
                    else{
                        Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? labelInfoStyle : labelImportantStyle);
                        lblMessage.setWrap(true);
                        lblMessage.setAlignment(Align.left);
                        chatTable.add(lblMessage).colspan(2).expandX().fillX();
                        chatTable.row();
                    }

                    _mode2MessagesContentTable.add(chatTable).expandX().fillX();
                    _mode2MessagesContentTable.row();
                }

                scrollToBottom();
            }
        };

        if(msg.getFromType() == ChatMessage.FromType.USER_VOICE && !msg.getSenderId().equals(_userId)){
            String fileName = msg.getMessage();
            final FileHandle fileHandle = Gdx.files.local(_recordsPath + fileName);
            _uploader.getUploadedFile(fileName, fileHandle, new UploadListener<FileHandle>() {
                @Override
                public void onCallBack(FileHandle result, Status status) {
                    if(status == Status.SUCCESS){
                        Threadings.postRunnable(runnable);
                    }
                }
            });
        }
        else{
            Threadings.postRunnable(runnable);
        }

    }

    public void scrollToBottom(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_mode == 1){
                    _mode1ChatScroll.setScrollPercentY(100);
                }
                else if(_mode == 2){
                    _mode2ChatScroll.setScrollPercentY(100);
                }
            }
        });
    }

    private void fadeInMode2(){
        _fading = false;
        _mode2MessagesContentTable.clearActions();
        _mode2MessagesContentTable.getColor().a = 1;
        fadeOutMode2();
    }

    private void fadeOutMode2(){
        if(_fading) return;

        if(!_expanded){
            _fading = true;
            _mode2MessagesContentTable.addAction(sequence(delay(5), fadeOut(0.3f), new Action() {
                @Override
                public boolean act(float delta) {
                    _fading = false;
                    return true;
                }
            }));
        }
    }


    public void resetChat() {
        _expanded = false;
        clearMessageTextField();

        if(_mode == 1){
            _mode1MessagesContentTable.clear();
            _mode1NotFirstMessage = false;
        }
        else if(_mode == 2){
            _mode2MessagesContentTable.clear();
            _mode2NotFirstMessage = false;
        }

    }

    public void render(float delta){
        if(isVisible()){
            try{
                _stage.act(delta);
                _stage.draw();
            }
            catch (Exception e){

            }
        }
    }

    public void clearMessageTextField(){
        _messageTextField.setText("");
        _broadcaster.broadcast(BroadcastEvent.LIBGDX_TEXT_CHANGED, new NativeLibgdxTextInfo("", 0));
    }


    public void screenTouched(float x, float y){
        if(isVisible() && _mode == 2){
            y = Positions.getHeight() - y;
            if(y > _messageBoxTable.getPrefHeight()){
                collapsed();
            }
        }
    }

    public void resize(int width, int height){
        _stage.getViewport().update(width, height);
    }

}
