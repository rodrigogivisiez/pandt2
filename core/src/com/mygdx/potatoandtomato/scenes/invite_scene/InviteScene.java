package com.mygdx.potatoandtomato.scenes.invite_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.Badge;
import com.mygdx.potatoandtomato.controls.BtnEggDownward;
import com.mygdx.potatoandtomato.controls.DummyButton;
import com.mygdx.potatoandtomato.controls.TopBar;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.mygdx.potatoandtomato.models.FacebookProfile;
import com.mygdx.potatoandtomato.models.GameHistory;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.models.Streak;
import com.potatoandtomato.common.utils.MultiHashMap;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;

import java.util.HashMap;


/**
 * Created by SiongLeng on 23/12/2015.
 */
public class InviteScene extends SceneAbstract {

    BtnEggDownward _inviteButton;
    MultiHashMap<String, Table> _usersHashMap;
    HashMap<InviteType, Table> _containersMap;
    Table _recentPlayedTable;
    Table _facebookFriendsTable;
    Table _invitesTable;
    Table _recentTabTable, _facebookTabTable, _leaderboardTabTable;

    public InviteScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public BtnEggDownward getInviteButton() {
        return _inviteButton;
    }

    public Table getLeaderboardTabTable() {
        return _leaderboardTabTable;
    }

    public Table getFacebookTabTable() {
        return _facebookTabTable;
    }

    public Table getRecentTabTable() {
        return _recentTabTable;
    }

