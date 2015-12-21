package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class Chat implements Disposable {

    private Table _chatRoot, _allMessagesTable, _messageBoxTable;
    private Stage _stage;
    private Assets _assets;
    private Texts _texts;
    private GamingKit _gamingKit;
    private boolean _init;
    private TextField _messageTextField;
    private Image _textFieldFocusImage, _textFieldNotFocusImage;
    private Image _dummyCloseButton;
    private boolean _shown, _messageNotificationShown;
    private float _originalY;
    private Table _boxChildTable, _messagesContentTable, _newMsgCounterTable, _sendTable;
    private Room _room;
    private ScrollPane _chatScroll;
    private Label _sendLabel, _messageCounterLabel;

    public boolean isInit() {
        return _init;
    }

    public void setRoom(Room _room) {
        this._room = _room;
    }

    public Chat() {
        _chatRoot = new Table();

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
                            newChatMessage(obj);
                        }
                    });
                }
            }
        });
    }

    public void show(Actor _root, Assets assets, Texts texts, Room room, GamingKit gamingKit){

        if(!_init){
            _assets = assets;
            _texts = texts;
            _room = room;
            _gamingKit = gamingKit;
            _stage = _root.getStage();

            ////////////////////////
            //All Messages Table
            ///////////////////////
            _allMessagesTable = new Table();
            _allMessagesTable.setBackground(new TextureRegionDrawable(_assets.getChatContainer()));
            _allMessagesTable.align(Align.top);


            _messagesContentTable = new Table();
            _messagesContentTable.align(Align.top);
            _chatScroll = new ScrollPane(_messagesContentTable);
            _chatScroll.setScrollingDisabled(true, false);

            _dummyCloseButton = new Image(_assets.getEmpty());
            _allMessagesTable.add(_chatScroll).expand().fill().padLeft(20).padTop(15);
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
            _chatRoot.add(_messageBoxTable).expandX().fillX();

            _chatRoot.setBounds(0, 0, Positions.getWidth(), _chatRoot.getPrefHeight());

            if(_stage != null) _stage.addActor(_chatRoot);

            attachListeners();
            _init = true;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    _originalY = _allMessagesTable.getY();
                }
            });

            _allMessagesTable.setVisible(false);
        }
        else{
            _allMessagesTable.setVisible(true);
            _allMessagesTable.addAction(sequence(moveTo(_allMessagesTable.getX(), _allMessagesTable.getY() - _allMessagesTable.getHeight(), 0),
                                                moveTo(_allMessagesTable.getX(), _originalY, 0.25f, Interpolation.circleOut)));
            _shown = true;
        }

        resetMessageNotificationCount();
        scrollToBottom();
    }

    public void show(){
        show(null, null, null, null, null);
    }

    private void moveChatPosition(float newY){
        _chatRoot.setPosition(0, newY);
    }

    public void hide(){
        _originalY = _allMessagesTable.getY();
        _allMessagesTable.addAction(sequence(moveTo(_allMessagesTable.getX(), _allMessagesTable.getY() - _allMessagesTable.getHeight(), 0.35f, Interpolation.circleIn), new Action() {
            @Override
            public boolean act(float delta) {
                _allMessagesTable.setVisible(false);
                return true;
            }
        }));
        _shown = false;
        _stage.setKeyboardFocus(_stage.getActors().get(0));
        _textFieldNotFocusImage.setVisible(true);
        _textFieldFocusImage.setVisible(false);

    }

    private void attachListeners(){

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
                hide();
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
                    if(!_shown) show();
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

    public void newChatMessage(ChatMessage msg){
        if(_init){
            Table chatTable = new Table();
            ////////////////
            //Styles
            ///////////////
            Label.LabelStyle lblUsernameStyle = new Label.LabelStyle();
            lblUsernameStyle.font = _assets.getBlackBold2();

            Label.LabelStyle lblMessageStyle = new Label.LabelStyle();
            lblMessageStyle.font = _assets.getBlackNormal2();

            Label.LabelStyle lblInfoStyle = new Label.LabelStyle();
            lblInfoStyle.font =  _assets.getBlueNormal2();

            Label.LabelStyle lblImportantStyle = new Label.LabelStyle();
            lblImportantStyle.font =  _assets.getRedNormal2();

            if(msg.getFromType() == ChatMessage.FromType.USER){
                Profile sender = _room.getProfileByUserId(msg.getSenderId());
                if(sender == null) return;
                Mascot mascotIcon = new Mascot(sender.getMascotEnum(), _assets);
                mascotIcon.resizeTo(20, 20);
                chatTable.add(mascotIcon).padRight(5).padLeft(5);

                Label lblUsername = new Label(sender.getDisplayName() + ": ", lblUsernameStyle);
                chatTable.add(lblUsername).minHeight(20).padRight(5);

                Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
                lblMessage.setWrap(true);
                chatTable.add(lblMessage).expandX().fillX();
                chatTable.row();
            }
            else{
                Image icon = new Image(msg.getFromType() == ChatMessage.FromType.SYSTEM ? _assets.getInfoIcon() : _assets.getImportantIcon());
                Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);

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

            if(!_shown){
                addMessageNotificationCount();
            }

        }
    }

    private void addMessageNotificationCount(){
        Integer value = Integer.valueOf(_messageCounterLabel.getText().toString());
        _messageCounterLabel.setText(String.valueOf(value+1));

        if(!_messageNotificationShown){
            _messageNotificationShown = true;
            _newMsgCounterTable.addAction(sequence(moveTo(_newMsgCounterTable.getX(), 58, 0.25f) ,forever(sequence(moveBy(0, -2, 0.3f), moveBy(0, 2, 0.3f)))));
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

    @Override
    public void dispose() {
        _init = false;
        _shown = false;
        _messageNotificationShown = false;
        _chatRoot.clear();
        _chatRoot.remove();
        _originalY = 0;
    }
}
