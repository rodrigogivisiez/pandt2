package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameLogic;
import com.potatoandtomato.common.absints.UserStateListener;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.ImageStorageListener;
import com.potatoandtomato.games.absintf.RoomMsgListener;
import com.potatoandtomato.games.absintf.TimeLogicListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.enums.StageType;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.*;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 5/4/2016.
 */
public class MainLogic extends GameLogic {

    private Services _services;
    private MainScreen _screen;
    private ImageDetails _imageDetails;
    private TimeLogic _timeLogic;
    private GameModel _gameModel;
    private ImageStorage _imageStorage;

    public MainLogic(Services services, GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        this._services = services;
        _screen = new MainScreen(services, gameCoordinator);

        setListeners();

        _gameModel = new GameModel();
        _imageStorage = new ImageStorage(services, gameCoordinator);
        _imageStorage.startMonitor();
    }

    public void init(){
        sendGoToNextStageIfIsDecisionMaker();
    }

    public void onContinue(){

    }

    public void sendGoToNextStageIfIsDecisionMaker(){
        if(getCoordinator().meIsDecisionMaker()) {
            _imageStorage.peek(new ImageStorageListener() {
                @Override
                public void onPeeked(ImagePair imagePair) {
                    _services.getRoomMsgHandler().sendGotoNextStage(imagePair.getImageDetails().getId(), StageType.Normal, "");
                }
            });
        }
    }

    public void newStage(String id, StageType stageType, String extra){
        _imageStorage.pop(id, new ImageStorageListener() {
            @Override
            public void onPopped(final ImagePair imagePair) {
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _gameModel.setGameState(GameState.Playing);
                        _gameModel.clearHandledAreas();
                        _imageDetails = imagePair.getImageDetails();
                        _imageDetails.setGameImageSize((int) _screen.getImageSize().x, (int) _screen.getImageSize().y);
                        _gameModel.addStageNumber();
                        if(_timeLogic != null) _timeLogic.dispose();

                        _gameModel.convertStageNumberToRemainingSecs();

                        _timeLogic = new TimeLogic(_gameModel, getCoordinator(), new TimeLogicListener() {
                            @Override
                            public void onTimeFinished() {
                                timeFinished();
                            }
                        });

                        changeScreenImages(imagePair.getImageOne(), imagePair.getImageTwo());

                        _timeLogic.start();
                    }
                });
            }
        });
    }

    public void changeScreenImages(Texture texture1, Texture texture2){
        _screen.resetImages(texture1, texture2);
    }

    public void imageTouched(float x, float y, int remainingSecs){
        Vector2 imageSize = _screen.getImageSize();

        float finalY = imageSize.y - y;  //libgdx origin is at bottomleft
        SimpleRectangle correctRect = _imageDetails.getTouchedCorrectRect(x, finalY);
        if(correctRect == null){
            _timeLogic.reduceTime();
        }
        else if(!_gameModel.isAreaAlreadyHandled(correctRect)) {
            _gameModel.addHandledArea(correctRect);
            Rectangle circleRect = new Rectangle();
            circleRect.setSize(correctRect.getWidth(), correctRect.getHeight());
            circleRect.setPosition(correctRect.getX(), imageSize.y - correctRect.getY()); //libgdx origin is at bottomleft
            _screen.circle(circleRect);
            checkGameEnded(remainingSecs);
        }
    }

    public void sendTouchedMsg(float x, float y){
        _services.getRoomMsgHandler().sendTouched(new TouchedPoint(x, y, _timeLogic.getRemainingSecs()));
    }

    //allow a 1 sec tolerance time for game over
    public void timeFinished(){
        if(getCoordinator().meIsDecisionMaker()){
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    if(_gameModel.getGameState() == GameState.Ended){
                        return;
                    }
                    else{
                        _services.getRoomMsgHandler().sendLose();
                    }
                }
            });
        }
    }

    public void checkGameEnded(int remainingSecs){
        if(_gameModel.getHandledAreas().size() == 5){
            _gameModel.setGameState(GameState.Ended);
            _timeLogic.stop();
            if(getCoordinator().meIsDecisionMaker()){
                _services.getRoomMsgHandler().sendWon(new WonStageModel(_gameModel.getStageNumber(),
                                        _gameModel.getScore(), remainingSecs, "0", StageType.Normal));
            }
        }
    }

    private void setListeners(){
        _screen.getImageOneTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(canClick()){
                    sendTouchedMsg(x, y);
                    imageTouched(x, y, _timeLogic.getRemainingSecs());
                }
            }
        });

        _screen.getImageTwoTable().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(canClick()){
                    sendTouchedMsg(x, y);
                    imageTouched(x, y, _timeLogic.getRemainingSecs());
                }
            }
        });

        _services.getRoomMsgHandler().setRoomMsgListener(new RoomMsgListener() {
            @Override
            public void onTouched(TouchedPoint touchedPoint, String userId) {
                imageTouched(touchedPoint.x, touchedPoint.y, touchedPoint.getRemainingSecs());
            }

            @Override
            public void onLose() {
                Logs.show("YOU LOSE!! Bye!");
            }

            @Override
            public void onWon(WonStageModel wonStageModel) {
                Logs.show("You win, time is: " + wonStageModel.getRemainingSecs());
                Threadings.delay(5000, new Runnable() {
                    @Override
                    public void run() {
                        sendGoToNextStageIfIsDecisionMaker();
                    }
                });

            }

            @Override
            public void onDownloadImageRequest(ArrayList<String> ids) {
                _imageStorage.receivedDownloadRequest(ids);
            }

            @Override
            public void onGoToNextStage(String id, StageType stageType, String extra) {
                newStage(id, stageType, extra);
            }
        });

        getCoordinator().setUserStateListener(new UserStateListener() {
            @Override
            public void userAbandoned(String s) {

            }

            @Override
            public void userConnected(String s) {

            }

            @Override
            public void userDisconnected(String s) {

            }
        });
    }

    private boolean canClick(){
        return _timeLogic != null && _timeLogic.isTimeRunning()
                && _gameModel.getHandledAreas().size() < 5 && _gameModel.getGameState() == GameState.Playing;
    }







    @Override
    public void dispose() {
        _imageStorage.dispose();
    }

    public MainScreen getMainScreen() {
        return _screen;
    }

    public void setMainScreen(MainScreen _screen) {
        this._screen = _screen;
    }

    public TimeLogic getTimeLogic() {
        return _timeLogic;
    }

    public void setTimeLogic(TimeLogic _timeLogic) {
        this._timeLogic = _timeLogic;
    }

    public GameModel getGameModel() {
        return _gameModel;
    }

    public void setGameModel(GameModel _gameModel) {
        this._gameModel = _gameModel;
    }

    public void setImageStorage(ImageStorage _imageStorage) {
        this._imageStorage = _imageStorage;
    }
}
