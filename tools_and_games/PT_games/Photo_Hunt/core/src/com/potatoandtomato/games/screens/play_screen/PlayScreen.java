package com.potatoandtomato.games.screens.play_screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.helpers.Assets;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class PlayScreen extends GameScreen {

    private Stage _stage;
    private Assets _assets;
    private Table _root, _imageOneTable, _imageTwoTable;
    private float _imageHeight, _imageWidth;

    public float getImageWidth() {
        return _imageWidth;
    }

    public float getImageHeight() {
        return _imageHeight;
    }

    public Table getImageOneTable() {
        return _imageOneTable;
    }

    public Table getImageTwoTable() {
        return _imageTwoTable;
    }

    public PlayScreen(GameCoordinator gameCoordinator, Assets assets) {
        super(gameCoordinator);
        _assets = assets;
        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                gameCoordinator.getSpriteBatch());

        _root = new Table();
        _root.setFillParent(true);
        _root.align(Align.top);
        _stage.addActor(_root);
        gameCoordinator.addInputProcessor(_stage);

        populateImageTable();
    }

    public void populateImageTable(){
        Table tableImage = new Table();
        _imageOneTable = new Table();

        _imageTwoTable = new Table();

        tableImage.add(_imageOneTable).uniformX().expand().fill();
        tableImage.add(_imageTwoTable).uniformX().expand().fill();


        _root.add(tableImage).expand().fill().padTop(50).padBottom(70);
    }

    public void setImageOne(Texture texture){
        Image imageOne = new Image(texture);
        _imageOneTable.add(imageOne).expand().fill();
    }

    public void setImageTwo(Texture texture){
        Image imageTwo = new Image(texture);
        _imageTwoTable.add(imageTwo).expand().fill();
    }

    public void drawEllipse(Rectangle rectangle){

        Image image = new Image(_assets.getCircle());
        image.setSize(rectangle.getWidth(), rectangle.getHeight());
        image.setPosition(rectangle.getX(), rectangle.getY() - rectangle.getHeight());

        Image image2  = new Image(_assets.getCircle());
        image2.setSize(rectangle.getWidth(), rectangle.getHeight());
        image2.setPosition(rectangle.getX(), rectangle.getY() - rectangle.getHeight());

        _imageOneTable.addActor(image);
        _imageTwoTable.addActor(image2);
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
        if(_imageHeight == 0) _imageHeight = _imageOneTable.getHeight();
        if(_imageWidth == 0) _imageWidth = _imageOneTable.getWidth();
    }

    public void onImageSizeCalculated(final Runnable toRun) {
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(_imageHeight != 0 && _imageWidth != 0){
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                toRun.run();
                            }
                        });
                        break;
                    }
                }
            }
        });
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
        getCoordinator().removeInputProcessor(_stage);
        _stage.dispose();
    }
}
