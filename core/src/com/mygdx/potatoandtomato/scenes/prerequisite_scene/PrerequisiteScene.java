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
import com.mygdx.potatoandtomato.controls.BtnColor;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.Threadings;

/**
 * Created by SiongLeng on 15/12/2015.
 */
public class PrerequisiteScene extends SceneAbstract {

    Table loadingTable,  buttonsTable;
    Label msgLabel;
    BtnColor retryButton;
    BtnColor quitButton;

    public PrerequisiteScene(Services services, PTScreen screen) {
        super(services, screen);
    }


    @Override
    public void populateRoot() {
        topBar = new TopBar(_root, _texts.loading(), false, _assets, _screen, _services.getCoins());

        loadingTable = new Table();
        loadingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));

        Label.LabelStyle msgLabelStyle = new Label.LabelStyle();
        msgLabelStyle.fontColor = Color.valueOf("fff6d8");
        msgLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_S_a05e00_1_1);
        msgLabel = new Label("", msgLabelStyle);
        msgLabel.setWrap(true);
        msgLabel.setAlignment(Align.center);

        buttonsTable = new Table();

        retryButton = new BtnColor(BtnColor.ColorChoice.BLUE, _assets);
        retryButton.setText(_texts.retry());

        quitButton = new BtnColor(BtnColor.ColorChoice.RED, _assets);
        quitButton.setText(_texts.quit());

        loadingTable.add(msgLabel).expandX().fillX().padLeft(10).padRight(10);
        loadingTable.row();
        loadingTable.add(buttonsTable).width(140).height(60).padTop(20);
        _root.add(loadingTable);
    }

    public void changeMessage(final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                msgLabel.setText(text);
                buttonsTable.clear();
            }
        });
    }

    public void failedMessage(final String text, final boolean canRetry){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                msgLabel.setText(text);
                if(canRetry){
                    buttonsTable.add(retryButton).expand().fill();
                }
                else{
                    buttonsTable.add(quitButton).expand().fill();
                }
            }
        });
    }

    public BtnColor getRetryButton() {
        return retryButton;
    }

    public BtnColor getQuitButton() {
        return quitButton;
    }
}