    @Override
    public void populateRoot() {
        _usersHashMap = new MultiHashMap();
        _containersMap = new HashMap<InviteType, Table>();
        topBar = new TopBar(_root, _texts.inviteSceneTitle(), false, _assets, _screen, _services.getCoins());

        Table _invitesRootTable = new Table();
        _invitesRootTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.INVITE_BG)));
        _invitesRootTable.align(Align.top);

        _invitesTable = new Table();

        //////////////////////////
        //Tabs tables
        ////////////////////////
        Table tabsTable = new Table();
        tabsTable.align(Align.left);

        /////////////////////////
        //Recent tab
        /////////////////////////
        _recentTabTable = new Table();
        Image recentImage = new Image(_assets.getTextures().get(Textures.Name.RECENT_ICON));
        _recentTabTable.add(recentImage).pad(5).size(19, 19);
        new DummyButton(_recentTabTable, _assets);

        Image separator1 = new Image(_assets.getTextures().get(Textures.Name.WHITE_VERTICAL_LINE));

        ////////////////////////////
        //Facebook Tab
        /////////////////////////////
        _facebookTabTable = new Table();
        Image facebookImage = new Image(_assets.getTextures().get(Textures.Name.FACEBOOK_INVITE_ICON));
        _facebookTabTable.add(facebookImage).pad(5).size(27, 19);
        new DummyButton(_facebookTabTable, _assets);

        Image separator2 = new Image(_assets.getTextures().get(Textures.Name.WHITE_VERTICAL_LINE));

        /////////////////////////////////
        //Leaderboard tab
        ////////////////////////////////
        _leaderboardTabTable = new Table();
        Image leaderboardImage = new Image(_assets.getTextures().get(Textures.Name.LEADERBOARD_INVITE_ICON));
        _leaderboardTabTable.add(leaderboardImage).pad(5).size(26, 19);
        new DummyButton(_leaderboardTabTable, _assets);

        //////////////////////////////////
        //Tabs table population
        /////////////////////////////////
        tabsTable.add(_recentTabTable).expand().fill().uniformX();
        tabsTable.add(separator1).width(1).expandY().fillY();
        tabsTable.add(_facebookTabTable).expand().fill().uniformX();
        tabsTable.add(separator2).width(1).expandY().fillY();
        tabsTable.add(_leaderboardTabTable).expand().fill().uniformX();

        //////////////////////////////////
        //Invite root population
        ////////////////////////////////////
        _invitesRootTable.add(tabsTable).expandX().fillX();
        _invitesRootTable.row();
        _invitesRootTable.add(_invitesTable).expand().fill();

        /////////////////////////////////////
        //Invite btnEggDownward button
        /////////////////////////////////////
        _inviteButton = new BtnEggDownward(_assets, _services.getSoundsPlayer());
        _inviteButton.setText(_texts.invite());

        ///////////////////////////////////
        //root population
        ///////////////////////////////////
        _root.add(_invitesRootTable).padLeft(15).padRight(15).expandX().fillX().height(450);
        _root.row();
        _root.add(_inviteButton).expandX().padTop(-10);
    }

    public void changeTab(final InviteType inviteType){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _invitesTable.clear();
                _invitesTable.add(getContainer(inviteType)).expand().fill();

                _recentTabTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
                _facebookTabTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));
                _leaderboardTabTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.EMPTY)));

                switch (inviteType){
                    case Recent:
                        _recentTabTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.INVITE_TAB_LEFT)));
                        break;
                    case Facebook:
                        _facebookTabTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.INVITE_TAB_CENTER)));
                        break;
                    case Leaderboard:
                        _leaderboardTabTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.INVITE_TAB_RIGHT)));
                        break;
                }
            }
        });
    }

    private Table getContainer(InviteType inviteType){
        if(!_containersMap.containsKey(inviteType)){

            final Table rootTable = new Table();
            rootTable.align(Align.top);
            rootTable.setName("rootTable");
            Table titleTable = new Table();
            titleTable.setName("titleTable");

            Table contentTable = new Table();
            contentTable.setName("contentTable");
            contentTable.align(Align.top);

            ScrollPane scrollPane = new ScrollPane(contentTable);

            rootTable.add(titleTable).expandX().fillX();
            rootTable.row();
            rootTable.add(scrollPane).expand().fill();
            _containersMap.put(inviteType, rootTable);
        }
        return _containersMap.get(inviteType);
    }

    private Table getContainerTitleTable(InviteType inviteType){
        return getContainer(inviteType).findActor("titleTable");
    }

    private Table getContainerContentTable(InviteType inviteType){
        return getContainer(inviteType).findActor("contentTable");
    }

    public void addTitleToContainer(final String title, final InviteType inviteType){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table containerTable = getContainerTitleTable(inviteType);

                final Table titleTable = new Table();

                titleTable.padLeft(10).padRight(10).padBottom(10).padTop(5);
                titleTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.EXPANDABLE_TITLE_BG)));

                Label titleLabel = new Label(title, new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_L_BOLD), null));
                titleTable.add(titleLabel).expandX().fillX();

                containerTable.add(titleTable).expandX().fillX();
            }
        });
    }

    public void putUserToTable(final Profile profile, final InviteScene.InviteType inviteType, final boolean alreadyInvited,
                                            final RunnableArgs<Actor> onFinish, final Object... objs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final Table userTable = new Table();
                userTable.setName("unselected");

                Label.LabelStyle normalStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD), null);
                Label.LabelStyle smallItalicStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR), null);

                Table contentTable = getContainerContentTable(inviteType);

                userTable.padLeft(10).padRight(10).padTop(5);
                new DummyButton(userTable, _assets);

                Table detailsTable = new Table();

                switch (inviteType){
                    case Recent:
                        GameHistory history = (GameHistory) objs[0];
                        Label nameLabel = new Label(profile.getDisplayName(30), normalStyle);
                        Label historyLabel = new Label(String.format(_texts.playedXAgo(), history.getNameOfGame(), history.getCreationDateAgo()),
                                smallItalicStyle);

                        detailsTable.add(nameLabel).expandX().fillX();
                        detailsTable.row();
                        detailsTable.add(historyLabel).expandX().fillX();
                        break;

                    case Facebook:
                        FacebookProfile facebookProfile = (FacebookProfile) objs[0];

                        Image image = new Image();
                        image.setName(facebookProfile.getUserId());

                        Table namesTable = new Table();
                        Label facebookNameLabel = new Label(Strings.cutOff(facebookProfile.getName(), 30), normalStyle);
                        Label gameNameLabel = new Label(String.format(_texts.gameNameIs(), profile.getDisplayName(25)),
                                smallItalicStyle);

                        namesTable.add(facebookNameLabel).expandX().fillX();
                        namesTable.row();
                        namesTable.add(gameNameLabel).expandX().fillX();

                        detailsTable.add(image).size(30, 30).padRight(10);
                        detailsTable.add(namesTable).expandX().fillX();
                        break;

                    case Leaderboard:
                        Integer rankNumber = (Integer) objs[0];
                        double score = (Double) objs[1];
                        Streak streak = (Streak) objs[2];

                        Badge rankBadge = new Badge(BadgeType.Rank, String.valueOf(rankNumber), _assets, 2);

                        Label nameLabel2 = new Label(profile.getDisplayName(30), normalStyle);
                        Label scoreLabel = new Label(String.format(_texts.xPoints(), Strings.formatNum((int) score)), smallItalicStyle);
                        Table nameScoreTable = new Table();
                        nameScoreTable.add(nameLabel2).expandX().fillX();
                        nameScoreTable.row();
                        nameScoreTable.add(scoreLabel).expandX().fillX();

                        detailsTable.add(rankBadge).padRight(5);
                        if(streak.hasValidStreak()){
                            Badge streakBadge = new Badge(BadgeType.Streak, String.valueOf(streak.getStreakCount()), _assets, 2);
                            detailsTable.add(streakBadge).size(28, 30).padRight(5);
                        }
                        detailsTable.add(nameScoreTable).expandX().fillX();

                        break;

                }
                Image selectBoxImage = new Image(_assets.getTextures().get(
                        alreadyInvited ? Textures.Name.SELECT_BOX : Textures.Name.UNSELECT_BOX));
                selectBoxImage.setName("selectbox");
                if(alreadyInvited) selectBoxImage.setColor(Color.valueOf("cdcdcd"));
                Image separator = new Image(_assets.getTextures().get(Textures.Name.WHITE_HORIZONTAL_LINE));

                userTable.add(detailsTable).expandX().fillX().padLeft(10);
                userTable.add(selectBoxImage).size(35, 35).padRight(10);
                userTable.row();
                userTable.add(separator).expandX().fillX().padTop(5).colspan(2);

                contentTable.add(userTable).expandX().fillX();
                contentTable.row();

                _usersHashMap.put(profile.getUserId(), userTable);

                onFinish.run(userTable);
            }
        });
    }


    public boolean toggleUserSelection(Profile user){

        boolean result = false;
        for(Table userTable : _usersHashMap.get(user.getUserId())){
            final Image selectBox =  userTable.findActor("selectbox");
            if(selectBox != null){
                if (userTable.getName().equals("selected")){
                    userTable.setName("unselected");
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            selectBox.setDrawable(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.UNSELECT_BOX)));
                        }
                    });
                    result = false;
                }
                else{
                    userTable.setName("selected");
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            selectBox.setDrawable(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SELECT_BOX)));
                        }
                    });
                    result = true;
                }
            }
        }
        return result;
    }

    public void putMessageToTable(final String msg, final InviteType type){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table messageTable = getContainerContentTable(type);
                messageTable.clear();

                Label msgLabel = new Label(msg, new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR), null));
                msgLabel.setWrap(true);
                messageTable.add(msgLabel).expandX().fillX().pad(10);
            }
        });

    }

    public void clearTableContent(final InviteType type){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                getContainerContentTable(type).clear();
            }
        });
    }

    public void putFacebookProfilePicture(final String fbId, final Texture texture){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table contentTable = getContainerContentTable(InviteType.Facebook);
                Image image = contentTable.findActor(fbId);
                image.setDrawable(new SpriteDrawable(new Sprite(texture)));
            }
        });
    }

    public enum InviteType {
        Recent, Facebook, Leaderboard
    }


}
