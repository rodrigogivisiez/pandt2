package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameScreen;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.Announcement;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.MyAssets;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.assets.Textures;
import com.potatoandtomato.games.controls.Circle;
import com.potatoandtomato.games.controls.Cross;
import com.potatoandtomato.games.controls.DummyButton;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.helpers.Positions;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.models.SimpleRectangle;
import com.potatoandtomato.games.screens.hints.HintsActor;
import com.potatoandtomato.games.screens.review.ReviewActor;
import com.potatoandtomato.games.screens.scores.ScoresActor;
import com.potatoandtomato.games.screens.stage_counter.StageCounterActor;
import com.potatoandtomato.games.screens.time_bar.TimeActor;
import com.potatoandtomato.games.screens.user_counters.UserCountersActor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/4/2016.
 */
public class MainScreen extends GameScreen {

    private Services _services;
    private MyAssets _assets;
    private Stage _stage;
    private Table _root, _imageOneTable, _imageTwoTable,
            _imageOneInnerTable, _imageTwoInnerTable, _bottomBarTable;
    private Table _blockTable;
    private Table _endGameTable;
    private Vector2 _imageSize;
    private GameState _previousGameState;
    private GameCoordinator _coordinator;

    public MainScreen(Services services, GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        this._services = services;
        this._coordinator = gameCoordinator;
        this._assets = _services.getAssets();
        init();
    }

    public void init(){
        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                getCoordinator().getSpriteBatch());
        getCoordinator().addInputProcessor(_stage);

        _root = new Table();
        _root.setFillParent(true);
        _root.getColor().a = 0f;
        _stage.addActor(_root);

