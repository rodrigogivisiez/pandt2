package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.helpers.controls.Mascot;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListScene extends SceneAbstract {

    Table _gameListTable, _gameTitleTable;
    Label _titleGameLabel, _titlePlayersLabel, _titleHostLabel;
    Image _titleSeparator1, _titleSeparator2;
    HashMap<String, Table> _gameRowsTableMap;
    ScrollPane _gameListScrollPane;
    Table _scrollTable;
    BtnEggDownward _newGameButton, _joinGameButton;
    Table _userProfileTable;
    Mascot _userMascot;
    Label _usernameLabel;
    Button _settingsButton, _ratingButtons;
    Table _settingsTable, _ratingTable;
    Image _settingsIconImg, _ratingIconImg;

    public int getGameRowsCount() {
        return _gameRowsTableMap.size();
    }

    public GameListScene(Services services, PTScreen screen) {
        super(services, screen);
        _gameRowsTableMap = new HashMap();
    }

    public BtnEggDownward getNewGameButton() {
        return _newGameButton;
    }

    public BtnEggDownward getJoinGameButton() {
        return _joinGameButton;
    }

    public Button getSettingsButton() {
        return _settingsButton;
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.gamesList(), true, _assets, _screen);
        _root.align(Align.top);

        //Game List Table START
        _gameListTable = new Table();
        _gameListTable.align(Align.top);
        _gameListTable.setBackground(new TextureRegionDrawable(_assets.getGameListBg()));

        _gameTitleTable = new Table();
        _gameTitleTable.align(Align.left);
        _gameTitleTable.setBackground(new TextureRegionDrawable(_assets.getGameListTitleBg()));
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = _assets.getWhiteNormal2();
        _titleGameLabel = new Label(_texts.game(), titleLabelStyle);
        _titleHostLabel = new Label(_texts.host(), titleLabelStyle);
        _titlePlayersLabel = new Label(_texts.players(), titleLabelStyle);
        _titleSeparator1 = new Image(_assets.getGameListTitleSeparator());
        _titleSeparator2 = new Image(_assets.getGameListTitleSeparator());

        _gameTitleTable.add(_titleGameLabel).padLeft(10).padRight(10).width(110);
        _gameTitleTable.add(_titleSeparator1).width(5).height(25);
        _gameTitleTable.add(_titleHostLabel).padLeft(8).padRight(10).width(95);
        _gameTitleTable.add(_titleSeparator2).width(5).height(25);
        _gameTitleTable.add(_titlePlayersLabel).padLeft(8).padRight(10).expandX().left();

        _scrollTable = new Table();
        _scrollTable.align(Align.top);
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new NinePatchDrawable(_assets.getScrollVerticalHandle());
        _gameListScrollPane = new ScrollPane(_scrollTable, scrollPaneStyle);
        _gameListScrollPane.setFadeScrollBars(false);
        //Game list Table END

        //Buttons START
        _newGameButton = new BtnEggDownward(_assets);
        _newGameButton.setText(_texts.newGame());

        _joinGameButton = new BtnEggDownward(_assets, _services.getShaders());
        _joinGameButton.setText(_texts.joinGame());
        _joinGameButton.setEnabled(false);
        //Buttons END

        //User profile START
        _userProfileTable = new Table();
        _userProfileTable.setBackground(new NinePatchDrawable(_assets.getBlackRoundedBg()));

        Profile profile = _services.getProfile();
        _userMascot = new Mascot(profile.getMascotEnum(), _assets);
        _userMascot.resizeTo(50, 45);
        _usernameLabel = new Label(profile.getDisplayName(), new Label.LabelStyle(_assets.getWhiteNormal3GrayS(), Color.WHITE));


        _settingsTable = new Table();
        _settingsTable.setBackground(new TextureRegionDrawable(_assets.getBtnWhiteRound()));
        _settingsIconImg = new Image(_assets.getSettingsIcon());
        _settingsTable.add(_settingsIconImg).expand().fill().pad(5);
        _settingsButton = new Button(new TextureRegionDrawable(_assets.getEmpty()));
        _settingsButton.setFillParent(true);
        _settingsTable.addActor(_settingsButton);

        _ratingTable = new Table();
        _ratingTable.setBackground(new TextureRegionDrawable(_assets.getBtnWhiteRound()));
        _ratingIconImg = new Image(_assets.getRatingIcon());
        _ratingTable.add(_ratingIconImg).expand().fill().pad(5);
        _ratingButtons = new Button(new TextureRegionDrawable(_assets.getEmpty()));
        _ratingButtons.setFillParent(true);
        _ratingTable.addActor(_ratingButtons);

        _userProfileTable.add(_userMascot).size(_userMascot.getPrefWidth(), _userMascot.getPrefHeight()).padLeft(10).padRight(5);
        _userProfileTable.add(_usernameLabel).expand().fill().padLeft(10).padRight(10);
        _userProfileTable.add(_settingsTable).size(40).padRight(10);
        _userProfileTable.add(_ratingTable).size(40).padRight(10);
        //User profile END

        _gameListTable.add(_gameTitleTable).expandX().fillX().height(30);
        _gameListTable.row();
        _gameListTable.add(_gameListScrollPane).expand().fill().padBottom(15);

        _root.add(_gameListTable).padLeft(15).padRight(15).padTop(15).expandX().fillX().height(400).colspan(2);
        _root.row();
        _root.add(_newGameButton).padTop(-15).uniformX();
        _root.add(_joinGameButton).padTop(-15).uniformX();
        _root.row();
        _root.add(_userProfileTable).colspan(2).expand().fill().padTop(10).padLeft(30).padRight(30);
    }

    public Actor addNewRoomRow(Room room){

        Table gameRowTable = new Table();
        Label.LabelStyle contentLabelStyle = new Label.LabelStyle();
        contentLabelStyle.font = _assets.getWhiteNormal2GrayS();
        Label gameNameLabel = new Label(room.getGame().getName(), contentLabelStyle);
        gameNameLabel.setWrap(true);
        Label hostNameLabel = new Label(room.getHost().getDisplayName(), contentLabelStyle);
        hostNameLabel.setWrap(true);
        Label playersCountLabel = new Label(String.format("%s / %s", room.getRoomUsersCount(), room.getGame().getMaxPlayers()), contentLabelStyle);
        playersCountLabel.setName("playerCount");
        playersCountLabel.setWrap(true);

        Button dummyButton = new Button(new TextureRegionDrawable(_assets.getEmpty()));
        dummyButton.setFillParent(true);


        gameRowTable.add(gameNameLabel).width(115).padLeft(10).padRight(10);
        gameRowTable.add(hostNameLabel).width(100).padLeft(8).padRight(10);
        gameRowTable.add(playersCountLabel).expandX().left().padLeft(8).padRight(10);
        gameRowTable.padTop(5).padBottom(5);
        gameRowTable.addActor(dummyButton);
        _scrollTable.add(gameRowTable).expandX().fillX();
        _scrollTable.row();

        dummyButton.setName(String.valueOf(room.getId()));
        _gameRowsTableMap.put(room.getId(), gameRowTable);

        return dummyButton;
    }

    public void gameRowHighlight(String tableName){
        boolean found = false;
        for (Map.Entry<String, Table> entry : _gameRowsTableMap.entrySet()) {
            String key = entry.getKey();
            Table gameRowTable = entry.getValue();

            if(String.valueOf(key).equals(tableName)){
                found = true;
                gameRowTable.background(new TextureRegionDrawable(_assets.getGameListHighlight()));
            }
            else{
                gameRowTable.background(new TextureRegionDrawable(_assets.getEmpty()));
            }
        }

        if(found) _joinGameButton.setEnabled(true);
        else _joinGameButton.setEnabled(false);
    }

    public Actor updatedRoom(Room room){
        if(!_gameRowsTableMap.containsKey(room.getId())){
            return addNewRoomRow(room);
        }
        else{
            Label playerCountLabel = _gameRowsTableMap.get(room.getId()).findActor("playerCount");
            playerCountLabel.setText(String.format("%s / %s", room.getRoomUsersCount(), room.getGame().getMaxPlayers()));
            playerCountLabel.invalidate();
            return null;
        }
    }

    public void removeRoom(Room room){
        if(_gameRowsTableMap.containsKey(room.getId())){
            _gameRowsTableMap.get(room.getId()).remove();
            _gameRowsTableMap.remove(room.getId());
        }
    }

    public void setUsername(String username){
        _usernameLabel.setText(username);
    }
}
