package com.mygdx.potatoandtomato.helpers.services;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
import com.mygdx.potatoandtomato.helpers.controls.DummyButton;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.assets.Assets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 14/12/2015.
 */
public class Confirm {

    public enum Type{
        YESNO, YES
    }
    Assets _assets;
    Table _confirmRoot;
    Image _yesImage, _noImage;
    ConfirmResultListener _listener;
    Label _messageLabel;
    Table _buttonsTable, _msgTable;
    boolean _visible;
    Stage _stage;
    SpriteBatch _batch;
    IPTGame _game;
    ConfirmStateChangedListener _stateChangedListener;
    long _previousTime;
    Broadcaster _broadcaster;

    public Confirm(SpriteBatch spriteBatch, IPTGame game, Assets assets, Broadcaster broadcaster) {
        _batch = spriteBatch;
        _assets = assets;
        _game = game;
        _previousTime = 0;
        _broadcaster = broadcaster;

        _confirmRoot = new Table();
        invalidate();

        _broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener() {
            @Override
            public void onCallback(Object obj, Status st) {
                invalidate();
            }
        });
    }

    public void invalidate(){
        if(_stage != null){
            _stage.dispose();
            _confirmRoot.remove();
            _confirmRoot.clear();
        }


        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _confirmRoot.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
        _confirmRoot.setFillParent(true);
        _confirmRoot.align(Align.bottom);
        new DummyButton(_confirmRoot, _assets);

        _msgTable = new Table();
        _msgTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.POPUP_BG)));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
        labelStyle.fontColor = Color.BLACK;
        _messageLabel = new Label("", labelStyle);
        _messageLabel.setWrap(true);
        _messageLabel.setAlignment(Align.center);

        ScrollPane scrollPane = new ScrollPane(_messageLabel);

        _buttonsTable = new Table();

        _yesImage = new Image(_assets.getTextures().get(Textures.Name.TICK_ICON));
        _noImage = new Image(_assets.getTextures().get(Textures.Name.CROSS_ICON));

        _msgTable.add(scrollPane).padTop(20).padBottom(20).expand().fill().padLeft(10).padRight(10);
        _msgTable.row();
        _msgTable.add(_buttonsTable).expandX().fillX().padBottom(20);


        _confirmRoot.add(_msgTable).expandX().fillX();
        _confirmRoot.invalidate();

        _stage.addActor(_confirmRoot);
        close();

        attachEvent();

    }

    public void show(final String msg, final Type type, final ConfirmResultListener _listener){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                setListener(_listener);

                if(_previousTime !=0 && System.currentTimeMillis() - _previousTime < 500){
                    return;
                }
                _previousTime = System.currentTimeMillis();

                _messageLabel.setText(msg);
                _buttonsTable.clear();

                int w = 70;

                if(type == Type.YESNO){
                    _buttonsTable.add(_yesImage).size(w, w).space(70);
                    _buttonsTable.add(_noImage).size(w, w).space(70);
                }
                else if(type == Type.YES){
                    _buttonsTable.add(_yesImage).size(w, w);
                }

                _msgTable.getColor().a = 0;
                Threadings.renderFor(5f);
                _confirmRoot.clearActions();
                _confirmRoot.addAction(sequence(fadeOut(0f), fadeIn(0.3f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _msgTable.addAction(sequence(moveBy(0, -400), fadeIn(0f), moveBy(0, 400, 0.3f), new Action() {
                            @Override
                            public boolean act(float delta) {
                                return true;
                            }
                        }));
                        if(_stateChangedListener != null) _stateChangedListener.onShow();
                        return true;
                    }
                }));

                _visible = true;

                _game.addInputProcessor(_stage, 11);
            }
        });


    }

    public void resize(int width, int height){
        _stage.getViewport().update(width, height);
    }

    private void attachEvent(){

        _yesImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (_listener != null) {
                    _listener.onResult(ConfirmResultListener.Result.YES);
                }
                close();
            }
        });

        _noImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (_listener != null) {
                    _listener.onResult(ConfirmResultListener.Result.NO);
                }
                close();
            }
        });


    }

    public void setListener(ConfirmResultListener _listener) {
        this._listener = _listener;
    }

    public void setStateChangedListener(ConfirmStateChangedListener _stateListener) {
        this._stateChangedListener = _stateListener;
    }

    public void close(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _confirmRoot.clearActions();
                _confirmRoot.addAction(sequence(fadeOut(0.2f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _visible = false;
                        _game.removeInputProcessor(_stage);
                        if(_stateChangedListener != null) _stateChangedListener.onHide();
                        return true;
                    }
                }));
            }
        });
    }

    public boolean isVisible() {
        return _visible;
    }

    public void render(float delta){
        if(_visible){
            try{
                _stage.act(delta);
                _stage.draw();
            }
            catch (Exception e){

            }
        }
    }


}
