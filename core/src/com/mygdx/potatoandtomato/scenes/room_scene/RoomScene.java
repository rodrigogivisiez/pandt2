package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Animations;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.mygdx.potatoandtomato.controls.*;
import com.mygdx.potatoandtomato.enums.RoomUserState;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.RoomUser;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.utils.Sizes;
import com.potatoandtomato.common.controls.Animator;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

import java.util.concurrent.ConcurrentHashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class RoomScene extends SceneAbstract {

    private BtnEggDownward startButton, inviteButton;
    private Table teamsRoot, detailsRoot;
    private ConcurrentHashMap<String, Table> playerMaps;
    private Array<Table> slotsTable;
    private Image leaderboardButton;
    private SafeThread safeThread;
    private final int REFRESH_USER_ICON_PERIOD = 6000;

    public RoomScene(Services services, PTScreen screen, Room room) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {

        topBar = new TopBar(_root, _texts.roomSceneTitle(), false, _assets, _screen, _services.getCoins());
        _root.align(Align.top);

        playerMaps = new ConcurrentHashMap();
        slotsTable = new Array<Table>();

        Table buttonTable = new Table();

        startButton = new BtnEggDownward(_assets, _services.getSoundsPlayer(), _services.getShaders());
        startButton.setEnabled(false);
        startButton.setText(_texts.btnTextWaitingHost());

        inviteButton = new BtnEggDownward(_assets, _services.getSoundsPlayer());
        inviteButton.setText(_texts.invite());

        Image ropeImage1 = new Image(_assets.getTextures().get(Textures.Name.ROPE));
        Image ropeImage2 = new Image(_assets.getTextures().get(Textures.Name.ROPE));
        ropeImage1.setPosition(45, 84);
        ropeImage2.setPosition(startButton.getWidth() + 55, 84);

        buttonTable.add(startButton).padRight(10).width(startButton.getPrefWidth());
        buttonTable.add(inviteButton).padRight(10).width(inviteButton.getPrefWidth());

        buttonTable.addActor(ropeImage1);
        buttonTable.addActor(ropeImage2);

        teamsRoot = new Table();

        detailsRoot = new Table();
        detailsRoot.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX_ROUNDED)));
        detailsRoot.align(Align.top);
        detailsRoot.row();

        Table scrollableTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollableTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollableTable.add(teamsRoot).expandX().fillX().padTop(10);
        scrollableTable.row();
        scrollableTable.add(detailsRoot).expandX().fillX().padLeft(10).padRight(10).padBottom(200);

        _root.add(buttonTable).height(110).expandX();
        _root.row();
        _root.add(scrollPane).expandX().fillX();

        startRotateUserIconThread();
    }

    public void setStartButtonText(final String text){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                startButton.setText(text);
            }
        });
    }

    public void populateGameDetails(final Game game){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                WebImage gameImg = new WebImage(game.getIconUrl(), _assets, _services.getBroadcaster(), _ptGame);

                Image verticalSeparatorImage = new Image(_assets.getTextures().get(Textures.Name.ORANGE_VERTICAL_LINE));
                Image horizontalSeparatorImage = new Image(_assets.getTextures().get(Textures.Name.ORANGE_HORIZONTAL_LINE));


                Label.LabelStyle titleStyle = new Label.LabelStyle();
                titleStyle.fontColor = Color.valueOf("573801");
                titleStyle.font = _assets.getFonts().get(Fonts.FontId.HELVETICA_M_HEAVY);
                Label.LabelStyle smallStyle = new Label.LabelStyle();
                smallStyle.fontColor = Color.valueOf("573801");
                smallStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);
                Label.LabelStyle contentStyle = new Label.LabelStyle();
                contentStyle.fontColor = Color.valueOf("573801");
                contentStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR);

                Table _subRoot = new Table();
                _subRoot.align(Align.topLeft);

                Label titleLabel = new Label(game.getName(), titleStyle);
                titleLabel.setWrap(true);

                Table titleTable = new Table();
                titleTable.add(titleLabel).expandX().fillX().padRight(10);

                if(game.hasLeaderboard()){
                    leaderboardButton = new Image(_assets.getTextures().get(Textures.Name.LEADERBOARD_ICON));
                    titleTable.add(leaderboardButton).width(leaderboardButton.getPrefWidth());
                }

                Label playersLabel = new Label(String.format(_texts.xPlayers(), game.getMinPlayers(), game.getMaxPlayers()), smallStyle);
                Label versionAndLastUpdatedLabel = new Label(String.format(_texts.version(), game.getVersion()) + " (" + game.getLastUpdatedAgo() + ")", smallStyle);
                Label gameSizeLabel = new Label(String.format(_texts.xMb(), game.getGameSizeInMb()), smallStyle);
                Label descriptionLabel = new Label(game.getDescription(), contentStyle);
                descriptionLabel.setWrap(true);

                Table descriptionTable = new Table();
                descriptionTable.add(descriptionLabel).expand().fill();

                _subRoot.add(titleTable).expandX().fillX();
                _subRoot.row();
                _subRoot.add(playersLabel).left();
                _subRoot.row();
                _subRoot.add(versionAndLastUpdatedLabel).left();
                _subRoot.row();
                _subRoot.add(gameSizeLabel).left();
                _subRoot.row();
                _subRoot.add(horizontalSeparatorImage).expandX().fillX().padTop(5).padBottom(5);
                _subRoot.row();
                _subRoot.add(descriptionTable).left().expandX().fillX();

                detailsRoot.pad(10);
                detailsRoot.add(gameImg).size(120).top();
                detailsRoot.add(verticalSeparatorImage).padLeft(5).padRight(10).expandY().fillY();
                detailsRoot.add(_subRoot).expandX().fillX().top();
            }
        });
    }

    public void refreshTeamTables(final int totalTeams, final int teamMaxPlayers, final ConcurrentHashMap<String, RoomUser> roomUsers, final boolean isHost){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                teamsRoot.clear();
                slotsTable.clear();

                int accIndex = 0;
                Array<Table> teamTables = new Array();

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
                    teamTable.add(getWoodBoardTitleTable(String.format(_texts.teamTitle(), i+1))).padTop(-7).colspan(totalTeams == 1 ? 2 : 1);
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
                            playerTable = refreshAndGetPlayerTable(occupiedUser.getProfile().getDisplayName(15),
                                    occupiedUser.getProfile().getUserId(),
                                    occupiedUser.getRoomUserState(), isHost);
                        }
                        else{
                            playerTable = refreshAndGetPlayerTable(null, null, RoomUserState.Unknown, false);
                        }

                        playerTable.setName(String.valueOf(accIndex));

                        if(totalTeams == 1){
                            if((q % 2 == 0 && q != 0) || (teamMaxPlayers <= 2)) teamTable.row();
                            teamTable.add(playerTable).width(150).space(10);
                        }
                        else{
                            teamTable.add(playerTable).expandX().fillX().padLeft(10).padRight(10).padTop(5);
                            teamTable.row();
                        }

                        slotsTable.add(playerTable);
                        accIndex++;
                    }

                    teamTables.add(teamTable);
                }

                teamsRoot.padLeft(5).padRight(5);
                int i = 1;
                for(Table t : teamTables){

                    if(totalTeams > 1){
                        teamsRoot.add(t).expandX().fillX().center().uniformX().space(3).padBottom(10);
                    }
                    else{
                        teamsRoot.add(t).padBottom(10);
                    }


                    if(i % 2 == 0 && i!=0) {
                        teamsRoot.row();
                    }
                    i++;
                }
            }
        });
    }

    public void updateRoom(final Room room, final RunnableArgs<Boolean> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(room.getHost().equals(_services.getProfile())){
                    startButton.setEnabled(true);
                }

                if(checkCompleteRefreshRequired(room)){
                    refreshTeamTables(Integer.valueOf(room.getGame().getTeamCount()),
                            Integer.valueOf(room.getGame().getTeamMaxPlayers()), room.getRoomUsersMap(),
                            room.getHost().equals(_services.getProfile()));
                    onFinish.run(true);
                }
                else{
                    for(RoomUser roomUser : room.getRoomUsersMap().values()){
                        refreshAndGetPlayerTable(roomUser.getProfile().getDisplayName(15), roomUser.getProfile().getUserId(),
                                roomUser.getRoomUserState(), roomUser.getProfile().equals(room.getHost()));
                    }
                    onFinish.run(false);
                }
            }
        });
    }

    //complete refresh only occur when someone change slot/left room/join room
    private boolean checkCompleteRefreshRequired(Room room){
        boolean result = false;
        for(RoomUser roomUser : room.getRoomUsersMap().values()){
            Table playerTable = playerMaps.get(roomUser.getProfile().getUserId());
            if(playerTable == null){
                result = true;
                break;
            }
            else{
                if(!String.valueOf(roomUser.getSlotIndex()).equals(playerTable.getName())){
                    result = true;
                    break;
                }
            }

        }

        if(!result){
            for(String userId : playerMaps.keySet()){
                if(room.getRoomUserByUserId(userId) == null){
                    result = true;
                    playerMaps.remove(userId);
                }
            }
        }

        return result;
    }

    public void updateDownloadPercentage(final String userId, final int percent){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(playerMaps.containsKey(userId)){
                    Table playerTable = playerMaps.get(userId);
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
        });
    }

    private Table getWoodBoardTitleTable( final String title){
        final Table detailsTitleTable = new Table();
        detailsTitleTable.padTop(7).padBottom(7).padLeft(20).padRight(20);
        detailsTitleTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_TITLE)));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_M_REGULAR_B_ffffff_000000_1);

        Label labelTitle = new Label(title, labelStyle);
        labelTitle.setAlignment(Align.center);
        detailsTitleTable.add(labelTitle).expand().fill();

        return detailsTitleTable;
    }

    public void playerTableTouchedDown(final Table playerTable){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                playerTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.GREY_ROUNDED_BG)));
            }
        });
    }

    public void playerTableTouchedUp(final Table playerTable){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                playerTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.WHITE_ROUNDED_BG)));
            }
        });
    }

    private Table refreshAndGetPlayerTable(String name, String userId, RoomUserState roomUserState, boolean isHost){
        if(userId != null && playerMaps.containsKey(userId)){
            Table playerTable = playerMaps.get(userId);
            swapRoomUserStateIcon(userId, roomUserState);
            return playerMaps.get(userId);
        }

        BitmapFont font = _assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD);
        Color fontColor = Color.BLACK;
        if(name == null){
            name = _texts.slotOpen();
            fontColor = Color.valueOf("898887");
        }

        Table playerTable = new Table();
        playerTable.padTop(5).padBottom(5).padLeft(7).padRight(12);
        playerTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.WHITE_ROUNDED_BG)));

        Table iconTable = new Table();
        iconTable.setName("iconTable");

        Table roomUserStateIconTable = new Table();
        roomUserStateIconTable.setName("roomUserStateIconTable");
        iconTable.add(roomUserStateIconTable).expand().fill();

        Image unknownImage = new Image(_assets.getTextures().get(Textures.Name.UNKNOWN_ICON));
        unknownImage.setSize(5, 8);
        unknownImage.setPosition(5, 5);
        roomUserStateIconTable.addActor(unknownImage);


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = fontColor;

        Label nameLabel = new Label(name, labelStyle);

        //a hidden label to record next badge rotation time
        Label timeElapsedLabel = new Label("0", labelStyle);
        timeElapsedLabel.setName("timeElapsedLabel");
        timeElapsedLabel.setVisible(false);
        playerTable.addActor(timeElapsedLabel);


        Label.LabelStyle progressLabelStyle = new Label.LabelStyle();
        progressLabelStyle.fontColor = Color.valueOf("51bf1b");
        progressLabelStyle.font = _assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BOLD);
        Label progressLabel = new Label("22", progressLabelStyle);
        progressLabel.setName("progress");
        progressLabel.setVisible(false);

        Image downloadImage = new Image(_assets.getTextures().get(Textures.Name.DOWNLOAD_ICON));
        downloadImage.setName("download");
        downloadImage.setVisible(false);

        Image kickImage = new Image(_assets.getTextures().get(Textures.Name.KICK_ICON));
        Table kickTable = new Table();
        kickTable.add(kickImage).size(14, 14).padRight(-6).padBottom(2);

        Image kickDummy = new Image(_assets.getTextures().get(Textures.Name.EMPTY));
        kickDummy.setName("kickDummy");
        kickDummy.setSize(24, 24);
        kickDummy.setPosition(-4, -2);
        kickTable.addActor(kickDummy);

        new DummyButton(playerTable, _assets);
        playerTable.add(iconTable).size(16, 16).padRight(3);
        playerTable.add(nameLabel).expandX().fillX().padLeft(3).padBottom(2);
        playerTable.add(downloadImage).padRight(2);
        playerTable.add(progressLabel);
        if(isHost && !userId.equals(_services.getProfile().getUserId())) playerTable.add(kickTable).expandY().fillY();

        if(userId != null) playerMaps.put(userId, playerTable);

        swapRoomUserStateIcon(userId, roomUserState);

        return playerTable;
    }

    private void swapRoomUserStateIcon(final String userId, final RoomUserState roomUserState){
        if(userId != null && playerMaps.containsKey(userId)) {
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Table playerTable = playerMaps.get(userId);

                    Table iconTable = playerTable.findActor("iconTable");
                    Table roomUserStateIconTable = iconTable.findActor("roomUserStateIconTable");
                    roomUserStateIconTable.clear();

                    switch (roomUserState){
                        case Normal:
                            Image bulletIcon = new Image(_assets.getTextures().get(Textures.Name.BULLET_ICON));
                            bulletIcon.setName(roomUserState.name());
                            roomUserStateIconTable.add(bulletIcon).size(4, 4);
                            break;
                        case NotReady:
                            Animator loadingAnimator = new Animator(0.09f, _assets.getAnimations().get(Animations.Name.LOADING));
                            loadingAnimator.setName("loadingAnimator");
                            loadingAnimator.overrideSize(16, 16);
                            loadingAnimator.setName(roomUserState.name());
                            roomUserStateIconTable.addActor(loadingAnimator);
                            break;
                        case TemporaryDisconnected:
                            Image disconnectedIcon = new Image(_assets.getTextures().get(Textures.Name.DISCONNECTED_ICON));
                            disconnectedIcon.setName(roomUserState.name());
                            roomUserStateIconTable.add(disconnectedIcon).size(15, 15);
                            break;
                    }

                    ((Label) playerTable.findActor("timeElapsedLabel")).setText(String.valueOf(REFRESH_USER_ICON_PERIOD));
                }
            });
        }
    }

    public void addPlayerBadge(final String playerId, final BadgeType badgeType, final int num){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
            if(playerId != null && playerMaps.containsKey(playerId)) {
                Table playerTable = playerMaps.get(playerId);
                Table iconTable = playerTable.findActor("iconTable");

                for(Actor actor : iconTable.getChildren()){
                    if(actor.getName().equals(badgeType.name())){
                        actor.remove();
                    }
                }

                Table badgeTable = new Table();
                badgeTable.getColor().a = 0f;
                badgeTable.setVisible(false);
                badgeTable.setFillParent(true);
                badgeTable.setName(badgeType.name());
                Badge badge = new Badge(badgeType, String.valueOf(num), _assets);

                Vector2 size = Sizes.resizeByWidthWithMaxWidth(20, badge.getBadgeRegion());
                badgeTable.add(badge).padBottom(4).size(size.x, size.y);

                iconTable.addActor(badgeTable);
            }
            }
        });
    }

    public void removePlayerBadge(final String playerId, final BadgeType badgeType){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(playerId != null && playerMaps.containsKey(playerId)) {
                    Table playerTable = playerMaps.get(playerId);
                    Table iconTable = playerTable.findActor("iconTable");

                    for(Actor actor : iconTable.getChildren()){
                        if(actor.getName().equals(badgeType.name())){
                            actor.remove();
                        }
                    }
                }
            }
        });
    }

    public void removeAllPlayerBadges(final String playerId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(playerId != null && playerMaps.containsKey(playerId)) {
                    Table playerTable = playerMaps.get(playerId);
                    Table iconTable = playerTable.findActor("iconTable");
                    Table roomUserStateIconTable = iconTable.findActor("roomUserStateIconTable");
                    roomUserStateIconTable.clearActions();
                    roomUserStateIconTable.getColor().a = 1f;
                    roomUserStateIconTable.setVisible(true);


                    for(int i = 0; i < iconTable.getChildren().size; i++)
                    {
                        Actor actor = iconTable.getChildren().get(i);
                        if(!actor.getName().equals("roomUserStateIconTable")){
                            actor.remove();
                        }
                    }
                }
            }
        });
    }

    private void startRotateUserIconThread(){
        safeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int sleepPeriod = 1000;

                while (true){
                    if(safeThread.isKilled()) return;
                    else{
                        for(Table playerTable : playerMaps.values()){
                            Table iconTable = playerTable.findActor("iconTable");

                            boolean foundVisible = false;
                            for(Actor actor : iconTable.getChildren()){
                                if(actor.isVisible()){
                                    foundVisible = true;
                                    break;
                                }
                            }

                            if(!foundVisible && iconTable.getChildren().size > 0){
                                iconTable.getChildren().get(0).clearActions();
                                iconTable.getChildren().get(0).getColor().a = 1f;
                                iconTable.getChildren().get(0).setVisible(true);
                            }


                            Label timeElapsedLabel = playerTable.findActor("timeElapsedLabel");
                            int elapsed = Integer.valueOf(timeElapsedLabel.getText().toString());
                            elapsed += sleepPeriod;
                            if(elapsed < REFRESH_USER_ICON_PERIOD){
                                timeElapsedLabel.setText(String.valueOf(elapsed));
                                continue;
                            }
                            else{
                                timeElapsedLabel.setText("0");
                            }

                            for(int i = 0; i < iconTable.getChildren().size; i++){
                                int actor2Index = 0;

                                final Actor actor1 = iconTable.getChildren().get(i);
                                if(actor1.getName().equals(BadgeType.NoCoin.name())){
                                    break;
                                }

                                Actor actor2 = null;
                                if(i + 1 < iconTable.getChildren().size){
                                    actor2 = iconTable.getChildren().get(i+1);
                                    actor2Index = i+1;
                                }
                                else if(i > 0){
                                    actor2 = iconTable.getChildren().get(0);
                                    actor2Index = 0;
                                }

                                if(actor1.getName().equals("roomUserStateIconTable")){
                                    boolean locked = false;
                                    if(((Table) actor1).getChildren().size > 0){
                                        Actor child = ((Table) actor1).getChildren().get(0);
                                        if(!child.getName().equals(RoomUserState.Normal.name())){
                                            locked = true;
                                            if(!actor1.isVisible()){
                                                actor1.clearActions();
                                                actor1.getColor().a = 1f;
                                                actor1.setVisible(true);
                                                actor1.addAction(fadeIn(0.2f));
                                            }
                                        }
                                    }

                                    if(locked){
                                        for(Actor actor :  iconTable.getChildren()){
                                            if(actor.isVisible() && actor != actor1){
                                                actor.clearActions();
                                                actor.addAction(sequence(fadeOut(0.2f), new RunnableAction(){
                                                    @Override
                                                    public void run() {
                                                        actor.setVisible(false);
                                                    }
                                                }));
                                            }
                                        }

                                        break;
                                    }
                                }



                                if(actor2 != null && actor2.getName().equals("roomUserStateIconTable")){
                                    if(((Table) actor2).getChildren().size > 0){
                                        Actor child = ((Table) actor2).getChildren().get(0);
                                        if(child.getName().equals(RoomUserState.Normal.name())){
                                            actor2 = iconTable.getChildren().get(actor2Index + 1);
                                        }
                                        else{
                                            break;
                                        }
                                    }
                                }

                                if(actor1 != actor2){
                                    if(actor1.isVisible() && actor2 != null){
                                        actor1.clearActions();
                                        actor2.clearActions();
                                        actor1.addAction(sequence(fadeOut(0.2f), new RunnableAction(){
                                            @Override
                                            public void run() {
                                                actor1.setVisible(false);
                                            }
                                        }));
                                        actor2.setVisible(true);
                                        actor2.addAction(fadeIn(0.2f));
                                        break;
                                    }
                                    else if(!actor1.isVisible() && actor2 == null){
                                        actor1.clearActions();
                                        actor1.setVisible(true);
                                        actor1.addAction(fadeIn(0.2f));
                                        break;
                                    }
                                }
                            }

                        }

                        Threadings.sleep(sleepPeriod);
                    }
                }
            }
        });
    }

    public Table getPlayerTableByUserId(String userId){
        return playerMaps.get(userId);
    }

    public Array<Table> getSlotsTable() {
        return slotsTable;
    }

    public BtnEggDownward getStartButton() {
        return startButton;
    }

    public BtnEggDownward getInviteButton() {
        return inviteButton;
    }

    public Image getLeaderboardButton() {
        return leaderboardButton;
    }

    public Table getTeamsRoot() {
        return teamsRoot;
    }

    @Override
    public void dispose() {
        super.dispose();
        safeThread.kill();
    }
}
