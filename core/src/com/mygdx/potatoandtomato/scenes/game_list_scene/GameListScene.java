package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.helpers.assets.Profile;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.helpers.controls.Mascot;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.helpers.utils.Assets;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListScene extends SceneAbstract {

    Table _gameListTable, _gameTitleTable;
    Label _titleGameLabel, _titlePlayersLabel, _titleHostLabel;
    Image _titleSeparator1, _titleSeparator2;
    HashMap<Integer, Table> _gameRowsTableMap;
    int _gameRowIndex;
    ScrollPane _gameListScrollPane;
    Table _scrollTable;
    BtnEggDownward _newGameButton, _joinGameButton;
    Table _userProfileTable;
    Mascot _userMascot;
    Label _usernameLabel;
    Button _settingsButton, _ratingButtons;
    Table _settingsTable, _ratingTable;
    Image _settingsIconImg, _ratingIconImg;

    public GameListScene(Assets assets) {
        super(assets);
        _gameRowIndex = 0;
        _gameRowsTableMap = new HashMap<>();
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.gamesList(), true, _textures, _fonts);
        _root.align(Align.top);

        //Game List Table START
        _gameListTable = new Table();
        _gameListTable.align(Align.top);
        _gameListTable.setBackground(new TextureRegionDrawable(_textures.getGameListBg()));

        _gameTitleTable = new Table();
        _gameTitleTable.align(Align.left);
        _gameTitleTable.setBackground(new TextureRegionDrawable(_textures.getGameListTitleBg()));
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = _fonts.getArialBold(11, Color.valueOf("e5f7e2"), 0, Color.BLACK, 0, Color.BLACK);
        _titleGameLabel = new Label(_texts.game(), titleLabelStyle);
        _titleHostLabel = new Label(_texts.host(), titleLabelStyle);
        _titlePlayersLabel = new Label(_texts.players(), titleLabelStyle);
        _titleSeparator1 = new Image(_textures.getGameListTitleSeparator());
        _titleSeparator2 = new Image(_textures.getGameListTitleSeparator());

        _gameTitleTable.add(_titleGameLabel).padLeft(10).padRight(10).width(110);
        _gameTitleTable.add(_titleSeparator1).width(5).height(25);
        _gameTitleTable.add(_titleHostLabel).padLeft(8).padRight(10).width(95);
        _gameTitleTable.add(_titleSeparator2).width(5).height(25);
        _gameTitleTable.add(_titlePlayersLabel).padLeft(8).padRight(10).expandX().left();

        _scrollTable = new Table();
        _gameListScrollPane = new ScrollPane(_scrollTable);
        //Game list Table END

        //Buttons START
        _newGameButton = new BtnEggDownward(_textures, _fonts);
        _newGameButton.setText(_texts.newGame());

        _joinGameButton = new BtnEggDownward(_textures, _fonts, _assets.getShaders());
        _joinGameButton.setText(_texts.joinGame());
        _joinGameButton.setEnabled(false);
        //Buttons END

        //User profile START
        _userProfileTable = new Table();
        _userProfileTable.setBackground(new TextureRegionDrawable(_textures.getBlackRoundedBg()));

        Profile profile = _assets.getProfile();
        _userMascot = new Mascot(profile.getMascotEnum(), _textures);
        _userMascot.resizeTo(50, 45);
        _usernameLabel = new Label(profile.getDisplayName(), new Label.LabelStyle(_fonts.getArialBold(17, Color.WHITE,
                                                                        0, Color.BLACK, 0, Color.BLACK), Color.WHITE));


        _settingsTable = new Table();
        _settingsTable.setBackground(new TextureRegionDrawable(_textures.getBtnWhiteRound()));
        _settingsIconImg = new Image(_textures.getSettingsIcon());
        _settingsTable.add(_settingsIconImg).expand().fill().pad(5);
        _settingsButton = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        _settingsButton.setFillParent(true);
        _settingsTable.addActor(_settingsButton);

        _ratingTable = new Table();
        _ratingTable.setBackground(new TextureRegionDrawable(_textures.getBtnWhiteRound()));
        _ratingIconImg = new Image(_textures.getRatingIcon());
        _ratingTable.add(_ratingIconImg).expand().fill().pad(5);
        _ratingButtons = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        _ratingButtons.setFillParent(true);
        _ratingTable.addActor(_ratingButtons);

        _userProfileTable.add(_userMascot).padLeft(10).padRight(10);
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

    public Actor addNewGameRow(){

        Table gameRowTable = new Table();
        Label.LabelStyle contentLabelStyle = new Label.LabelStyle();
        contentLabelStyle.font = _fonts.getArial(12, Color.WHITE, 0, Color.BLACK, 0, Color.BLACK);
        Label gameNameLabel = new Label("Covered Chess", contentLabelStyle);
        gameNameLabel.setWrap(true);
        Label hostNameLabel = new Label("soulwraith", contentLabelStyle);
        hostNameLabel.setWrap(true);
        Label playersCountLable = new Label("1 / 2", contentLabelStyle);
        playersCountLable.setWrap(true);

        Button dummyButton = new Button(new TextureRegionDrawable(_textures.getEmpty()));
        dummyButton.setFillParent(true);


        gameRowTable.add(gameNameLabel).width(115).padLeft(10).padRight(10);
        gameRowTable.add(hostNameLabel).width(100).padLeft(8).padRight(10);
        gameRowTable.add(playersCountLable).expandX().left().padLeft(8).padRight(10);
        gameRowTable.padTop(5).padBottom(5);
        gameRowTable.addActor(dummyButton);
        _scrollTable.add(gameRowTable).expandX().fillX();
        _scrollTable.row();

        dummyButton.setName(String.valueOf(_gameRowIndex));
        _gameRowsTableMap.put(_gameRowIndex, gameRowTable);
        _gameRowIndex++;

        return dummyButton;
    }

    public void gameRowHighlight(String tableName){
        for (Map.Entry<Integer, Table> entry : _gameRowsTableMap.entrySet()) {
            Integer key = entry.getKey();
            Table gameRowTable = entry.getValue();

            if(String.valueOf(key).equals(tableName)){
                gameRowTable.background(new TextureRegionDrawable(_textures.getGameListHighlight()));
            }
            else{
                gameRowTable.background(new TextureRegionDrawable(_textures.getEmpty()));
            }
        }
        _joinGameButton.setEnabled(true);
    }

}
