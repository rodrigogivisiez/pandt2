package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.controls.ConfirmStateChangedListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.DummyButton;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.ConfirmMsgType;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 14/12/2015.
 */
public class Confirm {

    Assets assets;
    SpriteBatch batch;
    IPTGame game;
    Broadcaster broadcaster;
    Stage stage;
    Table confirmRoot;
    Image yesImage, noImage;
    Table cancelButtonTable;
    Label messageLabel;
    Table buttonsTable, msgTable;
    boolean visible;
    long previousTime;
    ConfirmIdentifier currentConfirmIdentifier;
    Type currentType;
    boolean locked;
    ConfirmResultListener confirmResultListener;
    ConfirmStateChangedListener stateChangedListener;


    public Confirm(SpriteBatch spriteBatch, IPTGame game, Assets assets, Broadcaster broadcaster) {
        batch = spriteBatch;
        this.assets = assets;
        this.game = game;
        previousTime = 0;
        this.broadcaster = broadcaster;

        populateRoot();
        invalidate();

        this.broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                invalidate();
            }
        });
    }

    public void populateRoot(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                confirmRoot = new Table();
                confirmRoot.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
                confirmRoot.setFillParent(true);
                confirmRoot.align(Align.bottom);
                new DummyButton(confirmRoot, assets);

                msgTable = new Table();
                msgTable.setBackground(new NinePatchDrawable(assets.getPatches().get(Patches.Name.POPUP_BG)));

                Label.LabelStyle labelStyle = new Label.LabelStyle();
                labelStyle.font = assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
                labelStyle.fontColor = Color.BLACK;
                messageLabel = new Label("", labelStyle);
                messageLabel.setWrap(true);
                messageLabel.setAlignment(Align.center);

                buttonsTable = new Table();

                yesImage = new Image(assets.getTextures().get(Textures.Name.TICK_ICON));
                noImage = new Image(assets.getTextures().get(Textures.Name.CROSS_ICON));
                cancelButtonTable = new Table();
                Label cancelButtonLabel = new Label("",
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.HELVETICA_M_REGULAR),  Color.valueOf("1a0dab")));
                cancelButtonLabel.setName("cancelButtonLabel");
                Image underLineImage = new Image(assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
                underLineImage.setColor(Color.valueOf("1a0dab"));
                cancelButtonTable.add(cancelButtonLabel);
                cancelButtonTable.row();
                cancelButtonTable.add(underLineImage).expandX().fillX();


                new DummyButton(cancelButtonTable, assets);

                confirmRoot.add(msgTable).expandX().fillX();

                attachEvent();
            }
        });
    }

    public void invalidate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(stage == null){
                    StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                    viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                    stage = new Stage(viewPort, batch);
                    game.addInputProcessor(stage, 15, false);
                    stage.addActor(confirmRoot);
                }
                else{
                    if(stage.getViewport().getWorldWidth() != Positions.getWidth()
                            || stage.getViewport().getWorldHeight() != Positions.getHeight()){
                        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
                        viewPort.update(Positions.getWidth(), Positions.getHeight(), true);
                        stage.setViewport(viewPort);
                    }
                }
            }
        });
    }

    public void show(ConfirmIdentifier identifier, final String msg, final Type type, final ConfirmResultListener listener){
        show(identifier, msg, type, listener, "");
    }

    public void show(ConfirmIdentifier identifier, final String msg, final Type type, final ConfirmResultListener listener, final String extra){
        if(currentConfirmIdentifier != null && currentConfirmIdentifier == identifier) return;
        if(locked) return;

        if(type == Type.LOADING_WITH_CANCEL || type == Type.LOADING_NO_CANCEL){
            locked = true;
        }
        visible = true;
        currentConfirmIdentifier = identifier;
        currentType = type;

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                confirmResultListener = listener;

                if (previousTime != 0 && System.currentTimeMillis() - previousTime < 500) {
                    return;
                }
                previousTime = System.currentTimeMillis();

                msgTable.clear();
                if(type == Type.YESNO || type == Type.YES){
                    msgTable.add(messageLabel).padTop(20).padBottom(20).expand().fill().padLeft(10).padRight(10);
                    msgTable.row();
                    msgTable.add(buttonsTable).expandX().fillX().padBottom(20);

                    messageLabel.setText(msg);
                }
                else if(type == Type.LOADING_NO_CANCEL || type == Type.LOADING_WITH_CANCEL){

                    messageLabel.setText(msg);
                    Table loadingTable = new Table();
                    Image loadingMascotsImage = new Image(assets.getTextures().get(Textures.Name.LOGGING_IN_MASCOTS));
                    Vector2 sizes = Sizes.resizeByH(40, assets.getTextures().get(Textures.Name.LOGGING_IN_MASCOTS));
                    loadingMascotsImage.setSize(sizes.x, sizes.y);
                    loadingMascotsImage.setPosition(-100, 0);
                    loadingMascotsImage.addAction(forever(sequence(moveBy(Positions.getWidth() + 100, 0, (Positions.getWidth() + 100)/ 100),
                                                                moveTo(-100, 0))));
                    loadingTable.addActor(loadingMascotsImage);

                    msgTable.add(loadingTable).expandX().fillX().padTop(10).height(sizes.y);
                    msgTable.row();
                    msgTable.add(messageLabel).padTop(5).padBottom(5).expandX().fillX().padLeft(10).padRight(10);
                    msgTable.row();
                    msgTable.add(buttonsTable).expandX().fillX().padBottom(10);

                }

                setButtonTable(type, extra);
                animateShowConfirm();
            }
        });
    }

    public void updateMessage(final String newMessage){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(newMessage);
            }
        });
    }

    private void setButtonTable(final Type type, final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                buttonsTable.clear();
                int w = 70;

                if (type == Type.YESNO) {
                    buttonsTable.add(yesImage).size(w, w).space(70);
                    buttonsTable.add(noImage).size(w, w).space(70);
                } else if (type == Type.YES) {
                    buttonsTable.add(yesImage).size(w, w);
                }
                else if(type == Type.LOADING_WITH_CANCEL){
                    ((Label) cancelButtonTable.findActor("cancelButtonLabel")).setText(text);
                    buttonsTable.add(cancelButtonTable);
                }

            }
        });
    }

    private void animateShowConfirm(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                msgTable.getColor().a = 0;
                Threadings.renderFor(5f);
                confirmRoot.setVisible(true);
                confirmRoot.clearActions();
                confirmRoot.addAction(sequence(fadeOut(0f), fadeIn(0.3f), new RunnableAction() {
                    @Override
                    public void run() {
                        msgTable.addAction(sequence(moveBy(0, -400), fadeIn(0f), moveBy(0, 400, 0.3f)));
                        if (stateChangedListener != null) stateChangedListener.onShow();
                    }
                }));
            }
        });
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    private void attachEvent(){

        yesImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (confirmResultListener != null) {
                    confirmResultListener.onResult(ConfirmResultListener.Result.YES);
                }
                close();
            }
        });

        noImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (confirmResultListener != null) {
                    confirmResultListener.onResult(ConfirmResultListener.Result.NO);
                }
                close();
            }
        });

        cancelButtonTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (confirmResultListener != null) {
                    confirmResultListener.onResult(ConfirmResultListener.Result.CANCEL);
                }
                close();
            }
        });

    }

    public void setStateChangedListener(ConfirmStateChangedListener _stateListener) {
        this.stateChangedListener = _stateListener;
    }

    public void close(ConfirmIdentifier confirmIdentifier){
        if(currentConfirmIdentifier == confirmIdentifier){
            close();
        }
    }

    public void close(Type type){
        if(currentType == type){
            close();
        }
    }

    private void close(){
        locked = false;
        currentConfirmIdentifier = null;
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                confirmRoot.clearActions();
                confirmRoot.addAction(sequence(fadeOut(0.2f), new RunnableAction() {
                    @Override
                    public void run() {
                        msgTable.clear();
                        confirmRoot.setVisible(false);
                        visible = false;
                        if (stateChangedListener != null) stateChangedListener.onHide();
                    }
                }));
            }
        });
    }

    public boolean isVisible() {
        return visible;
    }

    public void render(float delta){
        if(visible){
            try{
                stage.act(delta);
                stage.draw();
            }
            catch (Exception e){

            }
        }
    }

    public String getClassTag(){
        return this.getClass().getName();
    }

    public enum Type{
        YESNO, YES, LOADING_WITH_CANCEL, LOADING_NO_CANCEL
    }
}
