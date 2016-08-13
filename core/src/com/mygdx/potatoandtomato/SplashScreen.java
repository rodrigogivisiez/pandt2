package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.enums.FlurryEvent;
import com.mygdx.potatoandtomato.helpers.Flurry;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.Positions;
import com.potatoandtomato.common.assets.MyFreetypeFontLoader;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 8/7/2016.
 */
public class SplashScreen implements Screen {

    private Texture screenTexture, screenGlowTexture, controllerTexture,
                tomatoTexture, potatoTexture;
    private Music arcadeSound;
    private Sound rustySound;
    private BitmapFont font;

    private Image screenImage, screenGlowImage, potatoImage, tomatoImage;


    private Table rootTable, screenTable, mascotsTable;
    private Table workTable, bottomTextTable;
    private Stage stage;
    private AssetManager assetManager;
    private boolean disposed;

    public SplashScreen() {


    }

    @Override
    public void show() {
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                assetManager = new AssetManager();

                FileHandleResolver resolver = new InternalFileHandleResolver();
                assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
                assetManager.setLoader(BitmapFont.class, ".otf", new MyFreetypeFontLoader(resolver));

                MyFreetypeFontLoader.FreeTypeFontLoaderParameter size1Params = new MyFreetypeFontLoader.FreeTypeFontLoaderParameter();
                size1Params.fontFileName = "splash/SPLASH_FONT.otf";
                size1Params.fontParameters.size = 25;
                size1Params.fontParameters.color = Color.WHITE;
                size1Params.fontParameters.genMipMaps = true;
                size1Params.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearNearest;
                size1Params.fontParameters.magFilter = Texture.TextureFilter.Linear;
                assetManager.load("SPLASH_FONT.otf", BitmapFont.class, size1Params);

                assetManager.finishLoading();

                font = assetManager.get("SPLASH_FONT.otf", BitmapFont.class);

                arcadeSound = Gdx.audio.newMusic(Gdx.files.internal("splash/ARCADE_BUTTON.ogg"));
                rustySound = Gdx.audio.newSound(Gdx.files.internal("splash/RUSTY.ogg"));


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

                Label.LabelStyle normalLabelStyle = new Label.LabelStyle(font, Color.WHITE);
                Label.LabelStyle specialLabelStyle1 = new Label.LabelStyle(font, Color.valueOf("e44235"));
                Label.LabelStyle specialLabelStyle2 = new Label.LabelStyle(font, Color.valueOf("d2af5e"));

                Texts texts = new Texts();

                Label topLabel = new Label(texts.splashPhrase1(), normalLabelStyle);
                topLabel.setAlignment(Align.center);

                bottomTextTable = new Table();
                bottomTextTable.getColor().a = 0f;
                Label bottomLabel1 = new Label(texts.splashPhrase2(), normalLabelStyle);

                Label bottomLabel2_1 = new Label(texts.splashPhrase3(), specialLabelStyle1);
                Label bottomLabel2_2 = new Label(texts.splashPhrase4(), specialLabelStyle2);
                workTable = new Table();
                workTable.setTransform(true);
                workTable.setOrigin(Align.center);
                workTable.add(bottomLabel2_1);
                workTable.add(bottomLabel2_2);

                Label bottomLabel3 = new Label(texts.splashPhrase5(), normalLabelStyle);
                bottomLabel3.setWrap(true);
                bottomLabel3.setAlignment(Align.center);

                bottomTextTable.add(bottomLabel1).right();
                bottomTextTable.add(workTable).left();
                bottomTextTable.row();
                bottomTextTable.add(bottomLabel3).colspan(2).expandX().fillX();

                stage = new Stage(new StretchViewport(Positions.getWidth(), Positions.getHeight()));
                rootTable = new Table();

                rootTable.setFillParent(true);
                rootTable.addActor(screenTable);
                rootTable.addActor(mascotsTable);

                rootTable.add(topLabel).expandX().fillX().padBottom(200).padTop(50);
                rootTable.row();
                rootTable.add(bottomTextTable).expandX().fillX();
                stage.addActor(rootTable);
            }
        });



        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
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
                                bottomTextTable.addAction(sequence(fadeIn(0.9f), delay(1f), new RunnableAction(){
                                    @Override
                                    public void run() {
                                        if(Global.ENABLE_SOUND) {
                                            rustySound.play();
                                        }
                                        workTable.addAction(rotateBy(-10, 0.4f));
                                    }
                                }));
                            }
                        })
                ));

                mascotsTable.addAction(fadeIn(1.5f));

                screenGlowImage.addAction(forever(sequence(alpha(0.9f, 1f), alpha(0.4f, 1f))));
                potatoImage.addAction(forever(sequence(rotateBy(1f, 0.2f), rotateBy(-1f, 0.2f))));
                tomatoImage.addAction(forever(sequence(rotateBy(1f, 0.1f), rotateBy(-1f, 0.1f))));
            }
        });

    }

    public void close(final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                onFinish.run();
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            Gdx.app.exit();
        }
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
            disposed = true;
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    assetManager.dispose();
                    rootTable.clear();
                    screenTexture.dispose();
                    screenGlowTexture.dispose();
                    controllerTexture.dispose();
                    tomatoTexture.dispose();
                    potatoTexture.dispose();
                    arcadeSound.dispose();
                    rustySound.dispose();
                    stage.dispose();
                }
            });
        }
    }
}
