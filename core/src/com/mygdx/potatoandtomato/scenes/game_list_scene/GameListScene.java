package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.helpers.controls.BtnEggDownward;
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
    HashMap<String, Table> _gameRowsTableMap;
    ScrollPane _gameListScrollPane;
    Table _scrollTable;
    BtnEggDownward _newGameButton, _joinGameButton, _continueGameButton;
    Table _userProfileTable;
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

    public BtnEggDownward getContinueGameButton() {
        return _continueGameButton;
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
        _gameListTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.GAMELIST_BG)));

        _gameTitleTable = new Table();
        _gameTitleTable.align(Align.left);
        _gameTitleTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GAMELIST_TITLE_BG)));
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD, Fonts.FontColor.WHITE);
        _titleGameLabel = new Label(_texts.game(), titleLabelStyle);
        _titleHostLabel = new Label(_texts.host(), titleLabelStyle);
        _titlePlayersLabel = new Label(_texts.players(), titleLabelStyle);

        _gameTitleTable.add(_titleGameLabel).padLeft(20).padRight(10).width(100);
        _gameTitleTable.add(_titleHostLabel).padLeft(8).padRight(10).width(95);
        _gameTitleTable.add(_titlePlayersLabel).padLeft(8).padRight(20).expandX().left();

        _scrollTable = new Table();
        _scrollTable.align(Align.top);
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SCROLLBAR_VERTICAL_HANDLE));
        _gameListScrollPane = new ScrollPane(_scrollTable, scrollPaneStyle);
        _gameListScrollPane.setFadeScrollBars(false);
        //Game list Table END

        //Buttons START
        _newGameButton = new BtnEggDownward(_assets, _services.getSoundsWrapper());
        _newGameButton.setText(_texts.newGame());

        _joinGameButton = new BtnEggDownward(_assets, _services.getSoundsWrapper(), _services.getShaders());
        _joinGameButton.setText(_texts.joinGame());
        _joinGameButton.setEnabled(false);

        _continueGameButton = new BtnEggDownward(_assets, _services.getSoundsWrapper(), _services.getShaders());
        _continueGameButton.setText(_texts.continueLastGame());
        _continueGameButton.setEnabled(false);

        Table buttonsTable = new Table();
        buttonsTable.setSize(Positions.getWidth(), 230);
        buttonsTable.setPosition(0, 0);
        buttonsTable.add(_newGameButton).space(10).uniformX();
        buttonsTable.add(_continueGameButton).space(10).uniformX();
        buttonsTable.add(_joinGameButton).space(10).uniformX();
        //Buttons END

        //User profile START
        _userProfileTable = new Table();
        _userProfileTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.TRANS_BLACK_ROUNDED_BG)));

        Profile profile = _services.getProfile();
        _usernameLabel = new Label(profile.getDisplayName(15),
                            new Label.LabelStyle(_assets.getFonts().get(Fonts.FontName.MYRIAD,
                                    Fonts.FontSize.L, Fonts.FontColor.WHITE, Fonts.FontStyle.BOLD), null));


        _settingsTable = new Table();
        _settingsTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG)));
        _settingsIconImg = new Image(_assets.getTextures().get(Textures.Name.SETTINGS_ICON));
        _settingsTable.add(_settingsIconImg).expand().fill().pad(5);
        _settingsButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        _settingsButton.setFillParent(true);
        _settingsTable.addActor(_settingsButton);

        _ratingTable = new Table();
        _ratingTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG)));
        _ratingIconImg = new Image(_assets.getTextures().get(Textures.Name.RATE_ICON));
        _ratingTable.add(_ratingIconImg).expand().fill().pad(5);
        _ratingButtons = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        _ratingButtons.setFillParent(true);
        _ratingTable.addActor(_ratingButtons);

        _userProfileTable.add(_usernameLabel).expand().fill().padLeft(10).padRight(10);
        _userProfileTable.add(_settingsTable).size(40).padRight(10);
        _userProfileTable.add(_ratingTable).size(40).padRight(10);
        //User profile END

        _gameListTable.add(_gameTitleTable).expandX().fillX().height(45).padLeft(20).padRight(20);
        _gameListTable.row();
        _gameListTable.add(_gameListScrollPane).expand().fill().padBottom(45).padLeft(20).padRight(20);

        _root.add(_gameListTable).padTop(10).expandX().fillX().height(440).colspan(3);
        _root.row();
        _root.addActor(buttonsTable);
        _root.add(_userProfileTable).expand().fill().padTop(60).padBottom(10).padLeft(30).padRight(30);
    }

    public Actor addNewRoomRow(Room room, boolean isInvited){

        Table gameRowTable = new Table();
        Label.LabelStyle contentLabelStyle = new Label.LabelStyle();
        contentLabelStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD, Fonts.FontSize.S, Fonts.FontColor.WHITE, Fonts.FontStyle.BOLD);

        Table gameNameInvitationTable = new Table();
        gameNameInvitationTable.align(Align.topLeft);
        gameNameInvitationTable.setName("gameNameInvitationTable");
        Image invitedImage = new Image(_assets.getTextures().get(Textures.Name.INVITED_ICON));
        gameNameInvitationTable.add(invitedImage).size(isInvited ? 12 : 0, 10).padRight(3);
        Label gameNameLabel = new Label(room.getGame().getName(), contentLabelStyle);
        gameNameLabel.setWrap(true);
        gameNameInvitationTable.add(gameNameLabel).expand().fill();

        Label hostNameLabel = new Label(room.getHost().getDisplayName(15), contentLabelStyle);
        hostNameLabel.setWrap(true);
        Label playersCountLabel = new Label(String.format("%s / %s", room.getRoomUsersCount(), room.getGame().getMaxPlayers()), contentLabelStyle);
        playersCountLabel.setName("playerCount");
        playersCountLabel.setWrap(true);


        Button dummyButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        dummyButton.setFillParent(true);

        gameRowTable.add(gameNameInvitationTable).width(100).padLeft(18).padRight(10);
        gameRowTable.add(hostNameLabel).width(95).padLeft(8).padRight(10);
        gameRowTable.add(playersCountLabel).expandX().left().padLeft(8).padRight(20);
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
                gameRowTable.background(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GAMELIST_HIGHLIGHT)));
            }
            else{
                gameRowTable.background(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
            }
        }

        if(found) _joinGameButton.setEnabled(true);
        else _joinGameButton.setEnabled(false);
    }

    public boolean alreadyContainsRoom(Room room){
        return _gameRowsTableMap.containsKey(room.getId());
    }

    public Actor updatedRoom(Room room){
        boolean isInvited = (room.getInvitedUserByUserId(_services.getProfile().getUserId()) != null);
        if(!_gameRowsTableMap.containsKey(room.getId())){
            return addNewRoomRow(room, isInvited);
        }
        else{
            if(isInvited){
                Table table = _gameRowsTableMap.get(room.getId()).findActor("gameNameInvitationTable");
                table.getCells().get(0).width(12);
                table.invalidate();
            }

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
