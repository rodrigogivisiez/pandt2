package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.controls.*;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.mygdx.potatoandtomato.models.Services;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomScene extends SceneAbstract {

    BtnEggDownward _startButton, _inviteButton;
    Array<Table> _teamTables;
    Table _teamsRoot, _detailsRoot;
    HashMap<String, Table> _playerMaps;
    Array<Table> _playersTable;
    Room _room;

    public Array<Table> getTeamTables() {
        return _teamTables;
    }

    public HashMap<String, Table> getPlayersMaps() {
        return _playerMaps;
    }

    public Array<Table> getPlayersTable() {
        return _playersTable;
    }

    public BtnEggDownward getStartButton() {
        return _startButton;
    }

    public BtnEggDownward getInviteButton() {
        return _inviteButton;
    }

    public Table getTeamsRoot() {
        return _teamsRoot;
    }

    public RoomScene(Services services, PTScreen screen, Room room) {
        super(services, screen);
        this._room = room;
    }

    @Override
    public void populateRoot() {

        new TopBar(_root, _texts.roomTitle(), false, _assets, _screen);
        _root.align(Align.top);

        _teamTables = new Array();
        _playerMaps = new HashMap();
        _playersTable = new Array<Table>();

        Table buttonTable = new Table();

        _startButton = new BtnEggDownward(_assets, _services.getSoundsWrapper(), _services.getShaders());
        _startButton.setEnabled(false);
        _startButton.setText(_texts.waitingHost());

        _inviteButton = new BtnEggDownward(_assets, _services.getSoundsWrapper());
        _inviteButton.setText(_texts.invite());

        buttonTable.add(_startButton).padRight(10);
        buttonTable.add(_inviteButton);

        _teamsRoot = new Table();

        _detailsRoot = new Table();
        _detailsRoot.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX_ROUNDED)));
        _detailsRoot.align(Align.top);
        _detailsRoot.row();

        Table scrollableTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollableTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollableTable.add(_teamsRoot).expandX().fillX().padTop(10);
        scrollableTable.row();
        scrollableTable.add(_detailsRoot).expandX().fillX().padLeft(10).padRight(10).padBottom(200);

        _root.add(buttonTable).height(110).expandX();
        _root.row();
        _root.add(scrollPane).expandX().fillX();
    }

    public void setStartButtonText(String text){
        _startButton.setText(text);
    }

    public void populateGameDetails(Game game){
        WebImage gameImg = new WebImage(game.getIconUrl(), _assets, _services.getBroadcaster());

        Image separatorImage = new Image(_assets.getTextures().get(Textures.Name.WHITE_VERTICAL_LINE));

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = _assets.getFonts().get(Fonts.FontName.HELVETICA, Fonts.FontSize.M, Fonts.FontColor.DARK_BROWN, Fonts.FontStyle.BOLD);
        Label.LabelStyle smallStyle = new Label.LabelStyle();
        smallStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD, Fonts.FontSize.S, Fonts.FontColor.DARK_BROWN, Fonts.FontStyle.ITALIC);
        Label.LabelStyle contentStyle = new Label.LabelStyle();
        contentStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD, Fonts.FontSize.S, Fonts.FontColor.DARK_BROWN);

        Table _subRoot = new Table();
        _subRoot.align(Align.topLeft);

        Label titleLabel = new Label(game.getName(), titleStyle);
        Label playersLabel = new Label(String.format(_texts.xPlayers(), game.getMinPlayers(), game.getMaxPlayers()), smallStyle);
        Label versionLabel = new Label(String.format(_texts.version(), game.getVersion()), smallStyle);
        Label descriptionLabel = new Label(game.getDescription(), contentStyle);
        descriptionLabel.setWrap(true);

        Table descriptionTable = new Table();
        descriptionTable.add(descriptionLabel).expand().fill();

        _subRoot.add(titleLabel).expandX().fillX();
        _subRoot.row();
        _subRoot.add(playersLabel).left();
        _subRoot.row();
        _subRoot.add(versionLabel).left();
        _subRoot.row();
        _subRoot.add(descriptionTable).left().expandX().fillX().padTop(5);

        _detailsRoot.pad(10);
        _detailsRoot.add(gameImg).size(120).top();
        _detailsRoot.add(separatorImage).padLeft(5).padRight(10).expandY().fillY();
        _detailsRoot.add(_subRoot).expandX().fillX().top();
    }

    public void populateTeamTables(int totalTeams, int teamMaxPlayers, HashMap<String, RoomUser> roomUsers, boolean isHost){
        _teamsRoot.clear();
        int accIndex = 0;

        for(int i=0; i<totalTeams; i++){

            Table teamTable = new Table();
            new DummyButton(teamTable, _assets);
            if(totalTeams > 1){
                teamTable.setBackground(teamMaxPlayers <= 2 ? new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_FAT)) :
                        new NinePatchDrawable(_assets.getPatches().get(Patches.Name.WOOD_BG_FAT_PATCH)));
            }
            else{
                teamTable.setBackground(teamMaxPlayers <= 2 ? new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_FAT)) :
                        new NinePatchDrawable(_assets.getPatches().get(Patches.Name.WOOD_BG_SMALL_PATCH)));
            }
            teamTable.align(Align.top);
            teamTable.padBottom(20);
            teamTable.add(getWoodBoardTitleTable(_texts.team() + " " + (i+1))).padTop(-7).colspan(totalTeams == 1 ? 2 : 1);
            teamTable.row();


            for(int q = 0; q< teamMaxPlayers; q++){

                RoomUser occupiedUser = null;
                for(RoomUser roomUser : roomUsers.values()){
                    if(accIndex == roomUser.getSlotIndex()){
                        occupiedUser = roomUser;
                    }
                }
                Table playerTable;
                if(occupiedUser != null){
                    playerTable = getPlayerTable(occupiedUser.getProfile().getDisplayName(15),
                                                occupiedUser.getProfile().getUserId(),
                                                occupiedUser.getReady(), isHost);
                }
                else{
                    playerTable = getPlayerTable(null, null, false, false);
                }

                if(totalTeams == 1){
                    if((q % 2 == 0 && q != 0) || (teamMaxPlayers <= 2)) teamTable.row();
                    teamTable.add(playerTable).width(150).space(10);
                }
                else{
                    teamTable.add(playerTable).expandX().fillX().padLeft(10).padRight(10).padTop(5);
                    teamTable.row();
                }

                _playersTable.add(playerTable);
                accIndex++;
            }

            _teamTables.add(teamTable);
        }

        _teamsRoot.padLeft(5).padRight(5);
        int i = 1;
        for(Table t : _teamTables){

            if(totalTeams > 1){
                _teamsRoot.add(t).expandX().fillX().center().uniformX().space(3).padBottom(10);
            }
            else{
                _teamsRoot.add(t).padBottom(10);
            }


            if(i % 2 == 0 && i!=0) {
                _teamsRoot.row();
            }
            i++;
        }



    }

    public void updateRoom(Room room){
        if(room.getHost().equals(_services.getProfile())){
            _startButton.setEnabled(true);
        }
        for(Table t : _teamTables) t.remove();
        _teamTables.clear();
        _playersTable.clear();
        populateTeamTables(Integer.valueOf(room.getGame().getTeamCount()),
                Integer.valueOf(room.getGame().getTeamMaxPlayers()), room.getRoomUsers(), room.getHost().equals(_services.getProfile()));
    }

    public void updateDownloadPercentage(String userId, int percent){
        if(_playerMaps.containsKey(userId)){
            Table playerTable = _playerMaps.get(userId);
            Image downloadImage = playerTable.findActor("download");
            if(downloadImage != null){
                downloadImage.addAction(forever(sequence(moveBy(0, -2), moveBy(0, 2, 0.9f))));
                downloadImage.setName("downloading");
            }
            else{
                downloadImage = playerTable.findActor("downloading");
            }

            Label progressLabel = playerTable.findActor("progress");
            progressLabel.setText(String.valueOf(percent));
            progressLabel.invalidate();
            if(percent >= 100){
                progressLabel.setVisible(false);
                downloadImage.setVisible(false);
                downloadImage.clearActions();
                downloadImage.setName("download");
            } else{
                progressLabel.setVisible(true);
                downloadImage.setVisible(true);
            }
        }
    }

    private Table getWoodBoardTitleTable( String title){
        Table detailsTitleTable = new Table();
        detailsTitleTable.padTop(7).padBottom(7).padLeft(20).padRight(20);
        detailsTitleTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_TITLE)));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _assets.getFonts().get(Fonts.FontName.PIZZA, Fonts.FontColor.WHITE, Fonts.FontBorderColor.BLACK);

        Label labelTitle = new Label(title, labelStyle);
        labelTitle.setAlignment(Align.center);
        detailsTitleTable.add(labelTitle).expand().fill();
        return detailsTitleTable;
    }

    private Table getPlayerTable(String name, String userId, boolean isReady, boolean isHost){

        if(_playerMaps.containsKey(userId) && userId != null){
            Table playerTable = _playerMaps.get(userId);
            swapIsReadyIcon((Table) playerTable.findActor("iconTable"), userId, isReady);
            return _playerMaps.get(userId);
        }

        BitmapFont font = _assets.getFonts().get(Fonts.FontName.HELVETICA, Fonts.FontSize.XS);
        if(name == null){
            name = _texts.open();
            font = _assets.getFonts().get(Fonts.FontName.HELVETICA, Fonts.FontSize.XS, Fonts.FontColor.GRAY);
        }

        Table playerTable = new Table();
        playerTable.padTop(5).padBottom(5).padLeft(7).padRight(12);
        playerTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.WHITE_ROUNDED_BG)));

        Table iconTable = new Table();
        iconTable.setName("iconTable");
        Animator loadingAnimator = new Animator(0.2f, _assets.getAnimations().getLoadingAnimation());
        loadingAnimator.setName("loadingAnimator");
        loadingAnimator.setSize(16, 16);
        Image unknownImage = new Image(_assets.getTextures().get(Textures.Name.UNKNOWN_ICON));
        unknownImage.setName("unknownImage");
        unknownImage.setSize(5, 8);
        unknownImage.setPosition(5, 5);
        Image bulletIcon = new Image(_assets.getTextures().get(Textures.Name.BULLET_ICON));
        bulletIcon.setName("bulletIcon");
        bulletIcon.setSize(4, 4);
        bulletIcon.setPosition(6, 6);
        iconTable.addActor(loadingAnimator);
        iconTable.addActor(unknownImage);
        iconTable.addActor(bulletIcon);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        Label nameLabel = new Label(name, labelStyle);

        Label.LabelStyle progressLabelStyle = new Label.LabelStyle();
        progressLabelStyle.font = _assets.getFonts().get(Fonts.FontName.HELVETICA, Fonts.FontSize.XS, Fonts.FontColor.GREEN);
        Label progressLabel = new Label("22", progressLabelStyle);
        progressLabel.setName("progress");
        progressLabel.setVisible(false);

        Image downloadImage = new Image(_assets.getTextures().get(Textures.Name.DOWNLOAD_ICON));
        downloadImage.setName("download");
        downloadImage.setVisible(false);

        Image kickImage = new Image(_assets.getTextures().get(Textures.Name.KICK_ICON));
        kickImage.setName("kickImage");

        playerTable.add(iconTable).size(16, 16).padRight(3);
        playerTable.add(nameLabel).expandX().fillX().padLeft(3).padBottom(2);
        playerTable.add(downloadImage).padRight(2);
        playerTable.add(progressLabel);
        if(isHost && !userId.equals(_services.getProfile().getUserId())) playerTable.add(kickImage).padLeft(5);

        playerTable.setName("nokickattached," + ((userId != null) ? "disableclick" : ""));
        new DummyButton(playerTable, _assets);

        if(userId != null) _playerMaps.put(userId, playerTable);

        swapIsReadyIcon(iconTable, userId, isReady);

        return playerTable;
    }

    private void swapIsReadyIcon(Table table, String userId, boolean isReady){
        Actor loadingAnimator = table.findActor("loadingAnimator");
        Actor unknownImage = table.findActor("unknownImage");
        Actor bulletIcon = table.findActor("bulletIcon");
        loadingAnimator.setVisible(false);
        unknownImage.setVisible(false);
        bulletIcon.setVisible(false);

        if(userId == null){
            unknownImage.setVisible(true);
        }
        else{
            if(!isReady){
                loadingAnimator.setVisible(true);
            }
            else{
                bulletIcon.setVisible(true);
            }
        }


    }

}
