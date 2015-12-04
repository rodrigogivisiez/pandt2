package com.mygdx.potatoandtomato.abstractions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.potatoandtomato.helpers.assets.Textures;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public abstract class ScreenAbstract implements Screen {

    protected LogicAbstract _logic;
    protected Textures _textures;

    public ScreenAbstract(LogicAbstract logic) {
        _logic = logic;
        _textures = logic.getTextures();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

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
