package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameScreen;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.models.Team;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absint.ScoresListener;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.services.Texts;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;
import java.util.HashMap;

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
    private boolean _abandoning;


    public BoardScreen(GameCoordinator gameCoordinator, Services services,
                       final SplashActor splashActor, final GraveyardActor graveyardActor){
        super(gameCoordinator);
        this._services = services;
        this._texts = _services.getTexts();
        this._assets = _services.getAssets();

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void thunderAnimation(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void setSuddenDeathBg(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _root.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.SUDDEN_DEATH_GAME_BG)));
            }
        });
    }

    public void populateTerrains(final ArrayList<TerrainLogic> terrainLogics){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _root.clear();

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
        });
    }

    public void setCanTouchChessTable(final boolean canTouch){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(canTouch || (Global.DEBUG && !Global.BOT_MATCH)){
                    _root.setTouchable(Touchable.enabled);
                }
                else{
                    _root.setTouchable(Touchable.disabled);
                }
            }
        });
    }

    public void populateEndGameTable(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _endGameRootTable = new Table();
                _endGameRootTable.setFillParent(true);
                new DummyButton(_endGameRootTable, _assets);
                _endGameTable = new Table();
                _endGameRootTable.addActor(_endGameTable);
                _stage.addActor(_endGameRootTable);
            }
        });
    }

    public void showEndGameTable(final boolean won, final ChessColor chessColor){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    public void setPaused(boolean paused, final boolean isMyTurn){
        _paused = paused;
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (_paused) {
                    _overlayTable.setVisible(true);
                    setCanTouchChessTable(false);
                } else {
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
        super.render(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(253 / 255, 221 / 255, 221 / 255, 1f);

        _stage.act(delta);
        _stage.draw();
    }

    public Table getEndGameRootTable() {
        return _endGameRootTable;
    }

    public Table getRoot() {
        return _root;
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
