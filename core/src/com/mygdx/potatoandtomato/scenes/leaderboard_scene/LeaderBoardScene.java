package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Animations;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.controls.Animator;
import com.mygdx.potatoandtomato.helpers.controls.FlowContainer;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.helpers.controls.WebImage;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.potatoandtomato.common.utils.Strings;
import com.mygdx.potatoandtomato.models.Game;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.Threadings;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 8/3/2016.
 */
public class LeaderBoardScene extends SceneAbstract {

    private HashMap<String, ScrollPane> _leaderboardScrolls;
    private Label _titleLabel, _animatingScoreLabel;
    private Table _iconTable, _ranksTable, _animatingRecordTable, _mascotsTable, _loadingTable;
    private float _recordHeight;
    private Game _currentShowingGame;

    public Label getTitleLabel() {
        return _titleLabel;
    }

    public LeaderBoardScene(Services services, PTScreen screen) {
        super(services, screen);

        _leaderboardScrolls = new HashMap<String, ScrollPane>();
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.leaderBoards(), false, _assets, _screen);
        _root.align(Align.top);

        ////////////////////////
        //leaderboard root
        ////////////////////////
        Table leaderBoardRoot = new Table();
        leaderBoardRoot.align(Align.top);
        leaderBoardRoot.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LEADER_BOARD_BG)));

        /////////////////////////////////
        //mascots
        ////////////////////////////////
        _mascotsTable = new Table();
        _mascotsTable.setTransform(true);
        _mascotsTable.setSize(100, 80);
        _mascotsTable.setPosition(260, -30);

        Table mascotContainer = new Table();
        mascotContainer.addActor(_mascotsTable);

        //////////////////////////////////
        //icon
        /////////////////////////////////
        _iconTable = new Table();
        _iconTable.setTransform(true);
        _iconTable.setRotation(15);

        //////////////////////////////////
        //Title
        /////////////////////////////////
        Table titleTable = new Table();

        _titleLabel = new Label(_texts.loading(), new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_B_ffffff_0f5673_1_S_0e516c_1_3), null));
        _titleLabel.setWrap(true);
        _titleLabel.setAlignment(Align.center);
        titleTable.add(_titleLabel).expand().fill();

        ///////////////////////////////////
        //Ranks table
        //////////////////////////////////
        _ranksTable = new Table();

        /////////////////////////////////////
        //population of leaderboard
        ///////////////////////////////////
        leaderBoardRoot.add(_iconTable).width(64).height(60).padLeft(54).padTop(25);
        leaderBoardRoot.add(titleTable).expandX().fillX().padLeft(15).padRight(75).height(60).padTop(20);
        leaderBoardRoot.row();
        leaderBoardRoot.add(_ranksTable).expand().fill().colspan(2).padTop(30);

        ///////////////////////////////
        //population of root
        //////////////////////////////
        _root.add(mascotContainer).expandX().fillX().padTop(62);
        _root.row();
        _root.add(leaderBoardRoot).expand().fill();
        _root.row();
    }

    public void showGameLeaderboard(Game game){
        _currentShowingGame = game;
        _iconTable.clear();

        WebImage webImage = new WebImage(game.getIconUrl(), _assets, _services.getBroadcaster());
        _iconTable.add(webImage).expand().fill();
        _iconTable.addAction(sequence(fadeOut(0f), fadeIn(0.2f)));

        _titleLabel.setText(game.getName());
        _titleLabel.addAction(sequence(fadeOut(0f), fadeIn(0.2f)));

        _ranksTable.clear();
        _ranksTable.addAction(sequence(fadeOut(0f), fadeIn(0.2f)));

        boolean found = _leaderboardScrolls.containsKey(game.getAbbr());
        if(!found){
            Table gameRanksTable = new Table();
            gameRanksTable.padLeft(10).padRight(10).padBottom(50);
            gameRanksTable.setName("ranksTable");
            ScrollPane scroll = new ScrollPane(gameRanksTable);
            scroll.setScrollingDisabled(true, false);
            scroll.getColor().a = 0f;

            _leaderboardScrolls.put(game.getAbbr(), scroll);
        }

        _ranksTable.add(_leaderboardScrolls.get(game.getAbbr())).expand().fill();

        if(!found){
            Label loadingLabel = new Label(_texts.loading(), new Label.LabelStyle(
                    _assets.getFonts().get(Fonts.FontId.MYRIAD_S_ITALIC), Color.BLACK));
            _loadingTable = new Table();
            _loadingTable.add(loadingLabel);
            _loadingTable.setFillParent(true);
            _ranksTable.addActor(_loadingTable);
        }

    }

    public void leaderboardDataLoaded(final Game game, ArrayList<LeaderboardRecord> records){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");
        ranksTable.padBottom(20);
        ranksTable.align(Align.top);
        for(int i = 0; i < records.size(); i++){
            ////////////////////////////////
            //insert to ranks table
            /////////////////////////////////
            Table recordTable = getRecordTable(game, records.get(i), i);
            ranksTable.add(recordTable).expandX().fillX();
            ranksTable.row();
        }
    }

    public void changeRecordTableToUnknownRank(Game game, int rank){
        Table rankTable = getRankTable(game);
        Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
        ((Label) recordTable.findActor("countLabel")).setText("-");

//        Table newTable = new Table();
//        Image verticalDotsImage = new Image(_assets.getTextures().get(Textures.Name.VERTICAL_DOTS));
//        newTable.add(verticalDotsImage).padTop(30).padBottom(20);
//        newTable.row();
//        newTable.add(recordTable).expandX().fillX();
//        rankTable.removeActor(recordTable);
//        rankTable.add(newTable).expandX().fillX();

    }

    private Table getRecordTable(Game game, LeaderboardRecord record, int rank){

        Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_M_REGULAR), getTextColorOfRecord(record));
        Label.LabelStyle style2 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_L_BOLD), getTextColorOfRecord(record));

        ////////////////////////
        //Index label
        //////////////////////////
        Label countLabel = new Label((rank + 1) + "." ,style1);
        countLabel.setName("countLabel");

        ////////////////////////
        //name score table
        ///////////////////////
        Table nameScoreTable = new Table();

        ////////////////////////
        //name and streak table
        ///////////////////////
        Table nameStreakTable = invalidateNameStreakTable(game, record, rank, false);
        nameStreakTable.setName("nameStreakTable");

        FlowContainer nameStreakContainer = new FlowContainer(nameStreakTable, _assets);

        ////////////////////////
        //score label
        ///////////////////////
        Label scoreLabel = new Label(Strings.formatNum((int) record.getScore()), style2);
        scoreLabel.setAlignment(Align.right);

        ////////////////////////
        //separator
        ///////////////////////
        Image imageSeparator = new Image(_assets.getTextures().get(Textures.Name.LEADER_BOARD_SEPARATOR));

        ////////////////////////////////
        //populate name score table
        /////////////////////////////////
        nameScoreTable.add(nameStreakContainer).expandX().fillX().padLeft(5).center();
        nameScoreTable.add(scoreLabel).right().padRight(5).padTop(2).padBottom(2).width(55);
        nameScoreTable.row();
        nameScoreTable.add(imageSeparator).colspan(2).expandX().fillX();

        ////////////////////////////////
        //record table population
        /////////////////////////////////
        Table recordTable = new Table();
        recordTable.setTransform(true);
        recordTable.padLeft(40).padRight(40);
        recordTable.add(countLabel).width(30).center();
        recordTable.add(nameScoreTable).expandX().fillX();
        recordTable.layout();

        if(_recordHeight == 0){
            recordTable.layout();
            _recordHeight = recordTable.getPrefHeight();
        }

        nameStreakContainer.setSizeLimit(150, _recordHeight);

        return recordTable;
    }

    public void populateAnimateTable(final Game game, LeaderboardRecord record, int rank, boolean isLast){
        ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");

        Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_M_REGULAR), Color.valueOf("5b3000"));
        Label.LabelStyle style3 = new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontId.HELVETICA_L_HEAVY), Color.valueOf("5b3000"));
        Label.LabelStyle style4 = new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontId.HELVETICA_XL_HEAVYITALIC_B_ffffff_81562c_2), null);

        ////////////////////////
        //Index label
        //////////////////////////
        Label countLabel = new Label((isLast ? "-" : (rank + 1) + ". ") ,style1);

        ////////////////////////
        //name score table
        ///////////////////////
        Table nameScoreTable = new Table();
        nameScoreTable.setName("nameScoreTable");
        nameScoreTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LEADERBOARD_ANIMATING_BASE)));

        ////////////////////
        //name label
        /////////////////////
        Label nameLabel = new Label(Strings.cutOff(record.getAllUsernameCommaSeparated(), 16), style3);

        /////////////////////////
        //score label
        /////////////////////////
        _animatingScoreLabel = new Label(Strings.formatNum((int) record.getScore()), style4);

        //////////////////////////////
        //populate namescore table
        ///////////////////////////
        nameScoreTable.add(nameLabel).expandX().fillX().padLeft(10).center();
        nameScoreTable.add(_animatingScoreLabel).right().padRight(10);

        ////////////////////////////////
        //populate record table
        //////////////////////////////////
        _animatingRecordTable = new Table();
        _animatingRecordTable.setTransform(true);
        _animatingRecordTable.padLeft(40).padRight(35);
        _animatingRecordTable.add(countLabel).width(25).center();
        _animatingRecordTable.add(nameScoreTable).expand().fill().center();
        _animatingRecordTable.layout();

        ///////////////////////////////////////
        //insert to ranks table
        ///////////////////////////////////////
        ranksTable.add(_animatingRecordTable).expandX().fillX().height(_recordHeight);
        ranksTable.row();

        int count = ranksTable.getChildren().size;
        if(count-1 != rank){
            swapActorInTable(ranksTable, count - 1, rank);
            ranksTable.getCells().get(count - 1).getActor().remove();
        }

    }

    public void hideLoading(final Game game){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        if(_currentShowingGame.getAbbr().equals(game.getAbbr())){
            _loadingTable.addAction(sequence(fadeOut(0.2f), new RunnableAction(){
                @Override
                public void run() {
                    scrollPane.addAction(fadeIn(0.2f));
                }
            }));
        }
        else{
            _loadingTable.setVisible(false);
            scrollPane.getColor().a = 1f;
        }
    }

    public void addScore(ScoreDetails scoreDetails, final Runnable onFinish){

        Color fontColor = scoreDetails.isAddOrMultiply() ? Color.valueOf("fff600") : Color.valueOf("ff7676");

        Label.LabelStyle style5 = new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontId.CARTER_S_REGULAR_B_ffffff_000000_1), null);
        style5.font.getData().setLineHeight(13);
        style5.fontColor = fontColor;

        Label.LabelStyle style6 = new Label.LabelStyle(
                _assets.getFonts().get(Fonts.FontId.CARTER_L_REGULAR_B_ffffff_000000_1), null);
        style6.fontColor = fontColor;

        ////////////////////////////////////////
        //added score label
        ///////////////////////////////////////
        final Table addedScoreTable = new Table();
        addedScoreTable.setTransform(true);
        addedScoreTable.getColor().a = 0f;

        Label addedScoreLabel = new Label((scoreDetails.isAddOrMultiply() ? "+" : "x") + (Strings.formatNum(scoreDetails.getValue())), style6);
        addedScoreLabel.setAlignment(Align.center);
        Label addedReasonLabel = new Label(scoreDetails.getReason(), style5);
        addedReasonLabel.setAlignment(Align.center);
        addedReasonLabel.setWrap(true);
        addedScoreTable.add(addedScoreLabel).expandX().fillX();
        addedScoreTable.row();
        addedScoreTable.add(addedReasonLabel).minWidth(80).expandX().fillX().padTop(-10);
        addedScoreTable.layout();
        addedScoreTable.setSize(addedScoreTable.getPrefWidth(), 30);
        addedScoreTable.setPosition(Positions.getWidth() - addedScoreTable.getPrefWidth() - 10, 0);

        _animatingRecordTable.addActor(addedScoreTable);

        Vector2 coords = Positions.actorLocalToStageCoord(addedScoreTable);
        addedScoreTable.remove();
        addedScoreTable.setPosition(coords.x, coords.y);
        _root.addActor(addedScoreTable);

        float delayDuration = 1.6f;
        if(scoreDetails.getReason().length() > 10) delayDuration = 2.3f;

        addedScoreTable.setOrigin(addedScoreTable.getWidth()/2, addedScoreTable.getHeight()/2);
        addedScoreTable.addAction(sequence(scaleTo(0, 0), fadeIn(0), scaleTo(1, 1, 0.3f, Interpolation.exp5In), delay(delayDuration), fadeOut(0.1f),
                new RunnableAction(){
                    @Override
                    public void run() {
                        addedScoreTable.remove();
                        onFinish.run();
                    }
                }
        ));

        Image starImageOne = new Image(_assets.getTextures().get(Textures.Name.SMALL_STAR_ICON));
        starImageOne.setSize(10, 10);
        starImageOne.getColor().a = 0f;
        starImageOne.setPosition(0, addedScoreTable.getHeight() - 10);
        Image starImageTwo = new Image(_assets.getTextures().get(Textures.Name.SMALL_STAR_ICON));
        starImageTwo.setSize(10, 10);
        starImageTwo.getColor().a = 0f;
        starImageTwo.setPosition(addedScoreTable.getWidth() - 10, addedScoreTable.getHeight() - 10);

        addedScoreTable.addActor(starImageOne);
        addedScoreTable.addActor(starImageTwo);

        starImageOne.addAction(sequence(delay(0.3f), fadeIn(0f), moveBy(-5, 5, 0.3f), parallel(moveBy(-5, -5, 0.3f), fadeOut(0.3f))));
        starImageTwo.addAction(sequence(delay(0.3f), fadeIn(0f), moveBy(5, 5, 0.3f), parallel(moveBy(5, -5, 0.3f), fadeOut(0.3f))));


    }


    public void setAnimatingScore(double value){
        _animatingScoreLabel.setText(Strings.formatNum( (int) value));
    }

    //current rank start from zero
    public void moveUpRank(final Game game, final int toRank, final int originalRank,
                           final LeaderboardRecord movingRecord, final int maxSize, final Runnable finishAnimate){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");

        final Runnable onFinishMoved = new Runnable() {
            @Override
            public void run() {
                movingEndedAnimate(game, ranksTable, movingRecord, toRank, maxSize, finishAnimate);
            }
        };

        if(toRank == originalRank){
            Threadings.delay(200, new Runnable() {
                @Override
                public void run() {
                    onFinishMoved.run();
                }
            });
        }
        else{
            float duration = 1.5f;
            if(originalRank - toRank < 20) duration = 0.9f;
            if(originalRank - toRank < 10) duration = 0.5f;

            final float finalDuration = duration;
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    final boolean[] finishMoving = {false};
                    final Table toSwitchActor = (Table) ranksTable.getCells().get(toRank).getActor();

                    _services.getSoundsWrapper().playSoundEffectLoop(Sounds.Name.MOVING_RANK);

                    _animatingRecordTable.addAction(sequence(Actions.moveTo(toSwitchActor.getX(), toSwitchActor.getY(), finalDuration, Interpolation.exp10Out), new RunnableAction(){
                        @Override
                        public void run() {
                            finishMoving[0] = true;
                            _services.getSoundsWrapper().stopSoundEffectLoop(Sounds.Name.MOVING_RANK);
                            moveDownOneRank(ranksTable, toRank, originalRank, maxSize, new Runnable() {
                                @Override
                                public void run() {
                                    Threadings.delay(500, new Runnable() {
                                        @Override
                                        public void run() {
                                            _services.getSoundsWrapper().playSoundEffect(Sounds.Name.MOVING_RANK_END);
                                            onFinishMoved.run();
                                        }
                                    });
                                }
                            });
                        }
                    }));

                    scrollPane.scrollTo(0, toSwitchActor.getY() + 100, 0, _recordHeight);
                }
            });
        }
    }

    public Table invalidateNameStreakTable(Game game, LeaderboardRecord record, int rank, boolean animate){
        Table rankTable = getRankTable(game);
        Table nameStreakTable = null;
        boolean notFound = false;
        if(rankTable.getCells().size - 1 <= rank){
            notFound = true;
        }
        else{
            Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
            nameStreakTable = recordTable.findActor("nameStreakTable");
            if(nameStreakTable == null){
                notFound = true;
            }
            else{
                nameStreakTable.clear();
            }
        }

        if(notFound){
            nameStreakTable = new Table();
            nameStreakTable.setName("nameStreakTable");
        }

        Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_M_REGULAR), getTextColorOfRecord(record));

        Label.LabelStyle style2 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_XS_SEMIBOLD_B_ffffff_000000_1), null);

        Label nameLabel = new Label(record.getAllUsernameCommaSeparated(), style1);
        nameLabel.setAlignment(Align.left);

        if(record.getStreak().hasValidStreak()){
            Table streakTable = new Table();
            streakTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.STREAK_ICON)));
            Label streakLabel = new Label(String.valueOf(record.getStreak().getStreakCount()), style2);
            streakTable.add(streakLabel).padTop(3).padLeft(2);
            streakTable.setName("streakTable");
            nameStreakTable.add(streakTable).padRight(5).width(15);

            if(animate){
                streakLabel.getColor().a = 0f;
                streakLabel.addAction(fadeIn(0.2f));
            }
        }

        nameStreakTable.add(nameLabel).expand().fill();

        return nameStreakTable;
    }

    public void loseStreakAnimate(final Game game, int rank){
        Table rankTable = getRankTable(game);
        Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
        Table streakTable = recordTable.findActor("streakTable");
        streakTable.addAction(fadeOut(0.2f));
        _services.getSoundsWrapper().playSoundEffect(Sounds.Name.STREAK_DIED);
    }

    public void reviveStreakAnimate(final Game game, int rank){
        Table rankTable = getRankTable(game);
        Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
        Table streakTable = recordTable.findActor("streakTable");
        streakTable.addAction(fadeIn(0.2f));
        _services.getSoundsWrapper().playSoundEffect(Sounds.Name.STREAK);
    }

    private void movingEndedAnimate(Game game, Table ranksTable, LeaderboardRecord movingRecord, final int toRank, int maxSize, Runnable finishAnimate){
        Table newRecordTable = getRecordTable(game, movingRecord, toRank);
        if(toRank >= maxSize){
            ((Label) newRecordTable.findActor("countLabel")).setText("-");
        }
        ranksTable.getCells().get(toRank).setActor(newRecordTable);
        ranksTable.layout();
        animateStarTrace(newRecordTable, finishAnimate);
    }


    private void animateStarTrace(Table actor, final Runnable finishAnimate){
        Image starImageOne = new Image(_assets.getTextures().get(Textures.Name.SMALL_STAR_ICON));
        starImageOne.setSize(10, 10);
        starImageOne.setPosition(40, actor.getHeight() / 2  + 5);

        Image starImageTwo = new Image(_assets.getTextures().get(Textures.Name.SMALL_STAR_ICON));
        starImageTwo.setSize(10, 10);
        starImageTwo.setPosition(actor.getWidth() - 40, actor.getHeight() / 2  + 5);

        Image starImageThree = new Image(_assets.getTextures().get(Textures.Name.SMALL_STAR_ICON));
        starImageThree.setSize(10, 10);
        starImageThree.setPosition(actor.getWidth() / 2, actor.getHeight() / 2  + 5);

        actor.addActor(starImageOne);
        actor.addActor(starImageTwo);
        actor.addActor(starImageThree);

        starImageOne.addAction(sequence(moveBy(-5, 5, 0.3f), parallel(moveBy(-10, -10, 0.5f), fadeOut(0.5f))));
        starImageTwo.addAction(sequence(moveBy(5, 5, 0.3f), parallel(moveBy(10, -10, 0.5f), fadeOut(0.5f))));

        starImageThree.addAction(sequence(moveBy(0, 5, 0.3f), parallel(moveBy(10, -10, 0.5f), fadeOut(0.5f)), new RunnableAction(){
            @Override
            public void run() {
                finishAnimate.run();
            }
        }));
    }

    public void scrollToAnimateRecord(Game game, int offset){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        scrollPane.scrollTo(0, _animatingRecordTable.getY() + offset, 0, _animatingRecordTable.getHeight());
    }

    public void scrollToRecord(Game game, int rankNumber){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");
        scrollPane.scrollTo(0, ranksTable.getCells().get(rankNumber).getActorY() - 100, 0, ranksTable.getCells().get(rankNumber).getActorHeight());
    }

    public void moveDownOneRank(final Table ranksTable, final int startRank, final int endRank, final int maxSize, final Runnable onFinish){

        for(int i = startRank; i < endRank; i ++){
            ranksTable.getChildren().get(i).addAction(moveBy(0, -_recordHeight, 0.2f));
        }
        Threadings.delay(300, new Runnable() {
            @Override
            public void run() {
                for(int i = startRank; i < endRank; i ++){
                    swapActorInTable(ranksTable, i, endRank);
                }
                for(int i = startRank + 1; i <= endRank; i++){
                    final Label countLabel = ((Table) ranksTable.getCells().get(i).getActor()).findActor("countLabel");
                    final int newRank = Integer.valueOf(countLabel.getText().toString().replace(".", "")) + 1;
                    if(newRank - 1 < maxSize){
                        countLabel.addAction(sequence(alpha(0.1f, 0.8f), new RunnableAction(){
                            @Override
                            public void run() {
                                countLabel.setText(String.valueOf(newRank) + ".");
                            }
                        }, fadeIn(1f)));
                    }
                }
                onFinish.run();
            }
        });
    }

    private Color getTextColorOfRecord(LeaderboardRecord record){
        Color textColor = Color.valueOf("5b3000");
        if(record.containUser(_services.getProfile().getUserId())){
            textColor = Color.valueOf("3daaef");
        }
        return textColor;
    }

    private Table getRankTable(Game game){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");
        return ranksTable;
    }

    private void swapActorInTable(Table table, int index1, int index2){
        Actor actor1 = (Table) table.getCells().get(index1).getActor();
        Actor actor2 = (Table) table.getCells().get(index2).getActor();
        table.getCells().get(index1).setActor(null);
        table.getCells().get(index2).setActor(null);
        table.getCells().get(index1).setActor(actor2);
        table.getCells().get(index2).setActor(actor1);
    }

    public void setMascots(final MascotType type){
        _mascotsTable.clearActions();
        _mascotsTable.addAction(sequence(alpha(0.5f, 0.2f), new RunnableAction() {
            @Override
            public void run() {
                _mascotsTable.clear();
                if (type == MascotType.BORING) {
                    Animator tomato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.TOMATO_BORING));
                    tomato.setSize(tomato.getWidth(), tomato.getHeight());
                    tomato.setPosition(-70, 15);
                    tomato.setRotation(-15);

                    Animator potato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.POTATO_BORING));
                    potato.setSize(potato.getWidth(), potato.getHeight());
                    potato.setPosition(-28, 8);
                    potato.setRotation(-20);

                    _mascotsTable.addActor(tomato);
                    _mascotsTable.addActor(potato);

                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.TOGETHER_BORED);

                } else if (type == MascotType.FAILED) {
                    Animator tomato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.TOMATO_FAILED));
                    tomato.setSize(tomato.getWidth(), tomato.getHeight());
                    tomato.setPosition(-70, 20);
                    tomato.setRotation(-15);

                    Animator potato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.POTATO_FAILED));
                    potato.setSize(potato.getWidth(), potato.getHeight());
                    potato.setPosition(-28, 8);
                    potato.setRotation(-20);

                    _mascotsTable.addActor(tomato);
                    _mascotsTable.addActor(potato);

                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.TOGETHER_FAILED);

                } else if (type == MascotType.CRY) {
                    Animator tomato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.TOMATO_CRY));
                    tomato.setSize(tomato.getWidth(), tomato.getHeight());
                    tomato.setPosition(-100, 0);
                    tomato.setRotation(-5);

                    Animator potato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.POTATO_CRY));
                    potato.setSize(potato.getWidth(), potato.getHeight());
                    potato.setPosition(-28, -15);
                    potato.setRotation(-5);

                    _mascotsTable.addActor(tomato);
                    _mascotsTable.addActor(potato);

                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.TOGETHER_CRY);

                } else if (type == MascotType.HAPPY) {
                    Animator tomato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.TOMATO_HAPPY));
                    tomato.setSize(tomato.getWidth(), tomato.getHeight());
                    tomato.setPosition(-70, 0);
                    tomato.setRotation(0);

                    Animator potato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.POTATO_HAPPY));
                    potato.setSize(potato.getWidth(), potato.getHeight());
                    potato.setPosition(0, -5);
                    potato.setRotation(0);

                    _mascotsTable.addActor(tomato);
                    _mascotsTable.addActor(potato);

                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.TOGETHER_HAPPY);

                } else if (type == MascotType.ANTICIPATE) {
                    Animator tomato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.TOMATO_ANTICIPATE));
                    tomato.setSize(tomato.getWidth(), tomato.getHeight());
                    tomato.setPosition(-80, 13);
                    tomato.setRotation(-5);

                    Animator potato = new Animator(0.3f, _assets.getAnimations().get(Animations.Name.POTATO_ANTICIPATE));
                    potato.setSize(potato.getWidth(), potato.getHeight());
                    potato.setPosition(-10, -5);
                    potato.setRotation(-5);

                    _mascotsTable.addActor(tomato);
                    _mascotsTable.addActor(potato);

                    _services.getSoundsWrapper().playSoundEffect(Sounds.Name.TOGETHER_ANTICIPATING);
                }
                _mascotsTable.addAction(sequence(fadeIn(0.1f)));
            }
        }));


    }

    public enum MascotType{
        BORING, FAILED, CRY, HAPPY, ANTICIPATE
    }

}
