package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.utils.Colors;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.potatoandtomato.common.*;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

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
    private boolean _expanded;
    private Table _boxChildTable, _mode1MessagesContentTable, _sendTable;
    private Table _mode2MessagesContentTable;
    private Room _room;
    private ScrollPane _mode1ChatScroll, _mode2ChatScroll;
    private Label _sendLabel;
    private boolean _mode1NotFirstMessage, _mode2NotFirstMessage;
    private boolean _visible;
    private SpriteBatch _batch;
    private IPTGame _game;
    private HashMap<String, Color> _userColors;
    private int _mode;
    private Image _micImage;

    public void setRoom(Room _room) {
        this._room = _room;
    }

    public Chat(GamingKit gamingKit, Texts texts, Assets assets, SpriteBatch batch, IPTGame game) {
        this._gamingKit = gamingKit;
        this._texts = texts;
        this._assets = assets;
        this._batch = batch;
        this._game = game;
        this._mode = 1;
        this._userColors = new HashMap<>();

        if(batch == null) return;

        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _chatRoot = new Table();

        ////////////////////////
        //All Messages Table Mode 1
        ///////////////////////
        _mode1AllMessagesTable = new Table();
        _mode1AllMessagesTable.setBackground(new TextureRegionDrawable(_assets.getChatContainer()));
        _mode1AllMessagesTable.align(Align.top);

        _mode1MessagesContentTable = new Table();
        _mode1MessagesContentTable.align(Align.top);
        _mode1ChatScroll = new ScrollPane(_mode1MessagesContentTable);
        _mode1ChatScroll.setScrollingDisabled(true, false);

        //_dummyCloseButton = new Image(_assets.getEmpty());
        _mode1AllMessagesTable.add(_mode1ChatScroll).expand().fill().padLeft(20).padTop(3);
        //_mode1AllMessagesTable.add(_dummyCloseButton).width(35).height(35).top();

        /////////////////////////////
        //All Messages Table Mode 2
        /////////////////////////////
        _mode2AllMessagesTable = new Table();
        _mode2AllMessagesTable.setTouchable(Touchable.disabled);
        _mode2AllMessagesTable.setPosition(0, 70);
        _mode2AllMessagesTable.setSize(Positions.getWidth(), 90);

        _mode2MessagesContentTable = new Table();
        _mode2MessagesContentTable.align(Align.bottomLeft);
        _mode2ChatScroll = new ScrollPane(_mode2MessagesContentTable);
        _mode2AllMessagesTable.add(_mode2ChatScroll).expand().fill().padLeft(80).padRight(80);

        ////////////////////////////////
        //Bottom message box
        ///////////////////////////////////
        _messageBoxTable = new Table();
        _messageBoxTable.setBackground(new NinePatchDrawable(_assets.getYellowGradientBox()));
        _boxChildTable = new Table();
        _boxChildTable.setBackground(new NinePatchDrawable(_assets.getChatBox()));
        new DummyButton(_boxChildTable, _assets);
        _messageBoxTable.add(_boxChildTable).expandX().fillX().padLeft(15).padRight(15).padTop(3).padBottom(3);

        _textFieldFocusImage = new Image(_assets.getOrangeLine());
        _textFieldNotFocusImage = new Image(_assets.getGreyLine());
        _textFieldFocusImage.setVisible(false);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _assets.getBlackNormal3();
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(_assets.getTextCursor());
        _messageTextField = new TextField("", textFieldStyle);

        _micImage = new Image(_assets.getMicIcon());

        ///////////////////////////////
        //Send Label
        /////////////////////////////////
        _sendTable = new Table();
        new DummyButton(_sendTable, _assets);

        Label.LabelStyle sendLabelStyle = new Label.LabelStyle();
        sendLabelStyle.font = _assets.getOrangePizza3();
        _sendLabel = new Label(_texts.send(), sendLabelStyle);
        _sendTable.add(_sendLabel);

        _boxChildTable.add(_messageTextField).expandX().fillX().padLeft(15).padRight(40).padTop(5).padBottom(5);
        _boxChildTable.add(_sendTable).width(70).expandY().fillY();

        _textFieldFocusImage.setWidth(_boxChildTable.getPrefWidth() - 30);
        _textFieldFocusImage.setHeight(1);
        _textFieldFocusImage.setPosition(10, 5);
        _boxChildTable.addActor(_textFieldFocusImage);
        _textFieldNotFocusImage.setWidth(_boxChildTable.getPrefWidth() - 30);
        _textFieldNotFocusImage.setHeight(1);
        _textFieldNotFocusImage.setPosition(10, 5);
        _boxChildTable.addActor(_textFieldNotFocusImage);
        _micImage.setSize(25, 30);
        _micImage.setPosition(227, 13);
        _boxChildTable.addActor(_micImage);

        //////////////////////////////////////
        //populating
        /////////////////////////////////////
        _chatRoot.add(_mode1AllMessagesTable).expandX().fillX().height(130).padLeft(15).padRight(15);
        _chatRoot.row();
        _chatRoot.addActor(_mode2AllMessagesTable);
        _chatRoot.add(_messageBoxTable).expandX().fillX().height(60);

        _chatRoot.setBounds(0, 0, Positions.getWidth(), _chatRoot.getPrefHeight());

        _stage.addActor(_chatRoot);

        attachListeners();
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
            fadeOutMode2();
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


    public boolean isVisible() {
        return _visible;
    }

    private void moveChatPosition(float newY){
        _chatRoot.setPosition(0, newY);
        if(newY > 0){
            expanded();
        }
        else{
            collapsed();
        }
    }


    private void attachListeners(){

        Broadcaster.getInstance().subscribe(BroadcastEvent.SCREEN_LAYOUT_CHANGED, new BroadcastListener<Float>() {
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

        Broadcaster.getInstance().subscribe(BroadcastEvent.CHAT_NEW_MESSAGE, new BroadcastListener<ChatMessage>() {
            @Override
            public void onCallback(final ChatMessage obj, Status st) {
                if(st == Status.SUCCESS){
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            add(obj);
                        }
                    });
                }
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
                    if(!focusEvent.isFocused()) return false;
                }
                else{
                    eventString = event.toString();
                }

                if (eventString.equals("keyboard") ||
                        eventString.equals("touchDown")) {
                    _textFieldFocusImage.setVisible(true);
                    _textFieldNotFocusImage.setVisible(false);
                    if(!_expanded) expanded();
                    return true;
                }
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
    }

    public void setMessage(String msg){
        _messageTextField.setText(msg);
    }

    public void sendMessage(){
        String msg = _messageTextField.getText().trim();
        if(!msg.equals("")){
            ChatMessage c = new ChatMessage(msg, ChatMessage.FromType.USER, "1");
            _gamingKit.sendRoomMessage(msg);
        }
        _messageTextField.setText("");
    }

    public void add(final ChatMessage msg){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

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
                    lblUsernameStyle.font = _assets.getBlackBold2();

                    Label.LabelStyle lblMessageStyle = new Label.LabelStyle();
                    lblMessageStyle.font = _assets.getBlackNormal2();

                    Label.LabelStyle lblInfoStyle = new Label.LabelStyle();
                    lblInfoStyle.font = _assets.getBlueNormal2();

                    Label.LabelStyle lblImportantStyle = new Label.LabelStyle();
                    lblImportantStyle.font = _assets.getRedNormal2();

                    if (msg.getFromType() == ChatMessage.FromType.USER) {
                        Profile sender = _room.getProfileByUserId(msg.getSenderId());
                        if (sender == null) return;

                        Label lblUsername = new Label(sender.getDisplayName(30) + ": ", lblUsernameStyle);
                        chatTable.add(lblUsername).minHeight(20).padRight(5);

                        Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
                        lblMessage.setWrap(true);
                        chatTable.add(lblMessage).expandX().fillX().minHeight(20);
                        chatTable.row();
                    } else {
                        // Image icon = new Image(msg.getFromType() == ChatMessage.FromType.SYSTEM ? _assets.getInfoIcon() : _assets.getImportantIcon());
                        Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);
                        lblMessage.setWrap(true);
                        // chatTable.add(icon).size(20, 20).padRight(5).padLeft(5);
                        chatTable.add(lblMessage).colspan(2).expandX().fillX().minHeight(20);
                        chatTable.row();
                    }

                    Image separator = new Image(_assets.getGreyLine());
                    chatTable.add(separator).colspan(3).padTop(5).padBottom(5).expandX().fillX();
                    chatTable.row();

                    _mode1MessagesContentTable.add(chatTable).expandX().fillX();
                    _mode1MessagesContentTable.row();
                }
                else if(_mode == 2){

                    fadeOutMode2();

                    if(msg.getFromType() != ChatMessage.FromType.USER) return;

                    Table chatTable = new Table();
                    chatTable.align(Align.left);
                    if (!_mode2NotFirstMessage) {
                        chatTable.padTop(12);
                        _mode2NotFirstMessage = true;
                    }

                    Label.LabelStyle labelStyle = new Label.LabelStyle();
                    labelStyle.font = _assets.getWhiteBold2GrayS();
                    labelStyle.fontColor = getUserColor(msg.getSenderId());

                    Profile sender = _room.getProfileByUserId(msg.getSenderId());
                   if (sender == null) return;

                    Label userNameLabel = new Label(sender.getDisplayName(30) + ":", labelStyle);

                    Label.LabelStyle labelStyle2 = new Label.LabelStyle();
                    labelStyle2.font = _assets.getWhiteNormal2Black();
                    labelStyle2.fontColor = Color.WHITE;
                    Label messageLabel = new Label(msg.getMessage(), labelStyle2);
                    messageLabel.setWrap(true);
                    messageLabel.setAlignment(Align.left);

                    chatTable.add(userNameLabel).top().padRight(5);
                    chatTable.add(messageLabel).expandX().fillX();

                    _mode2MessagesContentTable.add(chatTable).expandX().fillX();
                    _mode2MessagesContentTable.row();
                }

                scrollToBottom();

            }
        });
    }

    private void scrollToBottom(){
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

    private void fadeOutMode2(){
        _mode2MessagesContentTable.getColor().a = 1;
        _mode2MessagesContentTable.clearActions();
        if(!_expanded){
            _mode2MessagesContentTable.addAction(sequence(delay(5), fadeOut(0.3f)));
        }
    }

    private Color getUserColor(String userId){
        if(!_userColors.containsKey(userId)){
            _userColors.put(userId, Colors.generatePleasingColor());
        }
        return _userColors.get(userId);
    }

    public void resetChat() {
        _expanded = false;
        _messageTextField.setText("");
        _userColors.clear();

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




}
