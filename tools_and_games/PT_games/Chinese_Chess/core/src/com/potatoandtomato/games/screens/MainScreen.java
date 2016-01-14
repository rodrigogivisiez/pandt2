package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.games.helpers.Assets;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 15/1/2016.
 */
public class MainScreen extends GameScreen {

    private Services _services;
    private Stage _stage;
    private Table _root;
    private Assets _assets;

    public MainScreen(GameCoordinator gameCoordinator, Services services) {
        super(gameCoordinator);

        _services = services;
        _assets = services.getAssets();
        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()));

        _root = new Table();
        _root.setFillParent(true);
        _root.setBackground(new TextureRegionDrawable(_assets.getBackground()));
        _stage.addActor(_root);

        populate();
        getCoordinator().addInputProcessor(_stage);
    }

    public void populate(){

//        Table topInfoTable = new Table();
//        topInfoTable.setBackground(new TextureRegionDrawable(_assets.getTopBackground()));
//        topInfoTable.setSize(getCoordinator().getGameWidth(), 350);
//        topInfoTable.setPosition(0, getCoordinator().getGameHeight() - 80);
//        _stage.addActor(topInfoTable);

        Table gamePlayRoot = new Table();
        gamePlayRoot.setBackground(new TextureRegionDrawable(_assets.getChessBoardBackground()));
        _root.add(gamePlayRoot).padBottom(125).padTop(65).expand().fill();

        Table gamePlayTable = new Table();
        gamePlayTable.setBackground(new TextureRegionDrawable(_assets.getChessBoard()));
        gamePlayRoot.add(gamePlayTable).padTop(30).padBottom(30).expand().fill().padLeft(10).padRight(10);

        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8 ; col++){
                Table plateTable = new Table();
                //plateTable.setBackground(new NinePatchDrawable(_assets.getRedBox()));
                gamePlayTable.add(plateTable).uniform().fill().expand();
            }
            gamePlayTable.row();
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
