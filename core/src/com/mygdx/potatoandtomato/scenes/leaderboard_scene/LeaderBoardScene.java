package com.mygdx.potatoandtomato.scenes.leaderboard_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.controls.*;
import com.mygdx.potatoandtomato.enums.BadgeType;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.potatoandtomato.common.controls.Animator;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.controls.AutoDisposeTable;
import com.potatoandtomato.common.utils.Strings;
import com.mygdx.potatoandtomato.models.Game;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.utils.Threadings;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 8/3/2016.
 */
public class LeaderBoardScene extends SceneAbstract {

    private HashMap<String, ScrollPane> _leaderboardScrolls;
    private Label _titleLabel, _animatingScoreLabel, _originalScoreLabel, _fakeScoreLabel;
    private Table _ranksTable, _animatingRecordTable, _mascotsTable, _loadingTable, _scoresTable, _fakeScoreTable;
    private AutoDisposeTable _iconTable;
    private float _recordHeight, _animatingTableY;
    private Game _currentShowingGame;
    private Button _nextButton, _prevButton;
    private Table _nextPrevContainer;
    private WebImage _webImage;
    private Image extinguisherImage;
    private Animator extinguisherAnimator;
    private Array<Table> plusFriendsTables;
    private Table clonePlusFriendsDetailsTable;

    public LeaderBoardScene(Services services, PTScreen screen) {
        super(services, screen);
        _leaderboardScrolls = new HashMap<String, ScrollPane>();
        plusFriendsTables = new Array();
    }

    @Override
    public void populateRoot() {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                topBar = new TopBar(_root, _texts.leaderBoardSceneTitle(), false, _assets, _screen, _services.getCoins());
                _root.align(Align.top);

                ////////////////////////
                //leaderboard root
                ////////////////////////
                Table leaderBoardRoot = new Table();
                leaderBoardRoot.align(Align.top);
                leaderBoardRoot.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.LEADER_BOARD_BG)));

                ////////////////////////////////
                //Next and prev button
                ////////////////////////////////
                Button.ButtonStyle nextButtonStyle = new Button.ButtonStyle();
                nextButtonStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.NEXT_LEADERBOARD_NORMAL));
                nextButtonStyle.down = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.NEXT_LEADERBOARD_CLICKED));
                _nextButton = new Button(nextButtonStyle);

                Button.ButtonStyle prevButtonStyle = new Button.ButtonStyle();
                prevButtonStyle.up = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.PREV_LEADERBOARD_NORMAL));
                prevButtonStyle.down = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.PREV_LEADERBOARD_CLICKED));
                _prevButton = new Button(prevButtonStyle);

                _nextPrevContainer = new Table();
                _nextPrevContainer.setSize(100, 100);
                _nextPrevContainer.setVisible(false);
                _nextPrevContainer.setPosition(50, Positions.getHeight() - 160);
                _nextPrevContainer.add(_prevButton).padRight(10);
                _nextPrevContainer.add(_nextButton);

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
                _iconTable = new AutoDisposeTable();
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
                _root.addActor(_nextPrevContainer);

                _root.add(mascotContainer).expandX().fillX().padTop(62);
                _root.row();
                _root.add(leaderBoardRoot).expand().fill();
                _root.row();

                _loadingTable = new Table();

                setInternalListeners();
            }
        });
    }

    public void showGameLeaderboard(final Game game){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _currentShowingGame = game;
                _iconTable.clear();

                _webImage = new WebImage(game.getIconUrl(), _assets, _services.getBroadcaster(), _ptGame);
                _iconTable.add(_webImage).expand().fill();
                _iconTable.addAction(sequence(fadeOut(0f), fadeIn(0.2f)));

                _titleLabel.setText(game.getName());
                _titleLabel.addAction(sequence(fadeOut(0f), fadeIn(0.2f)));

                _ranksTable.clearChildren();
                _ranksTable.clearActions();
                _ranksTable.addAction(sequence(fadeOut(0f), fadeIn(0.2f)));

                boolean found = _leaderboardScrolls.containsKey(game.getAbbr());
                if(!found){
                    Table gameRanksTable = new Table();
                    gameRanksTable.padLeft(3).padRight(3).padBottom(50);
                    gameRanksTable.setName("ranksTable");
                    ScrollPane scroll = new ScrollPane(gameRanksTable);
                    scroll.setScrollingDisabled(true, false);
                    scroll.getColor().a = 0f;

                    _leaderboardScrolls.put(game.getAbbr(), scroll);

                }

                _ranksTable.add(_leaderboardScrolls.get(game.getAbbr())).expand().fill();

                Table gameRanksTable = (Table) _leaderboardScrolls.get(game.getAbbr()).findActor("ranksTable");

                if(gameRanksTable.getChildren().size == 0){
                    Label loadingLabel = new Label(_texts.loading(), new Label.LabelStyle(
                            _assets.getFonts().get(Fonts.FontId.MYRIAD_S_ITALIC), Color.BLACK));
                    loadingLabel.setName("loadingLabel");
                    _loadingTable.clear();
                    _loadingTable.add(loadingLabel);
                    _loadingTable.setFillParent(true);
                    _loadingTable.setTouchable(Touchable.disabled);
                    _ranksTable.addActor(_loadingTable);
                }

            }
        });
    }

    public void setLeaderboardScrollPaneScrollable(final Game game, final boolean enable){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
                if(scrollPane != null){
                    scrollPane.setTouchable(enable ? Touchable.enabled : Touchable.disabled);
                }
            }
        });
    }

    public void setLoadingToUpdatingScores(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                ((Label) _loadingTable.findActor("loadingLabel")).setText(_texts.updatingScores());
            }
        });
    }

    public void showNextPrevContainer(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _nextPrevContainer.setVisible(true);
            }
        });
    }

    public void leaderboardDataLoaded(final Game game, final ArrayList<LeaderboardRecord> records){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void changeRecordTableToUnknownRank(final Game game, final int rank){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table rankTable = getRankTable(game);
                Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
                ((Label) recordTable.findActor("countLabel")).setText("-");
            }
        });

