package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameScreen;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.assets.Fonts;

/**
 * Created by SiongLeng on 20/7/2016.
 */
public class MainScreen extends GameScreen {

    private Assets assets;
    private Stage stage;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public MainScreen(GameCoordinator gameCoordinator, Assets assets) {
        super(gameCoordinator);
        this.assets = assets;
        spriteBatch = getCoordinator().getSpriteBatch();
    }

    @Override
    public void show() {

//        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/DroidSansFallbackFull.ttf"));
//        font = gen.generateFont(40, "ºÃÄãÂðabc", false);

        font = assets.getFonts().get(Fonts.FontId.CHINESE_XL_REGULAR);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label("adb", labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.add(label);

        stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                                        getCoordinator().getSpriteBatch());
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.draw();
        stage.act(delta);

//        spriteBatch.begin();
//        font.draw(spriteBatch, "ÄãºÃÂðabc", 10, 100);
//        spriteBatch.end();

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
