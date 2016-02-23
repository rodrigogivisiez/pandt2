package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.controls.AnimateLabel;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class BoardScreen extends GameScreen {

    private Services _services;
    private Texts _texts;
    private Table _root;
    private Table _overlayTable;
    private Table _endGameTable, _endGameRootTable;
    private Table _preStartTable;
    private Table _chessesTable;
    private Stage _stage;
    private Assets _assets;
    private boolean _paused;

    public BoardScreen(GameCoordinator gameCoordinator, Services services){
        super(gameCoordinator);
        this._services = services;
        this._texts = _services.getTexts();
        this._assets = _services.getAssets();

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                getCoordinator().getSpriteBatch());

        _root = new Table();
        _root.setFillParent(true);
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().getBackground()));
        _root.align(Align.top);

        _overlayTable = new Table();
        _overlayTable.setFillParent(true);
        _overlayTable.setBackground(new TextureRegionDrawable(_assets.getTextures().getBlackBgTrans()));
        _overlayTable.setVisible(false);

        _stage.addActor(_root);
        _stage.addActor(_overlayTable);

        getCoordinator().addInputProcessor(_stage);

    }

    public void setGraveActor(GraveyardActor graveActor){
        _stage.addActor(graveActor);
    }


    public void populateTerrains(ArrayList<TerrainLogic> terrainLogics){
        _chessesTable = new Table();
        _root.add(_chessesTable).expand().fill().padTop(60).padBottom(65).padLeft(15).padRight(15);

        int i =0;
        for(int row = 0; row < 8 ; row++){
            for(int col = 0; col < 4; col++){
                _chessesTable.add(terrainLogics.get(i).getTerrainActor()).space(2).expand().fill();
                i++;
            }
            _chessesTable.row();
        }
    }


    public void populatePreStartTable(float duration, String yellowName, String redName, final Runnable onFinish){
        _preStartTable = new Table();
        _preStartTable.setFillParent(true);

        AnimateLabel gameStartLabel = new AnimateLabel(_texts.gameStart(), _assets.getFonts().getOrangePizza5BlackS());
        gameStartLabel.setTransform(true);
        gameStartLabel.setOrigin(Align.center);
        gameStartLabel.getColor().a = 0;
        gameStartLabel.addAction(sequence(delay(0.5f), fadeIn(0.3f)));
        _preStartTable.add(gameStartLabel).expand().fill().padBottom(100);

        Label.LabelStyle nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = _assets.getFonts().getWhiteBold2BlackS();

        Table _yellowSideTable = new Table();
        _yellowSideTable.setBackground(new TextureRegionDrawable(_assets.getTextures().getYellowSide()));
        _yellowSideTable.setSize(130, 110);
        _yellowSideTable.setPosition(-130, Positions.centerY(getCoordinator().getGameHeight(), 110));
        _yellowSideTable.addAction(moveBy(140, 0, 0.3f));

        Label yellowNameLabel = new Label(yellowName, nameLabelStyle);
        yellowNameLabel.setAlignment(Align.center);
        _yellowSideTable.add(yellowNameLabel).expandX().fillX().padTop(60);

        _preStartTable.addActor(_yellowSideTable);

        Table _redSideTable = new Table();
        _redSideTable.setBackground(new TextureRegionDrawable(_assets.getTextures().getRedSide()));
        _redSideTable.setSize(130, 110);
        _redSideTable.setPosition(getCoordinator().getGameWidth(), Positions.centerY(getCoordinator().getGameHeight(), 110));
        _redSideTable.addAction(sequence(moveBy(-140, 0, 0.3f)));

        Label redNameLabel = new Label(redName, nameLabelStyle);
        redNameLabel.setAlignment(Align.center);
        _redSideTable.add(redNameLabel).expandX().fillX().padTop(60);

        _preStartTable.addActor(_redSideTable);

        Image vsImage = new Image(_assets.getTextures().getVs());
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

    public void setCanTouchChessTable(boolean canTouch){
        if(canTouch || Global.DEBUG){
            _root.setTouchable(Touchable.enabled);
        }
        else{
            _root.setTouchable(Touchable.disabled);
        }
    }

    public void populateEndGameTable(){
        _endGameRootTable = new Table();
        _endGameRootTable.setFillParent(true);
        new DummyButton(_endGameRootTable, _assets);
        _endGameTable = new Table();
        _endGameRootTable.addActor(_endGameTable);
        _stage.addActor(_endGameRootTable);
    }

    public void showEndGameTable(final boolean won){
        _endGameTable.getColor().a = 0;
        _endGameTable.setBackground(new TextureRegionDrawable(_assets.getTextures().getBlackBg()));
        _endGameTable.setSize(getCoordinator().getGameWidth(), 150);
        _endGameTable.setPosition(0, Positions.centerY(getCoordinator().getGameHeight(), 150));
        AnimateLabel label = new AnimateLabel(won ? _texts.youWin() : _texts.youLose(),
                                    won ? _assets.getFonts().getOrangePizza5BlackS() : _assets.getFonts().getGreyPizza5BlackS());
        _endGameTable.add(label).expand().fill().center();

        _endGameTable.addAction(sequence(fadeOut(0f), parallel(fadeIn(0.3f))));


    }

    public void setPaused(boolean paused, final boolean isMyTurn){
        _paused = paused;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_paused){
                    _overlayTable.setVisible(true);
                    setCanTouchChessTable(false);
                }
                else{
                    _overlayTable.setVisible(false);
                    setCanTouchChessTable(isMyTurn);
                }
            }
        });
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

    public Table getEndGameRootTable() {
        return _endGameRootTable;
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
