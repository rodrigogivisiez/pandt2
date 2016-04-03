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

import java.util.HashMap;

/**
 * Created by SiongLeng on 26/12/2015.
 */
public class GameSandboxScene extends SceneAbstract {

    private HashMap<String, Table> _userTableMap;
    Table _loadingTable;
    Label _remainingTimeLabel;

    public GameSandboxScene(Services services, PTScreen screen) {
        super(services, screen);
        _userTableMap = new HashMap<String, Table>();
    }

    @Override
    public void populateRoot() {
        _root.align(Align.top);
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LOADING_PAGE)));

        _loadingTable = new Table();
        _loadingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));
        _loadingTable.padBottom(10);

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

    public void setUser(String userId, String name, boolean isReady, boolean isFailed, Color color){
        Table userTable;
        Label userNameLabel;
        Label statusLabel;
        Label.LabelStyle labelNameStyle =  new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_SEMIBOLD), color);
        Label.LabelStyle labelStatusStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_SEMIBOLD),
                                    isFailed ? Color.RED : isReady ? Color.GREEN : Color.WHITE);
        String status = isFailed ? _texts.failed() : isReady ? _texts.ready() : _texts.loading();

        if(_userTableMap.containsKey(userId)){
            userTable = _userTableMap.get(userId);
            userNameLabel = userTable.findActor("userNameLabel");
            statusLabel = userTable.findActor("statusLabel");
        }
        else{
            userTable = new Table();
            userNameLabel = new Label(name, labelNameStyle);
            userNameLabel.setName("userNameLabel");
            statusLabel = new Label(status, labelStatusStyle);
            statusLabel.setName("statusLabel");
            userTable.add(userNameLabel).width(150);
            userTable.add(statusLabel).width(100);

            _loadingTable.row();
            _loadingTable.add(userTable).expandX().fillX().space(10);

            _userTableMap.put(userId, userTable);
        }

        userNameLabel.setText(name);
        statusLabel.setText(status);
        statusLabel.setStyle(labelStatusStyle);
    }

    public void setRemainingTime(int sec){
        int minute = sec / 60;
        int seconds = sec % 60;

        _remainingTimeLabel.setText(String.valueOf(minute) + ":" + String.format("%02d", sec));
    }

    public void clearRoot(){
        _root.clear();
    }

}
