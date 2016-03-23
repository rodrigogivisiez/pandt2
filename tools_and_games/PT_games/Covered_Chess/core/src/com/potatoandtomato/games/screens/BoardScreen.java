package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
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
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.AnimateLabel;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessColor;
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
    private Table _thunderTable;
    private Table _endGameTable, _endGameRootTable;
    private Table _preStartTable;
    private Table _chessesTable;
    private Stage _stage;
    private Assets _assets;
    private boolean _paused;


    public BoardScreen(GameCoordinator gameCoordinator, Services services,
                       SplashActor splashActor, GraveyardActor graveyardActor){
        super(gameCoordinator);
        this._services = services;
        this._texts = _services.getTexts();
        this._assets = _services.getAssets();

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                getCoordinator().getSpriteBatch());

        _root = new Table();
        _root.setFillParent(true);
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.GAME_BG)));
        _root.align(Align.top);

        _overlayTable = new Table();
        _overlayTable.setFillParent(true);
        _overlayTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
        _overlayTable.setVisible(false);

        splashActor.populate();
        splashActor.setFillParent(true);

        _thunderTable = new Table();
        _thunderTable.setFillParent(true);
        _thunderTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK)));
        _thunderTable.setVisible(false);

        _stage.addActor(_root);
        _stage.addActor(graveyardActor);
        _stage.addActor(_overlayTable);
        _stage.addActor(_thunderTable);
        _stage.addActor(splashActor);

        getCoordinator().addInputProcessor(_stage);

    }

    public void thunderAnimation(){
        _thunderTable.getColor().a = 0f;
        _thunderTable.setVisible(true);

        _services.getSoundsWrapper().stopTheme();
        _services.getSoundsWrapper().playSounds(Sounds.Name.THUNDER);

        _thunderTable.addAction(sequence(delay(0.6f), fadeIn(0.05f), alpha(0.6f, 0.4f), delay(0.1f), fadeIn(0.06f), alpha(0.6f, 0.1f),
                delay(0.2f), fadeIn(0.02f), fadeOut(2f), new RunnableAction(){
                    @Override
                    public void run() {
                        _services.getSoundsWrapper().playThemeMusicSuddenD();;
                    }
                }));

        Threadings.delay(1000, new Runnable() {
            @Override
            public void run() {
                _services.getSoundsWrapper().playSounds(Sounds.Name.THUNDER);
            }
        });

    }

    public void setSuddenDeathBg(){
        _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SUDDEN_DEATH_GAME_BG)));
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

    public void showEndGameTable(final boolean won, ChessColor chessColor){
        _endGameTable.setSize(getCoordinator().getGameWidth(), 350);
        _endGameTable.setPosition(0, Positions.centerY(getCoordinator().getGameHeight(), 350));

        Image endImage;
        if(chessColor == ChessColor.RED){
            endImage = new Image(_assets.getTextures().get(won ? Textures.Name.YOU_WIN_RED : Textures.Name.YOU_LOSE_RED));
        }
        else{
            endImage = new Image(_assets.getTextures().get(won ? Textures.Name.YOU_WIN_YELLOW : Textures.Name.YOU_LOSE_YELLOW));
        }
        endImage.setOrigin(Align.center);
        _endGameTable.add(endImage).expand().fill().center();
        endImage.setScale(0, 0);
        endImage.addAction(Actions.scaleTo(1, 1, 0.3f));


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
            getCoordinator().abandon(true);
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
