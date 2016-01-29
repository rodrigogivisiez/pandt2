package com.potatoandtomato.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class PhotoHuntScreen extends GameScreen {

    private Stage _stage;
    private Assets _assets;



    public PhotoHuntScreen(GameCoordinator gameCoordinator, Assets assets) {
        super(gameCoordinator);
        gameCoordinator.setLandscape();
        _assets = assets;

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                gameCoordinator.getSpriteBatch());

        Image image = new Image(new Texture(getCoordinator().getFileH("bb.jpg")));
        Table table = new Table();
        table.setFillParent(true);
        table.add(image).width(320).height(260);

        Image image2 = new Image(new Texture(getCoordinator().getFileH("bb.jpg")));
        table.add(image2).width(320).height(260);
        _stage.addActor(table);
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
        _stage.dispose();
    }
}