//        Table newTable = new Table();
//        Image verticalDotsImage = new Image(_assets.getTextures().get(Textures.Name.VERTICAL_DOTS));
//        newTable.add(verticalDotsImage).padTop(30).padBottom(20);
//        newTable.row();
//        newTable.add(recordTable).expandX().fillX();
//        rankTable.removeActor(recordTable);
//        rankTable.add(newTable).expandX().fillX();

    }

    private Table getRecordTable(Game game, LeaderboardRecord record, int rank){

        Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD),
                getTextColorOfRecord(record));
        Label.LabelStyle style2 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_L_BOLD), getTextColorOfRecord(record));

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
        nameScoreTable.add(nameStreakTable).expandX().fillX().padLeft(5).center();
        nameScoreTable.add(scoreLabel).right().padRight(5).padTop(2).padBottom(2).width(55);
        nameScoreTable.row();
        nameScoreTable.add(imageSeparator).colspan(2).expandX().fillX();

        ////////////////////////////////
        //record table population
        /////////////////////////////////
        Table recordTable = new Table();
        recordTable.setTransform(true);
        recordTable.padLeft(40).padRight(40);
        recordTable.add(countLabel).width(25).center();
        recordTable.add(nameScoreTable).expandX().fillX();
        recordTable.layout();

        if(_recordHeight == 0){
            recordTable.layout();
            _recordHeight = recordTable.getPrefHeight();
        }

        return recordTable;
    }

    public void populateAnimateTable(final Game game, final LeaderboardRecord record, final int rank, final boolean isLast){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
                final Table ranksTable = scrollPane.findActor("ranksTable");

                Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_M_REGULAR),
                        Color.valueOf("5b3000"));
                Label.LabelStyle style3 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_L_HEAVY), Color.valueOf("5b3000"));
                Label.LabelStyle style4 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_XL_HEAVYITALIC_B_ffffff_81562c_2), null);
                Label.LabelStyle style5 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_S_HEAVYITALIC_B_ffffff_81562c_2), null);
                Label.LabelStyle style6 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_S_HEAVYITALIC_B_ffffff_9e9d9c_2), null);

                ////////////////////////
                //Index label
                //////////////////////////
                Label countLabel = new Label((isLast ? "-" : (rank + 1) + ". ") ,style1);

                ////////////////////////
                //name score table
                ///////////////////////
                Table nameScoreTable = new Table();
                nameScoreTable.setName("nameScoreTable");
                nameScoreTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.LEADERBOARD_ANIMATING_BASE)));

                ////////////////////
                //name label
                /////////////////////
                Label nameLabel = new Label(getLeaderPlusFriendsText(record, 15), style3);


                ///////////////////////
                //score table
                /////////////////////
                _scoresTable = new Table();
                boolean useFakeScoreLabel = false;
                if(game.getLeaderboardTypeEnum() == LeaderboardType.Normal && record.getScore() != 0){
                    useFakeScoreLabel = true;
                }

                /////////////////////////////
                //original score label
                //////////////////////////////
                if(useFakeScoreLabel){
                    _fakeScoreLabel = new Label(Strings.formatNum((int) record.getScore()), style4);
                    _fakeScoreLabel.setAlignment(Align.right);
                    _fakeScoreTable = new Table();
                    _fakeScoreTable.setFillParent(true);
                    _fakeScoreTable.add(_fakeScoreLabel).expand().fill();
                    _fakeScoreTable.setVisible(false);

                    _originalScoreLabel = new Label(Strings.formatNum((int) record.getScore()), style6);
                    _originalScoreLabel.setAlignment(Align.right);
                    _scoresTable.add(_originalScoreLabel).right().expand().fill().row();

                    _scoresTable.addActor(_fakeScoreTable);
                }

                /////////////////////////
                //score label
                /////////////////////////
                _animatingScoreLabel = new Label(useFakeScoreLabel ? "0" : Strings.formatNum((int) record.getScore()),
                                        !useFakeScoreLabel ? style4 : style5);
                _animatingScoreLabel.setAlignment(Align.right);
                _scoresTable.add(_animatingScoreLabel).right().expand().fill();


                //////////////////////////////
                //populate namescore table
                ///////////////////////////
                nameScoreTable.add(nameLabel).expandX().fillX().padLeft(10).center();
                nameScoreTable.add(_scoresTable).expand().fill().padRight(10);

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
        });
    }

    public void hideLoading(final Game game){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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
                    scrollPane.setVisible(true);
                    scrollPane.getColor().a = 1f;
                }
            }
        });
    }

    public void addScore(final ScoreDetails scoreDetails, final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_animatingTableY == 0){
                    Vector2 coords = Positions.actorLocalToStageCoord(_animatingRecordTable);
                    _animatingTableY = coords.y;
                }

                Color fontColor = scoreDetails.isAddOrMultiply() ? Color.valueOf("fff600") : Color.valueOf("ff7676");

                Label.LabelStyle style5 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.CARTER_M_REGULAR_B_ffffff_000000_2), null);
                style5.font.getData().setLineHeight(13);
                style5.fontColor = fontColor;

                Label.LabelStyle style6 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.CARTER_L_REGULAR_B_ffffff_000000_2), null);
                style6.fontColor = fontColor;

                Label.LabelStyle style7 = new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.CARTER_S_REGULAR_B_ffffff_000000_2), null);
                style7.font.getData().setLineHeight(12);
                style7.fontColor = fontColor;

                ////////////////////////////////////////
                //added score label
                ///////////////////////////////////////
                final Table addedScoreTable = new Table();
                addedScoreTable.setTransform(true);
                addedScoreTable.getColor().a = 0f;

                Label addedScoreLabel = new Label((scoreDetails.isAddOrMultiply() ? "+" : "x") + (Strings.formatNum(scoreDetails.getValue())), style6);
                addedScoreLabel.setAlignment(Align.center);
                Label addedReasonLabel = new Label(scoreDetails.getReason(), style7);
                addedReasonLabel.setAlignment(Align.center);
                addedReasonLabel.setWrap(true);
                addedScoreTable.add(addedScoreLabel).expandX().fillX();
                addedScoreTable.row();
                addedScoreTable.add(addedReasonLabel).minWidth(80).expandX().fillX().padTop(-10);
                addedScoreTable.pack();

                float positionY = _animatingTableY + _recordHeight / 2 - addedScoreTable.getPrefHeight() / 2;
                if(positionY - addedScoreTable.getPrefHeight() < 5){
                    positionY = 5;
                }

                addedScoreTable.setPosition(Positions.getWidth() - addedScoreTable.getPrefWidth() - 3, positionY);

                _root.addActor(addedScoreTable);


                int totalWords = scoreDetails.getReason().split(" ").length;
                float delayDuration = Math.max(totalWords * 0.8f, 2.0f);

                addedScoreTable.setOrigin(addedScoreTable.getWidth()/2, addedScoreTable.getHeight()/2);


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

                addedScoreTable.setScale(0f, 0f);
                addedScoreTable.getColor().a = 1f;

                addedScoreTable.addAction(sequence(scaleTo(1, 1, 0.3f, Interpolation.exp5In), delay(delayDuration), fadeOut(0.1f),
                        new RunnableAction(){
                            @Override
                            public void run() {
                                addedScoreTable.remove();
                                onFinish.run();
                            }
                        }
                ));
            }
        });
    }


    public void setAnimatingScore(final double value){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _animatingScoreLabel.setText(Strings.formatNum( (int) value));
            }
        });
    }

    public void animateFakeLabelIfExist(final Runnable onFinish){
        if(_fakeScoreLabel != null) {
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {

                    Actor discardActor;
                    final Actor remainActor;
                    final float remainActorMoveY;

                    boolean isOriginalHigher = Double.valueOf(_originalScoreLabel.getText().toString().replace(",", "")) >= Double.valueOf(_animatingScoreLabel.getText().toString().replace(",", ""));
                    if(isOriginalHigher){
                        _fakeScoreLabel.setText(_originalScoreLabel.getText());
                        discardActor = _animatingScoreLabel;
                        remainActor = _originalScoreLabel;
                        remainActorMoveY = -_originalScoreLabel.getPrefHeight() / 2;
                    }
                    else{
                        _fakeScoreLabel.setText(_animatingScoreLabel.getText());
                        discardActor = _originalScoreLabel;
                        remainActor = _animatingScoreLabel;
                        remainActorMoveY = _animatingScoreLabel.getPrefHeight() / 2;
                    }


                    discardActor.addAction(sequence(fadeOut(0.2f), delay(0.8f), new RunnableAction(){
                        @Override
                        public void run() {
                            remainActor.addAction(sequence(Actions.moveBy(0, remainActorMoveY, 0.2f)));

                            _fakeScoreTable.getColor().a = 0f;
                            _fakeScoreTable.setVisible(true);
                            remainActor.addAction(fadeOut(0.4f));
                            _fakeScoreTable.addAction(sequence(fadeIn(0.4f), new RunnableAction(){
                                @Override
                                public void run() {
                                    onFinish.run();
                                }
                            }));

                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.APPEAR);

                        }
                    }));

                }
            });
        }
        else{
            onFinish.run();
        }

    }

    //current rank start from zero
    public void moveUpRank(final Game game, final int toRank, final int originalRank,
                           final LeaderboardRecord movingRecord, final int maxSize, final boolean starAnimate,
                           final Runnable finishAnimate){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");

        final Runnable onFinishMoved = new Runnable() {
            @Override
            public void run() {
                movingEndedAnimate(game, ranksTable, movingRecord, originalRank, toRank, maxSize, starAnimate, finishAnimate);
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
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    final boolean[] finishMoving = {false};
                    final Table toSwitchActor = (Table) ranksTable.getCells().get(toRank).getActor();

                    _services.getSoundsPlayer().playSoundEffectLoop(Sounds.Name.MOVING_RANK);

                    _animatingRecordTable.addAction(sequence(Actions.moveTo(toSwitchActor.getX(), toSwitchActor.getY(), finalDuration, Interpolation.exp10Out), new RunnableAction() {
                        @Override
                        public void run() {
                            finishMoving[0] = true;
                            _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.MOVING_RANK);
                            moveDownOneRank(ranksTable, toRank, originalRank, maxSize, new Runnable() {
                                @Override
                                public void run() {
                                    Threadings.delay(500, new Runnable() {
                                        @Override
                                        public void run() {

                                            if(ranksTable.getCells().size > maxSize){
                                                for(int i = maxSize; i <ranksTable.getCells().size; i++){
                                                    if(ranksTable.getCells().get(i).getActor() != null){
                                                        ranksTable.getCells().get(i).getActor().remove();
                                                    }
                                                }
                                            }

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
            nameStreakTable.align(Align.left);
            nameStreakTable.setName("nameStreakTable");
        }

        Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD),
                                        getTextColorOfRecord(record));

        Label nameLabel = new Label(Strings.cutOff(record.getLeaderName(), record.getUserIds().size() > 1 ? 16 : 18), style1);
        nameLabel.setName("nameLabel");
        nameLabel.setAlignment(Align.left);

        if(record.getUserIds().size() > 0){
            Badge badge = new Badge(BadgeType.Country, "", _assets, record.getUserCountryByUserId(record.getLeaderId()));
            nameStreakTable.add(badge).size(badge.getPrefWidth(), badge.getPrefHeight()).padRight(5);
        }

        if(record.getStreak().hasValidStreak()){
            Badge streakBadge = new Badge(BadgeType.Streak, String.valueOf(record.getStreak().getStreakCount()), _assets, "");
            streakBadge.setName("streakTable");
            nameStreakTable.add(streakBadge).padRight(5);

            if(animate){
                streakBadge.getColor().a = 0f;
                streakBadge.addAction(fadeIn(0.2f));
            }
        }

        nameStreakTable.add(nameLabel).expandX().fillX();

        if(record.getUserIds().size() > 1){
            int otherFriendsCount = record.getUserIds().size() - 1;
            String friendsCountText = "+" + otherFriendsCount;

            Table plusFriendsTable = new Table();
            plusFriendsTable.setName("collapsed");
            Image plusFriendsBg = new Image(
                    new NinePatchDrawable(_assets.getPatches().get(Patches.Name.LEADERBOARD_FRIENDS_COLLAPSED_BG)));
            plusFriendsBg.setFillParent(true);
            plusFriendsBg.setName("plusFriendsBg");
            plusFriendsBg.getColor().a = 0.5f;

            Table plusFriendsLabelTable = new Table();
            Label plusFriendsLabel = new Label(friendsCountText, style1);

            Image expandImage = new Image(_assets.getTextures().get(Textures.Name.LEADERBOARD_EXPAND_BUTTON));
            expandImage.setOrigin(Align.center);
            expandImage.setName("expandImage");
            expandImage.setRotation(90);
            expandImage.getColor().a = 0.5f;

            plusFriendsLabelTable.addActor(plusFriendsBg);
            plusFriendsLabelTable.add(plusFriendsLabel).pad(1, 3, 1, 20);

            Table plusFriendsDetailsTable = getPlusFriendsDetailsTable(record);
            plusFriendsDetailsTable.setName("plusFriendsDetailsTable");
            plusFriendsDetailsTable.pack();
            plusFriendsDetailsTable.setSize(plusFriendsDetailsTable.getPrefWidth(), plusFriendsDetailsTable.getPrefHeight());
            plusFriendsDetailsTable.setPosition(0, -plusFriendsDetailsTable.getHeight() + 1);

            plusFriendsTable.add(plusFriendsLabelTable);
            plusFriendsTable.add(expandImage).size(16, 16).padLeft(-10);
            plusFriendsTable.addActor(plusFriendsDetailsTable);

            nameStreakTable.add(plusFriendsTable).padLeft(10).right();

            plusFriendsTables.add(plusFriendsTable);

            new DummyButton(plusFriendsTable, _assets);
            setListenerForPlusFriendsTable(plusFriendsTable, record, game);
        }
        return nameStreakTable;
    }

    private Table getPlusFriendsDetailsTable(LeaderboardRecord record){
        Label.LabelStyle style1 = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_SEMIBOLD),
                             getTextColorOfRecord(record));

        Table plusFriendsDetailsTable = new Table();
        plusFriendsDetailsTable.setVisible(false);
        plusFriendsDetailsTable.pad(0, 10, 5, 10);
        plusFriendsDetailsTable.align(Align.topLeft);
        plusFriendsDetailsTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.LEADERBOARD_FRIENDS_BG)));

        for(String userId : record.getNonLeaderIds()){
            Table userTable = new Table();
            Badge badge = new Badge(BadgeType.Country, "", _assets, record.getUserCountryByUserId(userId));
            Label userLabel = new Label(Strings.cutOff(record.getUserNameByUserId(userId), 25), style1);
            userLabel.setWrap(true);
            userLabel.setAlignment(Align.left);

            userTable.add(badge).padRight(5);
            userTable.add(userLabel).expandX().fillX().width(100);

            plusFriendsDetailsTable.add(userTable).expandX().fillX().padTop(5);
            plusFriendsDetailsTable.row();
        }

        return plusFriendsDetailsTable;
    }

    public void loseStreakAnimatePhaseOne(final Game game, final int rank){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table rankTable = getRankTable(game);
                Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
                Table streakTable = recordTable.findActor("streakTable");

                Vector2 anchorPoint = Positions.actorLocalToStageCoord(streakTable);

                extinguisherImage = new Image(_assets.getTextures().get(Textures.Name.EXTINGUISHER));
                extinguisherImage.setName("extinguisherImage");
                extinguisherImage.setPosition(-40 + anchorPoint.x, 0 + anchorPoint.y);
                extinguisherImage.getColor().a = 0f;
                _root.addActor(extinguisherImage);

                extinguisherAnimator = new Animator(0.03f, _assets.getAnimations().get(Animations.Name.KILL_STREAK));
                extinguisherAnimator.setName("animator");
                extinguisherAnimator.overrideSize(70, 43);
                extinguisherAnimator.setPaused(true);
                extinguisherAnimator.setVisible(false);
                extinguisherAnimator.setPosition(-39 + anchorPoint.x, -10 + anchorPoint.y);

                _root.addActor(extinguisherAnimator);

                extinguisherImage.addAction(sequence(Actions.moveBy(-40, 0, 0f), fadeIn(0f), new RunnableAction(){
                            @Override
                            public void run() {
                                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.SWOOSH);
                            }
                        },
                        Actions.moveBy(40, 0, 0.4f, Interpolation.exp10Out), delay(1f), new RunnableAction(){
                            @Override
                            public void run() {

                                extinguisherAnimator.setPaused(false);
                                extinguisherAnimator.setVisible(true);
                                _services.getSoundsPlayer().playSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
                            }
                        }));

            }
        });
    }

    public void loseStreakAnimatePhaseTwo(final Game game, final int rank){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table rankTable = getRankTable(game);
                Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
                final Table streakTable = recordTable.findActor("streakTable");

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.STREAK_DIED);

                Threadings.delay(1000, new Runnable() {
                    @Override
                    public void run() {
                        streakTable.remove();
                        extinguisherAnimator.remove();
                        extinguisherImage.remove();
                        _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
                    }
                });


            }
        });
    }

    public void reviveStreakAnimate(final Game game, final int rank){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table rankTable = getRankTable(game);
                Table recordTable = (Table) rankTable.getCells().get(rank).getActor();
                Table streakTable = recordTable.findActor("streakTable");

                extinguisherAnimator.remove();
                extinguisherImage.remove();
                _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.STREAK);
            }
        });
    }

    private void movingEndedAnimate(final Game game, final Table ranksTable, final LeaderboardRecord movingRecord, final int fromRank,
                                            final int toRank, final int maxSize, final boolean starAnimate, final Runnable finishAnimate){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Table newRecordTable = getRecordTable(game, movingRecord, toRank);
                if(toRank >= maxSize){
                    ((Label) newRecordTable.findActor("countLabel")).setText("-");
                }
                ranksTable.getCells().get(toRank).setActor(newRecordTable);
                ranksTable.layout();

                if(starAnimate){
                    animateStarTrace(newRecordTable, finishAnimate);
                }
                else{
                    finishAnimate.run();
                }
            }
        });
    }


    private void animateStarTrace(final Table actor, final Runnable finishAnimate){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.MOVING_RANK_END);
            }
        });
    }

    public void scrollToAnimateRecord(final Game game, final int offset){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
                scrollPane.scrollTo(0, _animatingRecordTable.getY() + offset, 0, _animatingRecordTable.getHeight());
            }
        });
    }

    public void scrollToRecord(final Game game, final int rankNumber){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
                final Table ranksTable = scrollPane.findActor("ranksTable");
                scrollPane.scrollTo(0, ranksTable.getCells().get(rankNumber).getActorY() - 100, 0, ranksTable.getCells().get(rankNumber).getActorHeight());
            }
        });
    }

    public void moveDownOneRank(final Table ranksTable, final int startRank, final int endRank, final int maxSize, final Runnable onFinish){

        for(int i = startRank; i < endRank; i ++){
            final int finalI = i;
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    ranksTable.getChildren().get(finalI).addAction(moveBy(0, -_recordHeight, 0.2f));
                }
            });
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
            textColor = Color.valueOf("0d6706");
        }
        return textColor;
    }

    private Table getRankTable(Game game){
        final ScrollPane scrollPane = _leaderboardScrolls.get(game.getAbbr());
        final Table ranksTable = scrollPane.findActor("ranksTable");
        return ranksTable;
    }

    private void swapActorInTable(final Table table, final int index1, final int index2){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Actor actor1 = (Table) table.getCells().get(index1).getActor();
                Actor actor2 = (Table) table.getCells().get(index2).getActor();
                table.getCells().get(index1).setActor(null);
                table.getCells().get(index2).setActor(null);
                table.getCells().get(index1).setActor(actor2);
                table.getCells().get(index2).setActor(actor1);
            }
        });
    }

    public void setMascots(final MascotType type){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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

                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_BORED);

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

                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_FAILED);

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

                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_CRY);

                        } else if (type == MascotType.HAPPY) {
                            Animator tomato = new Animator(0.1f, _assets.getAnimations().get(Animations.Name.TOMATO_HAPPY));
                            tomato.setSize(tomato.getWidth(), tomato.getHeight());
                            tomato.setPosition(-70, 0);
                            tomato.setRotation(0);

                            Animator potato = new Animator(0.1f, _assets.getAnimations().get(Animations.Name.POTATO_HAPPY));
                            potato.setSize(potato.getWidth(), potato.getHeight());
                            potato.setPosition(0, -5);
                            potato.setRotation(0);

                            _mascotsTable.addActor(tomato);
                            _mascotsTable.addActor(potato);

                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_HAPPY);

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

                            _services.getSoundsPlayer().playSoundEffect(Sounds.Name.TOGETHER_ANTICIPATING);
                        }
                        _mascotsTable.addAction(sequence(fadeIn(0.1f)));
                    }
                }));
            }
        });
    }

    private void closePlusFriendsTable(final Table plusFriendsTable){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final Image plusFriendsBg = plusFriendsTable.findActor("plusFriendsBg");
                Image expandImage = plusFriendsTable.findActor("expandImage");
                plusFriendsTable.setName("collapsed");

                expandImage.clearActions();
                expandImage.getColor().a = 0.5f;
                expandImage.setRotation(90f);
                plusFriendsBg.setDrawable(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.LEADERBOARD_FRIENDS_COLLAPSED_BG)));
                plusFriendsBg.getColor().a = 0.5f;
                clonePlusFriendsDetailsTable.remove();

            }
        });
    }

    private void togglePlusFriendsTable(final Table plusFriendsTable, final LeaderboardRecord record, final boolean fromClick){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                boolean isExpanded = plusFriendsTable.getName().equals("expanded");

                final Image plusFriendsBg = plusFriendsTable.findActor("plusFriendsBg");
                Image expandImage = plusFriendsTable.findActor("expandImage");
                final Table plusFriendsDetailsTable = plusFriendsTable.findActor("plusFriendsDetailsTable");

                if(isExpanded){
                    plusFriendsTable.setName("collapsed");
                    expandImage.clearActions();
                    expandImage.addAction(sequence(Actions.alpha(0.5f), parallel(Actions.rotateTo(90, 0.1f), new RunnableAction(){
                        @Override
                        public void run() {
                            if(fromClick) clonePlusFriendsDetailsTable.addAction(parallel(moveBy(0, -2f, 0.1f), fadeOut(0.1f)));
                        }
                    }), new RunnableAction(){
                        @Override
                        public void run() {
                            plusFriendsBg.setDrawable(
                                    new NinePatchDrawable(_assets.getPatches().get(Patches.Name.LEADERBOARD_FRIENDS_COLLAPSED_BG)));
                            plusFriendsBg.getColor().a = 0.5f;
                        }
                    }));
                }
                else{
                    if(clonePlusFriendsDetailsTable != null){
                        clonePlusFriendsDetailsTable.addAction(parallel(moveBy(0, -2f, 0.1f), fadeOut(0.1f)));
                        for(Table findingPlusFriendsTable : plusFriendsTables){
                            if(findingPlusFriendsTable.getName().equals("expanded")){
                                togglePlusFriendsTable(findingPlusFriendsTable, record, false);
                            }
                        }
                    }

                    clonePlusFriendsDetailsTable = getPlusFriendsDetailsTable(record);
                    clonePlusFriendsDetailsTable.setTouchable(Touchable.disabled);
                    clonePlusFriendsDetailsTable.setSize(plusFriendsDetailsTable.getWidth(), plusFriendsDetailsTable.getHeight());
                    Vector2 originalPosition = Positions.actorLocalToStageCoord(plusFriendsDetailsTable);

                    clonePlusFriendsDetailsTable.setPosition(originalPosition.x, originalPosition.y);
                    if(originalPosition.y < 0){
                        clonePlusFriendsDetailsTable.setY(0);
                    }

                    plusFriendsTable.setName("expanded");
                    expandImage.clearActions();
                    expandImage.addAction(sequence(fadeIn(0f), parallel(Actions.rotateTo(0, 0.1f), new RunnableAction(){
                        @Override
                        public void run() {
                            clonePlusFriendsDetailsTable.getColor().a = 0f;
                            clonePlusFriendsDetailsTable.setVisible(true);
                            _root.addActor(clonePlusFriendsDetailsTable);
                            clonePlusFriendsDetailsTable.addAction(sequence(moveBy(0, 2f),
                                    parallel(fadeIn(0.1f), Actions.moveBy(0, -2f, 0.1f))));
                        }
                    }), new RunnableAction(){
                        @Override
                        public void run() {
                            plusFriendsBg.setDrawable(
                                    new NinePatchDrawable(_assets.getPatches().get(Patches.Name.LEADERBOARD_FRIENDS_EXPANDED_BG)));
                            plusFriendsBg.getColor().a = 1f;
                        }
                    }));
                }
            }
        });
    }

    private void setInternalListeners(){
        _root.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                for(Table plusFriendsTable : plusFriendsTables){
                    if(plusFriendsTable.getName().equals("expanded")){
                        closePlusFriendsTable(plusFriendsTable);
                    }
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    private void setListenerForPlusFriendsTable(final Table plusFriendsTable, final LeaderboardRecord record, final Game game){
        plusFriendsTable.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                togglePlusFriendsTable(plusFriendsTable, record,  true);
                event.stop();
                return true;
            }


        });
    }

    @Override
    public void dispose() {
        super.dispose();
        _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.MOVING_RANK);
        _services.getSoundsPlayer().stopSoundEffectLoop(Sounds.Name.EXTINGUISH_SOUND);
    }

    private String getLeaderPlusFriendsText(LeaderboardRecord record, int limit){
        String name = record.getLeaderName();
        if(record.getUserIds().size() > 1){
            int otherFriendsCount = record.getUserIds().size() - 1;
            name = String.format((otherFriendsCount == 1 ? _texts.leaderAndFriend() :
                    _texts.leaderAndFriends()), Strings.cutOff(name, limit - 12), otherFriendsCount);
        }
        else{
            name = Strings.cutOff(name, limit);
        }

        return name;
    }

    public enum MascotType{
        BORING, FAILED, CRY, HAPPY, ANTICIPATE
    }

    public Button getPrevButton() {
        return _prevButton;
    }

    public Button getNextButton() {
        return _nextButton;
    }

    public WebImage getWebImage() {
        return _webImage;
    }



}
