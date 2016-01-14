package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.*;
import com.potatoandtomato.games.actors.AnimateLabel;
import com.potatoandtomato.games.actors.DummyButton;
import com.potatoandtomato.games.actors.chesses.ChessActor;
import com.potatoandtomato.games.actors.chesses.enums.ChessType;
import com.potatoandtomato.games.actors.plates.PlateLogic;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.Services;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class MainScreen extends GameScreen {

    private Services _services;
    private Texts _texts;
    private Table _root;
    private Table _overlayTable;
    private Table _transitionTable;
    private Table _endGameTable;
    private Table _preStartTable;
    private Table _topInfoTable, _yellowGraveTable, _redGraveTable;
    private Table _chessesTable;
    private Stage _stage;
    private Assets _assets;
    private Table _redSideTurn, _yellowSideTurn;
    private ChessActor _yellowTotalChess, _redTotalChess;
    private Label _turnLabel;
    private MyCamera _camera;
    private boolean _topInfoExpanded;
    private Array<Drawable> _yellowGraveDrawables, _redGraveDrawables;
    private GrayScaleShader _grayScaleShader;
    private boolean _useGrayScaleShader;
    private boolean _paused;

    public MainScreen(GameCoordinator gameCoordinator, Services services){
        super(gameCoordinator);
        this._services = services;
        this._texts = _services.getTexts();
        this._assets = _services.getAssets();
        this._yellowGraveDrawables = new Array<Drawable>();
        this._redGraveDrawables = new Array<Drawable>();
        this._grayScaleShader = new GrayScaleShader();

        _camera = new MyCamera();
        _camera.setWorldBounds(0, 0, (int) getCoordinator().getGameWidth(), (int) getCoordinator().getGameHeight());
        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight(), _camera),
                getCoordinator().getSpriteBatch());

        _root = new Table();
        _root.setFillParent(true);
        _root.setBackground(new TextureRegionDrawable(_assets.getBackground()));
        _root.align(Align.top);
        _root.getColor().a = 0;

        _overlayTable = new Table();
        _overlayTable.setFillParent(true);
        _overlayTable.setBackground(new TextureRegionDrawable(_assets.getBlackBgTrans()));
        _overlayTable.setVisible(false);

        _stage.addActor(_root);
        _stage.addActor(_overlayTable);

        getCoordinator().addInputProcessor(_stage);
        setCanTouchChessTable(false);

    }

    public void fadeInScreen(float duration, final Runnable onFinish){
        _root.addAction(sequence(fadeIn(duration), new Action() {
            @Override
            public boolean act(float delta) {
                onFinish.run();
                return true;
            }
        }));
    }


    public void populateTopInfoTable(){
        _topInfoTable = new Table();
        _topInfoTable.setBackground(new NinePatchDrawable(_assets.getYellowBox()));
        _topInfoTable.pad(10);
        new DummyButton(_topInfoTable, _assets);

        _yellowTotalChess = new ChessActor(_assets);
        _yellowTotalChess.setChessType(ChessType.YELLOW);
        _yellowTotalChess.openChess(false);
        //_yellowTotalChess.setContent("14");

        _redTotalChess = new ChessActor(_assets);
        _redTotalChess.setChessType(ChessType.RED);
        _redTotalChess.openChess(false);
        //_redTotalChess.setContent("14");

        _turnLabel = new Label("", new Label.LabelStyle(_assets.getWhiteBold2BlackS(), Color.WHITE));
        _turnLabel.setAlignment(Align.center);

        Table _graveTable = new Table();
        _yellowGraveTable = new Table();
        _yellowGraveTable.align(Align.top);
        _yellowGraveTable.pad(10);
        _yellowGraveTable.setBackground(new TextureRegionDrawable(_assets.getGreyBg()));

        _redGraveTable = new Table();
        _redGraveTable.align(Align.top);
        _redGraveTable.pad(10);
        _redGraveTable.setBackground(new TextureRegionDrawable(_assets.getGreyBg()));

        Label.LabelStyle graveLabelStyle = new Label.LabelStyle();
        graveLabelStyle.font = _assets.getGreyPizza4BlackS();
        Label graveLabel = new Label(_texts.graveYard(), graveLabelStyle);
        graveLabel.setAlignment(Align.center);

        _graveTable.add(graveLabel).expandX().fillX().colspan(2);
        _graveTable.row();
        _graveTable.add(_yellowGraveTable).expand().fill().space(3);
        _graveTable.add(_redGraveTable).expand().fill().space(3);

        _topInfoTable.add(_graveTable).expand().fill().colspan(3).padBottom(10);
        _topInfoTable.row();
        _topInfoTable.add(_yellowTotalChess).padLeft(10).size(40, 40);
        _topInfoTable.add(_turnLabel).expandX().fillX();

        _topInfoTable.add(_redTotalChess).padRight(10).size(40, 40);

        _topInfoTable.setSize(getCoordinator().getGameWidth(), 400);
        _topInfoTable.setPosition(0, getCoordinator().getGameHeight() - 55);

        _stage.addActor(_topInfoTable);

    }

    public void toggleTopInfo(){

        if(!_topInfoExpanded){
            _topInfoTable.addAction(moveBy(0, -(400 - 55), 0.5f));
        }
        else{
            _topInfoTable.addAction(moveBy(0, (400 - 55), 0.5f));
        }
        _topInfoExpanded = !_topInfoExpanded;

    }

    public void populateChessTable(PlateLogic[][] plateLogics, final Runnable onFinish){
        _chessesTable = new Table();
        _root.add(_chessesTable).expand().fill().padTop(60).padBottom(65).padLeft(15).padRight(15);
        _chessesTable.getColor().a = 0;

        for(int row = 0; row < 8 ; row++){
            for(int col = 0; col < 4; col++){
                _chessesTable.add(plateLogics[col][row].getPlateActor()).space(2).expand().fill();
            }
            _chessesTable.row();
        }

        _chessesTable.addAction(sequence(fadeIn(0.3f), new Action() {
            @Override
            public boolean act(float delta) {
                onFinish.run();
                return true;
            }
        }));

    }


    public void populatePreStartTable(float duration, String yellowName, String redName, final Runnable onFinish){
        _preStartTable = new Table();
        _preStartTable.setFillParent(true);

        AnimateLabel gameStartLabel = new AnimateLabel(_texts.gameStart(), _assets.getOrangePizza5BlackS());
        gameStartLabel.setTransform(true);
        gameStartLabel.setOrigin(Align.center);
        gameStartLabel.getColor().a = 0;
        gameStartLabel.addAction(sequence(delay(0.5f), fadeIn(0.3f)));
        _preStartTable.add(gameStartLabel).expand().fill().padBottom(100);

        Label.LabelStyle nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = _assets.getWhiteBold2BlackS();

        Table _yellowSideTable = new Table();
        _yellowSideTable.setBackground(new TextureRegionDrawable(_assets.getYellowSide()));
        _yellowSideTable.setSize(130, 110);
        _yellowSideTable.setPosition(-130, Positions.centerY(getCoordinator().getGameHeight(), 110));
        _yellowSideTable.addAction(moveBy(140, 0, 0.3f));

        Label yellowNameLabel = new Label(yellowName, nameLabelStyle);
        yellowNameLabel.setAlignment(Align.center);
        _yellowSideTable.add(yellowNameLabel).expandX().fillX().padTop(60);

        _preStartTable.addActor(_yellowSideTable);

        Table _redSideTable = new Table();
        _redSideTable.setBackground(new TextureRegionDrawable(_assets.getRedSide()));
        _redSideTable.setSize(130, 110);
        _redSideTable.setPosition(getCoordinator().getGameWidth(), Positions.centerY(getCoordinator().getGameHeight(), 110));
        _redSideTable.addAction(sequence(moveBy(-140, 0, 0.3f)));

        Label redNameLabel = new Label(redName, nameLabelStyle);
        redNameLabel.setAlignment(Align.center);
        _redSideTable.add(redNameLabel).expandX().fillX().padTop(60);

        _preStartTable.addActor(_redSideTable);

        Image vsImage = new Image(_assets.getVs());
        vsImage.setSize(60, 62);
        vsImage.setPosition(Positions.centerX(getCoordinator().getGameWidth(), 60), Positions.centerY(getCoordinator().getGameHeight(), 62));

        _preStartTable.addActor(vsImage);


        _preStartTable.addAction(sequence(delay(duration), fadeOut(0.3f), new Action() {
            @Override
            public boolean act(float delta) {
                _preStartTable.remove();
                onFinish.run();
                return true;
            }
        }));


        _stage.addActor(_preStartTable);

    }


    public void setChessTotalCount(ChessType chessType, String count){
        if(chessType == ChessType.RED){
            _redTotalChess.setContent(count);
        }
        else{
            _yellowTotalChess.setContent(count);
        }
    }

    public void addToGraveyard(Drawable animalDrawable, boolean isYellow){

        Array<Drawable> drawables;
        Table grave;
        if(isYellow){
            _yellowGraveDrawables.add(animalDrawable);
            grave = _yellowGraveTable;
            drawables = _yellowGraveDrawables;
        }
        else{
            _redGraveDrawables.add(animalDrawable);
            grave = _redGraveTable;
            drawables = _redGraveDrawables;
        }

        grave.clear();
        for(Drawable d : drawables){
            Image img = new Image(d);
            if(grave.getChildren().size % 3 == 0 && grave.getChildren().size !=0) {
                grave.row();
            }
            grave.add(img).uniform().space(5);
        }

    }

    public void populateTransitionTable(){
        _transitionTable = new Table();
        _transitionTable.setBackground(new TextureRegionDrawable(_assets.getBlackBg()));
        _transitionTable.setSize(getCoordinator().getGameWidth(), 150);
        _transitionTable.setPosition(getCoordinator().getGameWidth(), Positions.centerY(getCoordinator().getGameHeight(), 150));
        _stage.addActor(_transitionTable);
    }

    public void switchTurn(boolean yellowTurn, boolean yellowIsMe, final Runnable onFinish){

        float width, height;
        width = 140;
        height = 120;

        _transitionTable.setVisible(true);

        if(!_transitionTable.hasChildren()){

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = _assets.getWhitePizza2BlackS();

            _redSideTurn = new Table();
            _redSideTurn.setBackground(new TextureRegionDrawable(_assets.getRedSide()));
            _redSideTurn.setSize(width, height);
            _redSideTurn.setPosition(Positions.centerX(_transitionTable.getWidth(), width), Positions.centerY(_transitionTable.getHeight(), height));
            _redSideTurn.setTransform(true);
            _redSideTurn.setOrigin(Align.center);
            _redSideTurn.setVisible(!yellowTurn);
            Label redTurnLabel = new Label(yellowIsMe ? _texts.enemyTurn() : _texts.yourTurn(), labelStyle);
            redTurnLabel.setAlignment(Align.center);
            _redSideTurn.add(redTurnLabel).expandX().fillX().padTop(60);

            _transitionTable.addActor(_redSideTurn);

            _yellowSideTurn = new Table();
            _yellowSideTurn.setBackground(new TextureRegionDrawable(_assets.getYellowSide()));
            _yellowSideTurn.setSize(width, height);
            _yellowSideTurn.setPosition(Positions.centerX(_transitionTable.getWidth(), width),
                                    Positions.centerY(_transitionTable.getHeight(), height));
            _yellowSideTurn.setTransform(true);
            _yellowSideTurn.setOrigin(Align.center);
            _yellowSideTurn.setVisible(yellowTurn);
            Label yellowTurnLabel = new Label(!yellowIsMe ? _texts.enemyTurn() : _texts.yourTurn(), labelStyle);
            yellowTurnLabel.setAlignment(Align.center);
            _yellowSideTurn.add(yellowTurnLabel).expandX().fillX().padTop(60);

            _transitionTable.addActor(_yellowSideTurn);
        }
        else{
            Table flipOffTable, flipOnTable;
            if(yellowTurn){
                flipOffTable = _redSideTurn;
                flipOnTable = _yellowSideTurn;

            }
            else{
                flipOnTable = _redSideTurn;
                flipOffTable = _yellowSideTurn;
            }
            flipOnTable.setScaleX(0);
            flipOnTable.setVisible(true);
            flipOffTable.setVisible(true);

            float duration = 0.3f;
            flipOffTable.addAction(sequence(delay(0.4f), Actions.scaleTo(0, 1, duration / 2)));
            flipOnTable.addAction(sequence(delay(0.4f), scaleTo(0, 1), Actions.delay(duration / 2), Actions.scaleTo(1, 1, duration / 2)));

        }

        if((yellowIsMe && yellowTurn) || (!yellowIsMe && !yellowTurn)){
            _turnLabel.setText(_texts.yourTurn());
        }
        else{
            _turnLabel.setText(_texts.enemyTurn());
        }

        _transitionTable.addAction(sequence(moveTo(getCoordinator().getGameWidth(),
                        Positions.centerY(getCoordinator().getGameHeight(), 150)),
                fadeIn(0f),
                moveBy(-getCoordinator().getGameWidth(), 0, 0.2f), delay(1.5f), fadeOut(0.5f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        _transitionTable.setVisible(false);
                        if(onFinish != null) onFinish.run();
                        return true;
                    }
                }));
    }

    public void setCanTouchChessTable(boolean canTouch){
        if(canTouch){
            _root.setTouchable(Touchable.enabled);
        }
        else{
            _root.setTouchable(Touchable.disabled);
        }
    }

    public void populateEndGameTable(){
        _endGameTable = new Table();
        _stage.addActor(_endGameTable);
    }

    public void showEndGameTable(final boolean won){

        _transitionTable.remove();
        _endGameTable.getColor().a = 0;
        _endGameTable.setBackground(new TextureRegionDrawable(_assets.getBlackBg()));
        _endGameTable.setSize(getCoordinator().getGameWidth(), 150);
        _endGameTable.setPosition(0, Positions.centerY(getCoordinator().getGameHeight(), 150));
        AnimateLabel label = new AnimateLabel(won ? _texts.youWin() : _texts.youLose(),
                                    won ? _assets.getOrangePizza5BlackS() : _assets.getGreyPizza5BlackS());
        _endGameTable.add(label).expand().fill().center();

        _endGameTable.addAction(sequence(fadeOut(0f), parallel(fadeIn(0.3f), new Action() {
            @Override
            public boolean act(float delta) {
                if(!won) _useGrayScaleShader = true;
                return true;
            }
        })));


    }

    public void setPaused(boolean paused){
        _paused = paused;
        if(_paused){
            _overlayTable.setVisible(true);
            setCanTouchChessTable(false);
        }
        else{
            _overlayTable.setVisible(false);
            setCanTouchChessTable(true);
        }

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            getCoordinator().abandon();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(253 / 255, 221 / 255, 221 / 255, 1f);

        _stage.act(delta);
        _stage.draw();
    }

    public Table getTopInfoTable() {
        return _topInfoTable;
    }

    public Table getEndGameTable() {
        return _endGameTable;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
