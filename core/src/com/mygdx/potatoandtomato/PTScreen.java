package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Sounds;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.scenes.shop_scene.ShopLogic;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.services.Texts;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameLogic;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListLogic;
import com.mygdx.potatoandtomato.scenes.game_sandbox_scene.GameSandboxLogic;
import com.mygdx.potatoandtomato.scenes.input_name_scene.InputNameLogic;
import com.mygdx.potatoandtomato.scenes.invite_scene.InviteLogic;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.MultipleGamesLeaderBoardLogic;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.SingleGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.mygdx.potatoandtomato.scenes.room_scene.RoomLogic;
import com.mygdx.potatoandtomato.scenes.settings_scene.SettingsLogic;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.assets.Assets;

import java.util.Stack;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class PTScreen implements Screen, InputProcessor {

    PTGame _ptGame;
    Image _bgBlueImg, _bgAutumnImg, _sunriseImg, _sunrayImg, _greenGroundImg, _autumnGroundImg;
    Services _services;
    Assets _assets;
    Texts _texts;
    Stage _stage;
    OrthographicCamera _camera;
    Stack<LogicEnumPair> _logicStacks;
    boolean _backRunning;
    Actor _currentRoot;
    boolean _isPTScreen;


    public PTScreen(PTGame ptGame, Services services) {
        this._ptGame = ptGame;
        this._services = services;
        this._assets = _services.getAssets();
        this._texts = _services.getTexts();
        this._logicStacks = new Stack();
        this._isPTScreen = true;
        init();
    }

    public void switchToGameScreen(){
        _ptGame.removeInputProcessor(_stage);
        _ptGame.removeInputProcessor(this);
        _isPTScreen = false;
    }

    public void switchToPTScreen(){
        if(!_isPTScreen){
            _isPTScreen = true;
            _ptGame.addInputProcessor(_stage);
            _ptGame.addInputProcessor(this);
            _ptGame.setScreen(this);
        }
    }

    //call this function to change scene
    public void toScene(final SceneEnum sceneEnum, final Object... objs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                final LogicAbstract logic = newSceneLogic(sceneEnum, objs);
                toScene(logic, sceneEnum, objs);
            }
        });
    }

    //call this function to change scene
    public void toScene(final LogicAbstract logic, final SceneEnum sceneEnum, final Object... objs){
        logic.onInit();
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (_logicStacks.size() == 0) {
                    logic.onShow();
                    logic.onShown();
                    _stage.addActor(logic.getScene().getRoot());
                    _currentRoot = logic.getScene().getRoot();
                } else {
                    final LogicEnumPair logicOut = _logicStacks.peek();
                    logicOut.getLogic().onHide();
                    logicOut.getLogic().onChangedScene(sceneEnum);
                    logic.onShow();
                    if (!logicOut.getLogic().isSaveToStack()) {
                        _logicStacks.remove(logicOut);
                    }
                    sceneTransition(logic.getScene().getRoot(), logicOut.getLogic().getScene().getRoot(), logic.getScene(), true, new Runnable() {
                        @Override
                        public void run() {
                            if (!logicOut.getLogic().isSaveToStack()) {
                                logicOut.getLogic().dispose();
                            }
                            logic.onShown();
                        }
                    });
                }
                _logicStacks.push(new LogicEnumPair(logic, sceneEnum, objs));

            }
        });
    }

    public void back(){

        final LogicEnumPair currentScene = _logicStacks.peek();
        if(currentScene.getLogic().getScene().getRoot().getActions().size > 0){
            return;
        }

        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {

                _logicStacks.peek().getLogic().onQuit(new OnQuitListener() {
                    @Override
                    public void onResult(Result result) {
                        if (result == Result.YES) {
                            if (_logicStacks.size() == 1) {
                                confirmQuitGame();
                                return;
                            }


                            final LogicEnumPair current = _logicStacks.pop();
                            final LogicEnumPair previous = _logicStacks.peek();
                            current.getLogic().onHide();
                            current.getLogic().onChangedScene(previous.getSceneEnum());

                            previous.getLogic().onShow();
                            sceneTransition(previous.getLogic().getScene().getRoot(), current.getLogic().getScene().getRoot(),
                                    previous.getLogic().getScene(), false, new Runnable() {
                                        @Override
                                        public void run() {
                                            current.getLogic().dispose();
                                            previous.getLogic().onShown();
                                        }
                                    });
                        } else {
                        }
                    }
                });
            }
        });
    }

    public void confirmQuitGame(){
        _services.getConfirm().show(ConfirmIdentifier.QuitGame,
                _texts.confirmQuit(), Confirm.Type.YESNO, new ConfirmResultListener() {
            @Override
            public void onResult(Result result) {
                if(result == Result.YES){
                    Gdx.app.exit();
                }
            }
        });
    }

    public void backToBoot(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                while(_logicStacks.size() > 0){
                    LogicEnumPair logicEnumPair = _logicStacks.pop();
                    logicEnumPair.getLogic().getScene().getRoot().remove();
                    logicEnumPair.getLogic().onHide();
                    logicEnumPair.getLogic().dispose();
                }
                toScene(SceneEnum.BOOT);
            }
        });
    }

    public LogicAbstract newSceneLogic(SceneEnum sceneEnum, Object... objs){
        LogicAbstract logic = null;
        switch (sceneEnum){
            case BOOT:
                logic = new BootLogic(this, _services, objs);
                break;
            case INPUT_NAME:
                logic = new InputNameLogic(this, _services, objs);
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
            case ROOM:
                logic = new RoomLogic(this, _services, objs);
                break;
            case SETTINGS:
                logic = new SettingsLogic(this, _services, objs);
                break;
            case INVITE:
                logic= new InviteLogic(this, _services, objs);
                break;
            case GAME_SANDBOX:
                logic = new GameSandboxLogic(this, _services, objs);
                break;
            case END_GAME_LEADER_BOARD:
                logic = new EndGameLeaderBoardLogic(this, _services, objs);
                break;
            case SINGLE_GAME_LEADER_BOARD:
                logic = new SingleGameLeaderBoardLogic(this, _services, objs);
                break;
            case MULTIPLE_GAMES_LEADER_BOARD:
                logic = new MultipleGamesLeaderBoardLogic(this, _services, objs);
                break;
            case SHOP:
                logic = new ShopLogic(this, _services, objs);
                break;
        }
        return logic;
    }

    private void sceneTransition(final Actor _rootIn, final Actor _rootOut, final SceneAbstract sceneToShow, final boolean toRight, final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                float duration = 0.5f;

                Threadings.renderFor(10f);

                _rootIn.remove();
                _rootOut.remove();
                _rootIn.clearActions();
                _rootOut.clearActions();
                _rootIn.setName("root");
                _stage.addActor(_rootIn);
                _stage.addActor(_rootOut);
                _currentRoot = _rootIn;

                _rootIn.setPosition(toRight ? Positions.getWidth() : -Positions.getWidth(), 0);
                _rootOut.setPosition(0, 0);

                _services.getSoundsPlayer().playSoundEffect(Sounds.Name.SLIDING);

                _rootIn.addAction(sequence(moveTo(0, 0, duration)));
                _rootOut.addAction(sequence(moveBy(toRight ? -Positions.getWidth() : Positions.getWidth(), 0, duration), new RunnableAction() {
                    @Override
                    public void run() {
                        _rootOut.remove();
                        onFinish.run();
                    }
                }));
            }
        });
    }

    public void init(){
        _camera = new OrthographicCamera(Positions.getWidth(), Positions.getHeight());
        _camera.setToOrtho(false);
        StretchViewport viewPort = new StretchViewport(Positions.getWidth(), Positions.getHeight(), _camera);
        _stage = new Stage(viewPort, _ptGame.getSpriteBatch());

        //Ground Texture START////////////////////////////////////////////
        _greenGroundImg = new Image(_assets.getTextures().get(Textures.Name.GREEN_GROUND));
        _autumnGroundImg = new Image(_assets.getTextures().get(Textures.Name.AUTUMN_GROUND));
        _autumnGroundImg.getColor().a = 0;
        _autumnGroundImg.addAction(sequence(delay(0.4f), fadeIn(0.5f)));
        //Ground Texture END//////////////////////////////////////////////

        //Background Texture START
        _bgBlueImg = new Image(_assets.getTextures().get(Textures.Name.BLUE_BG));
        _bgBlueImg.setSize(Positions.getWidth(), Positions.getHeight());

        _bgAutumnImg = new Image(_assets.getTextures().get(Textures.Name.AUTUMN_BG));
        _bgAutumnImg.setSize(Positions.getWidth(), Positions.getHeight());
        _bgAutumnImg.getColor().a = 0;

        _sunriseImg = new Image(_assets.getTextures().get(Textures.Name.SUNRISE));
        _sunriseImg.getColor().a = 0;

        _sunrayImg = new Image(_assets.getTextures().get(Textures.Name.SUNRAY));
        _sunrayImg.setPosition(Positions.centerX(1200), -470);
        _sunrayImg.setOrigin(599f, 601f);
        _sunrayImg.setSize(1200, 1200);
        _sunrayImg.getColor().a = 0;

        _sunriseImg.addAction(fadeIn(0.5f));
        _bgAutumnImg.addAction(sequence(fadeIn(0.5f), new Action() {
            @Override
            public boolean act(float delta) {
//                _sunrayImg.addAction(parallel(
//                        fadeIn(1f),
//                        forever(rotateBy(3, 0.15f))
//                ));
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

        _ptGame.addInputProcessor(_stage);
        _ptGame.addInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
    }

    public void showRotateSunrise(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                //Gdx.graphics.setContinuousRendering(true);
                _sunrayImg.clearActions();
                _sunrayImg.addAction(parallel(
                        fadeIn(1f),
                        forever(rotateBy(3, 0.15f))
                ));
            }
        });
    }

    public void hideRotateSunrise(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                _sunrayImg.clearActions();
                _sunrayImg.addAction(sequence(
                        fadeOut(1f), new Action() {
                            @Override
                            public boolean act(float delta) {
                                //Gdx.graphics.setContinuousRendering(false);
                                return true;
                            }
                        }
                ));
            }
        });
    }

    public boolean isPTScreen() {
        return _isPTScreen;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        _stage.act(delta);
        _stage.draw();

    }

    public PTGame getGame() {
        return _ptGame;
    }

    public void setGame(PTGame _ptGame) {
        this._ptGame = _ptGame;
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
        _ptGame.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) _backRunning = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (_backRunning){
            back();
        }
        _backRunning = false;
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    private class LogicEnumPair{
        LogicAbstract logicAbstract;
        SceneEnum sceneEnum;
        Object[] objs;

        public LogicEnumPair(LogicAbstract logicAbstract, SceneEnum sceneEnum, Object... objs) {
            this.logicAbstract = logicAbstract;
            this.sceneEnum = sceneEnum;
            this.objs = objs;
        }

        public LogicAbstract getLogic() {
            return logicAbstract;
        }

        public SceneEnum getSceneEnum() {
            return sceneEnum;
        }

        public void setLogic(LogicAbstract logicAbstract) {
            this.logicAbstract = logicAbstract;
        }

        public Object[] getObjs() {
            return objs;
        }
    }

}
