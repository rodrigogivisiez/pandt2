package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.Positions;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 8/7/2016.
 */
public class SplashScreen implements Screen {

    private Texture screenTexture, screenGlowTexture, controllerTexture,
                tomatoTexture, potatoTexture;
    private Music arcadeSound;
    private long soundId;

    private Image screenImage, screenGlowImage, potatoImage, tomatoImage;


    private Table rootTable, screenTable, mascotsTable;
    private Stage stage;
    private boolean disposed;

    public SplashScreen() {
        arcadeSound = Gdx.audio.newMusic(Gdx.files.internal("splash/ARCADE_BUTTON.ogg"));

        screenTexture = new Texture(Gdx.files.internal("splash/SCREEN.png"));
        screenImage = new Image(screenTexture);
        screenImage.setPosition(0, 0);

        screenGlowTexture = new Texture(Gdx.files.internal("splash/SCREEN_GLOW.png"));
        screenGlowImage = new Image(screenGlowTexture);
        screenGlowImage.getColor().a = 0f;
        screenGlowImage.setPosition(-12, 18);

        controllerTexture = new Texture(Gdx.files.internal("splash/CONTROLLER.png"));
        Image controllerImage = new Image(controllerTexture);
        controllerImage.setPosition(15, 28);

        Image controllerImage2 = new Image(controllerTexture);
        controllerImage2.setPosition(55, 28);

        potatoTexture = new Texture(Gdx.files.internal("splash/POTATO_ICON.png"));
        potatoImage = new Image(potatoTexture);
        potatoImage.setPosition(-30, -17);

        tomatoTexture = new Texture(Gdx.files.internal("splash/TOMATO_ICON.png"));
        tomatoImage = new Image(tomatoTexture);
        tomatoImage.setPosition(47, -15);

        screenTable = new Table();
        screenTable.getColor().a = 0f;
        screenTable.setSize(120, 120);
        screenTable.setPosition(Positions.getWidth() / 2 - screenTable.getWidth() / 2 + 10,
                Positions.getHeight() / 2 - screenTable.getHeight() / 2 + 10);
        screenTable.addActor(screenImage);
        screenTable.addActor(screenGlowImage);
        screenTable.addActor(controllerImage);
        screenTable.addActor(controllerImage2);

        mascotsTable = new Table();
        mascotsTable.setSize(120, 120);
        mascotsTable.getColor().a = 0f;
        mascotsTable.setPosition(Positions.getWidth() / 2 - mascotsTable.getWidth() / 2 + 10,
                Positions.getHeight() / 2 - mascotsTable.getHeight() / 2 + 10);
        mascotsTable.addActor(potatoImage);
        mascotsTable.addActor(tomatoImage);

        stage = new Stage(new StretchViewport(Positions.getWidth(), Positions.getHeight()));
        rootTable = new Table();

        rootTable.setFillParent(true);
        rootTable.addActor(screenTable);
        rootTable.addActor(mascotsTable);

        stage.addActor(rootTable);
    }

    @Override
    public void show() {

        if(Global.ENABLE_SOUND) {
            arcadeSound.play();
            arcadeSound.setVolume(0.1f);
            arcadeSound.setLooping(true);
        }

        screenTable.addAction(sequence(
                parallel(alpha(0.3f, 1f), new RunnableAction(){
                    @Override
                    public void run() {
                        if(Global.ENABLE_SOUND) arcadeSound.setVolume(0.2f);
                    }
                }) ,
                parallel(alpha(0.6f, 1f), new RunnableAction(){
                    @Override
                    public void run() {
                        if(Global.ENABLE_SOUND) arcadeSound.setVolume(0.4f);
                    }
                }),
                parallel(alpha(1f, 1f), new RunnableAction(){
                    @Override
                    public void run() {
                        if(Global.ENABLE_SOUND)  arcadeSound.setVolume(0.6f);
                    }
                })
        ));

        mascotsTable.addAction(fadeIn(1.5f));

        screenGlowImage.addAction(forever(sequence(alpha(0.9f, 1f), alpha(0.4f, 1f))));
        potatoImage.addAction(forever(sequence(rotateBy(1f, 0.2f), rotateBy(-1f, 0.2f))));
        tomatoImage.addAction(forever(sequence(rotateBy(1f, 0.1f), rotateBy(-1f, 0.1f))));
    }

    public void close(final Runnable onFinish){
        onFinish.run();
        dispose();

//        rootTable.addAction(sequence(
//                parallel(alpha(0.6f, 0), new RunnableAction(){
//                    @Override
//                    public void run() {
//                        if(Global.ENABLE_SOUND) arcadeSound.setVolume(0.3f);
//                    }
//                }),
//                parallel(alpha(0.3f, 0), new RunnableAction(){
//                    @Override
//                    public void run() {
//                        if(Global.ENABLE_SOUND) arcadeSound.setVolume(0.2f);
//                    }
//                }),
//                parallel(alpha(0f, 0), new RunnableAction(){
//                    @Override
//                    public void run() {
//                        if(Global.ENABLE_SOUND) arcadeSound.setVolume(0.1f);
//                    }
//                }), new RunnableAction(){
//                    @Override
//                    public void run() {
//
//                    }
//                }
//        ));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        if(!disposed){
            rootTable.clear();
            disposed = true;
            screenTexture.dispose();
            screenGlowTexture.dispose();
            controllerTexture.dispose();
            tomatoTexture.dispose();
            potatoTexture.dispose();
            arcadeSound.dispose();
            stage.dispose();
        }
    }
}
