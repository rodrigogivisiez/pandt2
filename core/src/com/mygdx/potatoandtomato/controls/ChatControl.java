package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.controls.ChatPopupListener;
import com.mygdx.potatoandtomato.absintflis.controls.CheckStateListener;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.mygdx.potatoandtomato.enums.GameConnectionStatus;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.services.Recorder;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 20/12/2015.
 */
public class ChatControl {

    private Stage stage;
    private SpriteBatch batch;
    private MyAssets assets;
    private Texts texts;
    private SoundsPlayer soundsPlayer;
    private Recorder recorder;

    private Table root;
    private Table chatBoxContainer, chatBoxInnerContainer, chatBoxTransContainer;
    private Image chatBoxBackground;
    private Table mode1MessagesContainer, mode2MessagesContainer;
    private Table mode1MessagesTable, mode2MessagesTable;
    private Table mode2MessagesTableBg;
    private Image mode2CloseImage;
    private ScrollPane mode1ChatScroll, mode2ChatScroll;

    private Table roomUsersButtonRoot, roomUsersButtonRootTrans, sendingRoot;
    private Table recordingTable;
    private Image recordingWaveOne, recordingWaveTwo, recordingWaveThree;
    private Button keyboardToggleButton, sendButton, sendButtonTrans, micButton, micButtonTrans;
    private CheckButton roomUsersButton, chatTemplateButton;
    private Button roomUsersButtonTrans, chatTemplateButtonTrans, keyboardToggleButtonTrans;
    private Vector2 roomUsersButtonPosition, chatTemplateButtonPosition;
    private ChatPopup chatTemplatesPopup, roomUsersPopup;
    private Label roomUsersLabel, roomUsersLabelTrans, recordingTimerLabel, recordCancelLabel;
    private PTTextField messageTextField;
    private Label showClickedPositionLabel;

    private ChatTemplateControl chatTemplateControl;
    private final int CHAT_CONTAINER_HEIGHT = 47;
    private final int BUTTON_WIDTH = 38;
    private IPTGame iptGame;
    private Array<VoiceMessageControl> mode2VoiceMessageControls;
    private ConcurrentHashMap<String, VoiceMessageControl> renderingVoiceMessageControlsMap;
    private ConcurrentHashMap<String, SafeThread> disconnectedCountDownThreads;
    private ConcurrentHashMap<String, Table> cacheRoomUserStatusTablesMap;

    public ChatControl(IPTGame iptGame, Texts texts, MyAssets assets, SoundsPlayer soundsPlayer, Recorder recorder, SpriteBatch batch,
                            ChatTemplateControl chatTemplateControl) {
        this.iptGame = iptGame;
        this.texts = texts;
        this.assets = assets;
        this.recorder = recorder;
        this.batch = batch;
        this.soundsPlayer = soundsPlayer;
        this.chatTemplateControl = chatTemplateControl;
        this.mode2VoiceMessageControls = new Array();
        renderingVoiceMessageControlsMap = new ConcurrentHashMap();
        disconnectedCountDownThreads = new ConcurrentHashMap();
        cacheRoomUserStatusTablesMap = new ConcurrentHashMap();

        populate();
    }

