package com.potatoandtomato.games.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 19/2/2016.
 */
public class GraveyardActor extends Table {

    private GraveyardActor _this;
    private Table _yellowGraveTable, _redGraveTable;
    private Table _yellowTotalRootTable, _redTotalRootTable;
    private Label _yellowTotalLabel, _redTotalLabel, _turnLabel, _turnCountLabel, _yellowPlayerLabel, _redPlayerLabel;
    private Label _yellowTimer, _redTimer;
    private Label _graveLabel;
    private Image _graveCloseImage, _tutorialCloseImage;
    private MyAssets _assets;
    private Texts _texts;
    private GameCoordinator _gameCoordinator;
    private Image _pointLeftImage, _pointRightImage;
    private Image _tutorialIcon, _graveIcon;
    private Image tutorialButton, graveButton;
    private Container _turnCountContainer;
    private Table _graveTable, _tutorialTable, _containerTable;
    private SoundsWrapper _soundsWrapper;

    public GraveyardActor(GameCoordinator gameCoordinator, Texts texts, MyAssets assets, SoundsWrapper soundsWrapper) {
        _this = this;
        this._assets = assets;
        this._texts = texts;
        this._soundsWrapper = soundsWrapper;
        this._gameCoordinator = gameCoordinator;

        populate();
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                new DummyButton(_this, _assets);

                Label.LabelStyle labelTotalStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR), Color.BLACK);
                Label.LabelStyle labelSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR), null);
                Label.LabelStyle labelXSmallStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XS_BlACKCONDENSEDITALIC), null);
                /////////////////////////
                //yellow total label
                ////////////////////////
                _yellowTotalRootTable = new Table();
                _yellowTotalRootTable.padLeft(10).padRight(10);
                _yellowTotalRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));

                Table yellowTotalTable = new Table();
                yellowTotalTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.YELLOW_CHESS_TOTAL)));
                _yellowTotalLabel = new Label("", labelTotalStyle);
                yellowTotalTable.add(_yellowTotalLabel).padLeft(1).padBottom(2);

                Table yellowPlayerTable = new Table();
                _yellowPlayerLabel = new Label(_gameCoordinator.getMyUniqueIndex() == 0 ? _texts.you() : _texts.enemy(), labelSmallStyle);
                _yellowPlayerLabel.setAlignment(Align.center);

                _yellowTimer = new Label("15:00", labelXSmallStyle);
                _yellowTimer.setAlignment(Align.center);
                yellowPlayerTable.add(_yellowPlayerLabel).expandX().fillX();
                yellowPlayerTable.row();
                yellowPlayerTable.add(_yellowTimer).expandX().fillX();

                _yellowTotalRootTable.add(yellowTotalTable);
                _yellowTotalRootTable.add(yellowPlayerTable).expandX().fillX().padLeft(10);

                /////////////////////////
                //red total label
                ////////////////////////
                _redTotalRootTable = new Table();
                _redTotalRootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));
                _redTotalRootTable.padLeft(10).padRight(10);

                Table redTotalTable = new Table();
                redTotalTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.RED_CHESS_TOTAL)));
                _redTotalLabel = new Label("", labelTotalStyle);
                redTotalTable.add(_redTotalLabel).padLeft(1).padBottom(2);

                Table redPlayerTable = new Table();
                _redPlayerLabel = new Label(_gameCoordinator.getMyUniqueIndex() == 1 ? _texts.you() : _texts.enemy(), labelSmallStyle);
                _redPlayerLabel.setAlignment(Align.center);

                _redTimer = new Label("15:00", labelXSmallStyle);
                _redTimer.setAlignment(Align.center);
                redPlayerTable.add(_redPlayerLabel).expandX().fillX();
                redPlayerTable.row();
                redPlayerTable.add(_redTimer).expandX().fillX();

                _redTotalRootTable.add(redPlayerTable).expandX().fillX().padRight(10);
                _redTotalRootTable.add(redTotalTable);

                /////////////////////////
                //turn icons
                ////////////////////////
                _turnLabel = new Label("", new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HELVETICA_XL_HEAVY), Color.valueOf("56380a")));
                _turnLabel.setAlignment(Align.center);

                _turnCountLabel = new Label("", new Label.LabelStyle(
                        _assets.getFonts().get(Fonts.FontId.HELVETICA_XXL_BlACKCONDENSEDITALIC_B_ffffff_56380a_1), null));
                _turnCountLabel.setOrigin(Align.center);
                _turnCountLabel.setSize(30, 20);
                _turnCountLabel.setPosition(0, 0);
                _turnCountContainer = new Container(_turnCountLabel);
                _turnCountContainer.setTransform(true);
                _turnCountContainer.setOrigin(Align.center);

                //////////////////////////////////
                //tiny icons
                ///////////////////////////////////
                tutorialButton = new Image(_assets.getTextures().get(Textures.Name.EMPTY));
                _tutorialIcon = new Image(_assets.getTextures().get(Textures.Name.TUTORIAL_ICON));
                _tutorialIcon.setPosition(_gameCoordinator.getGameWidth() - 2 - _tutorialIcon.getPrefWidth(), -13.4f);
                tutorialButton.setPosition(_tutorialIcon.getX() - _tutorialIcon.getPrefWidth()/2, _tutorialIcon.getY() - _tutorialIcon.getPrefHeight()/2 + 7);
                tutorialButton.setSize(_tutorialIcon.getPrefWidth() * 2, _tutorialIcon.getPrefHeight() * 2);

                graveButton = new Image(_assets.getTextures().get(Textures.Name.EMPTY));
                _graveIcon = new Image(_assets.getTextures().get(Textures.Name.GRAVE_ICON));
                _graveIcon.setPosition(2, -13.4f);
                graveButton.setPosition(_graveIcon.getX() - _graveIcon.getPrefWidth()/2, _graveIcon.getY() - _graveIcon.getPrefHeight()/2 + 7);
                graveButton.setSize(_graveIcon.getPrefWidth() * 2, _graveIcon.getPrefHeight() * 2);

                /////////////////////////
                //pointing icons
                ////////////////////////
                _pointLeftImage = new Image(_assets.getTextures().get(Textures.Name.POINT_LEFT_ICON));
                _pointRightImage = new Image(_assets.getTextures().get(Textures.Name.POINT_RIGHT_ICON));

                Table turnTable = new Table();
                turnTable.add(_turnLabel).expandX().fillX().colspan(3);
                turnTable.row();
                turnTable.add(_pointLeftImage).expandX().right().space(0, 10, 0, 10).height(20);
                turnTable.add(_turnCountContainer).height(20);
                turnTable.add(_pointRightImage).expandX().left().space(0, 10, 0, 10).height(20);

                ///////////////////////////
                //top info
                //////////////////////////
                Table topInfoTable = new Table();
                topInfoTable.add(_yellowTotalRootTable).padLeft(10).expandY().fillY().padTop(5).padBottom(2);
                topInfoTable.add(turnTable).expand().fill();
                topInfoTable.add(_redTotalRootTable).padRight(10).expandY().fillY().padTop(5).padBottom(2);
                topInfoTable.pad(2);
                topInfoTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));

                topInfoTable.addActor(_tutorialIcon);
                topInfoTable.addActor(tutorialButton);
                topInfoTable.addActor(_graveIcon);
                topInfoTable.addActor(graveButton);
                /////////////////////////
                //yellow grave table
                ////////////////////////
                _yellowGraveTable = new Table();
                _yellowGraveTable.align(Align.top);
                _yellowGraveTable.pad(10);
                _yellowGraveTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GRAVE_BG)));

                /////////////////////////
                //red grave table
                ////////////////////////
                _redGraveTable = new Table();
                _redGraveTable.align(Align.top);
                _redGraveTable.pad(10);
                _redGraveTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GRAVE_BG)));

                Label.LabelStyle headerLabelStyle = new Label.LabelStyle();
                headerLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXXL_REGULAR_S_000000_2_2);
                _graveLabel = new Label(_texts.graveYard(), headerLabelStyle);
                _graveLabel.setAlignment(Align.center);

                _graveCloseImage = new Image(_assets.getTextures().get(Textures.Name.CLOSE_ICON));

                Table graveHeaderTable = new Table();
                graveHeaderTable.add(_graveLabel).expandX().fillX();
                graveHeaderTable.add(_graveCloseImage).size(35, 35);

                _graveTable = new Table();
                _graveTable.setName("graveTable");
                _graveTable.pad(10);
                _graveTable.add(graveHeaderTable).expandX().fillX().colspan(2).height(40);
                _graveTable.row();
                _graveTable.add(_yellowGraveTable).expand().fill().space(3);
                _graveTable.add(_redGraveTable).expand().fill().space(3);
                _graveTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));

                //////////////////////////////////////
                //Tutorials Table
                ///////////////////////////////////////
                Label tutorialLabel = new Label(_texts.tutorial(), headerLabelStyle);
                tutorialLabel.setAlignment(Align.center);

                _tutorialCloseImage = new Image(_assets.getTextures().get(Textures.Name.CLOSE_ICON));

                Table tutorialHeaderTable = new Table();
                tutorialHeaderTable.add(tutorialLabel).expandX().fillX();
                tutorialHeaderTable.add(_tutorialCloseImage).size(35, 35);

                _tutorialTable = new Table();
                _tutorialTable.setName("tutorialTable");
                _tutorialTable.add(tutorialHeaderTable).expandX().fillX().height(40);
                _tutorialTable.row();
                _tutorialTable.add(getTutorialDesign()).expand().fill();

                _tutorialTable.pad(10);
                _tutorialTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.YELLOW_GRADIENT_BOX)));


                /////////////////////////
                //population
                ////////////////////////
                _containerTable = new Table();
                _containerTable.add(_graveTable);

                _this.add(_containerTable).expand().fill();
                _this.row();
                _this.add(topInfoTable).expandX().fillX();

                _this.setSize(_gameCoordinator.getGameWidth(), 400);
                _this.setPosition(0, _gameCoordinator.getGameHeight() - 50);
            }
        });
    }

    public Actor getTutorialDesign(){
        Table rootTable = new Table();
        rootTable.padTop(5);
        rootTable.padBottom(10);
        rootTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TUTORIAL_BG)));

        Table table = new Table();
        table.align(Align.topLeft);
        table.padLeft(10);
        table.padRight(15);

        ScrollPane scrollPane = new ScrollPane(table);

        Table chainImageTable = new Table();
        chainImageTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FOOD_CHAIN)));

        Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HANDWRITING_25_REGULAR_S_000000_1_1), Color.BLACK);
        Label label1 = new Label(_texts.tutorialAboutFoodChain(), labelStyle);
        label1.setWrap(true);
        label1.setAlignment(Align.left);

        table.add(label1).expandX().fillX();
        table.row();
        table.add(chainImageTable).center();
        for(int i = 0; i < 4; i++){
            table.row();
            table.add(getTutorialSegment(i)).expandX().fillX();
        }



        rootTable.add(scrollPane).expand().fill();
        return rootTable;
    }

    private Table getTutorialSegment(int i){
        Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.HANDWRITING_25_REGULAR_S_000000_1_1), Color.BLACK);

        Table segmentTable = new Table();
        Table contentTable = new Table();
        contentTable.align(Align.top);
        Image separator = new Image(_assets.getTextures().get(Textures.Name.GREY_HORIZONTAL_LINE));
        separator.setColor(Color.BLACK);
        segmentTable.add(separator).colspan(2).padTop(10).padBottom(10).expandX().fillX();
        segmentTable.row();
        segmentTable.add(contentTable).expandX().fillX().padLeft(15).padRight(15);


        if(i == 0){
            Image swipeImage = new Image(_assets.getTextures().get(Textures.Name.SWIPE_TO_OPEN));
            Label swipeLabel = new Label(_texts.tutorialAboutSwipeOpen(), labelStyle);
            contentTable.add(swipeImage).padRight(10);
            contentTable.add(swipeLabel).expandX().fillX().top();
        }
        else if(i == 1){
            Image dragImage = new Image(_assets.getTextures().get(Textures.Name.DRAG_TUTORIAL));
            Label dragLabel = new Label(_texts.tutorialAboutDrag(), labelStyle);
            dragLabel.setWrap(true);
            contentTable.add(dragImage);
            contentTable.row();
            contentTable.add(dragLabel).expandX().fillX().top().padTop(-10);
        }
        else if(i == 2){
            ArrayList<Pair<TextureRegion, String>> pairs = new ArrayList();
            pairs.add(new Pair<TextureRegion, String>(_assets.getTextures().getStatus(Status.POISON), _texts.tutorialAboutPoison()));
            pairs.add(new Pair<TextureRegion, String>(_assets.getTextures().getStatus(Status.DECREASE), _texts.tutorialAboutAttackDown()));
            pairs.add(new Pair<TextureRegion, String>(_assets.getTextures().getStatus(Status.VENGEFUL), _texts.tutorialAboutAttackUp()));
            pairs.add(new Pair<TextureRegion, String>(_assets.getTextures().getStatus(Status.PARALYZED), _texts.tutorialAboutParalyzed()));
            pairs.add(new Pair<TextureRegion, String>(_assets.getTextures().getStatus(Status.ANGRY), _texts.tutorialAboutAggressive()));
            pairs.add(new Pair<TextureRegion, String>(_assets.getTextures().getStatus(Status.KING), _texts.tutorialAboutKing()));

            for(Pair<TextureRegion, String> pair : pairs){
                Image statusImage = new Image(pair.getFirst());
                Label statusLabel = new Label(pair.getSecond(), labelStyle);
                statusLabel.setWrap(true);
                contentTable.add(statusImage).padRight(10).top().padTop(10);
                contentTable.add(statusLabel).expandX().fillX().top();
                contentTable.row().padBottom(10);
            }
        }
        else if(i == 3){
            Label endLabel = new Label(_texts.tutorialEnd(), labelStyle);
            endLabel.setWrap(true);
            endLabel.setAlignment(Align.center);
            contentTable.add(endLabel).expandX().fillX().top().padBottom(20);
        }


        return segmentTable;
    }

    public void modelChanged(final GraveModel graveModel){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _redTotalLabel.setText(String .valueOf(graveModel.getLeftChessCountByColor(ChessColor.RED)));
                _yellowTotalLabel.setText(String .valueOf(graveModel.getLeftChessCountByColor(ChessColor.YELLOW)));

                _yellowGraveTable.clear();
                _redGraveTable.clear();

                for(ChessType chessType : graveModel.getGraveChesses()){
                    addToGraveyard(chessType, chessType.name().startsWith("RED") ? _redGraveTable : _yellowGraveTable);
                }
            }
        });
    }

    public void setCountDownTime(final ChessColor chessColor, final String time){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label labelTime = chessColor == ChessColor.YELLOW ? _yellowTimer : _redTimer;
                labelTime.setText(time);
            }
        });
    }

    public void onBoardModelChanged(final BoardModel boardModel){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_gameCoordinator.getMyUniqueIndex() == boardModel.getCurrentTurnIndex()){
                    _turnLabel.setText(_texts.yourTurn());
                }
                else if(boardModel.getCurrentTurnIndex() != -1){
                    _turnLabel.setText(_texts.enemyTurn());
                }

                _yellowTotalRootTable.setBackground(new TextureRegionDrawable(boardModel.getCurrentTurnIndex() == 1 ?_assets.getTextures().get(Textures.Name.EMPTY) : _assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));
                //_yellowPlayerLabel.setVisible(boardModel.getCurrentTurnIndex() == 0);
                _pointLeftImage.getColor().a = boardModel.getCurrentTurnIndex() == 0 ? 1 : 0.2f;
                _redTotalRootTable.setBackground(new TextureRegionDrawable(boardModel.getCurrentTurnIndex() == 0 ?_assets.getTextures().get(Textures.Name.EMPTY) : _assets.getTextures().get(Textures.Name.TRANS_DARK_BROWN_ROUNDED_BG)));
                // _redPlayerLabel.setVisible(boardModel.getCurrentTurnIndex() == 1);
                _pointRightImage.getColor().a = boardModel.getCurrentTurnIndex() == 1 ? 1 : 0.2f;

                if(!_turnCountLabel.getText().toString().equals(String.format("%02d", boardModel.getAccTurnCount()))){
                    _turnCountContainer.getColor().a = 0f;
                    _turnCountContainer.addAction(sequence(Actions.scaleTo(1.5f, 1.5f), parallel(fadeIn(0.3f), Actions.scaleTo(1, 1, 0.3f))));
                    _turnCountLabel.setText(String.format("%02d", boardModel.getAccTurnCount()));
                    if(boardModel.isSuddenDeath()){
                        _turnCountLabel.getStyle().fontColor = Color.valueOf("ff0d0d");
                    }
                }
            }
        });
    }

    public void addToGraveyard(final ChessType chessType, final Table grave){
        Image img = new Image(_assets.getTextures().getAnimalByType(chessType));
        if(grave.getChildren().size % 3 == 0 && grave.getChildren().size !=0) {
            grave.row();
        }
        grave.add(img).uniform().space(5);
    }

    public void toggle(final boolean isGraveyard){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                boolean toggling = false;
                if(isGraveyard){
                    if(_containerTable.findActor("graveTable") != null){
                        toggling = true;
                    }
                    _containerTable.clear();
                    _containerTable.add(_graveTable).expand().fill().height(345);
                }
                else{
                    if(_containerTable.findActor("tutorialTable") != null){
                        toggling = true;
                    }
                    _containerTable.clear();
                    _containerTable.add(_tutorialTable).expand().fill().height(345);
                }
                if(_this.getName() == null || !_this.getName().equals("showed")){
                    _this.setName("showed");
                    _this.addAction(Actions.moveBy(0, -(400 - 55), 0.5f));
                    _soundsWrapper.playSounds(Sounds.Name.OPEN_SLIDE);
                }
                else{
                    if(toggling) hide();
                }
            }
        });
    }

    public void hide(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_this.getName() != null && _this.getName().equals("showed")){
                    _this.setName("hide");
                    _this.addAction(Actions.moveBy(0, (400 - 55), 0.5f));
                    _soundsWrapper.playSounds(Sounds.Name.OPEN_SLIDE);
                }
            }
        });
    }

    public Image getGraveButton() {
        return graveButton;
    }

    public Image getTutorialButton() {
        return tutorialButton;
    }

    public Image getGraveCloseImage() {
        return _graveCloseImage;
    }

    public Image getTutorialCloseImage() {
        return _tutorialCloseImage;
    }
}
