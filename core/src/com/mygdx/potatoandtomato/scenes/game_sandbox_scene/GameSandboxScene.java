package com.mygdx.potatoandtomato.scenes.game_sandbox_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.Threadings;

import java.util.HashMap;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxScene extends SceneAbstract {

    Table _loadingTable;
    Label _remainingTimeLabel;

    public GameSandboxScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        _root.align(Align.top);
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LOADING_PAGE)));

        _loadingTable = new Table();
        _loadingTable.padBottom(10);

        Image loadingTableBgImage = new Image(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG));
        loadingTableBgImage.setFillParent(true);
        _loadingTable.addActor(loadingTableBgImage);

        Label.LabelStyle remainingStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR), null);
        Table remainingTable = new Table();
        remainingTable.pad(10);

        Label remainingTitleLabel = new Label(_texts.remainingTime(), remainingStyle);
        remainingTable.add(remainingTitleLabel);

        _remainingTimeLabel = new Label("1:00", remainingStyle);
        remainingTable.add(_remainingTimeLabel);

        _loadingTable.add(remainingTable).expandX().fillX();

        _root.add(_loadingTable).expandX().fillX().padLeft(20).padRight(20).padTop(20);



    }

    public void setUser(final String userId, final String name, final boolean isReady, final boolean isFailed, final Color color){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table userTable;
                Label userNameLabel;
                Label statusLabel;
                Label.LabelStyle labelNameStyle =  new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_SEMIBOLD), color);
                Label.LabelStyle labelStatusStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_SEMIBOLD),
                        isFailed ? Color.RED : isReady ? Color.GREEN : Color.WHITE);
                String status = isFailed ? _texts.failed() : isReady ? _texts.ready() : _texts.loading();

                userTable = _loadingTable.findActor(userId);
                if(userTable != null){
                    userNameLabel = userTable.findActor("userNameLabel");
                    statusLabel = userTable.findActor("statusLabel");
                }
                else{
                    userTable = new Table();
                    userTable.setName(userId);
                    userNameLabel = new Label(name, labelNameStyle);
                    userNameLabel.setName("userNameLabel");
                    statusLabel = new Label(status, labelStatusStyle);
                    statusLabel.setName("statusLabel");
                    userTable.add(userNameLabel).width(150);
                    userTable.add(statusLabel).width(100);

                    _loadingTable.row();
                    _loadingTable.add(userTable).expandX().fillX().space(10);
                }

                userNameLabel.setText(name);
                statusLabel.setText(status);
                statusLabel.setStyle(labelStatusStyle);
            }
        });
    }

    public void setRemainingTime(final int sec){
        final int minute = sec / 60;
        int seconds = sec % 60;

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _remainingTimeLabel.setText(String.valueOf(minute) + ":" + String.format("%02d", sec));
            }
        });


    }

    public void clearRoot(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _root.clear();
            }
        });
    }

}
