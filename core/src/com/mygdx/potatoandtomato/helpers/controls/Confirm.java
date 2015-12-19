package com.mygdx.potatoandtomato.helpers.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.helpers.services.Fonts;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;

import java.util.AbstractList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 14/12/2015.
 */
public class Confirm {

    public enum Type{
        YESNO, YES
    }

    Table _root;
    Textures _textures;
    String _msg;
    Type _type;
    Table _confirmRoot;
    Fonts _fonts;
    Image _closeButton;
    DummyButton _buttonYes, _buttonNo;
    ConfirmResultListener _listener;

    public Confirm(Table root, Textures textures, Fonts fonts, String msg, Type type) {
        _root = root;
        _textures = textures;
        _msg = msg;
        _fonts = fonts;
        _type = type;
    }

    public void show(String text){
        _msg = text;
        show();
    }

    public void show(){
        Stage stage = _root.getStage();

        if(stage == null) return;

        _root.setTouchable(Touchable.disabled);

        _confirmRoot = new Table();
        _confirmRoot.setBackground(new TextureRegionDrawable(_textures.getBlackBg()));
        _confirmRoot.setFillParent(true);
        _confirmRoot.getColor().a = 0;

        final Table msgTable = new Table();
        msgTable.setBackground(new NinePatchDrawable(_textures.getPopupBg()));
        msgTable.getColor().a = 0;
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _fonts.getPizzaFont(21, Color.WHITE, 1, Color.BLACK, 2, Color.GRAY);
        Label messageLabel = new Label(_msg, labelStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);

        _closeButton = new Image(_textures.getCloseButton());

        Table buttonYesTable = new Table();
        buttonYesTable.setBackground(new NinePatchDrawable(_textures.getButtonGreen()));
        Vector2 yesSize = Sizes.resize(30, _textures.getTick());
        Image yesImage = new Image(_textures.getTick());
        buttonYesTable.add(yesImage).size(yesSize.x, yesSize.y);
        _buttonYes = new DummyButton(buttonYesTable, _textures);

        Table buttonNoTable = new Table();
        buttonNoTable.setBackground(new NinePatchDrawable(_textures.getButtonRed()));
        Vector2 noSize = Sizes.resize(30, _textures.getCross());
        Image noImage = new Image(_textures.getCross());
        buttonNoTable.add(noImage).size(noSize.x, noSize.y);
        _buttonNo = new DummyButton(buttonNoTable, _textures);

        msgTable.add(_closeButton).expandX().right().padTop(-50).colspan(2);
        msgTable.row();
        msgTable.add(messageLabel).padTop(10).padBottom(20).colspan(2).expandX().fillX().padLeft(10).padRight(10);
        msgTable.row();

        if(_type == Type.YESNO){
            msgTable.add(buttonYesTable).uniformX();
            msgTable.add(buttonNoTable).uniformX();
        }
        else if(_type == Type.YES){
            msgTable.add(buttonYesTable).center().expandX();
        }



        _confirmRoot.addAction(sequence(fadeIn(0.3f), new Action() {
            @Override
            public boolean act(float delta) {
                msgTable.addAction(sequence(moveBy(-50, 0), parallel(moveBy(50, 0, 0.1f), fadeIn(0.1f))));
                return true;
            }
        }));

        _confirmRoot.add(msgTable).expandX().fillX();
        stage.addActor(_confirmRoot);

        attachEvent();
    }

    private void attachEvent(){
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

    public void close(){
        _confirmRoot.addAction(sequence(fadeOut(0.2f), new Action() {
            @Override
            public boolean act(float delta) {
                _root.setTouchable(Touchable.enabled);
                _confirmRoot.remove();
                _confirmRoot = null;
                return true;
            }
        }));
    }


}
