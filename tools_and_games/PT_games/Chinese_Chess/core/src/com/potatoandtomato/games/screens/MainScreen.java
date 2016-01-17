package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.games.actors.ChessActor;
import com.potatoandtomato.games.actors.DummyImage;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.helpers.ChessType;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;
import javafx.scene.control.Tab;

import java.util.HashMap;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class MainScreen extends GameScreen {

    private Services _services;
    private Stage _stage;
    private Table _root;
    private Assets _assets;
    private Table _gamePlayTable;
    private Image _yourTurnImage, _enemyTurnImage;
    private Table _yourGraveyardTable;
    private Table _enemyGraveyardTable;
    private Table _overlayMessageTable;
    private Table _blockingOverlay;

    public Table getBlockingOverlay() {
        return _blockingOverlay;
    }

    public Table getOverlayMessageTable() {
        return _overlayMessageTable;
    }

    public MainScreen(GameCoordinator gameCoordinator, Services services) {
        super(gameCoordinator);

        _services = services;
        _assets = services.getAssets();
        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()));

        _root = new Table();
        _root.setFillParent(true);
        _root.setBackground(new TextureRegionDrawable(_assets.getBackground()));
        _stage.addActor(_root);

        _overlayMessageTable = new Table();
        _overlayMessageTable.setFillParent(true);
        new DummyImage(_overlayMessageTable, _assets);
        _overlayMessageTable.setVisible(false);
        _stage.addActor(_overlayMessageTable);

        _blockingOverlay = new Table();
        _blockingOverlay.setFillParent(true);
        _blockingOverlay.setBackground(new TextureRegionDrawable(_assets.getBlackBgTrans()));
        new DummyImage(_blockingOverlay, _assets);
        _blockingOverlay.setVisible(false);
        _stage.addActor(_blockingOverlay);

        populate();
        getCoordinator().addInputProcessor(_stage);
    }

    public void populate(){

        Table topPlayerTable = getPlayerTable(false);
        _enemyGraveyardTable = topPlayerTable.findActor("graveTable");
        _enemyTurnImage = topPlayerTable.findActor("turnImage");
        _root.add(topPlayerTable).expandX().fillX();
        _root.row();

        Table gamePlayRoot = new Table();
        gamePlayRoot.setBackground(new TextureRegionDrawable(_assets.getChessBoardBackground()));
        _root.add(gamePlayRoot).padTop(0).expand().fill();
        _root.row();

        Table bottomPlayerTable = getPlayerTable(true);
        _yourGraveyardTable = bottomPlayerTable.findActor("graveTable");
        _yourTurnImage = bottomPlayerTable.findActor("turnImage");
        _root.add(bottomPlayerTable).expandX().fillX().padBottom(65);
        _root.row();

        _gamePlayTable = new Table();
        _gamePlayTable.setBackground(new TextureRegionDrawable(_assets.getChessBoard()));
        _gamePlayTable.align(Align.topLeft);
        gamePlayRoot.add(_gamePlayTable).padTop(30).padBottom(30).padLeft(15).padRight(15).expand().fill();

    }

    private Table getPlayerTable(boolean yours){
        Table playerTable = new Table();

        Table nameTagTable = new Table();
        nameTagTable.setBackground(new NinePatchDrawable(_assets.getNameTag()));
        Image turnImage = new Image(yours ? _assets.getYourTurn() : _assets.getEnemyTurn());
        turnImage.getColor().a = 0;
        turnImage.setName("turnImage");
        nameTagTable.add(turnImage).padBottom(3);

        Table graveTable = new Table();
        graveTable.align(!yours ? Align.topLeft : Align.topRight);
        graveTable.setName("graveTable");

        if(!yours){
            playerTable.add(graveTable).padTop(5).padBottom(5).padLeft(5).expand().fill();
            playerTable.add(nameTagTable).height(53).pad(5).width(130);
        }
        else{
            playerTable.add(nameTagTable).height(53).pad(5).width(130);
            playerTable.add(graveTable).padTop(5).padBottom(5).padRight(5).expand().fill();
        }


        return playerTable;
    }

    public void setRootCanTouch(boolean canTouch){
        if(canTouch) _root.setTouchable(Touchable.enabled);
        else _root.setTouchable(Touchable.disabled);
    }

    public void clearGraveTable(){
        _enemyGraveyardTable.clear();
        _yourGraveyardTable.clear();
    }

    public void showJiangJun(boolean red){
        final Image image;
        image = new Image(red ? _assets.getRedJiangJun() : _assets.getBlackJiangJun());
        image.getColor().a = 0;
        _overlayMessageTable.clear();
        _overlayMessageTable.add(image).size(150, 300);
        _overlayMessageTable.setVisible(true);
        image.addAction(sequence(fadeIn(0.1f), delay(1f), fadeOut(0.1f), new Action() {
            @Override
            public boolean act(float delta) {
                _overlayMessageTable.setVisible(false);
                return true;
            }
        }));
    }

    public void showWinLose(boolean win){
        final Image image;
        image = new Image(win ? _assets.getYouWin() : _assets.getYouLose());
        image.getColor().a = 0;
        _overlayMessageTable.clear();
        _overlayMessageTable.add(image).size(150, 300);
        _overlayMessageTable.setVisible(true);
        image.addAction(sequence(fadeIn(0.1f), delay(1f)));

        new DummyImage(_overlayMessageTable, _assets);
    }

    public void addToGraveTable(ChessType chessType, boolean yours){
        ChessActor chessActor = new ChessActor(chessType, _assets, 0, 0, 20, 13);

        if(yours){
            _yourGraveyardTable.add(chessActor).size(26, 26);;
            if(_yourGraveyardTable.getChildren().size == 8) _yourGraveyardTable.row();
        }
        else{
            _enemyGraveyardTable.add(chessActor).size(26, 26);;
            if(_enemyGraveyardTable.getChildren().size == 8) _enemyGraveyardTable.row();
        }
    }

    public Table[][] populateChesses(ChessType[][] chessTypes){

        Table[][] plates = new Table[9][10];

        for(int row = 8; row >= 0; row--){
            for(int col = 0; col < 9 ; col++){
                Table plateTable = new Table();
                _gamePlayTable.add(plateTable).padBottom(1).width(0.3f).padLeft(col > 0 ? 41f : 0).height(41.8f);

                Table chessBoxTable = new Table();
                chessBoxTable.setSize(30, 30);
                chessBoxTable.setPosition(-15, -15);
                new DummyImage(chessBoxTable, _assets);
                plateTable.addActor(chessBoxTable);

                if(chessTypes[col][row] != ChessType.EMPTY){

                    ChessActor chessActor = new ChessActor(chessTypes[col][row], _assets, col, row, 0, 0);
                    chessActor.setName("chessActor");
                    chessBoxTable.add(chessActor).size(40, 40);
                    chessBoxTable.setName(chessTypes[col][row].name());
                }

                plates[col][row] = chessBoxTable;


                if(row == 8){
                    Table chessBoxTable2 = new Table();
                    chessBoxTable2.setSize(30, 30);
                    chessBoxTable2.setPosition(-15, 27);
                    new DummyImage(chessBoxTable2, _assets);
                    plateTable.addActor(chessBoxTable2);

                    if(chessTypes[col][row + 1] != ChessType.EMPTY){
                        ChessActor chessActor = new ChessActor(chessTypes[col][row + 1], _assets, col, row + 1, 0, 0);
                        chessActor.setName("chessActor");
                        chessBoxTable2.add(chessActor).size(40, 40);
                        chessBoxTable2.setName(chessTypes[col][row + 1].name());
                    }
                    plates[col][row + 1] = chessBoxTable2;
                }
            }
            _gamePlayTable.row();
        }

        return plates;
    }

    public void switchTurn(boolean yoursTurn){
        Image fadeIn = _yourTurnImage;
        Image fadeOut = _enemyTurnImage;
        if(!yoursTurn){
            fadeIn = _enemyTurnImage;
            fadeOut = _yourTurnImage;
        }

        fadeIn.addAction(fadeIn(0.3f));
        fadeOut.addAction(Actions.fadeOut(0.3f));

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
        Gdx.gl.glClearColor(253/255, 221/255, 221/255, 1f);


        _stage.act();
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