        _blockTable = new Table();
        _blockTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.TRANS_BLACK_BG)));
        _blockTable.setFillParent(true);
        new DummyButton(_blockTable, _assets);
        _blockTable.setVisible(false);
        _stage.addActor(_blockTable);

        _endGameTable = new Table();
        _endGameTable.setFillParent(true);
        _endGameTable.setVisible(false);
        new DummyButton(_endGameTable, _assets);
        _stage.addActor(_endGameTable);
    }

    public void readyToStart(){
        _root.getColor().a = 1f;
    }

    public void populate(TimeActor timeActor, HintsActor hintsActor, UserCountersActor userCountersActor, StageCounterActor stageCounterActor,
                         ScoresActor scoresActor, StageStateActor stageStateActor){

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
        topBarTable.add(timeActor).expand().fill();


        _root.add(topBarTable).expandX().fillX().height(60);

        _root.row();


        ////////////////////////////////////////
        //Image pairs
        //////////////////////////////////////////

        _imageOneTable = new Table();
        _imageOneTable.setClip(true);
        _imageOneTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));
        _imageTwoTable = new Table();
        _imageTwoTable.setClip(true);
        _imageTwoTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.FULL_BLACK_BG)));

        _imageOneInnerTable = new Table();
        _imageOneInnerTable.setTransform(true);
        _imageOneInnerTable.setName("innerTable");
        _imageTwoInnerTable = new Table();
        _imageTwoInnerTable.setTransform(true);
        _imageTwoInnerTable.setName("innerTable");

        _imageOneTable.add(_imageOneInnerTable).expand().fill();
        _imageTwoTable.add(_imageTwoInnerTable).expand().fill();

        Table imageTwoContainer = new Table();
        imageTwoContainer.setClip(true);

        Table imagesContainer = new Table();

        imagesContainer.add(_imageOneTable).expand().fill().space(6).uniform();
        imagesContainer.add(new Table()).expand().fill().uniform();

        imagesContainer.addActor(imageTwoContainer);
        imageTwoContainer.addActor(_imageTwoTable);

        _root.add(imagesContainer).expand().fill();
        _root.row();

        /////////////////////////////////////////
        //bottom bar
        ///////////////////////////////////////////

        _bottomBarTable = new Table();
        _bottomBarTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.BOTTOM_BG)));
        _bottomBarTable.align(Align.left);
        new DummyButton(_bottomBarTable, _assets);

        _bottomBarTable.add(userCountersActor).expandY().fillY().padLeft(15);

        _bottomBarTable.add(stageCounterActor).padLeft(76);
        _bottomBarTable.add(scoresActor).expand().fill();

        _root.add(_bottomBarTable).expandX().fillX().height(60);


        try{
            _stage.draw();
        }
        catch (Exception ex){

        }

        _imageSize = new Vector2(_imageOneTable.getWidth(), _imageOneTable.getHeight());

        imageTwoContainer.setSize(_imageOneTable.getWidth(), _imageOneTable.getHeight());
        imageTwoContainer.setPosition(_imageOneTable.getWidth() + 6, 0);
        _imageTwoTable.setSize(_imageOneTable.getWidth(), _imageOneTable.getHeight());

        Image topBarShadow = new Image(_assets.getTextures().get(Textures.Name.TOP_BG_SHADOW));
        topBarShadow.setSize(getCoordinator().getGameWidth(), 15);
        topBarShadow.setPosition(0, _imageSize.y - topBarShadow.getHeight() + 2);
        topBarShadow.setTouchable(Touchable.disabled);
        imagesContainer.addActor(topBarShadow);

        Image bottomBarShadow = new Image(_assets.getTextures().get(Textures.Name.BOTTOM_BG_SHADOW));
        bottomBarShadow.setSize(getCoordinator().getGameWidth(), 25);
        bottomBarShadow.setPosition(0, _bottomBarTable.getHeight() - 2);
        bottomBarShadow.setTouchable(Touchable.disabled);
        _bottomBarTable.addActor(bottomBarShadow);


        /////////////////////////////////////////
        //papyrus
        //////////////////////////////////////////
        stageStateActor.setSize(imagesContainer.getWidth(), imagesContainer.getHeight());
        Vector2 imagesContainerPosition = Positions.actorLocalToStageCoord(imagesContainer);
        stageStateActor.setPosition(0, imagesContainerPosition.y);
        stageStateActor.populate();
        _root.addActor(stageStateActor);
    }

    public void showMessages(final String msg){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _imageOneInnerTable.clear();
                _imageTwoInnerTable.clear();

                Table msgTable1 = new Table();
                msgTable1.pad(10);
                msgTable1.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_BG)));
                Table msgTable2 = new Table();
                msgTable2.pad(10);
                msgTable2.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WHITE_BG)));

                Label.LabelStyle labelStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontId.MYRIAD_M_REGULAR), Color.BLACK);

                Label label1 = new Label(msg, labelStyle);
                label1.setWrap(true);
                Label label2 = new Label(msg, labelStyle);
                label2.setWrap(true);

                msgTable1.add(label1).expandX().fillX();
                msgTable2.add(label2).expandX().fillX();

                _imageOneInnerTable.add(msgTable1).expand().fill();
                _imageTwoInnerTable.add(msgTable2).expand().fill();
            }
        });

    }

    public void setImages(final Texture texture1, final Texture texture2){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _imageOneInnerTable.clear();
                _imageTwoInnerTable.clear();

                Image image1 = new Image(texture1);
                image1.setName("image");

                Image image2 = new Image(texture2);
                image2.setName("image");

                _imageOneInnerTable.add(image1).expand().fill();
                _imageTwoInnerTable.add(image2).expand().fill();
            }
        });
    }

    public void onResumeRefreshImages(final Texture texture1, final Texture texture2){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(_imageOneInnerTable.findActor("image") != null){
                    ((Image) _imageOneInnerTable.findActor("image")).setDrawable(new TextureRegionDrawable(new TextureRegion(texture1)));
                }

                if(_imageTwoInnerTable.findActor("image") != null){
                    ((Image) _imageTwoInnerTable.findActor("image")).setDrawable(new TextureRegionDrawable(new TextureRegion(texture2)));
                }
            }
        });
    }


    public void cross(final float x, final float y, final String userId){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final Cross cross = new Cross(getCoordinator(), _services, userId);
                cross.setPosition(x - cross.getPrefWidth() / 2, y  - cross.getPrefHeight() / 2);
                cross.setSize(cross.getPrefWidth(), cross.getPrefHeight());

                _imageOneInnerTable.addActor(cross);

                cross.addAction(sequence(delay(0.6f), fadeOut(0.3f), new RunnableAction(){
                    @Override
                    public void run() {
                        cross.remove();
                    }
                }));

                for(Actor actor : _imageTwoTable.getChildren()){
                    if(actor instanceof Table){
                        Table innerTable = (Table) actor;
                        final Cross cross2 = new Cross(getCoordinator(), _services, userId);
                        cross2.setPosition(x - cross.getPrefWidth() / 2, y - cross.getPrefHeight() / 2);
                        cross2.setSize(cross.getPrefWidth(), cross.getPrefHeight());
                        innerTable.addActor(cross2);

                        cross2.addAction(sequence(delay(0.6f), fadeOut(0.3f), new RunnableAction(){
                            @Override
                            public void run() {
                                cross2.remove();
                            }
                        }));

                    }
                }

                _services.getSoundsWrapper().playSounds(Sounds.Name.WRONG);


            }
        });
    }

    public void circle(final SimpleRectangle correctRect, final String userId, final int playSoundNumber){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                Vector2 imageSize = getImageSize();
                Rectangle rectangle = new Rectangle();
                rectangle.setSize(correctRect.getWidth(), correctRect.getHeight());
                rectangle.setPosition(correctRect.getX(), imageSize.y - correctRect.getY()); //libgdx origin is at bottomleft

                Circle circle1 = new Circle(getCoordinator(), _services, userId, correctRect);
                circle1.setSize(rectangle.getWidth(), rectangle.getHeight());
                circle1.setPosition(rectangle.getX(), rectangle.getY() - rectangle.getHeight());
                circle1.getColor().a = 0f;
                circle1.addAction(fadeIn(0.1f));
                _imageOneInnerTable.addActor(circle1);


                for(Actor actor : _imageTwoTable.getChildren()){
                    if(actor instanceof Table){
                        Table innerTable = (Table) actor;
                        Circle circle2 = new Circle(getCoordinator(), _services, userId, correctRect);
                        circle2.setSize(rectangle.getWidth(), rectangle.getHeight());
                        circle2.setPosition(rectangle.getX(), rectangle.getY() - rectangle.getHeight());
                        circle2.getColor().a = 0f;
                        circle2.addAction(fadeIn(0.1f));
                        innerTable.addActor(circle2);
                    }
                }

                if(playSoundNumber != -1){
                    _services.getSoundsWrapper().playSounds(_assets.getSounds().getClickSound(playSoundNumber));
                }

            }
        });
    }

    public void unCircleAll(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(int i = _imageOneInnerTable.getChildren().size - 1; i >=0; i--){
                    Actor circle = _imageOneInnerTable.getChildren().get(i);
                    if(circle instanceof Circle){
                        circle.remove();
                    }
                }

                for(Actor actor : _imageTwoTable.getChildren()){
                    if(actor instanceof Table){
                        Table innerTable = (Table) actor;
                        for(Actor circle : innerTable.getChildren()){
                            if(circle instanceof Circle){
                                circle.remove();
                            }
                        }
                    }
                }
            }
        });
    }



    public void switchToReviewMode(ReviewActor reviewActor){
        _bottomBarTable.clear();
        _bottomBarTable.add(reviewActor).expand().fill();
    }

    public void showEndGameTable(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _endGameTable.setVisible(true);
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

    public Table getImageOneInnerTable() {
        return _imageOneInnerTable;
    }

    public Table getImageTwoInnerTable() {
        return _imageTwoInnerTable;
    }

    public Table getBlockTable() {
        return _blockTable;
    }

    public Table getBottomBarTable() {
        return _bottomBarTable;
    }

    public Table getEndGameTable() {
        return _endGameTable;
    }
}