    public void populate(){
        //////////////////////////////
        //Center recording table
        /////////////////////////////
        recordingTable = new Table();
        recordingTable.align(Align.top);
        recordingTable.setSize(180, 250);
        recordingTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.TRANS_BLACK_ROUNDED_BG)));

        Image micIcon = new Image(assets.getTextures().get(Textures.Name.MIC_ICON));
        recordingWaveOne = new Image(assets.getTextures().get(Textures.Name.MIC_WAVE_ONE));
        recordingWaveTwo = new Image(assets.getTextures().get(Textures.Name.MIC_WAVE_TWO));
        recordingWaveThree = new Image(assets.getTextures().get(Textures.Name.MIC_WAVE_THREE));

        Label.LabelStyle recordingLabelStyle = new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_L_REGULAR), null);
        recordingTimerLabel = new Label("", recordingLabelStyle);

        Image recordingIndication = new Image(assets.getTextures().get(Textures.Name.WHITE_DOT));
        recordingIndication.setColor(Color.RED);
        recordingIndication.addAction(forever(sequence(fadeIn(0.9f), fadeOut(0.9f))));

        Table recordingTimerTable = new Table();
        recordingTimerTable.add(recordingTimerLabel).padRight(10);
        recordingTimerTable.add(recordingIndication);

        recordCancelLabel = new Label("", recordingLabelStyle);

        recordingTable.add(recordingWaveThree);
        recordingTable.row();
        recordingTable.add(recordingWaveTwo).padTop(-15);
        recordingTable.row();
        recordingTable.add(recordingWaveOne).padTop(-5);
        recordingTable.row();
        recordingTable.add(micIcon);
        recordingTable.row();
        recordingTable.add(recordingTimerTable).padTop(10);
        recordingTable.row();
        recordingTable.add(recordCancelLabel);
        recordingTable.setVisible(false);
        resetRecordDesign();

        root = new Table();
        root.setVisible(false);
        root.align(Align.bottom);

        populateMode1();
        populateMode2();

        ////////////////////////////////
        //Bottom message box
        ///////////////////////////////////
        chatBoxContainer = new Table();
        chatBoxContainer.setName("shown");

        chatBoxInnerContainer = new Table();
        chatBoxTransContainer = new Table();
        chatBoxTransContainer.setFillParent(true);
        chatBoxTransContainer.setVisible(false);

        chatBoxBackground = new Image(new NinePatchDrawable(assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));
        chatBoxBackground.setFillParent(true);

        messageTextField = new PTTextField(assets, false);

        ///////////////////////////////
        //Various Button
        /////////////////////////////////
        chatTemplateButton = new CheckButton(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_BTN)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_BTN_ONPRESS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_BTN_ONPRESS)));

        chatTemplateButtonTrans = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_BTN_TRANS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_TEMPLATE_BTN_TRANS)));

        roomUsersButtonRoot = new Table();
        roomUsersLabel = new Label("0/0",
                                new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD), Color.WHITE));
        roomUsersLabel.setWidth(BUTTON_WIDTH);
        roomUsersLabel.setAlignment(Align.center);
        roomUsersLabel.setY(21);
        roomUsersLabel.setTouchable(Touchable.disabled);
        roomUsersButton = new CheckButton(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.ROOM_USERS_BTN)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.ROOM_USERS_BTN_ONPRESS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.ROOM_USERS_BTN_ONPRESS)));


        roomUsersButtonRoot.add(roomUsersButton).width(BUTTON_WIDTH);
        roomUsersButtonRoot.addActor(roomUsersLabel);


        roomUsersButtonRootTrans = new Table();
        new DummyButton(roomUsersButtonRootTrans, assets);
        roomUsersLabelTrans = new Label("0/0",
                new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD), Color.WHITE));
        roomUsersLabelTrans.setWidth(BUTTON_WIDTH);
        roomUsersLabelTrans.setAlignment(Align.center);
        roomUsersLabelTrans.setY(21);
        roomUsersLabelTrans.setTouchable(Touchable.disabled);
        roomUsersButtonTrans = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.ROOM_USERS_BTN_TRANS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.ROOM_USERS_BTN_TRANS)));

        roomUsersButtonRootTrans.add(roomUsersButtonTrans).width(BUTTON_WIDTH);
        roomUsersButtonRootTrans.addActor(roomUsersLabelTrans);

        keyboardToggleButton = new CheckButton(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.KEYBOARD_BTN)),
                        new TextureRegionDrawable(assets.getTextures().get(Textures.Name.KEYBOARD_BTN_ONPRESS)),
                        new TextureRegionDrawable(assets.getTextures().get(Textures.Name.KEYBOARD_BTN_ONPRESS)));


        keyboardToggleButtonTrans = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.KEYBOARD_BTN_TRANS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.KEYBOARD_BTN_TRANS)));


        sendingRoot = new Table();
        sendButton = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SEND_BTN)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SEND_BTN_ONPRESS)));
        sendButton.setVisible(false);

        sendButtonTrans = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SEND_BTN_TRANS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.SEND_BTN_TRANS)));

        micButton = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.VOICE_BTN)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.VOICE_BTN_ONPRESS)));

        micButtonTrans = new Button(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.VOICE_BTN_TRANS)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.VOICE_BTN_TRANS)));

        sendingRoot.addActor(sendButton);
        sendingRoot.addActor(micButton);

        /////////////////////////////////////////////
        //popups
        /////////////////////////////////////////////

        chatTemplatesPopup = new ChatPopup(assets, 8);
        chatTemplatesPopup.setPosition(10, 50);

        Table chatTemplateTable = new Table();
        chatTemplateTable.setSize(300, 200);
        chatTemplateTable.add(chatTemplateControl).expand().fill();
        chatTemplatesPopup.setActor(chatTemplateTable, chatTemplateTable.getHeight());

        roomUsersPopup = new ChatPopup(assets, 49);
        roomUsersPopup.setPosition(10, 50);

        ////////////////////////////////////////////////////
        //Population
        ///////////////////////////////////////////////////
        chatBoxContainer.addActor(chatBoxBackground);
        chatBoxContainer.add(chatBoxInnerContainer).expand().fill();

        root.addActor(chatBoxTransContainer);
        root.add(chatBoxContainer).expandX().fillX().height(CHAT_CONTAINER_HEIGHT);
        root.addActor(chatTemplatesPopup);
        root.addActor(roomUsersPopup);
        root.addActor(recordingTable);
        root.setFillParent(true);

//        showClickedPositionLabel = new Label("0", recordingLabelStyle);
//        showClickedPositionLabel.setPosition(0, 300);
//        root.addActor(showClickedPositionLabel);

        setInternalListeners();

    }

