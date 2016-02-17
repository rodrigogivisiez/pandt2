package com.potatoandtomato.games.screens.loading_screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.GameScreen;
import com.potatoandtomato.games.assets.Assets;

/**
 * Created by SiongLeng on 15/2/2016.
 */
public class LoadingScreen extends GameScreen {

    private Stage _stage;
    private Table _root;
    private Assets _assets;

    public LoadingScreen(GameCoordinator gameCoordinator, Assets assets) {
        super(gameCoordinator);
        this._assets = assets;

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                gameCoordinator.getSpriteBatch());

        Image image = new Image(_assets.getLoading());
        _root = new Table();
        _root.setFillParent(true);
        _root.add(image).expand().fill();

        _stage.addActor(_root);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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
