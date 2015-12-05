package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.abstractions.LogicAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.assets.Fonts;
import com.mygdx.potatoandtomato.helpers.assets.Textures;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Sizes;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.mygdx.potatoandtomato.scenes.shared_actors.BtnEggUpright;
import com.mygdx.potatoandtomato.scenes.social_login_scene.SocialLoginLogic;
import javafx.geometry.Pos;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class PTScreen implements Screen {

    Image _bgBlueImg, _bgAutumnImg, _sunriseImg, _sunrayImg;
    SceneEnum _currentScene;
    HashMap<SceneEnum, LogicAbstract> _sceneMap;
    Textures _textures;
    Fonts _fonts;
    Stage _stage;

    public PTScreen(Textures textures, Fonts fonts) {
        this._textures = textures;
        this._fonts = fonts;
        _currentScene = SceneEnum.NOTHING;
        _sceneMap = new HashMap<SceneEnum, LogicAbstract>();
    }

    //call this function to change scene
    public void toScene(SceneEnum sceneEnum){
        LogicAbstract logic = getSceneLogic(sceneEnum);
        Actor transitionInRoot = logic.getScene().getRoot();

        if(_currentScene == SceneEnum.NOTHING){
            _stage.addActor(transitionInRoot);
        }
        else{
            final Actor originalRoot = getSceneLogic(_currentScene).getScene().getRoot(); //remove original root
            transitionInRoot.setPosition(Positions.getWidth(), 0);
            _stage.addActor(transitionInRoot);
            float duration = 0.5f;
            transitionInRoot.addAction(moveTo(0, 0, duration));
            originalRoot.addAction(sequence(moveBy(-Positions.getWidth(), 0, duration), new Action() {
                @Override
                public boolean act(float delta) {
                    originalRoot.remove();
                    return false;
                }
            }));

        }

        _currentScene = sceneEnum;
    }

    private LogicAbstract getSceneLogic(SceneEnum sceneEnum){
        if(!_sceneMap.containsKey(sceneEnum)){
            LogicAbstract logic = null;
            switch (sceneEnum){
                case BOOT:
                    logic = new BootLogic(this, _textures, _fonts);
                    break;
                case SOCIAL_LOGIN:
                    logic = new SocialLoginLogic(this, _textures, _fonts);
                    break;
            }
            _sceneMap.put(sceneEnum, logic);
        }
        return _sceneMap.get(sceneEnum);
    }


    @Override
    public void show() {
        _stage = new Stage();

        //Background Texture START
        _bgBlueImg = new Image(_textures.getBlueBg());
        _bgBlueImg.setSize(Positions.getWidth(), Positions.getHeight());

        _bgAutumnImg = new Image(_textures.getAutumnBg());
        _bgAutumnImg.setSize(Positions.getWidth(), Positions.getHeight());
        _bgAutumnImg.getColor().a = 0;

        _sunriseImg = new Image(_textures.getSunrise());
        _sunriseImg.getColor().a = 0;

        _sunrayImg = new Image(_textures.getSunray());
        _sunrayImg.setPosition(Positions.centerX(1200), -470);
        _sunrayImg.setOrigin(599f, 601f);
        _sunrayImg.setSize(1200, 1200);
        _sunrayImg.getColor().a = 0;

        _sunriseImg.addAction(fadeIn(0.5f));
        _bgAutumnImg.addAction(sequence(fadeIn(0.5f), new Action() {
            @Override
            public boolean act(float delta) {
                _sunrayImg.addAction(parallel(
                        fadeIn(1f),
                        forever(rotateBy(3, 0.15f))
                ));
                return true;
            }
        }));
        //Background Texture END

        _stage.addActor(_bgBlueImg);
        _stage.addActor(_bgAutumnImg);
        _stage.addActor(_sunrayImg);
        _stage.addActor(_sunriseImg);
        Gdx.input.setInputProcessor(_stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        _stage.act(delta);
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

    }
}
