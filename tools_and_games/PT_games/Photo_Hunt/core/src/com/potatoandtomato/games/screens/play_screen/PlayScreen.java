package com.potatoandtomato.games.screens.play_screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.assets.Assets;
import com.potatoandtomato.games.helpers.ImageGetter;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class PlayScreen extends GameScreen {

    private Stage _stage;
    private Assets _assets;
    private Table _root, _imageOneTable, _imageTwoTable;
    private float _imageHeight, _imageWidth;
    private Label labelDelete, labelAbandon, labelNext, labelItemsCount, labelGo;
    private TextField textNumber;

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

    public Label getLabelAbandon() {
        return labelAbandon;
    }

    public Label getLabelNext() {
        return labelNext;
    }

    public Label getLabelDelete() {
        return labelDelete;
    }

    public Label getLabelGo() {
        return labelGo;
    }

    public TextField getTextNumber() {
        return textNumber;
    }

    public Label getLabelItemsCount() {
        return labelItemsCount;
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

        Table tableLabel = new Table();
        Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().getWhiteBold3(), Color.WHITE);
        labelDelete = new Label("Delete", labelStyle);
        tableLabel.add(labelDelete).left();

        labelNext = new Label("Next", labelStyle);
        tableLabel.add(labelNext).padLeft(20).left();

        labelAbandon = new Label("Abandon", labelStyle);
        tableLabel.add(labelAbandon).padLeft(20).left();

        labelItemsCount = new Label("", labelStyle);
        tableLabel.add(labelItemsCount).padLeft(20).left();

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = new TextureRegionDrawable(_assets.getWhiteRound());
        textFieldStyle.font = _assets.getFonts().getWhiteBold3();
        textFieldStyle.fontColor = Color.BLACK;
        textNumber = new TextField("", textFieldStyle);
        textNumber.setAlignment(Align.center);
        tableLabel.add(textNumber).padLeft(20).left();

        labelGo = new Label("Go", labelStyle);
        tableLabel.add(labelGo).padLeft(20).left();


        Table tableImage = new Table();
        _imageOneTable = new Table();

        _imageTwoTable = new Table();

        tableImage.add(_imageOneTable).uniformX().expand().fill();
        tableImage.add(_imageTwoTable).uniformX().expand().fill();


        _root.add(tableLabel).expandX().fillX().height(50);
        _root.row();
        _root.add(tableImage).expand().fill().padBottom(70);
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
