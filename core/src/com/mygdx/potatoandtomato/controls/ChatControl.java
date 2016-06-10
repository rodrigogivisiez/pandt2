package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.controls.StageChangedListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.services.Recorder;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Logs;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.ColorUtils;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class ChatControl {

    private Stage _stage;
    private SpriteBatch _batch;
    private Assets _assets;
    private Texts _texts;
    private SoundsPlayer _soundsPlayer;
    private Recorder _recorder;
    private TextField _messageTextField;
    private Image _textFieldFocusImage, _textFieldNotFocusImage;

    private Table _root;
    private Table _chatBoxContainer, _chatBoxTable;
    private Table _mode1MessagesContainer, _mode2MessagesContainer;
    private Table _mode1MessagesTable, _mode2MessagesTable;
    private Table _sendTable;

    private ScrollPane _mode1ChatScroll, _mode2ChatScroll;
    private Table _bigMicTable;
    private Image _btnMic, _btnKeyboard, _btnTemplate;
    private Image _micImage, _closeKeyboardImage;

    private ChatTemplateControl _chatTemplateControl;

    private StageChangedListener _stageChangedListener;

    public ChatControl(Texts texts, Assets assets, SoundsPlayer soundsPlayer, Recorder recorder, SpriteBatch batch,
                            ChatTemplateControl chatTemplateControl) {
        this._texts = texts;
        this._assets = assets;
        this._recorder = recorder;
        this._batch = batch;
        this._soundsPlayer = soundsPlayer;
        this._chatTemplateControl = chatTemplateControl;

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

        _root = new Table();

        populateMode1();
        populateMode2();

        ////////////////////////////////
        //Bottom message box
        ///////////////////////////////////
        _chatBoxContainer = new Table();
        _chatBoxContainer.setName("shown");
        _chatBoxContainer.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));
        _chatBoxTable = new Table();
        _chatBoxTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.CHAT_BOX)));
        new DummyButton(_chatBoxTable, _assets);
        _chatBoxContainer.add(_chatBoxTable).expandX().fillX().padLeft(15).padRight(15).padTop(3).padBottom(3);

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
        Label sendLabel = new Label(_texts.send(), sendLabelStyle);
        _sendTable.add(sendLabel);

        _chatBoxTable.add(_messageTextField).expandX().fillX().padLeft(15).padRight(40).padTop(5).padBottom(5);
        _chatBoxTable.add(_sendTable).width(70).expandY().fillY();

        ////////////////////////////////////////////////////////////
        //hidden micbutton/keyboard button/template control
        ////////////////////////////////////////////////////////////
        Table hiddenTable = new Table();
        _btnKeyboard = new Image(_assets.getTextures().get(Textures.Name.KEYBOARD_BUTTON));
        _btnMic = new Image(_assets.getTextures().get(Textures.Name.MIC_BUTTON));
        _btnTemplate = new Image(_assets.getTextures().get(Textures.Name.MIC_BUTTON));
        hiddenTable.add(_btnKeyboard).size(35, 35).padLeft(5);
        hiddenTable.add(_btnMic).size(35, 35);
        hiddenTable.add(_btnTemplate).size(35, 35);
        hiddenTable.setPosition(Positions.getHeight(), 0);      //use height because it only appear in landscape mode
        hiddenTable.setSize(110, 35);
        _chatBoxContainer.addActor(hiddenTable);

        _root.add(_chatBoxContainer).expandX().fillX().height(60);
        _root.setBounds(0, 0, Positions.getWidth(), _root.getPrefHeight());
    }

    public void populateMode1(){
        ////////////////////////
        //All Messages Table Mode 1
        ///////////////////////
        _mode1MessagesContainer = new Table();
        _mode1MessagesContainer.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.CHAT_CONTAINER)));
        _mode1MessagesContainer.align(Align.top);

        _mode1MessagesTable = new Table();
        _mode1MessagesTable.align(Align.top);
        _mode1ChatScroll = new ScrollPane(_mode1MessagesTable);
        _mode1ChatScroll.setScrollingDisabled(true, false);
        _mode1MessagesContainer.add(_mode1ChatScroll).expand().fill().padLeft(15).padRight(15).padTop(3);

        _root.add(_mode1MessagesContainer).expandX().fillX().height(130);
        _root.row();
    }

    public void populateMode2(){
        /////////////////////////////
        //All Messages Table Mode 2
        /////////////////////////////
        _mode2MessagesContainer = new Table();
        _mode2MessagesContainer.setTouchable(Touchable.disabled);
        _mode2MessagesContainer.setPosition(0, 70);

        _mode2MessagesTable = new Table();
        _mode2MessagesTable.align(Align.bottomLeft);
        _mode2ChatScroll = new ScrollPane(_mode2MessagesTable);
        _mode2MessagesContainer.add(_mode2ChatScroll).expand().fill().padLeft(80).padRight(20);
    }

    public void invalidate(final boolean isVisible){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_stage != null){
                    if(isVisible) hide();
                    _stage.dispose();
                    _root.remove();
                    _textFieldFocusImage.remove();
                    _textFieldNotFocusImage.remove();
                    _bigMicTable.remove();
                    _micImage.remove();
                    _closeKeyboardImage.remove();
                    _mode2MessagesContainer.remove();
                }


                final StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                Stage newStage = new Stage(viewPort, _batch);
                _stageChangedListener.onChanged(_stage, newStage);
                _stage = newStage;

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

                _mode2MessagesContainer.setSize(Positions.getWidth(), Global.IS_POTRAIT ? 80 : 70);
                _stage.addActor(_mode2MessagesContainer);

                _root.setPosition(0, 0);
                _root.setSize(Positions.getWidth(), _root.getPrefHeight());
                _root.addActor(_bigMicTable);

                _stage.addActor(_root);
                _stage.addActor(_chatTemplateControl);

                if(isVisible) {
                    show();
                    scrollToBottom();
                }

                if(!Global.IS_POTRAIT){
                    Threadings.delay(500, new Runnable() {
                        @Override
                        public void run() {
                            animateHideForMode2();
                        }
                    });
                }
            }
        });
    }

    public void modeChanged(final int mode){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    _chatBoxContainer.clearActions();
                    _mode1MessagesContainer.setVisible(true);
                    _mode2MessagesContainer.setVisible(false);
                }
                else if(mode == 2){
                    _mode2MessagesContainer.setVisible(true);
                    _mode1MessagesContainer.setVisible(false);
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
                _chatBoxContainer.clearActions();
                _chatBoxContainer.addAction(sequence(moveTo(-Positions.getWidth(), 0, 1f, Interpolation.exp10Out), new RunnableAction() {
                    @Override
                    public void run() {
                        fadeOutMode2(0);
                    }
                }));
                _chatBoxContainer.setName("hidden");
            }
        });
    }

    public void animateShowForMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _chatBoxContainer.clearActions();
                _chatBoxContainer.addAction(moveTo(0, 0, 0.3f));
                _chatBoxContainer.setName("shown");
                fadeInMode2();
            }
        });
    }

    public void fadeInMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _mode2MessagesContainer.setTouchable(Touchable.enabled);
                _mode2MessagesTable.clearActions();
                _mode2MessagesTable.getColor().a = 1;
            }
        });
    }

    public void fadeOutMode2(final int delaySecs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _mode2MessagesContainer.setTouchable(Touchable.disabled);
                _mode2MessagesTable.addAction(sequence(delay(delaySecs), fadeOut(0.3f)));
            }
        });

    }

    public void moveChatPosition(final float newY){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _root.setPosition(0, newY);
                _mode2MessagesContainer.setPosition(0, Global.IS_POTRAIT ? 80 + newY : 50 + newY);
            }
        });
    }

    public void showRecording(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _soundsPlayer.playSoundEffect(Sounds.Name.MIC);
                _bigMicTable.addAction(sequence(fadeOut(0f), forever(sequence(fadeOut(0.6f), fadeIn(0.6f)))));
                _bigMicTable.setVisible(true);
            }
        });
    }

    public void hideRecording(){
         Threadings.postRunnable(new Runnable() {
        @Override
        public void run() {
            _bigMicTable.setVisible(false);
            _bigMicTable.clearActions();
            }
        });
    }

    public void setRecordingSoundsLevel(int soundsLevel){
        System.out.println("________________________________Sound level is" + soundsLevel);
    }

    public void add(ChatMessage msg, int mode, String myUserId){
        add(msg, mode, null, myUserId);
    }

    public void add(final ChatMessage msg, final int mode, final RoomUser sender, final String myUserId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    Table chatTable = new Table();
                    if (_mode1MessagesTable.getCells().size == 0) {
                        chatTable.padTop(12);
                    }

                    chatTable.add(generateMessageTable(msg, mode, sender, myUserId)).expand().fill();

                    _mode1MessagesTable.add(chatTable).expandX().fillX();
                    _mode1MessagesTable.row();
                }
                else if(mode == 2){
                    Table chatTable = new Table();
                    chatTable.align(Align.left);
                    if (_mode2MessagesTable.getCells().size == 0) {
                        chatTable.padTop(12);
                    }

                    chatTable.add(generateMessageTable(msg, mode, sender, myUserId)).expand().fill();

                    _mode2MessagesTable.add(chatTable).expandX().fillX();
                    _mode2MessagesTable.row();
                }

                _soundsPlayer.playSoundEffect(Sounds.Name.MESSAGING);
            }
        });
    }

    public Table generateMessageTable(final ChatMessage msg, int mode, final RoomUser sender, String myUserId){
        Table messageTable = new Table();

        if ((msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) && sender == null) {
            return messageTable;
        }

        ////////////////
        //Styles
        ///////////////
        Label.LabelStyle lblUsernameStyle = new Label.LabelStyle();
        Label.LabelStyle lblMessageStyle = new Label.LabelStyle();
        Label.LabelStyle lblInfoStyle = new Label.LabelStyle();
        Label.LabelStyle lblImportantStyle = new Label.LabelStyle();

        if(mode == 1){
            lblUsernameStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD);
            lblUsernameStyle.fontColor = Color.BLACK;

            lblMessageStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
            lblMessageStyle.fontColor = Color.BLACK;

            lblInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
            lblInfoStyle.fontColor = Color.valueOf("11b1bf");

            lblImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
            lblImportantStyle.fontColor = Color.valueOf("F56C57");
        }
        else if(mode == 2){
            lblUsernameStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR_B_ffffff_000000_1);
            if(sender != null){
                lblUsernameStyle.fontColor = ColorUtils.getUserColorByIndex(sender.getSlotIndex());
            }

            lblMessageStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
            lblMessageStyle.fontColor = Color.WHITE;

            lblInfoStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
            lblInfoStyle.fontColor = Color.valueOf("11b1bf");

            lblImportantStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD_B_ffffff_000000_1);
            lblImportantStyle.fontColor = Color.valueOf("F56C57");
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
                VoiceMessageControl voiceMessageControl = new VoiceMessageControl(msg, _assets, _soundsPlayer, _recorder, myUserId);
                messageTable.add(voiceMessageControl).expandX().fillX().minHeight(20).left();
            }
        }
        else {
            Label lblMessage = new Label(msg.getMessage(), msg.getFromType() == ChatMessage.FromType.SYSTEM ? lblInfoStyle : lblImportantStyle);
            lblMessage.setWrap(true);
            lblMessage.setAlignment(Align.left);
            messageTable.add(lblMessage).expandX().fillX().minHeight(20);
        }

        if(mode == 1){
            messageTable.row();
            Image separator = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
            messageTable.add(separator).colspan(2).height(1).padTop(5).padBottom(5).expandX().fillX();
        }


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

    public void clearChat(final int mode){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    _mode1MessagesTable.clear();
                }
                else if(mode == 2){
                    _mode2MessagesTable.clear();
                }
            }
        });
    }

    public boolean positionHasChatElement(int mode, float x, float y){
        if(mode == 1){
            if(y <= _chatBoxContainer.getHeight() + _mode1MessagesContainer.getHeight()){
                return true;
            }
        }
        else if(mode == 2){
            if(_chatBoxContainer.getName() != null && _chatBoxContainer.getName().equals("shown")){
                if(_mode2MessagesContainer.getTouchable() == Touchable.enabled){
                    if(y <= _chatBoxContainer.getHeight() + _mode2MessagesTable.getPrefHeight()){
                        return true;
                    }
                }
                else{
                    if(y <= _chatBoxContainer.getHeight()){
                        return true;
                    }
                }
            }
        }

        return false;
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

    public Image getBtnTemplate() {
        return _btnTemplate;
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

    public void setStageChangedListener(StageChangedListener _stageChangedListener) {
        this._stageChangedListener = _stageChangedListener;
    }

    public Table getRoot() {
        return _root;
    }
}