//    public void updateClickPostion(final float x, final float y){
//        Threadings.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                showClickedPositionLabel.setText(x + ", " + y);
//            }
//        });
//    }

    public void populateMode1(){
        ////////////////////////
        //All Messages Table Mode 1
        ///////////////////////
        mode1MessagesContainer = new Table();
        mode1MessagesContainer.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.CHAT_CONTAINER)));
        mode1MessagesContainer.align(Align.top);
        mode1MessagesContainer.setPosition(0, CHAT_CONTAINER_HEIGHT);

        mode1MessagesTable = new Table();
        mode1MessagesTable.align(Align.top);
        mode1ChatScroll = new ScrollPane(mode1MessagesTable);
        mode1ChatScroll.setScrollingDisabled(true, false);
        mode1MessagesContainer.add(mode1ChatScroll).expand().fill().padLeft(15).padRight(15).padTop(3);


        root.addActor(mode1MessagesContainer);
        root.row();
    }

    public void populateMode2(){
        /////////////////////////////
        //All Messages Table Mode 2
        /////////////////////////////
        mode2MessagesContainer = new Table();
        mode2MessagesContainer.setTouchable(Touchable.disabled);
        mode2MessagesContainer.setPosition(0, 0);

        mode2MessagesTableBg = new Table();
        mode2MessagesTableBg.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.ORANGE_LESS_TRANS_BG)));
        mode2MessagesTableBg.setFillParent(true);
        mode2MessagesTableBg.setTouchable(Touchable.childrenOnly);
        mode2MessagesTableBg.getColor().a = 0f;
        mode2MessagesTableBg.align(Align.topRight);

        mode2CloseImage = new Image(assets.getTextures().get(Textures.Name.CHAT_CLOSE_ICON));
        mode2CloseImage.getColor().a = 0f;

        mode2MessagesTable = new Table();
        mode2MessagesTable.align(Align.bottomLeft);
        mode2MessagesTable.padBottom(CHAT_CONTAINER_HEIGHT + 5).padLeft(50).padRight(50).padTop(5);
        mode2MessagesTable.addActor(mode2MessagesTableBg);

        mode2ChatScroll = new ScrollPane(mode2MessagesTable);
        mode2ChatScroll.setOverscroll(false, false);

        mode2MessagesContainer.row();
        mode2MessagesContainer.add(mode2ChatScroll).expand().fill();
        mode2MessagesContainer.addActor(mode2CloseImage);

        root.addActor(mode2MessagesContainer);
    }

    public void refreshChatBoxInnerContainer(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                int space = 5;
                chatBoxInnerContainer.clearChildren();

                chatBoxInnerContainer.add(chatTemplateButton).width(BUTTON_WIDTH).space(space).padLeft(space).padBottom(3);
                chatBoxInnerContainer.add(roomUsersButtonRoot).width(BUTTON_WIDTH).space(space).padBottom(3);
                chatBoxInnerContainer.add(messageTextField).expandX().fillX().space(space).padTop(3).padBottom(3);
                if(!Global.IS_POTRAIT){
                    chatBoxInnerContainer.add(keyboardToggleButton).width(BUTTON_WIDTH).space(space).padBottom(3);
                }
                chatBoxInnerContainer.add(sendingRoot)
                        .space(space).padRight(space).size(BUTTON_WIDTH, sendButton.getPrefHeight())
                        .padBottom(3);
            }
        });
    }

    public void invalidate(final boolean isVisible){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(stage == null){
                    StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                    viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                    stage = new Stage(viewPort, batch);
                    iptGame.addInputProcessor(stage, 10, false);
                    stage.addActor(root);
                }
                else{
                    if(stage.getViewport().getWorldWidth() != Positions.getWidth()
                            || stage.getViewport().getWorldHeight() != Positions.getHeight()){
                        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                        viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                        stage.setViewport(viewPort);
                    }
                }
                refreshChatBoxInnerContainer();
                recordingTable.setPosition(Positions.getWidth() / 2 - recordingTable.getWidth() / 2,
                                        Positions.getHeight() / 2 - recordingTable.getHeight() / 2);

                mode1MessagesContainer.setSize(Positions.getWidth(), 130);
                mode2MessagesContainer.setSize(Positions.getWidth(), 75 + CHAT_CONTAINER_HEIGHT);
                mode2CloseImage.setPosition(mode2MessagesContainer.getWidth() - mode2CloseImage.getPrefWidth(),
                        mode2MessagesContainer.getHeight() - mode2CloseImage.getPrefHeight());

                if(!Global.IS_POTRAIT){
                    Threadings.delay(500, new Runnable() {
                        @Override
                        public void run() {
                            initTransTable();
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
                    mode1MessagesContainer.setVisible(true);
                    mode2MessagesContainer.setVisible(false);
                    chatBoxTransContainer.setVisible(false);
                    chatBoxContainer.clearActions();
                    chatBoxContainer.getColor().a = 1f;
                    chatBoxContainer.setVisible(true);
                }
                else if(mode == 2){
                    mode2MessagesContainer.setVisible(true);
                    mode1MessagesContainer.setVisible(false);
                    chatBoxTransContainer.setVisible(true);
                }
            }
        });
    }

    public void unfocusMessageTextField(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                stage.setKeyboardFocus(stage.getActors().get(0));
            }
        });
    }

    public void animateHideForMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                chatBoxContainer.clearActions();
                //chatBoxTransContainer.clearActions();

                chatBoxContainer.addAction(sequence(parallel(alpha(0.3f, 0.3f), new RunnableAction(){
                    @Override
                    public void run() {
//                        chatBoxTransContainer.getColor().a = 0.1f;
//                        chatBoxTransContainer.setVisible(true);
//                        chatBoxTransContainer.addAction(fadeIn(0.2f));
                        for(Actor button : getTransButtons()){
                            fadeOutTransButton(button);
                        }
                    }
                }), fadeOut(0.1f), new RunnableAction(){
                    @Override
                    public void run() {
                        chatBoxContainer.setVisible(false);
                        fadeOutMode2(0);
                    }
                }));
            }
        });
    }

    public void animateShowForMode2(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                chatBoxContainer.clearActions();
                //chatBoxTransContainer.clearActions();
                keyboardToggleButton.setChecked(true);

                chatBoxContainer.getColor().a = 0f;
                chatBoxContainer.setVisible(true);
                chatBoxContainer.addAction(fadeIn(0.2f));

//                chatBoxTransContainer.addAction(sequence(parallel(alpha(0.3f, 0.3f), new RunnableAction(){
//                    @Override
//                    public void run() {
//                        chatBoxContainer.getColor().a = 0f;
//                        chatBoxContainer.setVisible(true);
//                        chatBoxContainer.addAction(fadeIn(0.2f));
//                    }
//                }), fadeOut(0.1f), new RunnableAction(){
//                    @Override
//                    public void run() {
//                        //chatBoxTransContainer.setVisible(false);
//                    }
//                }));

                fadeInMode2(true);
            }
        });
    }

    public void initTransTable(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                chatBoxTransContainer.clear();

                Vector2 chatTemplateButtonPosition = Positions.actorLocalToStageCoord(chatTemplateButton);
                chatTemplateButtonTrans.setPosition(chatTemplateButtonPosition.x, chatTemplateButtonPosition.y - 6);

                Vector2 roomUsersButtonRootPosition = Positions.actorLocalToStageCoord(roomUsersButtonRoot);
                roomUsersButtonRootTrans.setSize(BUTTON_WIDTH, roomUsersButtonRoot.getPrefHeight());
                roomUsersButtonRootTrans.setPosition(roomUsersButtonRootPosition.x, roomUsersButtonRootPosition.y - 4);

                Vector2 keyboardButtonPosition = Positions.actorLocalToStageCoord(keyboardToggleButton);
                keyboardToggleButtonTrans.setPosition(keyboardButtonPosition.x, keyboardButtonPosition.y - 6);

                Vector2 sendButtonPosition = Positions.actorLocalToStageCoord(sendButton);
                sendButtonTrans.setPosition(sendButtonPosition.x, sendButtonPosition.y - 6);
                sendButtonTrans.setVisible(false);

                Vector2 micButtonPosition = Positions.actorLocalToStageCoord(micButton);
                micButtonTrans.setPosition(micButtonPosition.x, micButtonPosition.y - 6);

                chatBoxTransContainer.addActor(chatTemplateButtonTrans);
                chatBoxTransContainer.addActor(roomUsersButtonRootTrans);
                chatBoxTransContainer.addActor(keyboardToggleButtonTrans);
                chatBoxTransContainer.addActor(sendButtonTrans);
                chatBoxTransContainer.addActor(micButtonTrans);

                for(Actor actor : getTransButtons()){
                    actor.setWidth(BUTTON_WIDTH);
                }
            }
        });
    }

    private void fadeOutTransButton(final Actor transButton){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                transButton.clearActions();
                transButton.addAction(alpha(0.4f, 0.5f));
            }
        });
    }

    public void fadeInMode2(final boolean locked){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(locked && mode2MessagesTableBg.getColor().a < 1f){
                    mode2MessagesTableBg.clearActions();
                    mode2MessagesTableBg.getColor().a = 0f;
                    mode2MessagesTableBg.addAction(fadeIn(0.2f));
                    mode2CloseImage.clearActions();
                    mode2CloseImage.getColor().a = 0f;
                    mode2CloseImage.addAction(fadeIn(0.2f));

                }
                else if(!locked && mode2MessagesTableBg.getColor().a > 0f){
                    mode2MessagesTableBg.clearActions();
                    mode2MessagesTableBg.getColor().a = 0f;
                    mode2CloseImage.clearActions();
                    mode2CloseImage.getColor().a = 0f;
                }


                mode2MessagesContainer.setTouchable(locked ? Touchable.enabled : Touchable.disabled);
                mode2MessagesTable.clearActions();
                mode2MessagesTable.getColor().a = 1;
                mode2MessagesContainer.setName("fadeIn");
                for(VoiceMessageControl voiceMessageControl : mode2VoiceMessageControls){
                    voiceMessageControl.fadeInControl();
                }
            }
        });
    }

    public void fadeOutMode2(final int delaySecs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode2MessagesContainer.getName() == null || (!mode2MessagesContainer.getName().equals("fadeOut")
                        && !mode2MessagesContainer.getName().equals("fadeOutWait"))){
                    mode2MessagesTable.clearActions();
                    mode2MessagesTableBg.clearActions();
                    mode2MessagesTableBg.addAction(fadeOut(0.1f));
                    mode2CloseImage.clearActions();
                    mode2CloseImage.addAction(fadeOut(0.1f));

                    mode2MessagesContainer.setName("fadeOut");
                    mode2MessagesContainer.setTouchable(Touchable.disabled);
                    for(VoiceMessageControl voiceMessageControl : mode2VoiceMessageControls){
                        voiceMessageControl.fadeOutControl();
                    }

                    for(VoiceMessageControl voiceMessageControl : mode2VoiceMessageControls){
                        if(voiceMessageControl.isInPlayQueue()){
                            mode2MessagesContainer.setName("fadeOutWait");
                            return;
                        }
                    }
                    mode2MessagesTable.addAction(sequence(delay(delaySecs), fadeOut(0.3f)));

                }
            }
        });
    }

    public void fadeOutMode2IfApplicable(){
        if(mode2MessagesContainer.getName() != null && mode2MessagesContainer.getName().equals("fadeOutWait")){
            for(VoiceMessageControl voiceMessageControl : mode2VoiceMessageControls){
                if(voiceMessageControl.isInPlayQueue()){
                    return;
                }
            }

            mode2MessagesTable.addAction(sequence(delay(1), fadeOut(0.3f), new RunnableAction(){
                @Override
                public void run() {
                    mode2MessagesContainer.setName("fadedOut");
                }
            }));
        }
    }

    public void moveChatPosition(final float newY){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                root.setPosition(0, newY);
                //mode2MessagesContainer.setPosition(0, Global.IS_POTRAIT ? 80 + newY : 50 + newY);
            }
        });
    }

    public void showRecording(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                recordingTable.clearActions();
                recordingTable.getColor().a = 0f;
                recordingTable.addAction(fadeIn(0.2f));
                recordingTable.setVisible(true);
            }
        });
    }

    public void hideRecording(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(recordingTable.isVisible()){
                    recordingTable.clearActions();
                    recordingTable.addAction(sequence(fadeOut(0.2f), new RunnableAction(){
                        @Override
                        public void run() {
                            recordingTable.setVisible(false);
                        }
                    }));
                }
            }
        });
    }

    public void updateRecordStatus(final int soundsLevel, final int remainingSecs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Color originalColor = Color.valueOf("ebebeb");
                Color highLightColor = Color.valueOf("ffba44");

                if(soundsLevel == 3){
                    recordingWaveOne.setColor(highLightColor);
                    recordingWaveTwo.setColor(highLightColor);
                    recordingWaveThree.setColor(highLightColor);
                }
                else if(soundsLevel == 2){
                    recordingWaveOne.setColor(highLightColor);
                    recordingWaveTwo.setColor(highLightColor);
                    recordingWaveThree.setColor(originalColor);
                }
                else if(soundsLevel == 1){
                    recordingWaveOne.setColor(highLightColor);
                    recordingWaveTwo.setColor(originalColor);
                    recordingWaveThree.setColor(originalColor);
                }
                else if(soundsLevel == 0){
                    recordingWaveOne.setColor(originalColor);
                    recordingWaveTwo.setColor(originalColor);
                    recordingWaveThree.setColor(originalColor);
                }

                recordingTimerLabel.setText("0:" + String.format("%02d", remainingSecs));
            }
        });
    }

    public void add(ChatMessage msg, int mode, String myUserId){
        add(msg, mode, null, null, -1, myUserId);
    }

    public void add(final ChatMessage msg, final int mode, final String senderUserId, final String senderName, final int senderSlotIndex,
                    final String myUserId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    Table chatTable = new Table();
                    if (mode1MessagesTable.getCells().size == 0) {
                        chatTable.padTop(12);
                    }

                    Table messageTable = generateMessageTable(msg, mode, senderUserId, senderName, senderSlotIndex, myUserId);
                    if(messageTable == null) return;

                    chatTable.add(messageTable).expand().fill();

                    mode1MessagesTable.add(chatTable).expandX().fillX();
                    mode1MessagesTable.row();
                }
                else if(mode == 2){
                    Table chatTable = new Table();
                    chatTable.align(Align.left);
                    if (mode2MessagesTable.getCells().size == 0) {
                        chatTable.padTop(12);
                    }

                    Table messageTable = generateMessageTable(msg, mode, senderUserId, senderName, senderSlotIndex, myUserId);
                    if(messageTable == null) return;

                    chatTable.add(messageTable).expand().fill();

                    mode2MessagesTable.add(chatTable).expandX().fillX();
                    mode2MessagesTable.row();
                }

                soundsPlayer.playSoundEffect(Sounds.Name.MESSAGING);
            }
        });
    }

    public Table generateMessageTable(final ChatMessage msg, int mode,
                                      String senderUserId, String senderName, int senderSlotIndex,
                                      String myUserId){
        Table messageTable = new Table();

        if ((msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) && senderUserId == null) {
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
            lblUsernameStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD);
            lblUsernameStyle.fontColor = Color.BLACK;

            lblMessageStyle.font = assets.getFonts().get(Fonts.FontId.PT_S_REGULAR);
            lblMessageStyle.fontColor = Color.BLACK;

            lblInfoStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
            lblInfoStyle.fontColor = Color.valueOf("11b1bf");

            lblImportantStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
            lblImportantStyle.fontColor = Color.valueOf("F56C57");
        }
        else if(mode == 2){
            lblUsernameStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR_B_ffffff_000000_2);
            if(senderUserId != null){
                lblUsernameStyle.fontColor = ColorUtils.getUserColorByIndex(senderSlotIndex);
            }

            lblMessageStyle.font = assets.getFonts().get(Fonts.FontId.PT_S_REGULAR_B_ffffff_000000_2);
            lblMessageStyle.fontColor = Color.WHITE;

            lblInfoStyle.font = assets.getFonts().get(Fonts.FontId.PT_S_REGULAR_B_ffffff_000000_2);
            lblInfoStyle.fontColor = Color.valueOf("11b1bf");

            lblImportantStyle.font = assets.getFonts().get(Fonts.FontId.PT_S_REGULAR_B_ffffff_000000_2);
            lblImportantStyle.fontColor = Color.valueOf("F56C57");
        }

        if (msg.getFromType() == ChatMessage.FromType.USER || msg.getFromType() == ChatMessage.FromType.USER_VOICE) {
            Label lblUsername = new Label(Strings.cutOff(senderName, 25) + ": ", lblUsernameStyle);
            messageTable.add(lblUsername).minHeight(20).padRight(5);

            if(msg.getFromType() == ChatMessage.FromType.USER){
                Label lblMessage = new Label(msg.getMessage(), lblMessageStyle);
                lblMessage.setWrap(true);
                lblMessage.setAlignment(Align.left);
                messageTable.add(lblMessage).expandX().fillX().minHeight(20);
            }
            else if(msg.getFromType() == ChatMessage.FromType.USER_VOICE){
                if(renderingVoiceMessageControlsMap.containsKey(msg.getMessage())){
                    if(!msg.getExtra().equals("-1")){
                        VoiceMessageControl voiceMessageControl = renderingVoiceMessageControlsMap.get(msg.getMessage());
                        voiceMessageControl.updateTotalSecs(Integer.valueOf(msg.getExtra()));
                        renderingVoiceMessageControlsMap.remove(msg.getMessage());
                    }
                    return null;
                }

                VoiceMessageControl voiceMessageControl = new VoiceMessageControl(msg, assets, soundsPlayer, recorder, myUserId, mode == 2);
                messageTable.add(voiceMessageControl).expandX().fillX().minHeight(20).left();
                if(mode == 2){
                    mode2VoiceMessageControls.add(voiceMessageControl);
                }
                if(msg._extra.equals("-1")){
                    renderingVoiceMessageControlsMap.put(msg.getMessage(), voiceMessageControl);
                }
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
            Image separator = new Image(assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
            messageTable.add(separator).colspan(2).height(1).padTop(5).padBottom(5).expandX().fillX();
        }


        return messageTable;
    }

    public void scrollToBottom(){
        Threadings.delay(100, new Runnable() {
            @Override
            public void run() {
                mode1ChatScroll.setScrollPercentY(100);
                mode2ChatScroll.setScrollPercentY(100);
            }
        });
    }

    public void clearChat(final int mode){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(mode == 1){
                    mode1MessagesTable.clear();
                    renderingVoiceMessageControlsMap.clear();
                }
                else if(mode == 2){
                    mode2MessagesTable.clear();
                    mode2MessagesTable.addActor(mode2MessagesTableBg);
                    mode2MessagesTableBg.getColor().a = 0f;
                }
                mode2VoiceMessageControls.clear();
                for(String username : disconnectedCountDownThreads.keySet()){
                        disconnectedCountDownThreads.get(username).kill();
                }
                disconnectedCountDownThreads.clear();
                cacheRoomUserStatusTablesMap.clear();
            }
        });
    }

    public boolean positionHasChatElement(int mode, float x, float y){
        if(mode == 1){
            if(y <= chatBoxContainer.getHeight() + mode1MessagesContainer.getHeight()){
                return true;
            }
        }
        else if(mode == 2){
            if(chatBoxContainer.getName() != null && chatBoxContainer.getName().equals("shown")){
                if(mode2MessagesContainer.getTouchable() == Touchable.enabled){
                    if(y <= mode2MessagesContainer.getHeight()){
                        return true;
                    }
                }
                else{
                    if(y <= chatBoxContainer.getHeight()){
                        return true;
                    }
                }
            }
        }

        if(chatTemplatesPopup.getVisible()){
            if(y <= chatTemplatesPopup.getY() + chatTemplatesPopup.getPrefHeight() && y >=chatTemplatesPopup.getY()){
                if(x <= chatTemplatesPopup.getX() + chatTemplatesPopup.getPrefWidth() && x >=chatTemplatesPopup.getX()){
                    return true;
                }
            }
        }

        if(roomUsersPopup.getVisible()){
            if(y <= roomUsersPopup.getY() + roomUsersPopup.getPrefHeight() && y >=roomUsersPopup.getY()){
                if(x <= roomUsersPopup.getX() + roomUsersPopup.getPrefWidth() && x >=roomUsersPopup.getX()){
                    return true;
                }
            }
        }

        return false;
    }

    public void hidePopupsIfNotTouching(float x, float y){
        if(roomUsersButtonPosition == null){
            roomUsersButtonPosition = Positions.actorLocalToStageCoord(roomUsersButton);
            chatTemplateButtonPosition = Positions.actorLocalToStageCoord(chatTemplateButton);
        }

        if(chatTemplatesPopup.getVisible()){
            if(y > chatTemplatesPopup.getY() + chatTemplatesPopup.getPrefHeight()
                    || y < chatTemplatesPopup.getY()
                    || x > chatTemplatesPopup.getX() + chatTemplatesPopup.getPrefWidth()
                    || x < chatTemplatesPopup.getX()){
                if(y > chatTemplateButtonPosition.y + chatTemplateButton.getPrefHeight()
                        || y < chatTemplateButtonPosition.y
                        || x > chatTemplateButtonPosition.x + chatTemplateButton.getPrefWidth()
                        || x < chatTemplateButtonPosition.x)
                chatTemplatesPopup.hide();
            }
        }

        if(roomUsersPopup.getVisible()){
            if(y > roomUsersPopup.getY() + roomUsersPopup.getPrefHeight()
                    || y < roomUsersPopup.getY()
                    || x > roomUsersPopup.getX() + roomUsersPopup.getPrefWidth()
                    || x < roomUsersPopup.getX()){
                if(y > roomUsersButtonPosition.y + roomUsersButton.getPrefHeight()
                        || y < roomUsersButtonPosition.y
                        || x > roomUsersButtonPosition.x + roomUsersButton.getPrefWidth()
                        || x < roomUsersButtonPosition.x)
                    roomUsersPopup.hide();
            }
        }
    }

    public void hideChatTemplatePopup(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                chatTemplatesPopup.hide();
            }
        });
    }

    public void resetRecordDesign(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                recordingWaveOne.setColor(Color.valueOf("ebebeb"));
                recordingWaveTwo.setColor(Color.valueOf("ebebeb"));
                recordingWaveThree.setColor(Color.valueOf("ebebeb"));
                recordingTimerLabel.setText("0:10");
                recordCancelLabel.setText(texts.slideUpCancel());
            }
        });
    }


    public void render(float delta){
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    private void messageTextFieldChanged(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(Strings.isEmpty(messageTextField.getText())){
                    for(Actor sendButton : getSendButtons()){
                        sendButton.setVisible(false);
                    }
                    for(Actor micButton : getMicButtons()){
                        micButton.setVisible(true);
                    }
                }
                else{
                    for(Actor sendButton : getSendButtons()){
                        sendButton.setVisible(true);
                    }
                    for(Actor micButton : getMicButtons()){
                        micButton.setVisible(false);
                    }
                }
            }
        });
    }

    public void setMessageTextFieldMsg(final String msg, final int cursorPosition){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                messageTextField.setText(msg);
                if(cursorPosition != -1) messageTextField.setCursorPosition(cursorPosition);
                messageTextFieldChanged();
            }
        });
    }

    private void setInternalListeners(){

        for(final Actor transButton : getTransButtons()){
            transButton.addListener(new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    transButton.clearActions();
                    transButton.addAction(sequence(fadeIn(0.1f), new RunnableAction(){
                        @Override
                        public void run() {

                        }
                    }));

                    if(transButton == roomUsersButtonRootTrans){
                        roomUsersPopup.show();
                        soundsPlayer.playSoundEffect(Sounds.Name.OPEN_POPUP);
                    }
                    else if(transButton == chatTemplateButtonTrans){
                        chatTemplatesPopup.show();
                        soundsPlayer.playSoundEffect(Sounds.Name.OPEN_POPUP);
                    }
                    else if(transButton == keyboardToggleButtonTrans){
                        animateShowForMode2();
                    }

                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    if(transButton == roomUsersButtonRootTrans){
                        if(roomUsersPopup.isVisible()) return;
                    }
                    else if(transButton == chatTemplateButtonTrans){
                        if(chatTemplatesPopup.isVisible()) return;
                    }
                    fadeOutTransButton(transButton);
                }
            });
        }

        keyboardToggleButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                animateHideForMode2();
            }
        });

        roomUsersButton.addCheckStateListeners(new CheckStateListener() {
            @Override
            public void onChanged(boolean isChecked) {
                roomUsersLabel.getStyle().fontColor = isChecked ? Color.valueOf("7f580a") : Color.WHITE;

                if(isChecked){
                    roomUsersPopup.show();
                    soundsPlayer.playSoundEffect(Sounds.Name.OPEN_POPUP);
                }
                else{
                    roomUsersPopup.hide();
                }
            }
        });

        roomUsersPopup.addChatPopupListeners(new ChatPopupListener() {
            @Override
            public void onVisibleChanged(boolean isShown) {
                if(!isShown){
                    roomUsersButton.setChecked(false);
                    fadeOutTransButton(roomUsersButtonRootTrans);
                }
            }
        });

        chatTemplateButton.addCheckStateListeners(new CheckStateListener() {
            @Override
            public void onChanged(boolean isChecked) {
                if(isChecked){
                    chatTemplatesPopup.show();
                    soundsPlayer.playSoundEffect(Sounds.Name.OPEN_POPUP);
                }
                else{
                    chatTemplatesPopup.hide();
                }
            }
        });

        chatTemplatesPopup.addChatPopupListeners(new ChatPopupListener() {
            @Override
            public void onVisibleChanged(boolean isShown) {
                if(!isShown){
                    chatTemplateButton.setChecked(false);
                    fadeOutTransButton(chatTemplateButtonTrans);
                }
            }
        });

        messageTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               messageTextFieldChanged();
            }
        });

        mode2CloseImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                fadeOutMode2(0);
                unfocusMessageTextField();
            }
        });

    }

    public void refreshRoomUsersPopupDesign(final ArrayList<Pair<String, ConnectionStatusAndCountryModel>> playersConnectionStatusPairs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> connectedUsers = new ArrayList<String>();
                ArrayList<String> disconnectedUsers = new ArrayList<String>();
                ArrayList<String> disconnectedNoCountDownUsers = new ArrayList<String>();
                ArrayList<String> abandonedUsers = new ArrayList<String>();

                Table rootTable = new Table();
                rootTable.align(Align.top);
                rootTable.setHeight(150);
                rootTable.setWidth(170);

                rootTable.pad(20, 15, 20, 0);

                Table resultTable = new Table();
                ScrollPane scrollPane = new GreyScrollPane(resultTable, assets);
                rootTable.add(scrollPane).expand().fill().padRight(3);

                for(Pair<String, ConnectionStatusAndCountryModel> playerConnectionStatus : playersConnectionStatusPairs){
                    Table userStatusTable = null;
                    if(playerConnectionStatus.getSecond().getGameConnectionStatus() == GameConnectionStatus.Connected){
                        connectedUsers.add(playerConnectionStatus.getFirst());
                    }
                    else if(playerConnectionStatus.getSecond().getGameConnectionStatus() == GameConnectionStatus.Disconnected){
                        disconnectedUsers.add(playerConnectionStatus.getFirst());
                    }
                    else if(playerConnectionStatus.getSecond().getGameConnectionStatus() == GameConnectionStatus.Disconnected_No_CountDown){
                        disconnectedNoCountDownUsers.add(playerConnectionStatus.getFirst());
                    }
                    else if(playerConnectionStatus.getSecond().getGameConnectionStatus() == GameConnectionStatus.Abandoned){
                        abandonedUsers.add(playerConnectionStatus.getFirst());
                    }
                }

                Collections.sort(connectedUsers);
                Collections.sort(disconnectedUsers);
                Collections.sort(disconnectedNoCountDownUsers);
                Collections.sort(abandonedUsers);

                for(int i = 0; i < 4; i++){
                    ArrayList<String> users = null;
                    GameConnectionStatus status = null;
                    if(i == 0){
                        users = connectedUsers;
                        status = GameConnectionStatus.Connected;
                    }
                    else if(i == 1){
                        users = disconnectedUsers;
                        status = GameConnectionStatus.Disconnected;
                    }
                    else if(i == 2){
                        users = disconnectedNoCountDownUsers;
                        status = GameConnectionStatus.Disconnected_No_CountDown;
                    }
                    else if(i == 3){
                        users = abandonedUsers;
                        status = GameConnectionStatus.Abandoned;
                    }



                    for(String userId : users){
                        ConnectionStatusAndCountryModel connectionStatusAndCountryModel = null;
                        for(Pair<String, ConnectionStatusAndCountryModel> playerConnectionStatus : playersConnectionStatusPairs){
                            if(playerConnectionStatus.getFirst().equals(userId)){
                                connectionStatusAndCountryModel = playerConnectionStatus.getSecond();
                                break;
                            }
                        }
                        if(connectionStatusAndCountryModel == null)
                            connectionStatusAndCountryModel = new ConnectionStatusAndCountryModel();

                        Table userStatusTable = getRoomUserStatusDesign(userId, status, connectionStatusAndCountryModel.getCountry());

                        if(userStatusTable != null){
                            resultTable.add(userStatusTable).expandX().fillX().space(5).padRight(13);
                            resultTable.row();
                        }
                    }
                }
                roomUsersPopup.setActor(rootTable, 0);

                for(Label label : getRoomUsersLabels()){
                    label.setText(connectedUsers.size() + "/" + playersConnectionStatusPairs.size());
                }

            }
        });
    }

    private Table getRoomUserStatusDesign(String userName, GameConnectionStatus status, String country){

        if(disconnectedCountDownThreads.containsKey(userName)){
            disconnectedCountDownThreads.get(userName).kill();
            disconnectedCountDownThreads.remove(userName);
            cacheRoomUserStatusTablesMap.remove(userName + GameConnectionStatus.Disconnected.name());
        }


        String id = userName + status.name();
        if(cacheRoomUserStatusTablesMap.containsKey(id)){
            return cacheRoomUserStatusTablesMap.get(id);
        }

        Color color = null;
        switch (status){
            case Connected:
                color = Color.valueOf("37a719");
                break;
            case Disconnected: case Disconnected_No_CountDown:
                color = Color.valueOf("ff0000");
                break;
            case Abandoned:
                color = Color.valueOf("cfcdcd");
                break;
        }

        Table statusTable = new Table();

        Label userNameLabel = new Label(Strings.cutOff(userName, 15),
                new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR), color));

        Badge badge = new Badge(BadgeType.Country, "", assets, country);

        statusTable.add(badge).padRight(5);
        statusTable.add(userNameLabel).expandX().fillX();

        if(status != GameConnectionStatus.Disconnected){
            Image connectionDotImage = new Image(assets.getTextures().get(Textures.Name.WHITE_DOT));
            connectionDotImage.setColor(color);
            statusTable.add(connectionDotImage).right();
        }
        else{
            final Label countDownTimeLabel = new Label("",
                    new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR), color));
            statusTable.add(countDownTimeLabel).right();

            final SafeThread safeThread = new SafeThread();
            disconnectedCountDownThreads.put(userName, safeThread);
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    int i = Global.ABANDON_TOLERANCE_SECS;
                    while (i >= 0 && !safeThread.isKilled()){
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                countDownTimeLabel.setText(String.valueOf(finalI));
                            }
                        });
                        i--;
                        Threadings.sleep(1000);
                    }
                }
            });
        }

        cacheRoomUserStatusTablesMap.put(id, statusTable);

        return statusTable;
    }


    public TextField getMessageTextField() {
        return messageTextField;
    }

    public ArrayList<Label> getRoomUsersLabels(){
        ArrayList<Label> result = new ArrayList();
        result.add(roomUsersLabel);
        result.add(roomUsersLabelTrans);
        return result;
    }

    public ArrayList<Actor> getMicButtons() {
        ArrayList<Actor> result = new ArrayList();
        result.add(micButton);
        result.add(micButtonTrans);
        return result;
    }

    public ArrayList<Actor> getSendButtons() {
        ArrayList<Actor> result = new ArrayList();
        result.add(sendButton);
        result.add(sendButtonTrans);
        return result;
    }

    public ArrayList<Actor> getTransButtons() {
        ArrayList<Actor> result = new ArrayList();
        result.add(micButtonTrans);
        result.add(sendButtonTrans);
        result.add(chatTemplateButtonTrans);
        result.add(roomUsersButtonRootTrans);
        result.add(keyboardToggleButtonTrans);
        return result;
    }

    public Button getKeyboardToggleButton() {
        return keyboardToggleButton;
    }

    public Button getKeyboardToggleButtonTrans() {
        return keyboardToggleButtonTrans;
    }

    public Table getRoot() {
        return root;
    }
}
