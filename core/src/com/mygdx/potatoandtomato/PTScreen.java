package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.Fonts;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameLogic;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListLogic;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickLogic;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class PTScreen implements Screen {

    Image _bgBlueImg, _bgAutumnImg, _sunriseImg, _sunrayImg, _greenGroundImg, _autumnGroundImg;
    SceneEnum _currentScene;
    HashMap<SceneEnum, LogicAbstract> _sceneMap;
    Services _services;
    Textures _textures;
    Fonts _fonts;
    Texts _texts;
    Stage _stage;

    public PTScreen(Services services) {
        this._services = services;
        this._textures = _services.getTextures();
        this._fonts = _services.getFonts();
        this._texts = _services.getTexts();
        _currentScene = SceneEnum.NOTHING;
        _sceneMap = new HashMap<SceneEnum, LogicAbstract>();
    }

    //call this function to change scene
    public void toScene(final SceneEnum sceneEnum, final Object... objs){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                LogicAbstract logic = getSceneLogic(sceneEnum, objs);
                logic.init();
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
        });

    }

    private LogicAbstract getSceneLogic(SceneEnum sceneEnum, Object... objs){
        if(!_sceneMap.containsKey(sceneEnum)){
            LogicAbstract logic = null;
            switch (sceneEnum){
                case BOOT:
                    logic = new BootLogic(this, _services, objs);
                    break;
                case MASCOT_PICK:
                    logic = new MascotPickLogic(this, _services, objs);
                    break;
                case GAME_LIST:
                    logic = new GameListLogic(this, _services, objs);
                    break;
                case CREATE_GAME:
                    logic = new CreateGameLogic(this, _services, objs);
                    break;
                case PREREQUISITE:
                    logic = new PrerequisiteLogic(this, _services, objs);
                    break;
            }
            _sceneMap.put(sceneEnum, logic);
        }
        return _sceneMap.get(sceneEnum);
    }


    @Override
    public void show() {
        _stage = new Stage();

        //Ground Texture START////////////////////////////////////////////
        _greenGroundImg = new Image(_textures.getGreenGround());
        _autumnGroundImg = new Image(_textures.getAutumnGround());
        _autumnGroundImg.getColor().a = 0;
        _autumnGroundImg.addAction(sequence(delay(0.4f), fadeIn(0.5f)));
        //Ground Texture END//////////////////////////////////////////////

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
        _stage.addActor(_greenGroundImg);
        _stage.addActor(_autumnGroundImg);
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
