package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.mygdx.potatoandtomato.helpers.services.Assets;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.potatoandtomato.common.IPTGame;

import javax.swing.plaf.synth.SynthGraphicsUtils;

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
    Image _closeButton;
    DummyButton _buttonYes, _buttonNo;
    ConfirmResultListener _listener;
    Label _messageLabel;
    Table _buttonsTable, _msgTable, _buttonYesTable, _buttonNoTable;
    boolean _visible;
    Stage _stage;
    SpriteBatch _batch;
    IPTGame _game;
    ConfirmStateChangedListener _stateChangedListener;
    long _previousTime;

    public Confirm(SpriteBatch spriteBatch, IPTGame game, Assets assets) {
        _batch = spriteBatch;
        _assets = assets;
        _game = game;
        _previousTime = 0;

        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight());
        _stage = new Stage(viewPort, _batch);

        _confirmRoot = new Table();
        _confirmRoot.setBackground(new TextureRegionDrawable(_assets.getBlackBg()));
        _confirmRoot.setFillParent(true);
        new DummyButton(_confirmRoot, _assets);

        _msgTable = new Table();
        _msgTable.setBackground(new NinePatchDrawable(_assets.getPopupBg()));
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _assets.getWhitePizza3BlackS();
        _messageLabel = new Label("", labelStyle);
        _messageLabel.setWrap(true);
        _messageLabel.setAlignment(Align.center);

        _closeButton = new Image(_assets.getCloseButton());

        _buttonsTable = new Table();

        _buttonYesTable = new Table();
        _buttonYesTable.setBackground(new NinePatchDrawable(_assets.getButtonGreen()));
        Vector2 yesSize = Sizes.resize(30, _assets.getTick());
        Image yesImage = new Image(_assets.getTick());
        _buttonYesTable.add(yesImage).size(yesSize.x, yesSize.y);
        _buttonYes = new DummyButton(_buttonYesTable, _assets);

        _buttonNoTable = new Table();
        _buttonNoTable.setBackground(new NinePatchDrawable(_assets.getButtonRed()));
        Vector2 noSize = Sizes.resize(30, _assets.getCross());
        Image noImage = new Image(_assets.getCross());
        _buttonNoTable.add(noImage).size(noSize.x, noSize.y);
        _buttonNo = new DummyButton(_buttonNoTable, _assets);


        _msgTable.add(_closeButton).expandX().right().padTop(-50).colspan(2);
        _msgTable.row();
        _msgTable.add(_messageLabel).padTop(10).padBottom(20).colspan(2).expandX().fillX().padLeft(10).padRight(10);
        _msgTable.row();
        _msgTable.add(_buttonsTable).expandX().fillX();


        _confirmRoot.add(_msgTable).expandX().fillX();
        _stage.addActor(_confirmRoot);

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

                _msgTable.getColor().a = 0;
                _messageLabel.setText(msg);
                _buttonsTable.clear();

                if(type == Type.YESNO){
                    _buttonsTable.add(_buttonYesTable).uniformX().space(20);
                    _buttonsTable.add(_buttonNoTable).uniformX().space(20);
                }
                else if(type == Type.YES){
                    _buttonsTable.add(_buttonYesTable).center().expandX();
                }

                Threadings.renderFor(5f);

                _confirmRoot.addAction(sequence(fadeOut(0f), fadeIn(0.3f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _msgTable.addAction(sequence(moveBy(-50, 0), fadeOut(0f), parallel(moveBy(50, 0, 0.1f), fadeIn(0.1f)), new Action() {
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

                _game.addInputProcessor(_stage, 5);
            }
        });


    }



    private void attachEvent(){

        _confirmRoot.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        _buttonYes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_listener != null){
                    _listener.onResult(ConfirmResultListener.Result.YES);
                }
                close();
            }
        });

        _buttonNo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_listener != null){
                    _listener.onResult(ConfirmResultListener.Result.NO);
                }
                close();
            }
        });

        _closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_listener != null){
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
