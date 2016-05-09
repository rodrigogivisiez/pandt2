package com.mygdx.potatoandtomato.controls;

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
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Files;
import com.mygdx.potatoandtomato.utils.Logs;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.ColorUtils;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class ChatControl {

    private Table _chatRoot, _mode1AllMessagesTable, _messageBoxTable;
    private Table _mode2AllMessagesTable;

    private Stage _stage;
    private Assets _assets;
    private Texts _texts;
    private TextField _messageTextField;
    private Image _textFieldFocusImage, _textFieldNotFocusImage;
    private Image _btnMic, _btnKeyboard;
    private boolean _expanded;
    private Table _chatBoxTable, _mode1MessagesContentTable, _sendTable;
    private Table _mode2MessagesContentTable;
    private Table _bigMicTable;
    private ScrollPane _mode1ChatScroll, _mode2ChatScroll;
    private Label _sendLabel;
    private SpriteBatch _batch;
    private Image _micImage, _closeKeyboardImage;
    private boolean _fading;
    private SoundsPlayer _soundsPlayer;

    public ChatControl(Texts texts, Assets assets, SpriteBatch batch, SoundsPlayer soundsPlayer) {
        this._texts = texts;
        this._assets = assets;
        this._batch = batch;
        this._soundsPlayer = soundsPlayer;

        populate();
    }

    public void populate(){
        //////////////////////////////
        //Big Mic Table
        /////////////////////////////
        _bigMicTable = new Table();
        Image bigMicImage = new Image(_assets.getTextures().get(Textures.Name.MIC_BIG_ICON));
        _bigMicTable.add(bigMicImage).width(150).height(300);
        _bigMicTable.setVisible(false);

        _chatRoot = new Table();

        populateMode1();
        populateMode2();

        ////////////////////////////////
        //Bottom message box
        ///////////////////////////////////
        _messageBoxTable = new Table();
        _messageBoxTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));
        _chatBoxTable = new Table();
        _chatBoxTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.CHAT_BOX)));
        new DummyButton(_chatBoxTable, _assets);
        _messageBoxTable.add(_chatBoxTable).expandX().fillX().padLeft(15).padRight(15).padTop(3).padBottom(3);

        _textFieldFocusImage = new Image(_assets.getTextures().get(Textures.Name.ORANGE_HORIZONTAL_LINE));
        _textFieldNotFocusImage = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
        _textFieldFocusImage.setVisible(false);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.CURSOR_BLACK));
        _messageTextField = new TextField("", textFieldStyle);

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

        _chatBoxTable.add(_messageTextField).expandX().fillX().padLeft(15).padRight(40).padTop(5).padBottom(5);
        _chatBoxTable.add(_sendTable).width(70).expandY().fillY();

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

        _chatRoot.add(_messageBoxTable).expandX().fillX().height(60);
        _chatRoot.setBounds(0, 0, Positions.getWidth(), _chatRoot.getPrefHeight());

        invalidate(true);
    }

    public void populateMode1(){
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

        _chatRoot.add(_mode1AllMessagesTable).expandX().fillX().height(130);
        _chatRoot.row();
    }

    public void populateMode2(){
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
    }

    public void invalidate(final boolean isVisible){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_stage != null){
                    if(isVisible) hide();
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
                _chatBoxTable.addActor(_textFieldFocusImage);
                _textFieldNotFocusImage.setWidth(Global.IS_POTRAIT ? 246 : 520);
                _textFieldNotFocusImage.setHeight(1);
                _textFieldNotFocusImage.setPosition(10, 5);
                _chatBoxTable.addActor(_textFieldNotFocusImage);
                _micImage.setSize(18, 30);
                _micImage.setPosition(Global.IS_POTRAIT ? 227 : 510, 13);
                _chatBoxTable.addActor(_micImage);
                if(!Global.IS_POTRAIT){
                    _closeKeyboardImage.setSize(40, 30);
                    _closeKeyboardImage.setPosition(460 ,13);
                    _chatBoxTable.addActor(_closeKeyboardImage);
                }

                _mode2AllMessagesTable.setSize(Positions.getWidth(), Global.IS_POTRAIT ? 80 : 70);
                _stage.addActor(_mode2AllMessagesTable);

                _chatRoot.setPosition(0, 0);
                _chatRoot.setSize(Positions.getWidth(), _chatRoot.getPrefHeight());
                _chatRoot.addActor(_bigMicTable);

                _stage.addActor(_chatRoot);

                if(isVisible) {
                    show();
                    scrollToBottom();
                }
            }
        });
    }

    public void modeChanged(final int mode){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    _mode1AllMessagesTable.setVisible(true);
                    _mode2AllMessagesTable.setVisible(false);
                }
                else if(mode == 2){
                    _mode2AllMessagesTable.setVisible(true);
                    _mode1AllMessagesTable.setVisible(false);
                }
            }
        });
    }

    public void unfocusMessageTextField(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _stage.setKeyboardFocus(_stage.getActors().get(0));
                _textFieldNotFocusImage.setVisible(true);
                _textFieldFocusImage.setVisible(false);
            }
        });
    }

    public void focusMessageTextField(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _stage.setKeyboardFocus(_messageTextField);
                _textFieldFocusImage.setVisible(true);
                _textFieldNotFocusImage.setVisible(false);
            }
        });
    }

    public void animateHideForMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _messageBoxTable.clearActions();
                _messageBoxTable.addAction(sequence(moveTo(-Positions.getWidth(), 0, 1f, Interpolation.exp10Out), new RunnableAction() {
                    @Override
                    public void run() {
                        fadeOutMode2();
                    }
                }));
            }
        });
    }

    public void animateShowForMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _messageBoxTable.clearActions();
                _messageBoxTable.addAction(moveTo(0, 0, 0.3f));
                fadeInMode2();
            }
        });
    }

    public void moveChatPosition(final float newY){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _chatRoot.setPosition(0, newY);
                _mode2AllMessagesTable.setPosition(0, Global.IS_POTRAIT ? 80 + newY : 50 + newY);
                if(newY > 0){
                    fadeInMode2();
                }
                else{
                    fadeOutMode2();
                }
            }
        });
    }

    private void hideRecording(){
             Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _bigMicTable.setVisible(false);
                _bigMicTable.clearActions();
            }
        });
    }

    private void showRecording(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _soundsPlayer.playSoundEffect(Sounds.Name.MIC);
                _bigMicTable.addAction(sequence(fadeOut(0f), forever(sequence(fadeOut(0.6f), fadeIn(0.6f)))));
                _bigMicTable.setVisible(true);
            }
        });
    }

    public void add(final ChatMessage msg, final int mode, final RoomUser sender){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    Table chatTable = new Table();
                    if (_mode1MessagesContentTable.getCells().size == 0) {
                        chatTable.padTop(12);
                    }

                    _mode1MessagesContentTable.add(chatTable).expandX().fillX();
                    _mode1MessagesContentTable.row();
                }
                else if(mode == 2){

                    Table chatTable = new Table();
                    chatTable.align(Align.left);
                    if (_mode2MessagesContentTable.getCells().size == 0) {
                        chatTable.padTop(12);
                    }

                    Label.LabelStyle labelStyle = new Label.LabelStyle();
                    labelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR_B_ffffff_000000_1);
                    if(sender != null) labelStyle.fontColor = ColorUtils.getUserColorByIndex(sender.getSlotIndex());

                    Label.LabelStyle labelInfoStyle = new Label.LabelStyle();
                    labelInfoStyle.fontColor = Color.valueOf("11b1bf");
                    labelInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);

                    Label.LabelStyle labelImportantStyle = new Label.LabelStyle();
                    labelImportantStyle.fontColor = Color.valueOf("F56C57");
                    labelImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);

                    if(msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
                        Label userNameLabel = new Label(sender.getProfile().getDisplayName(30) + ":", labelStyle);
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
                    }
                    else{
                        Label lblMessage = new Label(msg.getMessage(),
                                msg.getFromType() == ChatMessage.FromType.SYSTEM ? labelInfoStyle : labelImportantStyle);
                        lblMessage.setWrap(true);
                        lblMessage.setAlignment(Align.left);
                        chatTable.add(lblMessage).colspan(2).expandX().fillX();
                        chatTable.row();
                    }

                    _mode2MessagesContentTable.add(chatTable).expandX().fillX();
                    _mode2MessagesContentTable.row();
                }

                _soundsPlayer.playSoundEffect(Sounds.Name.MESSAGING);
            }
        });
    }

    public Table generateMessageTable(final ChatMessage msg, int mode, final RoomUser sender){
        Table messageTable = new Table();

        ////////////////
        //Styles
        ///////////////
        Label.LabelStyle lblUsernameStyle = new Label.LabelStyle();
        Label.LabelStyle lblMessageStyle = new Label.LabelStyle();
        Label.LabelStyle lblInfoStyle = new Label.LabelStyle();
        Label.LabelStyle lblImportantStyle = new Label.LabelStyle();

        if(mode == 1){
            lblUsernameStyle.fontColor = Color.BLACK;
            lblUsernameStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD);

            lblMessageStyle.fontColor = Color.BLACK;
            lblMessageStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

            lblInfoStyle.fontColor = Color.valueOf("11b1bf");
            lblInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

            lblImportantStyle.fontColor = Color.valueOf("F56C57");
            lblImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
        }
        else if(mode == 2){
            lblUsernameStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR_B_ffffff_000000_1);
            lblUsernameStyle.fontColor = ColorUtils.getUserColorByIndex(sender.getSlotIndex());

            lblInfoStyle.fontColor = Color.valueOf("11b1bf");
            lblInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);

            lblImportantStyle.fontColor = Color.valueOf("F56C57");
            lblImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
        }

        if (msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
            Label lblUsername = new Label(sender.getProfile().getDisplayName(30) + ": ", lblUsernameStyle);
            messageTable.add(lblUsername).minHeight(20).padRight(5);

            if(msg.getFromType() == ChatMessage.FromType.USER){
                Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
                lblMessage.setWrap(true);
                lblMessage.setAlignment(Align.left);
                messageTable.add(lblMessage).expandX().fillX().minHeight(20);
            }
            else if(msg.getFromType() == ChatMessage.FromType.USER_VOICE){
                Image imgVoice = new Image(_assets.getTextures().get(Textures.Name.VOICE_ICON));
                messageTable.add(imgVoice).size(20, 20).expandX().left();
            }
        } else {
            Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);
            lblMessage.setWrap(true);
            lblMessage.setAlignment(Align.left);
            messageTable.add(lblMessage).expandX().fillX().minHeight(20);
        }

        messageTable.row();
        Image separator = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
        messageTable.add(separator).colspan(2).minHeight(20).expandX().fillX();

        return messageTable;
    }

    public void scrollToBottom(){
        Threadings.delay(100, new Runnable() {
            @Override
            public void run() {
                _mode1ChatScroll.setScrollPercentY(100);
                _mode2ChatScroll.setScrollPercentY(100);
            }
        });
    }

    private void fadeInMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _mode2AllMessagesTable.setTouchable(Touchable.enabled);
                _fading = false;
                _mode2MessagesContentTable.clearActions();
                _mode2MessagesContentTable.getColor().a = 1;
                fadeOutMode2();
            }
        });
    }

    private void fadeOutMode2(){
        if(_fading) return;

        _fading = true;
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _mode2MessagesContentTable.addAction(sequence(delay(5), fadeOut(0.3f), new RunnableAction() {
                    @Override
                    public void run() {
                        _fading = false;
                    }
                }));
            }
        });
        _mode2AllMessagesTable.setTouchable(Touchable.disabled);
    }


    public void resetChat(final int mode) {
        _expanded = false;
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                setMessageTextFieldMsg("");

                if(mode == 1){
                    _mode1MessagesContentTable.clear();
                }
                else if(mode == 2){
                    _mode2MessagesContentTable.clear();
                }
            }
        });
    }

    public void render(float delta){
        _stage.act(delta);
        _stage.draw();
    }

    public void resize(int width, int height){
        _stage.getViewport().update(width, height);
    }

    public void setMessageTextFieldMsg(final String msg){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _messageTextField.setText(msg);
            }
        });
    }

    public TextField getMessageTextField() {
        return _messageTextField;
    }

    public Table getChatBoxTable() {
        return _chatBoxTable;
    }

    public Image getBtnMic() {
        return _btnMic;
    }

    public Image getBtnKeyboard() {
        return _btnKeyboard;
    }

    public Table getSendTable() {
        return _sendTable;
    }

    public Image getMicImage() {
        return _micImage;
    }

    public Image getCloseKeyboardImage() {
        return _closeKeyboardImage;
    }

    public Stage getStage() {
        return _stage;
    }
}
