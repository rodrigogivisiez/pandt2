package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.*;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.mygdx.potatoandtomato.models.InboxMessage;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListScene extends SceneAbstract {

    private Table gameTitleTable;
    private HashMap<String, String> hostToRoomIdMaps;
    private Table gameListTable;
    private BtnEggDownward newGameButton, joinGameButton, continueGameButton;
    private Label usernameLabel, inboxCountLabel;
    private Button settingsButton, leaderBoardsButton, shareButton, inboxButton;
    private Table inboxListTable, inboxMessageTable, inboxCountTable;
    private Table rateTable;
    private Table countryTable;

    public GameListScene(Services services, PTScreen screen) {
        super(services, screen);
        hostToRoomIdMaps = new HashMap();
    }

    @Override
    public void populateRoot() {
        topBar = new TopBar(_root, _texts.gamesListSceneTitle(), true, _assets, _screen, _services.getCoins());
        _root.align(Align.top);

        //Game List Table START
        Table gameListRootTable = new Table();
        gameListRootTable.align(Align.top);
        gameListRootTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.GAMELIST_BG)));

        gameTitleTable = new Table();
        gameTitleTable.align(Align.left);
        gameTitleTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GAMELIST_TITLE_BG)));
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR);
        Label titleGameLabel = new Label(_texts.gameHeader(), titleLabelStyle);
        Label titleHostLabel = new Label(_texts.hostHeader(), titleLabelStyle);
        Label titlePlayersLabel = new Label(_texts.playersHeader(), titleLabelStyle);

        gameTitleTable.add(titleGameLabel).padLeft(20).padRight(10).width(100);
        gameTitleTable.add(titleHostLabel).padLeft(8).padRight(10).width(95);
        gameTitleTable.add(titlePlayersLabel).padLeft(8).padRight(20).expandX().left();

        gameListTable = new Table();
        gameListTable.align(Align.top);
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SCROLLBAR_VERTICAL_HANDLE));
        ScrollPane gameListScrollPane = new ScrollPane(gameListTable, scrollPaneStyle);
        gameListScrollPane.setFadeScrollBars(false);
        //Game list Table END

        //Buttons START
        newGameButton = new BtnEggDownward(_assets, _services.getSoundsPlayer());
        newGameButton.setText(_texts.btnTextNewGame());

        joinGameButton = new BtnEggDownward(_assets, _services.getSoundsPlayer(), _services.getShaders());
        joinGameButton.setText(_texts.btnTextJoinGame());
        joinGameButton.setEnabled(false);

        continueGameButton = new BtnEggDownward(_assets, _services.getSoundsPlayer(), _services.getShaders());
        continueGameButton.setText(_texts.btnTextContinueLastGame());
        continueGameButton.setEnabled(false);

        Table buttonsTable = new Table();
        buttonsTable.setSize(Positions.getWidth(), 230);
        buttonsTable.setPosition(0, 0);
        buttonsTable.add(newGameButton).space(10).uniformX();
        buttonsTable.add(continueGameButton).space(10).uniformX();
        buttonsTable.add(joinGameButton).space(10).uniformX();
        //Buttons END

        //User profile START
        Table userProfileTable = new Table();
        userProfileTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.TRANS_BLACK_ROUNDED_BG)));

        Profile profile = _services.getProfile();
        usernameLabel = new Label(profile.getDisplayName(15),
                            new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_L_BOLD), null));


        Table inboxTable = new Table();
        inboxTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG)));
        Image inboxIconImg = new Image(_assets.getTextures().get(Textures.Name.INBOX_ICON));
        inboxTable.add(inboxIconImg).expand().fill().pad(7);
        inboxButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        inboxButton.setFillParent(true);
        inboxCountTable = new Table();
        inboxCountTable.setVisible(false);
        inboxCountTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.INBOX_UNREAD_COUNT)));
        Label.LabelStyle inboxCountLabelStyle = new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD), Color.valueOf("985c07"));
        inboxCountLabel = new Label("0", inboxCountLabelStyle);
        inboxCountTable.add(inboxCountLabel).padLeft(-2);
        inboxCountTable.setPosition(25, -1);
        inboxCountTable.setSize(21, 21);

        inboxTable.addActor(inboxButton);
        inboxTable.addActor(inboxCountTable);

        Table settingsTable = new Table();
        settingsTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG)));
        Image settingsIconImg = new Image(_assets.getTextures().get(Textures.Name.SETTINGS_ICON));
        settingsTable.add(settingsIconImg).expand().fill().pad(5);
        settingsButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        settingsButton.setFillParent(true);
        settingsTable.addActor(settingsButton);

        Table leaderBoardsTable = new Table();
        leaderBoardsTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG)));
        Image leaderBoardsIconImg = new Image(_assets.getTextures().get(Textures.Name.LEADERBOARD_MAIN_ICON));
        leaderBoardsTable.add(leaderBoardsIconImg).expandX().fillX().height(24).pad(5);
        leaderBoardsButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        leaderBoardsButton.setFillParent(true);
        leaderBoardsTable.addActor(leaderBoardsButton);


        Table shareTable = new Table();
        shareTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_ROUND_BUTTON_BG)));
        Image shareButtonImage = new Image(_assets.getTextures().get(Textures.Name.SHARE_ICON));
        shareTable.add(shareButtonImage).expandX().fillX().height(24).width(22).pad(5);
        shareButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        shareButton.setFillParent(true);
        shareTable.addActor(shareButton);

        countryTable = new Table();

        userProfileTable.add(countryTable).padRight(5);
        userProfileTable.add(usernameLabel).expand().fill().padLeft(5).padRight(10);
        userProfileTable.add(leaderBoardsTable).size(40).padRight(5);
        userProfileTable.add(inboxTable).size(40).padRight(5);
        userProfileTable.add(shareTable).size(40).padRight(5);
        userProfileTable.add(settingsTable).size(40).padRight(5);
        //User profile END

        gameListRootTable.add(gameTitleTable).expandX().fillX().height(45).padLeft(20).padRight(20);
        gameListRootTable.row();
        gameListRootTable.add(gameListScrollPane).expand().fill().padBottom(45).padLeft(20).padRight(20);

        ////////////////////////////////////////////
        //Inbox related
        //////////////////////////////////////////////
        inboxListTable = getInboxTableDesign(true);
        inboxListTable.setVisible(false);

        inboxMessageTable = getInboxTableDesign(false);
        inboxMessageTable.setVisible(false);

        _root.add(gameListRootTable).padTop(10).expandX().fillX().height(440).colspan(3);
        _root.row();
        _root.addActor(buttonsTable);
        _root.add(userProfileTable).expand().fill().padTop(60).padBottom(10).padLeft(10).padRight(10);
        _root.addActor(inboxListTable);
        _root.addActor(inboxMessageTable);
    }

    public void addNewRoomRow(final Room room, final boolean isInvited, final RunnableArgs<Actor> onFinish){
        final Button dummyButton = new Button(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
        final Table gameRowTable = new Table();
        gameRowTable.setName(room.getId());
        hostToRoomIdMaps.put(room.getHost().getUserId(), room.getId());

        Label.LabelStyle contentLabelStyle = new Label.LabelStyle();
        contentLabelStyle.font = _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD);

        Table gameNameInvitationTable = new Table();
        gameNameInvitationTable.align(Align.topLeft);
        gameNameInvitationTable.setName("gameNameInvitationTable");
        Image invitedImage = new Image(_assets.getTextures().get(Textures.Name.INVITED_ICON));
        gameNameInvitationTable.add(invitedImage).size(isInvited ? 12 : 0, 10).padRight(3);
        Label gameNameLabel = new Label(room.getGame().getName(), contentLabelStyle);
        gameNameLabel.setWrap(true);
        gameNameInvitationTable.add(gameNameLabel).expand().fill();

        Label hostNameLabel = new Label(room.getHost().getDisplayName(13), contentLabelStyle);
        hostNameLabel.setWrap(true);
        Label playersCountLabel = new Label(String.format("%s / %s", room.getRoomUsersCount(), room.getGame().getMaxPlayers()), contentLabelStyle);
        playersCountLabel.setName("playerCount");
        playersCountLabel.setWrap(true);

        dummyButton.setFillParent(true);

        gameRowTable.add(gameNameInvitationTable).width(100).padLeft(18).padRight(10);
        gameRowTable.add(hostNameLabel).width(95).padLeft(8).padRight(10);
        gameRowTable.add(playersCountLabel).expandX().left().padLeft(8).padRight(20);
        gameRowTable.padTop(5).padBottom(5);
        gameRowTable.addActor(dummyButton);
        gameListTable.add(gameRowTable).expandX().fillX();
        gameListTable.row();

        dummyButton.setName(String.valueOf(room.getId()));

        onFinish.run(dummyButton);
    }

    public void gameRowHighlight(final String roomId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                boolean found = false;

                for(Actor actor : gameListTable.getChildren()){
                    Table gameRow = (Table) actor;
                    if(gameRow.getName() != null && gameRow.getName().equals(roomId)){
                        found = true;
                        gameRow.background(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GAMELIST_HIGHLIGHT)));
                    }
                    else{
                        gameRow.background(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
                    }
                }

                if(found) joinGameButton.setEnabled(true);
                else joinGameButton.setEnabled(false);
            }
        });
    }

    public void arrangeInvitedGameRow(Table gameRowTable){
        //todo
    }

    public boolean alreadyContainsRoom(Room room){
        return getGameRowById(room.getId()) != null;
    }

    public void updatedRoom(final Room room, final RunnableArgs<Actor> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final boolean isInvited = (room.getUserIsInvited(_services.getProfile().getUserId()));
                if(!alreadyContainsRoom(room)){
                    if(hostToRoomIdMaps.containsKey(room.getHost().getUserId())){
                        removeRoom(hostToRoomIdMaps.get(room.getHost().getUserId()));
                    }
                    hostToRoomIdMaps.remove(room.getHost().getUserId());
                    addNewRoomRow(room, isInvited, onFinish);
                }
                else{
                    Table gameRowTable = getGameRowById(room.getId());

                    if(isInvited){
                        Table table = gameRowTable.findActor("gameNameInvitationTable");
                        table.getCells().get(0).width(12);
                        table.invalidate();
                    }

                    Label playerCountLabel = gameRowTable.findActor("playerCount");
                    playerCountLabel.setText(String.format("%s / %s", room.getRoomUsersCount(), room.getGame().getMaxPlayers()));
                    playerCountLabel.invalidate();

                    onFinish.run();
                }
            }
        });
    }

    public void removeRoom(final String roomId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final Actor actor = gameListTable.findActor(roomId);
                if(actor != null) actor.remove();
            }
        });
    }

    public void showRateMe(final RunnableArgs<Table> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(rateTable != null) rateTable.remove();

                rateTable = new Table();
                rateTable.setTransform(true);
                rateTable.align(Align.top);
                rateTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));

                Label.LabelStyle titleLabelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR),
                        Color.BLACK);

                Label titleLabel = new Label(_texts.askLikePandT(), titleLabelStyle);

                Image mascotTomato = new Image(_assets.getTextures().get(Textures.Name.TOMATO_LIKE_APPS));
                Image mascotPotato = new Image(_assets.getTextures().get(Textures.Name.POTATO_LIKE_APPS));

                Label.LabelStyle bigGreenStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.PIZZA_MAX_REGULAR_B_72c95e_ffffff_2),
                        null);
                Label.LabelStyle smallGreenStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.PIZZA_M_REGULAR_B_72c95e_ffffff_2),
                        null);

                Label yesBigLabel = new Label(_texts.yes() + ",", bigGreenStyle);
                Label yesSmallLabel = new Label(_texts.btnLikeApps(), smallGreenStyle);

                Table likeButtonTable = new Table();
                likeButtonTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BTN_CONCAVE)));
                likeButtonTable.add(yesBigLabel);
                likeButtonTable.row();
                likeButtonTable.add(yesSmallLabel).padLeft(15);
                likeButtonTable.setName("likeButtonTable");
                new DummyButton(likeButtonTable, _assets);

                TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
                textButtonStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BTN_CONVEX));
                textButtonStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_B_989898_ffffff_2);
                TextButton dislikeButton = new TextButton(_texts.btnDislikeApps(), textButtonStyle);
                dislikeButton.setName("dislikeButton");

                rateTable.add(titleLabel).colspan(2).padTop(20).padBottom(20);
                rateTable.row();
                rateTable.add(mascotTomato).padTop(5);
                rateTable.add(mascotPotato);
                rateTable.row();
                rateTable.add(likeButtonTable).padTop(10);
                rateTable.add(dislikeButton).padTop(10);

                rateTable.setSize(300, 254);
                rateTable.getColor().a = 0f;
                rateTable.setOrigin(Align.center);
                rateTable.setPosition(Positions.getWidth() / 2 - rateTable.getWidth() /2 , 200);

                _root.addActor(rateTable);

                onFinish.run(rateTable);

                rateTable.addAction(sequence(scaleTo(0, 0), fadeIn(0f), Actions.scaleTo(1, 1, 1f, Interpolation.bounceOut)));

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_ANTICIPATING);
            }
        });
    }

    public void showLikedApps(final RunnableArgs<Table> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(rateTable != null) rateTable.remove();

                rateTable = new Table();
                rateTable.align(Align.top);
                rateTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));

                Label.LabelStyle titleLabelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR),
                        Color.BLACK);

                Label titleLabel = new Label(_texts.thanksForLike(), titleLabelStyle);
                titleLabel.setAlignment(Align.center);

                Image mascotsImage = new Image(_assets.getTextures().get(Textures.Name.MASCOTS_LIKED_APPS));

                TextButton.TextButtonStyle goToPlayStoreStyle = new TextButton.TextButtonStyle();
                goToPlayStoreStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BTN_CONCAVE));
                goToPlayStoreStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_MAX_REGULAR_B_72c95e_ffffff_2);
                TextButton goToPlayStoreButton = new TextButton(_texts.btnGoToPlayStore(), goToPlayStoreStyle);
                goToPlayStoreButton.setName("goToPlayStoreButton");

                TextButton.TextButtonStyle dontGoToPlayStoreStyle = new TextButton.TextButtonStyle();
                dontGoToPlayStoreStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BTN_CONVEX));
                dontGoToPlayStoreStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_B_989898_ffffff_2);
                TextButton dontGoToPlayStoreButton = new TextButton(_texts.btnDontGoToPlayStore(), dontGoToPlayStoreStyle);
                dontGoToPlayStoreButton.setName("dontGoToPlayStoreButton");

                rateTable.add(titleLabel).padTop(20).padBottom(20).colspan(2).expandX().fillX();
                rateTable.row();
                rateTable.add(mascotsImage).padTop(5).colspan(2);
                rateTable.row();
                rateTable.add(goToPlayStoreButton).padTop(10).right();
                rateTable.add(dontGoToPlayStoreButton).padTop(10).left();

                rateTable.setSize(300, 254);
                rateTable.setPosition(Positions.getWidth() / 2 - rateTable.getWidth() /2 , 200);

                _root.addActor(rateTable);

                onFinish.run(rateTable);

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.WIN);
            }
        });
    }

    public void showDislikedApps(final RunnableArgs<Table> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(rateTable != null) rateTable.remove();

                rateTable = new Table();
                rateTable.align(Align.top);
                rateTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));

                Label.LabelStyle titleLabelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR),
                        Color.BLACK);

                Label titleLabel = new Label(_texts.sorryForDislike(), titleLabelStyle);
                titleLabel.setAlignment(Align.center);

                Image mascotsImage = new Image(_assets.getTextures().get(Textures.Name.MASCOT_DISLIKED_APPS));

                PTTextArea msgTextField = new PTTextArea(_assets, true);
                msgTextField.setName("msgTextField");

                TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
                backStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BTN_IRREGULAR));
                backStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_L_REGULAR_B_000000_ffffff_2);
                TextButton backButton = new TextButton(_texts.back(), backStyle);
                backButton.setName("backButton");

                TextButton.TextButtonStyle sendStyle = new TextButton.TextButtonStyle();
                sendStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BTN_IRREGULAR));
                sendStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_L_REGULAR_B_000000_ffffff_2);
                TextButton sendButton = new TextButton(_texts.send(), sendStyle);
                sendButton.setName("sendButton");

                Table buttonsTable = new Table();
                buttonsTable.add(backButton).padBottom(5);
                buttonsTable.row();
                buttonsTable.add(sendButton);

                rateTable.add(titleLabel).padTop(20).padBottom(10).colspan(2).expandX().fillX();
                rateTable.row();
                rateTable.add(mascotsImage).padTop(5).colspan(2);
                rateTable.row();
                rateTable.add(msgTextField).size(180, 80).padTop(5).padRight(5);
                rateTable.add(buttonsTable).left();

                rateTable.setSize(300, 254);
                rateTable.setPosition(Positions.getWidth() / 2 - rateTable.getWidth() /2 , 200);

                _root.addActor(rateTable);

                onFinish.run(rateTable);

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.LOSE);
            }
        });
    }

    public void removeRateApps(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(rateTable != null) rateTable.remove();
            }
        });
    }


    private Table getInboxTableDesign(boolean isInboxList){
        Table inboxTable = new Table();
        inboxTable.align(Align.top);
        inboxTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.INBOX_BG)));
        inboxTable.setSize(300, 200);
        inboxTable.setPosition(Positions.getWidth() / 2 - 150, 75);

        Table headerTable = new Table();
        headerTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.INBOX_HEADER_BG)));
        Image headerImage = new Image(_assets.getTextures().get(
                isInboxList ? Textures.Name.INBOX_ICON : Textures.Name.INBOX_OPENED_ICON));
        headerTable.add(headerImage);


        Image controlIcon = new Image(_assets.getTextures().get(
                isInboxList ? Textures.Name.INBOX_CLOSE_ICON : Textures.Name.INBOX_BACK_ICON));
        controlIcon.setPosition(inboxTable.getWidth() - controlIcon.getPrefWidth() - 10,
                inboxTable.getHeight() - controlIcon.getPrefHeight() - 10);

        Table contentTable = new Table();
        contentTable.align(Align.top);
        contentTable.setName("contentTable");
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SCROLLBAR_ORANGE_HANDLE));
        scrollPaneStyle.vScroll = new NinePatchDrawable(_assets.getPatches().get(Patches.Name.SCROLLBAR_ORANGE_BG));

        ScrollPane scrollPane = new ScrollPane(contentTable, scrollPaneStyle);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        inboxTable.add(headerTable).padTop(-30);
        inboxTable.addActor(controlIcon);
        inboxTable.row();
        inboxTable.add(scrollPane).expandX().fillX().pad(-15, 15, 15, 15).height(150);

        setInboxCloseBtnListener(controlIcon, isInboxList);
        return inboxTable;
    }

    public void addInboxMessageToList(final InboxMessage inboxMessage, final RunnableArgs<Actor> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table messageTable = new Table();
                messageTable.setName(inboxMessage.getId());
                messageTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.INBOX_MSG_BG)));

                Image imageIcon = new Image(!inboxMessage.isRead() ? _assets.getTextures().get(Textures.Name.INBOX_MSG_UNREAD) :
                        _assets.getTextures().get(Textures.Name.INBOX_MSG_READ));
                imageIcon.setName("imageIcon");

                Label.LabelStyle labelStyle = new Label.LabelStyle(
                        !inboxMessage.isRead() ? _assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD) :
                                _assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR),
                                                                    Color.BLACK);
                Label labelTitle = new Label(inboxMessage.getTitle(), labelStyle);
                labelTitle.setName("labelTitle");
                labelTitle.setAlignment(Align.left);
                labelTitle.setWrap(true);

                messageTable.add(imageIcon).expandY().center().padLeft(10);
                messageTable.add(labelTitle).expand().fill().pad(10);
                new DummyButton(messageTable, _assets);

                Table contentTable = inboxListTable.findActor("contentTable");
                contentTable.add(messageTable).expandX().fillX().padBottom(5).padRight(10);
                contentTable.row();

                onFinish.run(messageTable);

                if(!inboxMessage.isRead()){
                    String originalValue = inboxCountLabel.getText().toString();
                    int original = 0;
                    if(Strings.isNumeric(originalValue)){
                        original = Integer.valueOf(originalValue);
                    }

                    original++;
                    inboxCountLabel.setText(String.valueOf(original));
                    inboxCountTable.setVisible(true);

                    if(original == 1){
                        _services.getSoundsPlayer().playSoundEffect(Sounds.Name.INBOX_NOTIFICATION);
                    }
                }

            }
        });
    }

    public void inboxMessageIsRead(final InboxMessage inboxMessage){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table messageTable = inboxListTable.findActor(inboxMessage.getId());
                if(messageTable == null) return;

                Image imageIcon = messageTable.findActor("imageIcon");
                imageIcon.setDrawable(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.INBOX_MSG_READ)));

                Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR),
                        Color.BLACK);
                Label labelTitle =  messageTable.findActor("labelTitle");
                labelTitle.setStyle(labelStyle);

                String originalValue = inboxCountLabel.getText().toString();
                int original = 0;
                if(Strings.isNumeric(originalValue)){
                    original = Integer.valueOf(originalValue);
                }

                original--;
                original = Math.max(0, original);
                inboxCountLabel.setText(String.valueOf(original));
                inboxCountTable.setVisible(original > 0);
            }
        });
    }

    public void toggleInboxList(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                inboxListTable.setVisible(!inboxListTable.isVisible());
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.PAGE_TURN);
            }
        });
    }

    public void changeInboxMessage(final InboxMessage inboxMessage, final RunnableArgs<Array<Label>> onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table contentTable = inboxMessageTable.findActor("contentTable");
                contentTable.pad(10);
                contentTable.clear();

                Table rootTable = new Table();
                rootTable.pad(10);
                rootTable.align(Align.top);
                rootTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.INBOX_MSG_BG)));

                Label.LabelStyle titleLabelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_BOLD),
                        Color.BLACK);
                Label.LabelStyle contentLabelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR),
                        Color.BLACK);
                Label.LabelStyle urlLabelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR),
                        Color.valueOf("1a0dab"));

                Label titleLabel = new Label(inboxMessage.getTitle(), titleLabelStyle);
                titleLabel.setWrap(true);
                titleLabel.setAlignment(Align.center);

                ArrayList<Pair<String, Boolean>> splitted = Strings.splitUrl(inboxMessage.getMsg());
                Array<Label> labelArray = new Array();
                Array<Label> urlLabelArray = new Array();
                for(Pair<String, Boolean> pair : splitted){
                    Label contentLabel = new Label(pair.getFirst(),
                                            !pair.getSecond() ? contentLabelStyle : urlLabelStyle);
                    contentLabel.setWrap(true);
                    contentLabel.setAlignment(Align.left);
                    labelArray.add(contentLabel);
                    if(pair.getSecond()){
                        urlLabelArray.add(contentLabel);
                    }
                }

                rootTable.add(titleLabel).expandX().fillX().padBottom(10);

                for(Label label : labelArray){
                    rootTable.row();
                    rootTable.add(label).expandX().fillX().padBottom(5);
                }

                contentTable.add(rootTable).expand().fill();

                onFinish.run(urlLabelArray);
            }
        });
    }

    public void toggleInboxMessage(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                inboxMessageTable.setVisible(!inboxMessageTable.isVisible());
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.PAGE_TURN);
            }
        });
    }

    private void setInboxCloseBtnListener(Actor closeBtn, final boolean isInboxList){
        closeBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(isInboxList){
                    toggleInboxList();
                }
                else{
                    toggleInboxMessage();
                }
            }
        });
    }


    private Table getGameRowById(String id){
        return gameListTable.findActor(id);
    }

    public void setProfileDesign(final Profile profile){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                countryTable.clear();
                Badge badge = new Badge(BadgeType.Country, "", _assets, profile.getCountry());
                countryTable.add(badge).size(badge.getPrefWidth(), badge.getPrefHeight());
                usernameLabel.setText(profile.getDisplayName(13));
            }
        });
    }

    public void setContinueButtonEnabled(final boolean enabled){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                continueGameButton.setEnabled(enabled);
            }
        });
    }

    public BtnEggDownward getNewGameButton() {
        return newGameButton;
    }

    public BtnEggDownward getJoinGameButton() {
        return joinGameButton;
    }

    public BtnEggDownward getContinueGameButton() {
        return continueGameButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }

    public Button getLeaderBoardsButton() {
        return leaderBoardsButton;
    }

    public Table getGameTitleTable() {
        return gameTitleTable;
    }

    public Button getShareButton() {
        return shareButton;
    }

    public Button getInboxButton() {
        return inboxButton;
    }
}
