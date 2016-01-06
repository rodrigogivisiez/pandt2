package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.IPTGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class Chat {

    private Table _chatRoot, _allMessagesTable, _messageBoxTable;
    private Stage _stage;
    private Assets _assets;
    private Texts _texts;
    private GamingKit _gamingKit;
    private TextField _messageTextField;
    private Image _textFieldFocusImage, _textFieldNotFocusImage;
    private Image _dummyCloseButton;
    private boolean _expanded, _messageNotificationShown;
    private float _collapsedY, _expandedY;
    private Table _boxChildTable, _messagesContentTable, _newMsgCounterTable, _sendTable;
    private Room _room;
    private ScrollPane _chatScroll;
    private Label _sendLabel, _messageCounterLabel;
    private boolean _notFirstMessage;
    private boolean _visible;
    private SpriteBatch _batch;
    private IPTGame _game;

    public void setRoom(Room _room) {
        this._room = _room;
    }

    public Chat(GamingKit gamingKit, Texts texts, Assets assets, SpriteBatch batch, IPTGame game) {
        this._gamingKit = gamingKit;
        this._texts = texts;
        this._assets = assets;
        this._batch = batch;
        this._game = game;

        if(batch == null) return;

        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _chatRoot = new Table();

        ////////////////////////
        //All Messages Table
        ///////////////////////
        _allMessagesTable = new Table();
        _allMessagesTable.setBackground(new TextureRegionDrawable(_assets.getChatContainer()));
        _allMessagesTable.align(Align.top);
        _allMessagesTable.setVisible(false);


        _messagesContentTable = new Table();
        _messagesContentTable.align(Align.top);
        _chatScroll = new ScrollPane(_messagesContentTable);
        _chatScroll.setScrollingDisabled(true, false);

        _dummyCloseButton = new Image(_assets.getEmpty());
        _allMessagesTable.add(_chatScroll).expand().fill().padLeft(20).padTop(3);
        _allMessagesTable.add(_dummyCloseButton).width(35).height(35).top();


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

        ///////////////////////////////
        //Send Label
        /////////////////////////////////
        _sendTable = new Table();
        new DummyButton(_sendTable, _assets);

        Label.LabelStyle sendLabelStyle = new Label.LabelStyle();
        sendLabelStyle.font = _assets.getOrangePizza3();
        _sendLabel = new Label(_texts.send(), sendLabelStyle);
        _sendTable.add(_sendLabel);

        _boxChildTable.add(_messageTextField).expandX().fillX().padLeft(15).padRight(10).padTop(5).padBottom(5);
        _boxChildTable.add(_sendTable).width(70).expandY().fillY();

        _textFieldFocusImage.setWidth(_boxChildTable.getPrefWidth());
        _textFieldFocusImage.setHeight(1);
        _textFieldFocusImage.setPosition(10, 5);
        _boxChildTable.addActor(_textFieldFocusImage);
        _textFieldNotFocusImage.setWidth(_boxChildTable.getPrefWidth());
        _textFieldNotFocusImage.setHeight(1);
        _textFieldNotFocusImage.setPosition(10, 5);
        _boxChildTable.addActor(_textFieldNotFocusImage);


        ////////////////////////////////
        //Message notification counter
        ////////////////////////////////////
        Vector2 _messageNotfSize = Sizes.resize(30, _assets.getMessageNotification());
        _newMsgCounterTable = new Table();
        _newMsgCounterTable.setBackground(new TextureRegionDrawable(_assets.getMessageNotification()));
        new DummyButton(_newMsgCounterTable, _assets);

        Label.LabelStyle messageCounterStyle = new Label.LabelStyle();
        messageCounterStyle.font = _assets.getRedNormal2();
        _messageCounterLabel = new Label("0", messageCounterStyle);
        _newMsgCounterTable.add(_messageCounterLabel).padBottom(5).padRight(1);

        _newMsgCounterTable.setPosition(Positions.getWidth()-60, 58);
        _newMsgCounterTable.setSize(_messageNotfSize.x, _messageNotfSize.y);


        //////////////////////////////////////
        //populating
        /////////////////////////////////////
        _chatRoot.add(_allMessagesTable).expandX().fillX().height(130).padLeft(15).padRight(15);
        _chatRoot.row();
        _chatRoot.addActor(_newMsgCounterTable);
        _chatRoot.add(_messageBoxTable).expandX().fillX().height(60);

        _chatRoot.setBounds(0, 0, Positions.getWidth(), _chatRoot.getPrefHeight());

        _stage.addActor(_chatRoot);

        attachListeners();
        resetMessageNotificationCount();
        positionHack();
    }

    private void positionHack(){
        _expandedY = 60;
        _collapsedY = -70;
    }

    public void expand(){
        resetMessageNotificationCount();
        _allMessagesTable.setVisible(true);
        _allMessagesTable.clearActions();
        _allMessagesTable.addAction(sequence(fadeIn(0),moveTo(_allMessagesTable.getX(), _expandedY)));
        _expanded = true;
        scrollToBottom();
    }

    public void collapsed(boolean withAnimation){
        _allMessagesTable.clearActions();
        _allMessagesTable.addAction(sequence(moveTo(_allMessagesTable.getX(), _collapsedY, withAnimation ? 0.35f : 0f, Interpolation.circleIn)));
        _expanded = false;
        _stage.setKeyboardFocus(_stage.getActors().get(0));
        _textFieldNotFocusImage.setVisible(true);
        _textFieldFocusImage.setVisible(false);
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

        _newMsgCounterTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _stage.setKeyboardFocus(_messageTextField);
            }
        });

        _dummyCloseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                collapsed(true);
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
                    if(!_expanded) expand();
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

                if(!_expanded) _allMessagesTable.setVisible(false);

                Table chatTable = new Table();
                if (!_notFirstMessage) {
                    chatTable.padTop(12);
                    _notFirstMessage = true;
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
                    Mascot mascotIcon = new Mascot(sender.getMascotEnum(), _assets);
                    mascotIcon.resizeTo(20, 20);
                    chatTable.add(mascotIcon).padRight(5).padLeft(5);

                    Label lblUsername = new Label(sender.getDisplayName(0) + ": ", lblUsernameStyle);
                    chatTable.add(lblUsername).minHeight(20).padRight(5);

                    Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
                    lblMessage.setWrap(true);
                    chatTable.add(lblMessage).expandX().fillX();
                    chatTable.row();
                } else {
                    Image icon = new Image(msg.getFromType() == ChatMessage.FromType.SYSTEM ? _assets.getInfoIcon() : _assets.getImportantIcon());
                    Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);
                    lblMessage.setWrap(true);
                    chatTable.add(icon).size(20, 20).padRight(5).padLeft(5);
                    chatTable.add(lblMessage).colspan(2).expandX().fillX();
                    chatTable.row();
                }

                Image separator = new Image(_assets.getGreyLine());
                chatTable.add(separator).colspan(3).padTop(5).padBottom(5).expandX().fillX();
                chatTable.row();

                _messagesContentTable.add(chatTable).expandX().fillX();
                _messagesContentTable.row();
                scrollToBottom();

                if (!_expanded) {
                    _allMessagesTable.setVisible(false);
                    collapsed(false);
                    addMessageNotificationCount();
                }

            }
        });

    }

    private void addMessageNotificationCount(){
        Integer value = Integer.valueOf(_messageCounterLabel.getText().toString());
        _messageCounterLabel.setText(String.valueOf(value+1));

        if(!_messageNotificationShown){
            _messageNotificationShown = true;
            _newMsgCounterTable.addAction(sequence(moveTo(Positions.getWidth()-60, 58, 0.25f) ,forever(sequence(moveBy(0, -2, 0.3f), moveBy(0, 2, 0.3f)))));
        }

    }

    private void resetMessageNotificationCount(){
        _messageCounterLabel.setText("0");
        _newMsgCounterTable.clearActions();
        _newMsgCounterTable.addAction(moveTo(_newMsgCounterTable.getX(), 23));
        _messageNotificationShown = false;
    }


    private void scrollToBottom(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                _chatScroll.setScrollPercentY(100);
            }
        });
    }

    public void resetChat() {
        _expanded = false;
        _messageNotificationShown = false;
        _messagesContentTable.clear();
        _messageCounterLabel.setText("0");
        _notFirstMessage = false;
        _messageTextField.setText("");
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
