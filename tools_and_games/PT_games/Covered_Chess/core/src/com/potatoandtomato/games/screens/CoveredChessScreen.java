package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.*;
import com.potatoandtomato.games.actors.chesses.ChessActor;
import com.potatoandtomato.games.actors.chesses.enums.ChessColor;
import com.potatoandtomato.games.actors.plates.PlateActor;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.helpers.Texts;
import com.potatoandtomato.games.models.Services;
import javafx.geometry.Pos;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class CoveredChessScreen extends GameScreen {

    private Services _services;
    private Texts _texts;
    private Table _root;
    private Table _transitionTable;
    private Table _preStartTable;
    private Table _topInfoTable;
    private Table _chessesTable;
    private Stage _stage;
    private Assets _assets;
    private Table _redSideTurn, _yellowSideTurn;
    boolean isYellowTurn;
    final float GAME_START_DELAY = 3f;
    private ChessActor _yellowTotalChess, _redTotalChess;
    private Label _turnLabel;

    public CoveredChessScreen(GameCoordinator gameCoordinator, Services services) {
        super(gameCoordinator);
        this._services = services;
        this._texts = _services.getTexts();
        this._assets = _services.getAssets();

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()));

        _root = new Table();
        _root.setFillParent(true);
        _root.setBackground(new TextureRegionDrawable(_assets.getBackground()));
        _root.align(Align.top);

        _topInfoTable = new Table();
        _topInfoTable.setBackground(new NinePatchDrawable(_assets.getYellowBox()));
        _topInfoTable.pad(10);
        _yellowTotalChess = new ChessActor(_assets);
        _yellowTotalChess.setChessColor(ChessColor.YELLOW);
        _yellowTotalChess.setContent("14");

        _redTotalChess = new ChessActor(_assets);
        _redTotalChess.setChessColor(ChessColor.RED);
        _redTotalChess.setContent("14");

        _turnLabel = new Label(_texts.yourTurn(), new Label.LabelStyle(_assets.getWhiteBold2BlackS(), Color.WHITE));
        _turnLabel.setAlignment(Align.center);

        _topInfoTable.add(_yellowTotalChess).padLeft(10).size(40, 40);
        _topInfoTable.add(_turnLabel).expandX().fillX();
        _topInfoTable.add(_redTotalChess).padRight(10).size(40, 40);

        _transitionTable = new Table();
        _transitionTable.setBackground(new TextureRegionDrawable(_assets.getBlackBg()));
        _transitionTable.setSize(getCoordinator().getGameWidth(), 150);
        _transitionTable.setPosition(getCoordinator().getGameWidth(), Positions.centerY(getCoordinator().getGameHeight(), 150));

//        populatePreStartTable(new Runnable() {
//            @Override
//            public void run() {
//                switchTurn(false);
//            }
//        });
        populateChessTable();

        _root.add(_topInfoTable).expandX().fillX();
        _root.row();
        _root.add(_chessesTable).expand().fill().padTop(10).padBottom(65).padLeft(15).padRight(15);

        _stage.addActor(_root);
        _stage.addActor(_transitionTable);


        Image img = new Image(_assets.getVs());
        img.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isYellowTurn = !isYellowTurn;
                switchTurn(isYellowTurn);
            }
        });

        img.setSize(100, 100);
        _stage.addActor(img);

        getCoordinator().addInputProcessor(_stage);

    }

    private void populateChessTable(){
        _chessesTable = new Table();
        final int totalCols = 4, totalRows = 8;

        for(int row = 0; row < totalRows ; row++){
            for(int col = 0; col < totalCols; col++){
                _chessesTable.add(new PlateActor(_assets)).space(2).expand().fill();
            }
            _chessesTable.row();

        }

    }


    private void populatePreStartTable(final Runnable onFinish){
        _preStartTable = new Table();
        _preStartTable.setBackground(new TextureRegionDrawable(_assets.getBlackBg()));
        _preStartTable.setFillParent(true);

        Table _yellowSideTable = new Table();
        _yellowSideTable.setBackground(new TextureRegionDrawable(_assets.getYellowSide()));
        _yellowSideTable.setSize(130, 110);
        _yellowSideTable.setPosition(-130, Positions.centerY(getCoordinator().getGameHeight(), 110));
        _yellowSideTable.addAction(moveBy(140, 0, 0.3f));

        _preStartTable.addActor(_yellowSideTable);

        Table _redSideTable = new Table();
        _redSideTable.setBackground(new TextureRegionDrawable(_assets.getRedSide()));
        _redSideTable.setSize(130, 110);
        _redSideTable.setPosition(getCoordinator().getGameWidth(), Positions.centerY(getCoordinator().getGameHeight(), 110));
        _redSideTable.addAction(sequence(moveBy(-140, 0, 0.3f), delay(GAME_START_DELAY), new Action() {
            @Override
            public boolean act(float delta) {
                _preStartTable.addAction(sequence(fadeOut(0.3f), new Action() {
                    @Override
                    public boolean act(float delta) {
                        onFinish.run();
                        return true;
                    }
                }));
                return true;
            }
        }));

        _preStartTable.addActor(_redSideTable);

        Image vsImage = new Image(_assets.getVs());
        vsImage.setSize(60, 62);
        vsImage.setPosition(Positions.centerX(getCoordinator().getGameWidth(), 60), Positions.centerY(getCoordinator().getGameHeight(), 62));

        _preStartTable.addActor(vsImage);

        _stage.addActor(_preStartTable);

    }

    private void switchTurn(boolean yellowTurn){

        float width, height;
        width = 140;
        height = 120;

        if(!_transitionTable.hasChildren()){
            _redSideTurn = new Table();
            _redSideTurn.setBackground(new TextureRegionDrawable(_assets.getRedSide()));
            _redSideTurn.setSize(width, height);
            _redSideTurn.setPosition(Positions.centerX(_transitionTable.getWidth(), width), Positions.centerY(_transitionTable.getHeight(), height));
            _redSideTurn.setTransform(true);
            _redSideTurn.setOrigin(Align.center);
            _redSideTurn.setVisible(!isYellowTurn);

            _transitionTable.addActor(_redSideTurn);

            _yellowSideTurn = new Table();
            _yellowSideTurn.setBackground(new TextureRegionDrawable(_assets.getYellowSide()));
            _yellowSideTurn.setSize(width, height);
            _yellowSideTurn.setPosition(Positions.centerX(_transitionTable.getWidth(), width),
                                    Positions.centerY(_transitionTable.getHeight(), height));
            _yellowSideTurn.setTransform(true);
            _yellowSideTurn.setOrigin(Align.center);
            _yellowSideTurn.setVisible(isYellowTurn);

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

        _transitionTable.addAction(sequence(moveTo(getCoordinator().getGameWidth(),
                Positions.centerY(getCoordinator().getGameHeight(), 150)),
                fadeIn(0f),
                moveBy(-getCoordinator().getGameWidth(), 0, 0.2f), delay(1.5f), fadeOut(0.5f)));
    }


    @Override
    public void show() {





    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(253/255, 221/255, 221/255, 1f);

        _stage.act(delta);
        _stage.draw();

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
