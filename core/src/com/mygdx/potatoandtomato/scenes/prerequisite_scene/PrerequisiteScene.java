package com.mygdx.potatoandtomato.scenes.prerequisite_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.controls.BtnColor;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class PrerequisiteScene extends SceneAbstract {

    Table _loadingTable;
    Label _msgLabel;
    BtnColor _retryButton;

    public PrerequisiteScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public BtnColor getRetryButton() {
        return _retryButton;
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.loading(), false, _assets, _screen);

        _loadingTable = new Table();
        _loadingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));

        Label.LabelStyle msgLabelStyle = new Label.LabelStyle();
        msgLabelStyle.fontColor = Color.valueOf("fff6d8");
        msgLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_S_a05e00_1_1);
        _msgLabel = new Label("", msgLabelStyle);
        _msgLabel.setWrap(true);
        _msgLabel.setAlignment(Align.center);

        _retryButton = new BtnColor(BtnColor.ColorChoice.RED, _assets);
        _retryButton.setText(_texts.retry());
        _retryButton.setVisible(false);

        _loadingTable.add(_msgLabel).expandX().fillX().padLeft(10).padRight(10);
        _loadingTable.row();
        _loadingTable.add(_retryButton).width(140).padTop(20);
        _root.add(_loadingTable);
    }

    public void changeMessage(final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _msgLabel.setText(text);
                _retryButton.setVisible(false);
            }
        });
    }

    public void failedMessage(final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _msgLabel.setText(text);
                _retryButton.setVisible(true);
            }
        });
    }



}
