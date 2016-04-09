package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameScreen;
import com.potatoandtomato.common.assets.TextureAssets;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.hints.HintsActor;
import com.potatoandtomato.games.screens.review.ReviewActor;

/**
 * Created by SiongLeng on 5/4/2016.
 */
public class MainScreen extends GameScreen {

    private Services _services;
    private MyAssets _assets;
    private Stage _stage;
    private Table _root, _imageOneTable, _imageTwoTable, _imageOneInnerTable, _imageTwoInnerTable, _bottomBarTable;
    private Table _blockTable;
    private Vector2 _imageSize;

    public MainScreen(Services services, GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        this._services = services;
        this._assets = _services.getAssets();
        init();
    }

    public void init(){
        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                getCoordinator().getSpriteBatch());
        getCoordinator().addInputProcessor(_stage);

        _root = new Table();
        _root.setFillParent(true);
        _stage.addActor(_root);

        _blockTable = new Table();
        _blockTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
        _blockTable.setFillParent(true);
        new DummyButton(_blockTable, _assets);
        _blockTable.setVisible(false);
        _stage.addActor(_blockTable);

    }

    public void populate(HintsActor hintsActor){

        ////////////////////////////
        //top bar
        /////////////////////////////
        Table topBarTable = new Table();
        topBarTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TOP_BG)));
        topBarTable.align(Align.left);

        Image castleRoomImage = new Image(_assets.getTextures().get(Textures.Name.CASTLE_ROOM));
        castleRoomImage.setSize(castleRoomImage.getPrefWidth(), castleRoomImage.getPrefHeight());
        castleRoomImage.setPosition(0, 0);
        topBarTable.addActor(castleRoomImage);

        topBarTable.add(hintsActor).padTop(7);

        _root.add(topBarTable).expandX().fillX().height(60);
        _root.row();

        ////////////////////////////////////////
        //Image pairs
        //////////////////////////////////////////

        _imageOneTable = new Table();
        _imageTwoTable = new Table();

        _imageOneInnerTable = new Table();
        _imageTwoInnerTable = new Table();

        _imageOneTable.add(_imageOneInnerTable).expand().fill();
        _imageTwoTable.add(_imageTwoInnerTable).expand().fill();

        Table _imagesContainer = new Table();

        _imagesContainer.add(_imageOneTable).expand().fill().space(5);
        _imagesContainer.add(_imageTwoTable).expand().fill();

        _root.add(_imagesContainer).expand().fill();
        _root.row();

        /////////////////////////////////////////
        //bottom bar
        ///////////////////////////////////////////

        _bottomBarTable = new Table();
        _bottomBarTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BOTTOM_BG)));
        new DummyButton(_bottomBarTable, _assets);


        _root.add(_bottomBarTable).expandX().fillX().height(60);



        try{
            _stage.draw();
        }
        catch (Exception ex){

        }
        _imageSize = new Vector2(_imageOneTable.getWidth(), _imageOneTable.getHeight());

        Image topBarShadow = new Image(_assets.getTextures().get(Textures.Name.TOP_BG_SHADOW));
        topBarShadow.setSize(getCoordinator().getGameWidth(), 15);
        topBarShadow.setPosition(0, _imageSize.y - topBarShadow.getHeight() + 2);
        topBarShadow.setTouchable(Touchable.disabled);
        _imagesContainer.addActor(topBarShadow);

        Image bottomBarShadow = new Image(_assets.getTextures().get(Textures.Name.BOTTOM_BG_SHADOW));
        bottomBarShadow.setSize(getCoordinator().getGameWidth(), 25);
        bottomBarShadow.setPosition(0, _bottomBarTable.getHeight() - 2);
        bottomBarShadow.setTouchable(Touchable.disabled);
        _bottomBarTable.addActor(bottomBarShadow);


    }

    public void resetImages(Texture texture1, Texture texture2){
        _imageOneInnerTable.clear();
        _imageTwoInnerTable.clear();

        Image image1 = new Image(texture1);
        Image image2 = new Image(texture2);

        _imageOneInnerTable.add(image1).expand().fill();
        _imageTwoInnerTable.add(image2).expand().fill();
    }

    public void circle(Rectangle rectangle){
        Image image = new Image(_assets.getTextures().get(Textures.Name.CIRCLE));
        image.setSize(rectangle.getWidth(), rectangle.getHeight());
        image.setPosition(rectangle.getX(), rectangle.getY() - rectangle.getHeight());

        Image image2  = new Image(_assets.getTextures().get(Textures.Name.CIRCLE));
        image2.setSize(rectangle.getWidth(), rectangle.getHeight());
        image2.setPosition(rectangle.getX(), rectangle.getY() - rectangle.getHeight());

        _imageOneInnerTable.addActor(image);
        _imageTwoInnerTable.addActor(image2);
    }

    public void switchToReviewMode(ReviewActor reviewActor){
        _bottomBarTable.clear();
        _bottomBarTable.add(reviewActor).expand().fill();
    }

    public void refreshGameState(GameState newState){
        _blockTable.setVisible(false);
        if(newState == GameState.Blocking){
            _blockTable.setVisible(true);
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
        Gdx.gl.glClearColor(1, 1, 1, 1);

        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().update(width, height, true);
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

    public Vector2 getImageSize() {
        return _imageSize;
    }

    public Table getImageTwoTable() {
        return _imageTwoTable;
    }

    public Table getImageOneTable() {
        return _imageOneTable;
    }

    public Table getBlockTable() {
        return _blockTable;
    }

    public Table getBottomBarTable() {
        return _bottomBarTable;
    }
}
