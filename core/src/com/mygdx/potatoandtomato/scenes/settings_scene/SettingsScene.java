package com.mygdx.potatoandtomato.scenes.settings_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.BtnColor;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public class SettingsScene extends SceneAbstract {

    TextField _displayNameTextField;
    BtnColor _facebookBtn, _saveBtn, _reportBtn;

    public SettingsScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.settingsTitle(), false, _assets, _screen);

        Label.LabelStyle labelTitleStyle = new Label.LabelStyle(_assets.getOrangePizza3(), Color.WHITE);

        Table settingsTable = new Table();
        settingsTable.setBackground(new NinePatchDrawable(_assets.getIrregularBg()));
        settingsTable.pad(10);

        ///////////////////
        //Display name
        ////////////////////
        Label profileLabel = new Label(_texts.profile(), labelTitleStyle);

        Label displayNameLabel = new Label(_texts.displayName(), new Label.LabelStyle(_assets.getWhitePizza2BlackS(), Color.WHITE));

        Table displayNameFieldTable = new Table();
        displayNameFieldTable.setBackground(new NinePatchDrawable(_assets.getWhiteRoundedBg()));
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _assets.getBlackNormal3();
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(_assets.getTextCursor());
        _displayNameTextField = new TextField(_services.getProfile().getDisplayName(15), textFieldStyle);
        displayNameFieldTable.add(_displayNameTextField).expand().fill().pad(10);

        //////////////////////////
        //Save Button
        //////////////////////////
        _saveBtn = new BtnColor(BtnColor.ColorChoice.GREEN, _assets);
        _saveBtn.setText(_texts.save());


        //////////////////////////
        //Separator
        //////////////////////////
        Image separatorImage = new Image(_assets.getWhiteLine());

        ///////////////////////////
        //Facebook status
        //////////////////////////
        Label socialLabel = new Label(_texts.facebook(), labelTitleStyle);

        _facebookBtn = new BtnColor(BtnColor.ColorChoice.BLUE, _assets);
        _facebookBtn.setText(_services.getSocials().isFacebookLogon() ? _texts.logout() : _texts.login());

        //////////////////////////
        //Separator
        //////////////////////////
        Image separatorImage2 = new Image(_assets.getWhiteLine());

        ///////////////////////////
        //Reporting
        //////////////////////////
        Label otherLabel = new Label(_texts.others(), labelTitleStyle);

        _reportBtn = new BtnColor(BtnColor.ColorChoice.RED, _assets);
        _reportBtn.setText(_texts.showReport());


        ////////////////////////
        //populations
        /////////////////////////
        settingsTable.align(Align.top);
        settingsTable.add(profileLabel).colspan(2).center().padBottom(15);
        settingsTable.row();
        settingsTable.add(displayNameLabel).width(150);
        settingsTable.add(displayNameFieldTable).expandX().fillX();
        settingsTable.row();
        settingsTable.add(_saveBtn).expandX().center().colspan(2).padTop(10).height(50);
        settingsTable.row();
        settingsTable.add(separatorImage).expandX().fillX().colspan(2).padTop(10).padBottom(10);
        settingsTable.row();
        settingsTable.add(socialLabel).colspan(2).center().padBottom(15);
        settingsTable.row();
        settingsTable.add(_facebookBtn).expandX().center().height(50).colspan(2);
        settingsTable.row();
        settingsTable.add(separatorImage2).expandX().fillX().colspan(2).padTop(10).padBottom(10);
        settingsTable.row();
        settingsTable.add(otherLabel).colspan(2).center().padBottom(15);
        settingsTable.row();
        settingsTable.add(_reportBtn).expandX().center().height(50).colspan(2);

        _root.add(settingsTable);
    }

    public TextField getDisplayNameTextField() {
        return _displayNameTextField;
    }

    public BtnColor getFacebookBtn() {
        return _facebookBtn;
    }

    public BtnColor getSaveBtn() {
        return _saveBtn;
    }

    public BtnColor getReportBtn() {
        return _reportBtn;
    }
}
