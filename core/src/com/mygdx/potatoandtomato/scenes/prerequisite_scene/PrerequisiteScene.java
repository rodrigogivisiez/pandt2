package com.mygdx.potatoandtomato.scenes.prerequisite_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.controls.BtnColor;
import com.mygdx.potatoandtomato.helpers.controls.DummyButton;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class PrerequisiteScene extends SceneAbstract {

    Table _loadingTable;
    Label _msgLabel;
    ProgressBar _progressBar;
    BtnColor _retryButton;

    public PrerequisiteScene(Services services) {
        super(services);
    }

    public BtnColor getRetryButton() {
        return _retryButton;
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.loading(), false, _textures, _fonts);

        _loadingTable = new Table();
        _loadingTable.align(Align.top);
        _loadingTable.setBackground(new TextureRegionDrawable(_textures.getWoodBgFat()));

        Label.LabelStyle msgLabelStyle = new Label.LabelStyle();
        msgLabelStyle.font = _fonts.getPizzaFont(23, Color.WHITE, 0, Color.BLACK, 2, Color.GRAY);
        _msgLabel = new Label("", msgLabelStyle);
        _msgLabel.setWrap(true);
        _msgLabel.setAlignment(Align.center);

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = new NinePatchDrawable(_textures.getProgressBarBg());
        progressBarStyle.knobBefore = progressBarStyle.knob = new NinePatchDrawable(_textures.getProgressBarInner());
        _progressBar = new ProgressBar(0, 100, 1, false, progressBarStyle);
        _progressBar.setAnimateDuration(0.5f);
        _progressBar.setValue(0);

        _retryButton = new BtnColor(BtnColor.ColorChoice.RED, _fonts, _textures);
        _retryButton.setText(_texts.retry());
        _retryButton.setVisible(false);

        _loadingTable.add(_msgLabel).height(150).expandX().fillX().padLeft(20).padRight(20);
        _loadingTable.row();
        _loadingTable.add(_progressBar).expandX().fillX().padLeft(20).padRight(20).padBottom(20);
        _loadingTable.row();
        _loadingTable.add(_retryButton);
        _root.add(_loadingTable);
    }

    public void changeMessage(String text){
        _msgLabel.setText(text);
        _retryButton.setVisible(false);
    }

    public void failedMessage(String text){
        _msgLabel.setText(text);
        _retryButton.setVisible(true);
    }

    public void setProgressBarValue(float value){
        _progressBar.setValue(value);
    }


}
