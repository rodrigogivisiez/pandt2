package com.potatoandtomato.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.*;
import com.sun.xml.internal.ws.api.ha.StickyFeature;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class SampleScreen extends GameScreen {

    private Table _imgTable;
    private Image _surrenderImg, _exitImg;
    private Stage _stage;
    Texture _surrenderTexture, _exitTexture;
    private int _index;
    private Assets _assets;
    private Music _currentMusic;
    private Label _label;

    public SampleScreen(GameCoordinator gameCoordinator, Assets assets) {
        super(gameCoordinator);

        _assets = assets;
        _index = 1;

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                gameCoordinator.getSpriteBatch());

        _surrenderTexture = new Texture(getCoordinator().getFileH("surrender.png"));
        _exitTexture = new Texture(getCoordinator().getFileH("exit.png"));
        _surrenderImg = new Image(_surrenderTexture);
        _exitImg = new Image(_exitTexture);

        Table table = new Table();
        table.padBottom(70);
        table.setFillParent(true);
        _imgTable = new Table();

        table.add(_imgTable).expand().fill();
        table.row();
        table.add(_surrenderImg).padTop(30).size(100, 30);
        table.row();
        table.add(_exitImg).padTop(30).size(100, 30);

        _stage.addActor(table);
        getCoordinator().addInputProcessor(_stage);


        table.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                next();
            }
        });

        _surrenderImg.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getCoordinator().abandon();
            }
        });

        _exitImg.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getCoordinator().endGame();
            }
        });

        gameCoordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String msg, String userId) {
                goTo(Integer.valueOf(msg));
            }
        });

        goTo(_index);

    }

    private void next(){
        _index++;
        if(_index > 14) _index = 1;
        getCoordinator().sendRoomUpdate(String.valueOf(_index));
    }

    private void goTo(final int i){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                _index = i;
                _imgTable.clear();

                if(_currentMusic != null){
                    _currentMusic.stop();
                }

                _currentMusic = _assets.getMusic(i);
                _currentMusic.setLooping(true);
                getCoordinator().getSoundManager().addMusic(_currentMusic);
                getCoordinator().getSoundManager().playMusic(_currentMusic);

                Image image = new Image(_assets.getTexture(i));
                image.getColor().a = 0;
                _imgTable.add(image).expandX().fillX();

                image.addAction(sequence(delay(15f), fadeIn(3f)));
            }
        });

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
        _surrenderTexture.dispose();
    }
}
