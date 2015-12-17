package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.MascotEnum;
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
    Confirm _hostLeftConfirm, _errorConfirm, _messageConfirm;

    public Array<Table> getTeamTables() {
        return _teamTables;
    }

    public Confirm getHostLeftConfirm() {
        return _hostLeftConfirm;
    }

    public Confirm getErrorConfirm() {
        return _errorConfirm;
    }

    public RoomScene(Services services) {
        super(services);
    }

    @Override
    public void populateRoot() {

        new TopBar(_root, _texts.roomTitle(), false, _textures, _fonts);
        _root.align(Align.top);

        _hostLeftConfirm = new Confirm(_root, _textures, _fonts, _texts.hostLeft(), Confirm.Type.YES);
        _errorConfirm = new Confirm(_root, _textures, _fonts, _texts.roomError(), Confirm.Type.YES);
        _messageConfirm = new Confirm(_root, _textures, _fonts, "", Confirm.Type.YES);

        _teamTables = new Array<>();
        _playerMaps = new HashMap<>();

        Table buttonTable = new Table();

        _startButton = new BtnEggDownward(_textures, _fonts, _services.getShaders());
        _startButton.setEnabled(false);
        _startButton.setText(_texts.startGame());

        _inviteButton = new BtnEggDownward(_textures, _fonts);
        _inviteButton.setText(_texts.invite());

        buttonTable.add(_startButton).padRight(10);
        buttonTable.add(_inviteButton);

        _teamsRoot = new Table();

        _detailsRoot = new Table();
        _detailsRoot.setBackground(new TextureRegionDrawable(_textures.getWoodBgNormal()));
        _detailsRoot.align(Align.top);
        _detailsRoot.add(getWoodBoardTitleTable(22, _texts.details())).width(170).height(50).padTop(-10).padBottom(20).colspan(2);
        _detailsRoot.row();

        Table scrollableTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollableTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollableTable.add(_teamsRoot).expandX().fillX().padTop(10);
        scrollableTable.row();
        scrollableTable.add(_detailsRoot).expandX().fillX().padTop(20).padLeft(10).padRight(10).padBottom(200);

        _root.add(buttonTable).height(100).right().expandX().padTop(-10);
        _root.row();
        _root.add(scrollPane).expandX().fillX().padTop(10);
    }

    public void populateGameDetails(Game game){
        WebImage gameImg = new WebImage(game.getIconUrl(), _textures, _services.getDownloader());

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = _fonts.getPizzaFont(18, Color.WHITE, 1, Color.BLACK, 1, Color.GRAY);
        Label.LabelStyle smallStyle = new Label.LabelStyle();
        smallStyle.font = _fonts.getArial(13, Color.WHITE, 1, Color.GRAY, 1, Color.GRAY);
        Label.LabelStyle contentStyle = new Label.LabelStyle();
        contentStyle.font = _fonts.getArial(12, Color.WHITE, 0, Color.BROWN, 0, Color.GRAY);

        Table _subRoot = new Table();
        _subRoot.align(Align.topLeft);

        Label titleLabel = new Label(game.getName(), titleStyle);
        Label playersLabel = new Label(String.format("From %s - %s players", game.getMinPlayers(), game.getMaxPlayers()), smallStyle);
        Label versionLabel = new Label(String.format("Version %s", game.getVersion()), smallStyle);
        Label descriptionLabel = new Label(game.getDescription(), contentStyle);
        descriptionLabel.setWrap(true);

        Table descriptionTable = new Table();
        descriptionTable.add(descriptionLabel).expand().fill();
        ScrollPane scrollPane = new ScrollPane(descriptionTable);
        scrollPane.setScrollingDisabled(true, false);

        _subRoot.add(titleLabel).expandX().fillX();
        _subRoot.row();
        _subRoot.add(playersLabel).left();
        _subRoot.row();
        _subRoot.add(versionLabel).left();
        _subRoot.row();
        _subRoot.add(scrollPane).left().expandX().fillX().height(100).padTop(5);

        _detailsRoot.add(gameImg).size(120).padLeft(20).padRight(10).top();
        _detailsRoot.add(_subRoot).expandX().fillX().top().padRight(20).padBottom(20);
    }

    public void populateTeamTables(int totalTeams, int teamMaxPlayers, HashMap<String, RoomUser> roomUsers){
        _teamsRoot.clear();
        int accIndex = 0;

        for(int i=0; i<totalTeams; i++){

            Table teamTable = new Table();
            new DummyButton(teamTable, _textures);
            teamTable.setBackground(new TextureRegionDrawable(_textures.getWoodBgSmall()));
            teamTable.align(Align.top);
            teamTable.padBottom(20);
            teamTable.add(getWoodBoardTitleTable(15, _texts.team() + " " + (i+1))).padTop(-7);
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
                    playerTable = getPlayerTable(occupiedUser.getProfile().getMascotEnum(),
                                                occupiedUser.getProfile().getDisplayName(), occupiedUser.getProfile().getUserId());
                }
                else{
                    playerTable = getPlayerTable(MascotEnum.UNKNOWN, null, null);
                }

                teamTable.add(playerTable).expandX().fillX().padLeft(10).padRight(10).padTop(10);
                teamTable.row();

                accIndex++;
            }


            _teamTables.add(teamTable);
        }

        _teamsRoot.padLeft(10).padRight(10);
        int i = 1;
        for(Table t : _teamTables){
            _teamsRoot.add(t).expandX().fillX().center().uniformX().space(0);
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
        populateTeamTables(Integer.valueOf(room.getGame().getTeamCount()),
                                Integer.valueOf(room.getGame().getTeamMaxPlayers()), room.getRoomUsers());
    }

    public void hostLeft(){
        _hostLeftConfirm.show();
    }

    public void showError(){
        _errorConfirm.show();
    }

    public void showMessage(String msg){
        _messageConfirm.show(msg);
    }

    public void updateDownloadPercentage(String userId, int percent){
        if(_playerMaps.containsKey(userId)){
            Table playerTable = _playerMaps.get(userId);
            Image downloadImage = playerTable.findActor("download");
            Label progressLabel = playerTable.findActor("progress");
            progressLabel.setText(String.valueOf(percent));
            progressLabel.invalidate();
            if(percent >= 100){
                progressLabel.setVisible(false);
                downloadImage.setVisible(false);
            }
            else{
                progressLabel.setVisible(true);
                downloadImage.setVisible(true);
            }
        }
    }

    private Table getWoodBoardTitleTable(int fontSize, String title){
        Table detailsTitleTable = new Table();
        detailsTitleTable.setBackground(new TextureRegionDrawable(_textures.getWoodBgTitle()));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _fonts.getPizzaFont(fontSize, Color.WHITE, 2, Color.valueOf("302505"), 0, Color.WHITE);

        Label labelTitle = new Label(title, labelStyle);
        labelTitle.setAlignment(Align.center);
        detailsTitleTable.add(labelTitle).expand().fill();
        return detailsTitleTable;
    }

    private Table getPlayerTable(MascotEnum mascotEnum, String name, String userId){

        if(_playerMaps.containsKey(userId) && userId != null) return _playerMaps.get(userId);

        Color fontColor = Color.BLACK;
        if(name == null){
            name = _texts.open();
            fontColor = Color.valueOf("c4c4c4");
        }

        Table playerTable = new Table();
        playerTable.padTop(5).padBottom(5).padLeft(7).padRight(7);
        playerTable.setBackground(new NinePatchDrawable(_textures.getWhiteRoundedBg()));

        Mascot mascotImage = new Mascot(mascotEnum, _textures);
        mascotImage.resizeTo(20, 20);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _fonts.getArialBold(11, fontColor, 0, Color.BLACK, 0, Color.GRAY);

        Label nameLabel = new Label(name, labelStyle);

        Label.LabelStyle progressLabelStyle = new Label.LabelStyle();
        progressLabelStyle.font = _fonts.getArial(11, Color.valueOf("51bf1b"));
        Label progressLabel = new Label("12", progressLabelStyle);
        progressLabel.setName("progress");
        progressLabel.setVisible(false);

        Image downloadImage = new Image(_textures.getDownloadIconSmall());
        downloadImage.addAction(forever(sequence(moveBy(0, -2), moveBy(0, 2, 0.9f))));
        downloadImage.setName("download");
        downloadImage.setVisible(false);

        playerTable.add(mascotImage);
        playerTable.add(nameLabel).expandX().fillX().padLeft(5);
        playerTable.add(downloadImage).padRight(2);
        playerTable.add(progressLabel);

        if(userId != null) _playerMaps.put(userId, playerTable);

        return playerTable;
    }


}
