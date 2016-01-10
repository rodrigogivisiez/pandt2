package com.mygdx.potatoandtomato.scenes.input_name_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.BtnColor;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 10/1/2016.
 */
public class InputNameScene extends SceneAbstract {

    TextField _displayNameTextField;
    BtnColor _btnConfirm;

    public InputNameScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public TextField getDisplayNameTextField() {
        return _displayNameTextField;
    }

    public BtnColor getBtnConfirm() {
        return _btnConfirm;
    }

    @Override
    public void populateRoot() {

        _root.pad(20);

        Table questionTable = new Table();
        questionTable.setBackground(new NinePatchDrawable(_assets.getIrregularBg()));
        questionTable.pad(15);

        Label.LabelStyle questionLabelStyle = new Label.LabelStyle();
        questionLabelStyle.font = _assets.getWhitePizza3BlackS();
        Label questionLabel = new Label(_texts.askForName(), questionLabelStyle);
        questionLabel.setWrap(true);
        questionLabel.setAlignment(Align.center);

        Table displayNameFieldTable = new Table();
        displayNameFieldTable.setBackground(new NinePatchDrawable(_assets.getWhiteRoundedBg()));
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _assets.getBlackNormal3();
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(_assets.getTextCursor());
        _displayNameTextField = new TextField(_services.getProfile().getGameName(), textFieldStyle);
        displayNameFieldTable.add(_displayNameTextField).expand().fill().pad(10);

        _btnConfirm = new BtnColor(BtnColor.ColorChoice.GREEN, _assets);
        _btnConfirm.setImage(_assets.getTick(), 30);

        questionTable.add(questionLabel).expandX().fillX();
        questionTable.row();
        questionTable.add(displayNameFieldTable).expandX().fillX().padTop(20);
        questionTable.row();
        questionTable.add(_btnConfirm).width(150).height(50).padTop(20);

        _root.add(questionTable).expandX().fillX().padBottom(30);
    }
}
